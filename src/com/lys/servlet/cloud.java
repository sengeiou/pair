package com.lys.servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.lys.base.utils.JsonHelper;
import com.lys.config.Config;
import com.lys.config.Table;
import com.lys.protobuf.SNetPicInfo;
import com.lys.protobuf.SProtocol;
import com.lys.utils.FsUtils;
import com.lys.utils.HttpUtils;
import com.lys.utils.ImageUtil;
import com.lys.utils.LOG;
import com.lys.utils.LOGJson;
import com.lys.utils.OssUtils;

@WebServlet("/cloud")
@MultipartConfig
public class cloud extends HttpServlet
{
	private static final Object lock = new Object();

	private static final long serialVersionUID = 1L;

	private static final Table T = Table.instance;

	private static List<SNetPicInfo> netPics = null;
	private static Map<String, SNetPicInfo> netPicMap = new HashMap<String, SNetPicInfo>();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String md5 = request.getParameter("md5");
		String suffix = request.getParameter("suffix");

		File file = new File(Config.fileDir, "/插图/云空间/" + md5 + suffix);

//		File jsonFile = new File(Config.fileDir, "/插图/云空间/list.json");
		File jsonFileRaw = new File(Config.fileDir, "/插图/云空间/list.json.raw");

		boolean success = false;
		synchronized (lock)
		{
			try
			{
				if (netPics == null)
				{
					jsonFileRaw.delete();

					while (!jsonFileRaw.exists())
					{
						FsUtils.createDir(jsonFileRaw.getParentFile());
						HttpUtils.doDownload("http://zjyk-file.oss-cn-huhehaote.aliyuncs.com/插图/云空间/list.json.raw", jsonFileRaw);
					}

					netPics = SNetPicInfo.loadList(JsonHelper.getJSONArray(FsUtils.readText(jsonFileRaw)));
					for (SNetPicInfo netPic : netPics)
					{
						netPicMap.put(netPic.name + netPic.type, netPic);
					}
				}

				int pos = file.getName().lastIndexOf('.');
				String type = file.getName().substring(pos);
				String name = file.getName().substring(0, pos);

				if (netPicMap.containsKey(name + type))
				{
					SNetPicInfo netPic = netPicMap.get(name + type);
					netPics.remove(netPic);
					netPics.add(0, netPic);

					FsUtils.writeText(jsonFileRaw, SNetPicInfo.saveList(netPics).toString());

					String path = jsonFileRaw.toString().replace('\\', '/').substring(Config.fileDir.toString().length() + 1);
					LOG.v(path);
					OssUtils.doUploadUntil(OssUtils.ZjykFile, jsonFileRaw, path);

					success = true;
				}
				else
				{
					long fileSize = process(request, response, file);
					if (fileSize != -1)
					{
						File smallFile = new File(Config.fileDir, "/插图/云空间/small/" + name + ".png");
						FsUtils.createDir(smallFile.getParentFile());

						BufferedImage image = ImageUtil.readImage(file.toString());
						BufferedImage smallImage = ImageUtil.scaleImageMax(image, 200);
						ImageUtil.writeImage(smallImage, smallFile.toString());

						String rootUrl = "http://zjyk-file.oss-cn-huhehaote.aliyuncs.com/插图/云空间";

						SNetPicInfo netPic = new SNetPicInfo();
						netPic.isMovie = false;
						netPic.type = type;
						netPic.name = name;
						netPic.smallUrl = String.format("%s/small/%s", rootUrl, smallFile.getName());
						netPic.smallWidth = smallImage.getWidth();
						netPic.smallHeight = smallImage.getHeight();
						netPic.bigUrl = String.format("%s/%s", rootUrl, file.getName());
						netPic.bigWidth = image.getWidth();
						netPic.bigHeight = image.getHeight();
						netPics.add(0, netPic);
						netPicMap.put(netPic.name + netPic.type, netPic);

//						FsUtils.createDir(jsonFile.getParentFile());
						FsUtils.createDir(jsonFileRaw.getParentFile());

//						FsUtils.writeText(jsonFile, LOGJson.getStr(SNetPicInfo.saveList(netPics).toString()));
						FsUtils.writeText(jsonFileRaw, SNetPicInfo.saveList(netPics).toString());

						String path = file.toString().replace('\\', '/').substring(Config.fileDir.toString().length() + 1);
						LOG.v(path);
						OssUtils.doUploadUntil(OssUtils.ZjykFile, file, path);

						path = smallFile.toString().replace('\\', '/').substring(Config.fileDir.toString().length() + 1);
						LOG.v(path);
						OssUtils.doUploadUntil(OssUtils.ZjykFile, smallFile, path);

						path = jsonFileRaw.toString().replace('\\', '/').substring(Config.fileDir.toString().length() + 1);
						LOG.v(path);
						OssUtils.doUploadUntil(OssUtils.ZjykFile, jsonFileRaw, path);

						success = true;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (success)
		{
			SProtocol trans = new SProtocol();
			trans.code = 200;
//			trans.data = Config.Path2Url(file);
//			trans.data = validPath;
//			trans.msg = Config.getUrlRoot();
			trans.msg = "success";
			LOGJson.log(trans.saveToStr());
			PrintWriter pw = response.getWriter();
			pw.print(trans.saveToStr());
			pw.close();
		}
		else
		{
			SProtocol trans = new SProtocol();
			trans.code = 0;
//			trans.data = "";
			trans.msg = "error";
			LOGJson.log(trans.saveToStr());
			PrintWriter pw = response.getWriter();
			pw.print(trans.saveToStr());
			pw.close();
		}
	}

	private long process(HttpServletRequest request, HttpServletResponse response, File file) throws Exception
	{
		FsUtils.createDir(file.getParentFile());
		Part part = request.getPart("file");

		LOG.v("cloud upload : " + file);

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
	}

}
