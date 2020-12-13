package com.lys.protobuf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lys.base.utils.AppDataTool;
import com.lys.base.utils.JsonHelper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.google.protobuf.ByteString;

import com.lys.base.utils.SPTData;
import com.lys.protobuf.ProtocolTeach.Response_TeachStart;

public class SResponse_TeachStart extends SPTData<Response_TeachStart>
{
	private static final SResponse_TeachStart DefaultInstance = new SResponse_TeachStart();


	public static SResponse_TeachStart create()
	{
		SResponse_TeachStart obj = new SResponse_TeachStart();
		return obj;
	}

	public SResponse_TeachStart clone()
	{
		return load(saveToBytes());
	}

	public void copyFrom(SResponse_TeachStart _other_)
	{
	}

	@Override
	public void parse(JSONObject _json_)
	{
	}

	public static SResponse_TeachStart load(String str)
	{
		try
		{
			SResponse_TeachStart obj = new SResponse_TeachStart();
			obj.parse(JsonHelper.getJSONObject(str));
			return obj;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static SResponse_TeachStart load(JSONObject json)
	{
		try
		{
			SResponse_TeachStart obj = new SResponse_TeachStart();
			obj.parse(json);
			return obj;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONObject saveToJson()
	{
		try
		{
			JSONObject _json_ = new JSONObject(true);
			return _json_;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static List<SResponse_TeachStart> loadList(JSONArray ja)
	{
		try
		{
			List<SResponse_TeachStart> list = new ArrayList<SResponse_TeachStart>();
			for (int i = 0; i < ja.size(); i++)
			{
				JSONObject jo = ja.getJSONObject(i);
				SResponse_TeachStart item = SResponse_TeachStart.load(jo);
				if (item == null)
					return null;
				list.add(item);
			}
			return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray saveList(List<SResponse_TeachStart> list)
	{
		JSONArray ja = new JSONArray();
		for (int i = 0; i < list.size(); i++)
		{
			SResponse_TeachStart item = list.get(i);
			JSONObject jo = item.saveToJson();
			ja.add(jo);
		}
		return ja;
	}

	@Override
	public void parse(Response_TeachStart _proto_)
	{
	}

	public static SResponse_TeachStart load(byte[] bytes)
	{
		try
		{
			SResponse_TeachStart obj = new SResponse_TeachStart();
			obj.parse(Response_TeachStart.parseFrom(bytes));
			return obj;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static SResponse_TeachStart load(Response_TeachStart proto)
	{
		try
		{
			SResponse_TeachStart obj = new SResponse_TeachStart();
			obj.parse(proto);
			return obj;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Response_TeachStart saveToProto()
	{
		Response_TeachStart.Builder _builder_ = Response_TeachStart.newBuilder();
		return _builder_.build();
	}
}
