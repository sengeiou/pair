package io.rong.methods.user;

import io.rong.RongCloud;
import io.rong.methods.user.blacklist.Blacklist;
import io.rong.methods.user.block.Block;
import io.rong.methods.user.mute.MuteChatrooms;
import io.rong.methods.user.mute.MuteGroups;
import io.rong.methods.user.onlinestatus.OnlineStatus;
import io.rong.methods.user.onlinestatus.UserInfo;
import io.rong.models.*;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import io.rong.methods.user.tag.Tag;
import io.rong.util.*;

import java.net.HttpURLConnection;
import java.net.URLEncoder;


/**
 * 用户服务
 * docs : http://www.rongcloud.cn/docs/server.html#user
 **/
public class User {

    private static final String UTF8 = "UTF-8";
    private static final String PATH = "user";
    private String appKey;
    private String appSecret;
    public Block block;
    public Blacklist blackList;
    public OnlineStatus onlineStatus;
    public UserInfo userInfo;
    public Tag tag;
    public MuteChatrooms muteChatrooms;
    public MuteGroups muteGroups;
    private RongCloud rongCloud;

    public RongCloud getRongCloud() {
        return rongCloud;
    }

    public void setRongCloud(RongCloud rongCloud) {
        this.rongCloud = rongCloud;
    }

    public User(String appKey, String appSecret, RongCloud rongCloud) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.rongCloud = rongCloud;
        this.block = new Block(appKey, appSecret, rongCloud);
        this.blackList = new Blacklist(appKey, appSecret, rongCloud);
        this.onlineStatus = new OnlineStatus(appKey, appSecret, rongCloud);
        this.userInfo = new UserInfo(appKey, appSecret, rongCloud);
        this.muteChatrooms = new MuteChatrooms(appKey, appSecret, rongCloud);
        this.muteGroups = new MuteGroups(appKey, appSecret, rongCloud);
        this.tag = new Tag(appKey, appSecret, rongCloud);
    }

    /**
     * 获取 Token 方法
     * url  "/user/getToken"
     * docs "http://rongcloud.cn/docs/server.html#getToken"
     *
     * @param user 用户信息 id,name,portrait(必传)
     * @return TokenResult
     **/
    public TokenResult register(UserModel user) throws Exception {
        //需要校验的字段
        String message = CommonUtil.checkFiled(user, PATH, CheckMethod.REGISTER);
        if (null != message) {
            return (TokenResult) GsonUtil.fromJson(message, TokenResult.class);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("&userId=").append(URLEncoder.encode(user.id, UTF8));
        sb.append("&name=").append(URLEncoder.encode(user.name, UTF8));
        sb.append("&portraitUri=").append(URLEncoder.encode(user.portrait, UTF8));
        String body = sb.toString();
        if (body.indexOf("&") == 0) {
            body = body.substring(1, body.length());
        }

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(rongCloud.getApiHostType(), appKey, appSecret, "/user/getToken.json", "application/x-www-form-urlencoded");
        HttpUtil.setBodyParameter(body, conn);

        return (TokenResult) GsonUtil.fromJson(CommonUtil.getResponseByCode(PATH, CheckMethod.REGISTER, HttpUtil.returnResult(conn)), TokenResult.class);
    }

    /**
     * 刷新用户信息方法
     * url  "/user/refresh"
     * docs "http://www.rongcloud.cn/docs/server.html#user_refresh"
     *
     * @param user 用户信息 id name portrait(必传)
     * @return ResponseResult
     **/
    public Result update(UserModel user) throws Exception {
        //需要校验的字段
        String message = CommonUtil.checkFiled(user, PATH, CheckMethod.UPDATE);
        if (null != message) {
            return (ResponseResult) GsonUtil.fromJson(message, ResponseResult.class);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("&userId=").append(URLEncoder.encode(user.id.toString(), UTF8));

        if (user.name != null) {
            sb.append("&name=").append(URLEncoder.encode(user.name.toString(), UTF8));
        }

        if (user.portrait != null) {
            sb.append("&portraitUri=").append(URLEncoder.encode(user.portrait.toString(), UTF8));
        }
        String body = sb.toString();
        if (body.indexOf("&") == 0) {
            body = body.substring(1, body.length());
        }

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(rongCloud.getApiHostType(), appKey, appSecret,
                "/user/refresh.json", "application/x-www-form-urlencoded");
        HttpUtil.setBodyParameter(body, conn);

        return (ResponseResult) GsonUtil.fromJson(CommonUtil.getResponseByCode(PATH, CheckMethod.UPDATE, HttpUtil.returnResult(conn)), ResponseResult.class);
    }


}