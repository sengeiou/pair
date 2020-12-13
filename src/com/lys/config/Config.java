package com.lys.config;

import java.io.File;

import com.lys.utils.CommonUtils;
import com.lys.utils.TextUtils;

public class Config
{
	public static final boolean isLinux;

	public static final String IP;
	public static final String URL_ROOT;

	public static final String projectName = "pair";
	public static final File tomcatDir;
	public static final File webappsDir;
	public static final File ROOTDir;
	public static final File rootFileDir;
	public static final File selfFileDir;
	public static final File fileDir;
//	public static final File logDir;
//	public static final File apkDir;
//	public static final File teachsDir;

	public static final String svnUrl;
	public static final String svnAccount;
	public static final String svnPsw;

	public static String convertIP(String ip)
	{
//		if (ip.equals("39.104.58.109"))
//			return "cloud.k12-eco.com";
		return ip;
	}

	static
	{
		String path = System.getProperty("catalina.home");
		if (TextUtils.isEmpty(path))
			path = "D:/catalina.home";
		tomcatDir = new File(path);
		webappsDir = new File(tomcatDir, "webapps");
		ROOTDir = new File(webappsDir, "ROOT");
		rootFileDir = new File(new File(ROOTDir, "files"), projectName);
		selfFileDir = new File(Config.class.getResource("").getPath()).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();

		isLinux = tomcatDir.toString().startsWith(File.separator);
		if (isLinux)
		{
			IP = CommonUtils.getNetIp();
			URL_ROOT = String.format("http://%s", convertIP(IP));
//			URL_ROOT = String.format("http://%s:8080", IP);
			fileDir = rootFileDir;

//			svnUrl = String.format("svn://%s", IP);
//			svnAccount = "guest";
//			svnPsw = "123456";

			svnUrl = String.format("http://120.24.162.51:81/svn/board");
			svnAccount = "any";
			svnPsw = "269543";
		}
		else
		{
			IP = CommonUtils.getHostIP();
			URL_ROOT = String.format("http://%s", IP);
			fileDir = selfFileDir;

			svnUrl = String.format("https://%s/svn/local", IP);
			svnAccount = "wangzhiting";
			svnPsw = "wangzhiting";
		}
//		logDir = new File(fileDir, "log");
//		apkDir = new File(fileDir, "apk");
//		teachsDir = new File(fileDir, "lys.tasks");
//		FsUtils.createDir(logDir);
//		FsUtils.createDir(apkDir);
//		FsUtils.createDir(teachsDir);
	}

	// 获取http根路径
	public static String getUrlRoot()
	{
		if (Config.isLinux)
			return Config.URL_ROOT + "/files/" + Config.projectName;
		else
			return Config.URL_ROOT + "/" + Config.projectName;
	}

	// 获取相对路径
	public static String getPath(File file)
	{
		if (isLinux)
			return file.toString().substring(fileDir.toString().length());
		else
			return file.toString().substring(fileDir.toString().length()).replace('\\', '/');
	}

	// 获取http全路径
	public static String Path2Url(File file)
	{
		if (isLinux)
			return file.toString().replace(ROOTDir.toString(), URL_ROOT);
		else
			return file.toString().replace(fileDir.toString(), URL_ROOT + "/" + projectName).replace('\\', '/');
	}

	private static String seriveIp = null;

	public static String getSeriveIp()
	{
		if (TextUtils.isEmpty(seriveIp))
			seriveIp = CommonUtils.getNetIp();
		return seriveIp;
	}

}
