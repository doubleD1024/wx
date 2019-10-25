package com.syc.wx.menu;

import lombok.Getter;
import lombok.Setter;

/**
 * 父按钮
 * @author lidd
 */
@Setter
@Getter
public class ComplexButton extends BasicButton {
    private BasicButton[] sub_button;

}
