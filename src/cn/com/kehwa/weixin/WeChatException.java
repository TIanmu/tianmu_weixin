package cn.com.kehwa.weixin;

/**
 * We Chat 's exception
 *
 */
public class WeChatException extends Exception {

	private static final long serialVersionUID = -185060216536262348L;

	public WeChatException() {
		super();
	}

	public WeChatException(String message, Throwable cause) {
		super(message, cause);
	}

	public WeChatException(String message) {
		super(message);
	}

	public WeChatException(Throwable cause) {
		super(cause);
	}

	
}
