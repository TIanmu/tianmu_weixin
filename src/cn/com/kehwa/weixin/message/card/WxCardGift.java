package cn.com.kehwa.weixin.message.card;
/**
 * 礼品券
 * @author shituo
 */
public class WxCardGift extends WxCard
{

    public WxCardGift()
    {
        init("GIFT");
    }
    
    /**
     * 礼品券专用，表示礼品名字。
     * @param detail
     */
    public void setGift(String gift)
    {
        m_data.put("gift", gift);
    }
    
}
