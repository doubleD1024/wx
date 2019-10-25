package com.syc.wx.service.impl;

import com.syc.wx.constant.AccessToken;
import com.syc.wx.constant.ConstantWeChat;
import com.syc.wx.menu.*;
import com.syc.wx.service.MenuService;
import com.syc.wx.utils.CommonWechatUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author lidd
 * @date 2019-10-22 14:15
 */
@Service("menuService")
public class MenuServiceImpl implements MenuService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuServiceImpl.class);

    @Override
    public Boolean createMenu() {
        // 第三方用户唯一凭证
        String appId = ConstantWeChat.APPID;
        // 第三方用户唯一凭证密钥
        String appSecret = ConstantWeChat.APPSECRET;
        // 调用接口获取access_token
        AccessToken at = CommonWechatUtil.getToken(appId, appSecret);
        if (null != at.getAccessToken()) {
            // 调用接口创建菜单
            int result = CommonWechatUtil.createMenu(getMenu(), at.getAccessToken());
            // 判断菜单创建结果
            if (0 == result){
                LOGGER.info("菜单创建成功！");
                return true;
            }
            else{
                LOGGER.info("菜单创建失败，错误码：{}", result);
                return false;
            }
        }
        return false;
    }

    @Override
    public JSONObject getMenuBtn() {
        // 第三方用户唯一凭证
        String appId = ConstantWeChat.APPID;
        // 第三方用户唯一凭证密钥
        String appSecret = ConstantWeChat.APPSECRET;
        // 调用接口获取access_token
        AccessToken at = CommonWechatUtil.getToken(appId, appSecret);
        if (null != at) {
            // 调用接口获取菜单
            JSONObject result = CommonWechatUtil.getMenu(at.getAccessToken());
            // 判断菜单创建结果
            if (null != result && result.size()>0){
                LOGGER.info("菜单查询成功！");
                return result;
            }
            else{
                LOGGER.info("菜单查询失败，错误码：{}", result);
                return null;
            }

        }
        return null;
    }

    @Override
    public Boolean deleteMenu() {
        // 第三方用户唯一凭证
        String appId = ConstantWeChat.APPID;
        // 第三方用户唯一凭证密钥
        String appSecret = ConstantWeChat.APPSECRET;
        // 调用接口获取access_token
        AccessToken at = CommonWechatUtil.getToken(appId, appSecret);
        if (null != at) {
            // 调用接口删除菜单
            int result = CommonWechatUtil.deleteMenu(at.getAccessToken());
            // 判断菜单删除结果
            if (0 == result){
                LOGGER.info("菜单删除成功！");
                return true;
            }
            else{
                LOGGER.info("菜单删除失败，错误码：{}", result);
                return false;
            }
        }
        return false;
    }

    /**
     * 组装菜单数据
     */
    private Menu getMenu() {

        ViewButton btn11 = new ViewButton();
        btn11.setName("首页");
        btn11.setType("view");
        btn11.setUrl("https://mp.weixin.qq.com/mp/homepage?__biz=Mzg4MDEzMzI5Nw==&hid=1&sn=7e670186b86aa561c97959ee3ddc7670");

        ViewButton btn41 = new ViewButton();
        btn41.setName("❤发起筹款");
        btn41.setType("view");
        btn41.setUrl("http://fzsykeji.com/wx/raise");

        CommonButton btn12 = new CommonButton();
        btn12.setName("了解我们");
        btn12.setType("click");
        btn12.setKey("return_content");

        /*CommonButton btn12 = new CommonButton();
        btn12.setName("赞");
        btn12.setType("click");
        btn12.setKey("return_content");

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName("自我介绍");
        mainBtn1.setSub_button(new BasicButton[] { btn11 });

        ComplexButton mainBtn2 = new ComplexButton();
        mainBtn2.setName("谢谢！");
        mainBtn2.setSub_button(new BasicButton[] { btn41, btn12 });*/

        /**
         *在某个一级菜单下没有二级菜单的情况，menu应该这样定义：
         * menu.setButton(new Button[] { mainBtn1, mainBtn2, btn33 });
         */
        Menu menu = new Menu();
        menu.setButton(new BasicButton[] { btn11, btn41, btn12});

        return menu;
    }

}
