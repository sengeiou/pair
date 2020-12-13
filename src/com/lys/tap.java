package com.lys;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.lys.utils.FsUtils;
import com.lys.utils.LOG;
import com.lys.utils.TextUtils;

public class tap
{
	public static ArrayList<String> touchDown(int touchId, int x, int y)
	{
		LOG.v("down " + x + ", " + y);
		ArrayList<String> list = new ArrayList<>();
		list.add("sendevent /dev/input/event1 3 57" + touchId);
		list.add("sendevent /dev/input/event1 3 53 " + x);
		list.add("sendevent /dev/input/event1 3 54 " + y);
		list.add("sendevent /dev/input/event1 0 0 0");
		return list;
	}

	public static void touchMove(ArrayList<String> list, int x, int y)
	{
		LOG.v("move " + x + ", " + y);
		list.add("sendevent /dev/input/event1 3 53 " + x);
		list.add("sendevent /dev/input/event1 3 54 " + y);
		list.add("sendevent /dev/input/event1 0 0 0");
	}

	public static void touchUp(ArrayList<String> list)
	{
//		LOG.v("up " + x + ", " + y);
		list.add("sendevent /dev/input/event1 3 57 -1"); // 4294967295
		list.add("sendevent /dev/input/event1 0 0 0");
	}

	public static ArrayList<String> tap(int x, int y)
	{
		ArrayList<String> list = touchDown(174, x, y);
		touchUp(list);
		return list;
	}

	public static ArrayList<String> swipe(int x1, int y1, int x2, int y2)
	{
		ArrayList<String> list = touchDown(174, x1, y1);

		int xd = x2 - x1;
		int yd = y2 - y1;

		double distance = Math.sqrt(xd * xd + yd * yd);

		// 步数
		int step = (int) (distance / 10);

		// 步长
		int xs = xd / step;
		int ys = yd / step;

		for (int i = 1; i <= step; i++)
		{
			int x = x1 + i * xs;
			int y = y1 + i * ys;
			touchMove(list, x, y);
		}

		touchUp(list);
		return list;
	}

	public static void processEvent(File file)
	{
//		swipe(100, 100, 200, 200);
		String text = FsUtils.readText(file);
		String[] lines = text.split("\r\n");
		for (String line : lines)
		{
			line = line.trim();
			if (!TextUtils.isEmpty(line))
			{
				String[] parts = line.split(" ");

				line = parts[0].substring(0, parts[0].length() - 1) //
						+ " " + new BigInteger(parts[1], 16).intValue() //
						+ " " + new BigInteger(parts[2], 16).intValue() //
						+ " " + new BigInteger(parts[3], 16).intValue();
//				System.out.println("\"sendevent " + line + "\", //");
				if (line.contains("event4"))
				{
					if (line.endsWith(" 0 0 0"))
						System.out.println(String.format("list.add(\"sendevent %s\"); // ---------", line));
					else
						System.out.println(String.format("list.add(\"sendevent %s\");", line));
				}
			}
			else
			{
				System.out.println("");
			}
		}
	}

//	public static int to222Bit(int px)
//	{
//		return px & 0xffc0c0c0;
//	}
//
//	public static int to333Bit(int px)
//	{
//		return px & 0xffe0e0e0;
//	}

	public static int to444Bit(int px)
	{
		return px & 0xfff0f0f0;
	}

//	public static final HashMap<String, String> flagStrMap = new HashMap<String, String>();

//	public static final String flagStr = "F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F04070-F090A0-F0A0B0-F0A0B0-F0A0B0-F0A0B0-F0A0B0-F0C0D0-F0E0E0-F0E0E0-F0C0D0-F0A0B0-F0A0B0-F0A0B0-F0A0B0-F090B0-F080A0-E04060-F03050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-";

	// (1080, 1776)
	public static final String flagStr = "F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-F02050-";

//	public static String getFlagStr(int px, int count)
//	{
//		String key = String.format("%06X-%d", px & 0x00ffffff, count);
//		if (!flagStrMap.containsKey(key))
//		{
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < count; i++)
//			{
//				sb.append(String.format("%06X-", px & 0x00ffffff));
//			}
//			flagStrMap.put(key, sb.toString());
//			LOG.v(key + " : " + sb.toString());
//		}
//		return flagStrMap.get(key);
//	}

	public static void drawLine(Graphics g, Color color, int x1, int y1, int x2, int y2, int lineWidth)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color);
		g2.setStroke(new BasicStroke(lineWidth));
		g2.drawLine(x1, y1, x2, y2);
	}

	public static int findGuanZhu(File file, int x)
	{
		int findPos = -1;
		try
		{
			BufferedImage image = ImageIO.read(file);

//			if (true)
//			{
//				for (x = 0; x < image.getWidth(); x++)
//				{
//					for (int y = 0; y < image.getHeight(); y++)
//					{
//						int px = image.getRGB(x, y);
//						px = to444Bit(px);
//						image.setRGB(x, y, px);
//					}
//				}
//				ImageIO.write(image, "png", new File("C:/Users/wzt/Desktop/screen_out.png"));
//				return -1;
//			}

//			if (true)
//			{
//				int yfrom = 612;
//				int yto = 623;
//				StringBuilder sb = new StringBuilder();
//
//				for (int y = yfrom; y < yto; y++)
//				{
//					int px = image.getRGB(x, y);
//					px = to444Bit(px);
//					sb.append(String.format("%06X-", px & 0x00ffffff));
//				}
//
//				LOG.v(sb.toString());
//
//				drawLine(image.getGraphics(), Color.GREEN, x, yfrom, x, yto, 1);
//				ImageIO.write(image, "png", new File("C:/Users/wzt/Desktop/screen_out.png"));
//				return -1;
//			}

			int yfrom = 0;

			StringBuilder sb = new StringBuilder();

			for (int y = yfrom; y < image.getHeight(); y++)
			{
				int px = image.getRGB(x, y);
				px = to444Bit(px);
				sb.append(String.format("%06X-", px & 0x00ffffff));
			}

			LOG.v(sb.toString());

			int pos = sb.toString().indexOf(flagStr);

//			int pos = sb.toString().indexOf(getFlagStr(0xe0e0e0, flagLen));
//			if (pos == -1)
//			{
//				pos = sb.toString().indexOf(getFlagStr(0xe0e0e0, flagLen));
//			}

			if (pos >= 0)
			{
//				LOG.v("pos : " + pos);
				int index = pos / 7;
//				LOG.v("index : " + index);
				findPos = yfrom + index;
				LOG.v("findPos : " + findPos);
				drawLine(image.getGraphics(), Color.GREEN, x, findPos, x, findPos + flagStr.length() / 7, 1);
			}

			ImageIO.write(image, "png", new File("C:/Users/wzt/Desktop/screen_out.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return findPos;
	}

	public static ArrayList<Integer> findList(File file, int x)
	{
		ArrayList<Integer> findPosList = new ArrayList<Integer>();
		try
		{
			BufferedImage image = ImageIO.read(file);

//			if (true)
//			{
//				int yfrom = 640;
//				int yto = 718;
//				StringBuilder sb = new StringBuilder();
//
//				for (int y = yfrom; y < yto; y++)
//				{
//					int px = image.getRGB(x, y);
//					px = to444Bit(px);
//					sb.append(String.format("%06X-", px & 0x00ffffff));
//				}
//
//				LOG.v(sb.toString());
//
//				drawLine(image.getGraphics(), Color.GREEN, x, yfrom, x, yto, 1);
//				ImageIO.write(image, "png", new File("C:/Users/wzt/Desktop/screen_out.png"));
//				return null;
//			}

			StringBuilder sb = new StringBuilder();

			for (int y = 0; y < image.getHeight(); y++)
			{
				int px = image.getRGB(x, y);
				px = to444Bit(px);
				sb.append(String.format("%06X-", px & 0x00ffffff));
			}

			LOG.v(sb.toString());

			String str = sb.toString();
			int yBase = 0;
			while (true)
			{
				int pos = str.indexOf(flagStr);
				if (pos >= 0)
				{
//					LOG.v("pos : " + pos);
					int index = pos / 7;
//					LOG.v("index : " + index);
					int findPos = yBase + index;
					LOG.v("findPos : " + findPos);
					findPosList.add(findPos);
					str = str.substring(pos + flagStr.length());
					yBase += (index + flagStr.length() / 7);
					drawLine(image.getGraphics(), Color.GREEN, x, findPos, x, findPos + flagStr.length() / 7, 1);
				}
				else
				{
					break;
				}
			}

			ImageIO.write(image, "png", new File("C:/Users/wzt/Desktop/screen_out.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return findPosList;
	}

	public static void main(String[] args)
	{
//		processEvent(new File("C:/Users/wzt/Desktop/event.txt"));
//		findGuanZhu(new File("C:/Users/wzt/Desktop/screen.png"), 660);
//		findGuanZhu(new File("C:/Users/wzt/Desktop/screen.png"), 988);
//		findGuanZhu(new File("C:/Users/wzt/Desktop/screen.png"), 1008);
//		findList(new File("C:/Users/wzt/Desktop/screen.png"), 1004);
		LOG.v("-------------------------------- process over --------------------------------");
	}
}