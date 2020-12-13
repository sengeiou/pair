package com.lys.servlet.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.messages.TransMessage;
import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.SDevice;
import com.lys.protobuf.SErrorCode;
import com.lys.protobuf.SPhoneCodeType;
import com.lys.protobuf.SRequest_AddUser;
import com.lys.protobuf.SRequest_DeleteUser;
import com.lys.protobuf.SRequest_GetUser;
import com.lys.protobuf.SRequest_GetUserList;
import com.lys.protobuf.SRequest_ModifyGrade;
import com.lys.protobuf.SRequest_ModifyHead;
import com.lys.protobuf.SRequest_ModifyName;
import com.lys.protobuf.SRequest_ModifyPsw;
import com.lys.protobuf.SRequest_ModifySex;
import com.lys.protobuf.SRequest_SetCp;
import com.lys.protobuf.SRequest_SetVip;
import com.lys.protobuf.SRequest_UserLogin;
import com.lys.protobuf.SRequest_UserPhoneCode;
import com.lys.protobuf.SRequest_UserReg;
import com.lys.protobuf.SResponse_AddUser;
import com.lys.protobuf.SResponse_DeleteUser;
import com.lys.protobuf.SResponse_GetUser;
import com.lys.protobuf.SResponse_GetUserList;
import com.lys.protobuf.SResponse_ModifyGrade;
import com.lys.protobuf.SResponse_ModifyHead;
import com.lys.protobuf.SResponse_ModifyName;
import com.lys.protobuf.SResponse_ModifyPsw;
import com.lys.protobuf.SResponse_ModifySex;
import com.lys.protobuf.SResponse_SetCp;
import com.lys.protobuf.SResponse_SetVip;
import com.lys.protobuf.SResponse_UserLogin;
import com.lys.protobuf.SResponse_UserPhoneCode;
import com.lys.protobuf.SResponse_UserReg;
import com.lys.protobuf.SSex;
import com.lys.protobuf.SUser;
import com.lys.protobuf.SUserType;
import com.lys.utils.LOG;
import com.lys.utils.RandomHelper;
import com.lys.utils.RongHelper;
import com.lys.utils.SVNManager;
import com.lys.utils.SendSms;
import com.lys.utils.TextUtils;

public class ProcessUser extends BaseProcess
{
	public static SUser selectUser(String userIdOrPhone, boolean full) throws Exception
	{
		final List<SUser> users = new ArrayList<>();
		String sql = String.format("select * from %s where %s = '%s' or %s = '%s';", T.user, T.user.id, userIdOrPhone, T.user.phone, userIdOrPhone);
		DBHelper.exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				if (rs.next())
				{
					users.add(packUser(rs, full));
				}
			}
		});
		if (users.size() > 0)
			return users.get(0);
		else
			return null;
	}

	public static SUser selectUser(String userIdOrPhone) throws Exception
	{
		return selectUser(userIdOrPhone, false);
	}

	public static List<SUser> selectUserList(Integer userType) throws Exception
	{
		final List<SUser> users = new ArrayList<>();
		if (userType.equals(0))
		{
			DBHelper.select(T.user, new OnCallback()
			{
				@Override
				public void onResult(ResultSet rs) throws SQLException
				{
					while (rs.next())
					{
						users.add(packUser(rs));
					}
				}
			});
		}
		else
		{
			DBHelper.select(T.user, new OnCallback()
			{
				@Override
				public void onResult(ResultSet rs) throws SQLException
				{
					while (rs.next())
					{
						users.add(packUser(rs));
					}
				}
			}, T.user.userType, userType);
		}
		return users;
	}

	public static void recordDevice(SRequest_UserLogin requestData, String loginResult) throws Exception
	{
		final List<SDevice> devices = new ArrayList<>();
		DBHelper.select(T.device, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				if (rs.next())
				{
					SDevice device = new SDevice();
					device.deviceId = rs.getString(T.device.deviceId);
					device.loginCount = rs.getInt(T.device.loginCount);
					devices.add(device);
				}
			}
		}, T.device.deviceId, requestData.deviceId);
		if (devices.size() > 0)
		{
			SDevice device = devices.get(0);
			DBHelper.update(T.device, DBHelper.set(T.device.clientVersion, requestData.clientVersion, //
					T.device.userId, requestData.userId, //
					T.device.loginCount, device.loginCount + 1, //
					T.device.loginResult, loginResult), //
					T.device.deviceId, requestData.deviceId);
		}
		else
		{
			DBHelper.insert(T.device, //
					T.device.deviceId, requestData.deviceId, //
					T.device.clientVersion, requestData.clientVersion, //
					T.device.userId, requestData.userId, //
					T.device.loginCount, 1, //
					T.device.loginResult, loginResult);
		}
	}

	public static void checkOwnerCp(SUser user) throws Exception
	{
		if (user != null)
		{
			DBHelper.select(T.user, new OnCallback()
			{
				@Override
				public void onResult(ResultSet rs) throws SQLException
				{
					if (rs.next())
					{
						SUser owner = packUser(rs, false);
						user.ownerId = owner.id;
					}
				}
			}, T.user.cpId, user.id);
		}
	}

	private static final ConcurrentHashMap<String, String> phoneCodeMap = new ConcurrentHashMap<String, String>();

	public static void UserPhoneCode(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_UserPhoneCode requestData = SRequest_UserPhoneCode.load(data);
		SResponse_UserPhoneCode responseData = new SResponse_UserPhoneCode();

		String phone = requestData.phone;
		if (TextUtils.isEmpty(phone))
		{
			error(response, SErrorCode.unknown_error, "手机号为空");
			return;
		}

		if (requestData.type.equals(SPhoneCodeType.Reg))
		{
			if (DBHelper.hasRecord(T.user, T.user.phone, phone))
			{
				error(response, SErrorCode.unknown_error, "手机号已存在，可直接登录");
				return;
			}
			String code = RandomHelper.RandNumberString(4);
			if (SendSms.send(phone, SendSms.AppMath, SendSms.TemplateReg, code))
			{
				phoneCodeMap.put(phone, code);
				LOG.v(phone + "获取注册验证码：" + code);
				success(response, responseData.saveToStr());
			}
			else
			{
				error(response, SErrorCode.unknown_error, "验证码发送失败");
			}
		}
		else if (requestData.type.equals(SPhoneCodeType.Login))
		{
			if (!DBHelper.hasRecord(T.user, T.user.phone, phone))
			{
				error(response, SErrorCode.unknown_error, "手机号未注册");
				return;
			}
			String code = RandomHelper.RandNumberString(4);
			if (SendSms.send(phone, SendSms.AppMath, SendSms.TemplateLogin, code))
			{
				phoneCodeMap.put(phone, code);
				LOG.v(phone + "获取登录验证码：" + code);
				success(response, responseData.saveToStr());
			}
			else
			{
				error(response, SErrorCode.unknown_error, "验证码发送失败");
			}
		}
	}

	public static void UserReg(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_UserReg requestData = SRequest_UserReg.load(data);
		SResponse_UserReg responseData = new SResponse_UserReg();

		String phone = requestData.phone;
		if (TextUtils.isEmpty(phone))
		{
			error(response, SErrorCode.unknown_error, "手机号为空");
			return;
		}

		if (DBHelper.hasRecord(T.user, T.user.phone, phone))
		{
			error(response, SErrorCode.unknown_error, "手机号已存在，可直接登录");
			return;
		}

		if (!phoneCodeMap.containsKey(phone) || !phoneCodeMap.get(phone).equals(requestData.code))
		{
			error(response, SErrorCode.unknown_error, "验证码错误或无效");
			return;
		}

		String userId;
		do
		{
			userId = "U" + RandomHelper.RandNumberStringWithout4(8);
		} while (DBHelper.hasRecord(T.user, T.user.id, userId));

		DBHelper.insert(T.user, //
				T.user.id, userId, //
				T.user.userType, SUserType.Student, //
				T.user.psw, "123", //
				T.user.name, "未命名", //
				T.user.head, defaultHead, //
				T.user.sex, SSex.Unknow, //
				T.user.grade, 0, //
				T.user.phone, phone);

		String defaultMst = defaultMst2;

		ProcessFriend.addFriendImpl(userId, defaultMst);
		ProcessFriend.addFriendImpl(defaultMst, userId);

		RongHelper.sendPrivateMessage(new TransMessage(TransEvt_FriendChange, null), userId, defaultMst);
//		RongHelper.sendSystemMessage(new TxtMessage("欢迎使用小翼伴学！", null), userId);

		responseData.userId = userId;
		responseData.psw = "123";

		phoneCodeMap.remove(phone);

		success(response, responseData.saveToStr());
	}

	public static void UserLogin(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_UserLogin requestData = SRequest_UserLogin.load(data);
		SResponse_UserLogin responseData = new SResponse_UserLogin();

		if (TextUtils.isEmpty(requestData.userId))
		{
			error(response, SErrorCode.unknown_error, "账号为空");
			return;
		}

		LOG.v("login userId : " + requestData.userId);
		LOG.v("login psw : " + requestData.psw);

		responseData.user = selectUser(requestData.userId, true);
		if (responseData.user != null)
		{
			boolean pswOrCodeIsPass = false;
			if (!pswOrCodeIsPass)
			{
				String phone = responseData.user.phone;
				if (!TextUtils.isEmpty(phone))
				{
					if (phoneCodeMap.containsKey(phone) && phoneCodeMap.get(phone).equals(requestData.psw))
					{
						pswOrCodeIsPass = true;
						phoneCodeMap.remove(phone);
					}
				}
			}
			if (!pswOrCodeIsPass)
			{
				if (responseData.user.psw.equals(requestData.psw))
				{
					pswOrCodeIsPass = true;
				}
			}
			if (!pswOrCodeIsPass)
			{
				recordDevice(requestData, "密码或验证码错误");
				error(response, SErrorCode.PswError, "密码或验证码错误");
				return;
			}

			if (TextUtils.isEmpty(responseData.user.token))
			{
				LOG.v("get token ...");
				String token = RongHelper.getToken(responseData.user.id, responseData.user.name, TextUtils.isEmpty(responseData.user.head) ? defaultHead : checkUrl(responseData.user.head));
				if (!TextUtils.isEmpty(token))
				{
					responseData.user.token = token;
					DBHelper.update(T.user, DBHelper.set(T.user.token, token), T.user.id, responseData.user.id);
				}
				else
				{
					recordDevice(requestData, "获取token失败");
					error(response, SErrorCode.unknown_error, "获取token失败");
					return;
				}
			}
			recordDevice(requestData, "success");
			checkOwnerCp(responseData.user);
			success(response, responseData.saveToStr());
		}
		else
		{
			error(response, SErrorCode.AccountNotExist, "账号不存在");
		}
	}

	public static void GetUser(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetUser requestData = SRequest_GetUser.load(data);
		SResponse_GetUser responseData = new SResponse_GetUser();
		responseData.user = selectUser(requestData.userId);
		if (requestData.checkOwnerId)
			checkOwnerCp(responseData.user);
		success(response, responseData.saveToStr());
	}

	public static void ModifyHead(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ModifyHead requestData = SRequest_ModifyHead.load(data);
		SResponse_ModifyHead responseData = new SResponse_ModifyHead();
		DBHelper.update(T.user, DBHelper.set(T.user.head, requestData.head), T.user.id, requestData.userId);
		SUser user = selectUser(requestData.userId);
		RongHelper.refreshUserInfo(user.id, user.name, checkUrl(user.head));
		success(response, responseData.saveToStr());
	}

	public static void ModifyName(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ModifyName requestData = SRequest_ModifyName.load(data);
		SResponse_ModifyName responseData = new SResponse_ModifyName();
		DBHelper.update(T.user, DBHelper.set(T.user.name, requestData.name), T.user.id, requestData.userId);
		SUser user = selectUser(requestData.userId);
		RongHelper.refreshUserInfo(user.id, user.name, checkUrl(user.head));
		success(response, responseData.saveToStr());
	}

	public static void ModifySex(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ModifySex requestData = SRequest_ModifySex.load(data);
		SResponse_ModifySex responseData = new SResponse_ModifySex();
		DBHelper.update(T.user, DBHelper.set(T.user.sex, requestData.sex), T.user.id, requestData.userId);
		success(response, responseData.saveToStr());
	}

	public static void ModifyGrade(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ModifyGrade requestData = SRequest_ModifyGrade.load(data);
		SResponse_ModifyGrade responseData = new SResponse_ModifyGrade();
		DBHelper.update(T.user, DBHelper.set(T.user.grade, requestData.grade), T.user.id, requestData.userId);
		success(response, responseData.saveToStr());
	}

	public static void ModifyPsw(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ModifyPsw requestData = SRequest_ModifyPsw.load(data);
		SResponse_ModifyPsw responseData = new SResponse_ModifyPsw();
		SUser user = selectUser(requestData.userId, true);
		if (user != null)
		{
			if (!user.psw.equals(requestData.oldPsw))
			{
				error(response, SErrorCode.PswError, "旧密码错误");
				return;
			}
			DBHelper.update(T.user, DBHelper.set(T.user.psw, requestData.newPsw), T.user.id, requestData.userId);
			success(response, responseData.saveToStr());
		}
		else
		{
			error(response, SErrorCode.AccountNotExist, "账号不存在");
		}
	}

	public static void GetUserList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetUserList requestData = SRequest_GetUserList.load(data);
		SResponse_GetUserList responseData = new SResponse_GetUserList();
		responseData.users = selectUserList(requestData.userType);
		success(response, responseData.saveToStr());
	}

	public static void AddUser(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_AddUser requestData = SRequest_AddUser.load(data);
		SResponse_AddUser responseData = new SResponse_AddUser();
		String userId = requestData.userId;
		if (TextUtils.isEmpty(userId))
		{
			do
			{
				userId = "U" + RandomHelper.RandNumberStringWithout4(8);
			} while (DBHelper.hasRecord(T.user, T.user.id, userId));
		}
		else
		{
			if (DBHelper.hasRecord(T.user, T.user.id, userId))
			{
				error(response, SErrorCode.unknown_error, "该用户不能重复添加");
				return;
			}
		}
		DBHelper.insert(T.user, //
				T.user.id, userId, //
				T.user.userType, requestData.userType, //
				T.user.psw, requestData.psw, //
				T.user.name, requestData.name, //
				T.user.head, TextUtils.isEmpty(requestData.head) ? defaultHead : requestData.head, //
				T.user.sex, requestData.sex, //
				T.user.grade, requestData.grade);

		String defaultMst;
		if (requestData.from == 1)
			defaultMst = defaultMst2;
		else
			defaultMst = defaultMst1;

		ProcessFriend.addFriendImpl(userId, defaultMst);
		ProcessFriend.addFriendImpl(defaultMst, userId);

		RongHelper.sendPrivateMessage(new TransMessage(TransEvt_FriendChange, null), userId, defaultMst);
//		RongHelper.sendSystemMessage(new TxtMessage("欢迎使用小翼伴学！", null), userId);

		responseData.userId = userId;
		responseData.psw = requestData.psw;

		success(response, responseData.saveToStr());
	}

	public static void DeleteUser(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_DeleteUser requestData = SRequest_DeleteUser.load(data);
		SResponse_DeleteUser responseData = new SResponse_DeleteUser();
		if (DBHelper.hasRecord(T.user, T.user.id, requestData.userId))
		{
			DBHelper.delete(T.user, T.user.id, requestData.userId);

			DBHelper.delete(T.friend, T.friend.userId, requestData.userId);
			DBHelper.delete(T.friend, T.friend.friendId, requestData.userId);

			DBHelper.delete(T.task, T.task.userId, requestData.userId);
			SVNManager.deleteIfExists(requestData.userId, "delete user by server");

			DBHelper.delete(T.comment, T.comment.userId, requestData.userId);

			success(response, responseData.saveToStr());
		}
		else
		{
			error(response, SErrorCode.unknown_error, "用户不存在");
		}
	}

	public static void SetVip(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SetVip requestData = SRequest_SetVip.load(data);
		SResponse_SetVip responseData = new SResponse_SetVip();
		DBHelper.update(T.user, DBHelper.set(T.user.vipLevel, requestData.vipLevel, T.user.vipTime, requestData.vipTime), T.user.id, requestData.userId);
		success(response, responseData.saveToStr());
	}

	public static void SetCp(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SetCp requestData = SRequest_SetCp.load(data);
		SResponse_SetCp responseData = new SResponse_SetCp();
		if (TextUtils.isEmpty(requestData.userId))
		{
			error(response, SErrorCode.unknown_error, "未指定主角");
			return;
		}
		SUser owner = selectUser(requestData.userId);
		if (owner == null)
		{
			error(response, SErrorCode.unknown_error, "未找到主角");
			return;
		}

		if (TextUtils.isEmpty(requestData.cpId))
		{
			// 断开关系
			DBHelper.update(T.user, DBHelper.set(T.user.cpId, ""), T.user.id, requestData.userId);
			success(response, responseData.saveToStr());
		}
		else
		{
			if (!owner.userType.equals(SUserType.Student))
			{
				error(response, SErrorCode.unknown_error, "主角不是学生");
				return;
			}
			if (!TextUtils.isEmpty(owner.cpId))
			{
				error(response, SErrorCode.unknown_error, "主角已经有学伴");
				return;
			}
			checkOwnerCp(owner);
			if (!TextUtils.isEmpty(owner.ownerId))
			{
				error(response, SErrorCode.unknown_error, "主角是别人的学伴");
				return;
			}

			SUser cp = selectUser(requestData.cpId);
			if (cp == null)
			{
				error(response, SErrorCode.unknown_error, "未找到学伴");
				return;
			}
			if (!cp.userType.equals(SUserType.Student))
			{
				error(response, SErrorCode.unknown_error, "学伴不是学生");
				return;
			}
			if (!TextUtils.isEmpty(cp.cpId))
			{
				error(response, SErrorCode.unknown_error, "学伴已经有学伴");
				return;
			}
			checkOwnerCp(cp);
			if (!TextUtils.isEmpty(cp.ownerId))
			{
				error(response, SErrorCode.unknown_error, "学伴是别人的学伴");
				return;
			}

			// 建立关系
			DBHelper.update(T.user, DBHelper.set(T.user.cpId, requestData.cpId), T.user.id, requestData.userId);
			success(response, responseData.saveToStr());
		}
	}
}
