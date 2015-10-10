package cn.com.kehwa.weixin.util;

import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import cn.com.kehwa.weixin.message.response.BaseMessage;
import cn.com.kehwa.weixin.message.response.ImageMessage;
import cn.com.kehwa.weixin.message.response.MusicMessage;
import cn.com.kehwa.weixin.message.response.NewsMessage;
import cn.com.kehwa.weixin.message.response.TextMessage;
import cn.com.kehwa.weixin.message.response.VideoMessage;
import cn.com.kehwa.weixin.message.response.VoiceMessage;

/**
 * 解析与转换XML
 * @author ShiTuo
 *
 */
public class ParseXmlUtil {
	/**
	 * 根据文本解析xml
	 * 
	 * @param strxml
	 * @return
     * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(String strxml) throws DocumentException {
		Map<String, String> map = new HashMap<String, String>();
		Document document = DocumentHelper.parseText(strxml);
		// 得到XML根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();
		// 遍历所有子节点
		for (Element e : elementList) {
			map.put(e.getName(), e.getText());
		}
		return map;
	}
	
    /**
	 * 解析微信发来的请求(XML)
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();

		// 从request中取得输入流
		InputStream inputStream = request.getInputStream();
		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);

		// 得到XML根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();

		// 遍历所有子节点
		for (Element e : elementList) {
			map.put(e.getName(), e.getText());
		}
		// 释放资源
		inputStream.close();
		inputStream = null;

		return map;
	}
	
	/**
	 * 扩展xstream使支持CDATA
	 */
	private static XStream xstream = new XStream(new XppDriver() {

		@Override
		public HierarchicalStreamWriter createWriter(Writer out) {

			return new PrettyPrintWriter(out) {
				// 对其所有XML节点的转换都增加CDATA标记
				boolean cdata = true;

				@Override
				public void startNode(String name, Class clazz) {
					super.startNode(name, clazz);
				}

				@Override
				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}

			};
		}
	});

	/**
	 * 文本消息对象转换成XML
	 */
	public static String messageToXml(TextMessage textMessage) {
		xstream.alias("xml", textMessage.getClass());
		return xstream.toXML(textMessage);

	}

	/**
	 * 图片消息对象转换成XML
	 */
	public static String messageToXml(ImageMessage imageMessage) {
		xstream.alias("xml", imageMessage.getClass());
		return xstream.toXML(imageMessage);

	}

	/**
	 * 语音消息对象转换成XML
	 */
	public static String messageToXml(VoiceMessage voiceMessage) {
		xstream.alias("xml", voiceMessage.getClass());
		return xstream.toXML(voiceMessage);

	}

	/**
	 * 视频消息对象转换成XML
	 */
	public static String messageToXml(VideoMessage videoMessage) {
		xstream.alias("xml", videoMessage.getClass());
		return xstream.toXML(videoMessage);

	}

	/**
	 * 音乐消息对象转换成XML
	 */
	public static String messageToXml(MusicMessage musicMessage) {
		xstream.alias("xml", musicMessage.getClass());
		return xstream.toXML(musicMessage);

	}

	/**
	 * 图文消息对象转换成XML
	 */
	public static String messageToXml(NewsMessage newsMessage) {
		xstream.alias("xml", newsMessage.getClass());
		xstream.alias("item", new cn.com.kehwa.weixin.message.response.Article().getClass());
		return xstream.toXML(newsMessage);

	}
	/**
	 * transfer_customer_service消息对象转换成XML
	 */
	public static String messageToXml(BaseMessage baseMessage) {
		xstream.alias("xml", baseMessage.getClass());
		return xstream.toXML(baseMessage);

	}
}
