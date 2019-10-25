package com.syc.wx.menu;

import lombok.Getter;
import lombok.Setter;

/**
 * 普通按钮
 *
 * @author lidd
 */
@Setter
@Getter
public class CommonButton extends BasicButton {
    private String type;

    private String key;
}
