package com.lys.servlet.process;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.protobuf.SProtocol;
import com.lys.protobuf.SRequest_GetServerLog;
import com.lys.protobuf.SRequest_GetServerState;
import com.lys.protobuf.SRequest_GetServerUploadingList;
import com.lys.protobuf.SRequest_GetTimeRecord;
import com.lys.protobuf.SRequest_SetServerState;
import com.lys.protobuf.SResponse_GetServerLog;
import com.lys.protobuf.SResponse_GetServerState;
import com.lys.protobuf.SResponse_GetServerUploadingList;
import com.lys.protobuf.SResponse_GetTimeRecord;
import com.lys.protobuf.SResponse_SetServerState;
import com.lys.protobuf.SServerState;
import com.lys.protobuf.SServerUploading;
import com.lys.protobuf.STimeRecord;
import com.lys.servlet.upload;

public class ProcessRun
{
	public static void result(HttpServletResponse response, SProtocol trans)
	{
//		LOG.v("result:-----------------");
//		LOGJson.log(trans.saveToStr());
		try
		{
			PrintWriter pw = response.getWriter();
			pw.print(trans.saveToStr());
			pw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void success(HttpServletResponse response, String data)
	{
		SProtocol trans = new SProtocol();
		trans.code = 200;
		trans.data = data;
		result(response, trans);
	}

	public static void error(HttpServletResponse response, int errorCode, String errorMsg)
	{
		SProtocol trans = new SProtocol();
		trans.code = errorCode;
		trans.msg = errorMsg;
		result(response, trans);
	}

	public static final SServerState sServerState = new SServerState();

	private static final Object logLock = new Object();
	private static final Vector<String> sServerLogs = new Vector<String>();

	public static void addLog(String msg)
	{
		synchronized (logLock)
		{
			ProcessRun.sServerLogs.add(msg);
			if (ProcessRun.sServerLogs.size() > 3000)
				ProcessRun.sServerLogs.remove(0);
		}
	}

	public static void GetServerLog(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetServerLog requestData = SRequest_GetServerLog.load(data);
		SResponse_GetServerLog responseData = new SResponse_GetServerLog();
		responseData.logs = sServerLogs;
		String responseStr;
		synchronized (logLock)
		{
			responseStr = responseData.saveToStr();
		}
		success(response, responseStr);
	}

	public static void GetServerState(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetServerState requestData = SRequest_GetServerState.load(data);
		SResponse_GetServerState responseData = new SResponse_GetServerState();
		responseData.serverState = sServerState;
		success(response, responseData.saveToStr());
	}

	public static void SetServerState(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SetServerState requestData = SRequest_SetServerState.load(data);
		SResponse_SetServerState responseData = new SResponse_SetServerState();
		sServerState.stop = requestData.stop;
		responseData.stop = sServerState.stop;
		success(response, responseData.saveToStr());
	}

	public static final List<STimeRecord> timeRecords = new ArrayList<STimeRecord>();

	public static void GetTimeRecord(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetTimeRecord requestData = SRequest_GetTimeRecord.load(data);
		SResponse_GetTimeRecord responseData = new SResponse_GetTimeRecord();
		responseData.timeRecords = timeRecords;
		success(response, responseData.saveToStr());
	}

	public static void GetServerUploadingList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetServerUploadingList requestData = SRequest_GetServerUploadingList.load(data);
		SResponse_GetServerUploadingList responseData = new SResponse_GetServerUploadingList();
		responseData.uploadingList = upload.pathList();
		Collections.sort(responseData.uploadingList, new Comparator<SServerUploading>()
		{
			@Override
			public int compare(SServerUploading obj1, SServerUploading obj2)
			{
				return obj1.startTime.compareTo(obj2.startTime);
			}
		});
		responseData.uploadList = upload.sUploadRecords;
		success(response, responseData.saveToStr());
	}
}
