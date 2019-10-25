package com.syc.wx.controller;

import com.syc.wx.bean.RaiseInfo;
import com.syc.wx.service.MessageService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author lidd
 * @date 2019-10-23 11:52
 */
@Controller
@RequestMapping("/raise")
public class RaiseController {

    private static final Logger logger = LoggerFactory.getLogger(RaiseController.class);

    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ModelAndView getRaise() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("raise");
        modelAndView.addObject("sitrue", true);
        return modelAndView;
    }

    @PostMapping(path = "/submit")
    public ModelAndView raiseTreat(RaiseInfo raiseInfo) {

        boolean siture = true;

        if (raiseInfo.getName() == null || raiseInfo.getPhoneNumber() == null || raiseInfo.getMoney() == null || raiseInfo.getMoney() < 1000 || raiseInfo.getMoney() > 500000 ) {
            siture = false;
        }

        if (!siture) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("raise");
            modelAndView.addObject("sitrue", false);

            return modelAndView;
        }

        logger.info("raiseInfo: {}", raiseInfo);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", "osSJG5xYgA4nzl9aBiWqwyavf7pU");
        jsonObject.put("msgtype", "text");

        Map<String, Object> textMap = new HashMap<>(16);

        String content = "患者姓名：" + raiseInfo.getName() + "\n"
                + "筹款金额：" + raiseInfo.getMoney() + "\n"
                + "联系方式：" + raiseInfo.getPhoneNumber();

        if (raiseInfo.getTreatment() != null) {
            content = content + "\n所患疾病：" + raiseInfo.getTreatment();
        }

        textMap.put("content", content);
        jsonObject.put("text", textMap);

        messageService.sendMessageToUser(jsonObject.toString());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("raiseEnd");
        return modelAndView;
    }


    @PostMapping(path = "/send")
    @ResponseBody
    public void sendMessage(@RequestBody JSONObject request) {

        if (request == null || null == request.get("message")) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", "osSJG52du3S1z7-W7TEaW_phsdgU");
        jsonObject.put("msgtype", "text");

        Map<String, Object> textMap = new HashMap<>(16);
        textMap.put("content", request.getString("message"));
        jsonObject.put("text", textMap);

        messageService.sendMessageToUser(jsonObject.toString());

    }
}
