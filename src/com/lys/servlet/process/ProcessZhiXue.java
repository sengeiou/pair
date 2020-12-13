package com.lys.servlet.process;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.client;
import com.lys.zhixue;
import com.lys.config.AppConfig;
import com.lys.config.Config;
import com.lys.manager.TopicManager;
import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.SChapter;
import com.lys.protobuf.SErrorCode;
import com.lys.protobuf.SHandleId;
import com.lys.protobuf.SKnowledge;
import com.lys.protobuf.SPTask;
import com.lys.protobuf.SRequest_SearchTopics;
import com.lys.protobuf.SRequest_ZXAddTopic;
import com.lys.protobuf.SRequest_ZXCatchOver;
import com.lys.protobuf.SRequest_ZXCatchPageOver;
import com.lys.protobuf.SRequest_ZXCreateTask;
import com.lys.protobuf.SRequest_ZXDeviceList;
import com.lys.protobuf.SRequest_ZXGenChapterTree;
import com.lys.protobuf.SRequest_ZXGenKnowledgeTree;
import com.lys.protobuf.SRequest_ZXProcessJuan;
import com.lys.protobuf.SRequest_ZXProcessJuan2;
import com.lys.protobuf.SRequest_ZXPullAccount;
import com.lys.protobuf.SRequest_ZXPullTask;
import com.lys.protobuf.SRequest_ZXReportAccount;
import com.lys.protobuf.SRequest_ZXTickInfo;
import com.lys.protobuf.SResponse_SearchTopics;
import com.lys.protobuf.SResponse_ZXAddTopic;
import com.lys.protobuf.SResponse_ZXCatchOver;
import com.lys.protobuf.SResponse_ZXCatchPageOver;
import com.lys.protobuf.SResponse_ZXCreateTask;
import com.lys.protobuf.SResponse_ZXDeviceList;
import com.lys.protobuf.SResponse_ZXGenChapterTree;
import com.lys.protobuf.SResponse_ZXGenKnowledgeTree;
import com.lys.protobuf.SResponse_ZXProcessJuan;
import com.lys.protobuf.SResponse_ZXProcessJuan2;
import com.lys.protobuf.SResponse_ZXPullAccount;
import com.lys.protobuf.SResponse_ZXPullTask;
import com.lys.protobuf.SResponse_ZXReportAccount;
import com.lys.protobuf.SResponse_ZXTickInfo;
import com.lys.protobuf.STopic;
import com.lys.protobuf.SZXAccount;
import com.lys.protobuf.SZXChapterTreeNode;
import com.lys.protobuf.SZXDeviceInfo;
import com.lys.protobuf.SZXKnowledgeTreeNode;
import com.lys.protobuf.SZXTask;
import com.lys.protobuf.SZXTopic;
import com.lys.utils.CommonUtils;
import com.lys.utils.FsUtils;
import com.lys.utils.LOG;
import com.lys.utils.LOGJson;
import com.lys.utils.SVNManager;
import com.lys.utils.TextUtils;

public class ProcessZhiXue extends BaseProcess
{
	private static boolean startProcessImpl()
	{
		File rootDir = new File(Config.fileDir, "catch_zhixue");
		File[] subjectDirs = rootDir.listFiles();
		for (File subjectDir : subjectDirs)
		{
			if (subjectDir.isDirectory())
			{
				File[] taskDirs = subjectDir.listFiles();
				for (File taskDir : taskDirs)
				{
					if (taskDir.isDirectory() && taskDir.getName().startsWith("over_"))
					{
						LOG.v("process : " + taskDir);
						boolean success = zhixue.process(taskDir, false);
						if (success)
						{
							File taskDirFixed = new File(taskDir.getParentFile(), taskDir.getName().replace("over_", "fixed_"));
							LOG.v("rename to : " + taskDirFixed.toString());
							taskDir.renameTo(taskDirFixed);
						}
						else
						{
							File taskDirError = new File(taskDir.getParentFile(), taskDir.getName().replace("over_", "error_"));
							LOG.v("rename to : " + taskDirError.toString());
							taskDir.renameTo(taskDirError);
						}
						return success;
					}
				}
			}
		}
		return true;
	}

	public static void startProcess()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				LOG.v("---------- zhixue process thread start ----------");
				while (true)
				{
					LOG.v("---------- zhixue process ready loop ..... ----------");
					try
					{
						Thread.sleep(6 * 1000);
					}
					catch (Exception e)
					{
					}
					boolean success = startProcessImpl();
					LOG.v("---------- zhixue process loop over  ---------- : " + success);
//					if (success)
//					{
					try
					{
						Thread.sleep(20 * 1000);
					}
					catch (Exception e)
					{
					}
//					}
//					else
//					{
//						break;
//					}
				}
//				LOG.e("---------- zhixue process thread has error !!!!! ----------");
			}
		}).start();
	}

	public static void tmpProcessOver()
	{
		final List<SZXTask> tasks = new ArrayList<>();
		DBHelper.select(T.zhixue, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					tasks.add(packZXTask(rs));
				}
			}
		}, T.zhixue.deviceId, "over");
		for (SZXTask task : tasks)
		{
			File fileOld = new File(Config.fileDir, String.format("/catch_zhixue/%s_%s_%s/%s_%s_%s", task.phase, task.subject, task.material, task.diff, task.area, task.year));
			File fileNew = new File(Config.fileDir, String.format("/catch_zhixue/%s_%s_%s/over_%s_%s_%s", task.phase, task.subject, task.material, task.diff, task.area, task.year));
			if (fileOld.exists())
			{
				LOG.v("rename over : " + fileNew.toString());
				fileOld.renameTo(fileNew);
			}
		}
	}

	public static SZXTask packZXTask(ResultSet rs) throws SQLException
	{
		SZXTask task = new SZXTask();
		task.id = rs.getString(T.zhixue.id);
		task.phase = rs.getString(T.zhixue.phase);
		task.subject = rs.getString(T.zhixue.subject);
		task.material = rs.getString(T.zhixue.material);
//		task.chapterPath = rs.getString(T.zhixue.chapterPath);
//		task.style = rs.getString(T.zhixue.style);
		task.diff = rs.getString(T.zhixue.diff);
		task.area = rs.getString(T.zhixue.area);
		task.year = rs.getString(T.zhixue.year);
		task.currChapterPath = rs.getString(T.zhixue.currChapterPath);
		task.currPage = rs.getInt(T.zhixue.currPage);
		task.totalPage = rs.getInt(T.zhixue.totalPage);
		task.deviceId = rs.getString(T.zhixue.deviceId);
		return task;
	}

	public static SZXTask selectTask(String phase, String subject, String material, String diff, String area, String year, String deviceId) throws Exception
	{
		final List<SZXTask> tasks = new ArrayList<>();
		DBHelper.select(T.zhixue, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				if (rs.next())
				{
					tasks.add(packZXTask(rs));
				}
			}
		}, T.zhixue.phase, phase, //
				T.zhixue.subject, subject, //
				T.zhixue.material, material, //
//				T.zhixue.chapterPath, chapterPath, //
//				T.zhixue.style, style, //
				T.zhixue.diff, diff, //
				T.zhixue.area, area, //
				T.zhixue.year, year, //
				T.zhixue.deviceId, deviceId);
		if (tasks.size() > 0)
			return tasks.get(0);
		else
			return null;
	}

	public static SZXTask selectTask(String phase, String subject, String material, String deviceId) throws Exception
	{
		final List<SZXTask> tasks = new ArrayList<>();
		DBHelper.select(T.zhixue, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				if (rs.next())
				{
					tasks.add(packZXTask(rs));
				}
			}
		}, T.zhixue.phase, phase, //
				T.zhixue.subject, subject, //
				T.zhixue.material, material, //
				T.zhixue.deviceId, deviceId);
		if (tasks.size() > 0)
			return tasks.get(0);
		else
			return null;
	}

	public static List<SZXTask> selectTaskList(String phase, String subject, String material) throws Exception
	{
		final List<SZXTask> tasks = new ArrayList<>();
		DBHelper.select(T.zhixue, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					tasks.add(packZXTask(rs));
				}
			}
		}, T.zhixue.phase, phase, //
				T.zhixue.subject, subject, //
				T.zhixue.material, material);
		return tasks;
	}

//	private static boolean checkChapters(SZXChapterTreeNode parent, List<SZXChapterTreeNode> chapters, List<SZXChapterTreeNode> leafChapters) throws Exception
//	{
//		HashMap<String, SZXChapterTreeNode> map = new HashMap<String, SZXChapterTreeNode>();
//		for (SZXChapterTreeNode chapter : chapters)
//		{
//			chapter.parent = parent;
//			map.put(chapter.name, chapter);
//		}
//		if (map.size() != chapters.size())
//			return false;
//		for (SZXChapterTreeNode chapter : chapters)
//		{
//			if (chapter.chapters.size() == 0)
//			{
//				leafChapters.add(chapter);
//			}
//			else
//			{
//				if (!checkChapters(chapter, chapter.chapters, leafChapters))
//					return false;
//			}
//		}
//		return true;
//	}
//
//	private static void getChapterPath(SZXChapterTreeNode chapter, List<String> chapterPath) throws Exception
//	{
//		if (chapter.parent != null)
//		{
//			getChapterPath(chapter.parent, chapterPath);
//		}
//		chapterPath.add(chapter.name);
//	}

	public static void ZXCreateTask(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXCreateTask requestData = SRequest_ZXCreateTask.load(data);
		SResponse_ZXCreateTask responseData = new SResponse_ZXCreateTask();
		if (!DBHelper.hasRecord(T.zhixue, T.zhixue.phase, requestData.phase, T.zhixue.subject, requestData.subject, T.zhixue.material, requestData.material))
		{
//			List<SZXChapterTreeNode> leafChapters = new ArrayList<SZXChapterTreeNode>();
//			if (checkChapters(null, requestData.chapters, leafChapters))
//			{
//				for (SZXChapterTreeNode chapter : leafChapters)
//				{
//					List<String> chapterPath = new ArrayList<String>();
//					getChapterPath(chapter, chapterPath);
//					String chapterpath = AppDataTool.saveStringList(chapterPath).toString();
//					//
//				}
//			}
//			else
//			{
//				error(response, SErrorCode.unknown_error, "同级下存在重名章节");
//			}
			for (String diff : requestData.diffs)
			{
				for (String area : requestData.areas)
				{
					for (String year : requestData.years)
					{
						DBHelper.insert(T.zhixue, //
								T.zhixue.id, CommonUtils.uuid(), //
								T.zhixue.phase, requestData.phase, //
								T.zhixue.subject, requestData.subject, //
								T.zhixue.material, requestData.material, //
//								T.zhixue.chapterPath, chapterpath, //
//								T.zhixue.style, style, //
								T.zhixue.diff, diff, //
								T.zhixue.area, area, //
								T.zhixue.year, year, //
								T.zhixue.currChapterPath, "", //
								T.zhixue.currPage, 1, //
								T.zhixue.totalPage, -1, //
								T.zhixue.sort, 100, //
								T.zhixue.deviceId, "");
					}
				}
			}
		}
		success(response, responseData.saveToStr());
	}

	public static void ZXPullTask(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXPullTask requestData = SRequest_ZXPullTask.load(data);
		SResponse_ZXPullTask responseData = new SResponse_ZXPullTask();
		SZXTask existTask = selectTask(requestData.phase, requestData.subject, requestData.material, requestData.deviceId);
		if (existTask != null)
		{
			responseData.task = existTask;
		}
		else
		{
			List<SZXTask> tasks = selectTaskList(requestData.phase, requestData.subject, requestData.material);
			for (SZXTask task : tasks)
			{
				if (TextUtils.isEmpty(task.deviceId))
				{
					responseData.task = task;
					DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, requestData.deviceId), T.zhixue.id, task.id);
					break;
				}
			}
		}
		success(response, responseData.saveToStr());
	}

	public static void ZXCatchPageOver(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXCatchPageOver requestData = SRequest_ZXCatchPageOver.load(data);
		SResponse_ZXCatchPageOver responseData = new SResponse_ZXCatchPageOver();
		SZXTask existTask = selectTask(requestData.phase, requestData.subject, requestData.material, requestData.diff, requestData.area, requestData.year, requestData.deviceId);
		if (existTask != null)
		{
			DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.currChapterPath, requestData.currChapterPath, T.zhixue.currPage, requestData.currPage + 1), T.zhixue.id, existTask.id);
			success(response, responseData.saveToStr());
		}
		else
		{
			error(response, SErrorCode.unknown_error, "未找到要提交的任务");
		}
	}

	public static void ZXCatchOver(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXCatchOver requestData = SRequest_ZXCatchOver.load(data);
		SResponse_ZXCatchOver responseData = new SResponse_ZXCatchOver();
		SZXTask task = selectTask(requestData.phase, requestData.subject, requestData.material, requestData.diff, requestData.area, requestData.year, requestData.deviceId);
		if (task != null)
		{
			DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, "over"), T.zhixue.id, task.id);

			File fileOld = new File(Config.fileDir, String.format("/catch_zhixue/%s_%s_%s/%s_%s_%s", task.phase, task.subject, task.material, task.diff, task.area, task.year));
			File fileNew = new File(Config.fileDir, String.format("/catch_zhixue/%s_%s_%s/over_%s_%s_%s", task.phase, task.subject, task.material, task.diff, task.area, task.year));
			if (fileOld.exists())
			{
				LOG.v("rename over : " + fileNew.toString());
				fileOld.renameTo(fileNew);
			}

			success(response, responseData.saveToStr());
		}
		else
		{
			error(response, SErrorCode.unknown_error, "未找到要结束的任务");
		}
	}

	private static int perGetKnowledgeTopicCount(int phase, int subject, String knowledgeName)
	{
		SRequest_SearchTopics requestData = new SRequest_SearchTopics();
		requestData.content = "";
		requestData.phase = phase;
		requestData.subject = subject;
		requestData.start = 0;
		requestData.rows = 1;
		requestData.knowledges.add(SKnowledge.create("", knowledgeName, 0, 0, false, null, 0));
		String jsonStr = doPostSolr(SHandleId.SearchTopics, requestData.saveToStr());
		if (!TextUtils.isEmpty(jsonStr))
		{
			SResponse_SearchTopics responseData = SResponse_SearchTopics.load(jsonStr);
			return responseData.totalCount;
		}
		else
		{
			return -1;
		}
	}

	private static boolean genKnowledgeTree(int phase, int subject, String tableName, String history, String parent, List<SZXKnowledgeTreeNode> knowledges, List<SKnowledge> outKnowledges) throws Exception
	{
		for (SZXKnowledgeTreeNode knowledge : knowledges)
		{
			if (TextUtils.isEmpty(knowledge.name))
			{
				LOG.e("名字为空：" + knowledge.name);
				return false;
			}
			if (knowledge.name.contains("#"))
			{
				LOG.e("名字包含#：" + knowledge.name);
				return false;
			}
			if (DBHelper.hasRecord(tableName, T.knowledge.parent, parent, T.knowledge.name, knowledge.name))
			{
				LOG.e("同级存在相同知识点名：" + knowledge.name);
				return false;
			}
			else
			{
				int leaf = knowledge.knowledges.size() == 0 ? 1 : 0;
//				if (leaf == 1 && DBHelper.hasRecord(tableName, T.knowledge.name, knowledge.name, T.knowledge.leaf, 1))
//				{
//					LOG.e("叶子存在相同知识点名：" + knowledge.name);
//					return false;
//				}
//				else
				{
//					String id = CommonUtils.uuid();
					String id = CommonUtils.md5(history + "#S#" + knowledge.name + "#S#");
//					String id = CommonUtils.md5(knowledge.name);
					DBHelper.insert(tableName, //
							T.knowledge.id, id, //
							T.knowledge.parent, TextUtils.isEmpty(parent) ? "" : parent, //
							T.knowledge.name, knowledge.name, //
							T.knowledge.leaf, leaf);

					SKnowledge outKnowledge = new SKnowledge();
					outKnowledge.code = id;
					outKnowledge.name = knowledge.name;
					outKnowledge.topicCount = perGetKnowledgeTopicCount(phase, subject, "#S#" + knowledge.name + "#S#");
					if (outKnowledge.topicCount == -1)
						return false;
					outKnowledges.add(outKnowledge);

					if (!genKnowledgeTree(phase, subject, tableName, history + "#S#" + knowledge.name + "#S#", id, knowledge.knowledges, outKnowledge.nodes))
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	public static void ZXGenKnowledgeTree(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXGenKnowledgeTree requestData = SRequest_ZXGenKnowledgeTree.load(data);
		SResponse_ZXGenKnowledgeTree responseData = new SResponse_ZXGenKnowledgeTree();
		String tableName = AppConfig.getKnowledgeTableName(requestData.knowledgeTree.phase, requestData.knowledgeTree.subject);
		if (!TextUtils.isEmpty(tableName))
		{
			DBHelper.exeSql(String.format("drop table if exists %s;", tableName), null);
			if (!DBHelper.hasTable(tableName))
			{
				TopicManager.insureKnowledgeTableExists(tableName);
				int phaseCode = AppConfig.getPhase(requestData.knowledgeTree.phase);
				int subjectCode = AppConfig.getSubject(requestData.knowledgeTree.subject);
				boolean success = genKnowledgeTree(phaseCode, subjectCode, tableName, "", "", requestData.knowledgeTree.knowledges, responseData.knowledges);
				if (success)
				{
					File file = new File(Config.fileDir, String.format("fixed/%s.json", tableName));
					File fileRaw = new File(Config.fileDir, String.format("fixed/%s.json.raw", tableName));

					FsUtils.createDir(file.getParentFile());
					FsUtils.writeText(file, LOGJson.getStr(SKnowledge.saveList(responseData.knowledges).toString()));

					FsUtils.createDir(fileRaw.getParentFile());
					FsUtils.writeText(fileRaw, SKnowledge.saveList(responseData.knowledges).toString());

					success(response, responseData.saveToStr());
				}
				else
				{
					error(response, SErrorCode.unknown_error, "存在错误");
				}
			}
			else
			{
				error(response, SErrorCode.unknown_error, "表已经存在");
			}
		}
		else
		{
			error(response, SErrorCode.unknown_error, "参数错误");
		}
	}

	private static int perGetChapterTopicCount(int phase, int subject, String chapterName)
	{
		SRequest_SearchTopics requestData = new SRequest_SearchTopics();
		requestData.content = "";
		requestData.phase = phase;
		requestData.subject = subject;
		requestData.start = 0;
		requestData.rows = 1;
		requestData.chapters.add(SChapter.create("", chapterName, 0, 0, false, null, 0));
		String jsonStr = doPostSolr(SHandleId.SearchTopics, requestData.saveToStr());
		if (!TextUtils.isEmpty(jsonStr))
		{
			SResponse_SearchTopics responseData = SResponse_SearchTopics.load(jsonStr);
			return responseData.totalCount;
		}
		else
		{
			return -1;
		}
	}

	private static boolean genChapterTree(int phase, int subject, String tableName, String history, String parent, List<SZXChapterTreeNode> chapters, List<SChapter> outChapters) throws Exception
	{
		for (SZXChapterTreeNode chapter : chapters)
		{
			if (TextUtils.isEmpty(chapter.name))
			{
				LOG.e("名字为空：" + chapter.name);
				return false;
			}
			if (chapter.name.contains("#"))
			{
				LOG.e("名字包含#：" + chapter.name);
				return false;
			}
			if (DBHelper.hasRecord(tableName, T.chapter.parent, parent, T.chapter.name, chapter.name))
			{
				LOG.e("同级存在相同章节名：" + chapter.name);
				return false;
			}
			else
			{
				int leaf = chapter.chapters.size() == 0 ? 1 : 0;
//				if (leaf == 1 && DBHelper.hasRecord(tableName, T.chapter.name, chapter.name, T.chapter.leaf, 1))
//				{
//					LOG.e("叶子存在相同章节名：" + chapter.name);
//					return false;
//				}
//				else
				{
//					String id = CommonUtils.uuid();
					String id = CommonUtils.md5(history + "#S#" + chapter.name + "#S#");
//					String id = CommonUtils.md5(chapter.name);
					DBHelper.insert(tableName, //
							T.chapter.id, id, //
							T.chapter.parent, TextUtils.isEmpty(parent) ? "" : parent, //
							T.chapter.name, chapter.name, //
							T.chapter.leaf, leaf);

					SChapter outChapter = new SChapter();
					outChapter.code = id;
					outChapter.name = chapter.name;
					outChapter.topicCount = perGetChapterTopicCount(phase, subject, history + "#S#" + chapter.name + "#S#");
					if (outChapter.topicCount == -1)
						return false;
					outChapters.add(outChapter);

					if (!genChapterTree(phase, subject, tableName, history + "#S#" + chapter.name + "#S#", id, chapter.chapters, outChapter.nodes))
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	public static void ZXGenChapterTree(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXGenChapterTree requestData = SRequest_ZXGenChapterTree.load(data);
		SResponse_ZXGenChapterTree responseData = new SResponse_ZXGenChapterTree();
		String tableName = AppConfig.getChapterTableName(requestData.chapterTree.phase, requestData.chapterTree.subject);
		if (!TextUtils.isEmpty(tableName))
		{
			DBHelper.exeSql(String.format("drop table if exists %s;", tableName), null);
			if (!DBHelper.hasTable(tableName))
			{
				TopicManager.insureChapterTableExists(tableName);
				int phaseCode = AppConfig.getPhase(requestData.chapterTree.phase);
				int subjectCode = AppConfig.getSubject(requestData.chapterTree.subject);
				boolean success = genChapterTree(phaseCode, subjectCode, tableName, "", "", requestData.chapterTree.chapters, responseData.chapters);
				if (success)
				{
					File file = new File(Config.fileDir, String.format("fixed/%s.json", tableName));
					File fileRaw = new File(Config.fileDir, String.format("fixed/%s.json.raw", tableName));

					FsUtils.createDir(file.getParentFile());
					FsUtils.writeText(file, LOGJson.getStr(SChapter.saveList(responseData.chapters).toString()));

					FsUtils.createDir(fileRaw.getParentFile());
					FsUtils.writeText(fileRaw, SChapter.saveList(responseData.chapters).toString());

					success(response, responseData.saveToStr());
				}
				else
					error(response, SErrorCode.unknown_error, "存在错误");
			}
			else
			{
				error(response, SErrorCode.unknown_error, "表已经存在");
			}
		}
		else
		{
			error(response, SErrorCode.unknown_error, "参数错误");
		}
	}

	public static STopic convertTopic(SZXTopic zxTopic, StringBuilder knowledgeSb, StringBuilder chapterSb) throws Exception
	{
		STopic topic = new STopic();

		topic.id = zxTopic.id;

		topic.phase = AppConfig.getPhase(zxTopic.phase);
		if (topic.phase == AppConfig.ErrorCode)
		{
			LOG.v("参数错误：" + zxTopic.phase);
			return null;
		}

		topic.subject = AppConfig.getSubject(zxTopic.subject);
		if (topic.subject == AppConfig.ErrorCode)
		{
			LOG.v("参数错误：" + zxTopic.subject);
			return null;
		}

		topic.material = zxTopic.material;
		topic.style = zxTopic.style;

		topic.diff = AppConfig.getDifficulty(zxTopic.diff);
		if (topic.diff == AppConfig.ErrorCode)
		{
			LOG.v("参数错误：" + zxTopic.diff);
			return null;
		}

		topic.area = zxTopic.area;

		topic.year = zxTopic.year;

		for (String knowledge : zxTopic.knowledges)
		{
			if (!TextUtils.isEmpty(knowledge))
			{
				if (!knowledge.contains("#"))
				{
					knowledgeSb.append("#" + knowledge + "#");
				}
				else
				{
					LOG.v("知识点错误：" + knowledge);
					return null;
				}
			}
		}
		if (TextUtils.isEmpty(knowledgeSb.toString()))
		{
			LOG.v("知识点为空");
			return null;
		}

		topic.zujuan = zxTopic.zujuan;
		topic.zuoda = zxTopic.zuoda;

		topic.defen = -1f;
		if (!TextUtils.isEmpty(zxTopic.defen))
		{
			if (zxTopic.defen.endsWith("%"))
			{
				try
				{
					topic.defen = Float.valueOf(zxTopic.defen.substring(0, zxTopic.defen.length() - 1));
				}
				catch (Exception e)
				{
					LOG.v("参数错误：" + zxTopic.defen);
					return null;
				}
			}
			else
			{
				LOG.v("参数错误：" + zxTopic.defen);
				return null;
			}
		}

		topic.nandu = TextUtils.isEmpty(zxTopic.nandu) ? "" : zxTopic.nandu;

		String[] chapters = zxTopic.chapterPath.split("---##---");
		for (String chapter : chapters)
		{
			if (!TextUtils.isEmpty(chapter))
			{
				if (!chapter.contains("#"))
				{
					chapterSb.append("#" + chapter + "#");
				}
				else
				{
					LOG.v("章节错误：" + chapter);
					return null;
				}
			}
		}
		if (TextUtils.isEmpty(chapterSb.toString()))
		{
			LOG.v("章节为空");
			return null;
		}

		topic.content = zxTopic.content.replace(" ", "").replace("\t", "").replace(" ", "");
		topic.answer = zxTopic.answer.replace(" ", "").replace("\t", "").replace(" ", "");
		topic.parse = zxTopic.parse.replace(" ", "").replace("\t", "").replace(" ", "");

		return topic;
	}

	public static void ZXAddTopic(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXAddTopic requestData = SRequest_ZXAddTopic.load(data);
		SResponse_ZXAddTopic responseData = new SResponse_ZXAddTopic();
		boolean success = true;
		for (int i = 0; i < requestData.topics.size(); i++)
		{
			SZXTopic zxTopic = requestData.topics.get(i);
			String tableName = AppConfig.getTopicTableName(zxTopic.phase, zxTopic.subject);
			if (!TextUtils.isEmpty(tableName))
			{
				TopicManager.insureTopicTableExists(tableName);

				StringBuilder knowledgeSb = new StringBuilder();
				StringBuilder chapterSb = new StringBuilder();
				STopic topicNew = convertTopic(zxTopic, knowledgeSb, chapterSb);
				if (topicNew == null)
				{
					success = false;
					continue;
				}

				STopic topicOld = ProcessTopic.selectTopic(tableName, zxTopic.id);
				if (topicOld != null)
				{
					responseData.repeatIndexs.add(i);

					if (!topicOld.chapters.contains(topicNew.chapters))
					{
						String chaptersAll = topicOld.chapters + "#" + topicNew.chapters;
						DBHelper.update(tableName, DBHelper.set(T.topic.chapters, chaptersAll), T.topic.id, zxTopic.id);
					}
					if (!topicOld.area.contains(topicNew.area))
					{
						String areaAll = topicOld.area + "#" + topicNew.area;
						DBHelper.update(tableName, DBHelper.set(T.topic.area, areaAll), T.topic.id, zxTopic.id);
					}
					if (!topicOld.year.contains(topicNew.year))
					{
						String yearAll = topicOld.year + "#" + topicNew.year;
						DBHelper.update(tableName, DBHelper.set(T.topic.year, yearAll), T.topic.id, zxTopic.id);
					}

					if (!topicOld.style.equals(topicNew.style) || //
							!topicOld.diff.equals(topicNew.diff) || //
							!topicOld.knowledges.equals(topicNew.knowledges) || //
							!topicOld.content.equals(topicNew.content))
					{
						String differentDir;
						if (!topicOld.style.equals(topicNew.style) || //
								!topicOld.diff.equals(topicNew.diff) || //
								!topicOld.knowledges.equals(topicNew.knowledges))
							differentDir = "different_other_zhixue";
						else
							differentDir = "different_content_zhixue";
						{
							File jsonFile = new File(String.format("%s/%s/%s_%s/%s/%s.json", //
									Config.fileDir, differentDir, zxTopic.phase, zxTopic.subject, zxTopic.id, CommonUtils.md5(topicOld.style + topicOld.diff + topicOld.knowledges + topicOld.content)));
							FsUtils.createDir(jsonFile.getParentFile());
							FsUtils.writeText(jsonFile, LOGJson.getStr(topicOld.saveToStr()));
						}
						{
							File jsonFile = new File(String.format("%s/%s/%s_%s/%s/%s.json", //
									Config.fileDir, differentDir, zxTopic.phase, zxTopic.subject, zxTopic.id, CommonUtils.md5(topicNew.style + topicNew.diff + topicNew.knowledges + topicNew.content)));
							FsUtils.createDir(jsonFile.getParentFile());
							FsUtils.writeText(jsonFile, LOGJson.getStr(topicNew.saveToStr()));
						}
					}
				}
				else
				{
					responseData.addIndexs.add(i);

					DBHelper.insert(tableName, //
							T.topic.id, topicNew.id, //
							T.topic.phase, topicNew.phase, //
							T.topic.subject, topicNew.subject, //
							T.topic.material, topicNew.material, //
							T.topic.style, topicNew.style, //
							T.topic.diff, topicNew.diff, //
							T.topic.area, topicNew.area, //
							T.topic.year, topicNew.year, //
							T.topic.knowledges, knowledgeSb.toString(), //
							T.topic.zujuan, topicNew.zujuan, //
							T.topic.zuoda, topicNew.zuoda, //
							T.topic.defen, topicNew.defen, //
							T.topic.nandu, topicNew.nandu, //
							T.topic.chapters, chapterSb.toString(), //
							T.topic.content, topicNew.content, //
							T.topic.answer, topicNew.answer, //
							T.topic.parse, topicNew.parse);
				}
			}
			else
			{
				LOG.v("参数错误");
				success = false;
			}
		}
		if (success)
			success(response, responseData.saveToStr());
		else
			error(response, SErrorCode.unknown_error, "存在错误");
	}

	public static SZXAccount packSZXAccount(ResultSet rs) throws SQLException
	{
		SZXAccount account = new SZXAccount();
		account.account = rs.getString(T.zhixueAccount.account);
		account.psw = rs.getString(T.zhixueAccount.psw);
		account.state = rs.getString(T.zhixueAccount.state);
		account.deviceId = rs.getString(T.zhixueAccount.deviceId);
		return account;
	}

	public static List<SZXAccount> selectAccountList() throws Exception
	{
		final List<SZXAccount> accounts = new ArrayList<>();
		DBHelper.select(T.zhixueAccount, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					accounts.add(packSZXAccount(rs));
				}
			}
		}, T.zhixueAccount.state, "", //
				T.zhixueAccount.deviceId, "");
		return accounts;
	}

	public static SZXAccount selectAccount(String deviceId) throws Exception
	{
		final List<SZXAccount> accounts = new ArrayList<>();
		DBHelper.select(T.zhixueAccount, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				if (rs.next())
				{
					accounts.add(packSZXAccount(rs));
				}
			}
		}, T.zhixueAccount.deviceId, deviceId);
		if (accounts.size() > 0)
			return accounts.get(0);
		else
			return null;
	}

	public static void ZXPullAccount(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXPullAccount requestData = SRequest_ZXPullAccount.load(data);
		SResponse_ZXPullAccount responseData = new SResponse_ZXPullAccount();

		SZXAccount oldAccount = selectAccount(requestData.deviceId);

		List<SZXAccount> accounts = selectAccountList();
		if (accounts.size() > 0)
		{
			Collections.shuffle(accounts);
			SZXAccount newAccount = accounts.get(0);
			responseData.account = newAccount.account;
			responseData.psw = newAccount.psw;
			DBHelper.update(T.zhixueAccount, DBHelper.set(T.zhixueAccount.deviceId, requestData.deviceId), T.zhixueAccount.account, newAccount.account);
			if (oldAccount != null)
				DBHelper.update(T.zhixueAccount, DBHelper.set(T.zhixueAccount.deviceId, ""), T.zhixueAccount.account, oldAccount.account);
			success(response, responseData.saveToStr());
		}
		else
		{
			error(response, SErrorCode.unknown_error, "没有可用账号");
		}
	}

	public static void ZXReportAccount(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXReportAccount requestData = SRequest_ZXReportAccount.load(data);
		SResponse_ZXReportAccount responseData = new SResponse_ZXReportAccount();
		DBHelper.update(T.zhixueAccount, DBHelper.set(T.zhixueAccount.state, requestData.state), T.zhixueAccount.account, requestData.account);
		success(response, responseData.saveToStr());
	}

	private static boolean keepScreen = false;
	private static final ConcurrentHashMap<String, SZXDeviceInfo> deviceMap = new ConcurrentHashMap<String, SZXDeviceInfo>();

	public static void ZXDeviceList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXDeviceList requestData = SRequest_ZXDeviceList.load(data);
		SResponse_ZXDeviceList responseData = new SResponse_ZXDeviceList();
		if (requestData.keepScreen == 0)
			keepScreen = false;
		else if (requestData.keepScreen == 1)
			keepScreen = true;
		responseData.seriveIp = Config.getSeriveIp();
		responseData.keepScreen = keepScreen;
		responseData.devices = new ArrayList<SZXDeviceInfo>(deviceMap.values());
		Collections.sort(responseData.devices, new Comparator<SZXDeviceInfo>()
		{
			@Override
			public int compare(SZXDeviceInfo o1, SZXDeviceInfo o2)
			{
				return o1.lastTickTime.compareTo(o2.lastTickTime);
			}
		});
		success(response, responseData.saveToStr());
	}

	public static void ZXTickInfo(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXTickInfo requestData = SRequest_ZXTickInfo.load(data);
		SResponse_ZXTickInfo responseData = new SResponse_ZXTickInfo();

		SZXDeviceInfo info = new SZXDeviceInfo();
		info.deviceId = requestData.deviceId;
		info.host = request.getRemoteHost();
		info.addr = request.getRemoteAddr();
		info.xForwardedFor = request.getHeader("x-forwarded-for");
		info.xForwardedForPound = request.getHeader("x-forwarded-for-pound");
		info.hostIp = requestData.hostIp;
		info.netIp = requestData.netIp;
		info.versionCode = requestData.versionCode;
		info.lastTickTime = System.currentTimeMillis();
		info.capacity = requestData.capacity;
		info.dtTime = requestData.dtTime;
		deviceMap.put(info.deviceId, info);

		responseData.keepScreen = keepScreen;
		if (!TextUtils.isEmpty(info.xForwardedFor))
			responseData.clientIp = info.xForwardedFor;
		else
			responseData.clientIp = info.host;
		responseData.seriveIp = Config.getSeriveIp();

		success(response, responseData.saveToStr());
	}

	public static void ZXProcessJuan(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXProcessJuan requestData = SRequest_ZXProcessJuan.load(data);
		SResponse_ZXProcessJuan responseData = new SResponse_ZXProcessJuan();

		File file = new File(Config.fileDir, String.format("/juan/%s%s/%s/main.html", requestData.phase, requestData.subject, requestData.material.replace("/", "").replace("\\", "").replace("*", "").replace(":", "")));
		if (file.exists())
		{
			if (zhixue.processJuan(file))
			{
				SPTask task = client.genJuanTask(file.getParentFile(), requestData.phase, requestData.subject, requestData.material, 1600, true);
				if (task != null)
				{
					SVNManager.deleteIfExists(task.userId, task.id, "delete to recreate for juan");

					String taskUrl = SVNManager.getTaskUrl(task.userId, task.id);
					File taskDir = new File(file.getParentFile(), task.id);
					SVNManager.doImport(taskUrl, taskDir, "import task for juan");

					success(response, responseData.saveToStr());
					return;
				}
			}
		}

		error(response, SErrorCode.unknown_error, "处理错误");
	}

	public static void ZXProcessJuan2(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ZXProcessJuan2 requestData = SRequest_ZXProcessJuan2.load(data);
		SResponse_ZXProcessJuan2 responseData = new SResponse_ZXProcessJuan2();

		File file = new File(Config.fileDir, String.format("/juan/%s%s/%s/main.html", requestData.phase, requestData.subject, requestData.material.replace("/", "").replace("\\", "").replace("*", "").replace(":", "")));
		if (file.exists())
		{
			if (zhixue.processJuan2(file))
			{
				SPTask task1 = client.genJuanTask(file.getParentFile(), requestData.phase, requestData.subject, requestData.material, 1400, true);
				if (task1 != null)
				{
					SVNManager.deleteIfExists(task1.userId, task1.id, "delete to recreate for juan");

					String taskUrl1 = SVNManager.getTaskUrl(task1.userId, task1.id);
					File taskDir1 = new File(file.getParentFile(), task1.id);
					SVNManager.doImport(taskUrl1, taskDir1, "import task for juan");

					SPTask task2 = client.genJuanTask(file.getParentFile(), "试题", requestData.subject, requestData.material, 1400, false);
					if (task2 != null)
					{
						SVNManager.deleteIfExists(task2.userId, task2.id, "delete to recreate for juan");

						String taskUrl2 = SVNManager.getTaskUrl(task2.userId, task2.id);
						File taskDir2 = new File(file.getParentFile(), task2.id);
						SVNManager.doImport(taskUrl2, taskDir2, "import task for juan");

						success(response, responseData.saveToStr());
						return;
					}
				}
			}
		}

		error(response, SErrorCode.unknown_error, "处理错误");
	}
}
