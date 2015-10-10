package cn.com.kehwa.weixin.oauth;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.kehwa.weixin.WeChatException;
import cn.com.kehwa.weixin.message.bean.UserInfo;
import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 用户操作接口
 */
public class User {
	private final static Logger LOGGER = LoggerFactory.getLogger(User.class);
	private static final String USER_INFO_URI = "https://api.weixin.qq.com/cgi-bin/user/info";
	private static final String USER_GET_URI = "https://api.weixin.qq.com/cgi-bin/user/get";
	private static final String USER_UPDATE_REMARK_URI = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=";

	/**
	 * 获取用户列表：该方法获取所有关注公共账号的微信用户的openId
	 * 集合， 再通过openId集合既可以获取所有的用户的信息
	 * @return
	 */
	public List<String> getAll() {
		List<String> openIds = new ArrayList<String>();
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();

		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", weixinKit.getAccessToken());

		try {
			String result = HttpKit.get(USER_GET_URI, params);
			if (result != null && StringUtils.isNotEmpty(result)) {
				JSONObject jsonObject = JSONObject.parseObject(result);
				if (jsonObject.containsKey("data")) {
					jsonObject = JSONObject.parseObject(jsonObject.get("data").toString());
					JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("openid").toString());
					for (int i = 0; i < jsonArray.size(); i++) {
						openIds.add(i, jsonArray.getString(i));
					}
				} else {
					LOGGER.error("get user error because by {}", jsonObject);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return openIds;
	}
	
	/**
	 * 拉取用户信息
	 * @param accessToken
	 * @param openid
	 * @return
	 * @throws IOException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static UserInfo getUserInfo(String openid) throws WeChatException {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
		if(StringUtils.isBlank(openid) || StringUtils.isBlank(accessToken) ){
			LOGGER.error("OPENID or ACCESSTOKEN is blank");
			throw new WeChatException("OPENID or ACCESSTOKEN is blank");
		}
		
		try{
			Map<String, String> params = new HashMap<String, String>();
			params.put("access_token", accessToken);
			params.put("openid", openid);
			
			String  jsonStr = HttpKit.get(USER_INFO_URI, params);
			if(StringUtils.isNotEmpty(jsonStr)){
				JSONObject obj = JSONObject.parseObject(jsonStr);
				if(obj.get("errcode") != null){
					LOGGER.error("get {} user info fail{}",openid,obj.getString("errmsg"));
					throw new WeChatException("user info fail");
				}else{
					LOGGER.info("get {} user info sucess",openid);
					UserInfo user = JSONObject.toJavaObject(obj, UserInfo.class);
					if(user.getSubscribe() == 0){
						LOGGER.error("get {} user info fail{}", jsonStr);
						throw new WeChatException("user no guanzhu weixin");
					}
					return user;
				}
			}
		}catch (Exception e) {
			throw new WeChatException(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 获取帐号的关注者列表
	 * @param accessToken
	 * @param nextOpenid
	 * @return
	 */
	public static JSONObject getFollwersList(String nextOpenid) throws Exception{
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", weixinKit.getAccessToken());
		params.put("next_openid", nextOpenid);
		String  jsonStr = HttpKit.get(USER_GET_URI, params);
		if(StringUtils.isNotEmpty(jsonStr)){
			JSONObject obj = JSONObject.parseObject(jsonStr);
			if(obj.get("errcode") != null){
				throw new Exception(obj.getString("errmsg"));
			}
			return obj;
		}
		return null;
	}
	
	/**
	 * 设置用户备注名
	 * @param openId
	 * @param remark
	 * @return
	 * @throws Exception 
	 */
	public boolean updateRemark(String openId, String remark) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
		
		Map<String, String> json = new HashMap<String, String>();
		json.put("openid", openId);
		json.put("remark", remark);

		LOGGER.info("update remark json is {}", json);

		String result = HttpKit.post(USER_UPDATE_REMARK_URI.concat(accessToken), json);
		if (result != null && StringUtils.isNotEmpty(result)) {
			JSONObject jsonObject = JSONObject.parseObject(result);

			String errcode = jsonObject.getString("errcode");
			if ("0".equalsIgnoreCase(errcode)) {
				return true;
			}
		}
		return false;
	}
}