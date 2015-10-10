package cn.com.kehwa.weixin.message.card;

/**
 * card exception
 */
public class WxCardException extends Exception {

	private static final long serialVersionUID = -185060216536262348L;

	public WxCardException() {
		super();
	}

	public WxCardException(String message, Throwable cause) {
		super(message, cause);
	}

	public WxCardException(String message) {
		super(message);
	}

	public WxCardException(Throwable cause) {
		super(cause);
	}
	
}
