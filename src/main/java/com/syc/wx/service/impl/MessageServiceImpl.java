package com.syc.wx.service.impl;

import com.syc.wx.constant.AccessToken;
import com.syc.wx.constant.ConstantWeChat;
import com.syc.wx.message.Article;
import com.syc.wx.message.NewsMessage;
import com.syc.wx.message.TextMessage;
import com.syc.wx.service.MessageService;
import com.syc.wx.utils.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lidd
 */
@Service("messageService")
public class MessageServiceImpl implements MessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);
    @Override
    public String newMessageRequest(HttpServletRequest request) {
        String respMessage = null;
        try {
            // xml请求解析
            Map<String, String> requestMap = MessageUtil.xmlToMap(request);
            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            // 消息内容
            String content = requestMap.get("Content");
            LOGGER.info("FromUserName is:{}, ToUserName is:{}, MsgType is:{}", fromUserName, toUserName, msgType);
            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                //智能机器人自动回复
                TextMessage text = new TextMessage();

                String jiqirenAnswer = HttpClientUtils.sendGetUtF8("http://i.itpk.cn/api.php",
                        "limit=3&api_key=5da0a8ba0ccc5f9d7293af5200586e8b&api_secret=9sggnt7m1ekr&type=json&question="
                                + content);

                LOGGER.info("jiqirenAnswer: {}", jiqirenAnswer);
                if (RobotResultUtil.isJson(jiqirenAnswer)) {
                    JSONObject jsonObject = JSONObject.fromObject(jiqirenAnswer);
                    if (jsonObject.get("type") == null && jsonObject.get("title") != null ){
                        jiqirenAnswer = jsonObject.getString("title") + "\n" + jsonObject.getString("content");
                    } else {
                        StringBuilder stringBuilder = new StringBuilder("");
                        for (Object objectKey: jsonObject.keySet()) {
                            String key = (String) objectKey;

                            if (RobotResultUtil.resultMap.containsKey(key)) {
                                String value = jsonObject.getString(key);
                                if (!"".equals(value)) {
                                    stringBuilder.append(RobotResultUtil.resultMap.get(key) + value + "\n");
                                }
                            }
                        }

                        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() -1);
                        LOGGER.info("json string：{}", stringBuilder.toString());
                        jiqirenAnswer = stringBuilder.toString();
                    }
                }

                text.setContent(jiqirenAnswer);
                text.setToUserName(fromUserName);
                text.setFromUserName(toUserName);
                text.setCreateTime(System.currentTimeMillis());
                text.setMsgType(msgType);
                respMessage = MessageUtil.textMessageToXml(text);
            }
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    //文本消息
                    TextMessage text = new TextMessage();
                    text.setContent("欢迎关注福州善缘网络科技有限公司! \n" +
                                    "具体详情请添加我们客服小姐姐，微信号：shanyuan1688。或者直接拨打我们客服电话：059187245287");
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(System.currentTimeMillis());
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    respMessage = MessageUtil.textMessageToXml(text);

                    //对图文消息
                    /*NewsMessage newmsg=new NewsMessage();
                    newmsg.setToUserName(fromUserName);
                    newmsg.setFromUserName(toUserName);
                    newmsg.setCreateTime(System.currentTimeMillis());
                    newmsg.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
                    newmsg.setFuncFlag(0);
                    List<Article> articleList = new ArrayList<>();

                    Article article = new Article();
                    article.setTitle("我是一个图文");
                    article.setDescription("我是描述信息");
                    article.setPicUrl("https://sfault-avatar.b0.upaiyun.com/110/007/1100070317-5abcb09d42224_huge256");
                    article.setUrl("https://segmentfault.com/u/panzi_5abcaf30a5e6b");
                    articleList.add(article);
                    // 设置图文消息个数
                    newmsg.setArticleCount(articleList.size());
                    // 设置图文消息包含的图文集合
                    newmsg.setArticles(articleList);
                    // 将图文消息对象转换成xml字符串
                    respMessage = MessageUtil.newsMessageToXml(newmsg);*/

                }
                /*// TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    // 取消订阅

                }*/
                // 自定义菜单点击事件
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    // 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    String eventKey = requestMap.get("EventKey");
                    if ("return_content".equals(eventKey)) {
                        TextMessage text = new TextMessage();
                        text.setContent("一．善缘筹募捐发起流程及材料         \n" +
                                "\n" +
                                "1. 大病治疗康复募捐。\n" +
                                "\n" +
                                "1,材料证明。（医院疾病证明，治疗流程 治疗发票及当事人家中财力证明）\n" +
                                "\n" +
                                "2.发起众筹申请步骤（1.联系善缘筹款官方公总号（福州善缘网络科技有限公司）或电联0591 87245287大病众筹志愿者专线。）\n" +
                                "\n" +
                                "3.善缘筹款大病众筹，资金用途只可用于治疗康复。善缘筹款没有任何的服务费用！任何一笔资金出入账户直接从善缘筹公账户转账至当事人账户.（资金用途）\n" +
                                "\n" +
                                "4，筹款要求（当事人无力承担治疗费用当地医院出示疾病证明均可发起筹款）\n" +
                                "\n" +
                                "二．助学众筹。\n" +
                                "\n" +
                                "  1.众筹者需要当地村委盖章证明，以及录取学校证明，助学众筹资金直接打入录取学校账户\n" +
                                "\n" +
                                "  2，募捐资金以学校学费和当地生活费用综合发起。募捐资金除去学费打入录取学校账户生活费用一次性打入学生个人账户。（助学募捐资金以账户募捐资金实际为准）\n" +
                                "\n" +
                                "三．善缘筹，专注以大病治疗和助学帮扶。所有筹款资金均用于当事人。\n" +
                                "\n" +
                                "四．发起募捐通道059187245287。");
                        text.setToUserName(fromUserName);
                        text.setFromUserName(toUserName);
                        text.setCreateTime(System.currentTimeMillis());
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        respMessage = MessageUtil.textMessageToXml(text);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("error......", e);
        }
        return respMessage;
    }


    @Override
    public void sendMessageToUser(String jsonString) {

        // 第三方用户唯一凭证
        String appId = ConstantWeChat.APPID;
        // 第三方用户唯一凭证密钥
        String appSecret = ConstantWeChat.APPSECRET;
        AccessToken accessToken = CommonWechatUtil.getToken(appId, appSecret);
        if (accessToken.getAccessToken() != null) {
            CommonWechatUtil.sendMessage(accessToken.getAccessToken(), jsonString);
        }
    }

}
