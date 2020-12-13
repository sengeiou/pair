package com.lys.manager;

import java.util.concurrent.ConcurrentHashMap;

import com.lys.config.Table;
import com.lys.mysql.DBHelper;

public class TopicManager
{
	private static final Object lock = new Object();

	private static final Table T = Table.instance;

	private static final ConcurrentHashMap<String, String> insureMap = new ConcurrentHashMap<String, String>();

	public static void insureKnowledgeTableExists(String tableName)
	{
		synchronized (lock)
		{
			if (!insureMap.containsKey(tableName))
			{
				DBHelper.exeSql(String.format("create table if not exists %s(" + T.knowledge.id + " varchar(48)" + //
						", " + T.knowledge.parent + " varchar(48)" + //
						", " + T.knowledge.name + " varchar(200)" + //
						", " + T.knowledge.leaf + " int" + //
						");", tableName), null);
				insureMap.put(tableName, tableName);
			}
		}
	}

	public static void insureChapterTableExists(String tableName)
	{
		synchronized (lock)
		{
			if (!insureMap.containsKey(tableName))
			{
				DBHelper.exeSql(String.format("create table if not exists %s(" + T.chapter.id + " varchar(48)" + //
						", " + T.chapter.parent + " varchar(48)" + //
						", " + T.chapter.name + " varchar(200)" + //
						", " + T.chapter.leaf + " int" + //
						");", tableName), null);
				insureMap.put(tableName, tableName);
			}
		}
	}

	public static void insureTopicTableExists(String tableName)
	{
		synchronized (lock)
		{
			if (!insureMap.containsKey(tableName))
			{
				DBHelper.exeSql(String.format("create table if not exists %s(" + T.topic.id + " varchar(48)" + //
						", " + T.topic.phase + " int" + //
						", " + T.topic.subject + " int" + //
						", " + T.topic.material + " varchar(100)" + //
						", " + T.topic.style + " varchar(50)" + //
						", " + T.topic.diff + " int" + //
						", " + T.topic.area + " text" + //
						", " + T.topic.year + " text" + //
						", " + T.topic.knowledges + " text" + //
						", " + T.topic.zujuan + " int" + //
						", " + T.topic.zuoda + " int" + //
						", " + T.topic.defen + " float" + //
						", " + T.topic.nandu + " varchar(20)" + //
						", " + T.topic.chapters + " text" + //
						", " + T.topic.content + " text" + //
						", " + T.topic.answer + " text" + //
						", " + T.topic.parse + " text" + //
						", " + T.topic.reportError + " int" + //
						", " + T.topic.reportGood + " int" + //
						", " + T.topic.remark + " text" + //
						");", tableName), null);
				insureMap.put(tableName, tableName);
			}
		}
	}

	public static void insureEventTableExists(String tableName)
	{
		synchronized (lock)
		{
			if (!insureMap.containsKey(tableName))
			{
				DBHelper.exeSql(String.format("create table if not exists %s(" + T.event.action + " varchar(256)" + //
						", " + T.event.target + " varchar(256)" + //
						", " + T.event.des + " text" + //
						", " + T.event.time + " bigint" + //
						", " + T.event.ts + " timestamp" + //
						");", tableName), null);
				insureMap.put(tableName, tableName);
			}
		}
	}
}
