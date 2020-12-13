function GetQueryString(name)
{
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}

function isPC()
{
	var userAgentInfo = navigator.userAgent;
	var Agents = [ "Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod" ];
	var flag = true;
	for (var v = 0; v < Agents.length; v++)
	{
		if (userAgentInfo.indexOf(Agents[v]) > 0)
		{
			flag = false;
			break;
		}
	}
	return flag;
}

function isLandscape()
{
	return (window.orientation === 90 || window.orientation === -90);
}

function pxValue(str)
{
	return str.substring(0, str.length - 2);
}

function includes(array, element)
{
	for (var k = 0; k < array.length; k++)
	{
		if (array[k] == element)
			return true;
	}
	return false;
}

function currentTimeMillis()
{
	return new Date().getTime();
}

if (typeof String.prototype.startsWith != 'function')
{
	String.prototype.startsWith = function(prefix)
	{
		return this.slice(0, prefix.length) === prefix;
	};
}

if (typeof String.prototype.endsWith != 'function')
{
	String.prototype.endsWith = function(suffix)
	{
		return this.indexOf(suffix, this.length - suffix.length) !== -1;
	};
}

function formatSize(size)
{
	if (size < 1024)
		return size + " 字节";
	else if (size < 1024 * 1024)
	{
		var str = (size / 1024).toFixed(1);
		if (str.endsWith(".0"))
			str = str.substring(0, str.length - 2);
		return str + " KB";
	}
	else if (size < 1024 * 1024 * 1024)
	{
		var str = (size / (1024 * 1024)).toFixed(1);
		if (str.endsWith(".0"))
			str = str.substring(0, str.length - 2);
		return str + " MB";
	}
	else
	{
		var str = (size / (1024 * 1024 * 1024)).toFixed(1);
		if (str.endsWith(".0"))
			str = str.substring(0, str.length - 2);
		return str + " GB";
	}
}

function formatTime(ms)
{
	var second = parseInt(ms / 1000);
	var minute = parseInt(second / 60);
	second = second % 60;
	var hour = parseInt(minute / 60);
	minute = minute % 60;
	var day = parseInt(hour / 24);
	hour = hour % 24;
	if (day != 0)
		return day + "天 " + (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
	else if (hour != 0)
		return hour + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
	else
		return minute + ":" + (second < 10 ? "0" + second : second);
}

function formatTimeStr(ms)
{
	var datetime = new Date();
	datetime.setTime(ms);
	var year = datetime.getFullYear();
	var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
	var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
	var hour = datetime.getHours() < 10 ? "0" + datetime.getHours() : datetime.getHours();
	var minute = datetime.getMinutes() < 10 ? "0" + datetime.getMinutes() : datetime.getMinutes();
	var second = datetime.getSeconds() < 10 ? "0" + datetime.getSeconds() : datetime.getSeconds();
	return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second;
}

function textIsEmpty(str)
{
	return str == null || str.length == 0;
}

function loadImg(src, callback)
{
	var img = new Image();
	img.src = src;
	if (img.complete)
	{
		if (callback != null)
			callback(img);
	}
	else
	{
		img.onload = function()
		{
			if (callback != null)
				callback(img);
		}
	}
}

function HashMap()
{
	var length = 0;
	var obj = new Object();

	this.isEmpty = function()
	{
		return length == 0;
	};

	this.containsKey = function(key)
	{
		return (key in obj);
	};

	this.containsValue = function(value)
	{
		for ( var key in obj)
		{
			if (obj[key] == value)
			{
				return true;
			}
		}
		return false;
	};

	this.put = function(key, value)
	{
		if (!this.containsKey(key))
		{
			length++;
		}
		obj[key] = value;
	};

	this.get = function(key)
	{
		return this.containsKey(key) ? obj[key] : null;
	};

	this.remove = function(key)
	{
		if (this.containsKey(key) && (delete obj[key]))
		{
			length--;
		}
	};

	this.values = function()
	{
		var _values = new Array();
		for ( var key in obj)
		{
			_values.push(obj[key]);
		}
		return _values;
	};

	this.keySet = function()
	{
		var _keys = new Array();
		for ( var key in obj)
		{
			_keys.push(key);
		}
		return _keys;
	};

	this.size = function()
	{
		return length;
	};

	this.clear = function()
	{
		length = 0;
		obj = new Object();
	};
}

function intColor(value)
{
	if (value == 0)
		return "transparent";
	var str = (value & 0xffffff).toString(16);
	while (str.length < 6)
		str = "0" + str;
	return "#" + str;
}

function getSuffix(filename)
{
	var pos = filename.lastIndexOf('.');
	var suffix = "";
	if (pos >= 0)
		suffix = filename.substring(pos);
	return suffix.toLowerCase();
}
