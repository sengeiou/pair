package com.lys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.STeachRecord;
import com.lys.protobuf.SUser;
import com.lys.servlet.process.BaseProcess;
import com.lys.servlet.process.ProcessTeachRecord;
import com.lys.utils.CommonUtils;
import com.lys.utils.LOG;

public class DBClient extends BaseProcess
{
	public static List<SUser> selectUserList(String name)
	{
		final List<SUser> users = new ArrayList<>();
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
		}, T.user.name, name);
		return users;
	}

	public static SUser selectUser(String name)
	{
		List<SUser> users = DBClient.selectUserList(name);
		if (users.size() == 1)
		{
			return users.get(0);
		}
		else
		{
			LOG.e(name + ":" + users.size());
			return null;
		}
	}

	public static void bujiuTeachRecord(String teachId) throws Exception
	{
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s left join %s on %s.%s = %s.%s", T.teachRecord, T.task, //
				T.teachRecord, T.teachRecord.taskId, //
				T.task, T.task.id));

		sb.append(String.format(" where %s = '%s'", T.teachRecord.teachId, teachId));

//		sb.append(String.format(" and %s = %s", T.teachRecord.targetCount, 1));
//		sb.append(String.format(" and %s < %s", T.teachRecord.startTime, requestData.toTime));

		sb.append(String.format(" order by %s desc", T.teachRecord.startTime));

		sb.append(String.format(";"));

		List<STeachRecord> teachRecords = ProcessTeachRecord.selectTeachRecordList(sb.toString());

		for (int i = 0; i < teachRecords.size(); i++)
		{
			STeachRecord teachRecord = teachRecords.get(i);
			LOG.v(String.format("	userId = %10s	isHost = %s	targetCount = %s,	overTime = %15s,	teachPages = %s,	", teachRecord.userId, teachRecord.isHost, teachRecord.targetCount, teachRecord.overTime, teachRecord.teachPages != null));
		}

		if (teachRecords.size() == 2)
		{
			STeachRecord teachRecordRight = teachRecords.get(0);
			STeachRecord teachRecordWrong = teachRecords.get(1);
			if (teachRecordRight.overTime == 0)
			{
				STeachRecord tmp = teachRecordRight;
				teachRecordRight = teachRecordWrong;
				teachRecordWrong = tmp;
			}
			if (teachRecordRight.overTime == 0)
			{
				LOG.e("	双方结束时间都为空，距离现在：" + CommonUtils.formatTime(System.currentTimeMillis() - teachRecordRight.startTime));
			}
			else
			{
				LOG.v("	开始纠正，距离现在：" + CommonUtils.formatTime(System.currentTimeMillis() - teachRecordRight.startTime));
				DBHelper.update(T.teachRecord, DBHelper.set(T.teachRecord.overTime, teachRecordRight.overTime), //
						T.teachRecord.teachId, teachId, T.teachRecord.userId, teachRecordWrong.userId);
			}
		}
	}

	public static void processTeachRecord() throws Exception
	{
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s left join %s on %s.%s = %s.%s", T.teachRecord, T.task, //
				T.teachRecord, T.teachRecord.taskId, //
				T.task, T.task.id));

		sb.append(String.format(" where %s = %s", T.teachRecord.overTime, 0));

		sb.append(String.format(" and %s = %s", T.teachRecord.targetCount, 1));
//		sb.append(String.format(" and %s < %s", T.teachRecord.startTime, requestData.toTime));

		sb.append(String.format(" order by %s desc", T.teachRecord.startTime));

		sb.append(String.format(";"));

		List<STeachRecord> teachRecords = ProcessTeachRecord.selectTeachRecordList(sb.toString());

		for (int i = 0; i < teachRecords.size(); i++)
		{
			STeachRecord teachRecord = teachRecords.get(i);
			LOG.v(String.format("%d	teachId = %s	userId = %10s	isHost = %s	targetCount = %s,	", i, teachRecord.teachId, teachRecord.userId, teachRecord.isHost, teachRecord.targetCount));
			bujiuTeachRecord(teachRecord.teachId);
//			break;
		}
	}

}
