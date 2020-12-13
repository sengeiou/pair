package com.lys.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lys.servlet.process.ProcessRun;

public class LOG
{
	private static final Object lock = new Object();

	private static final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat formatTime = new SimpleDateFormat("MM-dd HH:mm:ss");

//	static
//	{
//		if (StaticConfig.isTomcat)
//		{
//			if (Config.isLinux)
//			{
//				try
//				{
//					Date date = new Date();
//					String dateStr = formatDate.format(date);
//
//					File logFile = new File(Config.logDir, String.format("%s.log", dateStr));
//					PrintStream printStream = new PrintStream(new FileOutputStream(logFile, true), true);
//					System.setOut(printStream);
//					System.setErr(printStream);
//				}
//				catch (FileNotFoundException e)
//				{
//				}
//			}
//			else
//			{
//				System.setErr(System.out);
//			}
//		}
//	}

	public static void log(String msg)
	{
		synchronized (lock)
		{
			System.out.println(msg);
		}
	}

	public static void err(String msg)
	{
		synchronized (lock)
		{
			System.err.println(msg);
		}
	}

	public static String getProcessId()
	{
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String name = runtime.getName();
		return name.substring(0, name.indexOf("@"));
	}

	public static void v(String msg)
	{
		synchronized (lock)
		{
			Date date = new Date();
			String timeStr = formatTime.format(date);
			long ms = date.getTime() % 1000;
			long threadId = Thread.currentThread().getId();
			System.out.println(String.format("-------pair------%s.%03d : %s : %d : %s", timeStr, ms, getProcessId(), threadId, msg));
			ProcessRun.addLog(String.format("%s.%03d : %s : %d : %s", timeStr, ms, getProcessId(), threadId, msg));
		}
	}

	public static void e(String msg)
	{
		synchronized (lock)
		{
			Date date = new Date();
			String timeStr = formatTime.format(date);
			long ms = date.getTime() % 1000;
			long threadId = Thread.currentThread().getId();
			System.err.println(String.format("-------pair------%s.%03d : %s : %d : %s", timeStr, ms, getProcessId(), threadId, msg));
			ProcessRun.addLog(String.format("%s.%03d : %s : %d : %s", timeStr, ms, getProcessId(), threadId, msg));
		}
	}

}
