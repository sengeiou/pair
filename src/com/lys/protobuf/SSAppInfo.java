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
import com.lys.protobuf.ProtocolApp.SAppInfo;

public class SSAppInfo extends SPTData<SAppInfo>
{
	private static final SSAppInfo DefaultInstance = new SSAppInfo();

	public String pkgName = null;
	public String channel = null;
	public Integer versionCode = 0;
	public String versionName = null;
	public Float probability = 0F;
	public String name = null;
	public Long size = 0L;
	public String apkUrl = null;
	public String icoUrl = null;
	public String des = null;
	public Boolean isSystem = false;

	public static SSAppInfo create(String pkgName, String channel, Integer versionCode, String versionName, Float probability, String name, Long size, String apkUrl, String icoUrl, String des, Boolean isSystem)
	{
		SSAppInfo obj = new SSAppInfo();
		obj.pkgName = pkgName;
		obj.channel = channel;
		obj.versionCode = versionCode;
		obj.versionName = versionName;
		obj.probability = probability;
		obj.name = name;
		obj.size = size;
		obj.apkUrl = apkUrl;
		obj.icoUrl = icoUrl;
		obj.des = des;
		obj.isSystem = isSystem;
		return obj;
	}

	public SSAppInfo clone()
	{
		return load(saveToBytes());
	}

	public void copyFrom(SSAppInfo _other_)
	{
		this.pkgName = _other_.pkgName;
		this.channel = _other_.channel;
		this.versionCode = _other_.versionCode;
		this.versionName = _other_.versionName;
		this.probability = _other_.probability;
		this.name = _other_.name;
		this.size = _other_.size;
		this.apkUrl = _other_.apkUrl;
		this.icoUrl = _other_.icoUrl;
		this.des = _other_.des;
		this.isSystem = _other_.isSystem;
	}

	@Override
	public void parse(JSONObject _json_)
	{
		if (_json_.containsKey("pkgName"))
			pkgName = _json_.getString("pkgName");
		if (_json_.containsKey("channel"))
			channel = _json_.getString("channel");
		if (_json_.containsKey("versionCode"))
			versionCode = _json_.getInteger("versionCode");
		if (_json_.containsKey("versionName"))
			versionName = _json_.getString("versionName");
		if (_json_.containsKey("probability"))
			probability = _json_.getFloat("probability");
		if (_json_.containsKey("name"))
			name = _json_.getString("name");
		if (_json_.containsKey("size"))
			size = _json_.getLong("size");
		if (_json_.containsKey("apkUrl"))
			apkUrl = _json_.getString("apkUrl");
		if (_json_.containsKey("icoUrl"))
			icoUrl = _json_.getString("icoUrl");
		if (_json_.containsKey("des"))
			des = _json_.getString("des");
		if (_json_.containsKey("isSystem"))
			isSystem = _json_.getBoolean("isSystem");
	}

	public static SSAppInfo load(String str)
	{
		try
		{
			SSAppInfo obj = new SSAppInfo();
			obj.parse(JsonHelper.getJSONObject(str));
			return obj;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static SSAppInfo load(JSONObject json)
	{
		try
		{
			SSAppInfo obj = new SSAppInfo();
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
			if (pkgName != null)
				_json_.put("pkgName", pkgName);
			if (channel != null)
				_json_.put("channel", channel);
			if (versionCode != null)
				_json_.put("versionCode", versionCode);
			if (versionName != null)
				_json_.put("versionName", versionName);
			if (probability != null)
				_json_.put("probability", probability);
			if (name != null)
				_json_.put("name", name);
			if (size != null)
				_json_.put("size", String.valueOf(size));
			if (apkUrl != null)
				_json_.put("apkUrl", apkUrl);
			if (icoUrl != null)
				_json_.put("icoUrl", icoUrl);
			if (des != null)
				_json_.put("des", des);
			if (isSystem != null)
				_json_.put("isSystem", isSystem);
			return _json_;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static List<SSAppInfo> loadList(JSONArray ja)
	{
		try
		{
			List<SSAppInfo> list = new ArrayList<SSAppInfo>();
			for (int i = 0; i < ja.size(); i++)
			{
				JSONObject jo = ja.getJSONObject(i);
				SSAppInfo item = SSAppInfo.load(jo);
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

	public static JSONArray saveList(List<SSAppInfo> list)
	{
		JSONArray ja = new JSONArray();
		for (int i = 0; i < list.size(); i++)
		{
			SSAppInfo item = list.get(i);
			JSONObject jo = item.saveToJson();
			ja.add(jo);
		}
		return ja;
	}

	@Override
	public void parse(SAppInfo _proto_)
	{
		if (_proto_.hasPkgName())
			pkgName = _proto_.getPkgName();
		if (_proto_.hasChannel())
			channel = _proto_.getChannel();
		if (_proto_.hasVersionCode())
			versionCode = _proto_.getVersionCode();
		if (_proto_.hasVersionName())
			versionName = _proto_.getVersionName();
		if (_proto_.hasProbability())
			probability = _proto_.getProbability();
		if (_proto_.hasName())
			name = _proto_.getName();
		if (_proto_.hasSize())
			size = _proto_.getSize();
		if (_proto_.hasApkUrl())
			apkUrl = _proto_.getApkUrl();
		if (_proto_.hasIcoUrl())
			icoUrl = _proto_.getIcoUrl();
		if (_proto_.hasDes())
			des = _proto_.getDes();
		if (_proto_.hasIsSystem())
			isSystem = _proto_.getIsSystem();
	}

	public static SSAppInfo load(byte[] bytes)
	{
		try
		{
			SSAppInfo obj = new SSAppInfo();
			obj.parse(SAppInfo.parseFrom(bytes));
			return obj;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static SSAppInfo load(SAppInfo proto)
	{
		try
		{
			SSAppInfo obj = new SSAppInfo();
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
	public SAppInfo saveToProto()
	{
		SAppInfo.Builder _builder_ = SAppInfo.newBuilder();
		if (pkgName != null && !pkgName.equals(SAppInfo.getDefaultInstance().getPkgName()))
			_builder_.setPkgName(pkgName);
		if (channel != null && !channel.equals(SAppInfo.getDefaultInstance().getChannel()))
			_builder_.setChannel(channel);
		if (versionCode != null && !versionCode.equals(SAppInfo.getDefaultInstance().getVersionCode()))
			_builder_.setVersionCode(versionCode);
		if (versionName != null && !versionName.equals(SAppInfo.getDefaultInstance().getVersionName()))
			_builder_.setVersionName(versionName);
		if (probability != null && !probability.equals(SAppInfo.getDefaultInstance().getProbability()))
			_builder_.setProbability(probability);
		if (name != null && !name.equals(SAppInfo.getDefaultInstance().getName()))
			_builder_.setName(name);
		if (size != null && !size.equals(SAppInfo.getDefaultInstance().getSize()))
			_builder_.setSize(size);
		if (apkUrl != null && !apkUrl.equals(SAppInfo.getDefaultInstance().getApkUrl()))
			_builder_.setApkUrl(apkUrl);
		if (icoUrl != null && !icoUrl.equals(SAppInfo.getDefaultInstance().getIcoUrl()))
			_builder_.setIcoUrl(icoUrl);
		if (des != null && !des.equals(SAppInfo.getDefaultInstance().getDes()))
			_builder_.setDes(des);
		if (isSystem != null && !isSystem.equals(SAppInfo.getDefaultInstance().getIsSystem()))
			_builder_.setIsSystem(isSystem);
		return _builder_.build();
	}
}
