/**
 * 微信公众平台开发模式(JAVA) SDK
 */
package cn.com.kehwa.weixin.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cn.com.kehwa.weixin.WxException;
import cn.com.kehwa.weixin.message.bean.UserInfo;
import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 微信用户高级接口相关
 */
public class Oauth {

    private static final String CODE_URI = "http://open.weixin.qq.com/connect/oauth2/authorize";
    private static final String TOKEN_URI = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private static final String REFRESH_TOKEN_URI = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
    private static final String USER_INFO_URI = "https://api.weixin.qq.com/sns/userinfo";
    
    /**
     * 请求code
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String getCode(String redirectUri, boolean isNeedUserInfo) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", WeixinKitFactory.getWeixinKit().getAppid());
        params.put("response_type", "code");
        params.put("redirect_uri", redirectUri);
        if(isNeedUserInfo){
        	params.put("scope", "snsapi_userinfo");
        } else {
        	params.put("scope", "snsapi_base");
        }
        // snsapi_base（不弹出授权页面，只能拿到用户openid）snsapi_userinfo
        // （弹出授权页面，这个可以通过 openid 拿到昵称、性别、所在地）
        params.put("state", "wx#wechat_redirect");
        String para = PayV2.createSign(params, false);
        return CODE_URI + "?" + para;
    }
    
    /**
     * 请求code
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String getCode(String redirectUri, boolean isNeedUserInfo, String state) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", WeixinKitFactory.getWeixinKit().getAppid());
        params.put("response_type", "code");
        params.put("redirect_uri", redirectUri);
        if(isNeedUserInfo){
        	params.put("scope", "snsapi_userinfo");
        } else {
        	params.put("scope", "snsapi_base");
        }
        // snsapi_base（不弹出授权页面，只能拿到用户openid）snsapi_userinfo
        // （弹出授权页面，这个可以通过 openid 拿到昵称、性别、所在地）
        params.put("state", state + "#wechat_redirect");
        String para = PayV2.createSign(params, false);
        return CODE_URI + "?" + para;
    }

    /**
     * 通过code 换取 access_token
     * @param code
     * @return
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public static String getToken(String code) throws Exception {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", weixinKit.getAppid());
        params.put("secret", weixinKit.getAppsecret());
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        return HttpKit.get(TOKEN_URI, params);
    }

    /**
     * 刷新 access_token
     * @param refreshToken
     * @return
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public static String getRefreshToken(String refreshToken) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", WeixinKitFactory.getWeixinKit().getAppid());
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refreshToken);
        return HttpKit.get(REFRESH_TOKEN_URI, params);
    }
    
    /**
	 * 拉取用户信息
	 * @param accessToken 这个AccessToken是这里临时的，不是全局的那个
	 * @param openid
	 * @return
	 * @throws IOException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static UserInfo getUserInfo(String openid, String accessToken) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		params.put("openid", openid);
		params.put("lang", "zh_CN");
		String  jsonStr = HttpKit.get(USER_INFO_URI, params);
		if(StringUtils.isNotEmpty(jsonStr)){
			JSONObject obj = JSONObject.parseObject(jsonStr);
			if(obj.get("errcode") != null){
				throw new WxException(jsonStr);
			}
			UserInfo user = JSONObject.toJavaObject(obj, UserInfo.class);
			return user;
		}
		return null;
	}
    
}
