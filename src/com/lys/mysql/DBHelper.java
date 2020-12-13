package com.lys.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.lys.config.Config;
import com.lys.protobuf.STimeRecord;
import com.lys.servlet.process.ProcessRun;
import com.lys.utils.LOG;
import com.mysql.jdbc.Driver;

public class DBHelper
{
	public static boolean fromClient = false;

	private static final Object lock = new Object();
	private static final String db = Config.projectName;
	private static Connection connection = null;
	private static long lastUseTime = System.currentTimeMillis();

	static
	{
		LOG.v("DBHelper static init");
		try
		{
			new Driver();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static Connection getConn()
	{
		if (connection != null && System.currentTimeMillis() - lastUseTime > 3600 * 1000)
		{
			LOG.v("connection timeout close");
			try
			{
				connection.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			connection = null;
		}
		if (connection == null)
		{
			try
			{
				if (fromClient)
				{
					connection = DriverManager.getConnection("jdbc:mysql://39.104.58.109:3306/?useAffectedRows=true&useUnicode=true&characterEncoding=UTF-8", "root", "Dispress_8");
				}
				else
				{
					if (Config.isLinux)
					{
						LOG.v("connect at linux");
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/?useAffectedRows=true&useUnicode=true&characterEncoding=UTF-8", "root", "Dispress_8");
//						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/?useAffectedRows=true&useUnicode=true&characterEncoding=UTF-8", "root", "lysDiskEasy");
					}
					else
					{
						LOG.v("connect at windows");
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/?useAffectedRows=true&useUnicode=true&characterEncoding=UTF-8", "root", "flyint8");
					}
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			exeSqlImpl(connection, String.format("create database if not exists %s character set utf8;", db), null);
			exeSqlImpl(connection, String.format("use %s;", db), null);
		}
		lastUseTime = System.currentTimeMillis();
		return connection;
	}

	private static boolean exeSqlImpl(Connection conn, String sql, OnCallback callback, Object... params)
	{
//		LOG.v("sql : " + sql);
		boolean result = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = conn.prepareStatement(sql);
			if (params != null && params.length > 0)
			{
				for (int i = 0; i < params.length; i++)
				{
					Object param = params[i];
					stmt.setString(i + 1, param != null ? param.toString() : "");
				}
			}
			boolean hasResultSet = stmt.execute();
			if (hasResultSet)
			{
				rs = stmt.getResultSet();
				if (callback != null)
					callback.onResult(rs);
			}
			result = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	public interface OnCallback
	{
		void onResult(ResultSet rs) throws SQLException;
	}

	public static boolean exeSql(String sql, OnCallback callback, Object... params)
	{
		STimeRecord timeRecord = new STimeRecord();
		timeRecord.startTime = System.currentTimeMillis();
		timeRecord.des = sql;
		synchronized (lock)
		{
			boolean ret;
			timeRecord.startProcessTime = System.currentTimeMillis();
			ret = exeSqlImpl(getConn(), sql, callback, params);
			timeRecord.overProcessTime = System.currentTimeMillis();
			ProcessRun.timeRecords.add(timeRecord);
			return ret;
		}
	}

	public static int getRecordCount(String sql)
	{
		int[] count = { 0 };
		exeSql(sql, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					count[0]++;
				}
			}
		});
		return count[0];
	}

	public static boolean hasRecord(String sql)
	{
		return getRecordCount(sql) > 0;
	}

	public static void showResultSet(ResultSet rs) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i <= columnCount; i++)
			{
				sb.append(String.format("%20s\t", rsmd.getColumnName(i)));
			}
			LOG.v(sb.toString());
		}
		while (rs.next())
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i <= columnCount; i++)
			{
				sb.append(String.format("%20s\t", rs.getString(i)));
			}
			LOG.v(sb.toString());
		}
	}

	public static void showTables()
	{
		exeSql("show tables;", new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				showResultSet(rs);
			}
		});
	}

	public static boolean hasTable(String table)
	{
		boolean[] result = { false };
		exeSql("show tables;", new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					if (rs.getString(1).toLowerCase().equals(table.toLowerCase()))
					{
						result[0] = true;
						break;
					}
				}
			}
		});
		return result[0];
	}

	public static void showTableDesc(String table)
	{
		exeSql(String.format("desc %s;", table), new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				showResultSet(rs);
			}
		});
	}

	public static boolean hasColumn(String table, String field)
	{
		boolean[] result = { false };
		exeSql(String.format("desc %s;", table), new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					if (rs.getString("field").toLowerCase().equals(field.toLowerCase()))
					{
						result[0] = true;
						break;
					}
				}
			}
		});
		return result[0];
	}

	public static void addColumnFirst(String table, String field, String type)
	{
		if (!hasColumn(table, field))
		{
			exeSql(String.format("alter table %s add %s %s first;", table, field, type), null);
		}
	}

	public static void addColumn(String table, String field, String type)
	{
		if (!hasColumn(table, field))
		{
			exeSql(String.format("alter table %s add %s %s;", table, field, type), null);
		}
	}

	public static void addColumn(String table, String field, String type, String posField)
	{
		if (!hasColumn(table, field))
		{
			exeSql(String.format("alter table %s add %s %s after %s;", table, field, type, posField), null);
		}
	}

	public static void dropColumn(String table, String field)
	{
		if (hasColumn(table, field))
		{
			exeSql(String.format("alter table %s drop %s;", table, field), null);
		}
	}

	public static void modifyColumn(String table, String field, String newType)
	{
		if (hasColumn(table, field))
		{
			exeSql(String.format("alter table %s modify %s %s;", table, field, newType), null);
		}
	}

	public static void changeColumn(String table, String oldField, String newField, String newType)
	{
		if (hasColumn(table, oldField))
		{
			exeSql(String.format("alter table %s change %s %s %s;", table, oldField, newField, newType), null);
		}
	}

	public static void renameTable(String oldTable, String newTable)
	{
		if (hasTable(oldTable) && !hasTable(newTable))
		{
			exeSql(String.format("alter table %s rename to %s;", oldTable, newTable), null);
		}
	}

	// ---------------------------------------------------

	public static boolean insert(Object table, Object... params)
	{
		StringBuilder keys = new StringBuilder();
		StringBuilder values = new StringBuilder();
		Object[] objs = new Object[params.length / 2];
		for (int i = 0; i < params.length; i += 2)
		{
			Object key = params[i];
			Object value = params[i + 1];
			if (i == 0)
			{
				keys.append(key);
				values.append("?");
			}
			else
			{
				keys.append(", " + key);
				values.append(", " + "?");
			}
			objs[i / 2] = value;
		}
		return exeSql(String.format("insert into %s(%s) values(%s);", table, keys, values), null, objs);
	}

	public static boolean select(Object table, OnCallback callback, Object... params)
	{
		return exeSql(String.format("select * from %s%s;", table, where(params)), callback);
	}

	public static int getRecordCount(Object table, Object... params)
	{
		int[] count = { 0 };
		select(table, new OnCallback()
		{
			@Override
			public void onResult(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					count[0]++;
				}
			}
		}, params);
		return count[0];
	}

	public static boolean hasRecord(Object table, Object... params)
	{
		return getRecordCount(table, params) > 0;
	}

	public static StringBuilder set(Object... params)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.length; i += 2)
		{
			Object key = params[i];
			Object value = params[i + 1];
			if (i == 0)
				sb.append(String.format("%s = '%s'", key, value));
			else
				sb.append(String.format(", %s = '%s'", key, value));
		}
		return sb;
	}

	public static StringBuilder where(Object... params)
	{
		StringBuilder sb = new StringBuilder();
		if (params != null && params.length > 0)
		{
			for (int i = 0; i < params.length; i += 2)
			{
				Object key = params[i];
				Object value = params[i + 1];
				if (i == 0)
					sb.append(String.format(" where %s = '%s'", key, value));
				else
					sb.append(String.format(" and %s = '%s'", key, value));
			}
		}
		return sb;
	}

	public static boolean update(Object table, StringBuilder setBuilder, Object... params)
	{
		return exeSql(String.format("update %s set %s%s;", table, setBuilder, where(params)), null);
	}

	public static boolean delete(Object table, Object... params)
	{
		return exeSql(String.format("delete from %s%s;", table, where(params)), null);
	}
}
