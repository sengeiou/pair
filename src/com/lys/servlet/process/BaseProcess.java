package com.lys.servlet.process;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import com.lys.base.utils.AppDataTool;
import com.lys.base.utils.JsonHelper;
import com.lys.config.Config;
import com.lys.config.Table;
import com.lys.protobuf.SComment;
import com.lys.protobuf.SEvent;
import com.lys.protobuf.SGoods;
import com.lys.protobuf.SHandleId;
import com.lys.protobuf.SLiveTask;
import com.lys.protobuf.SMatter;
import com.lys.protobuf.SMatterDetail;
import com.lys.protobuf.SMatterHour;
import com.lys.protobuf.SOrder;
import com.lys.protobuf.SPTask;
import com.lys.protobuf.SProtocol;
import com.lys.protobuf.SSAppInfo;
import com.lys.protobuf.STaskGroup;
import com.lys.protobuf.STeach;
import com.lys.protobuf.STeachPage;
import com.lys.protobuf.STeachRecord;
import com.lys.protobuf.STopicRecord;
import com.lys.protobuf.SUser;
import com.lys.utils.HttpUtils;
import com.lys.utils.LOG;
import com.lys.utils.TextUtils;

public class BaseProcess
{
	public static final String TransEvt_FriendChange = "FriendChange";

	public static final String masterHead = "http://file.k12-eco.com/head/default_head.jpg";
	public static final String defaultHead = "http://file.k12-eco.com/head/default_head.jpg";

	public static final String defaultMst1 = "mst1";
	public static final String defaultMst2 = "mst2";

	public static final Table T = Table.instance;

	public static void result(HttpServletResponse response, SProtocol trans)
	{
		LOG.v("result:--------- " + trans.code + " --------" + (trans.code != 200 ? trans.msg : ""));
//		LOGJson.log(trans.saveToStr());
		try
		{
			PrintWriter pw = response.getWriter();
			pw.print(trans.saveToStr());
			pw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void success(HttpServletResponse response, String data)
	{
		SProtocol trans = new SProtocol();
		trans.code = 200;
		trans.data = data;
		result(response, trans);
	}

	public static void error(HttpServletResponse response, int errorCode, String errorMsg)
	{
		SProtocol trans = new SProtocol();
		trans.code = errorCode;
		trans.msg = errorMsg;
		result(response, trans);
	}

	public static String checkUrl(String url)
	{
		if (TextUtils.isEmpty(url))
		{
			return url;
		}
		else
		{
			if (url.startsWith("http://") || url.startsWith("https://"))
			{
				return url;
			}
			else
			{
				return Config.getUrlRoot() + url;
			}
		}
	}

	// ------------------------------------- xxxxx ---------------------------------------

	public static String doPostSolr(int handleId, String data)
	{
		String api = String.format("%s/%s/api", Config.URL_ROOT, "solr");
		SProtocol transSend = new SProtocol();
		transSend.handleId = handleId;
		transSend.data = data;
		LOG.v("上行Solr--------" + SHandleId.name(handleId) + "---------" + handleId + "---------");
//		LOGJson.log(transSend.saveToStr());
		String jsonStr = HttpUtils.doHttpPost(api, transSend.saveToStr());
		if (!TextUtils.isEmpty(jsonStr))
		{
			SProtocol transRcv = null;
			try
			{
				LOG.v("下行Solr--------" + SHandleId.name(handleId) + "---------" + handleId + "---------");
//				LOGJson.log(jsonStr);
				transRcv = SProtocol.load(jsonStr);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			if (transRcv != null)
			{
				if (transRcv.code == 200)
				{
					return transRcv.data;
				}
				else
				{
					return null;
				}
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	// ------------------------------------- pack ---------------------------------------

	public static SUser packUser(ResultSet rs) throws SQLException
	{
		return packUser(rs, false);
	}

	public static SUser packUser(ResultSet rs, boolean full) throws SQLException
	{
		SUser user = new SUser();
		user.id = rs.getString(T.user.id);
		user.userType = rs.getInt(T.user.userType);
		if (full)
			user.psw = rs.getString(T.user.psw);
		user.name = rs.getString(T.user.name);
		user.head = rs.getString(T.user.head);
		if (full)
			user.token = rs.getString(T.user.token);
		user.sex = rs.getInt(T.user.sex);
		user.grade = rs.getInt(T.user.grade);
		user.vipLevel = rs.getInt(T.user.vipLevel);
		user.vipTime = rs.getLong(T.user.vipTime);
		user.phone = rs.getString(T.user.phone);
		user.score = rs.getInt(T.user.score);
		user.cpId = rs.getString(T.user.cpId);
		return user;
	}

	public static SSAppInfo packAppInfo(ResultSet rs) throws SQLException
	{
		SSAppInfo info = new SSAppInfo();
		info.pkgName = rs.getString(T.app.pkgName);
		info.channel = rs.getString(T.app.channel);
		info.versionCode = rs.getInt(T.app.versionCode);
		info.versionName = rs.getString(T.app.versionName);
		info.probability = rs.getFloat(T.app.probability);
		info.name = rs.getString(T.app.name);
		info.size = rs.getLong(T.app.size);
		info.apkUrl = rs.getString(T.app.apkUrl);
		info.icoUrl = rs.getString(T.app.icoUrl);
		info.des = rs.getString(T.app.des);
		return info;
	}

	public static SPTask packTask(ResultSet rs) throws SQLException
	{
		SPTask task = new SPTask();
		task.id = rs.getString(T.task.id);
		task.userId = rs.getString(T.task.userId);
		task.type = rs.getInt(T.task.type);
		task.jobType = rs.getInt(T.task.jobType);
		task.group = rs.getString(T.task.group);
		task.name = rs.getString(T.task.name);
		task.note = rs.getString(T.task.note);
		task.createTime = rs.getLong(T.task.createTime);
		task.state = rs.getInt(T.task.state);
		task.text = rs.getString(T.task.text);
		task.comment = rs.getString(T.task.comment);
		task.overTime = rs.getLong(T.task.overTime);
		task.score = rs.getInt(T.task.score);
		task.open = rs.getInt(T.task.open);
		task.timesForWeb = rs.getInt(T.task.timesForWeb);
		task.lastModifyTime = rs.getLong(T.task.lastModifyTime);
		return task;
	}

	public static SLiveTask packLive(ResultSet rs) throws SQLException
	{
		SLiveTask live = new SLiveTask();
		live.id = rs.getString(T.live.id);
		live.actorId = rs.getString(T.live.actorId);
		live.name = rs.getString(T.live.name);
		live.des = rs.getString(T.live.des);
		live.cover = rs.getString(T.live.cover);
		live.video = rs.getString(T.live.video);
		live.duration = rs.getInt(T.live.duration);
		live.taskId = rs.getString(T.live.taskId);
		live.type = rs.getInt(T.live.type);
		live.userIds = AppDataTool.loadStringList(JsonHelper.getJSONArray(rs.getString(T.live.userIds)));
		live.startTime = rs.getLong(T.live.startTime);
		return live;
	}

	public static STopicRecord packTopicRecord(ResultSet rs) throws SQLException
	{
		STopicRecord topicRecord = new STopicRecord();
		topicRecord.userId = rs.getString(T.topicRecord.userId);
		topicRecord.topicId = rs.getString(T.topicRecord.topicId);
		topicRecord.fav = rs.getInt(T.topicRecord.fav);
		topicRecord.result = rs.getInt(T.topicRecord.result);
		topicRecord.time = rs.getLong(T.topicRecord.time);
		return topicRecord;
	}

	public static STeachRecord packTeachRecord(ResultSet rs) throws SQLException
	{
		STeachRecord teachRecord = new STeachRecord();
		teachRecord.teachId = rs.getString(T.teachRecord.teachId);
		teachRecord.userId = rs.getString(T.teachRecord.userId);
		teachRecord.isHost = rs.getInt(T.teachRecord.isHost) == 1;
		teachRecord.targetCount = rs.getInt(T.teachRecord.targetCount);
		teachRecord.targetIds = AppDataTool.loadStringList(JsonHelper.getJSONArray(rs.getString(T.teachRecord.targetIds)));
		teachRecord.taskId = rs.getString(T.teachRecord.taskId);
		teachRecord.startTime = rs.getLong(T.teachRecord.startTime);
		teachRecord.overTime = rs.getLong(T.teachRecord.overTime);
		teachRecord.teachPages = STeachPage.loadList(JsonHelper.getJSONArray(rs.getString(T.teachRecord.teachPages)));
		teachRecord.confirmMsg = rs.getString(T.teachRecord.confirmMsg);
		teachRecord.questionMatch = rs.getString(T.teachRecord.questionMatch);
		teachRecord.questionDiff = rs.getString(T.teachRecord.questionDiff);
		teachRecord.questionGot = rs.getString(T.teachRecord.questionGot);
		teachRecord.questionQuality = rs.getString(T.teachRecord.questionQuality);
		teachRecord.questionLike = rs.getString(T.teachRecord.questionLike);
		teachRecord.questionHot = rs.getString(T.teachRecord.questionHot);
		teachRecord.questionMind = rs.getString(T.teachRecord.questionMind);
		teachRecord.questionLogic = rs.getString(T.teachRecord.questionLogic);
		teachRecord.questionOther = rs.getString(T.teachRecord.questionOther);
		return teachRecord;
	}

	public static SMatter packMatter(ResultSet rs) throws SQLException
	{
		SMatter matter = new SMatter();
		matter.id = rs.getString(T.matter.id);
		matter.name = rs.getString(T.matter.name);
		matter.userId = rs.getString(T.matter.userId);
		matter.type = rs.getInt(T.matter.type);
		matter.place = rs.getInt(T.matter.place);
		matter.cover = rs.getString(T.matter.cover);
		matter.banner = rs.getString(T.matter.banner);
		matter.buyCount = rs.getInt(T.matter.buyCount);
		matter.moneyRaw = rs.getInt(T.matter.moneyRaw);
		matter.money = rs.getInt(T.matter.money);
		matter.hours = SMatterHour.loadList(JsonHelper.getJSONArray(rs.getString(T.matter.hours)));
		matter.sort = rs.getLong(T.matter.sort);
		matter.invalid = rs.getInt(T.matter.invalid) == 1;
		matter.details = SMatterDetail.loadList(JsonHelper.getJSONArray(rs.getString(T.matter.details)));
		return matter;
	}

	public static SComment packComment(ResultSet rs) throws SQLException
	{
		SComment comment = new SComment();
		comment.id = rs.getString(T.comment.id);
		comment.matterId = rs.getString(T.comment.matterId);
//		comment.userId = rs.getString(T.comment.userId);
		comment.star = rs.getInt(T.comment.star);
		comment.text = rs.getString(T.comment.text);
		comment.time = rs.getLong(T.comment.time);
		comment.pass = rs.getInt(T.comment.pass) == 1;
		return comment;
	}

	public static SGoods packGoods(ResultSet rs) throws SQLException
	{
		SGoods goods = new SGoods();
		goods.id = rs.getString(T.goods.id);
		goods.name = rs.getString(T.goods.name);
		goods.cover = rs.getString(T.goods.cover);
		goods.score = rs.getInt(T.goods.score);
		goods.buyCount = rs.getInt(T.goods.buyCount);
		goods.yuCount = rs.getInt(T.goods.yuCount);
		goods.sort = rs.getLong(T.goods.sort);
		goods.invalid = rs.getInt(T.goods.invalid) == 1;
		return goods;
	}

	public static STaskGroup packTaskGroup(ResultSet rs) throws SQLException
	{
		STaskGroup taskGroup = new STaskGroup();
		taskGroup.id = rs.getString(T.taskGroup.id);
		taskGroup.name = rs.getString(T.taskGroup.name);
		taskGroup.important = rs.getInt(T.taskGroup.important);
		taskGroup.difficulty = rs.getInt(T.taskGroup.difficulty);
		taskGroup.cover = rs.getString(T.taskGroup.cover);
		taskGroup.sort = rs.getLong(T.taskGroup.sort);
		return taskGroup;
	}

	public static STeach packTeach(ResultSet rs) throws SQLException
	{
		STeach teach = new STeach();
		teach.teacherId = rs.getString(T.teach.teacherId);
		teach.year = rs.getInt(T.teach.year);
		teach.month = rs.getInt(T.teach.month);
		teach.day = rs.getInt(T.teach.day);
		teach.block = rs.getInt(T.teach.block);
		teach.flag = rs.getInt(T.teach.flag);
		teach.studentId = rs.getString(T.teach.studentId);
		return teach;
	}

	public static SOrder packOrder(ResultSet rs) throws SQLException
	{
		SOrder order = new SOrder();
		order.id = rs.getString(T.order.id);
		order.userId = rs.getString(T.order.userId);
//		order.goodsId = rs.getString(T.order.goodsId);
		order.count = rs.getInt(T.order.count);
		order.score = rs.getInt(T.order.score);
		order.time = rs.getLong(T.order.time);
		order.state = rs.getInt(T.order.state);
		order.name = rs.getString(T.order.name);
		order.phone = rs.getString(T.order.phone);
		order.address = rs.getString(T.order.address);
		return order;
	}

	public static SEvent packEvent(ResultSet rs) throws SQLException
	{
		SEvent event = new SEvent();
		event.action = rs.getString(T.event.action);
		event.target = rs.getString(T.event.target);
		event.des = rs.getString(T.event.des);
		event.time = rs.getLong(T.event.time);
		return event;
	}

	// ------------------------------------- XXXXXXXXX ---------------------------------------

//	public static void xxxx(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
//	{
//		SRequest_ requestData = SRequest_.load(data);
//		SResponse_ responseData = new SResponse_();
//		success(response, responseData.saveToStr());
//	}

}
