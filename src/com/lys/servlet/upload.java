package com.lys.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.lys.config.Config;
import com.lys.config.Table;
import com.lys.protobuf.SProtocol;
import com.lys.protobuf.SServerUpload;
import com.lys.protobuf.SServerUploading;
import com.lys.utils.FsUtils;
import com.lys.utils.LOG;
import com.lys.utils.LOGJson;
import com.lys.utils.TextUtils;

@WebServlet("/upload")
@MultipartConfig
public class upload extends HttpServlet
{
	private static final Object lock = new Object();

	private static final ConcurrentHashMap<String, Long> pathMap = new ConcurrentHashMap<String, Long>();

	private static final long serialVersionUID = 1L;

	private static final Table T = Table.instance;

	public static final Vector<SServerUpload> sUploadRecords = new Vector<SServerUpload>();

	public static boolean containsKey(String path)
	{
		synchronized (lock)
		{
			return pathMap.containsKey(path);
		}
	}

	public static List<SServerUploading> pathList()
	{
		List<SServerUploading> uploadingList = new ArrayList<SServerUploading>();
		synchronized (lock)
		{
			for (Entry<String, Long> entry : pathMap.entrySet())
			{
				SServerUploading uploading = new SServerUploading();
				uploading.path = entry.getKey();
				uploading.startTime = entry.getValue();
				uploadingList.add(uploading);
			}
		}
		return uploadingList;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String path = request.getParameter("path");
		String fullpath = request.getParameter("fullpath");

		File file = null;
		String validPath = null;

		if (!TextUtils.isEmpty(path))
		{
			file = new File(Config.fileDir, path);
			validPath = file.toString().substring(Config.fileDir.toString().length());
		}
		else if (!TextUtils.isEmpty(fullpath))
		{
			file = new File(fullpath);
			validPath = file.toString();
		}

		while (containsKey(validPath))
		{
			LOG.v("upload wait : " + validPath);
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		synchronized (lock)
		{
			pathMap.put(validPath, System.currentTimeMillis());
		}

		long fileSize = -1;
		try
		{
			fileSize = process(request, response, file);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		long startTime;
		synchronized (lock)
		{
			startTime = pathMap.remove(validPath);
		}

		SServerUpload serverUpload = new SServerUpload();
		serverUpload.path = validPath;
		serverUpload.startTime = startTime;
		serverUpload.endTime = System.currentTimeMillis();
		serverUpload.fileSize = fileSize;
		serverUpload.result = (fileSize != -1);
		sUploadRecords.add(serverUpload);

		if (fileSize != -1)
		{
			SProtocol trans = new SProtocol();
			trans.code = 200;
//			trans.data = Config.Path2Url(file);
			trans.data = validPath;
			trans.msg = Config.getUrlRoot();
			LOGJson.log(trans.saveToStr());
			PrintWriter pw = response.getWriter();
			pw.print(trans.saveToStr());
			pw.close();
		}
		else
		{
			SProtocol trans = new SProtocol();
			trans.code = 0;
			trans.data = "";
			trans.msg = "error";
			LOGJson.log(trans.saveToStr());
			PrintWriter pw = response.getWriter();
			pw.print(trans.saveToStr());
			pw.close();
		}
	}

	private long process(HttpServletRequest request, HttpServletResponse response, File file) throws Exception
	{
		if (file != null)
		{
			FsUtils.createDir(file.getParentFile());
			Part part = request.getPart("file");

			LOG.v("upload : " + file);

			File tmpFile = new File(file.getParentFile(), "~~" + file.getName());

			tmpFile.delete();
			file.delete();

			long fileSize = 0;

			InputStream is = part.getInputStream();
			FileOutputStream fos = new FileOutputStream(tmpFile);
			byte[] buffer = new byte[1024 * 16];
			int length = 0;
			while ((length = is.read(buffer)) > 0)
			{
				fos.write(buffer, 0, length);
				fileSize += length;
			}
			fos.close();
			is.close();

			tmpFile.renameTo(file);

			if (file.exists() && !tmpFile.exists())
			{
				return fileSize;
			}
			else
			{
				return -1;
			}

//			if (DBHelper.hasRecord(T.md5, T.md5.path, path))
//			{
//				DBHelper.update(T.md5, DBHelper.set(T.md5.md5, md5), T.md5.path, path);
//			}
//			else
//			{
//				DBHelper.insert(T.md5, //
//						T.md5.md5, md5, //
//						T.md5.path, path);
//			}
		}
		else
		{
			return -1;
		}
	}

}
