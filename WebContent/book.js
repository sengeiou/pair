var currNum = 1;
var allNum = 0;
var book = new Array();

function MyNnd_CatchPage(callback)
{
	var page_wrapper_list = MyNnd_Find_ClassList(document, "page-wrapper");
	for (var i = 0; i < page_wrapper_list.length; i++)
	{
		var page_wrapper = page_wrapper_list[i];
		var page = page_wrapper.getAttribute("page");
		if (page == currNum)
		{
			/* MyNnd_Log("find page-wrapper : " + page, null); */
			var IMG = MyNnd_Find_Tag(page_wrapper, "img");
			if (IMG != null)
			{
				var src = IMG.getAttribute("src");
				MyNnd_Log(page + " find IMG src : " + src, null);

				var onepage = new Object();
				onepage.page = page;
				onepage.url = src;
				book[book.length++] = onepage;

				if (allNum == currNum)
				{
					MyNnd_Log("catch all over ---------", null);
					MyNnd_CallCmd2("catchBookOver", JSON.stringify(book), window.location.href, null);
				}
				else
				{
					currNum++;
					if (callback != null)
						callback();
				}
			}
			else
			{
				MyNnd_Delay(10000, function()
				{
					MyNnd_CatchPage(callback);
				});
			}
			return;
		}
	}
}

function MyNnd_ClickNext()
{
	var nextArrow = MyNnd_Find_Class(document, "nextArrow");
	if (nextArrow != null)
	{
		MyNnd_Log("find nextArrow", null);
		var iconfont = MyNnd_Find_Class(nextArrow, "iconfont");
		if (iconfont != null)
		{
			/* MyNnd_Log("find iconfont", null); */
			MyNnd_Click(iconfont);
			return true;
		}
	}
	return false;
}

function MyNnd_ToClick()
{
	if (MyNnd_ClickNext())
	{
		MyNnd_Delay(10000, function()
		{
			MyNnd_CatchPage(function()
			{
				MyNnd_CatchPage(function()
				{
					MyNnd_ToClick();
				});
			});
		});
	}
}

function MyNnd_GetPageCount()
{
	var inputPage = MyNnd_Find_Class(document, "inputPage");
	if (inputPage != null)
	{
		MyNnd_Log("find inputPage : " + inputPage.value, null);
		var currNum = inputPage.value.substring(inputPage.value.indexOf("/") + 1);
		MyNnd_Log("currNum : " + currNum, null);
		return parseInt(currNum);
	}
	return 0;
}

function MyNnd_Start()
{
	MyNnd_Log("MyNnd_Start : " + window.location.href, null);
	try
	{
		currNum = 1;
		allNum = 0;
		book = new Array();

		allNum = MyNnd_GetPageCount();
		if (allNum > 0)
		{
			MyNnd_CatchPage(function()
			{
				MyNnd_ToClick();
			});
		}
	}
	catch (err)
	{
		alert(err.message);
	}
}

function MyNnd_Main()
{
	MyNnd_Log("MyNnd_Main", null);
	MyNnd_Delay(6000, function()
	{
		MyNnd_Start();
	});
}

MyNnd_Main();
