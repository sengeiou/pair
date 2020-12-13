package com.lys.config;

public final class Table
{
	public static final Table instance = new Table();

	private static class Base
	{
		protected String pack(String str)
		{
			return toString() + "_" + str;
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName().toLowerCase();
		}
	}

	// ------------------------

	public static final class App extends Base
	{
		public final String pkgName = pack("pkg_name");
		public final String channel = pack("channel");
		public final String versionCode = pack("version_code");
		public final String versionName = pack("version_name");
		public final String probability = pack("probability");
		public final String name = pack("name");
		public final String size = pack("size");
		public final String apkUrl = pack("apkUrl");
		public final String icoUrl = pack("icoUrl");
		public final String des = pack("des");
		public final String ts = pack("ts");
	}

	public static final App app = new App();

	// ------------------------

	public static final class Device extends Base
	{
		public final String deviceId = pack("device_id");
		public final String clientVersion = pack("client_version");
		public final String userId = pack("userId");
		public final String loginCount = pack("login_count");
		public final String loginResult = pack("login_result");
		public final String ts = pack("ts");
	}

	public static final Device device = new Device();

	// ------------------------

	public static final class User extends Base
	{
		public final String id = pack("id");
		public final String userType = pack("user_type");
		public final String psw = pack("psw");
		public final String name = pack("name");
		public final String head = pack("head");
		public final String token = pack("token");
		public final String sex = pack("sex");
		public final String grade = pack("grade");
		public final String vipLevel = pack("vip_level");
		public final String vipTime = pack("vip_time");
		public final String phone = pack("phone");
		public final String score = pack("score");
		public final String cpId = pack("cp_id");
		public final String ts = pack("ts");
	}

	public static final User user = new User();

	// ------------------------

	public static final class Friend extends Base
	{
		public final String userId = pack("user_id");
		public final String friendId = pack("friend_id");
		public final String group = pack("group");
		public final String ts = pack("ts");
	}

	public static final Friend friend = new Friend();

	// ------------------------

	public static final class Task extends Base
	{
		public final String id = pack("id");
		public final String userId = pack("user_id");
		public final String sendUserId = pack("send_user_id");
		public final String type = pack("type");
		public final String jobType = pack("job_type");
		public final String group = pack("group");
		public final String name = pack("name");
		public final String note = pack("note");
		public final String createTime = pack("create_time");
		public final String state = pack("state");
		public final String text = pack("text");
		public final String comment = pack("comment");
		public final String overTime = pack("over_time");
		public final String score = pack("score");
		public final String open = pack("open");
		public final String timesForWeb = pack("times_for_web");
		public final String lastModifyTime = pack("last_modify_time");
		public final String ts = pack("ts");
	}

	public static final Task task = new Task();

	// ------------------------

	public static final class Live extends Base
	{
		public final String id = pack("id");
		public final String actorId = pack("actor_id");
		public final String name = pack("name");
		public final String des = pack("des");
		public final String cover = pack("cover");
		public final String video = pack("video");
		public final String duration = pack("duration");
		public final String taskId = pack("task_id");
		public final String type = pack("type");
		public final String userIds = pack("user_ids");
		public final String startTime = pack("start_time");
		public final String ts = pack("ts");
	}

	public static final Live live = new Live();

	// ------------------------

	public static final class Topic_Record extends Base
	{
		public final String userId = pack("user_id");
		public final String topicId = pack("topic_id");
		public final String fav = pack("fav");
		public final String result = pack("result");
		public final String time = pack("time");
		public final String ts = pack("ts");
	}

	public static final Topic_Record topicRecord = new Topic_Record();

	// ------------------------

	public static final class Teach_Record extends Base
	{
		public final String teachId = pack("teach_id");
		public final String userId = pack("user_id");
		public final String isHost = pack("is_host");
		public final String targetCount = pack("target_count");
		public final String targetIds = pack("target_ids");
		public final String taskId = pack("task_id");
		public final String startTime = pack("start_time");
		public final String overTime = pack("over_time");
		public final String teachPages = pack("teach_pages");
		public final String confirmMsg = pack("confirm_msg");
		public final String questionMatch = pack("question_match");
		public final String questionDiff = pack("question_diff");
		public final String questionGot = pack("question_got");
		public final String questionQuality = pack("question_quality");
		public final String questionLike = pack("question_like");
		public final String questionHot = pack("question_hot");
		public final String questionMind = pack("question_mind");
		public final String questionLogic = pack("question_logic");
		public final String questionOther = pack("question_other");
		public final String ts = pack("ts");
	}

	public static final Teach_Record teachRecord = new Teach_Record();

	// ------------------------

//	public static final class Md5 extends Base
//	{
//		public final String md5 = pack("md5");
//		public final String path = pack("path");
//		public final String ts = pack("ts");
//	}
//
//	public static final Md5 md5 = new Md5();

	// ------------------------

	public static final class ZhiXue extends Base
	{
		public final String id = pack("id");
		public final String phase = pack("phase");
		public final String subject = pack("subject");
		public final String material = pack("material");
//		public final String chapterPath = pack("chapter_path");
//		public final String style = pack("style");
		public final String diff = pack("diff");
		public final String area = pack("area");
		public final String year = pack("year");
		public final String currChapterPath = pack("curr_chapter_path");
		public final String currPage = pack("curr_page");
		public final String totalPage = pack("total_page");
		public final String sort = pack("sort");
		public final String deviceId = pack("device_id");
		public final String ts = pack("ts");
	}

	public static final ZhiXue zhixue = new ZhiXue();

	// ------------------------

	public static final class ZhiXue_Account extends Base
	{
		public final String account = pack("account");
		public final String psw = pack("psw");
		public final String state = pack("state");
		public final String deviceId = pack("device_id");
		public final String ts = pack("ts");
	}

	public static final ZhiXue_Account zhixueAccount = new ZhiXue_Account();

	// ------------------------

	public static final class Knowledge extends Base
	{
		public final String id = pack("id");
		public final String parent = pack("parent");
		public final String name = pack("name");
		public final String leaf = pack("leaf");
	}

	public static final Knowledge knowledge = new Knowledge();

	// ------------------------

	public static final class Chapter extends Base
	{
		public final String id = pack("id");
		public final String parent = pack("parent");
		public final String name = pack("name");
		public final String leaf = pack("leaf");
	}

	public static final Chapter chapter = new Chapter();

	// ------------------------

	public static final class Topic extends Base
	{
		public final String id = pack("id");
		public final String phase = pack("phase");
		public final String subject = pack("subject");
		public final String material = pack("material");
		public final String style = pack("style");
		public final String diff = pack("diff");
		public final String area = pack("area");
		public final String year = pack("year");
		public final String knowledges = pack("knowledges");
		public final String zujuan = pack("zujuan");
		public final String zuoda = pack("zuoda");
		public final String defen = pack("defen");
		public final String nandu = pack("nandu");
		public final String chapters = pack("chapters");
		public final String content = pack("content");
		public final String answer = pack("answer");
		public final String parse = pack("parse");
		public final String reportError = pack("report_error");
		public final String reportGood = pack("report_good");
		public final String remark = pack("remark");
	}

	public static final Topic topic = new Topic();

	// ------------------------

	public static final class Matter extends Base
	{
		public final String id = pack("id");
		public final String name = pack("name");
		public final String userId = pack("user_id"); // 负责人
		public final String type = pack("type"); // 精品课、一对一
		public final String place = pack("place"); // BANNER、推荐列表、无
		public final String cover = pack("cover");
		public final String banner = pack("banner");
		public final String buyCount = pack("buy_count"); // 精品课有效
		public final String moneyRaw = pack("money_raw"); // 精品课有效
		public final String money = pack("money"); // 一对一时指单价
		public final String hours = pack("hours"); // 一对一小时列表（购买时长、赠送时长）
		public final String sort = pack("sort"); // 排序
		public final String invalid = pack("invalid"); // 是否无效
		public final String details = pack("details"); // 视频图片列表
		public final String ts = pack("ts");
	}

	public static final Matter matter = new Matter();

	// ------------------------

	public static final class Comment extends Base
	{
		public final String id = pack("id");
		public final String matterId = pack("matter_id");
		public final String userId = pack("user_id");
		public final String star = pack("star");
		public final String text = pack("text");
		public final String time = pack("time");
		public final String pass = pack("pass"); // 是否通过
		public final String ts = pack("ts");
	}

	public static final Comment comment = new Comment();

	// ------------------------

	public static final class Buy extends Base
	{
		public final String userId = pack("user_id");
		public final String matterId = pack("matter_id");
		public final String hourBuy = pack("hour_buy");
		public final String hourGive = pack("hour_give");
//		public final String status = pack("status"); // 状态（0：正常、1：退费）
		public final String ts = pack("ts");
	}

	public static final Buy buy = new Buy();

	// ------------------------

	public static final class Goods extends Base
	{
		public final String id = pack("id");
		public final String name = pack("name");
		public final String cover = pack("cover");
		public final String score = pack("score");
		public final String buyCount = pack("buy_count"); // 销量
		public final String yuCount = pack("yu_count"); // 余量
		public final String sort = pack("sort"); // 排序
		public final String invalid = pack("invalid"); // 是否无效
		public final String ts = pack("ts");
	}

	public static final Goods goods = new Goods();

	// ------------------------

	public static final class Task_Group extends Base
	{
		public final String id = pack("id");
		public final String name = pack("name");
		public final String important = pack("important");
		public final String difficulty = pack("difficulty");
		public final String cover = pack("cover");
		public final String sort = pack("sort");
		public final String ts = pack("ts");
	}

	public static final Task_Group taskGroup = new Task_Group();

	// ------------------------

	public static final class Teach extends Base
	{
		public final String teacherId = pack("teacher_id");
		public final String year = pack("year");
		public final String month = pack("month");
		public final String day = pack("day");
		public final String block = pack("block");
		public final String flag = pack("flag");
		public final String studentId = pack("student_id");
		public final String ts = pack("ts");
	}

	public static final Teach teach = new Teach();

	// ------------------------

	public static final class Order extends Base
	{
		@Override
		public String toString()
		{
			return "_" + super.toString();
		}

		public final String id = pack("id");
		public final String userId = pack("user_id");
		public final String goodsId = pack("goods_id");
		public final String count = pack("count");
		public final String score = pack("score");
		public final String time = pack("time");
		public final String state = pack("state");
		public final String name = pack("name");
		public final String phone = pack("phone");
		public final String address = pack("address");
		public final String ts = pack("ts");
	}

	public static final Order order = new Order();

	// ------------------------

	public static final class Event extends Base
	{
		@Override
		public String toString()
		{
			return "zz_" + super.toString();
		}

//		public final String id = pack("id");
//		public final String userId = pack("user_id");
		public final String action = pack("action");
		public final String target = pack("target");
		public final String des = pack("des");
//		public final String param1 = pack("param1");
//		public final String param2 = pack("param2");
//		public final String param3 = pack("param3");
//		public final String param4 = pack("param4");
//		public final String param5 = pack("param5");
//		public final String param6 = pack("param6");
//		public final String param7 = pack("param7");
//		public final String param8 = pack("param8");
//		public final String param9 = pack("param9");
		public final String time = pack("time");
		public final String ts = pack("ts");
	}

	public static final Event event = new Event();

}