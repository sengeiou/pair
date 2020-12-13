package com.lys.servlet.process;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.config.Config;
import com.lys.protobuf.SErrorCode;
import com.lys.protobuf.SRequest_GetConfig;
import com.lys.protobuf.SRequest_GetOssToken;
import com.lys.protobuf.SResponse_GetConfig;
import com.lys.protobuf.SResponse_GetOssToken;
import com.lys.utils.OssToken;

public class ProcessConfig extends BaseProcess
{

	public static void GetConfig(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetConfig requestData = SRequest_GetConfig.load(data);
		SResponse_GetConfig responseData = new SResponse_GetConfig();
		responseData.urlRoot = Config.getUrlRoot();
		responseData.root = String.format("%s/%s", Config.URL_ROOT, Config.projectName);
		responseData.api = String.format("%s/%s/api", Config.URL_ROOT, Config.projectName);
		responseData.upload = String.format("%s/%s/upload", Config.URL_ROOT, Config.projectName);
//		responseData.server = "http://cloud.k12-eco.com:8070/api";
//		responseData.search = "http://cloud.k12-eco.com:8078/getResources";
		responseData.time = System.currentTimeMillis();
		responseData.svnUrl = Config.svnUrl;
		responseData.svnAccount = Config.svnAccount;
		responseData.svnPsw = Config.svnPsw;
		success(response, responseData.saveToStr());
	}

	public static void GetOssToken(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetOssToken requestData = SRequest_GetOssToken.load(data);
		SResponse_GetOssToken responseData = OssToken.requestToken(requestData.userId);
		if (responseData != null)
			success(response, responseData.saveToStr());
		else
			error(response, SErrorCode.unknown_error, "请求TOKEN失败");
	}

}
