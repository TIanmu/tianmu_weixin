package cn.com.kehwa.weixin.message.response;

/**
 * 消息类型
 * 
 */
public enum SendAllMsgTypes {
    TEXT("text"),
    IMAGE("image"),
    VOICE("voice"),
    MPVIDEO("mpvideo"),
    MPNEWS("mpnews"),
    VIDEO("video");

    private String type;

    SendAllMsgTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
