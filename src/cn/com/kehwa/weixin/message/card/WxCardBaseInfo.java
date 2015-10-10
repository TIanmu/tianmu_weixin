package cn.com.kehwa.weixin.message.card;

import java.util.Collection;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author shituo
 */
public class WxCardBaseInfo
{

	JSONObject m_data;

    public WxCardBaseInfo()
    {
        m_data = new JSONObject();
        m_data.put("date_info", new JSONObject());
        m_data.put("location_id_list", new JSONArray());
        m_data.put("sku", new JSONObject());
    }

    public String toJsonString()
    {
        return m_data.toString();
    }

    public String toString()
    {
        return toJsonString();
    }
    
    /**
     * 卡券的商户 logo，尺寸为300*300。
     * @param logoUrl
     */
    public void setLogoUrl(String logoUrl)
    {
        m_data.put("logo_url", logoUrl);
    }

    public String getLogoUrl()
    {
        return m_data.getString("logo_url");
    }
	
	/**
     * code 码展示类型
     */
	public enum CodeType {
		/**
         * "CODE_TYPE_TEXT"，文本；
         */
        CODE_TYPE_TEXT("文本"),
        /**
         * "CODE_TYPE_BARCODE"， 一维码 ；
         */
        CODE_TYPE_BARCODE(" 一维码"),
        /**
         * "CODE_TYPE_QRCODE"，二维码；
         */
        CODE_TYPE_QRCODE("二维码"),
        /**
         * 二维码无 code 显示
         */
        CODE_TYPE_ONLY_QRCODE("二维码无 code"),
        /**
         * 一维码无 code 显示
         */
        CODE_TYPE_ONLY_BARCODE("一维码无 code");
        
        private String value;
        
        private CodeType(String value){
        	this.value = value;
        }

		public String getValue() {
			return value;
		}
	}

    /**
     * code 码展示类型。
     * @param code
     */
    public void setCodeType(CodeType code)
    {
        m_data.put("code_type", code.toString());
    }

    public int getCodeType()
    {
        return m_data.getInteger("code_type");
    }

    /**
     * 商户名字,字数上限为 12 个汉字
     * @param name
     */
    public void setBrandName(String name)
    {
        m_data.put("brand_name", name);
    }

    public String GetBrandName()
    {
        return m_data.getString("brand_name");
    }

    /**
     * 券名，字数上限为 9 个汉字。
     * @param title
     */
    public void setTitle(String title)
    {
        m_data.put("title", title);
    }

    public String getTitle()
    {
        return m_data.getString("title");
    }

    /**
     * 券名的副标题， 字数上限为 18 个汉字。
     * @param subTitle
     */
    public void setSubTitle(String subTitle)
    {
        m_data.put("sub_title", subTitle);
    }

    public String getSubTitle()
    {
        return m_data.getString("sub_title");
    }

    /**
     * 固定日期区间内使用有效，就是有效期
     * @param beginTime 表示起用时间
     * @param endTime 表示结束时间
     */
    public void setDateInfoTimeRange(Date beginTime, Date endTime)
    {
        setDateInfoTimeRange(beginTime.getTime() / 1000, endTime.getTime() / 1000);
    }

    public void setDateInfoTimeRange(long beginTimestamp, long endTimestamp)
    {
        getDateInfo().put("type", 1);
        getDateInfo().put("begin_timestamp", beginTimestamp);
        getDateInfo().put("end_timestamp", endTimestamp);
    }

    /**
     * 固定时长（自领取后按天算）使用有效
     * @param fixedTerm 表示自领取后多少天内有效
     */
    public void setDateInfoFixTerm(int fixedTerm)
    {
        setDateInfoFixTerm(fixedTerm, 0);
    }

    /**
     * 固定时长（自领取后按天算）使用有效
     * @param fixedTerm 表示自领取后多少天内有效
     * @param fixedBeginTerm 表示自领取后多少天开始生效
     */
    public void setDateInfoFixTerm(int fixedTerm, int fixedBeginTerm) //fixedBeginTerm是领取后多少天开始生效
    {
        getDateInfo().put("type", 2);
        getDateInfo().put("fixed_term", fixedTerm);
        getDateInfo().put("fixed_begin_term", fixedBeginTerm);
    }

    public JSONObject getDateInfo()
    {
        return m_data.getJSONObject("date_info");
    }

    /**
     * 券颜色。按色彩规范标注填写Color010-Color100
     * @param color
     */
    public void setColor(String color)
    {
        m_data.put("color", color);
    }

    public String getColor()
    {
        return m_data.getString("color");
    }

    /**
     * 使用提醒，字数上限为 12 个汉字。
     * （一句话描述，展示在首页，示例：请出示二维码核销卡券）
     * @param notice
     */
    public void setNotice(String notice)
    {
        m_data.put("notice", notice);
    }

    public String getNotice()
    {
        return m_data.getString("notice");
    }
    
    /**
     * 客服电话。
     * @param phone
     */
    public void setServicePhone(String phone)
    {
        m_data.put("service_phone", phone);
    }

    public String getServicePhone()
    {
        return m_data.getString("service_phone");
    }

    /**
     * 使用说明。长文本描述，可以分行，上限为 1000 个汉字。
     * @param desc
     */
    public void setDescription(String desc)
    {
        m_data.put("description", desc);
    }

    public String getDescription()
    {
        return m_data.getString("description");
    }

    public void setLocationIdList(Collection<Integer> value)
    {
        JSONArray array = new JSONArray();
        array.addAll(value);
        m_data.put("location_id_list", array);
    }
    
    public void addLocationIdList(int locationId)
    {
        getLocationIdList().add(locationId);
    }
    
    public JSONArray getLocationIdList()
    {
        return m_data.getJSONArray("location_id_list");
    }

    public void setUseLimit(int limit)
    {
        m_data.put("use_limit", limit);
    }

    public int getUseLimit()
    {
        return m_data.getInteger("useLimit");
    }

    public void setGetLimit(int limit)
    {
        m_data.put("get_limit", limit);
    }

    public int getGetLimit()
    {
        return m_data.getInteger("get_limit");
    }

    public void setCanShare(boolean canShare)
    {
        m_data.put("can_share", canShare);
    }

    public boolean getCanShare()
    {
        return m_data.getBoolean("can_share");
    }

    public void setCanGiveFriend(boolean canGive)
    {
        m_data.put("can_give_friend", canGive);
    }

    public boolean getCanGiveFriend()
    {
        return m_data.getBoolean("can_give_friend");
    }

    /**
     * 是否自定义 code 码。
     * @param isUse
     */
    public void setUseCustomCode(boolean isUse)
    {
        m_data.put("use_custom_code", isUse);
    }

    public boolean getUseCustomCode()
    {
        return m_data.getBoolean("use_custom_code");
    }

    /**
     * 是否指定用户领取
     * @param isBind
     */
    public void setBindOpenid(boolean isBind)
    {
        m_data.put("bind_openid", isBind);
    }

    public boolean getBindOpenid()
    {
        return m_data.getBoolean("bind_openid");
    }
    
    public void setQuantity(int quantity)
    {
        m_data.getJSONObject("sku").put("quantity", quantity);
    }
   
    public int getQuantity()
    {
        return m_data.getJSONObject("sku").getInteger("quantity");
    }
}
