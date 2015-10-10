package cn.com.kehwa.weixin.message.menu;

import java.util.List;

/**
 * 复合类型的按钮
 * 
 * 
 * 
 */
public class ComplexButton extends Button {
	private List<Button> sub_button;

	public List<Button> getSub_button() {
		return sub_button;
	}

	public void setSub_button(List<Button> sub_button) {
		this.sub_button = sub_button;
	}
	
}
