package cn.com.kehwa.weixin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.kehwa.weixin.message.bean.Attachment;
import cn.com.kehwa.weixin.oauth.Group;
import cn.com.kehwa.weixin.oauth.Media;
import cn.com.kehwa.weixin.oauth.Menu;
import cn.com.kehwa.weixin.oauth.Message;
import cn.com.kehwa.weixin.oauth.Oauth;
import cn.com.kehwa.weixin.oauth.Qrcod;
import cn.com.kehwa.weixin.oauth.User;
import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.JsApiSign;
import cn.com.kehwa.weixin.util.Tools;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

/**
 * 微信常用的API
 *
 */
public class WeChat {
	private static final String GET_IP_URL = "https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=";
    private static final String ACCESSTOKEN_URL  = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
    private static final String PAYFEEDBACK_URL  = "https://api.weixin.qq.com/payfeedback/update";
    private static final String GET_MEDIA_URL    = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=";
    private static final String JSAPI_TICKET     = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=";
    
    /**
	 * 授权接口
	 */
	public static final Oauth oauth = new Oauth();

	/**
	 * 消息接口
	 */
	public static final Message message = new Message();

	/**
	 * 素材接口
	 */
	public static final Media media = new Media();

	/**
	 * 菜单接口
	 */
	public static final Menu menu = new Menu();

	/**
	 * 用户接口
	 */
	public static final User user = new User();

	/**
	 * 分组接口
	 */
	public static final Group group = new Group();

	/**
	 * 二维码接口
	 */
	public static final Qrcod qrcod = new Qrcod();

    /**
     * 获取access_token
     *
     * @return
     * @throws Exception
     */
    public static String getAccessToken() throws Exception {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
        String jsonStr = HttpKit.get(ACCESSTOKEN_URL.concat("&appid=") + weixinKit.getAppid() + "&secret=" + weixinKit.getAppsecret());
        Map<String, Object> map = JSONObject.parseObject(jsonStr);
        if(map.containsKey("access_token")){
        	return map.get("access_token").toString();
        } else {
        	throw new WxException(jsonStr);
        }
    }
    
	/**
	 * 获取微信服务器IP地址
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<String> getCallbackIP() throws Exception {
		List<String> ips = new ArrayList<String>();
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
		String result = HttpKit.get(GET_IP_URL.concat(accessToken));
		if (result != null && StringUtils.isNotEmpty(result)) {
			JSONObject jsonObject = JSONObject.parseObject(result);
			String ipList = jsonObject.get("ip_list").toString();
			JSONArray jsonArray = JSONArray.parseArray(ipList);
			for (int i = 0; i < jsonArray.size(); i++) {
				ips.add(jsonArray.get(i).toString());
			}
		}

		return ips;
	}
    
    /**
     * 支付反馈
     *
     * @param openid
     * @param feedbackid
     * @return
     * @throws Exception
     */
    public static boolean payfeedback(String openid, String feedbackid) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        String accessToken = getAccessToken();
        map.put("access_token", accessToken);
        map.put("openid", openid);
        map.put("feedbackid", feedbackid);
        String jsonStr = HttpKit.get(PAYFEEDBACK_URL, map);
        Map<String, Object> jsonMap = JSONObject.parseObject(jsonStr);
        if (!jsonMap.containsKey("errcode")) {
        	throw new WxException(jsonStr);
		}
        return "0".equals(jsonMap.get("errcode").toString());
    }

    /**
     * 签名检查
     *
     * @param token
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static Boolean checkSignature(String token, String signature, String timestamp, String nonce) {
        return Tools.checkSignature(token, signature, timestamp, nonce);
    }

    /**
     * 获取媒体资源
     *
     * @param accessToken
     * @param mediaId
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static Attachment getMedia(String mediaId) throws IOException, ExecutionException, InterruptedException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
    	String url = GET_MEDIA_URL + accessToken + "&media_id=" + mediaId;
        return HttpKit.download(url);
    }

    /**
     * 保存媒体资源至相关路径
     * @param attachment
     * @param savePath 最终结果
     * @return
     * @throws IOException
     */
    public static String saveMedia(Attachment attachment, String savePath) throws IOException{
    	if(savePath.charAt(savePath.length()-1) == File.separatorChar){
    		savePath += File.separator;
    	}
    	savePath += attachment.getFileName();
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(savePath));
		byte[] data = new byte[1024];  
        int len = -1;  
          
        while ((len = attachment.getFileStream().read(data)) != -1) {  
            bos.write(data,0,len);  
        }  
        bos.close();  
        attachment.getFileStream().close();  
        return savePath;
    }
    
    /**
     * 获得jsapi_ticket（有效期7200秒)
     *
     * @param accessToken
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     * @throws NoSuchProviderException
     */
    public static JSONObject getTicket() throws InterruptedException, ExecutionException, NoSuchAlgorithmException, KeyManagementException, IOException, NoSuchProviderException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
    	String jsonStr = HttpKit.get(JSAPI_TICKET.concat(accessToken));
        return JSONObject.parseObject(jsonStr);
    }

    /**
     * 生成jsApi的签名信息
     *
     * @param jsapiTicket
     * @param url
     * @return
     */
    public static Map<String, String> jsApiSign(String jsapiTicket, String url) {
        return JsApiSign.sign(jsapiTicket, url);
    }

    /**
     * 判断是否来自微信, 5.0 之后的支持微信支付
     *
     * @param request
     * @return
     */
    public static boolean isWeiXin(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (StringUtils.isNotBlank(userAgent)) {
            Pattern p = Pattern.compile("MicroMessenger/(\\d+).+");
            Matcher m = p.matcher(userAgent);
            String version = null;
            if (m.find()) {
                version = m.group(1);
            }
            return (null != version && NumberUtils.toInt(version) >= 5);
        }
        return false;
    }
    
}
