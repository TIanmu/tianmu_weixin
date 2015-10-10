# tmWeixin
这是微信公众平台工具，里面包含了相关的接口，可以直接调用。

# 使用方法:

你首先需要实现WeixinKit接口，里面是所有微信常见的参数，可以选择不填写。然后根据你工程的需要，配置如下文件
spring：
  配置applicationContext，在里面加入
``` xml
// 配置weixin这个
<bean id="springContextsUtil" class="cn.com.kehwa.weixin.util.SpringContextsUtil"></bean>
// 必须生成这个或者手工set进去
<bean name="weixinKitFactory" class="cn.com.kehwa.weixin.util.WeixinKitFactory">
	<property name="weixinKit">
		<ref bean="weixinKitService" />// weixinKitService你的实现类
	</property>
</bean>
```

