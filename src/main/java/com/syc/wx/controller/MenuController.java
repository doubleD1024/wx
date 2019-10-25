package com.syc.wx.controller;

import com.syc.wx.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author lidd
 * @date 2019-10-22 14:24
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    private MenuService menuService;

    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public void createMenu(){
        menuService.createMenu();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void deleteMenu(){
        menuService.deleteMenu();
    }
}
