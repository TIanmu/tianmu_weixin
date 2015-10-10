package cn.com.kehwa.weixin.message.response;

/**
 * 消息基类(公众账号->普通用户)
 * 
 */
public class BaseMessage {
	// 接收方账号(收到的OpenID)
	private String ToUserName;

	// 开发者微信号
	private String FromUserName;

	// 消息创建时间
	private long CreateTime;

	// 消息类型(text/music/news)
	private String MsgType;

	public long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(long createTime) {
		CreateTime = createTime;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

}
