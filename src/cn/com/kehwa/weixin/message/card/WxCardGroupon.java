package cn.com.kehwa.weixin.message.card;
/**
 * 团购券
 * @author shituo
 */
public class WxCardGroupon extends WxCard
{

    public WxCardGroupon()
    {
        init("GROUPON");
    }
    
    /**
     * 团购券专用，团购详情。
     * @param detail
     */
    public void setDealDetail(String detail)
    {
        m_data.put("deal_detail", detail);
    }
    
}
