$(document).ready(function()
{
	console.log('ready');
	request();
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

function request()
{
	console.log('request');
	var url = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/pair/run";
	$.post(url, JSON.stringify({
		handleId : GetServerState,
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

const numWidth = "100px";
const timeWidth = "200px";
const handleWidth = "220px";
const readyWidth = "180px";
const waitWidth = "180px";
const processWidth = "180px";
const useWidth = "180px";

function switchStopState()
{
	console.log('switchStopState');
	var url = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/pair/run";
	$.post(url, JSON.stringify({
		handleId : SetServerState,
		data : {
			stop : !obj.serverState.stop
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
					var ret = JSON.parse(reault.data);
					obj.serverState.stop = ret.stop;
					$("#switchButton").text(obj.serverState.stop ? "服务器已暂停" : "服务器正在运行");
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
			console.log("切换失败！");
		}
	});
}

function load()
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
	span.text("启动时间：" + formatTimeStr(obj.serverState.startTime));

	var span = $("<span></span>");
	div.append(span);
	span.css({
		"margin-left" : "50px",
		"color" : "#BB3D00"
	});
	span.text("启动时长：" + formatTime(currentTimeMillis() - obj.serverState.startTime));

	var span = $("<button id='switchButton' type='button' onclick='switchStopState();'></button>");
	div.append(span);
	span.css({
		"margin-left" : "50px",
		"color" : "black"
	});
	span.text(obj.serverState.stop ? "服务器已暂停" : "服务器正在运行");

	{
		var requestRecord = obj.serverState.requestRecords[t];

		var div = $("<div></div>");
		$("body").append(div);
		div.css({
			"padding-top" : "10px",
			"padding-bottom" : "10px",
			"background" : "#7FFFD4"
		});

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : numWidth,
			"text-align" : "center",
			"color" : "black"
		});
		// span.text(t + 1);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : timeWidth,
			"text-align" : "left",
			"color" : "black"
		});
		// span.text(formatTimeStr(requestRecord.entryTime));

		var handleSelect = $("<select id='handleSelect'></select>");
		div.append(handleSelect);
		handleSelect.css({
			"display" : "inline-block",
			"width" : handleWidth,
			"height" : "36px",
			"text-align" : "left",
			"color" : "black"
		});

		var handleMap = new Map();
		handleMap.set(0, "全部");
		for (var t = 0; t < obj.serverState.requestRecords.length; t++)
		{
			var requestRecord = obj.serverState.requestRecords[t];
			handleMap.set(requestRecord.handleId, requestRecord.handleName);
		}
		handleMap.forEach(function(value, key)
		{
			console.log(key + ":" + value);
			var option = $("<option></option>");
			handleSelect.append(option);
			option.attr("value", key);
			option.text(value);
		});

		handleSelect.change(function(data)
		{
			var value = data.target.options[data.target.options.selectedIndex].value;
			console.log(value);
			loadRecord();
		});

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : readyWidth,
			"text-align" : "left",
			"color" : "black"
		});
		// span.text("准备时长：" + (requestRecord.readyTime - requestRecord.entryTime));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : waitWidth,
			"text-align" : "left",
			"color" : "black"
		});
		// span.text("等待时长：" + (requestRecord.startProcessTime - requestRecord.readyTime));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : processWidth,
			"text-align" : "left",
			"color" : "black"
		});
		// span.text("处理时长：" + (requestRecord.overProcessTime - requestRecord.startProcessTime));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : useWidth,
			"text-align" : "left",
			"color" : "black"
		});
		// span.text("总时长：" + (requestRecord.overProcessTime - requestRecord.entryTime));
	}

	var recordCon = $("<div id='recordCon'></div>");
	$("body").append(recordCon);

	loadRecord();
}

function loadRecord()
{
	$("#recordCon").empty();

	var handleFilter = document.getElementById('handleSelect').value;

	var count = 0;

	var allReadyTime = 0;
	var allWaitTime = 0;
	var allProcessTime = 0;
	var allUseTime = 0;

	for (var t = Math.max(0, obj.serverState.requestRecords.length - 8); t < obj.serverState.requestRecords.length; t++)
	{
		var requestRecord = obj.serverState.requestRecords[t];

		if (handleFilter != 0 && handleFilter != requestRecord.handleId)
			continue;

		// if (requestRecord.overProcessTime - requestRecord.startProcessTime <= 10 * 1000)
		// continue;

		count++;

		var div = $("<div></div>");
		$("#recordCon").append(div);
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
		span.text(t);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : timeWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text(formatTimeStr(requestRecord.entryTime));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : handleWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text(requestRecord.handleName);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : readyWidth,
			"text-align" : "left",
			"color" : "black"
		});
		var readyTime = requestRecord.readyTime - requestRecord.entryTime;
		span.text("准备时长：" + readyTime);
		allReadyTime += readyTime;

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : waitWidth,
			"text-align" : "left",
			"color" : "black"
		});
		var waitTime = requestRecord.startProcessTime - requestRecord.readyTime;
		span.text("等待时长：" + waitTime);
		allWaitTime += waitTime;

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : processWidth,
			"text-align" : "left",
			"color" : "black"
		});
		var processTime = requestRecord.overProcessTime - requestRecord.startProcessTime;
		span.text("处理时长：" + processTime);
		allProcessTime += processTime;

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : useWidth,
			"text-align" : "left",
			"color" : "black"
		});
		var useTime = requestRecord.overProcessTime - requestRecord.entryTime;
		span.text("使用时长：" + useTime);
		allUseTime += useTime;
	}

	if (count > 0)
	{
		var div = $("<div></div>");
		$("#recordCon").prepend(div);
		div.css({
			"padding-top" : "10px",
			"padding-bottom" : "10px",
			"background" : "#7FFFD4"
		});

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : numWidth,
			"text-align" : "center",
			"color" : "black"
		});
		// span.text(count);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : timeWidth,
			"text-align" : "left",
			"color" : "black"
		});
		// span.text(formatTimeStr(requestRecord.entryTime));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : handleWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("总：");

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : readyWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("准备时长：" + allReadyTime);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : waitWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("等待时长：" + allWaitTime);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : processWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("处理时长：" + allProcessTime);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : useWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("使用时长：" + allUseTime);
	}

	if (count > 0)
	{
		var div = $("<div></div>");
		$("#recordCon").prepend(div);
		div.css({
			"padding-top" : "10px",
			"padding-bottom" : "10px",
			"background" : "#7FFFD4"
		});

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : numWidth,
			"text-align" : "center",
			"color" : "black"
		});
		// span.text(count);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : timeWidth,
			"text-align" : "left",
			"color" : "black"
		});
		// span.text(formatTimeStr(requestRecord.entryTime));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : handleWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("平均：");

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : readyWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("准备时长：" + allReadyTime / count);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : waitWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("等待时长：" + allWaitTime / count);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : processWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("处理时长：" + allProcessTime / count);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : useWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("使用时长：" + allUseTime / count);
	}
}
