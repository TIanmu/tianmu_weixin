/**
 * 微信公众平台开发模式(JAVA) SDK
 */
package cn.com.kehwa.weixin.message.request;

/**
 * 消息类型
 *
 */
public enum MsgTypes {
	TEXT("text"), 
	LOCATION("location"), 
	IMAGE("image"),
	LINK("link"),
	VOICE("voice"),
	EVENT("event"),
	VIDEO("video"),
	NEWS("news"),
	MUSIC("music"),
	VERIFY("verify"),
	// 缩略图
	THUMB("thumb"),
	//响应消息类型：多客服
	RESP_MESSAGE_TYPE_TRANSFER_CUSTOMER_SERVICE("transfer_customer_service"),
	// 事件类型：订阅
	EVENT_TYPE_SUBSCRIBE("subscribe"),
	// 事件类型：取消订阅
	EVENT_TYPE_UNSUBSCRIBE("unsubscribe"),
	// 事件类型：关注用户扫描带参数二维码
	EVENT_TYPE_SCAN("SCAN"),
	// 事件类型：CLICK 自定义菜单
	EVENT_TYPE_CLICK("CLICK"),
	// 事件类型：VIEW 自定义菜单
	EVENT_TYPE_VIEW("VIEW");
	
	private String type;
	
	MsgTypes(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
