package com.lys.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.lys.config.Config;
import com.lys.config.Table;
import com.lys.mysql.DBHelper;
import com.lys.protobuf.SUserType;
import com.lys.servlet.process.BaseProcess;
import com.lys.servlet.process.ProcessRun;
import com.lys.servlet.process.ProcessTask;
import com.lys.utils.LOG;
import com.lys.utils.SVNManager;

//@WebServlet("/start")//如果这里放开，当初次请求这个Servlet时还会再init一次，所以这里不要放开，避免重复初始化
public class start extends HttpServlet
{
	private static final Table T = Table.instance;

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		LOG.v("-------------- start ----------------");
		LOG.v(String.format("%s/%s/api", Config.URL_ROOT, Config.projectName));

		ProcessRun.sServerState.startTime = System.currentTimeMillis();

		SVNManager.init(Config.svnUrl, Config.svnAccount, Config.svnPsw);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.app.pkgName + " varchar(100)" + //
				", " + T.app.channel + " varchar(100)" + //
				", " + T.app.versionCode + " int" + //
				", " + T.app.versionName + " varchar(100)" + //
				", " + T.app.probability + " float not null default 1" + //
				", " + T.app.name + " varchar(100)" + //
				", " + T.app.size + " bigint" + //
				", " + T.app.apkUrl + " varchar(256)" + //
				", " + T.app.icoUrl + " varchar(256)" + //
				", " + T.app.des + " text" + //
				", " + T.app.ts + " timestamp" + //
				");", T.app), null);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.device.deviceId + " varchar(100)" + //
				", " + T.device.clientVersion + " varchar(100)" + //
				", " + T.device.userId + " varchar(48)" + //
				", " + T.device.loginCount + " int" + //
				", " + T.device.loginResult + " varchar(100)" + //
				", " + T.device.ts + " timestamp" + //
				");", T.device), null);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.user.id + " varchar(48)" + //
				", " + T.user.userType + " int" + //
				", " + T.user.psw + " varchar(30)" + //
				", " + T.user.name + " varchar(200)" + //
				", " + T.user.head + " varchar(200)" + //
				", " + T.user.token + " varchar(256)" + //
				", " + T.user.sex + " int default 1" + //
				", " + T.user.grade + " int default 0" + //
				", " + T.user.vipLevel + " int default 0" + //
				", " + T.user.vipTime + " bigint default 0" + //
				", " + T.user.phone + " varchar(30)" + //
				", " + T.user.score + " int default 0" + //
				", " + T.user.cpId + " text" + //
				", " + T.user.ts + " timestamp" + //
				");", T.user), null);
		DBHelper.addColumn(T.user.toString(), T.user.sex, " int default 1", T.user.token);
		DBHelper.addColumn(T.user.toString(), T.user.grade, " int default 0", T.user.sex);
		DBHelper.addColumn(T.user.toString(), T.user.vipLevel, " int default 0", T.user.grade);
		DBHelper.addColumn(T.user.toString(), T.user.vipTime, " bigint default 0", T.user.vipLevel);
		DBHelper.addColumn(T.user.toString(), T.user.phone, " varchar(30)", T.user.vipTime);
		DBHelper.addColumn(T.user.toString(), T.user.score, " int default 0", T.user.phone);
		DBHelper.addColumn(T.user.toString(), T.user.cpId, " text", T.user.score);
//		DBHelper.update(T.user, DBHelper.set(T.user.token, ""));

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.friend.userId + " varchar(48)" + //
				", " + T.friend.friendId + " varchar(48)" + //
				", " + T.friend.group + " varchar(100)" + //
				", " + T.friend.ts + " timestamp" + //
				");", T.friend), null);
		DBHelper.addColumn(T.friend.toString(), T.friend.group, " varchar(100)", T.friend.friendId);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.task.id + " varchar(48)" + //
				", " + T.task.userId + " varchar(48)" + //
				", " + T.task.sendUserId + " varchar(48)" + //
				", " + T.task.type + " int default 0" + //
				", " + T.task.jobType + " int default 0" + //
				", " + T.task.group + " varchar(100)" + //
				", " + T.task.name + " varchar(100)" + //
				", " + T.task.note + " text" + //
				", " + T.task.createTime + " bigint" + //
				", " + T.task.state + " int" + //
				", " + T.task.text + " text" + //
				", " + T.task.comment + " text" + //
				", " + T.task.overTime + " bigint default 0" + //
				", " + T.task.score + " int default 0" + //
				", " + T.task.open + " int default 0" + //
				", " + T.task.timesForWeb + " int default 0" + //
				", " + T.task.lastModifyTime + " bigint" + //
				", " + T.task.ts + " timestamp" + //
				");", T.task), null);
		DBHelper.addColumn(T.task.toString(), T.task.timesForWeb, " int default 0", T.task.state);
		DBHelper.addColumn(T.task.toString(), T.task.note, " text", T.task.name);
		DBHelper.addColumn(T.task.toString(), T.task.text, " text", T.task.state);
		DBHelper.addColumn(T.task.toString(), T.task.comment, " text", T.task.text);
		DBHelper.addColumn(T.task.toString(), T.task.overTime, " bigint default 0", T.task.comment);
		DBHelper.addColumn(T.task.toString(), T.task.score, " int default 0", T.task.overTime);
		DBHelper.addColumn(T.task.toString(), T.task.open, " int default 0", T.task.score);
		DBHelper.addColumn(T.task.toString(), T.task.jobType, " int default 0", T.task.type);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.live.id + " varchar(48)" + //
				", " + T.live.actorId + " varchar(48)" + //
				", " + T.live.name + " varchar(200)" + //
				", " + T.live.des + " text" + //
				", " + T.live.cover + " varchar(256)" + //
				", " + T.live.video + " varchar(256)" + //
				", " + T.live.duration + " int default 0" + //
				", " + T.live.taskId + " varchar(48)" + //
				", " + T.live.type + " int default 0" + //
				", " + T.live.userIds + " text" + //
				", " + T.live.startTime + " bigint default 0" + //
				", " + T.live.ts + " timestamp" + //
				");", T.live), null);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.topicRecord.userId + " varchar(48)" + //
				", " + T.topicRecord.topicId + " varchar(48)" + //
				", " + T.topicRecord.fav + " int" + //
				", " + T.topicRecord.result + " int" + //
				", " + T.topicRecord.time + " bigint" + //
				", " + T.topicRecord.ts + " timestamp" + //
				");", T.topicRecord), null);
//		DBHelper.addColumn(T.topicRecord.toString(), T.topicRecord.time, "bigint", T.topicRecord.result);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.teachRecord.teachId + " varchar(48)" + //
				", " + T.teachRecord.userId + " varchar(48)" + //
				", " + T.teachRecord.isHost + " int" + //
				", " + T.teachRecord.targetCount + " int" + //
				", " + T.teachRecord.targetIds + " text" + //
				", " + T.teachRecord.taskId + " varchar(48)" + //
				", " + T.teachRecord.startTime + " bigint default 0" + //
				", " + T.teachRecord.overTime + " bigint default 0" + //
				", " + T.teachRecord.teachPages + " text" + //
				", " + T.teachRecord.confirmMsg + " text" + //
				", " + T.teachRecord.questionMatch + " varchar(40)" + //
				", " + T.teachRecord.questionDiff + " varchar(40)" + //
				", " + T.teachRecord.questionGot + " varchar(40)" + //
				", " + T.teachRecord.questionQuality + " varchar(40)" + //
				", " + T.teachRecord.questionLike + " varchar(40)" + //
				", " + T.teachRecord.questionHot + " varchar(40)" + //
				", " + T.teachRecord.questionMind + " varchar(40)" + //
				", " + T.teachRecord.questionLogic + " varchar(40)" + //
				", " + T.teachRecord.questionOther + " text" + //
				", " + T.teachRecord.ts + " timestamp" + //
				");", T.teachRecord), null);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.matter.id + " varchar(48)" + //
				", " + T.matter.name + " varchar(200)" + //
				", " + T.matter.userId + " varchar(48)" + //
				", " + T.matter.type + " int" + //
				", " + T.matter.place + " int" + //
				", " + T.matter.cover + " varchar(200)" + //
				", " + T.matter.banner + " varchar(200)" + //
				", " + T.matter.buyCount + " int" + //
				", " + T.matter.moneyRaw + " int" + //
				", " + T.matter.money + " int" + //
				", " + T.matter.hours + " text" + //
				", " + T.matter.sort + " bigint" + //
				", " + T.matter.invalid + " int" + //
				", " + T.matter.details + " text" + //
				", " + T.matter.ts + " timestamp" + //
				");", T.matter), null);
		DBHelper.addColumn(T.matter.toString(), T.matter.name, "varchar(200)", T.matter.id);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.comment.id + " varchar(48)" + //
				", " + T.comment.matterId + " varchar(48)" + //
				", " + T.comment.userId + " varchar(48)" + //
				", " + T.comment.star + " int" + //
				", " + T.comment.text + " text" + //
				", " + T.comment.time + " bigint" + //
				", " + T.comment.pass + " int" + //
				", " + T.comment.ts + " timestamp" + //
				");", T.comment), null);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.buy.userId + " varchar(48)" + //
				", " + T.buy.matterId + " varchar(48)" + //
				", " + T.buy.hourBuy + " float" + //
				", " + T.buy.hourGive + " float" + //
//				", " + T.buy.status + " int" + //
				", " + T.buy.ts + " timestamp" + //
				");", T.buy), null);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.goods.id + " varchar(48)" + //
				", " + T.goods.name + " varchar(200)" + //
				", " + T.goods.cover + " varchar(200)" + //
				", " + T.goods.score + " int" + //
				", " + T.goods.buyCount + " int" + //
				", " + T.goods.yuCount + " int" + //
				", " + T.goods.sort + " bigint" + //
				", " + T.goods.invalid + " int" + //
				", " + T.goods.ts + " timestamp" + //
				");", T.goods), null);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.taskGroup.id + " varchar(48)" + //
				", " + T.taskGroup.name + " varchar(200)" + //
				", " + T.taskGroup.important + " int" + //
				", " + T.taskGroup.difficulty + " int" + //
				", " + T.taskGroup.cover + " varchar(200)" + //
				", " + T.taskGroup.sort + " bigint" + //
				", " + T.taskGroup.ts + " timestamp" + //
				");", T.taskGroup), null);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.teach.teacherId + " varchar(48)" + //
				", " + T.teach.year + " int" + //
				", " + T.teach.month + " int" + //
				", " + T.teach.day + " int" + //
				", " + T.teach.block + " int" + //
				", " + T.teach.flag + " int" + //
				", " + T.teach.studentId + " varchar(48)" + //
				", " + T.teach.ts + " timestamp" + //
				");", T.teach), null);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.order.id + " varchar(48)" + //
				", " + T.order.userId + " varchar(48)" + //
				", " + T.order.goodsId + " varchar(48)" + //
				", " + T.order.count + " int" + //
				", " + T.order.score + " int" + //
				", " + T.order.time + " bigint" + //
				", " + T.order.state + " int" + //
				", " + T.order.name + " varchar(200)" + //
				", " + T.order.phone + " varchar(30)" + //
				", " + T.order.address + " text" + //
				", " + T.order.ts + " timestamp" + //
				");", T.order), null);

//		DBHelper.exeSql(String.format("create table if not exists %s(" + T.md5.md5 + " varchar(48)" + //
//				", " + T.md5.path + " varchar(256)" + //
//				", " + T.md5.ts + " timestamp" + //
//				");", T.md5), null);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.zhixue.id + " varchar(48)" + //
				", " + T.zhixue.phase + " varchar(100)" + //
				", " + T.zhixue.subject + " varchar(100)" + //
				", " + T.zhixue.material + " varchar(100)" + //
//				", " + T.zhixue.chapterPath + " text" + //
//				", " + T.zhixue.style + " varchar(100)" + //
				", " + T.zhixue.diff + " varchar(100)" + //
				", " + T.zhixue.area + " varchar(100)" + //
				", " + T.zhixue.year + " varchar(100)" + //
				", " + T.zhixue.currChapterPath + " text" + //
				", " + T.zhixue.currPage + " int" + //
				", " + T.zhixue.totalPage + " int" + //
				", " + T.zhixue.sort + " int" + //
				", " + T.zhixue.deviceId + " varchar(100)" + //
				", " + T.zhixue.ts + " timestamp" + //
				");", T.zhixue), null);

		DBHelper.exeSql(String.format("create table if not exists %s(" + T.zhixueAccount.account + " varchar(100)" + //
				", " + T.zhixueAccount.psw + " varchar(100)" + //
				", " + T.zhixueAccount.state + " varchar(100)" + //
				", " + T.zhixueAccount.deviceId + " varchar(100)" + //
				", " + T.zhixueAccount.ts + " timestamp" + //
				");", T.zhixueAccount), null);

		AddUserIfNotExists("root", SUserType.SupterMaster, "0813", "超级管理员", BaseProcess.masterHead);
		AddUserIfNotExists("mst1", SUserType.Master, "123", "管理员1", BaseProcess.masterHead);
		AddUserIfNotExists("mst2", SUserType.Master, "123", "管理员2", BaseProcess.masterHead);
		AddUserIfNotExists("mst3", SUserType.Master, "123", "管理员3", BaseProcess.masterHead);

		try
		{
			ProcessTask.initRandomOpenTask();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

//		ProcessZhiXue.tmpProcessOver();
//		ProcessZhiXue.startProcess();

//		DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, "", T.zhixue.currChapterPath, "", T.zhixue.currPage, 1), T.zhixue.diff, "困难", T.zhixue.area, "广东", T.zhixue.year, "2017");
//		DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, "", T.zhixue.currChapterPath, "", T.zhixue.currPage, 1), T.zhixue.diff, "困难", T.zhixue.area, "广东", T.zhixue.year, "更早");
//		DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, "", T.zhixue.currChapterPath, "", T.zhixue.currPage, 1), T.zhixue.diff, "困难", T.zhixue.area, "广西", T.zhixue.year, "2016");
//		DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, "", T.zhixue.currChapterPath, "", T.zhixue.currPage, 1), T.zhixue.diff, "困难", T.zhixue.area, "江西", T.zhixue.year, "2016");
//		DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, "", T.zhixue.currChapterPath, "", T.zhixue.currPage, 1), T.zhixue.diff, "困难", T.zhixue.area, "江西", T.zhixue.year, "2018");
//		DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, "", T.zhixue.currChapterPath, "", T.zhixue.currPage, 1), T.zhixue.diff, "困难", T.zhixue.area, "江西", T.zhixue.year, "2019");
//		DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, "", T.zhixue.currChapterPath, "", T.zhixue.currPage, 1), T.zhixue.diff, "困难", T.zhixue.area, "江西", T.zhixue.year, "更早");
//		DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, "", T.zhixue.currChapterPath, "", T.zhixue.currPage, 1), T.zhixue.diff, "困难", T.zhixue.area, "山东", T.zhixue.year, "2018");
//		DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, "", T.zhixue.currChapterPath, "", T.zhixue.currPage, 1), T.zhixue.diff, "困难", T.zhixue.area, "山西", T.zhixue.year, "2016");

//		DBHelper.update(T.zhixue, DBHelper.set(T.zhixue.deviceId, "", T.zhixue.currChapterPath, "", T.zhixue.currPage, 1), T.zhixue.diff, "较难", T.zhixue.area, "山东", T.zhixue.year, "更早");

	}

	private void AddUserIfNotExists(String userId, Integer userType, String psw, String name, String head)
	{
		if (!DBHelper.hasRecord(T.user, T.user.id, userId))
		{
			DBHelper.insert(T.user, //
					T.user.id, userId, //
					T.user.userType, userType, //
					T.user.psw, psw, //
					T.user.name, name, //
					T.user.head, head);
		}
	}

}
