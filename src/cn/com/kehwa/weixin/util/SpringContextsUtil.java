package cn.com.kehwa.weixin.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring组件，再没有注入的时候获取注入
 *
 */
public class SpringContextsUtil implements ApplicationContextAware {
	 
    private static ApplicationContext applicationContext;
 
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }
 
    public static <T> T getBean(String beanName, Class<T> clazs) {
        return clazs.cast(getBean(beanName));
    }
 
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        SpringContextsUtil.applicationContext = applicationContext;
    }
}
