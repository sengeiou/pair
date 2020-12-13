package com.lys.servlet.process;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.config.Config;
import com.lys.protobuf.SFilePath;
import com.lys.protobuf.SRequest_FileCopy;
import com.lys.protobuf.SRequest_FileDelete;
import com.lys.protobuf.SRequest_FileExists;
import com.lys.protobuf.SRequest_FileList;
import com.lys.protobuf.SResponse_FileCopy;
import com.lys.protobuf.SResponse_FileDelete;
import com.lys.protobuf.SResponse_FileExists;
import com.lys.protobuf.SResponse_FileList;
import com.lys.utils.CommonUtils;
import com.lys.utils.FsUtils;

public class ProcessFile extends BaseProcess
{
	public static void FileDelete(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_FileDelete requestData = SRequest_FileDelete.load(data);
		SResponse_FileDelete responseData = new SResponse_FileDelete();
		for (String path : requestData.paths)
		{
			File file = new File(Config.fileDir, path);
			FsUtils.delete(file);
			FsUtils.deleteDirectoryIfEmpty(file.getParentFile());
//			DBHelper.delete(T.md5, T.md5.path, path);
		}
		success(response, responseData.saveToStr());
	}

	public static void FileList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_FileList requestData = SRequest_FileList.load(data);
		SResponse_FileList responseData = new SResponse_FileList();
		responseData.root = Config.getUrlRoot();
		File file = new File(Config.fileDir, requestData.path);
		for (File f : FsUtils.searchFiles(file))
		{
			SFilePath path = new SFilePath();
			path.path = f.getAbsolutePath().substring(Config.fileDir.getAbsolutePath().length());
			if (!Config.isLinux)
			{
				path.path = path.path.replace('\\', '/');
			}
			path.md5 = CommonUtils.md5(f);
//			DBHelper.select(T.md5, new OnCallback()
//			{
//				@Override
//				public void onResult(ResultSet rs) throws SQLException
//				{
//					if (rs.next())
//					{
//						path.md5 = rs.getString(T.md5.md5);
//					}
//				}
//			}, T.md5.path, path.path);
			responseData.paths.add(path);
		}
		success(response, responseData.saveToStr());
	}

	public static void FileExists(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_FileExists requestData = SRequest_FileExists.load(data);
		SResponse_FileExists responseData = new SResponse_FileExists();
		File file = new File(Config.fileDir, requestData.path);
		responseData.exists = file.exists();
		success(response, responseData.saveToStr());
	}

	public static void FileCopy(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_FileCopy requestData = SRequest_FileCopy.load(data);
		SResponse_FileCopy responseData = new SResponse_FileCopy();
		File srcFile = new File(Config.fileDir, requestData.srcPath);
		File dstFile = new File(Config.fileDir, requestData.dstPath);
		FsUtils.copyPath(srcFile, dstFile, true);
		success(response, responseData.saveToStr());
	}
}
