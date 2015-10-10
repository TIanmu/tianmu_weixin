package cn.com.kehwa.weixin.message.card;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author shituo
 */
public abstract class WxCard
{

    protected WxCardBaseInfo m_baseInfo;
    protected JSONObject m_requestData;
    protected JSONObject m_data;
    protected String m_cardType;

    public WxCard()
    {
        m_baseInfo = new WxCardBaseInfo();
        m_requestData = new JSONObject();
    }
    
    void init(String cardType)
    {
        m_cardType = cardType;
        JSONObject obj = new JSONObject();
        obj.put("card_type", m_cardType.toUpperCase());
        m_data = new JSONObject();
        m_data.put("base_info", m_baseInfo.m_data);
        obj.put(m_cardType.toLowerCase(), m_data);
        m_requestData.put("card", obj);
    }
    
    public JSONObject getJSONObject()
    {
        return m_requestData;
    }

    public String toJsonString()
    {
        return m_requestData.toString();
    }
    
    public String toString()
    {
        return toJsonString();
    }

    public WxCardBaseInfo getBaseInfo()
    {
        return m_baseInfo;
    }
    
    /**
     * 卡券类型
     */
	public enum CardType {
		GENERAL_COUPON("通用券"),
		GROUPON("团购券"),
		DISCOUNT("折扣券"),
		GIFT("礼品券"),
		CASH("代金券"),
		MEMBER_CARD("会员卡"),
		SCENIC_TICKET("景点门票"),
		MOVIE_TICKET("电影票"),
		BOARDING_PASS("飞机票"),
		LUCKY_MONEY("红包"),
		MEETING_TICKET("会议门票");
        
        private String value;
        
        private CardType(String value){
        	this.value = value;
        }

		public String getValue() {
			return value;
		}
	}
}
