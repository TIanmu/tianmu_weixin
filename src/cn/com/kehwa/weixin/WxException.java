package cn.com.kehwa.weixin;

/**
 * We Chat 's exception
 */
public class WxException extends Exception {

	private static final long serialVersionUID = -185060216536262348L;

	public WxException() {
		super();
	}

	public WxException(String message, Throwable cause) {
		super(message, cause);
	}

	public WxException(String message) {
		super(message);
	}

	public WxException(Throwable cause) {
		super(cause);
	}

	
}
