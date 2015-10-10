package cn.com.kehwa.weixin.oauth;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

import com.alibaba.fastjson.JSON;

/**
 * 菜单
 * 
 */
public class Menu {

	private static final String CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	private static final String GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	private static final String DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=";
	
    /**
     * 创建菜单
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
	public boolean createMenu(String params) throws InterruptedException, ExecutionException, IOException {
        WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String jsonStr = HttpKit.post(CREATE_URL.replace("ACCESS_TOKEN", weixinKit.getAccessToken()), params);
        System.out.println("createMenu"+jsonStr);
        Map<String, Object> map = JSON.parseObject(jsonStr,Map.class);
        return "0".equals(map.get("errcode").toString());
    }
    
    /**
     * 查询菜单
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public Map<String, Object> getMenuInfo() throws InterruptedException, ExecutionException, NoSuchAlgorithmException, KeyManagementException, IOException, NoSuchProviderException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String jsonStr = HttpKit.get(GET_URL.replace("ACCESS_TOKEN", weixinKit.getAccessToken()));
        Map<String, Object> map = JSON.parseObject(jsonStr, Map.class);
        return map;
    }
    
    /**
     * 查询菜单
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public String getMenuInfoToString() throws InterruptedException, ExecutionException, NoSuchAlgorithmException, KeyManagementException, IOException, NoSuchProviderException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String jsonStr = HttpKit.get(GET_URL.replace("ACCESS_TOKEN", weixinKit.getAccessToken()));
        return jsonStr;
    }
    
    /**
     * 删除自定义菜单
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public boolean deleteMenu() throws InterruptedException, ExecutionException, NoSuchAlgorithmException, KeyManagementException, IOException, NoSuchProviderException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String jsonStr = HttpKit.get(DELETE_URL.replace("ACCESS_TOKEN", weixinKit.getAccessToken()));
        Map<String, Object> map = JSON.parseObject(jsonStr, Map.class);
        return "0".equals(map.get("errcode").toString());
    }
    
}
