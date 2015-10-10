package cn.com.kehwa.weixin.oauth;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.com.kehwa.weixin.message.bean.Article;
import cn.com.kehwa.weixin.message.request.MsgTypes;
import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

/**
 * 素材接口
 */
public class Media {
	private final static Logger LOG = LoggerFactory.getLogger(Media.class);

	private static final String UPLOAD_IMG_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=";
	private static final String UPLOAD_VIDEO_URL = "https://file.api.weixin.qq.com/cgi-bin/media/uploadvideo?access_token=";
	private static final String UPLOAD_NEWS_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=";
	private static final String UPLOAD_MEDIA_URL = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	private static final String GET_MEDIA_URL = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
	private static final String GET_MATERIAL_URL = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=";
	private static final String ADD_NEWS_URL = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=";
	private static final String ADD_MATERIAL_URL = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE";
	private static final String UPDATE_MATERIAL_URL = "https://api.weixin.qq.com/cgi-bin/material/update_news?access_token=";
	private static final String GET_MATERIAL_COUNT_URL = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=";
	private static final String GET_MATERIAL_LIST_URL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=";

	/**
	 * 上传图文消息内的图片获取URL
	 * 
	 * @param type
	 * @param imgFile
	 * @return
	 * @throws Exception
	 */
	public JSONObject uploadImg(File imgFile) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
		String result = HttpKit.upload(UPLOAD_IMG_URL.concat(accessToken), imgFile);
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}

	/**
	 * 上传视频消息内的图片获取URL
	 * 
	 * @param type
	 * @param videoFile
	 * @return
	 * @throws Exception
	 */
	public JSONObject uploadVideo(String media_id, String title, String description) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
		Map<String, String> json = new HashMap<String, String>();
		json.put("media_id", media_id);
		json.put("title", title);
		json.put("description", description);
		LOG.info("upload video json is {}", JSONObject.toJSONString(json));

		String result = HttpKit.post(UPLOAD_VIDEO_URL.concat(accessToken), JSONObject.toJSONString(json));
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}

	/**
	 * 上传图文消息素材
	 * 
	 * @param articleList
	 * @return
	 * @throws Exception
	 */
	public JSONObject uploadNews(List<Article> articleList) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("articles", articleList);
		LOG.info("upload news json is {}", JSONObject.toJSONString(json));

		String result = HttpKit.post(UPLOAD_NEWS_URL.concat(accessToken), JSONObject.toJSONString(json));
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}

	/**
	 * 新增临时素材
	 * 
	 * @param type 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
	 * @param media form-data中媒体文件标识，有filename、filelength、content-type等信息
	 * @return
	 * @throws Exception
	 */
	public JSONObject uploadMedia(MsgTypes type, File media) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
		String result = HttpKit.upload(UPLOAD_MEDIA_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type.getType()), media);
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}

	/**
	 * 获取临时素材
	 * 
	 * @param media_id
	 * @return
	 * @throws Exception
	 */
	public JSONObject getMedia(String media_id) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
		String result = HttpKit.get(GET_MEDIA_URL.replace("ACCESS_TOKEN", accessToken).replace("MEDIA_ID", media_id));
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}

	/**
	 * 获取永久素材
	 * 
	 * @param media_id
	 * @return
	 * @throws Exception
	 */
	public JSONObject getMaterial(String media_id) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("media_id", media_id);

		String result = HttpKit.post(GET_MATERIAL_URL.concat(accessToken), JSONObject.toJSONString(json));
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}

	/**
	 * 新增永久图文消息素材
	 * 
	 * @param type
	 * @param mediaFile
	 * @return
	 * @throws Exception
	 */
	public JSONObject addNews(List<Article> articleList) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("articles", articleList);

		String result = HttpKit.post(ADD_NEWS_URL.concat(accessToken), JSONObject.toJSONString(json));
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}

	/**
	 * 新增其他类型永久素材
	 * 
	 * @param type 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
	 * @param media form-data中媒体文件标识，有filename、filelength、content-type等信息
	 * @return
	 * @throws Exception
	 */
	public JSONObject addMaterial(MsgTypes type, File media) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
		String result = HttpKit.upload(ADD_MATERIAL_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type.getType()), media);
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}

	/**
	 * 修改永久图文素材
	 * 
	 * @param media_id 要修改的图文消息的id
	 * @param index 要更新的文章在图文消息中的位置（多图文消息时，此字段才有意义），第一篇为0
	 * @param article 要更新的文章消息
	 * @return
	 * @throws Exception
	 */
	public JSONObject updateMaterial(String media_id, String index, Article article) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("media_id", media_id);
		json.put("index", index);
		json.put("articles", article);

		String result = HttpKit.post(UPDATE_MATERIAL_URL.concat(accessToken), JSONObject.toJSONString(json));
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}

	/**
	 * 获取素材总数
	 * 
	 * @return
	 * @throws Exception
	 */
	public JSONObject getMaterialCount() throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
		String result = HttpKit.get(GET_MATERIAL_COUNT_URL.concat(accessToken));
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}

	/**
	 * 获取素材列表
	 * 
	 * @param type 素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）
	 * @param offset 从全部素材的该偏移位置开始返回，0表示从第一个素材 返回
	 * @param count 返回素材的数量，取值在1到20之间
	 * @return
	 * @throws Exception
	 */
	public JSONObject getMaterialList(MsgTypes type, String offset, String count) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String accessToken = weixinKit.getAccessToken();
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("type", type.getType());
		json.put("offset", offset);
		json.put("count", count);

		String result = HttpKit.post(GET_MATERIAL_LIST_URL.concat(accessToken), JSONObject.toJSONString(json));
		if (StringUtils.isNotEmpty(result)) {
            return JSONObject.parseObject(result);
        }
		return null;
	}
}

