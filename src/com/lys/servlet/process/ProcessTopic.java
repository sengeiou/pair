package com.lys.servlet.process;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.base.utils.JsonHelper;
import com.lys.config.AppConfig;
import com.lys.config.Config;
import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.SErrorCode;
import com.lys.protobuf.SHandleId;
import com.lys.protobuf.SKnowledge;
import com.lys.protobuf.SProblemStyle;
import com.lys.protobuf.SRequest_GetKnowledges;
import com.lys.protobuf.SRequest_GetTopicStyles;
import com.lys.protobuf.SRequest_SearchTopics;
import com.lys.protobuf.SResponse_GetKnowledges;
import com.lys.protobuf.SResponse_GetTopicStyles;
import com.lys.protobuf.SResponse_SearchTopics;
import com.lys.protobuf.STopic;
import com.lys.utils.FsUtils;
import com.lys.utils.OssUtils;
import com.lys.utils.TextUtils;

public class ProcessTopic extends BaseProcess
{
	public static List<String> splitKnowledges(String knowledgesStr)
	{
		List<String> knowledges = new ArrayList<String>();
		String[] knowledgeArray = knowledgesStr.split("##");
		for (String knowledge : knowledgeArray)
		{
			if (!TextUtils.isEmpty(knowledge))
			{
				if (knowledge.startsWith("#"))
					knowledge = knowledge.substring(1);
				if (knowledge.endsWith("#"))
					knowledge = knowledge.substring(0, knowledge.length() - 1);
				knowledges.add(knowledge);
			}
		}
		return knowledges;
	}

	public static List<String> splitChapters(String chaptersStr)
	{
		List<String> chapters = new ArrayList<String>();
		String[] chapterArray = chaptersStr.split("###");
		for (String chapter : chapterArray)
		{
			if (!TextUtils.isEmpty(chapter))
			{
				if (!chapter.startsWith("#"))
					chapter = "#" + chapter;
				if (!chapter.endsWith("#"))
					chapter = chapter + "#";
				chapters.add(chapter);
			}
		}
		return chapters;
	}

	public static STopic packTopic(ResultSet rs) throws SQLException
	{
		STopic topic = new STopic();
		topic.id = rs.getString(T.topic.id);
		topic.phase = rs.getInt(T.topic.phase);
		topic.subject = rs.getInt(T.topic.subject);
		topic.material = rs.getString(T.topic.material);
		topic.style = rs.getString(T.topic.style);
		topic.diff = rs.getInt(T.topic.diff);
		topic.area = rs.getString(T.topic.area);
		topic.year = rs.getString(T.topic.year);
		topic.knowledges = splitKnowledges(rs.getString(T.topic.knowledges));
		topic.zujuan = rs.getInt(T.topic.zujuan);
		topic.zuoda = rs.getInt(T.topic.zuoda);
		topic.defen = rs.getFloat(T.topic.defen);
		topic.nandu = rs.getString(T.topic.nandu);
		topic.chapters = splitChapters(rs.getString(T.topic.chapters));
		topic.content = rs.getString(T.topic.content);
		topic.answer = rs.getString(T.topic.answer);
		topic.parse = rs.getString(T.topic.parse);
		return topic;
	}

	public static STopic selectTopic(String tableName, String id) throws Exception
	{
		final List<STopic> topics = new ArrayList<>();
		DBHelper.select(tableName, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				if (rs.next())
				{
					topics.add(packTopic(rs));
				}
			}
		}, T.topic.id, id);
		if (topics.size() > 0)
			return topics.get(0);
		else
			return null;
	}

//	public static void searchTopicList(SRequest_SearchTopics search, final List<STopic> topics, final List<Integer> counts) throws Exception
//	{
//		String tableName = AppConfig.getTopicTableName(SPhase.Gao, search.subject);
//		if (!TextUtils.isEmpty(tableName))
//		{
//			if (DBHelper.hasTable(tableName))
//			{
//				StringBuilder sb = new StringBuilder();
//				sb.append(String.format("select * from %s where %s = '%s'", tableName, T.topic.subject, search.subject));
//				if (!TextUtils.isEmpty(search.content))
//					sb.append(String.format(" and %s like '%%%s%%'", T.topic.content, search.content));
//				if (!TextUtils.isEmpty(search.style))
//					sb.append(String.format(" and %s = '%s'", T.topic.style, search.style));
//				if (search.diff != 0)
//					sb.append(String.format(" and %s = '%s'", T.topic.diff, search.diff));
//				if (search.knowledges.size() == 1)
//				{
//					sb.append(String.format(" and %s like '%%%s%%'", T.topic.knowledges, search.knowledges.get(0)));
//				}
//				else if (search.knowledges.size() > 1)
//				{
//					for (int i = 0; i < search.knowledges.size(); i++)
//					{
//						String knowledge = search.knowledges.get(i);
//						if (i == 0)
//							sb.append(String.format(" and (%s like '%%%s%%'", T.topic.knowledges, knowledge));
//						else if (i == search.knowledges.size() - 1)
//							sb.append(String.format(" or %s like '%%%s%%')", T.topic.knowledges, knowledge));
//						else
//							sb.append(String.format(" or %s like '%%%s%%'", T.topic.knowledges, knowledge));
//					}
//				}
//				sb.append(String.format(" limit %s, %s;", search.start, search.rows));
//				DBHelper.exeSql(sb.toString(), new OnCallback()
//				{
//					@Override
//					public void onResult(ResultSet rs) throws SQLException
//					{
//						while (rs.next())
//						{
//							topics.add(packTopic(rs));
//						}
//					}
//				});
//				String sql = sb.toString().replace("select * from ", "select count(*) from ");
//				sql = sql.substring(0, sql.lastIndexOf(" limit")) + ";";
//				DBHelper.exeSql(sql, new OnCallback()
//				{
//					@Override
//					public void onResult(ResultSet rs) throws SQLException
//					{
//						if (rs.next())
//						{
//							counts.add(rs.getInt(1));
//						}
//					}
//				});
//			}
//		}
//	}

	public static void SearchTopics(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SearchTopics requestData = SRequest_SearchTopics.load(data);
		if (true)
		{
			String jsonStr = doPostSolr(SHandleId.SearchTopics, requestData.saveToStr());
			if (!TextUtils.isEmpty(jsonStr))
			{
				SResponse_SearchTopics responseData = SResponse_SearchTopics.load(jsonStr);
				for (STopic topic : responseData.topics)
				{
					topic.contentUrl = String.format("%s%d_%d/%s/%s/content.png", OssUtils.getHost(OssUtils.ZjykTopic), topic.phase, topic.subject, topic.id.substring(topic.id.length() - 2), topic.id);
					topic.analyUrl = String.format("%s%d_%d/%s/%s/parse.png", OssUtils.getHost(OssUtils.ZjykTopic), topic.phase, topic.subject, topic.id.substring(topic.id.length() - 2), topic.id);
				}
				success(response, responseData.saveToStr());
			}
			else
			{
				error(response, SErrorCode.unknown_error, "参数错误");
			}
		}
		else
		{
//			SResponse_SearchTopics responseData = new SResponse_SearchTopics();
//			List<STopic> topics = new ArrayList<>();
//			List<Integer> counts = new ArrayList<>();
//			searchTopicList(requestData, topics, counts);
//			if (counts.size() == 1)
//			{
//				for (STopic topic : topics)
//				{
//					topic.contentUrl = String.format("%s/topic-imgs/%d_%d/%s.content.png", Config.getUrlRoot(), topic.phase, topic.subject, topic.id);
//					topic.analyUrl = String.format("%s/topic-imgs/%d_%d/%s.parse.png", Config.getUrlRoot(), topic.phase, topic.subject, topic.id);
//				}
//				responseData.totalCount = counts.get(0);
//				responseData.topics = topics;
//				success(response, responseData.saveToStr());
//			}
//			else
//			{
//				error(response, SErrorCode.unknown_error, "参数错误");
//			}
		}
	}

	public static void GetTopicStyles(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetTopicStyles requestData = SRequest_GetTopicStyles.load(data);
		SResponse_GetTopicStyles responseData = new SResponse_GetTopicStyles();
		String tableName = AppConfig.getTopicStyleTableName(requestData.phase, requestData.subject);
		File fileRaw = new File(Config.selfFileDir, String.format("fixed/%s.json.raw", tableName));
		if (fileRaw.exists())
		{
			responseData.styles = SProblemStyle.loadList(JsonHelper.getJSONArray(FsUtils.readText(fileRaw)));
			success(response, responseData.saveToStr());
		}
		else
		{
			error(response, SErrorCode.unknown_error, "参数错误");
		}
	}

	public static void GetKnowledges(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetKnowledges requestData = SRequest_GetKnowledges.load(data);
		SResponse_GetKnowledges responseData = new SResponse_GetKnowledges();
		String tableName = AppConfig.getKnowledgeTableName(requestData.phase, requestData.subject);
		File fileRaw = new File(Config.selfFileDir, String.format("fixed/%s.json.raw", tableName));
		if (fileRaw.exists())
		{
			responseData.knowledges = SKnowledge.loadList(JsonHelper.getJSONArray(FsUtils.readText(fileRaw)));
			success(response, responseData.saveToStr());
		}
		else
		{
			error(response, SErrorCode.unknown_error, "参数错误");
		}
	}
}
