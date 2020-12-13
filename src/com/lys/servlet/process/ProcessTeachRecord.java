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
import com.lys.protobuf.SErrorCode;
import com.lys.protobuf.SRequest_TeachConfirmByStudent;
import com.lys.protobuf.SRequest_TeachGetList;
import com.lys.protobuf.SRequest_TeachOverByStudent;
import com.lys.protobuf.SRequest_TeachOverByTeacher;
import com.lys.protobuf.SRequest_TeachQuestionByStudent;
import com.lys.protobuf.SRequest_TeachQuestionByTeacher;
import com.lys.protobuf.SRequest_TeachStart;
import com.lys.protobuf.SResponse_TeachConfirmByStudent;
import com.lys.protobuf.SResponse_TeachGetList;
import com.lys.protobuf.SResponse_TeachOverByStudent;
import com.lys.protobuf.SResponse_TeachOverByTeacher;
import com.lys.protobuf.SResponse_TeachQuestionByStudent;
import com.lys.protobuf.SResponse_TeachQuestionByTeacher;
import com.lys.protobuf.SResponse_TeachStart;
import com.lys.protobuf.STeachPage;
import com.lys.protobuf.STeachRecord;

public class ProcessTeachRecord extends BaseProcess
{
	public static List<STeachRecord> selectTeachRecordList(String sql) throws Exception
	{
		final List<STeachRecord> teachRecords = new ArrayList<>();
		DBHelper.exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					STeachRecord teachRecord = packTeachRecord(rs);
					teachRecord.task = packTask(rs);
					teachRecords.add(teachRecord);
				}
			}
		});
		return teachRecords;
	}

	public static void TeachStart(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TeachStart requestData = SRequest_TeachStart.load(data);
		SResponse_TeachStart responseData = new SResponse_TeachStart();
		if (DBHelper.hasRecord(T.teachRecord, T.teachRecord.teachId, requestData.teachId))
		{
			error(response, SErrorCode.unknown_error, "teachId已存在");
			return;
		}

		long startTime = System.currentTimeMillis();

		DBHelper.insert(T.teachRecord, //
				T.teachRecord.teachId, requestData.teachId, //
				T.teachRecord.userId, requestData.userId, //
				T.teachRecord.isHost, 1, //
				T.teachRecord.targetCount, requestData.targetIds.size(), //
				T.teachRecord.targetIds, AppDataTool.saveStringList(requestData.targetIds).toString(), //
				T.teachRecord.taskId, requestData.taskId, //
				T.teachRecord.startTime, startTime);

		for (String userId : requestData.targetIds)
		{
			List<String> targetIds = new ArrayList<String>();
			targetIds.add(requestData.userId);

			DBHelper.insert(T.teachRecord, //
					T.teachRecord.teachId, requestData.teachId, //
					T.teachRecord.userId, userId, //
					T.teachRecord.isHost, 0, //
					T.teachRecord.targetCount, targetIds.size(), //
					T.teachRecord.targetIds, AppDataTool.saveStringList(targetIds).toString(), //
					T.teachRecord.taskId, requestData.taskId, //
					T.teachRecord.startTime, startTime);
		}

		success(response, responseData.saveToStr());
	}

	public static void TeachOverByTeacher(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TeachOverByTeacher requestData = SRequest_TeachOverByTeacher.load(data);
		SResponse_TeachOverByTeacher responseData = new SResponse_TeachOverByTeacher();
		long overTime = System.currentTimeMillis();
		DBHelper.update(T.teachRecord, DBHelper.set(T.teachRecord.overTime, overTime), //
				T.teachRecord.teachId, requestData.teachId, T.teachRecord.userId, requestData.userId);
		DBHelper.update(T.teachRecord, DBHelper.set(T.teachRecord.teachPages, STeachPage.saveList(requestData.teachPages).toString()), //
				T.teachRecord.teachId, requestData.teachId);
		success(response, responseData.saveToStr());
	}

	public static void TeachQuestionByTeacher(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TeachQuestionByTeacher requestData = SRequest_TeachQuestionByTeacher.load(data);
		SResponse_TeachQuestionByTeacher responseData = new SResponse_TeachQuestionByTeacher();
		DBHelper.update(T.teachRecord, DBHelper.set(T.teachRecord.questionHot, requestData.questionHot, //
				T.teachRecord.questionMind, requestData.questionMind, //
				T.teachRecord.questionLogic, requestData.questionLogic, //
				T.teachRecord.questionOther, requestData.questionOther), //
				T.teachRecord.teachId, requestData.teachId);
		success(response, responseData.saveToStr());
	}

	public static void TeachOverByStudent(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TeachOverByStudent requestData = SRequest_TeachOverByStudent.load(data);
		SResponse_TeachOverByStudent responseData = new SResponse_TeachOverByStudent();
		long overTime = System.currentTimeMillis();
		DBHelper.update(T.teachRecord, DBHelper.set(T.teachRecord.overTime, overTime), //
				T.teachRecord.teachId, requestData.teachId, T.teachRecord.userId, requestData.userId);
		success(response, responseData.saveToStr());
	}

	public static void TeachConfirmByStudent(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TeachConfirmByStudent requestData = SRequest_TeachConfirmByStudent.load(data);
		SResponse_TeachConfirmByStudent responseData = new SResponse_TeachConfirmByStudent();
		DBHelper.update(T.teachRecord, DBHelper.set(T.teachRecord.confirmMsg, requestData.confirmMsg), //
				T.teachRecord.teachId, requestData.teachId, T.teachRecord.userId, requestData.userId);
		DBHelper.update(T.teachRecord, DBHelper.set(T.teachRecord.confirmMsg, requestData.confirmMsg), //
				T.teachRecord.teachId, requestData.teachId, T.teachRecord.userId, requestData.targetId);
		success(response, responseData.saveToStr());
	}

	public static void TeachQuestionByStudent(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TeachQuestionByStudent requestData = SRequest_TeachQuestionByStudent.load(data);
		SResponse_TeachQuestionByStudent responseData = new SResponse_TeachQuestionByStudent();
		DBHelper.update(T.teachRecord, DBHelper.set(T.teachRecord.questionMatch, requestData.questionMatch, //
				T.teachRecord.questionDiff, requestData.questionDiff, //
				T.teachRecord.questionGot, requestData.questionGot, //
				T.teachRecord.questionQuality, requestData.questionQuality, //
				T.teachRecord.questionLike, requestData.questionLike), //
				T.teachRecord.teachId, requestData.teachId, T.teachRecord.userId, requestData.userId);
		DBHelper.update(T.teachRecord, DBHelper.set(T.teachRecord.questionMatch, requestData.questionMatch, //
				T.teachRecord.questionDiff, requestData.questionDiff, //
				T.teachRecord.questionGot, requestData.questionGot, //
				T.teachRecord.questionQuality, requestData.questionQuality, //
				T.teachRecord.questionLike, requestData.questionLike), //
				T.teachRecord.teachId, requestData.teachId, T.teachRecord.userId, requestData.targetId);
		success(response, responseData.saveToStr());
	}

	public static void TeachGetList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_TeachGetList requestData = SRequest_TeachGetList.load(data);
		SResponse_TeachGetList responseData = new SResponse_TeachGetList();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s left join %s on %s.%s = %s.%s", T.teachRecord, T.task, //
				T.teachRecord, T.teachRecord.taskId, //
				T.task, T.task.id));

		sb.append(String.format(" where %s = '%s'", T.teachRecord.userId, requestData.userId));

		sb.append(String.format(" and %s > %s", T.teachRecord.startTime, requestData.fromTime));
		sb.append(String.format(" and %s < %s", T.teachRecord.startTime, requestData.toTime));

		sb.append(String.format(" order by %s desc", T.teachRecord.startTime));

		sb.append(String.format(";"));

		responseData.teachRecords = selectTeachRecordList(sb.toString());

		success(response, responseData.saveToStr());
	}
}
