$(window).resize(function()
{
	console.log('resize');
	load();
});

$(document).ready(function()
{
	console.log('ready');
	try
	{
		load();
	}
	catch (e)
	{
		alert(e);
	}
});

function log(str)
{
	console.log("log3 - " + str);
	var item = $("<div></div>");
	item.text(str);
	$("#log").append(item);
}

var pageW;
var pageH;

var nameSpace = 50;

var fromHour = 6 * 2;
var toHour = 22 * 2;
var hourCount = toHour - fromHour;

var scrollBarWidth = 30;

function load()
{
	$("#page").empty();

	pageW = $("#page").width();
	pageH = $("#page").height();

	log("use : " + pageW + ", " + pageH);

	if (true)
	{
		var dayBlock = $("<div></div>");
		loadTimeHead(dayBlock);
		$("#fixed").append(dayBlock);
	}

	for (var i = 0; i < 50; i++)
	{
		var dayBlock = $("<div></div>");
		loadDayBlock(dayBlock, i);
		dayBlock.css({
			"margin-top" : "1px"
		});
		$("#page").append(dayBlock);
	}
}

function loadTimeHead(dayBlock)
{
	var hourWidth = pageW - nameSpace - scrollBarWidth;
	var blockWidth = hourWidth / hourCount;

	for (var i = fromHour; i <= toHour; i++)
	{
		var timeBlock = $("<span></span>");
		timeBlock.text(i / 2);
		timeBlock.css({
			"position" : "absolute",
			"left" : (nameSpace + (i - fromHour) * blockWidth - blockWidth / 2) + "px",
			"width" : blockWidth + "px",
			"text-align" : "center",
		});
		dayBlock.append(timeBlock);
	}
}

function loadDayBlock(dayBlock, day)
{
	var dayText = $("<span></span>");
	dayText.text(day + 1);
	dayText.css({
		"display" : "inline-block",
		"width" : nameSpace + "px",
		"text-align" : "center",
	});
	dayBlock.append(dayText);

	var hourWidth = pageW - nameSpace - scrollBarWidth;
	var blockWidth = hourWidth / hourCount;

	for (var i = fromHour; i < toHour; i++)
	{
		var timeBlock = $("<span></span>");
		timeBlock.text('ㅤ');
		timeBlock.css({
			"display" : "inline-block",
			"width" : blockWidth + "px",
			"text-align" : "center",
			"background-color" : i % 2 == 0 ? "#f0ffff" : "#f5f5dc"
		});
		dayBlock.append(timeBlock);
		!function(timeBlock, day, i)
		{
			timeBlock.click(function()
			{
				// alert((day + 1) + "-" + i);
			});
		}(timeBlock, day, i);
	}
}
