package com.syc.wx.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lidd
 */
public interface MessageService {
    String newMessageRequest(HttpServletRequest request);

    void sendMessageToUser(String jsonString);
}
