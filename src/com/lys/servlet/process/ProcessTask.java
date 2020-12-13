package com.lys.servlet.process;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.config.Config;
import com.lys.messages.TaskMessage;
import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.SBoardConfig;
import com.lys.protobuf.SBoardPhoto;
import com.lys.protobuf.SErrorCode;
import com.lys.protobuf.SNotePage;
import com.lys.protobuf.SNotePageSet;
import com.lys.protobuf.SPTask;
import com.lys.protobuf.SPageData;
import com.lys.protobuf.SRequest_AddTaskScore;
import com.lys.protobuf.SRequest_CreateTask;
import com.lys.protobuf.SRequest_DeleteTask;
import com.lys.protobuf.SRequest_FindTask;
import com.lys.protobuf.SRequest_GetTask;
import com.lys.protobuf.SRequest_GetTaskForWeb;
import com.lys.protobuf.SRequest_GetTaskList;
import com.lys.protobuf.SRequest_ModifyTask;
import com.lys.protobuf.SRequest_ModifyTaskComment;
import com.lys.protobuf.SRequest_RandomOpenTask;
import com.lys.protobuf.SRequest_SendTask;
import com.lys.protobuf.SRequest_SetTaskNote;
import com.lys.protobuf.SRequest_SetTaskOpen;
import com.lys.protobuf.SRequest_SetTaskState;
import com.lys.protobuf.SResponse_AddTaskScore;
import com.lys.protobuf.SResponse_CreateTask;
import com.lys.protobuf.SResponse_DeleteTask;
import com.lys.protobuf.SResponse_FindTask;
import com.lys.protobuf.SResponse_GetTask;
import com.lys.protobuf.SResponse_GetTaskForWeb;
import com.lys.protobuf.SResponse_GetTaskList;
import com.lys.protobuf.SResponse_ModifyTask;
import com.lys.protobuf.SResponse_ModifyTaskComment;
import com.lys.protobuf.SResponse_RandomOpenTask;
import com.lys.protobuf.SResponse_SendTask;
import com.lys.protobuf.SResponse_SetTaskNote;
import com.lys.protobuf.SResponse_SetTaskOpen;
import com.lys.protobuf.SResponse_SetTaskState;
import com.lys.protobuf.SUser;
import com.lys.utils.CommonUtils;
import com.lys.utils.FsUtils;
import com.lys.utils.LOG;
import com.lys.utils.RongHelper;
import com.lys.utils.SVNManager;
import com.lys.utils.SVNManager.SvnTaskResult;
import com.lys.utils.TextUtils;

public class ProcessTask extends BaseProcess
{
	public static SPTask selectTask(String taskId) throws Exception
	{
		final List<SPTask> tasks = new ArrayList<>();
		if (true)
		{
			// TODO 测试
			DBHelper.exeSql(String.format("select * from %s left join %s on %s.%s = %s.%s where %s = '%s';", //
					T.task, //
					T.user, //
					T.task, T.task.sendUserId, //
					T.user, T.user.id, //
					T.task.id, //
					taskId), //
					new OnCallback()
					{
						@Override
						public void onResult(ResultSet rs) throws SQLException
						{
							if (rs.next())
							{
								SPTask task = packTask(rs);
								task.sendUser = packUser(rs);
								tasks.add(task);
							}
						}
					});
		}
		else
		{
			DBHelper.select(T.task, new OnCallback()
			{
				@Override
				public void onResult(ResultSet rs) throws SQLException
				{
					if (rs.next())
					{
						tasks.add(packTask(rs));
					}
				}
			}, T.task.id, taskId);
		}
		if (tasks.size() > 0)
			return tasks.get(0);
		else
			return null;
	}

	private static List<SPTask> selectTaskList(String sql) throws Exception
	{
		final List<SPTask> tasks = new ArrayList<>();
		if (true)
		{
			DBHelper.exeSql(sql, new OnCallback()
			{
				@Override
				public void onResult(ResultSet rs) throws SQLException
				{
					while (rs.next())
					{
						SPTask task = packTask(rs);
						task.sendUser = packUser(rs);
						tasks.add(task);
					}
				}
			});
		}
		else
		{
//			DBHelper.select(T.task, new OnCallback()
//			{
//				@Override
//				public void onResult(ResultSet rs) throws SQLException
//				{
//					while (rs.next())
//					{
//						tasks.add(packTask(rs));
//					}
//				}
//			}, T.task.userId, userId);
		}
		return tasks;
	}

	public static void FindTask(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_FindTask requestData = SRequest_FindTask.load(data);
		SResponse_FindTask responseData = new SResponse_FindTask();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s left join %s on %s.%s = %s.%s", T.task, //
				T.user, //
				T.task, T.task.sendUserId, //
				T.user, T.user.id));

		sb.append(String.format(" where %s = '%s'", T.task.name, requestData.name));

		sb.append(String.format(" and %s = '%s'", T.task.group, requestData.group));

		sb.append(String.format(";"));

		List<SPTask> tasks = selectTaskList(sb.toString());

		if (tasks.size() > 0)
		{
			responseData.task = tasks.get(0);
			success(response, responseData.saveToStr());
		}
		else
		{
			success(response, responseData.saveToStr());
//			error(response, SErrorCode.unknown_error, "未找到该任务");
		}
	}

	public static void GetTask(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetTask requestData = SRequest_GetTask.load(data);
		SResponse_GetTask responseData = new SResponse_GetTask();
		responseData.task = selectTask(requestData.taskId);
		if (responseData.task != null)
			success(response, responseData.saveToStr());
		else
			error(response, SErrorCode.unknown_error, "未找到该任务");
	}

	public static void GetTaskList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetTaskList requestData = SRequest_GetTaskList.load(data);
		SResponse_GetTaskList responseData = new SResponse_GetTaskList();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s left join %s on %s.%s = %s.%s", T.task, //
				T.user, //
				T.task, T.task.sendUserId, //
				T.user, T.user.id));

		sb.append(String.format(" where %s = '%s'", T.task.userId, requestData.userId));

		if (!TextUtils.isEmpty(requestData.group))
		{
			sb.append(String.format(" and %s = '%s'", T.task.group, requestData.group));
		}

		if (requestData.overType == 1)
		{
			sb.append(String.format(" and %s = %s", T.task.overTime, 0));
		}
		else if (requestData.overType == 2)
		{
			sb.append(String.format(" and %s > %s", T.task.overTime, 0));
		}

		if (requestData.sortType == 0)
		{
			if (requestData.createTime > 0)
			{
				if (requestData.prev)
					sb.append(String.format(" and %s > %s", T.task.createTime, requestData.createTime));
				else
					sb.append(String.format(" and %s < %s", T.task.createTime, requestData.createTime));
			}

			if (requestData.prev)
				sb.append(String.format(" order by %s asc", T.task.createTime));
			else
				sb.append(String.format(" order by %s desc", T.task.createTime));
		}
		else if (requestData.sortType == 1)
		{
			if (requestData.overTime > 0)
			{
				if (requestData.prev)
					sb.append(String.format(" and %s > %s", T.task.overTime, requestData.overTime));
				else
					sb.append(String.format(" and %s < %s", T.task.overTime, requestData.overTime));
			}

			if (requestData.prev)
				sb.append(String.format(" order by %s asc", T.task.overTime));
			else
				sb.append(String.format(" order by %s desc", T.task.overTime));
		}

		if (requestData.pageSize > 0)
			sb.append(String.format(" limit 0, %s", requestData.pageSize));

		sb.append(String.format(";"));

		responseData.tasks = selectTaskList(sb.toString());

		success(response, responseData.saveToStr());
	}

	public static void CreateTask(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_CreateTask requestData = SRequest_CreateTask.load(data);
		SResponse_CreateTask responseData = new SResponse_CreateTask();
		String id = CommonUtils.uuid();
		DBHelper.insert(T.task, //
				T.task.id, id, //
				T.task.userId, requestData.userId, //
				T.task.type, requestData.type, //
				T.task.jobType, requestData.jobType, //
				T.task.group, requestData.group, //
				T.task.name, requestData.name, //
				T.task.createTime, System.currentTimeMillis());
		responseData.task = selectTask(id);
		if (responseData.task != null)
			success(response, responseData.saveToStr());
		else
			error(response, SErrorCode.unknown_error, "添加失败，请检查是否有特殊字符");
	}

	public static void SendTask(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SendTask requestData = SRequest_SendTask.load(data);
		SResponse_SendTask responseData = new SResponse_SendTask();
		for (String taskId : requestData.taskIds)
		{
			SPTask srcTask = selectTask(taskId);
			if (srcTask != null)
			{
				for (String userId : requestData.userIds)
				{
					String id = CommonUtils.uuid();
					DBHelper.insert(T.task, //
							T.task.id, id, //
							T.task.userId, userId, //
							T.task.sendUserId, !TextUtils.isEmpty(requestData.sendUserId) ? requestData.sendUserId : srcTask.userId, //
							T.task.type, srcTask.type, //
							T.task.jobType, srcTask.jobType, //
							T.task.group, srcTask.group, //
							T.task.name, srcTask.name, //
							T.task.note, srcTask.note, //
							T.task.createTime, System.currentTimeMillis(), //
							T.task.text, requestData.text);
					SPTask newTask = selectTask(id);
					responseData.tasks.add(newTask);
					RongHelper.sendSystemMessage(new TaskMessage(newTask), newTask.userId);
//					File srcTaskDir = new File(Config.fileDir, getTaskPath(srcTask));
//					File newTaskDir = new File(Config.fileDir, getTaskPath(newTask));
//					if (srcTaskDir.exists())
//						FsUtils.copyPath(srcTaskDir, newTaskDir, true);
					SVNManager.copy(srcTask.userId, srcTask.id, newTask.userId, newTask.id, "copy by server");
				}
			}
		}
		success(response, responseData.saveToStr());
	}

	public static void DeleteTask(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_DeleteTask requestData = SRequest_DeleteTask.load(data);
		SResponse_DeleteTask responseData = new SResponse_DeleteTask();
		SPTask task = selectTask(requestData.taskId);
		if (task != null)
		{
			DBHelper.delete(T.task, T.task.id, requestData.taskId);
			SVNManager.deleteIfExists(task.userId, task.id, "delete by server");
//			File taskDir = new File(Config.fileDir, getTaskPath(task));
//			if (taskDir.exists())
//			{
//				FsUtils.deletePath(taskDir, true);
//				FsUtils.delete(taskDir);
//			}
		}
		success(response, responseData.saveToStr());
	}

//	public static String getTaskPath(SPTask task)
//	{
//		return String.format("/lys.tasks/%s/%s", task.userId, task.id);
//	}

//	public static long getTaskLastModifyTime(SPTask task)
//	{
//		File taskDir = new File(Config.fileDir, getTaskPath(task));
//		if (taskDir.exists())
//		{
//			File timeFile = new File(taskDir, "lastModifyTime.txt");
//			if (timeFile.exists())
//			{
//				return Long.valueOf(FsUtils.readText(timeFile));
//			}
//		}
//		return 0;
//	}

//	public static void GetTaskFileVersion(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
//	{
//		SRequest_GetTaskFileVersion requestData = SRequest_GetTaskFileVersion.load(data);
//		SResponse_GetTaskFileVersion responseData = new SResponse_GetTaskFileVersion();
//		File taskDir = new File(Config.fileDir, getTaskPath(requestData.task));
//		if (taskDir.exists())
//		{
//			responseData.exists = true;
//			responseData.lastModifyTime = getTaskLastModifyTime(requestData.task);
//		}
//		else
//		{
//			responseData.exists = false;
//		}
//		success(response, responseData.saveToStr());
//	}

	public static final String VideoLocal = "local:";
	public static final String VideoNet = "net:";

	private static String convertUrl(String url)
	{
		if (!TextUtils.isEmpty(url))
		{
			url = url.replace("file.k12-eco.com", "zjyk-file.oss-cn-huhehaote.aliyuncs.com");
			url = url.replace("topic.k12-eco.com", "zjyk-topic.oss-cn-huhehaote.aliyuncs.com");
		}
		return url;
	}

	private static SBoardConfig convertUrl(SBoardConfig board)
	{
		if (board != null)
		{
			for (SBoardPhoto photo : board.photos)
			{
				photo.cover = convertUrl(photo.cover);
				photo.url = convertUrl(photo.url);
			}
		}
		return board;
	}

	public static void GetTaskForWeb(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetTaskForWeb requestData = SRequest_GetTaskForWeb.load(data);
		SResponse_GetTaskForWeb responseData = new SResponse_GetTaskForWeb();

		SPTask task = null;
		try
		{
			task = ProcessTask.selectTask(requestData.id);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (task != null)
		{
			DBHelper.update(T.task, DBHelper.set(T.task.timesForWeb, task.timesForWeb + 1), T.task.id, task.id);
			String userId = task.userId;
			SvnTaskResult result = SVNManager.updateTask(false, userId, requestData.id);
			if (result.resultCode == SVNManager.ResultCode_Success)
			{
				File taskDir = SVNManager.getTaskDir(userId, requestData.id);
				if (taskDir.exists())
				{
					File pagesetFile = new File(taskDir, "pageset.json");
					if (pagesetFile.exists())
					{
						SNotePageSet pageset = SNotePageSet.load(FsUtils.readText(pagesetFile));

						responseData.userId = userId;
						responseData.id = requestData.id;
						responseData.urlRoot = Config.getUrlRoot() + "/lys.tasks";
						responseData.name = task.name;
						responseData.count = pageset.pages.size();

						if (!TextUtils.isEmpty(requestData.page))
						{
							responseData.singlePage = true;
							Integer index = null;
							for (int i = 0; i < pageset.pages.size(); i++)
							{
								SNotePage page = pageset.pages.get(i);
								if (page.pageDir.equals(requestData.page))
								{
									index = i;
									break;
								}
							}
							if (index != null)
							{
								SNotePage page = pageset.pages.get(index);

								if (index - 1 >= 0)
									responseData.prevPage = pageset.pages.get(index - 1).pageDir;

								if (index + 1 < pageset.pages.size())
									responseData.nextPage = pageset.pages.get(index + 1).pageDir;

								File dir = new File(taskDir, page.pageDir);
								if (dir.exists())
								{
									File boardFile = new File(dir, "board.json");
									if (boardFile.exists())
									{
										SBoardConfig board = convertUrl(SBoardConfig.load(FsUtils.readText(boardFile)));

										SPageData pageData = new SPageData();
										pageData.index = index;
										if (new File(dir, "big_video.mp4").exists())
											pageData.bigVideo = VideoLocal;
										if (new File(dir, "big_video.txt").exists())
											pageData.bigVideo = VideoNet + convertUrl(FsUtils.readText(new File(dir, "big_video.txt")));
										pageData.hasBoard = new File(dir, "board.png").exists();
										pageData.page = page;
										pageData.board = board;
										responseData.pageDatas.add(pageData);
									}
									else
									{
										error(response, SErrorCode.unknown_error, boardFile + " not exists");
										return;
									}
								}
								else
								{
									error(response, SErrorCode.unknown_error, dir + " not exists");
									return;
								}
							}
							else
							{
								error(response, SErrorCode.unknown_error, requestData.page + " not exists");
								return;
							}
						}
						else
						{
							responseData.singlePage = false;
							for (int i = 0; i < pageset.pages.size(); i++)
							{
								SNotePage page = pageset.pages.get(i);

								File dir = new File(taskDir, page.pageDir);
								if (dir.exists())
								{
									File boardFile = new File(dir, "board.json");
									if (boardFile.exists())
									{
										SBoardConfig board = convertUrl(SBoardConfig.load(FsUtils.readText(boardFile)));

										SPageData pageData = new SPageData();
										pageData.index = i;
										if (new File(dir, "big_video.mp4").exists())
											pageData.bigVideo = VideoLocal;
										if (new File(dir, "big_video.txt").exists())
											pageData.bigVideo = VideoNet + convertUrl(FsUtils.readText(new File(dir, "big_video.txt")));
										pageData.hasBoard = new File(dir, "board.png").exists();
										pageData.page = page;
										pageData.board = board;
										responseData.pageDatas.add(pageData);
									}
									else
									{
										error(response, SErrorCode.unknown_error, boardFile + " not exists");
										return;
									}
								}
								else
								{
									error(response, SErrorCode.unknown_error, dir + " not exists");
									return;
								}
							}
						}

						success(response, responseData.saveToStr());
					}
					else
					{
						error(response, SErrorCode.unknown_error, pagesetFile + " not exists");
					}
				}
				else
				{
					error(response, SErrorCode.unknown_error, taskDir + " not exists");
				}
			}
			else
			{
				error(response, SErrorCode.unknown_error, result.resultCode + " : " + result.errorMsg);
			}
		}
		else
		{
			error(response, SErrorCode.unknown_error, "未找到该任务");
		}
	}

	public static void SetTaskState(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SetTaskState requestData = SRequest_SetTaskState.load(data);
		SResponse_SetTaskState responseData = new SResponse_SetTaskState();
		DBHelper.update(T.task, DBHelper.set(T.task.state, requestData.state), T.task.id, requestData.taskId);
		success(response, responseData.saveToStr());
	}

	public static void SetTaskNote(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SetTaskNote requestData = SRequest_SetTaskNote.load(data);
		SResponse_SetTaskNote responseData = new SResponse_SetTaskNote();
		if (DBHelper.update(T.task, DBHelper.set(T.task.note, requestData.note), T.task.id, requestData.taskId))
			success(response, responseData.saveToStr());
		else
			error(response, SErrorCode.unknown_error, "设置失败，请检查是否有特殊字符");
	}

	public static void ModifyTask(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ModifyTask requestData = SRequest_ModifyTask.load(data);
		SResponse_ModifyTask responseData = new SResponse_ModifyTask();
		if (DBHelper.update(T.task, DBHelper.set(T.task.group, requestData.group, //
				T.task.name, requestData.name, //
				T.task.type, requestData.type, //
				T.task.jobType, requestData.jobType), T.task.id, requestData.taskId))
			success(response, responseData.saveToStr());
		else
			error(response, SErrorCode.unknown_error, "设置失败，请检查是否有特殊字符");
	}

	public static void ModifyTaskComment(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ModifyTaskComment requestData = SRequest_ModifyTaskComment.load(data);
		SResponse_ModifyTaskComment responseData = new SResponse_ModifyTaskComment();
		if (DBHelper.update(T.task, DBHelper.set(T.task.comment, requestData.comment), T.task.id, requestData.taskId))
			success(response, responseData.saveToStr());
		else
			error(response, SErrorCode.unknown_error, "设置失败，请检查是否有特殊字符");
	}

	public static void AddTaskScore(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_AddTaskScore requestData = SRequest_AddTaskScore.load(data);
		SResponse_AddTaskScore responseData = new SResponse_AddTaskScore();
		SPTask task = selectTask(requestData.taskId);
		if (task != null)
		{
			if (task.score == 0 && task.overTime == 0)
			{
				SUser user = ProcessUser.selectUser(task.userId);
				if (user != null)
				{
					DBHelper.update(T.task, DBHelper.set(T.task.score, requestData.score, T.task.overTime, System.currentTimeMillis()), T.task.id, requestData.taskId);
					DBHelper.update(T.user, DBHelper.set(T.user.score, user.score + requestData.score), T.user.id, user.id);
					success(response, responseData.saveToStr());
				}
				else
					error(response, SErrorCode.unknown_error, "未找到用户");
			}
			else
				error(response, SErrorCode.unknown_error, "任务已结束");
		}
		else
			error(response, SErrorCode.unknown_error, "未找到该任务");
	}

	public static void SetTaskOpen(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SetTaskOpen requestData = SRequest_SetTaskOpen.load(data);
		SResponse_SetTaskOpen responseData = new SResponse_SetTaskOpen();
		DBHelper.update(T.task, DBHelper.set(T.task.open, requestData.open), T.task.id, requestData.taskId);
		success(response, responseData.saveToStr());
	}

	private static List<SPTask> sOpenTasks = null;

	public static void initRandomOpenTask() throws Exception
	{
		if (sOpenTasks == null)
		{
			LOG.v("initRandomOpenTask begin");

			StringBuilder sb = new StringBuilder();

			sb.append(String.format("select * from %s left join %s on %s.%s = %s.%s", T.task, //
					T.user, //
					T.task, T.task.sendUserId, //
					T.user, T.user.id));

			sb.append(String.format(" where %s = %s", T.task.open, 1));

			sb.append(String.format(";"));

			List<SPTask> openTasks = selectTaskList(sb.toString());

			String urlRoot = Config.getUrlRoot() + "/lys.tasks";
			for (SPTask task : openTasks)
			{
				SVNManager.updateTask(false, task.userId, task.id);
				File taskDir = SVNManager.getTaskDir(task.userId, task.id);
				File pagesetFile = new File(taskDir, "pageset.json");
				if (pagesetFile.exists())
				{
					SNotePageSet pageset = SNotePageSet.load(FsUtils.readText(pagesetFile));
					if (pageset.pages.size() > 0)
					{
						SNotePage page = pageset.pages.get(0);
						task.cover = urlRoot + "/" + task.userId + "/" + task.id + "/" + page.pageDir + "/small.jpg";
					}
				}
			}

			LOG.v("initRandomOpenTask end");

			sOpenTasks = openTasks;
		}
	}

	public static void RandomOpenTask(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_RandomOpenTask requestData = SRequest_RandomOpenTask.load(data);
		SResponse_RandomOpenTask responseData = new SResponse_RandomOpenTask();
		if (sOpenTasks != null)
		{
			Collections.shuffle(sOpenTasks);
			for (int i = 0; i < requestData.count && i < sOpenTasks.size(); i++)
			{
				responseData.tasks.add(sOpenTasks.get(i));
			}
			success(response, responseData.saveToStr());
		}
		else
		{
			error(response, SErrorCode.unknown_error, "未初始化完成");
		}
	}

}
