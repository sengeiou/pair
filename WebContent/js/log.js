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

function request()
{
	console.log('request');
	var url = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/pair/run";
	$.post(url, JSON.stringify({
		handleId : GetServerLog,
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
					load(JSON.parse(reault.data));
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

function load(data)
{
	for (var t = 0; t < data.logs.length; t++)
	{
		var log = data.logs[t];

		var div = $("<div></div>");
		$("body").append(div);
		div.css({
			"margin-top" : "0px"
		});

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"color" : "black",
			"white-space" : "pre"
		});
		span.text(log);
	}
}
