$(window).resize(function()
{
	console.log('resize');
	load();
});

$(document).ready(function()
{
	console.log('ready');
	load();
});

function pxValue(str)
{
	return str.substring(0, str.length - 2);
}

function isWeiXin()
{
	var ua = window.navigator.userAgent.toLowerCase();
	console.log('ua：' + ua);
	if (ua.match(/MicroMessenger/i) == 'micromessenger')
	{
		return true;
	}
	else
	{
		return false;
	}
}

function log(str)
{
	console.log("log3 - " + str);
	var item = $("<div></div>");
	item.text(str);
	$("#log").append(item);
}

function loadImg(img, callback)
{
	var w = img.width();
	var h = img.height();
	if (w == 0 || h == 0)
	{
		log("retry...");
		setTimeout(function()
		{
			loadImg(img, callback);
		}, 10);
	}
	else
	{
		if (callback != null)
			callback(w, h);
	}
}

function load()
{
	var w = $(window).width();
	var h = $(window).height();
	log("window : " + w + ", " + h);

	var w = $("#page").width();
	var h = $("#page").height();
	log("page : " + w + ", " + h);

	var windowW = $("#page").width();
	var windowH = $("#page").height();

	log("use : " + windowW + ", " + windowH);

	if (isWeiXin())
	{
		// loadImg($("#bg"), function(bgW, bgH)
		// {
		// log("bg : " + bgW + ", " + bgH);
		//
		$("#bg").css("left", (windowW - 834) / 2 + "px");
		$("#bg").css("top", (windowH - 792) / 2 + "px");

		$("#bg").css("display", "inline");
		// });

		loadImg($("#tip"), function(tipW, tipH)
		{
			log("tip : " + tipW + ", " + tipH);
			$("#tip").css("left", (windowW - tipW - 50) + "px");
			$("#tip").css("display", "inline");
		});
	}
	else
	{
		// loadImg($("#bg"), function(bgW, bgH)
		// {
		// log("bg : " + bgW + ", " + bgH);
		//
		$("#bg").css("left", (windowW - 834) / 2 + "px");
		$("#bg").css("top", (windowH - 792) / 4 + "px");

		$("#bg").css("display", "inline");
		// });

		// loadImg($("#btn"), function(btnW, btnH)
		// {
		// log("btn : " + btnW + ", " + btnH);
		//
		$("#btn").css("left", (windowW - 655) / 2 + "px");
		$("#btn").css("top", (windowH - 113) - (windowH / 5) + "px");

		$("#btn").css("display", "inline");
		// });
	}
}
