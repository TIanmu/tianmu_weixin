package cn.com.kehwa.weixin.test;

import java.util.ArrayList;
import java.util.List;

import cn.com.kehwa.weixin.WeChat;
import cn.com.kehwa.weixin.message.bean.Articles;
import cn.com.kehwa.weixin.util.WeixinKitFactory;

public class MessageTest {
	public static void main(String[] args) {
		new WeixinKitFactory().setWeixinKit(new WeixinKitImpl());
		List<cn.com.kehwa.weixin.message.bean.Articles> articlesList = new ArrayList<>();
		Articles articles = new Articles();
		articles.setDescription("Description");
		articles.setUrl("urlurlurlurl");
		articles.setTitle("Title");
		articles.setPicurl("Picurl");
		articlesList.add(articles);
		try {
			String result = WeChat.message.sendNews("oHP7ujufcbsgICCaQddGBAq8Q53k", articlesList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
