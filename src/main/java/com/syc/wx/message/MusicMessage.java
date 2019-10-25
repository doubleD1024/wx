package com.syc.wx.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lidd
 */
@Setter
@Getter
public class MusicMessage extends BaseMessage {
    /**
     * 音乐
     */
    private Music Music;

}
