package cn.com.kehwa.weixin.oauth;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.ParseXmlUtil;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

/**
 * 红包相关方法 
 */
public class Redpack {

	private static final Logger LOG = LoggerFactory.getLogger(Redpack.class);
    private static final String SENDREDPACK_URL= "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";// 发送现金红包
    private static final String SENDGROUPREDPACK_URL= "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendgroupredpack";// 发放裂变红包
    private static final String GETHBINFO_URL= "https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo";
    
    /**
     * 发送现金红包
     * @param nick_name 商户名称
     * @param mch_billno 商户订单号
     * @param openid 用户openid
     * @param total_amount 付款金额，单位分
     * @param total_num 红包发放总人数
     * @param wishing 红包祝福语
     * @param act_name 活动名称
     * @param remark 备注
     * @param filePath 密钥
     * @param password 密码（商户号）
     * @return 红包订单的微信单号
     * @throws Exception
     */
    public static String sendredpack(String nick_name, String mch_billno, 
    		String openid, String total_amount, int total_num,
    		String wishing, String act_name, String remark, String filePath, String password) throws Exception{
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String client_ip = "192.168.1.1";//getIp(request)
		String nonce_str = Pay.getNonceStr();
    	
    	SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("nonce_str", nonce_str);// 随机字符串，不长于32位
		packageParams.put("mch_billno", mch_billno);//商户订单号
		packageParams.put("mch_id", weixinKit.getMchId());//商户号
		packageParams.put("wxappid", weixinKit.getAppid());//公众账号appid
		packageParams.put("nick_name", nick_name);// 提供方名称
		packageParams.put("send_name", nick_name);// 商户名称
		packageParams.put("re_openid", openid);// 用户openid
		packageParams.put("total_amount", total_amount);// 付款金额
		packageParams.put("min_value", total_amount);//最小红包金额
		packageParams.put("max_value", total_amount);//最大红包金额
		packageParams.put("total_num", total_num+"");// 红包发放总人数
		packageParams.put("wishing", wishing);// 红包祝福语
		packageParams.put("client_ip", client_ip);// 调用接口的机器Ip地址
		packageParams.put("act_name", act_name);// 活动名称
		packageParams.put("remark", remark);// 备注
		//packageParams.put("logo_imgurl", logo_imgurl);// 商户logo的url
		String sign = Pay.createSign(packageParams);// 获得签名
    	
    	
    	String xml = "<xml>" 
    			+ "<nonce_str>" + nonce_str+ "</nonce_str>" 
				+ "<sign>" + sign + "</sign>" 
				+ "<mch_billno>" + mch_billno + "</mch_billno>" 
				+ "<mch_id>" + weixinKit.getMchId()+ "</mch_id>"
				+ "<wxappid><![CDATA[" + weixinKit.getAppid() + "]]></wxappid>"
				+ "<nick_name><![CDATA[" + nick_name + "]]></nick_name>"
				+ "<send_name><![CDATA[" + nick_name + "]]></send_name>"
				+ "<re_openid><![CDATA[" + openid + "]]></re_openid>"
				+ "<total_amount><![CDATA[" + total_amount + "]]></total_amount>"
				+ "<min_value><![CDATA[" + total_amount + "]]></min_value>"
				+ "<max_value><![CDATA[" + total_amount + "]]></max_value>"
				+ "<total_num>"+ total_num+ "</total_num>"
				+ "<wishing>"+ wishing + "</wishing>"
				+ "<client_ip>" + client_ip + "</client_ip>"
				+ "<act_name>" + act_name + "</act_name>"
				+ "<remark><![CDATA[" + remark + "]]></remark>"
				// + "<logo_imgurl><![CDATA[" + body + "]]></logo_imgurl>"
				// + "<share_content>"+ openId + "</share_content>" 
				// + "<share_url>"+ openId + "</share_url>" 
				// + "<share_imgurl>"+ openId + "</share_imgurl>" 
				+ "</xml>";
		String resultFromWeixin = HttpKit.post(SENDREDPACK_URL, xml, filePath, password);
		Map<String,String> resultMap =  ParseXmlUtil.parseXml(resultFromWeixin);
		if(resultMap.containsKey("return_code")&&resultMap.get("return_code").equals("SUCCESS")){
			if(resultMap.containsKey("result_code")&&resultMap.get("result_code").equals("SUCCESS")){
				return resultMap.get("send_listid");
			} else {
				LOG.error("weixin {} error, cause by {}", resultMap.get("err_code"), resultMap.get("err_code_des"));
				throw new Exception("weixin "+resultMap.get("err_code")+" error, cause by " + resultMap.get("err_code_des"));
			}
		} else {
			LOG.error("weixin send red pack error, cause by {}", resultMap.get("return_msg"));
			throw new Exception("weixin send red pack error, cause by " + resultMap.get("return_msg"));
		}
    }
    
    /**
     * 发放裂变红包
     * @param nick_name 商户名称
     * @param mch_billno 商户订单号
     * @param openid 用户openid
     * @param total_amount 付款金额，单位分
     * @param total_num 红包发放总人数
     * @param wishing 红包祝福语
     * @param act_name 活动名称
     * @param remark 备注
     * @param filePath 密钥
     * @param password 密码（商户号）
     * @return 红包订单的微信单号
     * @throws Exception
     */
    public static String sendgroupredpack(String nick_name, String mch_billno, 
    		String openid, String total_amount, String total_num,
    		String wishing, String act_name, String remark, String filePath, String password) throws Exception{
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String nonce_str = Pay.getNonceStr();
    	
    	SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("nonce_str", nonce_str);// 随机字符串，不长于32位
		packageParams.put("mch_billno", mch_billno);//商户订单号
		packageParams.put("mch_id", weixinKit.getMchId());//商户号
		packageParams.put("wxappid", weixinKit.getAppid());//公众账号appid
		packageParams.put("send_name", nick_name);// 商户名称
		packageParams.put("re_openid", openid);// 用户openid
		packageParams.put("total_amount", total_amount);// 总金额
		packageParams.put("total_num", total_num);// 红包发放总人数
		packageParams.put("amt_type", "ALL_RAND");// 红包金额设置方式
		packageParams.put("wishing", wishing);// 红包祝福语
		packageParams.put("act_name", act_name);// 活动名称
		packageParams.put("remark", remark);// 备注
		String sign = Pay.createSign(packageParams);// 获得签名
    	
    	
    	String xml = "<xml>" 
    			+ "<nonce_str>" + nonce_str+ "</nonce_str>" 
				+ "<sign>" + sign + "</sign>" 
				+ "<mch_billno>" + mch_billno + "</mch_billno>" 
				+ "<mch_id>" + weixinKit.getMchId()+ "</mch_id>"
				+ "<wxappid><![CDATA[" + weixinKit.getAppid() + "]]></wxappid>"
				+ "<send_name><![CDATA[" + nick_name + "]]></send_name>"
				+ "<re_openid><![CDATA[" + openid + "]]></re_openid>"
				+ "<total_amount><![CDATA[" + total_amount + "]]></total_amount>"
				+ "<amt_type><![CDATA[ALL_RAND]]></amt_type>"
				+ "<total_num>"+ total_num+ "</total_num>"
				+ "<wishing>"+ wishing + "</wishing>"
				+ "<act_name>" + act_name + "</act_name>"
				+ "<remark><![CDATA[" + remark + "]]></remark>"
				+ "</xml>";
		String resultFromWeixin = HttpKit.post(SENDGROUPREDPACK_URL, xml, filePath, password);
		Map<String,String> resultMap =  ParseXmlUtil.parseXml(resultFromWeixin);
		if(resultMap.containsKey("return_code")&&resultMap.get("return_code").equals("SUCCESS")){
			if(resultMap.containsKey("result_code")&&resultMap.get("result_code").equals("SUCCESS")){
				return resultMap.get("send_listid");
			} else {
				LOG.error("weixin {} error, cause by {}", resultMap.get("err_code"), resultMap.get("err_code_des"));
				throw new Exception("weixin "+resultMap.get("err_code")+" error, cause by " + resultMap.get("err_code_des"));
			}
		} else {
			LOG.error("weixin sendgroupredpack error, cause by {}", resultMap.get("return_msg"));
			throw new Exception("weixin sendgroupredpack error, cause by " + resultMap.get("return_msg"));
		}
    }
    
    /**
     * 红包查询
     * @param mch_billno 商户订单号
     * @param filePath 密钥
     * @param password 密码（商户号）
     * @return 红包订单
     * @throws Exception
     */
    public static String gethbinfo(String mch_billno, String filePath, String password) throws Exception{
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String nonce_str = Pay.getNonceStr();
    	
    	SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("nonce_str", nonce_str);// 随机字符串，不长于32位
		packageParams.put("mch_billno", mch_billno);//商户订单号
		packageParams.put("mch_id", weixinKit.getMchId());//商户号
		packageParams.put("appid", weixinKit.getAppid());//公众账号appid
		packageParams.put("bill_type", "MCHT");// 订单类型
		String sign = Pay.createSign(packageParams);// 获得签名
    	
    	String xml = "<xml>" 
				+ "<sign>" + sign + "</sign>" 
				+ "<mch_billno>" + mch_billno + "</mch_billno>" 
				+ "<mch_id>" + weixinKit.getMchId()+ "</mch_id>"
				+ "<appid><![CDATA[" + weixinKit.getAppid() + "]]></appid>"
				+ "<bill_type><![CDATA[MCHT]]></bill_type>"
				+ "<nonce_str>" + nonce_str+ "</nonce_str>" 
				+ "</xml>";
		String resultFromWeixin = HttpKit.post(GETHBINFO_URL, xml, filePath, password);
		Map<String,String> resultMap =  ParseXmlUtil.parseXml(resultFromWeixin);
		if(resultMap.containsKey("return_code")&&resultMap.get("return_code").equals("SUCCESS")){
			if(resultMap.containsKey("result_code")&&resultMap.get("result_code").equals("SUCCESS")){
				if (resultMap.get("hb_type").equalsIgnoreCase("GROUP")) {
					//裂变红包 
					return resultMap.get("hblist");
				} else {
					// NORMAL:普通红包
					return resultFromWeixin;
				}
			} else {
				LOG.error("weixin {} error, cause by {}", resultMap.get("err_code"), resultMap.get("err_code_des"));
				throw new Exception("weixin "+resultMap.get("err_code")+" error, cause by " + resultMap.get("err_code_des"));
			}
		} else {
			LOG.error("weixin gethbinfo error, cause by {}", resultMap.get("return_msg"));
			throw new Exception("weixin gethbinfo error, cause by " + resultMap.get("return_msg"));
		}
    }
}
