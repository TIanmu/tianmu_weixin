/**
 * 微信公众平台开发模式(JAVA) SDK
 */
package cn.com.kehwa.weixin.demo.payv2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import cn.com.kehwa.weixin.WeChat;
import cn.com.kehwa.weixin.oauth.PayV2;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

/**
 * 示例部分请根据自己实际情况修改
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
		// 判断是否微信环境, 5.0 之后的支持微信支付
		boolean isweixin = WeChat.isWeiXin(req);
		if (isweixin) {
			String productName = "测试商品001";
			String total_fee   = "100";
			String orderid     = "123456";
			
			// package 参数------------------------------  //
			Map<String, String> params = new HashMap<String, String>();
			// 对商品名截取, 去除空格 
			productName = productName.replaceAll(" ", "");
			productName = productName.length() > 17 ? productName.substring(0, 17) + "..." : productName;

			params.put("body", productName);	//商品描述。
			params.put("total_fee", total_fee);	//订单总金额
			params.put("fee_type", "1");	//现金支付币种,取值： 1 （人民币）
			params.put("out_trade_no", orderid); //商户系统内部的订单号
			params.put("spbill_create_ip", getIp(req)); // ip
			// 参数
			String timeStamp = System.currentTimeMillis() + "";
			
			String nonceStr = RandomStringUtils.random(8, "123456789"); // 8位随机数
			String packagestring = PayV2.getPackage(params); //对参数打包
			String paySign = PayV2.paySign(timeStamp, nonceStr, packagestring); // 构造签名
			WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
			// appId
			req.setAttribute("appid", weixinKit.getAppid());
			// timeStamp
			req.setAttribute("timeStamp", timeStamp);
			// nonceStr
			req.setAttribute("nonceStr", nonceStr);
			// package
			req.setAttribute("package", packagestring);
			// paySign
			req.setAttribute("paySign", paySign);
			// 判定微信
			req.setAttribute("isweixin", 1);
			req.getRequestDispatcher("/pay.jsp").forward(req, resp);
		} else {
			resp.sendRedirect("/index.jsp");
		}
	}
		
	/**
	 * 获取ip
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request) {
		if (request == null)
			return "";
		String ip = request.getHeader("X-Requested-For");
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
