package com.syc.wx.constant;

import lombok.Data;

/**
 * TODO
 *
 * @author lidd
 * @date 2019-10-22 9:27
 */
@Data
public class AccessToken {
    /**
     *  获取到的凭证
     */
    private String accessToken;

    /**
     *  凭证有效时间，单位：毫秒
     */
    private long expiresIn;
}
