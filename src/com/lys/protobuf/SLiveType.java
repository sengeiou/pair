package com.lys.protobuf;

public class SLiveType
{
	public static final int Private = 0;
	public static final int Public = 1;

	public static String name(int value)
	{
		return ProtocolLive.LiveType.valueOf(value).name().substring("LiveType_".length());
	}
}
