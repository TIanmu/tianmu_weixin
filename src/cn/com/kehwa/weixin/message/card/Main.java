package cn.com.kehwa.weixin.message.card;

import java.util.ArrayList;
import java.util.Calendar;


/**
 *
 * @author shituo
 */
public class Main {
    public static void main(String[] args) {
        WxCardGroupon card = new WxCardGroupon();
        WxCardBaseInfo baseInfo = card.getBaseInfo();
        baseInfo.setLogoUrl("123");
        baseInfo.setDateInfoTimeRange(Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
        baseInfo.setBrandName("brandname");
        baseInfo.setBindOpenid(false);
        baseInfo.setCanGiveFriend(false);
        baseInfo.setCanShare(true);
        baseInfo.setCodeType(WxCardBaseInfo.CodeType.CODE_TYPE_QRCODE);
        baseInfo.setColor("Color010");
        baseInfo.setDescription("desc");
        baseInfo.setGetLimit(3);
        baseInfo.setUseCustomCode(false);
        baseInfo.setNotice("notice");
        baseInfo.setServicePhone("phone");
        baseInfo.addLocationIdList(123123);
        baseInfo.addLocationIdList(5345);
        baseInfo.setUseLimit(5);
        baseInfo.setQuantity(10000000);
        
        System.out.println(baseInfo.toJsonString());
        baseInfo.setLogoUrl("435345");
        ArrayList<Integer> locationIdList = new ArrayList<Integer>();
        locationIdList.add(809809);
        locationIdList.add(423532);
        card.setDealDetail("团购详情啊啊啊啊啊！！！");
        
        System.out.println(locationIdList.getClass().isArray());
        baseInfo.setLocationIdList(locationIdList);
        System.out.println(card.toJsonString());
    }
}
