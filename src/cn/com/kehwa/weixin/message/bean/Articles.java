/**
 * 微信公众平台开发模式(JAVA) SDK
 */
package cn.com.kehwa.weixin.message.bean;

import java.io.Serializable;

/**
 * 客服图文消息对象
 */
public class Articles implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title; // 图文消息/视频消息/音乐消息的标题
	private String description; // 图文消息/视频消息/音乐消息的描述
	private String picurl; // 图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图640*320，小图80*80
	private String url;// 图文消息被点击后跳转的链接

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
