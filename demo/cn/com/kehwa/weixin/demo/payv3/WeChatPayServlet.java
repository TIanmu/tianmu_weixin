package cn.com.kehwa.weixin.demo.payv3;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;

import cn.com.kehwa.weixin.WeChat;
import cn.com.kehwa.weixin.oauth.Message;
import cn.com.kehwa.weixin.oauth.Pay;
import cn.com.kehwa.weixin.util.HttpKit;

/**
 * 微信支付V3版本
 */
public class WeChatPayServlet extends HttpServlet {

	private static final long serialVersionUID = 4638321491356022766L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			Pay.payInfo(req, "", "", "", null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

}
