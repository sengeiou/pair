package com.lys.servlet.process;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.SErrorCode;
import com.lys.protobuf.SRequest_AddFriend;
import com.lys.protobuf.SRequest_DeleteFriend;
import com.lys.protobuf.SRequest_GetFriendList;
import com.lys.protobuf.SRequest_ModifyFriendGroup;
import com.lys.protobuf.SResponse_AddFriend;
import com.lys.protobuf.SResponse_DeleteFriend;
import com.lys.protobuf.SResponse_GetFriendList;
import com.lys.protobuf.SResponse_ModifyFriendGroup;
import com.lys.protobuf.SUser;

public class ProcessFriend extends BaseProcess
{
	public static void GetFriendList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetFriendList requestData = SRequest_GetFriendList.load(data);
		SResponse_GetFriendList responseData = new SResponse_GetFriendList();
		DBHelper.exeSql(String.format("select * from %s left join %s on %s.%s = %s.%s where %s = '%s';", //
				T.friend, //
				T.user, //
				T.friend, T.friend.friendId, //
				T.user, T.user.id, //
				T.friend.userId, //
				requestData.userId), //
				new OnCallback()
				{
					@Override
					public void onResult(ResultSet rs) throws SQLException
					{
						while (rs.next())
						{
							SUser user = packUser(rs);
							user.group = rs.getString(T.friend.group);
							responseData.friends.add(user);
						}
					}
				});

		if (requestData.checkOwnerId)
		{
			for (SUser user : responseData.friends)
			{
				ProcessUser.checkOwnerCp(user);
			}
		}

		success(response, responseData.saveToStr());
	}

	public static void addFriendImpl(String userId, String friendId) throws Exception
	{
		if (userId.equals(friendId))
			return;
		if (!DBHelper.hasRecord(T.friend, T.friend.userId, userId, T.friend.friendId, friendId))
		{
			DBHelper.insert(T.friend, //
					T.friend.userId, userId, //
					T.friend.friendId, friendId);
		}
	}

	public static void AddFriend(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_AddFriend requestData = SRequest_AddFriend.load(data);
		SResponse_AddFriend responseData = new SResponse_AddFriend();
		if (!DBHelper.hasRecord(T.user, T.user.id, requestData.userId))
		{
			error(response, SErrorCode.unknown_error, "用户<" + requestData.userId + ">不存在");
		}
		else if (!DBHelper.hasRecord(T.user, T.user.id, requestData.friendId))
		{
			error(response, SErrorCode.unknown_error, "用户<" + requestData.friendId + ">不存在");
		}
		else
		{
			addFriendImpl(requestData.userId, requestData.friendId);
			addFriendImpl(requestData.friendId, requestData.userId);
			success(response, responseData.saveToStr());
		}
	}

	private static void deleteFriendImpl(String userId, String friendId) throws Exception
	{
		if (DBHelper.hasRecord(T.friend, T.friend.userId, userId, T.friend.friendId, friendId))
		{
			DBHelper.delete(T.friend, //
					T.friend.userId, userId, //
					T.friend.friendId, friendId);
		}
	}

	public static void DeleteFriend(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_DeleteFriend requestData = SRequest_DeleteFriend.load(data);
		SResponse_DeleteFriend responseData = new SResponse_DeleteFriend();
		deleteFriendImpl(requestData.userId, requestData.friendId);
		deleteFriendImpl(requestData.friendId, requestData.userId);
		success(response, responseData.saveToStr());
	}

	public static void ModifyFriendGroup(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ModifyFriendGroup requestData = SRequest_ModifyFriendGroup.load(data);
		SResponse_ModifyFriendGroup responseData = new SResponse_ModifyFriendGroup();
		if (DBHelper.update(T.friend, DBHelper.set(T.friend.group, requestData.group), T.friend.userId, requestData.userId, T.friend.friendId, requestData.friendId))
			success(response, responseData.saveToStr());
		else
			error(response, SErrorCode.unknown_error, "设置失败，请检查是否有特殊字符");
	}
}
