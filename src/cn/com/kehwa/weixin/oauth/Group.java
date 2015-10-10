/**
 * 微信公众平台开发模式(JAVA) SDK
 */
package cn.com.kehwa.weixin.oauth;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.StringUtils;

import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

/**
 * 用户组操作接口
 */
public class Group {

	private static final String GROUP_CREATE_URI = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=";
	private static final String GROUP_GET_URI = "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=";
	private static final String GROUP_GETID_URI = "https://api.weixin.qq.com/cgi-bin/groups/getid?access_token=";
	private static final String GROUP_UPDATE_URI = "https://api.weixin.qq.com/cgi-bin/groups/update?access_token=";
	private static final String GROUP_MEMBERS_UPDATE_URI = "https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token=";

	/**
	 * 创建分组
	 * @param accessToken
	 * @param name 分组名字（30个字符以内）
	 * @return
	 * @throws Exception
	 */
	public static JSONObject create(String name) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
		Map<String,Object> group = new HashMap<String,Object>();
        Map<String,Object> nameObj = new HashMap<String,Object>();
        nameObj.put("name", name);
        group.put("group", nameObj);
        String post = JSONObject.toJSONString(group);
    	String reslut = HttpKit.post(GROUP_CREATE_URI.concat(accessToken), post);
    	if(StringUtils.isNotEmpty(reslut)){
    		return JSONObject.parseObject(reslut);
    	}
        return null;
	}
	
	/**
	 * 查询所有分组
	 * @param accessToken
	 * @return
	 * @throws Exception
	 */
	public static JSONObject get() throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
    	String reslut = HttpKit.get(GROUP_GET_URI.concat(accessToken));
    	if(StringUtils.isNotEmpty(reslut)){
    		return JSONObject.parseObject(reslut);
    	}
        return null;
	}
	
	/**
	 * 查询用户所在分组
	 * @param accessToken
	 * @param openid 用户的OpenID
	 * @return
	 * @throws Exception
	 */
	public static JSONObject membersIn(String openid) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
    	String reslut = HttpKit.post(GROUP_GETID_URI.concat(accessToken),"{\"openid\":\""+openid+"\"}");
    	if(StringUtils.isNotEmpty(reslut)){
    		return JSONObject.parseObject(reslut);
    	}
        return null;
	}
	
	/**
	 * 修改分组名
	 * @param accessToken
	 * @param id 分组id，由微信分配
	 * @param name 分组名字（30个字符以内）
	 * @return
	 * @throws Exception
	 */
	public static JSONObject updateName(String id,String name) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
		Map<String,Object> group = new HashMap<String,Object>();
        Map<String,Object> nameObj = new HashMap<String,Object>();
        nameObj.put("name", name);
        nameObj.put("id", id);
        group.put("group", nameObj);
        String post = JSONObject.toJSONString(group);
    	String reslut = HttpKit.post(GROUP_UPDATE_URI.concat(accessToken), post);
    	if(StringUtils.isNotEmpty(reslut)){
    		return JSONObject.parseObject(reslut);
    	}
        return null;
	}
	
	/**
	 * 移动用户分组
	 * @param accessToken
	 * @param openid 用户唯一标识符
	 * @param to_groupid 分组id
	 * @return
	 * @throws Exception
	 */
	public static JSONObject membersMove(String openid, String to_groupid) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
    	String reslut = HttpKit.post(GROUP_MEMBERS_UPDATE_URI.concat(accessToken), "{\"openid\":\""+openid+"\",\"to_groupid\":"+to_groupid+"}");
    	if(StringUtils.isNotEmpty(reslut)){
    		return JSONObject.parseObject(reslut);
    	}
        return null;
	}
	
}