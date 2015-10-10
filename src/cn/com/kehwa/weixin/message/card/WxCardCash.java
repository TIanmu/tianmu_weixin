package cn.com.kehwa.weixin.message.card;
/**
 * 代金券
 * @author shituo
 */
public class WxCardCash extends WxCard
{

    public WxCardCash()
    {
        init("GROUPON");
    }
    
    /**
     * 代金券专用，表示起用金额（单位为分） 非必填
     * @param leastCost
     */
    public void setLeastCost(String leastCost)
    {
        m_data.put("least_cost", leastCost);
    }
    
    /**
     * 代金券专用，表示减免金额（单位为分）
     * @param reduceCost
     */
    public void setReduceCost(String reduceCost)
    {
        m_data.put("reduce_cost", reduceCost);
    }
}
