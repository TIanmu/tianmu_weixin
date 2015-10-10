package cn.com.kehwa.weixin.test;

import cn.com.kehwa.weixin.oauth.Redpack;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

public class RedpackTest {
	public static void main(String[] args) throws Exception {
		new WeixinKitFactory().setWeixinKit(new WeixinKitImpl());
		Redpack.gethbinfo("11680942574749", "D:\\apiclient_cert.p12", "1233777202");
	}
}
