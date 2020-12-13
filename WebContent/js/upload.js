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
		handleId : GetServerUploadingList,
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

const numWidth = "120px";
const startTimeWidth = "300px";
const useTimeWidth = "180px";
const fileSizeWidth = "200px";
const resultWidth = "150px";

function load()
{
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
		span.text("正在上传 " + obj.uploadingList.length + " 个文件");

		var uploadingCon = $("<div id='uploadingCon'></div>");
		$("body").append(uploadingCon);

		loadUploading();
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
		span.text("上传记录：" + obj.uploadList.length + " 个");

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

function loadUploading()
{
	$("#uploadingCon").empty();

	var count = 0;

	for (var t = 0; t < obj.uploadingList.length; t++)
	{
		var uploading = obj.uploadingList[t];

		count++;

		var div = $("<div></div>");
		$("#uploadingCon").append(div);
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
			"width" : startTimeWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("开始时间：" + formatTimeStr(uploading.startTime));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : useTimeWidth,
			"text-align" : "left",
			"color" : "black"
		});
		span.text("已经用时：" + formatTime(currentTimeMillis() - uploading.startTime));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"text-align" : "left",
			"color" : "black"
		});
		span.text(uploading.path);
	}
}

function loadUpload()
{
	$("#uploadCon").empty();

	var count = 0;
	var failCount = 0;

	for (var t = 0; t < obj.uploadList.length; t++)
	{
		var upload = obj.uploadList[t];

		count++;
		if (!upload.result)
			failCount++;

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
			"color" : (upload.result ? "black" : "red")
		});
		span.text(count);

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : startTimeWidth,
			"text-align" : "left",
			"color" : (upload.result ? "black" : "red")
		});
		span.text("开始时间：" + formatTimeStr(upload.startTime));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : useTimeWidth,
			"text-align" : "left",
			"color" : (upload.result ? "black" : "red")
		});
		span.text("用时：" + formatTime(upload.endTime - upload.startTime));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : fileSizeWidth,
			"text-align" : "left",
			"color" : (upload.result ? "black" : "red")
		});
		span.text("文件大小：" + formatSize(upload.fileSize));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"width" : resultWidth,
			"text-align" : "left",
			"color" : (upload.result ? "black" : "red")
		});
		span.text("结果：" + (upload.result ? "success" : "fail"));

		var span = $("<span></span>");
		div.append(span);
		span.css({
			"display" : "inline-block",
			"text-align" : "left",
			"color" : (upload.result ? "black" : "red")
		});
		span.text(upload.path);
	}

	$("#uploadFailCount").text("上传失败：" + failCount + " 个");
}
