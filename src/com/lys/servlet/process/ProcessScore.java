package com.lys.servlet.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.mysql.DBHelper;
import com.lys.mysql.DBHelper.OnCallback;
import com.lys.protobuf.SErrorCode;
import com.lys.protobuf.SGoods;
import com.lys.protobuf.SOrder;
import com.lys.protobuf.SOrderState;
import com.lys.protobuf.SRequest_AddModifyGoods;
import com.lys.protobuf.SRequest_AddModifyTaskGroup;
import com.lys.protobuf.SRequest_AddOrder;
import com.lys.protobuf.SRequest_DeleteGoods;
import com.lys.protobuf.SRequest_DeleteTaskGroup;
import com.lys.protobuf.SRequest_GetGoodsList;
import com.lys.protobuf.SRequest_GetOrderList;
import com.lys.protobuf.SRequest_GetTaskGroupList;
import com.lys.protobuf.SRequest_GetTeachList;
import com.lys.protobuf.SRequest_ModifyOrderState;
import com.lys.protobuf.SRequest_ModifyTeach;
import com.lys.protobuf.SRequest_SwapGoods;
import com.lys.protobuf.SRequest_SwapTaskGroup;
import com.lys.protobuf.SResponse_AddModifyGoods;
import com.lys.protobuf.SResponse_AddModifyTaskGroup;
import com.lys.protobuf.SResponse_AddOrder;
import com.lys.protobuf.SResponse_DeleteGoods;
import com.lys.protobuf.SResponse_DeleteTaskGroup;
import com.lys.protobuf.SResponse_GetGoodsList;
import com.lys.protobuf.SResponse_GetOrderList;
import com.lys.protobuf.SResponse_GetTaskGroupList;
import com.lys.protobuf.SResponse_GetTeachList;
import com.lys.protobuf.SResponse_ModifyOrderState;
import com.lys.protobuf.SResponse_ModifyTeach;
import com.lys.protobuf.SResponse_SwapGoods;
import com.lys.protobuf.SResponse_SwapTaskGroup;
import com.lys.protobuf.STaskGroup;
import com.lys.protobuf.STeach;
import com.lys.protobuf.STeachFlag;
import com.lys.protobuf.SUser;
import com.lys.utils.CommonUtils;
import com.lys.utils.RandomHelper;
import com.lys.utils.TextUtils;

public class ProcessScore extends BaseProcess
{
	private static List<SGoods> selectGoodsList(String sql) throws Exception
	{
		final List<SGoods> goodsList = new ArrayList<>();
		DBHelper.exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					goodsList.add(packGoods(rs));
				}
			}
		});
		return goodsList;
	}

	private static SGoods selectGoods(String goodsId) throws Exception
	{
		final List<SGoods> goodsList = new ArrayList<>();
		DBHelper.select(T.goods, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				if (rs.next())
				{
					goodsList.add(packGoods(rs));
				}
			}
		}, T.goods.id, goodsId);
		if (goodsList.size() > 0)
			return goodsList.get(0);
		else
			return null;
	}

	private static List<SOrder> selectOrderList(String sql) throws Exception
	{
		final List<SOrder> orders = new ArrayList<>();
		DBHelper.exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					SOrder order = packOrder(rs);
					order.goods = packGoods(rs);
					orders.add(order);
				}
			}
		});
		return orders;
	}

	public static void GetGoodsList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetGoodsList requestData = SRequest_GetGoodsList.load(data);
		SResponse_GetGoodsList responseData = new SResponse_GetGoodsList();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s", T.goods));

		if (!requestData.containInvalid)
			sb.append(String.format(" where %s = %s", T.goods.invalid, 0));

		sb.append(String.format(" order by %s asc", T.goods.sort));

		sb.append(String.format(";"));

		responseData.goodsList = selectGoodsList(sb.toString());

		success(response, responseData.saveToStr());
	}

	public static void AddModifyGoods(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_AddModifyGoods requestData = SRequest_AddModifyGoods.load(data);
		SResponse_AddModifyGoods responseData = new SResponse_AddModifyGoods();

		if (TextUtils.isEmpty(requestData.goods.id))
		{
			// Add
			DBHelper.insert(T.goods, //
					T.goods.id, CommonUtils.uuid(), //
					T.goods.name, requestData.goods.name, //
					T.goods.cover, requestData.goods.cover, //
					T.goods.score, requestData.goods.score, //
					T.goods.buyCount, 0, //
					T.goods.yuCount, requestData.goods.yuCount, //
					T.goods.sort, System.currentTimeMillis(), //
					T.goods.invalid, requestData.goods.invalid ? 1 : 0);
		}
		else
		{
			// Modify
			DBHelper.update(T.goods, DBHelper.set(T.goods.name, requestData.goods.name, //
					T.goods.cover, requestData.goods.cover, //
					T.goods.score, requestData.goods.score, //
					T.goods.yuCount, requestData.goods.yuCount, //
					T.goods.sort, requestData.goods.sort, //
					T.goods.invalid, requestData.goods.invalid ? 1 : 0), T.goods.id, requestData.goods.id);
		}

		success(response, responseData.saveToStr());
	}

	public static void SwapGoods(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SwapGoods requestData = SRequest_SwapGoods.load(data);
		SResponse_SwapGoods responseData = new SResponse_SwapGoods();
		DBHelper.update(T.goods, DBHelper.set(T.goods.sort, requestData.goods2.sort), T.goods.id, requestData.goods1.id);
		DBHelper.update(T.goods, DBHelper.set(T.goods.sort, requestData.goods1.sort), T.goods.id, requestData.goods2.id);
		success(response, responseData.saveToStr());
	}

	public static void DeleteGoods(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_DeleteGoods requestData = SRequest_DeleteGoods.load(data);
		SResponse_DeleteGoods responseData = new SResponse_DeleteGoods();
		DBHelper.delete(T.goods, T.goods.id, requestData.goodsId);
		success(response, responseData.saveToStr());
	}

	public static void GetOrderList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetOrderList requestData = SRequest_GetOrderList.load(data);
		SResponse_GetOrderList responseData = new SResponse_GetOrderList();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s left join %s on %s.%s = %s.%s", T.order, //
				T.goods, //
				T.order, T.order.goodsId, //
				T.goods, T.goods.id));

		if (!TextUtils.isEmpty(requestData.userId))
			sb.append(String.format(" and %s = '%s'", T.order.userId, requestData.userId));

		if (requestData.state != 0)
			sb.append(String.format(" and %s = %s", T.order.state, requestData.state));

		if (requestData.time > 0)
		{
			if (requestData.prev)
				sb.append(String.format(" and %s > %s", T.order.time, requestData.time));
			else
				sb.append(String.format(" and %s < %s", T.order.time, requestData.time));
		}

		if (requestData.prev)
			sb.append(String.format(" order by %s asc", T.order.time));
		else
			sb.append(String.format(" order by %s desc", T.order.time));

		if (requestData.pageSize > 0)
			sb.append(String.format(" limit 0, %s", requestData.pageSize));

		sb.append(String.format(";"));

		responseData.orders = selectOrderList(sb.toString().replaceFirst(" and ", " where "));

		success(response, responseData.saveToStr());
	}

	private static final SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMddHHmmss");

	public static void AddOrder(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_AddOrder requestData = SRequest_AddOrder.load(data);
		SResponse_AddOrder responseData = new SResponse_AddOrder();

		if (TextUtils.isEmpty(requestData.order.userId))
		{
			error(response, SErrorCode.unknown_error, "未指定购买人");
			return;
		}
		if (requestData.order.goods == null || TextUtils.isEmpty(requestData.order.goods.id))
		{
			error(response, SErrorCode.unknown_error, "未指定商品");
			return;
		}
		if (requestData.order.count <= 0)
		{
			error(response, SErrorCode.unknown_error, "数量不正确");
			return;
		}
		if (requestData.order.score < 0)
		{
			error(response, SErrorCode.unknown_error, "积分不正确");
			return;
		}
		if (TextUtils.isEmpty(requestData.order.name))
		{
			error(response, SErrorCode.unknown_error, "未指定收货人");
			return;
		}
		if (TextUtils.isEmpty(requestData.order.phone))
		{
			error(response, SErrorCode.unknown_error, "未指定收货人电话");
			return;
		}
		if (TextUtils.isEmpty(requestData.order.address))
		{
			error(response, SErrorCode.unknown_error, "未指定收货人地址");
			return;
		}

		SUser user = ProcessUser.selectUser(requestData.order.userId);
		if (user == null)
		{
			error(response, SErrorCode.unknown_error, "未找到购买人");
			return;
		}
		SGoods goods = selectGoods(requestData.order.goods.id);
		if (goods == null)
		{
			error(response, SErrorCode.unknown_error, "未找到商品");
			return;
		}

		if (goods.invalid)
		{
			error(response, SErrorCode.unknown_error, "商品已下架");
			return;
		}
		if (goods.yuCount < requestData.order.count)
		{
			error(response, SErrorCode.unknown_error, "商品库存不足");
			return;
		}
		if (goods.score * requestData.order.count != requestData.order.score)
		{
			error(response, SErrorCode.unknown_error, "不支持改价");
			return;
		}
		if (user.score < requestData.order.score)
		{
			error(response, SErrorCode.unknown_error, "您的剩余积分不足");
			return;
		}

		DBHelper.update(T.goods, DBHelper.set(T.goods.buyCount, goods.buyCount + requestData.order.count, T.goods.yuCount, goods.yuCount - requestData.order.count), T.goods.id, requestData.order.goods.id);

		DBHelper.update(T.user, DBHelper.set(T.user.score, user.score - requestData.order.score), T.user.id, requestData.order.userId);

		String orderId = null;
		do
		{
			orderId = formatDate.format(new Date(System.currentTimeMillis()));
			orderId = RandomHelper.RandNumberString(4) + orderId.substring(2);
		} while (DBHelper.hasRecord(T.order, T.order.id, orderId));

		DBHelper.insert(T.order, //
				T.order.id, orderId, //
				T.order.userId, requestData.order.userId, //
				T.order.goodsId, requestData.order.goods.id, //
				T.order.count, requestData.order.count, //
				T.order.score, requestData.order.score, //
				T.order.time, System.currentTimeMillis(), //
				T.order.state, SOrderState.Init, //
				T.order.name, requestData.order.name, //
				T.order.phone, requestData.order.phone, //
				T.order.address, requestData.order.address);

		success(response, responseData.saveToStr());
	}

	public static void ModifyOrderState(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ModifyOrderState requestData = SRequest_ModifyOrderState.load(data);
		SResponse_ModifyOrderState responseData = new SResponse_ModifyOrderState();
		DBHelper.update(T.order, DBHelper.set(T.order.state, requestData.state), T.order.id, requestData.orderId);
		success(response, responseData.saveToStr());
	}

	// ------------------------------------------------------------------------------------

	private static List<STaskGroup> selectTaskGroupList(String sql) throws Exception
	{
		final List<STaskGroup> taskGroupList = new ArrayList<>();
		DBHelper.exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					taskGroupList.add(packTaskGroup(rs));
				}
			}
		});
		return taskGroupList;
	}

	private static STaskGroup selectTaskGroup(String taskGroupId) throws Exception
	{
		final List<STaskGroup> taskGroupList = new ArrayList<>();
		DBHelper.select(T.taskGroup, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				if (rs.next())
				{
					taskGroupList.add(packTaskGroup(rs));
				}
			}
		}, T.taskGroup.id, taskGroupId);
		if (taskGroupList.size() > 0)
			return taskGroupList.get(0);
		else
			return null;
	}

	public static void GetTaskGroupList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetTaskGroupList requestData = SRequest_GetTaskGroupList.load(data);
		SResponse_GetTaskGroupList responseData = new SResponse_GetTaskGroupList();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s", T.taskGroup));

		sb.append(String.format(" order by %s asc", T.taskGroup.sort));

		sb.append(String.format(";"));

		responseData.taskGroupList = selectTaskGroupList(sb.toString());

		success(response, responseData.saveToStr());
	}

	public static void AddModifyTaskGroup(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_AddModifyTaskGroup requestData = SRequest_AddModifyTaskGroup.load(data);
		SResponse_AddModifyTaskGroup responseData = new SResponse_AddModifyTaskGroup();

		if (TextUtils.isEmpty(requestData.taskGroup.id))
		{
			// Add
			DBHelper.insert(T.taskGroup, //
					T.taskGroup.id, CommonUtils.uuid(), //
					T.taskGroup.name, requestData.taskGroup.name, //
					T.taskGroup.important, requestData.taskGroup.important, //
					T.taskGroup.difficulty, requestData.taskGroup.difficulty, //
					T.taskGroup.cover, requestData.taskGroup.cover, //
					T.taskGroup.sort, System.currentTimeMillis());
		}
		else
		{
			// Modify
			DBHelper.update(T.taskGroup, DBHelper.set(T.taskGroup.name, requestData.taskGroup.name, //
					T.taskGroup.important, requestData.taskGroup.important, //
					T.taskGroup.difficulty, requestData.taskGroup.difficulty, //
					T.taskGroup.cover, requestData.taskGroup.cover, //
					T.taskGroup.sort, requestData.taskGroup.sort), T.taskGroup.id, requestData.taskGroup.id);
		}

		success(response, responseData.saveToStr());
	}

	public static void SwapTaskGroup(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_SwapTaskGroup requestData = SRequest_SwapTaskGroup.load(data);
		SResponse_SwapTaskGroup responseData = new SResponse_SwapTaskGroup();
		DBHelper.update(T.taskGroup, DBHelper.set(T.taskGroup.sort, requestData.taskGroup2.sort), T.taskGroup.id, requestData.taskGroup1.id);
		DBHelper.update(T.taskGroup, DBHelper.set(T.taskGroup.sort, requestData.taskGroup1.sort), T.taskGroup.id, requestData.taskGroup2.id);
		success(response, responseData.saveToStr());
	}

	public static void DeleteTaskGroup(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_DeleteTaskGroup requestData = SRequest_DeleteTaskGroup.load(data);
		SResponse_DeleteTaskGroup responseData = new SResponse_DeleteTaskGroup();
		DBHelper.delete(T.taskGroup, T.taskGroup.id, requestData.taskGroupId);
		success(response, responseData.saveToStr());
	}

	// ------------------------------------------------------------------------------------

	private static List<STeach> selectTeachList(String sql) throws Exception
	{
		final List<STeach> teachList = new ArrayList<>();
		DBHelper.exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					teachList.add(packTeach(rs));
				}
			}
		});
		return teachList;
	}

	private static STeach selectTeach(STeach teach) throws Exception
	{
		final List<STeach> teachList = new ArrayList<>();
		DBHelper.select(T.teach, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				if (rs.next())
				{
					teachList.add(packTeach(rs));
				}
			}
		}, T.teach.teacherId, teach.teacherId, //
				T.teach.year, teach.year, //
				T.teach.month, teach.month, //
				T.teach.day, teach.day, //
				T.teach.block, teach.block);
		if (teachList.size() > 0)
			return teachList.get(0);
		else
			return null;
	}

	public static void GetTeachList(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_GetTeachList requestData = SRequest_GetTeachList.load(data);
		SResponse_GetTeachList responseData = new SResponse_GetTeachList();

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("select * from %s", T.teach));

		if (!TextUtils.isEmpty(requestData.teacherId))
		{
			sb.append(String.format(" where %s = '%s'", T.teach.teacherId, requestData.teacherId));
		}
		else
		{
			sb.append(String.format(" where %s = %s", T.teach.year, requestData.year));
			sb.append(String.format(" and %s = %s", T.teach.month, requestData.month));
			sb.append(String.format(" and %s = %s", T.teach.day, requestData.day));
		}

		sb.append(String.format(";"));

		responseData.teachs = selectTeachList(sb.toString());

		success(response, responseData.saveToStr());
	}

	public static void ModifyTeach(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_ModifyTeach requestData = SRequest_ModifyTeach.load(data);
		SResponse_ModifyTeach responseData = new SResponse_ModifyTeach();

		for (STeach teach : requestData.teachs)
		{
			if (teach.flag == STeachFlag.None && TextUtils.isEmpty(teach.studentId))
			{
				DBHelper.delete(T.teach, T.teach.teacherId, teach.teacherId, //
						T.teach.year, teach.year, //
						T.teach.month, teach.month, //
						T.teach.day, teach.day, //
						T.teach.block, teach.block);
			}
			else
			{
				STeach existTeach = selectTeach(teach);
				if (existTeach == null)
				{
					// Add
					DBHelper.insert(T.teach, //
							T.teach.teacherId, teach.teacherId, //
							T.teach.year, teach.year, //
							T.teach.month, teach.month, //
							T.teach.day, teach.day, //
							T.teach.block, teach.block, //
							T.teach.flag, teach.flag, //
							T.teach.studentId, teach.studentId);
				}
				else
				{
					// Modify
					DBHelper.update(T.teach, DBHelper.set(T.teach.flag, teach.flag, //
							T.teach.studentId, teach.studentId), //
							T.teach.teacherId, teach.teacherId, //
							T.teach.year, teach.year, //
							T.teach.month, teach.month, //
							T.teach.day, teach.day, //
							T.teach.block, teach.block);
				}
			}
		}

		success(response, responseData.saveToStr());
	}

}
