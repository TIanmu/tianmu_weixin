package cn.com.kehwa.weixin.util;

/**
 * 微信基本信息工厂
 *
 */
public class WeixinKitFactory {
	private static WeixinKit weixinKit;
	
	public WeixinKitFactory() {

	}

	public static WeixinKit getWeixinKit() {
		if (weixinKit == null) {
			weixinKit = (WeixinKit) SpringContextsUtil.getBean("weixinKitService");
		}
		return weixinKit;
	}

	public void setWeixinKit(WeixinKit weixinKit) {
		WeixinKitFactory.weixinKit = weixinKit;
	}
	
}
