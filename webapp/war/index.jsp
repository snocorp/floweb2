<%@ page contentType="text/html;charset=UTF-8" language="java" 
%><%@ page import="com.google.appengine.api.users.User" 
%><%@ page import="com.google.appengine.api.users.UserService" 
%><%@ page import="com.google.appengine.api.users.UserServiceFactory" 
%><%
    String loginUrl = "";

	UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
    	response.sendRedirect("app.jsp");
    } else {
    	loginUrl = userService.createLoginURL(request.getRequestURI());
    }
%><!DOCTYPE html>
<html>
    <head>
        <title>flophase</title>
        <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
        <style type="text/css">
			body {
		        padding-top: 60px;
		    }
		</style>
    </head>
    <body>
        <div class="navbar navbar-fixed-top">
   			<div class="navbar-inner">
   				<div class="container">
	   				<span class="brand">flophase [beta]</span>
	   				<ul class="nav pull-right">
	   					<li><a href="<%=loginUrl %>">login</a></li>
	   				</ul>
   				</div>
   			</div>
        </div>
        <div class="container">
        	<div class="hero-unit">
        		<h1>cash flow</h1>
        		<h2>management system</h2>
        	</div>
        </div>
    </body>
</html>
