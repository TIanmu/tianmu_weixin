/**
 * 微信公众平台开发模式(JAVA) SDK
 */
package cn.com.kehwa.weixin.oauth;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.MD5Utils;
import cn.com.kehwa.weixin.util.ParseXmlUtil;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

/**
 * 支付相关方法 V3
 */
public class Pay {

	private static final Logger LOG = LoggerFactory.getLogger(Pay.class);
    private static final String ORDERQUERY_URL= "https://api.mch.weixin.qq.com/pay/orderquery";// 订单查询接口
    private static final String DOWNLOADBILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";// 对账单接口
    
    /**
     * 微信支付V3
     * 微信公众平台内支付字符串获得
     * @param req
     * @param totalFee 金额(元)
     * @param openId 用户Openid
     * @param orderNo 商户订单号，系统内唯一
     * @param notify_url 成功支付地址
     * @return 如果微信是5.0以上版本返回相关字符串，反之返回null
     * @throws InterruptedException 
     * @throws ExecutionException 
     * @throws IOException 
     * @throws DocumentException 
     */
    public static String payInfo(HttpServletRequest req, String totalFee, String openId, String orderNo, String notify_url) 
    				throws IOException, ExecutionException, InterruptedException, DocumentException{
    	// 判断是否微信环境, 5.0 之后的支持微信支付
//		boolean isweixin = WeChat.isWeiXin(req);
//		if (isweixin) {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String nonce_str = getNonceStr(); // 随机数
		// 商品描述根据情况修改
		String body = "MSDYPAY";
		// 调用统一下单接口
		String prepay_id = unifiedorder(req, totalFee, body, openId, orderNo, nonce_str, notify_url, "JSAPI", null);
		// 生成前台所用字符串
		SortedMap<String, String> finalpackage = new TreeMap<String, String>();
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		String packages = "prepay_id=" + prepay_id;
		finalpackage.put("appId", weixinKit.getAppid());
		finalpackage.put("timeStamp", timestamp);
		finalpackage.put("nonceStr", nonce_str);
		finalpackage.put("package", packages);
		finalpackage.put("signType", "MD5");
		String finalsign = Pay.createSign(finalpackage);
		String payInfoValue = "timestamp:" + timestamp
				+ ",nonceStr:'" + nonce_str + "',package:'"
				+ packages + "',signType : 'MD5',paySign:'"
				+ finalsign + "'";
		return payInfoValue;
//		}
//		return null;
    }
    
    /**
     * 微信支付V3
     * 微信公众平台内支付字符串获得
     * @param req
     * @param totalFee 金额(元)
     * @param openId 用户Openid
     * @param orderNo 商户订单号，系统内唯一
     * @param notify_url 成功支付地址
     * @param body 商品描述
     * @return 如果微信是5.0以上版本返回相关字符串，反之返回null
     * @throws InterruptedException 
     * @throws ExecutionException 
     * @throws IOException 
     * @throws DocumentException 
     */
	public static Map<String, String> payInfoGetMap(HttpServletRequest req,
			String totalFee, String openId, String orderNo, String notify_url, String body)
			throws IOException, ExecutionException, InterruptedException,
			DocumentException {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String nonce_str = getNonceStr(); // 随机数
		// 调用统一下单接口
		String prepay_id = unifiedorder(req, totalFee, body, openId, orderNo, nonce_str, notify_url, "JSAPI", null);
		// 生成前台所用字符串
		SortedMap<String, String> finalpackage = new TreeMap<String, String>();
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		String packages = "prepay_id=" + prepay_id;
		finalpackage.put("appId", weixinKit.getAppid());
		finalpackage.put("timeStamp", timestamp);
		finalpackage.put("nonceStr", nonce_str);
		finalpackage.put("package", packages);
		finalpackage.put("signType", "MD5");
		String finalsign = Pay.createSign(finalpackage);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("timestamp", timestamp);
		map.put("nonceStr", nonce_str);
		map.put("package", packages);
		map.put("signType", "MD5");
		map.put("paySign", finalsign);
		
		return map;
    }
    
    /**
     * 微信支付V3
     * 订单查询接口
     * @param transactionId 微信订单号
     * @param outTradeNo 商户订单号,商户系统内部的订单号,
     * transaction_id、 out_trade_no 二选一，如果同时存在优先级：
	 * transaction_id > out_trade_no
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws DocumentException
     */
    public static String orderquery(String transactionId, String outTradeNo) 
    		throws IOException, ExecutionException, InterruptedException, DocumentException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String nonce_str = getNonceStr();
    	SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", weixinKit.getAppid());
		packageParams.put("mch_id", weixinKit.getMchId());
		if(!StringUtils.isBlank(transactionId)){
			packageParams.put("transaction_id", transactionId);
		} else {
			packageParams.put("out_trade_no", outTradeNo);
		}
		packageParams.put("nonce_str", nonce_str);
		String sign = Pay.createSign(packageParams);
    	
    	
    	StringBuffer xml = new StringBuffer("<xml>" 
    			+ "<nonce_str>" + nonce_str+ "</nonce_str>" 
				+ "<sign><![CDATA[" + sign + "]]></sign>" 
				+ "<mch_id>" + weixinKit.getMchId()+ "</mch_id>"
				+ "<appid><![CDATA[" + weixinKit.getAppid() + "]]></appid>");
    	if(!StringUtils.isBlank(transactionId)){
    		xml.append("<transaction_id><![CDATA[" + transactionId + "]]></transaction_id>");
		} else {
			xml.append("<out_trade_no><![CDATA[" + outTradeNo + "]]></out_trade_no>");
		}
    	xml.append("</xml>");
		String resultFromWeixin = HttpKit.post(ORDERQUERY_URL, xml.toString());
		Map<String,String> resultMap =  ParseXmlUtil.parseXml(resultFromWeixin);
		if(resultMap.containsKey("return_code")&&resultMap.get("return_code").equals("SUCCESS")){
			if(resultMap.containsKey("result_code")&&resultMap.get("result_code").equals("SUCCESS")){
				return resultFromWeixin;
			} else {
				LOG.error("weixin {} error, cause by {}", resultMap.get("err_code"), resultMap.get("err_code_des"));
			}
		} else {
			LOG.error("weixin send red pack error, cause by {}", resultMap.get("return_msg"));
		}
		return null;
	}
    
    /**
     * 微信支付V3
     * 对账单接口
     * @param device_info 终端设备号  非必填
     * @param billDate 下载对账单的日期，格式：20140603 请与每日9：30分之后调用
     * @param billType  非必填
     * ALL，返回当日所有订单信息，默认值
     * SUCCESS，返回当日成功支付的订单
     * REFUND，返回当日退款订单
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws DocumentException
     */
    public static String downloadbill(String deviceInfo, 
    		String billDate, String billType, String partnerkey) 
    		throws IOException, ExecutionException, InterruptedException, DocumentException {
    	// 构造签名
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String appid = weixinKit.getAppid();
    	String mchId = weixinKit.getMchId();
    	String nonceStr = getNonceStr();
    	SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", appid);
		packageParams.put("mch_id", mchId);
		if(!StringUtils.isBlank(deviceInfo)){
			packageParams.put("device_info", deviceInfo);
		}
		packageParams.put("nonce_str", nonceStr);
		packageParams.put("bill_date", billDate);
		packageParams.put("bill_type", billType);
		String sign = Pay.createSign(packageParams);
    	// 构造POST参数
		StringBuffer xml = new StringBuffer("<xml>" 
    			+ "<nonce_str>" + nonceStr+ "</nonce_str>" 
				+ "<sign><![CDATA[" + sign + "]]></sign>" 
				+ "<mch_id>" + mchId+ "</mch_id>"
				+ "<appid><![CDATA[" + appid + "]]></appid>"
		    	+ "<bill_date><![CDATA[" + billDate + "]]></bill_date>");
		if(!StringUtils.isBlank(deviceInfo)){
    		xml.append("<device_info><![CDATA[" + deviceInfo + "]]></device_info>");
		}
		if(!StringUtils.isBlank(billType)){
    		xml.append("<bill_type><![CDATA[" + billType + "]]></bill_type>");
		}
    	xml.append("</xml>");
    	// 提交至微信并获得内容
		String resultFromWeixin = HttpKit.post(DOWNLOADBILL_URL, xml.toString());
		LOG.info("downloadbill is {}", resultFromWeixin);
		if (resultFromWeixin.contains("return_code")) {
			Map<String,String> resultMap =  ParseXmlUtil.parseXml(resultFromWeixin);
			if(resultMap.containsKey("return_code")&&resultMap.get("return_code").equalsIgnoreCase("fail")){
				LOG.error("downloadbill {} error, cause by {}", resultMap.get("return_code"), resultMap.get("return_msg"));
				return null;
			}
		} else {
			return resultFromWeixin;
		}
		return null;
	}
    
    /**
     * 微信支付V3
     * 创建一个扫码支付二维码
     * @param outTradeNo 订单号ID或商品Id
     * @param filePath 二维码存放路径
     * @param fileName 二维码文件名称
     * @return
     */
    public static String createBizpayQrcode(String outTradeNo, String filePath, String fileName) {
    	String nonceStr = getNonceStr();
    	String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit(); // 获得微信相关信息
    	SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", weixinKit.getAppid());
		packageParams.put("mch_id", weixinKit.getMchId());
		packageParams.put("nonce_str", nonceStr);
		packageParams.put("time_stamp", timestamp);
		packageParams.put("product_id", outTradeNo);
		String sign = Pay.createSign(packageParams);
    	
		String url = "weixin://wxpay/bizpayurl?appid=" + weixinKit.getAppid() +
				"&mch_id=" + weixinKit.getMchId() +
				"&nonce_str=" + nonceStr +
				"&product_id=" + outTradeNo +
				"&time_stamp=" + timestamp +
				"&sign=" + sign;
		File directory = new File(filePath);
		if(!directory.exists()){
			directory.mkdir();
		}
		File file = Qrcod.createQrcode(url, filePath, fileName);
		LOG.info("create bizpay qrcode file path is {} ", filePath+fileName);
		return file.getPath();
	}
    
    /**
     * 微信支付V3
     * 统一下单接口
     * @param req
     * @param totalFee 商品价格
     * @param body 商品描述
     * @param openId 公众账号appid
     * @param orderNo 订单Id
     * @param nonce_str 随机字符串
     * @param notify_url 回调URL
     * @param trade_type JSAPI、 NATIVE、 APP
     * @param productId trade_type 为 NATIVE时需要填写
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws DocumentException
     */
    public static String unifiedorder(HttpServletRequest req,String totalFee, String body, String openId, String orderNo, String nonce_str,
    		 String notify_url, String trade_type, String productId) 
    				throws IOException, ExecutionException, InterruptedException, DocumentException{
		// 订单生成的机器 IP
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String spbill_create_ip = getIp(req);
		// 这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", weixinKit.getAppid());
		packageParams.put("mch_id", weixinKit.getMchId());
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", body);
		packageParams.put("out_trade_no", orderNo);
		packageParams.put("total_fee", totalFee); // 商品金额
		packageParams.put("spbill_create_ip", spbill_create_ip);
		packageParams.put("notify_url", notify_url);
		packageParams.put("trade_type", trade_type);
		if(StringUtils.isNotBlank(openId)){
			packageParams.put("openid", openId);
		}
		if(StringUtils.isNotBlank(productId)){
			packageParams.put("product_id", productId);
		}

		String sign = Pay.createSign(packageParams);
		
		// 构造POST参数
		StringBuffer xml = new StringBuffer("<xml>" 
				+ "<appid>" + weixinKit.getAppid() + "</appid>" 
				+ "<mch_id>" + weixinKit.getMchId() + "</mch_id>" 
				+ "<nonce_str>" + nonce_str+ "</nonce_str>" 
				+ "<sign><![CDATA[" + sign + "]]></sign>"
				+ "<body><![CDATA[" + body + "]]></body>"
				+ "<out_trade_no>"+ orderNo+ "</out_trade_no>"
				+ "<total_fee>"+ totalFee + "</total_fee>"
				+ "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
				+ "<notify_url>" + notify_url + "</notify_url>"
				+ "<trade_type>" + trade_type + "</trade_type>" );
		if(StringUtils.isNotBlank(openId)){
			xml.append("<openid>"+ openId + "</openid>");
		}
		if(StringUtils.isNotBlank(productId)){
			xml.append("<product_id>"+ productId + "</product_id>");
		}
    	xml.append("</xml>");
    
		String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		String resultFromWeixin = HttpKit.post(createOrderURL, xml.toString());
		Map<String,String> resultMap =  ParseXmlUtil.parseXml(resultFromWeixin);
		String prepay_id =  resultMap.get("prepay_id");
		if (prepay_id==null || prepay_id.equals("")) {
			LOG.error("get weixin order (unifiedorder) error, cause by {} and {},{}", new Object[]{resultMap.get("return_msg"), resultMap.get("err_code"), resultMap.get("err_code_des")});
		} else {
			LOG.info("get weixin order (unifiedorder) prepay_id is {}", prepay_id);
		}
		return prepay_id;
    }
    
    /**
     * 微信支付V3 签名
	 * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 */
	public static String createSign(SortedMap<String, String> packageParams) {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, String>> es = packageParams.entrySet();
		Iterator<Entry<String, String>> it = es.iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String k = entry.getKey();
			String v = entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k)
					&& !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + weixinKit.getPartnerkey());
		String sign = MD5Utils.MD5Encode(sb.toString(), "UTF-8")
				.toUpperCase();
		return sign;
	}
	
    /**
	 * 获取ip
	 * @param request
	 * @return
	 */
	private static String getIp(HttpServletRequest request) {
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
	
	/**
	 * 获取一个随机数
	 * @return
	 */
	public static String getNonceStr() {
		String currTime = String.valueOf(System.currentTimeMillis());
		// 8位日期
		String strTime = currTime.substring(8, currTime.length());
		// 四位随机数
		String strRandom = RandomStringUtils.random(4, "123456789") + "";
		// 10位序列号,可以自行调整。随机数
		String nonce_str = strTime + strRandom;
		
		return nonce_str;
	}
}
