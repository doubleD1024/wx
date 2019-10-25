package com.syc.wx.utils;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lidd
 * @date 2019-10-24 16:50
 */
public class RobotResultUtil {

    private static final Logger logger = LoggerFactory.getLogger(RobotResultUtil.class);

    private RobotResultUtil(){}

    public static final Map<String, String> resultMap = new HashMap<>(32);

    static {
        resultMap.put("number2", "签号：");
        resultMap.put("haohua", "吉凶：");
        resultMap.put("qianyu", "签语：");
        resultMap.put("shiyi", "诗意：");
        resultMap.put("jieqian", "解签：");
        resultMap.put("zhushi", "注释：");
        resultMap.put("baihua", "解签：");
        resultMap.put("jieshuo", "解说：");
        resultMap.put("jieguo", "结果：");
        resultMap.put("hunyin", "婚姻：");
        resultMap.put("shiye", "事业：");
        resultMap.put("gongming", "功名：");
        resultMap.put("shiwu", "失物：");
        resultMap.put("cwyj", "出外移居：");
        resultMap.put("liujia", "六甲：");
        resultMap.put("qiucai", "求财：");
        resultMap.put("jiaoyi", "交易：");
        resultMap.put("jibin", "疾病：");
        resultMap.put("susong", "诉讼：");
        resultMap.put("yuntu", "运途：");
        resultMap.put("moushi", "某事：");
        resultMap.put("hhzsy", "合伙做生意：");
    }

    public static boolean isJson(String content) {
        try {
            JSONObject.fromObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Unicode转中文
     * @param unicode unicode
     * @return 中文
     */
    public static String decodeUnicode(final String unicode) {
        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 0; i < hex.length; i++) {

            try {
                // 汉字范围 \u4e00-\u9fa5 (中文)
                if(hex[i].length()>=4){//取前四个，判断是否是汉字
                    String chinese = hex[i].substring(0, 4);
                    try {
                        int chr = Integer.parseInt(chinese, 16);
                        boolean isChinese = isChinese((char) chr);
                        //转化成功，判断是否在  汉字范围内
                        if (isChinese){//在汉字范围内
                            // 追加成string
                            string.append((char) chr);
                            //并且追加  后面的字符
                            String behindString = hex[i].substring(4);
                            string.append(behindString);
                        }else {
                            string.append(hex[i]);
                        }
                    } catch (NumberFormatException e1) {
                        string.append(hex[i]);
                    }

                }else{
                    string.append(hex[i]);
                }
            } catch (NumberFormatException e) {
                string.append(hex[i]);
            }
        }

        return string.toString();
    }

    /**
     * 判断是否为中文字符
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

}
