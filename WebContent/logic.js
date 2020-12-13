function MyNnd_Main()
{
	/* MyNnd_Log("MyNnd_Main", null); */
}

function MyNnd_SetAccount(account, psw)
{
	MyNnd_Log("MyNnd_SetAccount " + account + "," + psw, null);
	var txtUserName = MyNnd_Find_Id(document, "txtUserName");
	if (txtUserName != null)
	{
		MyNnd_Log("find txtUserName", null);
		txtUserName.value = account;
	}
	var txtPassword = MyNnd_Find_Id(document, "txtPassword");
	if (txtPassword != null)
	{
		MyNnd_Log("find txtPassword", null);
		txtPassword.value = psw;
	}
}

function MyNnd_ClickIgnore()
{
	/* MyNnd_Log("MyNnd_ClickIgnore", null); */
	MyNnd_Delay(4000, function()
	{
		var aui_content = MyNnd_Find_Class(document, "aui_content");
		if (aui_content != null)
		{
			/* MyNnd_Log("find aui_content", null); */
			var ignore = MyNnd_Find_Class(aui_content, "ignore");
			if (ignore != null)
			{
				MyNnd_Log("click ignore", null);
				MyNnd_Click(ignore);
			}
		}
	});
}

function MyNnd_ClickXtzj()
{
	/* MyNnd_Log("MyNnd_ClickXtzj", null); */
	MyNnd_Delay(4000, function()
	{
		var headMenuList = MyNnd_Find_Id(document, "headMenuList");
		if (headMenuList != null)
		{
			/* MyNnd_Log("find headMenuList", null); */
			var xtzj = MyNnd_FindByText(headMenuList, "A", "选题组卷");
			if (xtzj != null)
			{
				MyNnd_Log("click 选题组卷", null);
				MyNnd_Click(xtzj);
			}
		}
	});
}

function MyNnd_ClickTbzj()
{
	/* MyNnd_Log("MyNnd_ClickTbzj", null); */
	MyNnd_Delay(4000, function()
	{
		var pub_nav = MyNnd_Find_Class(document, "pub-nav");
		if (pub_nav != null)
		{
			/* MyNnd_Log("find pub_nav", null); */
			var tbzj = MyNnd_FindByText(pub_nav, "A", "同步组卷");
			if (tbzj != null)
			{
				MyNnd_Log("click 同步组卷", null);
				MyNnd_Click(tbzj);
			}
		}
	});
}

function MyNnd_ModifyPage()
{
	var kldList = MyNnd_Find_Class(document, "kldList");
	var topics = MyNnd_Find_TagList(kldList, "app-topic-show");
	MyNnd_Log("MyNnd_ModifyPage 题数：" + topics.length, null);
	for (var i = 0; i < topics.length; i++)
	{
		var topic = topics[i];

		var question_list = MyNnd_GetChild(topic, "DIV", 0);
		var question_content = MyNnd_GetChild(question_list, "DIV", 1);
		var ans_analy = MyNnd_GetChild(question_list, "DIV", 4);

		question_content.setAttribute("style", "font-size: 20px;");

		ans_analy.setAttribute("style", "display: block;");

		var answer = MyNnd_GetChild(ans_analy, "DIV", 0);
		var parse = MyNnd_GetChild(ans_analy, "DIV", 1);

		var answer_p = MyNnd_GetChild(answer, "P", 0);
		answer_p.setAttribute("style", "font-size: 20px;");

		var parse_div = MyNnd_GetChild(parse, "DIV", 0);
		parse_div.setAttribute("style", "font-size: 20px;");
	}
	return topics.length;
}

function MyNnd_CatchProblems()
{
	var problems = new Array();
	var kldList = MyNnd_Find_Class(document, "kldList");
	var topics = MyNnd_Find_TagList(kldList, "app-topic-show");
	/* MyNnd_Log("MyNnd_CatchProblems 题数："+topics.length, null); */
	for (var i = 0; i < topics.length; i++)
	{
		var topic = topics[i];

		var problem = new Object();

		var question_list = MyNnd_GetChild(topic, "DIV", 0);
		var question_content = MyNnd_GetChild(question_list, "DIV", 1);
		var operate_area = MyNnd_GetChild(question_list, "DIV", 3);
		var ans_analy = MyNnd_GetChild(question_list, "DIV", 4);

		problem.id = question_content.getAttribute("id").trim();

		problem.content = question_content.innerText.trim().replace(new RegExp("\r\n", "g"), "").replace(new RegExp("\n", "g"), "");

		var answer = MyNnd_GetChild(ans_analy, "DIV", 0);
		var parse = MyNnd_GetChild(ans_analy, "DIV", 1);
		var knowledge = MyNnd_GetChild(ans_analy, "DIV", 2);
		var style = MyNnd_GetChild(ans_analy, "DIV", 3);

		var answer_p = MyNnd_GetChild(answer, "P", 0);
		problem.answer = answer_p.innerText.trim().replace(new RegExp("\r\n", "g"), "").replace(new RegExp("\n", "g"), "");

		var parse_div = MyNnd_GetChild(parse, "DIV", 0);
		problem.parse = parse_div.innerText.trim().replace(new RegExp("\r\n", "g"), "").replace(new RegExp("\n", "g"), "");

		problem.knowledges = new Array();
		var knowledge_div = MyNnd_GetChild(knowledge, "DIV", 0);
		var spans = MyNnd_Find_TagList(knowledge_div, "SPAN");
		for (var j = 0; j < spans.length; j++)
		{
			var span = spans[j];
			problem.knowledges[problem.knowledges.length++] = span.innerHTML.trim();
		}

		var style_div = MyNnd_GetChild(style, "DIV", 0);
		var style_p = MyNnd_GetChild(style_div, "P", 0);
		problem.style = style_p.innerHTML.trim();

		var zujuan = MyNnd_Find_Class(operate_area, "zujuan");
		var zuoda = MyNnd_Find_Class(operate_area, "zuoda");
		problem.zujuan = zujuan.innerText.trim();
		problem.zuoda = zuoda.innerText.trim();

		var defens = operate_area.getElementsByClassName("defen");
		if (defens != null && defens.length == 1)
		{
			problem.defen = defens[0].innerText.trim();
		}

		var nandus = operate_area.getElementsByClassName("nandu");
		if (nandus != null && nandus.length == 1)
		{
			problem.nandu = nandus[0].innerText.trim();
		}

		problems[problems.length++] = problem;
	}
	return problems;
}

function MyNnd_CheckTopicImpl(text)
{
	if (text.indexOf("\\(") >= 0 || text.indexOf("(\\") >= 0 || text.indexOf("^{") >= 0)
	{
		return false;
	}
	else
	{
		return true;
	}
}

function MyNnd_CheckTopic(problem)
{
	if (!MyNnd_CheckTopicImpl(problem.content))
	{
		MyNnd_Log("bad content : " + problem.content, null);
		return false;
	}
	if (!MyNnd_CheckTopicImpl(problem.answer))
	{
		MyNnd_Log("bad answer : " + problem.answer, null);
		return false;
	}
	if (!MyNnd_CheckTopicImpl(problem.parse))
	{
		MyNnd_Log("bad parse : " + problem.parse, null);
		return false;
	}
	return true;
}

function MyNnd_HasRequestError()
{
	var pt_msg_div_list = MyNnd_Find_ClassList(document, "pt_msg_div");
	if (pt_msg_div_list.length > 0)
	{
		for (var i = 0; i < pt_msg_div_list.length; i++)
		{
			var pt_msg_div = pt_msg_div_list[i];
			var msg_str = pt_msg_div.innerText.trim();
			if (msg_str == "请求出错")
			{
				return true;
			}
		}
	}
	return false;
}

function MyNnd_TryCatchProblems(phase, subject, material, diff, area, year, currChapterPath, currPage, times)
{
	var problems = MyNnd_CatchProblems();
	var checkSuccess = true;
	for (var i = 0; i < problems.length; i++)
	{
		var problem = problems[i];
		if (!MyNnd_CheckTopic(problem))
		{
			MyNnd_Log(times + " check fail and topic number is : " + (i + 1), null);
			checkSuccess = false;
			break;
		}
	}
	if (checkSuccess || times > 6)
	{
		for (var i = 0; i < problems.length; i++)
		{
			var problem = problems[i];
			problem.phase = phase;
			problem.subject = subject;
			problem.material = material;
			problem.chapterPath = currChapterPath;
			problem.diff = diff;
			problem.area = area;
			problem.year = year;
		}
		var info = new Object();
		info.phase = phase;
		info.subject = subject;
		info.material = material;
		info.chapterPath = currChapterPath;
		info.diff = diff;
		info.area = area;
		info.year = year;
		var htmlStr = document.getElementsByTagName('html')[0].innerHTML;
		if (MyNnd_HasRequestError())
		{
			MyNnd_Log("has 请求出错", null);
		}
		else
		{
			MyNnd_CallCmd4("resultProblems", JSON.stringify(info), currPage, JSON.stringify(problems), htmlStr, null);
		}
	}
	else
	{
		MyNnd_Delay(8000, function()
		{
			MyNnd_TryCatchProblems(phase, subject, material, diff, area, year, currChapterPath, currPage, times + 1);
		});
	}
}

function MyNnd_CatchOnePage(phase, subject, material, diff, area, year, currChapterPath, currPage)
{
	/* MyNnd_Log("MyNnd_CatchOnePage " + diff + "," + area + "," + year + "," + currChapterPath + "," + currPage, null); */
	var totalPage_span = MyNnd_Find_Class(document, "totalPage");
	if (totalPage_span != null)
	{
		var totalPage = totalPage_span.innerHTML.trim();
		/* MyNnd_Log("find totalPage " + totalPage, null); */
		if (parseInt(currPage) <= parseInt(totalPage))
		{
			var snyc_page = MyNnd_Find_Class(document, "snyc-page");
			if (snyc_page != null)
			{
				var snyc_page_div = MyNnd_GetChild(snyc_page, "DIV", 0);
				if (snyc_page_div != null)
				{
					var page_str = snyc_page_div.innerText.trim();
					var currNum = page_str.substring(1, page_str.indexOf("/"));
					/* MyNnd_Log("currNum " + currNum, null); */
					if (parseInt(currPage) == parseInt(currNum))
					{
						MyNnd_Delay(8000, function()
						{
							var topicCount = MyNnd_ModifyPage();
							if (topicCount > 0)
							{
								MyNnd_Delay(20000, function()
								{
									MyNnd_TryCatchProblems(phase, subject, material, diff, area, year, currChapterPath, currPage, 0);
								});
							}
							else
							{
								var info = new Object();
								info.phase = phase;
								info.subject = subject;
								info.material = material;
								info.chapterPath = currChapterPath;
								info.diff = diff;
								info.area = area;
								info.year = year;
								MyNnd_CallCmd1("tick", JSON.stringify(info), null);
								if (true)
								{
									MyNnd_CatchNextChapter(phase, subject, material, diff, area, year, currChapterPath, 1);
								}
							}
						});
					}
					else
					{
						if (parseInt(currNum) < parseInt(currPage))
						{
							var willPage = parseInt(currNum) + 2;
							if (parseInt(willPage) > parseInt(currPage))
							{
								willPage = currPage;
							}
							var page_nav_box = MyNnd_Find_ClassExt(document, "page_nav_box", 2, 2, 1);
							if (page_nav_box != null)
							{
								var jump = MyNnd_FindByText(page_nav_box, "A", willPage);
								if (jump != null)
								{
									MyNnd_Log("click page : " + willPage, null);
									MyNnd_Click_Delay_Callback(jump, 4000, function()
									{
										MyNnd_CatchOnePage(phase, subject, material, diff, area, year, currChapterPath, currPage);
									});
								}
							}
						}
					}
				}
			}
		}
		else
		{
			var info = new Object();
			info.phase = phase;
			info.subject = subject;
			info.material = material;
			info.chapterPath = currChapterPath;
			info.diff = diff;
			info.area = area;
			info.year = year;
			MyNnd_CallCmd1("tick", JSON.stringify(info), null);
			if (true)
			{
				MyNnd_CatchNextChapter(phase, subject, material, diff, area, year, currChapterPath, 1);
			}
		}
	}
}

function MyNnd_CatchNextChapter(phase, subject, material, diff, area, year, currChapterPath, currPage)
{
	var chapterLeaf = MyNnd_FindNextLeafChapter(currChapterPath);
	if (chapterLeaf != null)
	{
		MyNnd_Log("click chapterLeaf : " + chapterLeaf.path, null);
		MyNnd_MouseDown_Delay_Callback(chapterLeaf.li_a, 4000, function()
		{
			currChapterPath = chapterLeaf.path;
			var totalPage_span = MyNnd_Find_Class(document, "totalPage");
			if (totalPage_span != null)
			{
				var totalPage = totalPage_span.innerHTML.trim();
				/* MyNnd_Log("find totalPage " + totalPage, null); */
				if (parseInt(currPage) <= parseInt(totalPage))
				{
					MyNnd_CatchOnePage(phase, subject, material, diff, area, year, currChapterPath, currPage);
				}
				else
				{
					MyNnd_CatchNextChapter(phase, subject, material, diff, area, year, currChapterPath, 1);
				}
			}
		});
	}
	else
	{
		/* MyNnd_Log("not find next chapterLeaf", null); */
		var info = new Object();
		info.phase = phase;
		info.subject = subject;
		info.material = material;
		info.diff = diff;
		info.area = area;
		info.year = year;
		MyNnd_CallCmd1("catchOver", JSON.stringify(info), null);
	}
}

function MyNnd_CatchPage(diff, area, year, currChapterPath, currPage)
{
	/* MyNnd_Log("MyNnd_CatchPage " + diff + "," + area + "," + year + "," + currChapterPath + "," + currPage, null); */
	MyNnd_Delay(4000, function()
	{
		/* MyNnd_Log("start catch", null); */
		var phase_subject = MyNnd_Find_Class(document, "phase_subject");
		if (phase_subject != null)
		{
			var phaseSubject = phase_subject.innerHTML.trim().split("·");
			var phase = phaseSubject[0].trim();
			var subject = phaseSubject[1].trim();
			/* MyNnd_Log("find phase_subject " + phase + "," + subject, null); */
			var leftTree = MyNnd_Find_Class(document, "leftTree");
			if (leftTree != null)
			{
				/* MyNnd_Log("find leftTree", null); */
				var leftTree_material = MyNnd_GetChild(leftTree, "DIV", 0);
				var curBook = MyNnd_Find_Class(leftTree_material, "curBook");
				var material = curBook.innerText.trim();
				/* MyNnd_Log("find material " + material, null); */

				var selectorPanel = MyNnd_Find_Class(document, "selectorPanel");
				if (selectorPanel != null)
				{
					/* MyNnd_Log("find selectorPanel", null); */
					var span = MyNnd_GetChild(selectorPanel, "SPAN", 0);
					var dl_diff = MyNnd_GetChild(span, "DL", 1);
					var a_diff = MyNnd_FindByText(dl_diff, "A", diff);
					if (a_diff != null)
					{
						MyNnd_Log("click " + diff, null);
						MyNnd_Click_Delay_Callback(a_diff, 4000, function()
						{
							var filter_item_content = MyNnd_Find_Class(span, "filter-item-content");
							var filter_area = MyNnd_GetChild(filter_item_content, "app-condition-selector-more", 0);
							var filter_year = MyNnd_GetChild(filter_item_content, "app-condition-selector-more", 2);

							var filter_area_dropdown = MyNnd_Find_Class(filter_area, "dropdown");
							var a_area = MyNnd_FindByText(filter_area_dropdown, "A", area);
							if (a_area != null)
							{
								MyNnd_Log("click " + area, null);
								MyNnd_Click_Delay_Callback(a_area, 4000, function()
								{
									var filter_year_dropdown = MyNnd_Find_Class(filter_year, "dropdown");
									var a_year = MyNnd_FindByText(filter_year_dropdown, "A", year);
									if (a_year != null)
									{
										MyNnd_Log("click " + year, null);
										MyNnd_Click_Delay_Callback(a_year, 4000, function()
										{
											MyNnd_ClickChapter(function()
											{
												/* start logic */
												if (currChapterPath == "")
												{
													MyNnd_CatchNextChapter(phase, subject, material, diff, area, year, currChapterPath, 1);
												}
												else
												{
													var chapterLeaf = MyNnd_FindLeafChapter(currChapterPath);
													if (chapterLeaf != null)
													{
														MyNnd_MouseDown_Delay_Callback(chapterLeaf.li_a, 4000, function()
														{
															MyNnd_CatchOnePage(phase, subject, material, diff, area, year, currChapterPath, currPage);
														});
													}
													else
													{
														alert("error @293 " + currChapterPath);
													}
												}
												/* end logic */
											});
										});
									}
								});
							}
						});
					}
				}
			}
		}
	});
}

function MyNnd_GetPhaseSubject()
{
	/* MyNnd_Log("MyNnd_GetPhaseSubject", null); */
	var result = new Object();
	try
	{
		var phase_subject = MyNnd_Find_Class(document, "phase_subject");
		if (phase_subject != null)
		{
			var phaseSubject = phase_subject.innerHTML.trim().split("·");
			result.phase = phaseSubject[0].trim();
			result.subject = phaseSubject[1].trim();
			/* MyNnd_Log("find phase_subject " + result.phase + "," + result.subject, null); */
			return result;
		}
	}
	catch (err)
	{
		alert(err.message);
	}
	return null;
}

function MyNnd_GetMaterial()
{
	/* MyNnd_Log("MyNnd_GetMaterial", null); */
	var result = new Object();
	var leftTree = MyNnd_Find_Class(document, "leftTree");
	if (leftTree != null)
	{
		/* MyNnd_Log("find leftTree", null); */
		var leftTree_material = MyNnd_GetChild(leftTree, "DIV", 0);
		var curBook = MyNnd_Find_Class(leftTree_material, "curBook");
		result.material = curBook.innerText.trim();
		return result;
	}
}

function MyNnd_GetGroup()
{
	/* MyNnd_Log("MyNnd_GetGroup", null); */
	var group = new Object();
	try
	{
		var selectorPanel = MyNnd_Find_Class(document, "selectorPanel");
		if (selectorPanel != null)
		{
			/* MyNnd_Log("find selectorPanel", null); */
			var span = MyNnd_GetChild(selectorPanel, "SPAN", 0);
			var dl_style = MyNnd_GetChild(span, "DL", 0);
			var dl_diff = MyNnd_GetChild(span, "DL", 1);

			group.diffs = new Array();
			group.areas = new Array();
			group.years = new Array();

			var as_diff = MyNnd_Find_TagList(dl_diff, "A");
			for (var i = 0; i < as_diff.length; i++)
			{
				var a_diff = as_diff[i];
				if (a_diff.innerHTML.trim() != "全部")
				{
					group.diffs[group.diffs.length++] = a_diff.innerHTML.trim();
				}
			}

			var filter_item_content = MyNnd_Find_Class(span, "filter-item-content");
			var filter_area = MyNnd_GetChild(filter_item_content, "app-condition-selector-more", 0);
			var filter_year = MyNnd_GetChild(filter_item_content, "app-condition-selector-more", 2);

			var filter_area_dropdown = MyNnd_Find_Class(filter_area, "dropdown");
			var as_area = MyNnd_Find_TagList(filter_area_dropdown, "A");
			for (var i = 0; i < as_area.length; i++)
			{
				var a_area = as_area[i];
				if (a_area.innerHTML.trim() != "全国" && a_area.innerHTML.trim() != "本省" && a_area.innerHTML.trim() != "香港" && a_area.innerHTML.trim() != "澳门" && a_area.innerHTML.trim() != "台湾")
				{
					group.areas[group.areas.length++] = a_area.innerHTML.trim();
				}
			}

			var filter_year_dropdown = MyNnd_Find_Class(filter_year, "dropdown");
			var as_year = MyNnd_Find_TagList(filter_year_dropdown, "A");
			for (var i = 0; i < as_year.length; i++)
			{
				var a_year = as_year[i];
				if (a_year.innerHTML.trim() != "全部")
				{
					group.years[group.years.length++] = a_year.innerHTML.trim();
				}
			}

			return group;
		}
	}
	catch (err)
	{
		alert(err.message);
	}
	return null;
}

function MyNnd_ClickKnowledge()
{
	/* MyNnd_Log("MyNnd_ClickKnowledge", null); */
	var leftTree = MyNnd_Find_Class(document, "leftTree");
	if (leftTree != null)
	{
		/* MyNnd_Log("find leftTree", null); */
		var leftTree_div = MyNnd_GetChild(leftTree, "DIV", 2);
		var leftTree_app_tree = MyNnd_GetChild(leftTree_div, "app-tree", 0);
		var ztree = MyNnd_GetChild(leftTree_app_tree, "UL", 0);
		var roots_close_list = MyNnd_Find_ClassList(ztree, "roots_close");
		if (roots_close_list.length > 0)
		{
			MyNnd_Click_Delay_Callback(roots_close_list[0], 500, function()
			{
				MyNnd_ClickKnowledge();
			});
		}
		else
		{
			var bottom_close_list = MyNnd_Find_ClassList(ztree, "bottom_close");
			if (bottom_close_list.length > 0)
			{
				MyNnd_Click_Delay_Callback(bottom_close_list[0], 500, function()
				{
					MyNnd_ClickKnowledge();
				});
			}
			else
			{
				var center_close_list = MyNnd_Find_ClassList(ztree, "center_close");
				if (center_close_list.length > 0)
				{
					MyNnd_Click_Delay_Callback(center_close_list[0], 500, function()
					{
						MyNnd_ClickKnowledge();
					});
				}
				else
				{
					MyNnd_Log("展开 knowledges over", null);
					var phaseSubject = MyNnd_GetPhaseSubject();
					var knowledges = MyNnd_GetKnowledge();
					knowledges.phase = phaseSubject.phase;
					knowledges.subject = phaseSubject.subject;
					MyNnd_CallCmd1("resultKnowledges", JSON.stringify(knowledges), null);
				}
			}
		}
	}
}

function MyNnd_GetKnowledgeNode(li)
{
	var knowledge = new Object();

	var li_span = MyNnd_GetChild(li, "SPAN", 0);
	var li_a = MyNnd_GetChild(li, "A", 0);
	var li_ul = MyNnd_GetChild(li, "UL", 0);
	var span = MyNnd_GetChild(li_a, "SPAN", 1);

	knowledge.index = li.getAttribute("id").trim().substring(37);
	knowledge.name = span.innerHTML.trim();

	if (li_ul != null)
	{
		knowledge.knowledges = new Array();
		for (var i = 0; i < li_ul.childNodes.length; i++)
		{
			var child = li_ul.childNodes[i];
			if (child.nodeName == "LI")
			{
				knowledge.knowledges[knowledge.knowledges.length++] = MyNnd_GetKnowledgeNode(child);
			}
		}
	}

	return knowledge;
}

function MyNnd_GetKnowledge()
{
	/* MyNnd_Log("MyNnd_GetKnowledge", null); */
	var root = new Object();
	try
	{
		var leftTree = MyNnd_Find_Class(document, "leftTree");
		if (leftTree != null)
		{
			/* MyNnd_Log("find leftTree", null); */

			var leftTree_div = MyNnd_GetChild(leftTree, "DIV", 2);
			var leftTree_app_tree = MyNnd_GetChild(leftTree_div, "app-tree", 0);
			var ztree = MyNnd_GetChild(leftTree_app_tree, "UL", 0);

			root.knowledges = new Array();

			for (var i = 0; i < ztree.childNodes.length; i++)
			{
				var li = ztree.childNodes[i];
				if (li.nodeName == "LI")
				{
					root.knowledges[root.knowledges.length++] = MyNnd_GetKnowledgeNode(li);
				}
			}

			return root;
		}
	}
	catch (err)
	{
		alert(err.message);
	}
	return null;
}

function MyNnd_ClickChapter(callback)
{
	/* MyNnd_Log("MyNnd_ClickChapter", null); */
	var leftTree = MyNnd_Find_Class(document, "leftTree");
	if (leftTree != null)
	{
		/* MyNnd_Log("find leftTree", null); */
		var leftTree_div = MyNnd_GetChild(leftTree, "DIV", 1);
		var leftTree_app_tree = MyNnd_GetChild(leftTree_div, "app-tree", 0);
		var ztree = MyNnd_GetChild(leftTree_app_tree, "UL", 0);
		var roots_close_list = MyNnd_Find_ClassList(ztree, "roots_close");
		if (roots_close_list.length > 0)
		{
			MyNnd_Click_Delay_Callback(roots_close_list[0], 500, function()
			{
				MyNnd_ClickChapter(callback);
			});
		}
		else
		{
			var bottom_close_list = MyNnd_Find_ClassList(ztree, "bottom_close");
			if (bottom_close_list.length > 0)
			{
				MyNnd_Click_Delay_Callback(bottom_close_list[0], 500, function()
				{
					MyNnd_ClickChapter(callback);
				});
			}
			else
			{
				var center_close_list = MyNnd_Find_ClassList(ztree, "center_close");
				if (center_close_list.length > 0)
				{
					MyNnd_Click_Delay_Callback(center_close_list[0], 500, function()
					{
						MyNnd_ClickChapter(callback);
					});
				}
				else
				{
					MyNnd_Log("展开 chapters over", null);
					if (callback != null)
						callback();
				}
			}
		}
	}
}

function MyNnd_CatchChapter()
{
	MyNnd_ClickChapter(function()
	{
		var phaseSubject = MyNnd_GetPhaseSubject();
		var chapters = MyNnd_GetChapter();
		chapters.phase = phaseSubject.phase;
		chapters.subject = phaseSubject.subject;
		chapters.material = MyNnd_GetMaterial().material;
		MyNnd_CallCmd1("resultChapters", JSON.stringify(chapters), null);
	});
}

function MyNnd_GetChapterNode(li)
{
	var chapter = new Object();

	var li_span = MyNnd_GetChild(li, "SPAN", 0);
	var li_a = MyNnd_GetChild(li, "A", 0);
	var li_ul = MyNnd_GetChild(li, "UL", 0);
	var span = MyNnd_GetChild(li_a, "SPAN", 1);

	chapter.index = li.getAttribute("id").trim().substring(37);
	chapter.name = span.innerHTML.trim();

	if (li_ul != null)
	{
		chapter.chapters = new Array();
		for (var i = 0; i < li_ul.childNodes.length; i++)
		{
			var child = li_ul.childNodes[i];
			if (child.nodeName == "LI")
			{
				chapter.chapters[chapter.chapters.length++] = MyNnd_GetChapterNode(child);
			}
		}
	}

	return chapter;
}

function MyNnd_GetChapter()
{
	/* MyNnd_Log("MyNnd_GetChapter", null); */
	var root = new Object();
	try
	{
		var leftTree = MyNnd_Find_Class(document, "leftTree");
		if (leftTree != null)
		{
			/* MyNnd_Log("find leftTree", null); */

			var leftTree_div = MyNnd_GetChild(leftTree, "DIV", 1);
			var leftTree_app_tree = MyNnd_GetChild(leftTree_div, "app-tree", 0);
			var ztree = MyNnd_GetChild(leftTree_app_tree, "UL", 0);

			root.chapters = new Array();

			for (var i = 0; i < ztree.childNodes.length; i++)
			{
				var li = ztree.childNodes[i];
				if (li.nodeName == "LI")
				{
					root.chapters[root.chapters.length++] = MyNnd_GetChapterNode(li);
				}
			}

			return root;
		}
	}
	catch (err)
	{
		alert(err.message);
	}
	return null;
}

function MyNnd_GetLeafChapterNode(li, chapterPath, chapterLeafs)
{
	var li_span = MyNnd_GetChild(li, "SPAN", 0);
	var li_a = MyNnd_GetChild(li, "A", 0);
	var li_ul = MyNnd_GetChild(li, "UL", 0);
	var span = MyNnd_GetChild(li_a, "SPAN", 1);

	var name = span.innerHTML.trim();

	chapterPath = chapterPath + "---##---" + name;

	if (li_ul != null && li_ul.childNodes.length > 0)
	{
		for (var i = 0; i < li_ul.childNodes.length; i++)
		{
			var child = li_ul.childNodes[i];
			if (child.nodeName == "LI")
			{
				MyNnd_GetLeafChapterNode(child, chapterPath, chapterLeafs);
			}
		}
	}
	else
	{
		var chapterLeafPath = chapterPath;
		/* MyNnd_Log("chapterLeafPath : " + chapterLeafPath, null); */
		var chapterLeaf = new Object();
		chapterLeaf.path = chapterLeafPath;
		chapterLeaf.li_a = li_a;
		chapterLeafs[chapterLeafs.length++] = chapterLeaf;
	}
}

function MyNnd_GetLeafChapter()
{
	/* MyNnd_Log("MyNnd_GetLeafChapter", null); */
	try
	{
		var leftTree = MyNnd_Find_Class(document, "leftTree");
		if (leftTree != null)
		{
			/* MyNnd_Log("find leftTree", null); */

			var leftTree_div = MyNnd_GetChild(leftTree, "DIV", 1);
			var leftTree_app_tree = MyNnd_GetChild(leftTree_div, "app-tree", 0);
			var ztree = MyNnd_GetChild(leftTree_app_tree, "UL", 0);

			var chapterLeafs = new Array();

			var chapterPath = "";

			for (var i = 0; i < ztree.childNodes.length; i++)
			{
				var li = ztree.childNodes[i];
				if (li.nodeName == "LI")
				{
					MyNnd_GetLeafChapterNode(li, chapterPath, chapterLeafs);
				}
			}

			return chapterLeafs;
		}
	}
	catch (err)
	{
		alert(err.message);
	}
}

function MyNnd_FindNextLeafChapter(currChapterPath)
{
	var chapterLeafs = MyNnd_GetLeafChapter();
	for (var i = 0; i < chapterLeafs.length; i++)
	{
		var chapterLeaf = chapterLeafs[i];
		if (currChapterPath == "")
		{
			return chapterLeaf;
		}
		else
		{
			if (currChapterPath == chapterLeaf.path)
			{
				currChapterPath = "";
			}
		}
	}
	return null;
}

function MyNnd_FindLeafChapter(currChapterPath)
{
	var chapterLeafs = MyNnd_GetLeafChapter();
	for (var i = 0; i < chapterLeafs.length; i++)
	{
		var chapterLeaf = chapterLeafs[i];
		if (currChapterPath == chapterLeaf.path)
		{
			return chapterLeaf;
		}
	}
	return null;
}

function MyNnd_Test(currChapterPath)
{
	MyNnd_ClickChapter(function()
	{
		var chapterLeaf = MyNnd_FindNextLeafChapter(currChapterPath);
		if (chapterLeaf != null)
		{
			MyNnd_Log("next chapterLeaf : " + chapterLeaf.path, null);
			MyNnd_MouseDown_Delay_Callback(chapterLeaf.li_a, 4000, function()
			{
				MyNnd_Log("222", null);
			});
		}
		else
		{
			MyNnd_Log("not find next chapterLeaf", null);
		}
	});
}

MyNnd_Main();
