package com.lys.servlet.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.SComment;
import com.lys.protobuf.SMatter;
import com.lys.protobuf.SMatterDetail;
import com.lys.protobuf.SMatterHour;
import com.lys.protobuf.SMatterListType;
import com.lys.protobuf.SMatterPlace;
import com.lys.protobuf.SMatterType;
import com.lys.protobuf.SRequest_AddModifyBuy;
import com.lys.protobuf.SRequest_AddModifyComment;
import com.lys.protobuf.SRequest_AddModifyMatter;
import com.lys.protobuf.SRequest_DeleteBuy;
import com.lys.protobuf.SRequest_DeleteComment;
import com.lys.protobuf.SRequest_DeleteMatter;
import com.lys.protobuf.SRequest_GetBuyList;
import com.lys.protobuf.SRequest_GetCommentList;
import com.lys.protobuf.SRequest_GetMatterList;
import com.lys.protobuf.SRequest_SwapMatter;
import com.lys.protobuf.SResponse_AddModifyBuy;
import com.lys.protobuf.SResponse_AddModifyComment;
import com.lys.protobuf.SResponse_AddModifyMatter;
import com.lys.protobuf.SResponse_DeleteBuy;
import com.lys.protobuf.SResponse_DeleteComment;
import com.lys.protobuf.SResponse_DeleteMatter;
import com.lys.protobuf.SResponse_GetBuyList;
import com.lys.protobuf.SResponse_GetCommentList;
import com.lys.protobuf.SResponse_GetMatterList;
import com.lys.protobuf.SResponse_SwapMatter;
import com.lys.utils.CommonUtils;
import com.lys.utils.TextUtils;

public class ProcessShop extends BaseProcess
{
	private static List<SMatter> selectMatterList(String sql) throws Exception
	{
		final List<SMatter> matters = new ArrayList<>();
		DBHelper.exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					matters.add(packMatter(rs));
				}
			}
		});
		return matters;
	}

	private static List<SComment> selectCommentList(String sql) throws Exception
	{
		final List<SComment> comments = new ArrayList<>();
		DBHelper.exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					SComment comment = packComment(rs);
					comment.user = packUser(rs);
					comments.add(comment);
				}
			}
		});
		return comments;
	}

	public static void GetMatterList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetMatterList requestData = SRequest_GetMatterList.load(data);
		SResponse_GetMatterList responseData = new SResponse_GetMatterList();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s", T.matter));

		if (requestData.type == SMatterListType.Home)
		{
			sb.append(String.format(" where (%s = %s or %s = %s)", T.matter.place, SMatterPlace.Banner, T.matter.place, SMatterPlace.Main));
		}
		else if (requestData.type == SMatterListType.Class)
		{
			sb.append(String.format(" where %s = %s", T.matter.type, SMatterType.Class));
		}
		else if (requestData.type == SMatterListType.Pair)
		{
			sb.append(String.format(" where %s = %s", T.matter.type, SMatterType.Pair));
		}

		if (!requestData.containInvalid)
			sb.append(String.format(" and %s = %s", T.matter.invalid, 0));

		if (requestData.sort > 0)
		{
			if (requestData.prev)
				sb.append(String.format(" and %s > %s", T.matter.sort, requestData.sort));
			else
				sb.append(String.format(" and %s < %s", T.matter.sort, requestData.sort));
		}

		if (requestData.prev)
			sb.append(String.format(" order by %s desc", T.matter.sort));
		else
			sb.append(String.format(" order by %s asc", T.matter.sort));

		if (requestData.pageSize > 0)
			sb.append(String.format(" limit 0, %s", requestData.pageSize));

		sb.append(String.format(";"));

		responseData.matters = selectMatterList(sb.toString());

		success(response, responseData.saveToStr());
	}

	public static void AddModifyMatter(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_AddModifyMatter requestData = SRequest_AddModifyMatter.load(data);
		SResponse_AddModifyMatter responseData = new SResponse_AddModifyMatter();

		if (TextUtils.isEmpty(requestData.matter.id))
		{
			// Add
			DBHelper.insert(T.matter, //
					T.matter.id, CommonUtils.uuid(), //
					T.matter.name, requestData.matter.name, //
					T.matter.userId, requestData.matter.userId, //
					T.matter.type, requestData.matter.type, //
					T.matter.place, requestData.matter.place, //
					T.matter.cover, requestData.matter.cover, //
					T.matter.banner, requestData.matter.banner, //
					T.matter.buyCount, requestData.matter.buyCount, //
					T.matter.moneyRaw, requestData.matter.moneyRaw, //
					T.matter.money, requestData.matter.money, //
					T.matter.hours, SMatterHour.saveList(requestData.matter.hours).toString(), //
					T.matter.sort, System.currentTimeMillis(), //
					T.matter.invalid, requestData.matter.invalid ? 1 : 0, //
					T.matter.details, SMatterDetail.saveList(requestData.matter.details).toString());
		}
		else
		{
			// Modify
			DBHelper.update(T.matter, DBHelper.set(T.matter.name, requestData.matter.name, //
					T.matter.userId, requestData.matter.userId, //
					T.matter.type, requestData.matter.type, //
					T.matter.place, requestData.matter.place, //
					T.matter.cover, requestData.matter.cover, //
					T.matter.banner, requestData.matter.banner, //
					T.matter.buyCount, requestData.matter.buyCount, //
					T.matter.moneyRaw, requestData.matter.moneyRaw, //
					T.matter.money, requestData.matter.money, //
					T.matter.hours, SMatterHour.saveList(requestData.matter.hours).toString(), //
					T.matter.sort, requestData.matter.sort, //
					T.matter.invalid, requestData.matter.invalid ? 1 : 0, //
					T.matter.details, SMatterDetail.saveList(requestData.matter.details).toString()), T.matter.id, requestData.matter.id);
		}

		success(response, responseData.saveToStr());
	}

	public static void SwapMatter(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SwapMatter requestData = SRequest_SwapMatter.load(data);
		SResponse_SwapMatter responseData = new SResponse_SwapMatter();
		DBHelper.update(T.matter, DBHelper.set(T.matter.sort, requestData.matter2.sort), T.matter.id, requestData.matter1.id);
		DBHelper.update(T.matter, DBHelper.set(T.matter.sort, requestData.matter1.sort), T.matter.id, requestData.matter2.id);
		success(response, responseData.saveToStr());
	}

	public static void DeleteMatter(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_DeleteMatter requestData = SRequest_DeleteMatter.load(data);
		SResponse_DeleteMatter responseData = new SResponse_DeleteMatter();
		DBHelper.delete(T.matter, T.matter.id, requestData.matterId);
		success(response, responseData.saveToStr());
	}

	public static void GetBuyList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetBuyList requestData = SRequest_GetBuyList.load(data);
		SResponse_GetBuyList responseData = new SResponse_GetBuyList();
		success(response, responseData.saveToStr());
	}

	public static void AddModifyBuy(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_AddModifyBuy requestData = SRequest_AddModifyBuy.load(data);
		SResponse_AddModifyBuy responseData = new SResponse_AddModifyBuy();
		success(response, responseData.saveToStr());
	}

	public static void DeleteBuy(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_DeleteBuy requestData = SRequest_DeleteBuy.load(data);
		SResponse_DeleteBuy responseData = new SResponse_DeleteBuy();
		success(response, responseData.saveToStr());
	}

	public static void GetCommentList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetCommentList requestData = SRequest_GetCommentList.load(data);
		SResponse_GetCommentList responseData = new SResponse_GetCommentList();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s left join %s on %s.%s = %s.%s", T.comment, //
				T.user, //
				T.comment, T.comment.userId, //
				T.user, T.user.id));

		sb.append(String.format(" where %s = '%s'", T.comment.matterId, requestData.matterId));

		if (!requestData.containAll)
			sb.append(String.format(" and %s = %s", T.comment.pass, 1));

		if (requestData.time > 0)
		{
			if (requestData.prev)
				sb.append(String.format(" and %s > %s", T.comment.time, requestData.time));
			else
				sb.append(String.format(" and %s < %s", T.comment.time, requestData.time));
		}

		if (requestData.prev)
			sb.append(String.format(" order by %s asc", T.comment.time));
		else
			sb.append(String.format(" order by %s desc", T.comment.time));

		if (requestData.pageSize > 0)
			sb.append(String.format(" limit 0, %s", requestData.pageSize));

		sb.append(String.format(";"));

		responseData.comments = selectCommentList(sb.toString());

		success(response, responseData.saveToStr());
	}

	public static void AddModifyComment(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_AddModifyComment requestData = SRequest_AddModifyComment.load(data);
		SResponse_AddModifyComment responseData = new SResponse_AddModifyComment();

		if (TextUtils.isEmpty(requestData.comment.id))
		{
			// Add
			DBHelper.insert(T.comment, //
					T.comment.id, CommonUtils.uuid(), //
					T.comment.matterId, requestData.comment.matterId, //
					T.comment.userId, requestData.comment.user.id, //
					T.comment.star, requestData.comment.star, //
					T.comment.text, requestData.comment.text, //
					T.comment.time, System.currentTimeMillis(), //
					T.comment.pass, requestData.comment.pass ? 1 : 0);
		}
		else
		{
			// Modify
			DBHelper.update(T.comment, DBHelper.set(T.comment.pass, requestData.comment.pass ? 1 : 0), T.comment.id, requestData.comment.id);
		}

		success(response, responseData.saveToStr());
	}

	public static void DeleteComment(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_DeleteComment requestData = SRequest_DeleteComment.load(data);
		SResponse_DeleteComment responseData = new SResponse_DeleteComment();
		DBHelper.delete(T.comment, T.comment.id, requestData.commentId);
		success(response, responseData.saveToStr());
	}

}
