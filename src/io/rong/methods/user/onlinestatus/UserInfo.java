package io.rong.methods.user.onlinestatus;

import java.net.HttpURLConnection;
import java.net.URLEncoder;

import io.rong.RongCloud;
import io.rong.models.CheckMethod;
import io.rong.models.response.UserInfoResult;
import io.rong.models.user.UserModel;
import io.rong.util.CommonUtil;
import io.rong.util.GsonUtil;
import io.rong.util.HttpUtil;

public class UserInfo
{
	private static final String UTF8 = "UTF-8";
	private static final String PATH = "user/online-status";
	private String appKey;
	private String appSecret;
	private RongCloud rongCloud;

	public RongCloud getRongCloud()
	{
		return rongCloud;
	}

	public void setRongCloud(RongCloud rongCloud)
	{
		this.rongCloud = rongCloud;
	}

	public UserInfo(String appKey, String appSecret, RongCloud rongCloud)
	{
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.rongCloud = rongCloud;
	}

	public UserInfoResult getInfo(UserModel user) throws Exception
	{
		// 参数校验
		String message = CommonUtil.checkFiled(user, PATH, CheckMethod.CHECK);
		if (null != message)
		{
			return (UserInfoResult) GsonUtil.fromJson(message, UserInfoResult.class);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("&userId=").append(URLEncoder.encode(user.id.toString(), UTF8));
		String body = sb.toString();
		if (body.indexOf("&") == 0)
		{
			body = body.substring(1, body.length());
		}

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(rongCloud.getApiHostType(), appKey, appSecret, "/user/info.json", "application/x-www-form-urlencoded");
		HttpUtil.setBodyParameter(body, conn);

		return (UserInfoResult) GsonUtil.fromJson(CommonUtil.getResponseByCode(PATH, CheckMethod.CHECK, HttpUtil.returnResult(conn)), UserInfoResult.class);
	}
}
