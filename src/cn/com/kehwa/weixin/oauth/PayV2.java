package cn.com.kehwa.weixin.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

/**
 * 支付相关方法 V2旧版本
 */
public class PayV2 {

	private static final Logger LOG = LoggerFactory.getLogger(PayV2.class);
    private static final String DELIVERNOTIFY_URL = "https://api.weixin.qq.com/pay/delivernotify?access_token=";// 发货通知接口
    
    /**
     * 微信V2
     * 发货通知
     * @param access_token
     * @param openid
     * @param transid
     * @param out_trade_no
     * @return
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     * @throws InterruptedException 
     * @throws ExecutionException 
     */
    public static boolean delivernotify(String openid, String transid, String out_trade_no) throws IOException, ExecutionException, InterruptedException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
    	
    	Map<String, String> paras = new HashMap<String, String>();
        paras.put("appid", weixinKit.getAppid());
        paras.put("openid", openid);
        paras.put("transid", transid);
        paras.put("out_trade_no", out_trade_no);
        paras.put("deliver_timestamp", (System.currentTimeMillis() / 1000) + "");
        paras.put("deliver_status", "1");
        paras.put("deliver_msg", "ok");
        // 签名
        String app_signature = deliverSign(paras);
        paras.put("app_signature", app_signature);
        paras.put("sign_method", "sha1");
        String json = HttpKit.post(DELIVERNOTIFY_URL.concat(accessToken), JSONObject.toJSONString(paras));
        if (StringUtils.isNotBlank(json)) {
            JSONObject object = JSONObject.parseObject(json);
            if (object.containsKey("errcode")) {
                int errcode = object.getIntValue("errcode");
                LOG.error("发货通知出错 {}",object.toJSONString());
                return errcode == 0;
            }
        }
        return false;
    }
    
    
    /**
     * 微信支付V2
     * 参与 paySign 签名的字段包括：appid、timestamp、noncestr、package 以及 appkey。
     * 这里 signType 并不参与签名微信的Package参数
     * @param params
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String getPackage(Map<String, String> params) throws UnsupportedEncodingException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String partnerKey = weixinKit.getPartnerkey();
        String partnerId = weixinKit.getPartnerkey();// ???TODO 这里不对
        String notifyUrl = "";//notify_url
        // 公共参数
        params.put("bank_type", "WX");
        params.put("attach", "yongle");
        params.put("partner", partnerId);
        params.put("notify_url", notifyUrl);
        params.put("input_charset", "UTF-8");
        return packageSign(params, partnerKey);
    }

    /**
     * 微信支付V2
     * 构造签名
     * @param params
     * @param encode
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String createSign(Map<String, String> params, boolean encode) throws UnsupportedEncodingException {
        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuffer temp = new StringBuffer();
        boolean first = true;
        for (Object key : keys) {
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = params.get(key);
            String valueString = "";
            if (null != value) {
                valueString = value.toString();
            }
            if (encode) {
                temp.append(URLEncoder.encode(valueString, "UTF-8"));
            } else {
                temp.append(valueString);
            }
        }
        return temp.toString();
    }

    /**
     * 微信支付V2
     * 支付签名
     * @param timestamp
     * @param noncestr
     * @param packages
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String paySign(String timestamp, String noncestr,String packages) throws UnsupportedEncodingException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	Map<String, String> paras = new HashMap<String, String>();
        paras.put("appid", weixinKit.getAppid());
        paras.put("timestamp", timestamp);
        paras.put("noncestr", noncestr);
        paras.put("package", packages);
        paras.put("appkey", "");// paySignKey  TODO 这里不对
        // appid、timestamp、noncestr、package 以及 appkey。
        String string1 = createSign(paras, false);
        String paySign = DigestUtils.sha1Hex(string1);
        return paySign;
    }
    
    /**
     * 微信支付V2
     * 支付回调校验签名
     * @param timestamp
     * @param noncestr
     * @param openid
     * @param issubscribe
     * @param appsignature
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static boolean verifySign(long timestamp,
            String noncestr, String openid, int issubscribe, String appsignature) throws UnsupportedEncodingException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	Map<String, String> paras = new HashMap<String, String>();
        paras.put("appid", weixinKit.getAppid());
        paras.put("appkey", "");// paySignKey TODO 这里不对
        paras.put("timestamp", String.valueOf(timestamp));
        paras.put("noncestr", noncestr);
        paras.put("openid", openid);
        paras.put("issubscribe", String.valueOf(issubscribe));
        // appid、appkey、productid、timestamp、noncestr、openid、issubscribe
        String string1 = createSign(paras, false);
        String paySign = DigestUtils.sha1Hex(string1);
        return paySign.equalsIgnoreCase(appsignature);
    }
    
    /**
     * 发货通知签名
     * @param paras
     * @return
     * @throws UnsupportedEncodingException
     * 
     * @参数 appid、appkey、openid、transid、out_trade_no、deliver_timestamp、deliver_status、deliver_msg；
     */
    private static String deliverSign(Map<String, String> paras) throws UnsupportedEncodingException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	paras.put("appkey", weixinKit.getAppsecret());//paySignKey TODO 这里不对 
        String string1 = createSign(paras, false);
        String paySign = DigestUtils.sha1Hex(string1);
        return paySign;
    }
    
    /**
     * 微信支付V2
     * 构造package, 这是我见到的最草蛋的加密，尼玛文档还有错
     * @param params
     * @param paternerKey
     * @return
     * @throws UnsupportedEncodingException 
     */
    private static String packageSign(Map<String, String> params, String paternerKey) throws UnsupportedEncodingException {
        String string1 = createSign(params, false);
        String stringSignTemp = string1 + "&key=" + paternerKey;
        String signValue = DigestUtils.md5Hex(stringSignTemp).toUpperCase();
        String string2 = createSign(params, true);
        return string2 + "&sign=" + signValue;
    }
    
}
