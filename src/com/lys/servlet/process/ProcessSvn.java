package com.lys.servlet.process;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.protobuf.SErrorCode;
import com.lys.protobuf.SRequest_SvnGetDir;
import com.lys.protobuf.SResponse_SvnGetDir;
import com.lys.utils.SVNManager;

public class ProcessSvn extends BaseProcess
{
	public static void SvnGetDir(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SvnGetDir requestData = SRequest_SvnGetDir.load(data);
		SResponse_SvnGetDir responseData = new SResponse_SvnGetDir();
		responseData.objs = SVNManager.getDir(requestData.path);
		if (responseData.objs != null)
		{
			success(response, responseData.saveToStr());
		}
		else
		{
			error(response, SErrorCode.unknown_error, "获取SVN目录失败");
		}
	}
}
