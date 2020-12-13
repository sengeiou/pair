package com.lys.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.utils.TextUtils;

@WebServlet("/ip")
public class ip extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	public ip()
	{
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String xForwardedFor = request.getHeader("x-forwarded-for");
		if (!TextUtils.isEmpty(xForwardedFor))
			out.print(xForwardedFor);
		else
			out.print(request.getRemoteHost());
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}

}
