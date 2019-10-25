package com.syc.wx.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO
 *
 * @author lidd
 * @date 2019-10-23 15:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaiseInfo {

    private Double money;

    private String name;

    private String phoneNumber;

    private String treatment;

}
