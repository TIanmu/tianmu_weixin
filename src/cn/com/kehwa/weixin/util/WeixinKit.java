package cn.com.kehwa.weixin.util;

/**
 * 微信基本信息接口，必须实现
 *
 */
public interface WeixinKit {
	
	 java.lang.String getAccessTime();

	 java.lang.String getAccessToken();

	 java.lang.String getJsapiTime();

	 java.lang.String getJsapiTicket();

	 java.lang.String getAccount();

	 java.lang.String getAppid();

	 java.lang.String getAppsecret();

	 java.lang.String getName();

	 java.lang.String getOpenid();

	 java.lang.String getToken();

	 java.lang.String getTrCode();

	 java.lang.String getUrl();

	 java.lang.String getWpassword();

	 java.lang.String getMenu();

	 java.lang.String getSubscribeType();

	 java.lang.String getSubscribeText();

	 java.lang.String getPartnerkey();

	 java.lang.String getMchId();

}
