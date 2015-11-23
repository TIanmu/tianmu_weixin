package cn.com.kehwa.weixin.test;

import java.util.Map;

import cn.com.kehwa.weixin.oauth.Redpack;
import cn.com.kehwa.weixin.util.JsApiSign;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

public class JsApiSignTest {
	public static void main(String[] args) {
		String jsapi_ticket = "sM4AOVdWfPE4DxkXGEs8VKnBnlUmFK4p4VCOh8PgYCfTOPw2BfaTGPl8_dQbllZEYJ51BQH8F2LEnr4kWyfpmA";
		String url = "http://www.qunageya.com";
		Map<String, String> map = JsApiSign.sign(jsapi_ticket, url);
		System.out.println(map.get("timestamp"));
		System.out.println(map.get("nonceStr"));
		System.out.println(map.get("signature"));
	}
}
