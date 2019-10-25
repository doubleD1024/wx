package com.syc.wx.service;

import com.syc.wx.menu.Menu;
import net.sf.json.JSONObject;

/**
 * TODO
 *
 * @author lidd
 * @date 2019-10-22 14:15
 */
public interface MenuService {
    Boolean createMenu();

    JSONObject getMenuBtn();

    Boolean deleteMenu();

}
