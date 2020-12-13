package com.lys.servlet.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.manager.TopicManager;
import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.SEvent;
import com.lys.protobuf.SRequest_AddEvent;
import com.lys.protobuf.SRequest_GetEventList;
import com.lys.protobuf.SResponse_AddEvent;
import com.lys.protobuf.SResponse_GetEventList;

public class ProcessEvent extends BaseProcess
{
	private static List<SEvent> selectEventList(String sql) throws Exception
	{
		final List<SEvent> eventList = new ArrayList<>();
		DBHelper.exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					eventList.add(packEvent(rs));
				}
			}
		});
		return eventList;
	}

	public static String getTableName(String userId)
	{
		return String.format("%s_%s", T.event, userId);
	}

	public static void AddEvent(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_AddEvent requestData = SRequest_AddEvent.load(data);
		SResponse_AddEvent responseData = new SResponse_AddEvent();

		String tableName = getTableName(requestData.userId);

		TopicManager.insureEventTableExists(tableName);

		DBHelper.insert(tableName, //
				T.event.action, requestData.event.action, //
				T.event.target, requestData.event.target, //
				T.event.des, requestData.event.des, //
				T.event.time, System.currentTimeMillis());

		success(response, responseData.saveToStr());
	}

	public static void GetEventList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetEventList requestData = SRequest_GetEventList.load(data);
		SResponse_GetEventList responseData = new SResponse_GetEventList();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s", getTableName(requestData.userId)));

		if (requestData.actions.size() == 1)
		{
			sb.append(String.format(" and %s = '%s'", T.event.action, requestData.actions.get(0)));
		}
		else if (requestData.actions.size() > 1)
		{
			for (int i = 0; i < requestData.actions.size(); i++)
			{
				String action = requestData.actions.get(i);
				if (i == 0)
					sb.append(String.format(" and (%s = '%s'", T.event.action, action));
				else if (i == requestData.actions.size() - 1)
					sb.append(String.format(" or %s = '%s')", T.event.action, action));
				else
					sb.append(String.format(" or %s = '%s'", T.event.action, action));
			}
		}

		if (requestData.targets.size() == 1)
		{
			sb.append(String.format(" and %s = '%s'", T.event.target, requestData.targets.get(0)));
		}
		else if (requestData.targets.size() > 1)
		{
			for (int i = 0; i < requestData.targets.size(); i++)
			{
				String target = requestData.targets.get(i);
				if (i == 0)
					sb.append(String.format(" and (%s = '%s'", T.event.target, target));
				else if (i == requestData.targets.size() - 1)
					sb.append(String.format(" or %s = '%s')", T.event.target, target));
				else
					sb.append(String.format(" or %s = '%s'", T.event.target, target));
			}
		}

		sb.append(String.format(";"));

		responseData.events = selectEventList(sb.toString().replaceFirst(" and ", " where "));

		success(response, responseData.saveToStr());
	}

}
