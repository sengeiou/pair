package com.lys;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lys.config.StaticConfig;
import com.lys.protobuf.SEnglishWord;
import com.lys.protobuf.SEnglishWordExample;
import com.lys.utils.CommonUtils;
import com.lys.utils.FsUtils;
import com.lys.utils.HttpUtils;
import com.lys.utils.LOG;
import com.lys.utils.LOGJson;
import com.lys.utils.TextUtils;
import com.lys.utils.TimeDebug;

public class suxuewang
{
	public static String doHttpGet(String url)
	{
		LOG.v("-->doHttpGet : " + url);
		String ret = null;
		try
		{
			ret = HttpUtils.doHttpGet(url);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (TextUtils.isEmpty(ret))
			return doHttpGet(url);
		else
			return ret;
	}

	public static void download(String url, File file)
	{
		if (!file.exists())
		{
			LOG.v("-->download : " + url);
			byte[] bytes = null;
			try
			{
				bytes = HttpUtils.doHttpGetBytes(url);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			if (bytes == null || bytes.length == 0)
			{
				download(url, file);
			}
			else
			{
				FsUtils.writeBytes(file, bytes);
			}
		}
	}

	public static void catchMainPage()
	{
		for (int i = 51515;; i++)
		{
			String url = String.format("https://www.suxuewang.com/sortlist/%d.html", i);
			LOG.v(url);
			String text = doHttpGet(url);
			Matcher matcher = null;
			if ((matcher = Pattern.compile("<h1 class=\"unit_name\">([^<]+)</h1>").matcher(text)).find())
			{
				String bookName = matcher.group(1);
				LOG.v(bookName);
				File file = new File(String.format("C:/Users/xnktyu/Desktop/suxuewang/right_%d_%s.txt", i, bookName));
				FsUtils.writeText(file, text);
			}
			else
			{
				LOG.e("not find bookName");
				File file = new File(String.format("C:/Users/xnktyu/Desktop/suxuewang/error_%d.txt", i));
				FsUtils.writeText(file, text);
			}
		}
	}

	public static void moveRepeatBook()
	{
		Map<String, File> md5Pool = new HashMap<String, File>();

		File rootDir = new File("C:/Users/xnktyu/Desktop/suxuewang");

		File repeatDir = new File(rootDir, "repeat");
		FsUtils.createDir(repeatDir);

		File[] files = rootDir.listFiles();
		for (File file : files)
		{
			if (file.isFile())
			{
				String md5 = CommonUtils.md5(file);
				if (md5Pool.containsKey(md5))
				{
					File repeatFile = new File(repeatDir, file.getName());
					LOG.v("move to : " + repeatFile);
					file.renameTo(repeatFile);
				}
				else
				{
					md5Pool.put(md5, file);
				}
			}
		}

		LOG.v("md5Pool.size : " + md5Pool.size());
	}

	public static void moveBook(String bookName)
	{
		File rootDir = new File("C:/Users/xnktyu/Desktop/suxuewang");

		File bookDir = new File(rootDir, bookName);
		FsUtils.createDir(bookDir);

		File[] files = rootDir.listFiles();
		for (File file : files)
		{
			if (file.isFile())
			{
				if (file.getName().contains(bookName))
				{
					File bookFile = new File(bookDir, file.getName());
					LOG.v("move to : " + bookFile);
					file.renameTo(bookFile);
				}
			}
		}
	}

	// -------------------------------------------------------------------

	public static void catchBookDetail(Map<String, String> unitPool, List<SEnglishWord> words, File unitDir, String detailUrl, String number)
	{
		String text = null;

		File detailFile = new File(unitDir, String.format("detail_%s.txt", number));
		if (detailFile.exists())
		{
			text = FsUtils.readText(detailFile);
		}
		else
		{
			text = doHttpGet(detailUrl);
			FsUtils.writeText(detailFile, text);
		}

//		LOG.v(detailUrl + " --> " + detailFile + " : " + text.length());

		if (text.length() < 2000)
		{
			LOG.e("detail data is bad");
			return;
		}

		SEnglishWord item = new SEnglishWord();

		Matcher matcher = null;
		if ((matcher = Pattern.compile("<h1>单词：([^<]+)<span class=\"soundmark\">音标：\\[<font face=\"Lucida Sans Unicode\">([^<]*)</font>\\]</span></h1>").matcher(text)).find())
		{
			item.word = matcher.group(1).trim();
			item.mark = matcher.group(2).trim();
//			LOG.v(word);
//			LOG.v(mark);
			if (unitPool.containsKey(item.word))
			{
				LOG.e("repeat word : " + item.word + " -- " + unitPool.get(item.word));
				return;
			}
			else
			{
				unitPool.put(item.word, detailUrl);
			}
		}
		else
		{
			throw new RuntimeException("not find word and mark");
		}

		if ((matcher = Pattern.compile("<i class=\"fa fa-volume-up playico animate-hover\" data-src=\"(/resource/word/mp3/\\d+\\.mp3)\" title=\"点击播放").matcher(text)).find())
		{
			String audioUrl = "https://www.suxuewang.com" + matcher.group(1);
//			LOG.v(audioUrl);

			File audioDir = new File(unitDir, "audio");
			FsUtils.createDir(audioDir);

			File audioFile = new File(audioDir, item.word + ".mp3");
			download(audioUrl, audioFile);
		}
		else
		{
//			throw new RuntimeException("not find audioUrl");
		}

		if ((matcher = Pattern.compile("<span class=\"word_mean\"><b>(.*)").matcher(text)).find())
		{
			String str = matcher.group(1);
			if (str.endsWith("</b></span>"))
			{
				str = str.substring(0, str.length() - "</b></span>".length());
				if ((matcher = Pattern.compile("(\\w+|)(\\.|)(.+)").matcher(str)).find())
				{
					item.type = matcher.group(1).trim();
					item.mean = matcher.group(3).trim();
//					LOG.v(type);
//					LOG.v(mean);
				}
				else
				{
					throw new RuntimeException("not match type and mean : " + str);
				}
			}
			else
			{
				throw new RuntimeException("error str : " + str);
			}
		}
		else
		{
			throw new RuntimeException("not find type and mean");
		}

//		if ((matcher = Pattern.compile("<span class=\"word_mean\"><b>(\\w+|)(\\.|)([^<]+)</b></span>").matcher(text)).find())
//		{
//			String type = matcher.group(1);
//			String mean = matcher.group(3);
//			LOG.v(type);
//			LOG.v(mean);
//		}
//		else
//		{
//			throw new RuntimeException("not find type and mean");
//		}

		if ((matcher = Pattern.compile("<b class=\"memory_method\">联想方式：([^<]+)<br>记忆方法：([^<]+)</b>").matcher(text)).find())
		{
			item.think = matcher.group(1).trim();
			item.memory = matcher.group(2).trim();
//			LOG.v(think);
//			LOG.v(memory);
		}
		else
		{
//			throw new RuntimeException("not find think and memory");
		}

		String[] lines = text.split("\r\n");
		for (int i = 0; i < lines.length; i++)
		{
			String line = lines[i];
			if (line.trim().equals("<div class=\"lmemo exam-lmemo\">"))
			{
				String english = lines[i + 1];
				String explain = lines[i + 2];

				StringBuilder sb = new StringBuilder();
				matcher = Pattern.compile(">([^<]+)<").matcher(english);
				while (matcher.find())
				{
					sb.append(matcher.group(1));
				}
				english = sb.toString().trim();

				if ((matcher = Pattern.compile("<p>([^<]+)</p>").matcher(explain)).find())
				{
					explain = matcher.group(1).trim();
				}
				else
				{
//					throw new RuntimeException("explain is error : " + explain);
					explain = "";
				}

				if (!TextUtils.isEmpty(english) && !TextUtils.isEmpty(explain))
				{
					SEnglishWordExample example = new SEnglishWordExample();
					example.english = english;
					example.explain = explain;
					item.examples.add(example);
				}

//				LOG.v(english);
//				LOG.v(explain);
			}
		}

		words.add(item);
	}

	public static void catchBookList(File bookFile, String listUrl, String number)
	{
		String bookName = bookFile.getName();
		bookName = bookName.substring("right_".length());
		bookName = bookName.substring(bookName.indexOf("_") + 1);
		bookName = bookName.substring(0, bookName.length() - ".txt".length());
//		LOG.v(listUrl + " --> " + bookName);

		File bookDir = new File(bookFile.getParentFile(), bookName);
		FsUtils.createDir(bookDir);

		String text = null;

		File listFile = new File(bookDir, String.format("list_%s.txt", number));
		if (listFile.exists())
		{
			text = FsUtils.readText(listFile);
		}
		else
		{
			text = doHttpGet(listUrl);
			FsUtils.writeText(listFile, text);
		}

		String unitName = null;

		Matcher matcher = null;
		if ((matcher = Pattern.compile("<h2>(\\w+) </h2>").matcher(text)).find())
		{
			unitName = matcher.group(1);
			LOG.v(unitName);
		}
		else
		{
			throw new RuntimeException("not find unitName");
		}

		File unitDir = new File(bookDir, unitName);
		FsUtils.createDir(unitDir);

		Map<String, String> unitPool = new HashMap<String, String>();
		List<SEnglishWord> words = new ArrayList<>();

		matcher = Pattern.compile("<li class=\"see_details\"><span class=\"initial_btn\"><a href=\"/word_info-(\\d+)\\.html\" target=\"_blank\">查看详细</a></span></li>").matcher(text);
		while (matcher.find())
		{
			String detailUrl = "https://www.suxuewang.com/word_info-" + matcher.group(1) + ".html";
//			LOG.v(detailUrl);
			catchBookDetail(unitPool, words, unitDir, detailUrl, matcher.group(1));
//			break;
		}

		LOG.v("unitPool.size : " + unitPool.size());

		File jsonFile = new File(unitDir, "words.json");
		File jsonFileRaw = new File(unitDir, "words.json.raw");

		FsUtils.writeText(jsonFile, LOGJson.getStr(SEnglishWord.saveList(words).toString()));
		FsUtils.writeText(jsonFileRaw, SEnglishWord.saveList(words).toString());
	}

	public static void catchBookFile(File bookFile)
	{
		String text = FsUtils.readText(bookFile);
		Matcher matcher = null;
		matcher = Pattern.compile("<li style=\"float:right\"><a href=\"/word_initial-(\\d+)\\.html\" class=\"unit_li_vs\" target=\"_blank\">闪电学习</a></li>").matcher(text);
		while (matcher.find())
		{
			String listUrl = "https://www.suxuewang.com/word_initial-" + matcher.group(1) + ".html";
			catchBookList(bookFile, listUrl, matcher.group(1));
//			break;
		}
	}

	public static void catchBook(File bookDir)
	{
		File[] bookFiles = bookDir.listFiles();
		for (File bookFile : bookFiles)
		{
			if (bookFile.isFile())
			{
				LOG.v("bookFile : " + bookFile);
				catchBookFile(bookFile);
//				break;
			}
		}
	}

	public static void main(String[] args)
	{
		StaticConfig.isTomcat = false;

		TimeDebug.init();

		if (true)
		{
//			catchMainPage();
//			moveRepeatBook();
//			moveBook("高考高频词汇高中英语");

//			catchBook(new File("C:/Users/xnktyu/Desktop/suxuewang/高考高频词汇高中英语"));
//			catchBook(new File("C:/Users/xnktyu/Desktop/suxuewang/考试英语初中英语中考英语单词"));

			catchBook(new File("C:/Users/xnktyu/Desktop/suxuewang/人教版初中英语"));
			catchBook(new File("C:/Users/xnktyu/Desktop/suxuewang/人教版高中英语"));
		}

		TimeDebug.over("----- over -----");

		LOG.v("-------------------------------- process over --------------------------------");
	}
}