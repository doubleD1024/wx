package com.syc.wx.utils;

import com.syc.wx.constant.AccessToken;
import com.syc.wx.menu.Menu;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;

/**
 * @author lidd
 */
public class CommonWechatUtil {
    private static Logger log = LoggerFactory.getLogger(CommonWechatUtil.class);

    private static final AccessToken ACCESS_TOKEN = new AccessToken();

    private CommonWechatUtil(){}

    /**
     * 凭证获取（GET）
     */
    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * 菜单创建（POST） 限100（次/天）
     */
    private static final String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

    private static final String MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";

    private static final String MENU_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";

    /**
     * 推送用户消息
     */
    private static final String MESSAGE_CUSTOM_SEND = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
    /**
     * 发送https请求
     *
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    private static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();
            jsonObject = JSONObject.fromObject(buffer.toString());
        } catch (ConnectException ce) {
            log.error("连接超时：{}", ce);
        } catch (Exception e) {
            log.error("https请求异常：{}", e);
        }
        return jsonObject;
    }
    /**
     * 获取接口访问凭证
     *
     * @param appid 凭证
     * @param appsecret 密钥
     */
    public static AccessToken getToken(String appid, String appsecret) {
        String requestUrl = TOKEN_URL.replace("APPID", appid).replace("APPSECRET", appsecret);
        // 发起GET请求获取凭证
        JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
        if (null != jsonObject) {
            try {

                long currentTime = System.currentTimeMillis();
                long expiresIn = jsonObject.getLong("expires_in");
                if (currentTime > ACCESS_TOKEN.getExpiresIn()) {
                    ACCESS_TOKEN.setAccessToken(jsonObject.getString("access_token"));
                    ACCESS_TOKEN.setExpiresIn(expiresIn * 1000 + currentTime);
                }
            } catch (JSONException e) {
                ACCESS_TOKEN.setExpiresIn(System.currentTimeMillis());
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            }
        }

        log.info("accessToken: {}", ACCESS_TOKEN);

        return ACCESS_TOKEN;
    }
    /**
     * URL编码（utf-8）
     */
    public static String urlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 根据内容类型判断文件扩展名
     *
     * @param contentType 内容类型
     */
    public static String getFileExt(String contentType) {
        String fileExt = "";
        if ("image/jpeg".equals(contentType)) {
            fileExt = ".jpg";
        }
        else if ("audio/mpeg".equals(contentType)){
            fileExt = ".mp3";
        }
        else if ("audio/amr".equals(contentType)){
            fileExt = ".amr";
        }
        else if ("video/mp4".equals(contentType)){
            fileExt = ".mp4";
        }
        else if ("video/mpeg4".equals(contentType)){
            fileExt = ".mp4";
        }

        return fileExt;
    }


    /**
     * 创建菜单
     *
     * @param menu 菜单实例
     * @param accessToken 有效的access_token
     * @return 0表示成功，其他值表示失败
     */
    public static int createMenu(Menu menu, String accessToken) {
        int result = 0;

        // 拼装创建菜单的url
        String url = MENU_CREATE_URL.replace("ACCESS_TOKEN", accessToken);
        // 将菜单对象转换成json字符串
        String jsonMenu = JSONObject.fromObject(menu).toString();
        // 调用接口创建菜单
        JSONObject jsonObject = httpsRequest(url, "POST", jsonMenu);

        if (null != jsonObject) {
            if (0 != jsonObject.getInt("errcode")) {
                result = jsonObject.getInt("errcode");
                log.error("创建菜单失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            }
        }

        return result;
    }

    /**
     * 查询菜单
     *
     * @param accessToken 有效的access_token
     * @return 0表示成功，其他值表示失败
     */
    public static JSONObject getMenu(String accessToken) {
        int result = 0;

        // 拼装创建菜单的url
        String url = MENU_GET_URL.replace("ACCESS_TOKEN", accessToken);
        // 将菜单对象转换成json字符串
//        String jsonMenu = JSONObject.fromObject(menu).toString();
        // 调用接口创建菜单
        JSONObject jsonObject = httpsRequest(url, "POST", null);

        return jsonObject;
    }

    /**
     * 查询菜单
     *
     * @param accessToken 有效的access_token
     * @return 0表示成功，其他值表示失败
     */
    public static int deleteMenu(String accessToken) {
        int result = 0;

        // 拼装创建菜单的url
        String url = MENU_DELETE_URL.replace("ACCESS_TOKEN", accessToken);
        // 调用接口创建菜单
        JSONObject jsonObject = httpsRequest(url, "POST", null);

        if (null != jsonObject) {
            if (0 != jsonObject.getInt("errcode")) {
                result = jsonObject.getInt("errcode");
                log.error("删除菜单失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            }
        }
        return result;
    }


    public static void sendMessage(String accessToken, String jsonString) {
        // 拼装创建菜单的url
        String url = MESSAGE_CUSTOM_SEND.replace("ACCESS_TOKEN", accessToken);

        JSONObject jsonObject = httpsRequest(url, "POST", jsonString);

        if (null != jsonObject) {
            log.info("response: {}", jsonObject);
        }

    }

}


