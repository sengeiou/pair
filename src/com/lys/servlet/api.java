package com.lys.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.protobuf.SErrorCode;
import com.lys.protobuf.SHandleId;
import com.lys.protobuf.SProtocol;
import com.lys.protobuf.SRequestRecord;
import com.lys.servlet.process.BaseProcess;
import com.lys.servlet.process.ProcessApp;
import com.lys.servlet.process.ProcessConfig;
import com.lys.servlet.process.ProcessEvent;
import com.lys.servlet.process.ProcessFile;
import com.lys.servlet.process.ProcessFriend;
import com.lys.servlet.process.ProcessLive;
import com.lys.servlet.process.ProcessRun;
import com.lys.servlet.process.ProcessScore;
import com.lys.servlet.process.ProcessShop;
import com.lys.servlet.process.ProcessSvn;
import com.lys.servlet.process.ProcessTask;
import com.lys.servlet.process.ProcessTeachRecord;
import com.lys.servlet.process.ProcessTest;
import com.lys.servlet.process.ProcessTopic;
import com.lys.servlet.process.ProcessTopicRecord;
import com.lys.servlet.process.ProcessUser;
import com.lys.servlet.process.ProcessZhiXue;
import com.lys.utils.LOG;
import com.lys.utils.TextUtils;

@WebServlet("/api")
public class api extends HttpServlet
{
	private static final Object lock = new Object();

	private static final long serialVersionUID = 1L;

	private static final Map<Integer, Integer> withLockMap = new HashMap<Integer, Integer>();

	static
	{
		LOG.v("api static init");

		withLockMap.put(SHandleId.AddUser, 0);
		withLockMap.put(SHandleId.DeleteUser, 0);

		withLockMap.put(SHandleId.AddFriend, 0);
		withLockMap.put(SHandleId.DeleteFriend, 0);

		withLockMap.put(SHandleId.RandomOpenTask, 0);

		withLockMap.put(SHandleId.TeachStart, 0);
	}

	public api()
	{
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		if (ProcessRun.sServerState.stop)
		{
			BaseProcess.error(response, SErrorCode.unknown_error, "服务器已暂停");
			return;
		}

		SRequestRecord requestRecord = new SRequestRecord();
		requestRecord.entryTime = System.currentTimeMillis();
		ProcessRun.sServerState.requestRecords.add(requestRecord);

		BufferedReader br = request.getReader();
		StringBuilder sb = new StringBuilder();
		String str;
		while ((str = br.readLine()) != null)
		{
			sb.append(str);
		}
		String requestStr = sb.toString();

		if (!TextUtils.isEmpty(requestStr))
		{
			SProtocol trans = SProtocol.load(requestStr);

			requestRecord.handleId = trans.handleId;
			requestRecord.handleName = SHandleId.name(trans.handleId);

			requestRecord.readyTime = System.currentTimeMillis();

			if (withLockMap.containsKey(trans.handleId))
			{
				synchronized (lock)
				{
					processImpl(request, response, trans, requestRecord);
				}
			}
			else
			{
				processImpl(request, response, trans, requestRecord);
			}
		}
		else
		{
			BaseProcess.error(response, SErrorCode.unknown_error, "没有参数");
		}
	}

	private void processImpl(HttpServletRequest request, HttpServletResponse response, SProtocol trans, SRequestRecord requestRecord) throws ServletException, IOException
	{
		requestRecord.startProcessTime = System.currentTimeMillis();

		LOG.v(String.format("receive:------ %s ---- %s ----- %s ------", SHandleId.name(trans.handleId), trans.userId, trans.userName));
//		LOGJson.log(requestStr);

		try
		{
			if (trans.handleId.equals(SHandleId.Test))
				ProcessTest.Test(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.GetConfig))
				ProcessConfig.GetConfig(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetOssToken))
				ProcessConfig.GetOssToken(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.GetAppInfoList))
				ProcessApp.GetAppInfoList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetAppInfo))
				ProcessApp.GetAppInfo(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.SetAppInfo))
				ProcessApp.SetAppInfo(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.UserPhoneCode))
				ProcessUser.UserPhoneCode(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.UserReg))
				ProcessUser.UserReg(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.UserLogin))
				ProcessUser.UserLogin(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetUser))
				ProcessUser.GetUser(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ModifyHead))
				ProcessUser.ModifyHead(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ModifyName))
				ProcessUser.ModifyName(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ModifySex))
				ProcessUser.ModifySex(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ModifyGrade))
				ProcessUser.ModifyGrade(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ModifyPsw))
				ProcessUser.ModifyPsw(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetUserList))
				ProcessUser.GetUserList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.AddUser))
				ProcessUser.AddUser(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.DeleteUser))
				ProcessUser.DeleteUser(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.SetVip))
				ProcessUser.SetVip(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.SetCp))
				ProcessUser.SetCp(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.GetFriendList))
				ProcessFriend.GetFriendList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.AddFriend))
				ProcessFriend.AddFriend(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.DeleteFriend))
				ProcessFriend.DeleteFriend(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ModifyFriendGroup))
				ProcessFriend.ModifyFriendGroup(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.FindTask))
				ProcessTask.FindTask(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetTask))
				ProcessTask.GetTask(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetTaskList))
				ProcessTask.GetTaskList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.CreateTask))
				ProcessTask.CreateTask(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.SendTask))
				ProcessTask.SendTask(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.DeleteTask))
				ProcessTask.DeleteTask(request, trans.data, response);
//			else if (trans.handleId.equals(SHandleId.GetTaskFileVersion))
//			ProcessTask.GetTaskFileVersion(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.SetTaskState))
				ProcessTask.SetTaskState(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.SetTaskNote))
				ProcessTask.SetTaskNote(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ModifyTask))
				ProcessTask.ModifyTask(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ModifyTaskComment))
				ProcessTask.ModifyTaskComment(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.AddTaskScore))
				ProcessTask.AddTaskScore(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.SetTaskOpen))
				ProcessTask.SetTaskOpen(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.RandomOpenTask))
				ProcessTask.RandomOpenTask(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.TopicRecordGetList))
				ProcessTopicRecord.TopicRecordGetList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.TopicRecordGet))
				ProcessTopicRecord.TopicRecordGet(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.TopicRecordSetFav))
				ProcessTopicRecord.TopicRecordSetFav(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.TopicRecordSetResult))
				ProcessTopicRecord.TopicRecordSetResult(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.TopicRecordDelete))
				ProcessTopicRecord.TopicRecordDelete(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.TeachStart))
				ProcessTeachRecord.TeachStart(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.TeachOverByTeacher))
				ProcessTeachRecord.TeachOverByTeacher(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.TeachQuestionByTeacher))
				ProcessTeachRecord.TeachQuestionByTeacher(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.TeachOverByStudent))
				ProcessTeachRecord.TeachOverByStudent(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.TeachConfirmByStudent))
				ProcessTeachRecord.TeachConfirmByStudent(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.TeachQuestionByStudent))
				ProcessTeachRecord.TeachQuestionByStudent(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.TeachGetList))
				ProcessTeachRecord.TeachGetList(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.GetMatterList))
				ProcessShop.GetMatterList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.AddModifyMatter))
				ProcessShop.AddModifyMatter(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.SwapMatter))
				ProcessShop.SwapMatter(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.DeleteMatter))
				ProcessShop.DeleteMatter(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetBuyList))
				ProcessShop.GetBuyList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.AddModifyBuy))
				ProcessShop.AddModifyBuy(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.DeleteBuy))
				ProcessShop.DeleteBuy(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetCommentList))
				ProcessShop.GetCommentList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.AddModifyComment))
				ProcessShop.AddModifyComment(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.DeleteComment))
				ProcessShop.DeleteComment(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.GetGoodsList))
				ProcessScore.GetGoodsList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.AddModifyGoods))
				ProcessScore.AddModifyGoods(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.SwapGoods))
				ProcessScore.SwapGoods(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.DeleteGoods))
				ProcessScore.DeleteGoods(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetOrderList))
				ProcessScore.GetOrderList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.AddOrder))
				ProcessScore.AddOrder(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ModifyOrderState))
				ProcessScore.ModifyOrderState(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.GetTaskGroupList))
				ProcessScore.GetTaskGroupList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.AddModifyTaskGroup))
				ProcessScore.AddModifyTaskGroup(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.SwapTaskGroup))
				ProcessScore.SwapTaskGroup(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.DeleteTaskGroup))
				ProcessScore.DeleteTaskGroup(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.GetTeachList))
				ProcessScore.GetTeachList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ModifyTeach))
				ProcessScore.ModifyTeach(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.AddEvent))
				ProcessEvent.AddEvent(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetEventList))
				ProcessEvent.GetEventList(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.LiveGetAll))
				ProcessLive.LiveGetAll(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.LiveGetList))
				ProcessLive.LiveGetList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.LiveAddModify))
				ProcessLive.LiveAddModify(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.LiveDelete))
				ProcessLive.LiveDelete(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.FileDelete))
				ProcessFile.FileDelete(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.FileList))
				ProcessFile.FileList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.FileExists))
				ProcessFile.FileExists(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.FileCopy))
				ProcessFile.FileCopy(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.SearchTopics))
				ProcessTopic.SearchTopics(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetTopicStyles))
				ProcessTopic.GetTopicStyles(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.GetKnowledges))
				ProcessTopic.GetKnowledges(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.SvnGetDir))
				ProcessSvn.SvnGetDir(request, trans.data, response);

			else if (trans.handleId.equals(SHandleId.ZXCreateTask))
				ProcessZhiXue.ZXCreateTask(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXPullTask))
				ProcessZhiXue.ZXPullTask(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXCatchPageOver))
				ProcessZhiXue.ZXCatchPageOver(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXCatchOver))
				ProcessZhiXue.ZXCatchOver(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXGenKnowledgeTree))
				ProcessZhiXue.ZXGenKnowledgeTree(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXGenChapterTree))
				ProcessZhiXue.ZXGenChapterTree(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXAddTopic))
				ProcessZhiXue.ZXAddTopic(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXPullAccount))
				ProcessZhiXue.ZXPullAccount(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXReportAccount))
				ProcessZhiXue.ZXReportAccount(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXDeviceList))
				ProcessZhiXue.ZXDeviceList(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXTickInfo))
				ProcessZhiXue.ZXTickInfo(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXProcessJuan))
				ProcessZhiXue.ZXProcessJuan(request, trans.data, response);
			else if (trans.handleId.equals(SHandleId.ZXProcessJuan2))
				ProcessZhiXue.ZXProcessJuan2(request, trans.data, response);

			else
				BaseProcess.error(response, SErrorCode.unknown_error, trans.handleId + " unregistered !!!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		requestRecord.overProcessTime = System.currentTimeMillis();
	}

}
