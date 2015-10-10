package cn.com.kehwa.weixin.oauth;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.MatrixToImageWriter;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

/**
 * 创建二维码
 */
public class Qrcod {

    private static final String QRCOD_CREATE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
    private static final String QRCOD_SHOW = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
    private static final String SHORT_URL = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token=";

    /**
     * 创建临时二维码
     *
     * @param accessToken
     * @param expireSeconds 最大不超过1800
     * @param sceneId       场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     */
    public static JSONObject createScene(int expireSeconds, int sceneId) 
    		throws InterruptedException, ExecutionException, IOException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> actionInfo = new HashMap<String, Object>();
        Map<String, Object> scene = new HashMap<String, Object>();
        params.put("expire_seconds", expireSeconds);
        params.put("action_name", "QR_SCENE");
        scene.put("scene_id", sceneId);
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);
        String post = JSONObject.toJSONString(params);
        String reslut = HttpKit.post(QRCOD_CREATE.concat(accessToken), post);
        if (StringUtils.isNotEmpty(reslut)) {
            return JSONObject.parseObject(reslut);
        }
        return null;
    }

    /**
     * 创建永久二维码
     *
     * @param accessToken
     * @param sceneId
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     */
    public static JSONObject createLimitScene(int sceneId) throws InterruptedException, ExecutionException, IOException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
    	Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> actionInfo = new HashMap<String, Object>();
        Map<String, Object> scene = new HashMap<String, Object>();
        params.put("action_name", "QR_LIMIT_SCENE");
        scene.put("scene_id", sceneId);
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);
        String post = JSONObject.toJSONString(params);
        String reslut = HttpKit.post(QRCOD_CREATE.concat(accessToken), post);
        if (StringUtils.isNotEmpty(reslut)) {
            return JSONObject.parseObject(reslut);
        }
        return null;
    }
    
    /**
     * 创建永久二维码
     *
     * @param accessToken
     * @param sceneStr
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     */
    public static JSONObject createLimitScene(String sceneStr) throws InterruptedException, ExecutionException, IOException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
    	Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> actionInfo = new HashMap<String, Object>();
        Map<String, Object> scene = new HashMap<String, Object>();
        params.put("action_name", "QR_LIMIT_STR_SCENE");
        scene.put("scene_str", sceneStr);
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);
        String post = JSONObject.toJSONString(params);
        String reslut = HttpKit.post(QRCOD_CREATE.concat(accessToken), post);
        if (StringUtils.isNotEmpty(reslut)) {
            return JSONObject.parseObject(reslut);
        }
        return null;
    }

    /**
     * 获取查看二维码链接
     *
     * @param ticket
     * @return
     */
    public static String showqrcodeUrl(String ticket) {
        return QRCOD_SHOW.concat(ticket);
    }
    
    /**
     * 创建二维码，自己的不依靠腾讯
     * @param url 要创建的连接
     * @param filePath 存放二维码图片的路径
     * @param fileName 存放二维码图片的名称 jpg格式
     * @return
     */
    public static File createQrcode(String url, String filePath, String fileName){
    	File file = null;
    	try {
    	     MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
    	     Map<EncodeHintType,String> hints = new HashMap<EncodeHintType,String>();
    	     hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    	     BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 400, 400, hints);
    	     file = new File(filePath, fileName);
    	     MatrixToImageWriter.writeToFile(bitMatrix, "jpg", file);
    	 } catch (Exception e) {
    	     e.printStackTrace();
    	 }
    	return file;
    }
    
    /**
	 * 长链接转短链接
	 * 
	 * @return
	 * @throws Exception
	 */
	public JSONObject shortUrl(String url) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();

		Map<String, String> json = new HashMap<String, String>();
		json.put("action", "long2short");
		json.put("long_url", url);

		String result = HttpKit.post(SHORT_URL.concat(accessToken), JSONObject.toJSONString(json));
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}
}
