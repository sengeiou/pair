$(document).ready(function()
{
	console.log('ready');
	request(-1);
});

var config = null;

function requestConfig()
{
	console.log('requestConfig');
	var url = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/pair/api";
	$.post(url, JSON.stringify({
		handleId : GetConfig,
		data : {}
	}), function(data, status)
	{
		if (status == "success")
		{
			var reault = JSON.parse(data);
			if (reault.code == 200)
			{
				try
				{
					config = JSON.parse(reault.data);
					requestLog();
				}
				catch (e)
				{
					alert(e);
				}
			}
			else
			{
				console.log(reault.msg);
			}
		}
		else
		{
			console.log("加载失败！");
		}
	});
}

var obj = null;

function request(keepScreen)
{
	console.log('request');
	var url = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/pair/api";
	$.post(url, JSON.stringify({
		handleId : ZXDeviceList,
		data : {
			keepScreen : keepScreen
		}
	}), function(data, status)
	{
		if (status == "success")
		{
			var reault = JSON.parse(data);
			if (reault.code == 200)
			{
				try
				{
					obj = JSON.parse(reault.data);
					load();
				}
				catch (e)
				{
					alert(e);
				}
			}
			else
			{
				console.log(reault.msg);
			}
		}
		else
		{
			console.log("加载失败！");
		}
	});
}

const numWidth = "60px";
const startTimeWidth = "200px";
const useTimeWidth = "150px";
const deviceWidth = "180px";
const hostWidth = "230px";

function flush()
{
	request(obj.keepScreen ? 0 : 1);
}

function load()
{
	$("body").empty();

	{
		var div = $("<div></div>");
		$("body").append(div);
		div.css({
			"margin-top" : "10px"
		});

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"color" : "black"
		});
		span.text("seriveIp：" + obj.seriveIp);
	}

	{
		var div = $("<div></div>");
		$("body").append(div);
		div.css({
			"margin-top" : "10px"
		});

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"color" : "black"
		});
		span.text("设备总数：" + obj.devices.length);

		var span = $("<button type='button' onclick='flush();'></button>");
		div.append(span);
		span.css({
			"margin-left" : "50px",
			"color" : "black"
		});
		span.text("keepScreen：" + obj.keepScreen);

		var span = $("<span id='uploadFailCount'></span>");
		div.append(span);
		span.css({
			"margin-left" : "50px",
			"color" : "#BB3D00"
		});

		var uploadCon = $("<div id='uploadCon'></div>");
		$("body").append(uploadCon);

		loadUpload();
	}
}

function loadUpload()
{
	$("#uploadCon").empty();

	var count = 0;
	var failCount = 0;

	var map = new HashMap();
	for (var t = 0; t < obj.devices.length; t++)
	{
		var device = obj.devices[t];
		var key = device.netIp.cip;
		if (map.containsKey(key))
		{
			map.put(key, map.get(key) + 1);
		}
		else
		{
			map.put(key, 1);
		}
	}

	for (var t = 0; t < obj.devices.length; t++)
	{
		var device = obj.devices[t];

		count++;

		var redCount = 0;

		var div = $("<div></div>");
		$("#uploadCon").append(div);
		div.css({
			"padding-top" : "10px",
			"padding-bottom" : "10px",
			"background" : (t % 2 == 0 ? "#F0FFFF" : "#F5F5DC")
		});

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : numWidth,
			"text-align" : "center",
			"color" : "black"
		});
		span.text(count);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : deviceWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("" + device.deviceId);

//		var span = $("<span></span>");
//		div.append(span);
//		span.css({
//			"display" : "inline-block",
//			"width" : hostWidth,
//			"text-align" : "left",
//			"color" : "black"
//		});
//		span.text("host：" + device.hostIp);

		var color = device.netIp.cip != obj.seriveIp ? "black" : "red";
		if (color == "red")
			redCount++;
		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : 180,
			"text-align" : "left",
			"color" : color
		});
		span.text(map.get(device.netIp.cip) + "：" + device.netIp.cip);

		var color = device.netIp.cip != obj.seriveIp ? "black" : "red";
		if (color == "red")
			redCount++;
		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : 200,
			"text-align" : "left",
			"color" : color
		});
		span.text(device.netIp.cname);

		var color = device.versionCode == 19120401 ? "black" : "red";
		if (color == "red")
			redCount++;
		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : 150,
			"text-align" : "left",
			"color" : color
		});
		span.text("v：" + device.versionCode);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : startTimeWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("" + formatTimeStr(device.lastTickTime));

		var color = currentTimeMillis() - device.lastTickTime < 2 * 60 * 1000 ? "black" : "red";
		if (color == "red")
			redCount++;
		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : 120,
			"text-align" : "left",
			"color" : color
		});
		span.text("tick：" + formatTime(currentTimeMillis() - device.lastTickTime));

		var color = device.dtTime < 7 * 60 * 1000 ? "black" : "red";
		if (color == "red")
			redCount++;
		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : 150,
			"text-align" : "left",
			"color" : color
		});
		span.text("dtTime：" + formatTime(device.dtTime));

		var color = device.capacity > 50 ? "black" : "red";
		if (color == "red")
			redCount++;
		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : 100,
			"text-align" : "left",
			"color" : color
		});
		span.text("电量：" + device.capacity);

		if (redCount > 0)
			failCount++;
	}

	$("#uploadFailCount").text("错误：" + failCount + " 行");
}
