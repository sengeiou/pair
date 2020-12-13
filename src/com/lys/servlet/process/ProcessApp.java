package com.lys.servlet.process;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.SErrorCode;
import com.lys.protobuf.SRequest_GetAppInfo;
import com.lys.protobuf.SRequest_GetAppInfoList;
import com.lys.protobuf.SRequest_SetAppInfo;
import com.lys.protobuf.SResponse_GetAppInfo;
import com.lys.protobuf.SResponse_GetAppInfoList;
import com.lys.protobuf.SResponse_SetAppInfo;

public class ProcessApp extends BaseProcess
{
	public static void GetAppInfoList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetAppInfoList requestData = SRequest_GetAppInfoList.load(data);
		SResponse_GetAppInfoList responseData = new SResponse_GetAppInfoList();
		DBHelper.select(T.app, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					responseData.apps.add(packAppInfo(rs));
				}
			}
		}, T.app.channel, requestData.channel);
		success(response, responseData.saveToStr());
	}

	public static void GetAppInfo(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetAppInfo requestData = SRequest_GetAppInfo.load(data);
		SResponse_GetAppInfo responseData = new SResponse_GetAppInfo();
		DBHelper.select(T.app, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				if (rs.next())
				{
					responseData.app = packAppInfo(rs);
				}
			}
		}, T.app.pkgName, requestData.pkgName, T.app.channel, requestData.channel);
		success(response, responseData.saveToStr());
	}

	public static void SetAppInfo(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SetAppInfo requestData = SRequest_SetAppInfo.load(data);
		SResponse_SetAppInfo responseData = new SResponse_SetAppInfo();
		if (requestData.app == null)
		{
			error(response, SErrorCode.unknown_error, "错误");
			return;
		}
		if (DBHelper.hasRecord(T.app, T.app.pkgName, requestData.app.pkgName, T.app.channel, requestData.app.channel))
		{
			DBHelper.update(T.app, DBHelper.set(//
					T.app.versionCode, requestData.app.versionCode, //
					T.app.versionName, requestData.app.versionName, //
					T.app.probability, requestData.app.probability, //
					T.app.name, requestData.app.name, //
					T.app.size, requestData.app.size, //
					T.app.apkUrl, requestData.app.apkUrl, //
					T.app.icoUrl, requestData.app.icoUrl, //
					T.app.des, requestData.app.des), //
					T.app.pkgName, requestData.app.pkgName, T.app.channel, requestData.app.channel);
		}
		else
		{
			DBHelper.insert(T.app, //
					T.app.pkgName, requestData.app.pkgName, //
					T.app.channel, requestData.app.channel, //
					T.app.versionCode, requestData.app.versionCode, //
					T.app.versionName, requestData.app.versionName, //
					T.app.probability, requestData.app.probability, //
					T.app.name, requestData.app.name, //
					T.app.size, requestData.app.size, //
					T.app.apkUrl, requestData.app.apkUrl, //
					T.app.icoUrl, requestData.app.icoUrl, //
					T.app.des, requestData.app.des);
		}
		success(response, responseData.saveToStr());
	}
}
