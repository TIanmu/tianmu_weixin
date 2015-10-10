package cn.com.kehwa.weixin.message.card;
/**
 * 折扣券
 * @author shituo
 */
public class WxCardDiscount extends WxCard
{

    public WxCardDiscount()
    {
        init("GROUPON");
    }
    
    /**
     * 折扣券专用，表示打折额度（百分比）。填 30 就是七折。
     * @param detail
     */
    public void setDiscount(String discount)
    {
        m_data.put("discount", discount);
    }
    
}
