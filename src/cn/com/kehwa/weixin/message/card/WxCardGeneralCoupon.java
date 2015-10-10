package cn.com.kehwa.weixin.message.card;
/**
 * 通用券
 * @author shituo
 */
public class WxCardGeneralCoupon extends WxCard
{
    public WxCardGeneralCoupon()
    {
        init("GENERAL_COUPON");
    }
    
    /**
     * 描述文本
     * @param detail
     */
    public void setDefaultDetail(String detail)
    {
        m_data.put("default_detail", detail);
    }
    
    public String getDefaultDetail()
    {
        return m_data.getString("default_detail");
    }
}
