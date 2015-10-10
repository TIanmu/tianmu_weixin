/**
 * 微信公众平台开发模式(JAVA) SDK
 */
package cn.com.kehwa.weixin.demo.payv3;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.kehwa.weixin.WeChat;
import cn.com.kehwa.weixin.message.bean.WeChatBuyPost;
import cn.com.kehwa.weixin.oauth.Message;
import cn.com.kehwa.weixin.oauth.Pay;
import cn.com.kehwa.weixin.util.ParseXmlUtil;
import cn.com.kehwa.weixin.util.Tools;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * V3支付回调
 */
public class WeChatPayNotifyServlet extends HttpServlet {

	private static final long serialVersionUID = 6139862236335427037L;
	private Logger log = LoggerFactory.getLogger(WeChatPayNotifyServlet.class);
	
	// 微信返回  fail 失败，success 成功
	private static final String STATUC_SUCCESS = "success";
	private static final String STATUC_FAIL    = "fail";
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) {
		String statucSuccess = "success";
		String statucFail    = "fail";
		try {
			log.info("have a order pay process");
			// post 过来的xml
			ServletInputStream in = request.getInputStream();
			String xmlMsg = Tools.inputStream2String(in);
			log.info("order pay complement from weixin message is:{}", xmlMsg);
			// 转换微信post过来的xml内容
			Map<String, String> paraMap = ParseXmlUtil.parseXml(xmlMsg);
			String openid = paraMap.get("openid");
			String trade_type = paraMap.get("trade_type");
			String totalFee = paraMap.get("total_fee");
			String orderId = paraMap.get("out_trade_no");
			String transId = paraMap.get("transaction_id");
	
			System.out.println("trade_state:\t" + trade_type + "totalFee:\t" + totalFee + "orderId:\t" + orderId);
			
			if (StringUtils.isEmpty(orderId)) {
//				printScript(statucFail);
				return;
			}
			// 自己的业务逻辑 bg
//			order = orderService.getOrderByNo(orderId);
//	
//			Emplorer customer = order.getOrderUser();
//			if (customer != null && order != null) {
//				orderService.saveOrderPayment(customer, order,
//						Constant.PAY_WECHAT, orderId, order.getCreate()
//								.getId());
//			}
//			log.info("微信支付回调成功");
			// 自己的业务逻辑 ed
			
			// 发送客服消息
//			String accessToken = appService.getAccessToken();
//			String messageResult = WeChat.message.sendText(accessToken, openid, "您的订单号" + orderId + "已经支付成功！");
//			log.info("cutoum message result is {} ", messageResult);
//			printScript(statucSuccess);
		} catch (Exception e) {
			e.printStackTrace();
//			printScript(statucFail);
		}
	}

	/**
	 * 直接写字符串
	 * @param response
	 * @param msg
	 */
	private void writeString(HttpServletResponse response, String msg) {
		try {
			response.getWriter().write(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
