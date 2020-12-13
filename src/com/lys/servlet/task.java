package com.lys.servlet;

import java.io.BufferedReader;
import java.io.IOException;

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
import com.lys.servlet.process.ProcessRun;
import com.lys.servlet.process.ProcessTask;
import com.lys.utils.LOG;
import com.lys.utils.LOGJson;
import com.lys.utils.TextUtils;

@WebServlet("/task")
public class task extends HttpServlet
{
	private static final Object lock = new Object();

	private static final long serialVersionUID = 1L;

	public task()
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

			synchronized (lock)
			{
				requestRecord.startProcessTime = System.currentTimeMillis();

				LOG.v("receive:-----------------" + SHandleId.name(trans.handleId));
				LOGJson.log(requestStr);

				try
				{
					if (trans.handleId.equals(SHandleId.GetTaskForWeb))
						ProcessTask.GetTaskForWeb(request, trans.data, response);
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
		else
		{
			BaseProcess.error(response, SErrorCode.unknown_error, "没有参数");
		}
	}

}
