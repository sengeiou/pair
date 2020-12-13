package com.lys.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lys.config.Config;
import com.lys.utils.CommonUtils;
import com.lys.utils.LOG;

@WebServlet("/test")
public class test extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	public test()
	{
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		LOG.v("doGet " + this.getClass().getName());
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>test info !</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h2>other info</h2>");
		out.println("getHostIP : " + CommonUtils.getHostIP() + "<br />");
		out.println("getNetIp : " + CommonUtils.getNetIp() + "<br />");
//		out.println("getLocalIp : " + CommonUtils.getLocalIp() + "<br />");
		out.println("isLinux : " + Config.isLinux + "<br />");
		out.println("IP : " + Config.IP + "<br />");
		out.println("URL_ROOT : " + Config.URL_ROOT + "<br />");
		out.println("projectName : " + Config.projectName + "<br />");
		out.println("tomcatDir : " + Config.tomcatDir + "<br />");
		out.println("webappsDir : " + Config.webappsDir + "<br />");
		out.println("ROOTDir : " + Config.ROOTDir + "<br />");
		out.println("rootFileDir : " + Config.rootFileDir + "<br />");
		out.println("selfFileDir : " + Config.selfFileDir + "<br />");
		out.println("fileDir : " + Config.fileDir + "<br />");
//		out.println("logDir : " + Config.logDir + "<br />");
		out.println("<h2>base info</h2>");
		out.println("getContextPath : " + request.getContextPath() + "<br />");
		out.println("getRealPath : " + request.getServletContext().getRealPath("") + "<br />");
		out.println("getResource.getPath : " + test.class.getResource("").getPath() + "<br />");
		out.println("getMethod : " + request.getMethod() + "<br />");
		out.println("getRequestURI : " + request.getRequestURI() + "<br />");
		out.println("getProtocol : " + request.getProtocol() + "<br />");
		out.println("getPathInfo : " + request.getPathInfo() + "<br />");
		out.println("getQueryString : " + request.getQueryString() + "<br />");
		out.println("getRemoteHost : " + request.getRemoteHost() + "<br />");
		out.println("getRemoteAddr : " + request.getRemoteAddr() + "<br />");
		out.println("getRemotePort : " + request.getRemotePort() + "<br />");
		out.println("<h2>getHeaders</h2>");
		Enumeration<String> e = request.getHeaderNames();
		while (e.hasMoreElements())
		{
			String name = e.nextElement();
			String value = request.getHeader(name);
			out.println(name + " = " + value + "<br />");
		}
		out.println("<h2>getParameters</h2>");
		e = request.getParameterNames();
		while (e.hasMoreElements())
		{
			String name = e.nextElement();
			String value = request.getParameter(name);
			out.println(name + " = " + value + "<br />");
		}
		out.println("<h2>getCookies</h2>");
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				Cookie c = cookies[i];
				String name = c.getName();
				String value = c.getValue();
				out.println(name + " = " + value + "<br />");
			}
		}
		out.println("<h2>addCookie</h2>");
		String name = request.getParameter("cookieName");
		if (name != null && name.length() > 0)
		{
			String value = request.getParameter("cookieValue");
			Cookie c = new Cookie(name, value);
			out.println(name + " = " + value + "<br />");
			response.addCookie(c);
		}
		out.println("</body>");
		out.println("</html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}

}
