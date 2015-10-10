package cn.com.kehwa.weixin.demo.payv3;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.kehwa.weixin.oauth.Pay;
import cn.com.kehwa.weixin.util.ParseXmlUtil;
import cn.com.kehwa.weixin.util.Tools;

/**
 * 微信支付V3版本
 * 扫码支付，扫码之后的回调代码
 */
public class BizpayDemo extends HttpServlet {

	private static final long serialVersionUID = 4638321491356022766L;
	private Logger log = LoggerFactory.getLogger(BizpayDemo.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 需要你提供的东西 ******************
		String partnerkey = "";
		String appId = "";
		String mchId = "";
		String notifyUrl = "";
		// *****************
		try {
			log.info("have a Bizpay process");
			// post 过来的xml
			ServletInputStream in = request.getInputStream();
			String xmlMsg = Tools.inputStream2String(in);
			log.info("Bizpay complement from weixin message is:{}", xmlMsg);
			// 转换微信post过来的xml内容
			Map<String, String> paraMap = ParseXmlUtil.parseXml(xmlMsg);
			// 验证是否是微信
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("appid", paraMap.get("appid"));
			packageParams.put("openid", paraMap.get("openid"));
			packageParams.put("mch_id", paraMap.get("mch_id"));
			packageParams.put("is_subscribe", paraMap.get("is_subscribe"));
			packageParams.put("nonce_str", paraMap.get("nonce_str"));
			packageParams.put("product_id", paraMap.get("product_id")); // 商户定义的商品id 或者订单号
			String sign = Pay.createSign(packageParams);
			if (sign.equals(paraMap.get("sign"))) {
				// 说明是微信
				//TODO 自己的逻辑 bg
				String orderNo = "";
				String totalFee = "1";
				String bodyString = "";
				// 自己的逻辑 ed
				// 调用统一下单接口
				// TODO 注意这里要自己改一下参数
				String prepay_id = Pay.unifiedorder(request, totalFee, bodyString, null, orderNo, paraMap.get("nonce_str"), notifyUrl, "NATIVE", orderNo);
				// 构造POST参数
				packageParams.put("return_code", "SUCCESS");
				packageParams.put("appid", paraMap.get("appid"));
				packageParams.put("mch_id", paraMap.get("mch_id"));
				packageParams.put("nonce_str", paraMap.get("nonce_str"));
				packageParams.put("prepay_id", prepay_id);
				packageParams.put("result_code", "SUCCESS");
				sign = Pay.createSign(packageParams);
				String xml = "<xml>" 
		    			+ "<return_code><![CDATA[SUCCESS]]></return_code>" 
						+ "<appid>" + paraMap.get("appid") + "</appid>" 
						+ "<mch_id>" + paraMap.get("mch_id") + "</mch_id>" 
						+ "<nonce_str>" + paraMap.get("nonce_str") + "</nonce_str>" 
						+ "<prepay_id>" + prepay_id + "</prepay_id>" 
						+ "<result_code><![CDATA[SUCCESS]]></result_code>" 
						+ "<sign>" + sign + "</sign>" 
						+ "</xml>";
				log.info("Bizpay ok");
				printScript(xml, response); // 输出给微信，之后微信会调用支付成功接口
			} else {
				// 构造POST参数
				String xml = "<xml>" 
		    			+ "<return_code><![CDATA[FAIL]]></return_code>" 
						+ "<return_msg><![CDATA[签名验证失败]]></return_msg>" 
						+ "</xml>";
				log.info("Bizpay error because sign error");
				printScript(xml, response); // 输出给微信意思是报错了
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param content 相应客服的的内容
	 * @Description 输出内容到客户端
	 */
	public void printScript(String content, HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.println(content);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null) out.close();
		}
	}
}
