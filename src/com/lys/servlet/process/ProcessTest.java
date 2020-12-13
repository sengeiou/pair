package com.lys.servlet.process;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.protobuf.SRequest_Test;
import com.lys.protobuf.SResponse_Test;

public class ProcessTest extends BaseProcess
{
	public static void Test(HttpServletRequest request, String data, HttpServletResponse response) throws Exception
	{
		SRequest_Test requestData = SRequest_Test.load(data);
		SResponse_Test responseData = new SResponse_Test();

//		RongHelper.sendPrivateMessage(new TransMessage(TransEvt_FriendChange, null), defaultMst, "47Q6R17306000310");
//		for (int i = 0; i < 1; i++)
//		{
//			RongHelper.sendSystemMessage(new TxtMessage("平方向", null), "KWQNU18912101375");
//		}
//		RongHelper.sendSystemMessage(new ImgMessage(null, null, defaultHead), "KWQNU18912101375");

		success(response, responseData.saveToStr());
	}
}
