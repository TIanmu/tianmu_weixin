package cn.com.kehwa.weixin.oauth;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.kehwa.weixin.message.card.WxCard;
import cn.com.kehwa.weixin.message.card.WxCardException;
import cn.com.kehwa.weixin.util.HttpKit;
import cn.com.kehwa.weixin.util.WeixinKit;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 微信卡券接口
 * @author shituo
 */
public class Card {
	private static final Logger LOG = LoggerFactory.getLogger(Card.class);
	
	private static final String CARD_CREATE_URI = "https://api.weixin.qq.com/card/create?access_token=ACCESS_TOKEN";
	private static final String MEDIA_UPLOADIMG_URI = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";
	private static final String CARD_QRCODE_CREATE_URI = "https://api.weixin.qq.com/card/qrcode/create?access_token=ACCESS_TOKEN";
	private static final String CARD_CODE_DECRYPT_URI = "https://api.weixin.qq.com/card/code/decrypt?access_token=TOKEN";
	private static final String CARD_CODE_CONSUME_URI = "https://api.weixin.qq.com/card/code/consume?access_token=TOKEN";
	
	/**
	 * 创建卡券
	 * @param accessToken
	 * @param card
	 * @return
	 * @throws Exception
	 */
	public static JSONObject create(WxCard card) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
    	String reslut = HttpKit.post(CARD_CREATE_URI.replace("ACCESS_TOKEN", weixinKit.getAccessToken()), card.toJsonString());
    	if(StringUtils.isNotEmpty(reslut)){
    		return JSONObject.parseObject(reslut);
    	}
        return null;
	}
	
	/**
	 * 卡券投放,创建二维码
	 * @param accessToken
	 * @param cardId 卡券 ID
	 * @param code 指定卡券 code 码，只能被领一次。use_custom_code 字段为 true 的卡券必须填写，非自定义 code 不必填写。
	 * @param openid 指定领取者的 openid，只有该用户能领取。bind_openid字段为 true 的卡券必须填写， 非自定义 openid 不必填写。
	 * @param expireSeconds 指定二维码的有效时间，范围是 60 ~ 1800 秒。不填默认为永久有效。
	 * @param isUniqueCode 指定下发二维码，生成的二维码随机分配一个 code，领取后不可再次扫描。填写 true 或 false。默认 false。
	 * @param balance 红包余额， 以分为单位。 红包类型必填 （LUCKY_MONEY） ，其他卡券类型不填。
	 * @param outerId 领取场景值，用于领取渠道的数据统计，默认值为 0，字段类型为整型。用户领取卡券后触发的事件推送中会带上此自定义场景值。
	 * @return 正确时包含ticket
	 * @throws Exception
	 */
	public static JSONObject publish(String cardId, String code, 
			String openid, String expireSeconds, String isUniqueCode, String balance, String outerId) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
		Map<String,Object> action = new HashMap<String,Object>();
		Map<String,Object> actionInfo = new HashMap<String,Object>();
        Map<String,Object> card = new HashMap<String,Object>();
        card.put("card_id", cardId);
        if(StringUtils.isNotBlank(code)){
        	card.put("code", code);
        }
        if(StringUtils.isNotBlank(openid)){
       	 	card.put("openid", openid);
        }
        if(StringUtils.isNotBlank(expireSeconds)){
       	 	card.put("expire_seconds", expireSeconds);
        }
        if(StringUtils.isNotBlank(isUniqueCode)){
       	 	card.put("is_unique_code", isUniqueCode);
        }
        if(StringUtils.isNotBlank(balance)){
       	 	card.put("balance", balance);
        }
        if(StringUtils.isNotBlank(outerId)){
       	 	card.put("outer_id", outerId);
        }
        actionInfo.put("card", card);
        action.put("action_name", "QR_CARD");
        action.put("action_info", actionInfo);
        String post = JSONObject.toJSONString(action);
        LOG.info("publish card json is {}", post);
		String reslut = HttpKit.post(CARD_CODE_DECRYPT_URI.replace("ACCESS_TOKEN", accessToken), post);
    	if(StringUtils.isNotEmpty(reslut)){
    		return JSONObject.parseObject(reslut);
    	}
        return null;
	}
	
	/**
	 * code 解码接口,用于获得真实的code。 当其为自定义code时无需调用
	 * @param accessToken
	 * @param encryptCode 通过 choose_card_info 获取的加密字符串
	 * @return 真实code
	 * @throws Exception
	 */
	public static String codeDecrypt(String encryptCode) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
		Map<String,Object> json = new HashMap<String,Object>();
		json.put("encrypt_code", encryptCode);
        String post = JSONObject.toJSONString(json);
		String reslut = HttpKit.post(CARD_QRCODE_CREATE_URI.replace("TOKEN", accessToken), post);
    	if(StringUtils.isNotEmpty(reslut)){
    		JSONObject jsonObject = JSONObject.parseObject(reslut);
    		if(jsonObject.containsKey("code")){
    			return jsonObject.getString("code");
    		} else {
    			throw new WxCardException("errcode:"+jsonObject.getString("errcode")+"errmsg:"+jsonObject.getString("errmsg"));
    		}
    	}
        return null;
	}
	
	/**
	 * 消耗 code 接口是核销卡券的唯一接口，仅支持核销有效期内的卡券，否则会返回错误码invalid time。
	 * 自定义 code（use_custom_code 为 true）的优惠券，在 code 被核销时，必须调用此接口。用于将用户客户端的 code 状态变更。
	 * 自定义 code 的卡券调用接口时， post 数据中需包含 card_id，非自定义 code 不需要包含card_id。
	 * @param accessToken
	 * @param code 要消耗序列号
	 * @param cardId 卡券 ID。 创建卡券时 use_custom_code 填写 true时必填。非自定义 code 不必填写。
	 * @return
	 * @throws Exception
	 */
	public static boolean codeConsume(String code, String cardId) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
		Map<String,Object> json = new HashMap<String,Object>();
		json.put("code", code);
		if(StringUtils.isNotBlank(cardId)){
			json.put("card_id", cardId);
        }
        String post = JSONObject.toJSONString(json);
		String reslut = HttpKit.post(CARD_CODE_CONSUME_URI.replace("TOKEN", accessToken), post);
		LOG.info("card code consume result is {}", reslut);
    	if(StringUtils.isNotEmpty(reslut)){
    		JSONObject jsonObject = JSONObject.parseObject(reslut);
    		if(jsonObject.containsKey("openid")){
    			return true;
    		} else {
    			throw new WxCardException("errcode:"+jsonObject.getString("errcode")+"errmsg:"+jsonObject.getString("errmsg"));
    		}
    	}
        return false;
	}
	
	/**
	 * 上传logo图片
	 * 上传的图片限制文件大小限制 1MB，像素为 300*300，支持 JPG 格式。
	 * 调用接口获取的 logo_url 仅支持在微信相关业务下使用，否则会做相应处理
	 * @param accessToken
	 * @param image
	 * @return 返回上传图片后得到的url
	 * @throws Exception
	 */
	public static String uploadimg(File image) throws Exception {
		WeixinKit weixinKit = WeixinKitFactory.getWeixinKit();
		String accessToken = weixinKit.getAccessToken();
    	String reslut = HttpKit.upload(MEDIA_UPLOADIMG_URI.replace("ACCESS_TOKEN", accessToken), image);
    	if(StringUtils.isNotEmpty(reslut)){
    		JSONObject jsonObject = JSONObject.parseObject(reslut);
    		if(jsonObject.containsKey("url")){
    			return jsonObject.getString("url");
    		} else {
    			throw new WxCardException("errcode:"+jsonObject.getString("errcode")+"errmsg:"+jsonObject.getString("errmsg"));
    		}
    	}
        return null;
	}
}