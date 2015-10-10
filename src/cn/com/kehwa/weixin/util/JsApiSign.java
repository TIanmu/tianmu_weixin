package cn.com.kehwa.weixin.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

/**
 * 微信js高级接口工具
 *
 */
public class JsApiSign {
	
	/**
	 * 添加微信Js接口所需的全部信息
	 * @param request 为了获取访问地址 如果url转码过，请在拦截器内设置htmlPath为访问地址
	 */
	public static void addJsapiInfo(HttpServletRequest request){
		// 这里的htmlPath为静态化时依靠程序的拦截器拦截得到的地址，在其它系统中请依靠拦截器实现或者干掉他
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String htmlPath = (String) request.getAttribute("htmlPath");
		String currenturl = null;
		if(htmlPath == null){
			currenturl = request.getRequestURL()+(request.getQueryString()==null?"":("?"+request.getQueryString()));
		} else {
			currenturl = htmlPath;
		}
		Map<String, String> map = JsApiSign.sign(weixinKit.getJsapiTicket(), currenturl);
		request.setAttribute("appId", weixinKit.getAppid());
		request.setAttribute("timestamp",map.get("timestamp"));
		request.setAttribute("nonceStr",map.get("nonceStr"));
		request.setAttribute("signature",map.get("signature"));
	}

	/**
	 * js高级接口签名
	 * @param jsapi_ticket
	 * @param url
	 * @return
	 */
    public static Map<String, String> sign(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                "&noncestr=" + nonce_str +
                "&timestamp=" + timestamp +
                "&url=" + url;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
