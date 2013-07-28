<%@ page contentType="text/html;charset=UTF-8" language="java" 
%><%@ page import="com.google.appengine.api.users.User" 
%><%@ page import="com.google.appengine.api.users.UserService" 
%><%@ page import="com.google.appengine.api.users.UserServiceFactory" 
%><%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
		response.sendRedirect("std/app.jsp");
    } else {
%><jsp:include page="splash.jsp"></jsp:include><%
    }
%>