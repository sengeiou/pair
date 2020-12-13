package com.lys.utils;

import com.lys.config.Config;
import com.lys.config.StaticConfig;

import io.rong.RongCloud;
import io.rong.messages.BaseMessage;
import io.rong.models.Result;
import io.rong.models.group.GroupMember;
import io.rong.models.group.GroupModel;
import io.rong.models.message.PrivateMessage;
import io.rong.models.message.SystemMessage;
import io.rong.models.response.CheckOnlineResult;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.response.UserInfoResult;
import io.rong.models.user.UserModel;

public class RongHelper
{
	public static final RongCloud rongCloud;

	static
	{
		if (Config.isLinux)
		{
			rongCloud = RongCloud.getInstance("e0x9wycfeb13q", "8fU1833MXaeA"); // 正式环境
		}
		else
		{
			rongCloud = RongCloud.getInstance("82hegw5u8xjpx", "qnyYdnh1CX75qp"); // 测试环境
		}
	}

	public static String getToken(String userId, String name, String head) throws Exception
	{
		UserModel user = new UserModel() //
				.setId(userId) //
				.setName(name) //
				.setPortrait(head);
		TokenResult result = rongCloud.user.register(user);
		if (result.getCode().equals(200))
			return result.getToken();
		return null;
	}

	public static boolean refreshUserInfo(String userId, String name, String head) throws Exception
	{
		UserModel user = new UserModel() //
				.setId(userId) //
				.setName(name) //
				.setPortrait(head);
		Result result = rongCloud.user.update(user);
		if (result.getCode().equals(200))
			return true;
		return false;
	}

	public static boolean testCheckOnline(String userId) throws Exception
	{
		UserModel user = new UserModel() //
				.setId(userId);
		CheckOnlineResult result = rongCloud.user.onlineStatus.check(user);
		LOG.v("result : " + result);
		if (result.getCode().equals(200))
			return result.getStatus().equals("1");
		return false;
	}

	public static UserInfoResult getUserInfo(String userId) throws Exception
	{
		UserModel user = new UserModel() //
				.setId(userId);
		UserInfoResult result = rongCloud.user.userInfo.getInfo(user);
		LOG.v(userId + " : " + result);
		if (result.getCode().equals(200))
			return result;
		return null;
	}

	public static void sendSystemMessage(BaseMessage message, String... targetIds) throws Exception
	{
		SystemMessage systemMessage = new SystemMessage() //
				.setSenderId("root") //
				.setTargetId(targetIds) //
				.setObjectName(message.getType()) //
				.setContent(message);
		ResponseResult result = rongCloud.message.system.send(systemMessage);
		if (!result.getCode().equals(200))
			LOG.v("发送系统消息失败：" + result);
	}

	public static void sendPrivateMessage(BaseMessage message, String senderId, String... targetIds) throws Exception
	{
		PrivateMessage privateMessage = new PrivateMessage() //
				.setSenderId(senderId) //
				.setTargetId(targetIds) //
				.setObjectName(message.getType()) //
				.setContent(message);
		ResponseResult result = rongCloud.message.msgPrivate.send(privateMessage);
		if (!result.getCode().equals(200))
			LOG.v("发送私聊消息失败：" + result);
	}

	public static void createGroup(String groupId, String groupName, String... memberIds) throws Exception
	{
		GroupMember[] members = new GroupMember[memberIds.length];
		for (int i = 0; i < members.length; i++)
		{
			members[i] = new GroupMember().setId(memberIds[i]);
		}
		GroupModel group = new GroupModel().setId(groupId).setName(groupName).setMembers(members);
		Result result = rongCloud.group.create(group);
		if (!result.getCode().equals(200))
			LOG.v("创建组失败：" + result);
		else
			LOG.v("创建组成功：" + result);
	}

	public static void main(String[] args)
	{
		StaticConfig.isTomcat = false;

		try
		{
//			boolean online = testCheckOnline("xxx");
//			LOG.v("online : " + online);
//			UserInfoResult userInfo = getUserInfo("mst1");
//			LOG.v("userInfo : " + userInfo);

			getUserInfo("root");
			getUserInfo("mst1");
			getUserInfo("mst2");
			getUserInfo("mst3");

			getUserInfo("47Q6R17306000310");
			getUserInfo("47Q6R16A22000317");
			getUserInfo("47Q6R17220002611");

			getUserInfo("47Q6R16B16001004");
			getUserInfo("47Q6R16B16001083");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		LOG.v("-------------------------------- over --------------------------------");
	}
}
