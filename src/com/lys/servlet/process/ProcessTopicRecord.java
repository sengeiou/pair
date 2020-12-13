package com.lys.servlet.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.SRequest_TopicRecordDelete;
import com.lys.protobuf.SRequest_TopicRecordGet;
import com.lys.protobuf.SRequest_TopicRecordGetList;
import com.lys.protobuf.SRequest_TopicRecordSetFav;
import com.lys.protobuf.SRequest_TopicRecordSetResult;
import com.lys.protobuf.SResponse_TopicRecordDelete;
import com.lys.protobuf.SResponse_TopicRecordGet;
import com.lys.protobuf.SResponse_TopicRecordGetList;
import com.lys.protobuf.SResponse_TopicRecordSetFav;
import com.lys.protobuf.SResponse_TopicRecordSetResult;
import com.lys.protobuf.STopicRecord;

public class ProcessTopicRecord extends BaseProcess
{
	public static STopicRecord selectTopicRecord(String userId, String topicId) throws Exception
	{
		final List<STopicRecord> topicRecords = new ArrayList<>();
		DBHelper.select(T.topicRecord, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				if (rs.next())
				{
					topicRecords.add(packTopicRecord(rs));
				}
			}
		}, T.topicRecord.userId, userId, T.topicRecord.topicId, topicId);
		if (topicRecords.size() > 0)
			return topicRecords.get(0);
		else
			return null;
	}

	private static List<STopicRecord> selectTopicRecordList(String sql) throws Exception
	{
		final List<STopicRecord> topicRecords = new ArrayList<>();
		DBHelper.exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					topicRecords.add(packTopicRecord(rs));
				}
			}
		});
		return topicRecords;
	}

	public static void TopicRecordGetList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TopicRecordGetList requestData = SRequest_TopicRecordGetList.load(data);
		SResponse_TopicRecordGetList responseData = new SResponse_TopicRecordGetList();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s", T.topicRecord));

		sb.append(String.format(" where %s = '%s'", T.topicRecord.userId, requestData.userId));

		if (requestData.type == 1) // fav
		{
			sb.append(String.format(" and %s = %s", T.topicRecord.fav, 1));
		}
		else if (requestData.type == 2) // result
		{
			sb.append(String.format(" and %s != %s", T.topicRecord.result, 0));
		}
		else if (requestData.type == 3) // error
		{
			sb.append(String.format(" and %s = %s", T.topicRecord.result, 1));
		}

		if (requestData.time > 0)
		{
			if (requestData.prev)
				sb.append(String.format(" and %s > %s", T.topicRecord.time, requestData.time));
			else
				sb.append(String.format(" and %s < %s", T.topicRecord.time, requestData.time));
		}

		if (requestData.prev)
			sb.append(String.format(" order by %s asc", T.topicRecord.time));
		else
			sb.append(String.format(" order by %s desc", T.topicRecord.time));

		if (requestData.pageSize > 0)
			sb.append(String.format(" limit 0, %s", requestData.pageSize));

		sb.append(String.format(";"));

		responseData.topicRecords = selectTopicRecordList(sb.toString());

		success(response, responseData.saveToStr());
	}

	public static void TopicRecordGet(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TopicRecordGet requestData = SRequest_TopicRecordGet.load(data);
		SResponse_TopicRecordGet responseData = new SResponse_TopicRecordGet();
		responseData.topicRecord = selectTopicRecord(requestData.userId, requestData.topicId);
		success(response, responseData.saveToStr());
	}

	public static void TopicRecordSetFav(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TopicRecordSetFav requestData = SRequest_TopicRecordSetFav.load(data);
		SResponse_TopicRecordSetFav responseData = new SResponse_TopicRecordSetFav();
		STopicRecord topicRecord = selectTopicRecord(requestData.userId, requestData.topicId);
		if (topicRecord != null)
		{
			if (requestData.fav == 1)
			{
				DBHelper.update(T.topicRecord, DBHelper.set(T.topicRecord.fav, requestData.fav), T.topicRecord.userId, requestData.userId, T.topicRecord.topicId, requestData.topicId);
			}
			else
			{
				if (topicRecord.result == 0)
				{
					DBHelper.delete(T.topicRecord, T.topicRecord.userId, requestData.userId, T.topicRecord.topicId, requestData.topicId);
				}
				else
				{
					DBHelper.update(T.topicRecord, DBHelper.set(T.topicRecord.fav, requestData.fav), T.topicRecord.userId, requestData.userId, T.topicRecord.topicId, requestData.topicId);
				}
			}
		}
		else
		{
			if (requestData.fav == 1)
			{
				DBHelper.insert(T.topicRecord, //
						T.topicRecord.userId, requestData.userId, //
						T.topicRecord.topicId, requestData.topicId, //
						T.topicRecord.fav, requestData.fav, //
						T.topicRecord.result, 0, //
						T.topicRecord.time, System.currentTimeMillis());
			}
		}
		responseData.topicRecord = selectTopicRecord(requestData.userId, requestData.topicId);
		success(response, responseData.saveToStr());
	}

	public static void TopicRecordSetResult(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TopicRecordSetResult requestData = SRequest_TopicRecordSetResult.load(data);
		SResponse_TopicRecordSetResult responseData = new SResponse_TopicRecordSetResult();
		STopicRecord topicRecord = selectTopicRecord(requestData.userId, requestData.topicId);
		if (topicRecord != null)
		{
			DBHelper.update(T.topicRecord, DBHelper.set(T.topicRecord.result, requestData.result), T.topicRecord.userId, requestData.userId, T.topicRecord.topicId, requestData.topicId);
		}
		else
		{
			DBHelper.insert(T.topicRecord, //
					T.topicRecord.userId, requestData.userId, //
					T.topicRecord.topicId, requestData.topicId, //
					T.topicRecord.fav, 0, //
					T.topicRecord.result, requestData.result, //
					T.topicRecord.time, System.currentTimeMillis());
		}
		responseData.topicRecord = selectTopicRecord(requestData.userId, requestData.topicId);
		success(response, responseData.saveToStr());
	}

	public static void TopicRecordDelete(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TopicRecordDelete requestData = SRequest_TopicRecordDelete.load(data);
		SResponse_TopicRecordDelete responseData = new SResponse_TopicRecordDelete();
		DBHelper.delete(T.topicRecord, T.topicRecord.userId, requestData.userId, T.topicRecord.topicId, requestData.topicId);
		success(response, responseData.saveToStr());
	}
}
