package cn.com.kehwa.weixin.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.kehwa.weixin.message.request.MsgTypes;
import cn.com.kehwa.weixin.message.response.Article;
import cn.com.kehwa.weixin.message.response.BaseMessage;
import cn.com.kehwa.weixin.message.response.NewsMessage;
import cn.com.kehwa.weixin.message.response.TextMessage;
import cn.com.kehwa.weixin.util.ParseXmlUtil;

public class WeixinCoreService {
	
	public String processRequest(HttpServletRequest request) {
		basePath = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
		try {
			// xml请求解析
			Map<String, String> requestMap = ParseXmlUtil.parseXml(request);
			// 消息类型
			String msgType = requestMap.get("MsgType");
			log.info("get a user message, the type is:{}",msgType);
			// 事件推送
			if (msgType.equals(MsgTypes.EVENT.getType())) {
//				wxEventLogService.addEventLog(requestMap);// 记录事件日志
				// 事件类型
				String eventType = requestMap.get("Event");
				// 订阅
				if (eventType.equals(MsgTypes.EVENT_TYPE_SUBSCRIBE.getType())) {
					log.info("new user guanzhu you weixin,the open id is{}",requestMap.get("FromUserName"));
					return replyString(requestMap, "谢谢关注");
				}
				// 取消订阅
				else if (eventType.equals(MsgTypes.EVENT_TYPE_UNSUBSCRIBE.getType())) {
					// 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
					log.info("new user not guanzhu you weixin,the open id is{}",requestMap.get("FromUserName"));
					return null;
				}
				// 自定义菜单点击事件
				// s 开头的表示单图文， m 开头的表示多图文，g开头表示高级功能模块  其余回复文字
				else if (eventType.equals(MsgTypes.EVENT_TYPE_CLICK.getType())) {
					String buttonType = requestMap.get("EventKey");
					log.info("user chick you weixin menu, the open id is{}, Event key is {}",requestMap.get("FromUserName"), buttonType);
					if (isSingalArticle(buttonType)) {
						// 回复单图文
						String id = buttonType.substring(1);
						return replySingalArticle(requestMap, id);
					} else if (isMultiArticle(buttonType)) {
						// 回复多图文
						String id = buttonType.substring(1);
						return replyMultiArticle(requestMap, id);
					} else if(isAdvancedModel(buttonType)) {
						// 高级模块功能
						String id = buttonType.substring(1);
						return replyAdvancedModel(requestMap, id);
					}
					
					return replyString(requestMap, buttonType);
				}
				// 点击view按钮事件
				else if (eventType.equals("VIEW")) {
					return null;
				}
				// 地理位置事件
				else if (eventType.equals(MsgTypes.LOCATION.getType())) {
					log.info("new user wang ge location,the open id is{}",requestMap.get("FromUserName"));
					return "";
				}
				// 二维码事件
				else if (eventType.equals(MsgTypes.EVENT_TYPE_SCAN.getType())) {
					log.info("new user do erweima event,the open id is{}",requestMap.get("FromUserName"));
					return "";
				}
			} else {
				// 记录日志
//				wxRequestLogService.addRequestLog(requestMap);
				log.info("new user send message for you,the open id is{}",requestMap.get("FromUserName"));
				// 如果不是event,就是消息
				// 文本消息
				if (msgType.equals(MsgTypes.TEXT.getType())) {
						return autoReply(requestMap);
						// 如果关闭了自动回复，就全部当做客服消息处理
						// return replyCustomerService(requestMap);
				}
				// 图片消息
				else if (msgType.equals(MsgTypes.IMAGE.getType())) {
					return replyString(requestMap, "您发送的是图片消息！");
				}
				// 地理位置消息
				else if (msgType.equals(MsgTypes.LOCATION.getType())) {
					return replyString(requestMap, "您发送的是地理位置消息！");
				}
				// 链接消息
				else if (msgType.equals(MsgTypes.LINK.getType())) {
					return replyString(requestMap, "您发送的是链接消息！");
				}
				// 音频消息
				else if (msgType.equals(MsgTypes.VOICE.getType())) {
					return replyString(requestMap, "您发送的是音频消息！");
				}
				// 视频消息
				else if (msgType.equals(MsgTypes.VIDEO.getType())) {
					return replyString(requestMap, "您发送的是视频消息！");
				}
			}
			return replyString(requestMap, "请求处理异常，请稍候尝试！");
		} catch (Exception e) {
			log.error("unkown xml format",e);
		}

		return null;
	}
	
	/**
	 * 文本回复
	 * 
	 * @param requestMap
	 * @param respContent
	 * @return
	 */
	private String replyString(Map<String, String> requestMap, String respContent) {
		String respMessage = "";
		// 发送方帐号（open_id）
		String fromUserName = requestMap.get("FromUserName");
		// 公众帐号
		String toUserName = requestMap.get("ToUserName");
		// 回复文本消息
		TextMessage textMessage = new TextMessage();
		textMessage.setToUserName(fromUserName);
		textMessage.setFromUserName(toUserName);
		textMessage.setCreateTime(new Date().getTime());
		textMessage.setMsgType(MsgTypes.TEXT.getType());
		textMessage.setContent(respContent);
		respMessage = ParseXmlUtil.messageToXml(textMessage);
		return respMessage;
	}
	
	/**
	 * 自动回复
	 * 
	 * @param requestMap
	 * @return
	 */
	private String autoReply(Map<String, String> requestMap) {
		String respMessage = "";
		String content = requestMap.get("Content"); // 内容
		// 取得自动回复内容
//		if (rs != null) {
//			switch (rs.getType()) {
//			case 1:{ 
//				// text
//				// 回复文本消息
//				respMessage = replyString(requestMap, rs.getValue());
//				break;
//			}
//			case 2:{
//				// img
//				// 回复图本消息
//				respMessage = replySingalArticle(requestMap, rs.getValue());
//				break;
//			}
//			case 3:{
//				// multiImg
//				// 回复多图本消息
//				respMessage = replyMultiArticle(requestMap, rs.getValue());
//				break;
//			}
//			default:
//				log.info("自动回复出错 cause {}", rs.getType());
//				break;
//			}
//		} else {
			// 回复文本消息
			respMessage = replyString(requestMap,"亲，您的信息已收到，稍后会有客服人员进行回复，请稍等哦~~");
			// 如果没有自动回复关键字就当做是客服消息
			// respMessage = replyCustomerService(requestMap);
//		}
		return respMessage;
	}

	

	/**
	 * 单图文回复
	 * 
	 * @param requestMap
	 * @param id
	 * @return
	 */
	private String replySingalArticle(Map<String, String> requestMap, String id) {
		String respMessage = "";
		String fromUserName = requestMap.get("FromUserName");// 发送方帐号（open_id）
		String toUserName = requestMap.get("ToUserName");// 接收方公众帐号
		// 回复单图文消息
		List<Article> list = new ArrayList<Article>();
		// 取得内容
		
		NewsMessage newsMessage = new NewsMessage();
		newsMessage.setToUserName(fromUserName);
		newsMessage.setFromUserName(toUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(MsgTypes.NEWS.getType());
		newsMessage.setArticles(list);
		newsMessage.setArticleCount(list.size());
		respMessage = ParseXmlUtil.messageToXml(newsMessage);
		log.info("单图文回复信息,{}",respMessage);
		return respMessage;
	}

	/**
	 * 多图文回复
	 * 
	 * @param requestMap
	 * @param id
	 * @return
	 */
	private String replyMultiArticle(Map<String, String> requestMap, String id) {
		// 发送方帐号（open_id）
		String fromUserName = requestMap.get("FromUserName");
		// 公众帐号
		String toUserName = requestMap.get("ToUserName");
		// 回复多图文消息
		List<Article> list = new ArrayList<Article>();
		// 取得内容
		NewsMessage newsMessage = new NewsMessage();
		newsMessage.setToUserName(fromUserName);
		newsMessage.setFromUserName(toUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(MsgTypes.NEWS.getType());
		newsMessage.setArticles(list);
		newsMessage.setArticleCount(list.size());
		String respMessage = ParseXmlUtil.messageToXml(newsMessage);
		return respMessage;
	}
	
	/**
	 * 高级模块处理
	 * @param requestMap
	 * @param id
	 * @return
	 */
	private String replyAdvancedModel(Map<String, String> requestMap, String id) {
		String respMessage = "";
		// 发送方帐号（open_id）
		String fromUserName = requestMap.get("FromUserName");
		// 公众帐号
		String toUserName = requestMap.get("ToUserName");
		
		if(id.equals("1")){
			// 高级模块1  发送多图文
			List<Article> list = new ArrayList<Article>();
			
			NewsMessage newsMessage = new NewsMessage();
			newsMessage.setToUserName(fromUserName);
			newsMessage.setFromUserName(toUserName);
			newsMessage.setCreateTime(new Date().getTime());
			newsMessage.setMsgType(MsgTypes.NEWS.getType());
			newsMessage.setArticles(list);
			newsMessage.setArticleCount(list.size());
			respMessage = ParseXmlUtil.messageToXml(newsMessage);
		} else {
			respMessage = replyString(requestMap, "高级模块错误，请于管理员联系.");
		}
		
		return respMessage;
	}
	
	/**
	 * 多客服消息回复
	 * 
	 * @param requestMap
	 * @return
	 */
	private String replyCustomerService(Map<String, String> requestMap) {
		log.debug("客服方法被调用+---------------");
		String respMessage = "";
		String fromUserName = requestMap.get("FromUserName");
		String toUserName = requestMap.get("ToUserName");
		// 给服务器发送MsgType为transfer_customer_service通知服务器把消息转到多客服系统
		BaseMessage baseMessage = new BaseMessage();
		baseMessage.setToUserName(fromUserName);
		baseMessage.setFromUserName(toUserName);
		baseMessage.setCreateTime(new Date().getTime());
		baseMessage.setMsgType(MsgTypes.RESP_MESSAGE_TYPE_TRANSFER_CUSTOMER_SERVICE.getType());
		respMessage = ParseXmlUtil.messageToXml(baseMessage);

		return respMessage;
	}

	/**
	 * 判断是否是单图文
	 * 
	 * @param buttonType
	 * @return
	 */
	private boolean isSingalArticle(String buttonType) {
		if (buttonType.length() < 2)
			return false;
		char[] s = buttonType.toCharArray();
		if (s[0] == 's') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是多图文
	 * 
	 * @param buttonType
	 * @return
	 */
	private boolean isMultiArticle(String buttonType) {
		if (buttonType.length() < 2)
			return false;
		char[] s = buttonType.toCharArray();
		if (s[0] == 'm') {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断是否是高级模块
	 * 
	 * @param buttonType
	 * @return
	 */
	private boolean isAdvancedModel(String buttonType) {
		if (buttonType.length() < 2)
			return false;
		char[] s = buttonType.toCharArray();
		if (s[0] == 'g') {
			return true;
		} else {
			return false;
		}
	}
	

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private String basePath; // 网站地址
}