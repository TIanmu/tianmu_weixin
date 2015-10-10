/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2014 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package cn.com.kehwa.weixin.oauth;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.com.kehwa.weixin.message.bean.Article;
import cn.com.kehwa.weixin.message.bean.Articles;
import cn.com.kehwa.weixin.message.bean.TemplateData;
import cn.com.kehwa.weixin.message.response.SendAllMsgTypes;
import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

/**
 * 客服消息接口
 *
 * @author shituo
 */
public class Message {

    private static final String MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
    private static final String UPLOADNEWS_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=";
    private static final String MASS_SENDALL_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=";
    private static final String MASS_DELETE_URL = "https://api.weixin.qq.com//cgi-bin/message/mass/delete?access_token=";
    private static final String MASS_GET_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/get?access_token=ACCESS_TOKEN";
    private static final String TEMPLATE_SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
    private static final String MASS_PREVIEW_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=ACCESS_TOKEN";
    	
    private final static Logger LOG = LoggerFactory.getLogger(Message.class);
    
    /**
     * 发送 客服消息
     * @param message
     * @return
     * @throws Exception
     */
    private String sendMsg(Map<String, Object> message) throws Exception {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
    	
        String result = HttpKit.post(MESSAGE_URL.concat(accessToken), JSONObject.toJSONString(message));
        return result;
    }

    /**
     * 发送 文本客服消息
     *
     * @param openId
     * @param text
     * @throws Exception
     */
    public String sendText(String openId, String text) throws Exception {
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> textObj = new HashMap<String, Object>();
        textObj.put("content", text);
        json.put("touser", openId);
        json.put("msgtype", "text");
        json.put("text", textObj);
        String result = sendMsg(json);
        return result;
    }

    /**
     * 发送 图片消息
     *
     * @param openId
     * @param media_id
     * @return
     * @throws Exception
     */
    public String sendImage(String openId, String media_id) throws Exception {
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> textObj = new HashMap<String, Object>();
        textObj.put("media_id", media_id);
        json.put("touser", openId);
        json.put("msgtype", "image");
        json.put("image", textObj);
        String result = sendMsg(json);
        return result;
    }

    /**
     * 发送 语言回复
     *
     * @param openId
     * @param media_id
     * @return
     * @throws Exception
     */
    public String sendVoice(String openId, String media_id) throws Exception {
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> textObj = new HashMap<String, Object>();
        textObj.put("media_id", media_id);
        json.put("touser", openId);
        json.put("msgtype", "voice");
        json.put("voice", textObj);
        String result = sendMsg(json);
        return result;
    }

    /**
     * 发送 视频回复
     *
     * @param openId
     * @param media_id
     * @param title
     * @param description
     * @return
     * @throws Exception
     */
    public String sendVideo(String openId, String media_id, String title, String description) throws Exception {
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> textObj = new HashMap<String, Object>();
        textObj.put("media_id", media_id);
        textObj.put("title", title);
        textObj.put("description", description);

        json.put("touser", openId);
        json.put("msgtype", "video");
        json.put("video", textObj);
        String result = sendMsg(json);
        return result;
    }

    /**
     * 发送 音乐回复
     *
     * @param openId
     * @param musicurl
     * @param hqmusicurl
     * @param thumb_media_id
     * @param title
     * @param description
     * @return
     * @throws Exception
     */
    public String sendMusic(String openId, String musicurl, String hqmusicurl, 
    		String thumb_media_id, String title, String description) throws Exception {
    	
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> textObj = new HashMap<String, Object>();
        textObj.put("musicurl", musicurl);
        textObj.put("hqmusicurl", hqmusicurl);
        textObj.put("thumb_media_id", thumb_media_id);
        textObj.put("title", title);
        textObj.put("description", description);

        json.put("touser", openId);
        json.put("msgtype", "music");
        json.put("music", textObj);
        String result = sendMsg(json);
        return result;
    }

    /**
     * 发送 图文回复
     *
     * @param openId
     * @param articles
     * @return
     * @throws Exception
     */
    public String sendNews(String openId, List<Articles> articles) throws Exception {
    	Map<String, Object> json = new HashMap<String, Object>();
		Map<String, Object> textObj = new HashMap<String, Object>();
		textObj.put("articles", articles);
		
		json.put("touser", openId);
		json.put("msgtype", "news");
		json.put("news", textObj);
		
		return sendMsg(json);
    }

    /**
     * 上传图文消息素材
     *
     * @param articles
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     */
    public JSONObject uploadnews(List<Article> articles) throws IOException, ExecutionException, InterruptedException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
    	
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("articles", articles);
        String result = HttpKit.post(UPLOADNEWS_URL.concat(accessToken), JSONObject.toJSONString(json));
        if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
        return null;
    }

    /**
     * 群发消息
     * @param type          群发消息类型
     * @param content       内容
     * @param title         类型是video是有效
     * @param description   类型是video是有效
     * @param groupId       发送目标对象的群组id
     * @param openids       发送目标对象的openid类表
     * @param toAll         是否发送给全部人
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public JSONObject massSendall(SendAllMsgTypes type, 
    		String content, String title, String description, String groupId, 
    		String[] openids, boolean toAll) throws InterruptedException, ExecutionException, IOException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
    	
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> filter = new HashMap<String, Object>();
        Map<String, Object> body = new HashMap<String, Object>();

        filter.put("is_to_all", false);
        json.put("msgtype", type.getType());
        if (toAll) {
            filter.put("is_to_all", true);
        } else if (StringUtils.isNotEmpty(groupId)) {
            filter.put("group_id", groupId);
        } else if (openids != null && openids.length > 0) {
            json.put("touser", openids);
        }

        switch (type) {
            case TEXT:
                body.put("content", content);
                json.put("text", body);
                break;
            case IMAGE:
                body.put("media_id", content);
                json.put("image", body);
                break;
            case VOICE:
                body.put("media_id", content);
                json.put("voice", body);
                break;
            case MPVIDEO:
                body.put("media_id", content);
                json.put("mpvideo", body);
                break;
            case MPNEWS:
                body.put("media_id", content);
                json.put("mpnews", body);
                break;
            case VIDEO:
                body.put("media_id", content);
                body.put("title", title);
                body.put("description", description);
                json.put("video", body);
                break;
        }
        json.put("filter", filter);
        LOG.debug("massSendall json is {}",JSONObject.toJSONString(json));
        String result = HttpKit.post(MASS_SENDALL_URL.concat(accessToken), JSONObject.toJSONString(json));
        if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
        return null;
    }

    /**
     * 删除群发
     *
     * @param msgid
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     */
    public JSONObject massSend(String msgid) throws IOException, ExecutionException, InterruptedException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
    	
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("msgid", msgid);
        String result = HttpKit.post(MASS_DELETE_URL.concat(accessToken), JSONObject.toJSONString(json));
        if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
        return null;
    }
    
    
    /**
     * 群发消息预览
     * @param type          群发消息类型
     * @param content       内容或media_id
     * @param openid        接收人
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public JSONObject massPreview(SendAllMsgTypes type, 
    		String content, String openid) throws InterruptedException, ExecutionException, IOException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
    	
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> body = new HashMap<String, Object>();

        json.put("touser", openid);
        json.put("msgtype", type.getType());
        switch (type) {
            case TEXT:
                body.put("content", content);
                json.put("text", body);
                break;
            case IMAGE:
                body.put("media_id", content);
                json.put("image", body);
                break;
            case VOICE:
                body.put("media_id", content);
                json.put("voice", body);
                break;
            case MPVIDEO:
                body.put("media_id", content);
                json.put("mpvideo", body);
                break;
            case MPNEWS:
                body.put("media_id", content);
                json.put("mpnews", body);
                break;
            case VIDEO:
                body.put("media_id", content);
                json.put("video", body);
                break;
        }
        LOG.debug("massPreview send json is",JSONObject.toJSONString(json));
        String result = HttpKit.post(MASS_PREVIEW_URL.replace("ACCESS_TOKEN", accessToken), JSONObject.toJSONString(json));
        if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
        return null;
    }
    
    /**
     * 查询群发消息发送状态
     * @param msgId 消息ID
     * @return SEND_SUCCESS 表示成功
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public String massGet(String msgId) throws InterruptedException, ExecutionException, IOException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
    	
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("msg_id", msgId);
        
        LOG.debug("massGet send json is {}", JSONObject.toJSONString(json));
        String result = HttpKit.post(MASS_GET_URL.replace("ACCESS_TOKEN", accessToken), JSONObject.toJSONString(json));
        if (StringUtils.isNotEmpty(result)) {
        	JSONObject jsonObject = JSONObject.parseObject(result);
        	if (jsonObject.containsKey("msg_status")) {
        		return jsonObject.getString("msg_status");
			}
            return "error";
        }
        return null;
    }

    /**
     * 发送模板消息
     *
     * @param accessToken
     * @param data
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public JSONObject templateSend(TemplateData data) throws IOException, ExecutionException, InterruptedException {
    	WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
    	String result = HttpKit.post(TEMPLATE_SEND_URL.concat(accessToken), JSONObject.toJSONString(data));
        if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
        return null;
    }
    
}
