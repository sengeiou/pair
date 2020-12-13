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
import com.lys.servlet.process.BaseProcess;
import com.lys.servlet.process.ProcessRun;
import com.lys.utils.TextUtils;

@WebServlet("/run")
public class run extends HttpServlet
{
	private static final Object lock = new Object();

	private static final long serialVersionUID = 1L;

	public run()
	{
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		synchronized (lock)
		{
			process(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		synchronized (lock)
		{
			process(request, response);
		}
	}

	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

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
//			LOG.v("receive:-----------------" + SHandleId.name(trans.handleId));
//			LOGJson.log(requestStr);

			try
			{
				if (trans.handleId.equals(SHandleId.GetServerLog))
					ProcessRun.GetServerLog(request, trans.data, response);
				else if (trans.handleId.equals(SHandleId.GetServerState))
					ProcessRun.GetServerState(request, trans.data, response);
				else if (trans.handleId.equals(SHandleId.SetServerState))
					ProcessRun.SetServerState(request, trans.data, response);
				else if (trans.handleId.equals(SHandleId.GetTimeRecord))
					ProcessRun.GetTimeRecord(request, trans.data, response);
				else if (trans.handleId.equals(SHandleId.GetServerUploadingList))
					ProcessRun.GetServerUploadingList(request, trans.data, response);
				else
					BaseProcess.error(response, SErrorCode.unknown_error, trans.handleId + " unregistered !!!");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			BaseProcess.error(response, SErrorCode.unknown_error, "没有参数");
		}
	}

}
