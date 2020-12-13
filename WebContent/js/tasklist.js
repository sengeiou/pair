$(document).ready(function()
{
	console.log('ready');
	requestConfig();
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
					requestUser();
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

function requestUser()
{
	console.log('requestUser');
	$.post(config.api, JSON.stringify({
		handleId : GetUserList,
		data : {
			userType : 0
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
					loadUser(JSON.parse(reault.data));
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

function loadUser(data)
{
	for (var t = 0; t < data.users.length; t++)
	{
		var user = data.users[t];

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
		span.text("[" + user.userType + "]");

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"color" : "#BB3D00"
		});
		span.text(user.id);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"color" : "black"
		});
		span.text("@");

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"color" : "#009100"
		});
		span.text(user.name);

		var taskCon = $("<div></div>");
		$("body").append(taskCon);

		requestTask(taskCon, user);
	}
}

function requestTask(taskCon, user)
{
	console.log('requestTask : ' + user.id);
	$.post(config.api, JSON.stringify({
		handleId : GetTaskList,
		data : {
			userId : user.id
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
					loadTask(taskCon, JSON.parse(reault.data));
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

function loadTask(taskCon, data)
{
	for (var t = 0; t < data.tasks.length; t++)
	{
		var task = data.tasks[t];

		var div = $("<div></div>");
		taskCon.append(div);
		div.css({
			"margin-top" : "10px"
		});

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"margin-left" : "50px",
			"display" : "inline-block",
			"width" : "100px",
			"text-align" : "left",
			"color" : "black"
		});
		span.text("[" + task.type + "]");

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : "200px",
			"text-align" : "left",
			"color" : "black"
		});
		span.text(task.group);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : "400px",
			"text-align" : "left",
			"color" : "black"
		});
		span.text(task.name);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : "250px",
			"text-align" : "left",
			"color" : "black"
		});
		span.text(formatTimeStr(task.createTime));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : "200px",
			"text-align" : "left",
			"color" : "black"
		});
		span.text("访问：" + task.timesForWeb + " 次");

		var a = $("<a></a>");
		taskCon.append(a);
		a.css({
			"margin-left" : "50px"
		});
		var url = config.root + "/task.html?id=" + task.id;
		a.text(url);
		a.attr("href", url);
	}
}
