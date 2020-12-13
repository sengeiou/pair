package com.lys.config;

import com.lys.protobuf.SPhase;
import com.lys.protobuf.SSubject;

public class AppConfig
{
	public static final int ErrorCode = -100;

	public static final int Difficulty1 = 1;
	public static final int Difficulty2 = 2;
	public static final int Difficulty3 = 3;
	public static final int Difficulty4 = 4;
	public static final int Difficulty5 = 5;

	public static int getDifficulty(String name)
	{
		if (name.equals("容易"))
			return Difficulty1;
		else if (name.equals("较易"))
			return Difficulty2;
		else if (name.equals("一般"))
			return Difficulty3;
		else if (name.equals("较难"))
			return Difficulty4;
		else if (name.equals("困难"))
			return Difficulty5;
		return ErrorCode;
	}

	public static int getPhase(String name)
	{
		if (name.equals("初中"))
			return SPhase.Chu;
		else if (name.equals("高中"))
			return SPhase.Gao;
		return ErrorCode;
	}

	public static String getPhaseName(int phase)
	{
		switch (phase)
		{
		case SPhase.Chu:
			return "初中";
		case SPhase.Gao:
			return "高中";
		}
		return "未知";
	}

	public static int getSubject(String name)
	{
		if (name.equals("语文"))
			return SSubject.Yu;
		else if (name.equals("数学"))
			return SSubject.Shu;
		else if (name.equals("英语"))
			return SSubject.Wai;
		else if (name.equals("物理"))
			return SSubject.Li;
		else if (name.equals("化学"))
			return SSubject.Hua;
		else if (name.equals("生物"))
			return SSubject.Shen;
		else if (name.equals("政治"))
			return SSubject.Zhen;
		else if (name.equals("历史"))
			return SSubject.Shi;
		else if (name.equals("地理"))
			return SSubject.Di;
		return ErrorCode;
	}

	public static String getSubjectName(int subject)
	{
		switch (subject)
		{
		case SSubject.Yu:
			return "语文";
		case SSubject.Shu:
			return "数学";
		case SSubject.Wai:
			return "英语";
		case SSubject.Li:
			return "物理";
		case SSubject.Hua:
			return "化学";
		case SSubject.Shen:
			return "生物";
		case SSubject.Zhen:
			return "政治";
		case SSubject.Shi:
			return "历史";
		case SSubject.Di:
			return "地理";
		}
		return "未知";
	}

//	public static int getYear(String name)
//	{
//		if (name.equals("2019"))
//			return 2019;
//		else if (name.equals("2018"))
//			return 2018;
//		else if (name.equals("2017"))
//			return 2017;
//		else if (name.equals("2016"))
//			return 2016;
//		else if (name.equals("更早"))
//			return 0;
//		return ErrorCode;
//	}

	public static String getTopicStyleTableName(String phase, String subject)
	{
		int phaseCode = getPhase(phase);
		int subjectCode = getSubject(subject);
		if (phaseCode == ErrorCode)
			return null;
		if (subjectCode == ErrorCode)
			return null;
		return getTopicStyleTableName(phaseCode, subjectCode);
	}

	public static String getTopicStyleTableName(int phaseCode, int subjectCode)
	{
		return String.format("topic_style_%d_%d", phaseCode, subjectCode);
	}

	public static String getKnowledgeTableName(String phase, String subject)
	{
		int phaseCode = getPhase(phase);
		int subjectCode = getSubject(subject);
		if (phaseCode == ErrorCode)
			return null;
		if (subjectCode == ErrorCode)
			return null;
		return getKnowledgeTableName(phaseCode, subjectCode);
	}

	public static String getKnowledgeTableName(int phaseCode, int subjectCode)
	{
		return String.format("knowledge_%d_%d", phaseCode, subjectCode);
	}

	public static String getChapterTableName(String phase, String subject)
	{
		int phaseCode = getPhase(phase);
		int subjectCode = getSubject(subject);
		if (phaseCode == ErrorCode)
			return null;
		if (subjectCode == ErrorCode)
			return null;
		return getChapterTableName(phaseCode, subjectCode);
	}

	public static String getChapterTableName(int phaseCode, int subjectCode)
	{
		return String.format("chapter_%d_%d", phaseCode, subjectCode);
	}

	public static String getTopicTableName(String phase, String subject)
	{
		int phaseCode = getPhase(phase);
		int subjectCode = getSubject(subject);
		if (phaseCode == ErrorCode)
			return null;
		if (subjectCode == ErrorCode)
			return null;
		return getTopicTableName(phaseCode, subjectCode);
	}

	public static String getTopicTableName(int phaseCode, int subjectCode)
	{
		return String.format("topic_%d_%d", phaseCode, subjectCode);
	}

}
