package com.lys.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.CRC32;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

public class CommonUtils
{
	public static String getIndentStr(int indent)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; i++)
		{
			sb.append("\t");
		}
		return sb.toString();
	}

	// 获取内网IP地址
	public static String getHostIP()
	{
		String hostIp = null;
		try
		{
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			InetAddress ia = null;
			while (nis.hasMoreElements())
			{
				NetworkInterface ni = (NetworkInterface) nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				while (ias.hasMoreElements())
				{
					ia = ias.nextElement();
					if (ia instanceof Inet6Address)
					{
						continue; // skip ipv6
					}
					String ip = ia.getHostAddress();
					if (!"127.0.0.1".equals(ip))
					{
						hostIp = ip;
						break;
					}
				}
			}
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		return hostIp;
	}

	// 获取外网IP地址
	public static String getNetIp()
	{
		String netIp = null;
		try
		{
			URL url = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK)
			{
				InputStream inStream = httpConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null)
					sb.append(line + "\n");
				inStream.close();
				// 从反馈的结果中提取出IP地址
				int start = sb.indexOf("{");
				int end = sb.indexOf("}");
				String json = sb.substring(start, end + 1);
				if (json != null)
				{
					try
					{
						JSONObject jsonObject = JSONObject.parseObject(json, Feature.OrderedField);
						netIp = jsonObject.getString("cip");
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return netIp;
	}

//	public static String getLocalIp()
//	{
//		try
//		{
//			InetAddress addr = InetAddress.getLocalHost();
//			return addr.getHostAddress().toString();
//		}
//		catch (UnknownHostException e)
//		{
//			e.printStackTrace();
//		}
//		return "0.0.0.0";
//	}

	public static String uuid()
	{
		return UUID.randomUUID().toString();
	}

	public static long crc32(String str)
	{
		CRC32 crc32 = new CRC32();
		crc32.update(str.getBytes());
		return crc32.getValue();
	}

	public static String md5(byte[] bytes)
	{
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(bytes);
			byte[] data = md5.digest();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < data.length; i++)
			{
				int d = data[i];
				if (d < 0)
					d += 256;
				if (d < 16)
					buffer.append("0");
				buffer.append(Integer.toHexString(d));
			}
			return buffer.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public static String md5(String text)
	{
		byte[] bytes = text.getBytes(Charset.forName("utf-8"));
		return md5(bytes);
	}

	public static String md5(File file)
	{
		byte[] bytes = FsUtils.readBytes(file);
		return md5(bytes);
	}

	public static String base64Encode(byte[] bytes)
	{
		return Base64.getEncoder().encodeToString(bytes);
	}

	public static byte[] base64Decode(String str)
	{
		return Base64.getDecoder().decode(str);
	}

	public static String formatSize(double size)
	{
		if (size < 1024)
		{
			return (int) size + " 字节";
		}
		else if (size < 1024 * 1024)
		{
			String str = String.format("%.1f", size / 1024);
			if (str.endsWith(".0"))
				str = str.substring(0, str.length() - 2);
			return str + " KB";
		}
		else if (size < 1024 * 1024 * 1024)
		{
			String str = String.format("%.1f", size / (1024 * 1024));
			if (str.endsWith(".0"))
				str = str.substring(0, str.length() - 2);
			return str + " MB";
		}
		else
		{
			String str = String.format("%.1f", size / (1024 * 1024 * 1024));
			if (str.endsWith(".0"))
				str = str.substring(0, str.length() - 2);
			return str + " GB";
		}
	}

	public static String formatTime(long ms)
	{
		long second = ms / 1000;
		ms = ms % 1000;
		long minute = second / 60;
		second = second % 60;
		long hour = minute / 60;
		minute = minute % 60;
		long day = hour / 24;
		hour = hour % 24;
		if (day > 0)
			return String.format("%02d天 %02d:%02d:%02d:%03d", day, hour, minute, second, ms);
		else if (hour > 0)
			return String.format("%02d:%02d:%02d:%03d", hour, minute, second, ms);
		else if (minute > 0)
			return String.format("%02d:%02d:%03d", minute, second, ms);
		else
			return String.format("%02d:%03d", second, ms);
	}

	public static float[] rgb2hsb(int rgbR, int rgbG, int rgbB)
	{
		assert 0 <= rgbR && rgbR <= 255;
		assert 0 <= rgbG && rgbG <= 255;
		assert 0 <= rgbB && rgbB <= 255;
		int[] rgb = new int[] { rgbR, rgbG, rgbB };
		Arrays.sort(rgb);
		int max = rgb[2];
		int min = rgb[0];

		float hsbB = max / 255.0f;
		float hsbS = max == 0 ? 0 : (max - min) / (float) max;

		float hsbH = 0;
		if (max == rgbR && rgbG >= rgbB)
		{
			hsbH = (rgbG - rgbB) * 60f / (max - min) + 0;
		}
		else if (max == rgbR && rgbG < rgbB)
		{
			hsbH = (rgbG - rgbB) * 60f / (max - min) + 360;
		}
		else if (max == rgbG)
		{
			hsbH = (rgbB - rgbR) * 60f / (max - min) + 120;
		}
		else if (max == rgbB)
		{
			hsbH = (rgbR - rgbG) * 60f / (max - min) + 240;
		}

		return new float[] { hsbH, hsbS, hsbB };
	}

	public static int[] hsb2rgb(float h, float s, float v)
	{
		assert Float.compare(h, 0.0f) >= 0 && Float.compare(h, 360.0f) <= 0;
		assert Float.compare(s, 0.0f) >= 0 && Float.compare(s, 1.0f) <= 0;
		assert Float.compare(v, 0.0f) >= 0 && Float.compare(v, 1.0f) <= 0;

		float r = 0, g = 0, b = 0;
		int i = (int) ((h / 60) % 6);
		float f = (h / 60) - i;
		float p = v * (1 - s);
		float q = v * (1 - f * s);
		float t = v * (1 - (1 - f) * s);
		switch (i)
		{
		case 0:
			r = v;
			g = t;
			b = p;
			break;
		case 1:
			r = q;
			g = v;
			b = p;
			break;
		case 2:
			r = p;
			g = v;
			b = t;
			break;
		case 3:
			r = p;
			g = q;
			b = v;
			break;
		case 4:
			r = t;
			g = p;
			b = v;
			break;
		case 5:
			r = v;
			g = p;
			b = q;
			break;
		default:
			break;
		}
		return new int[] { (int) (r * 255.0), (int) (g * 255.0), (int) (b * 255.0) };
	}

}
