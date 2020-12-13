package com.lys.servlet.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.base.utils.AppDataTool;
import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.SLiveTask;
import com.lys.protobuf.SRequest_LiveAddModify;
import com.lys.protobuf.SRequest_LiveDelete;
import com.lys.protobuf.SRequest_LiveGetAll;
import com.lys.protobuf.SRequest_LiveGetList;
import com.lys.protobuf.SResponse_LiveAddModify;
import com.lys.protobuf.SResponse_LiveDelete;
import com.lys.protobuf.SResponse_LiveGetAll;
import com.lys.protobuf.SResponse_LiveGetList;
import com.lys.utils.CommonUtils;
import com.lys.utils.TextUtils;

public class ProcessLive extends BaseProcess
{
	private static List<SLiveTask> selectLiveList(String sql) throws Exception
	{
		final List<SLiveTask> lives = new ArrayList<>();
		DBHelper.exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					lives.add(packLive(rs));
				}
			}
		});
		return lives;
	}

	public static void LiveGetAll(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_LiveGetAll requestData = SRequest_LiveGetAll.load(data);
		SResponse_LiveGetAll responseData = new SResponse_LiveGetAll();

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("select * from %s", T.live));
		sb.append(String.format(" order by %s desc", T.live.startTime));
		sb.append(String.format(";"));

		responseData.lives = selectLiveList(sb.toString());

		success(response, responseData.saveToStr());
	}

	public static void LiveGetList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_LiveGetList requestData = SRequest_LiveGetList.load(data);
		SResponse_LiveGetList responseData = new SResponse_LiveGetList();

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("select * from %s", T.live));
		sb.append(String.format(" where %s = %s", T.live.type, 1));
		sb.append(String.format(" or %s like '%%%s%%'", T.live.userIds, requestData.userId));
		sb.append(String.format(" order by %s desc", T.live.startTime));
		sb.append(String.format(";"));

		responseData.lives = selectLiveList(sb.toString());

		success(response, responseData.saveToStr());
	}

	public static void LiveAddModify(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_LiveAddModify requestData = SRequest_LiveAddModify.load(data);
		SResponse_LiveAddModify responseData = new SResponse_LiveAddModify();

		if (TextUtils.isEmpty(requestData.live.id))
		{
			// Add
			DBHelper.insert(T.live, //
					T.live.id, CommonUtils.uuid(), //
					T.live.actorId, requestData.live.actorId, //
					T.live.name, requestData.live.name, //
					T.live.des, requestData.live.des, //
					T.live.cover, requestData.live.cover, //
					T.live.video, requestData.live.video, //
					T.live.duration, requestData.live.duration, //
					T.live.taskId, requestData.live.taskId, //
					T.live.type, requestData.live.type, //
					T.live.userIds, AppDataTool.saveStringList(requestData.live.userIds).toString(), //
					T.live.startTime, requestData.live.startTime);
		}
		else
		{
			// Modify
			DBHelper.update(T.live, DBHelper.set(T.live.actorId, requestData.live.actorId, //
					T.live.name, requestData.live.name, //
					T.live.des, requestData.live.des, //
					T.live.cover, requestData.live.cover, //
					T.live.video, requestData.live.video, //
					T.live.duration, requestData.live.duration, //
					T.live.taskId, requestData.live.taskId, //
					T.live.type, requestData.live.type, //
					T.live.userIds, AppDataTool.saveStringList(requestData.live.userIds).toString(), //
					T.live.startTime, requestData.live.startTime), T.live.id, requestData.live.id);
		}

		success(response, responseData.saveToStr());
	}

	public static void LiveDelete(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_LiveDelete requestData = SRequest_LiveDelete.load(data);
		SResponse_LiveDelete responseData = new SResponse_LiveDelete();
		DBHelper.delete(T.live, T.live.id, requestData.id);
		success(response, responseData.saveToStr());
	}
}
