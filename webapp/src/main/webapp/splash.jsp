<%@ page contentType="text/html;charset=UTF-8" language="java" 
%><%@ page import="com.google.appengine.api.users.User" 
%><%@ page import="com.google.appengine.api.users.UserService" 
%><%@ page import="com.google.appengine.api.users.UserServiceFactory" 
%><%
    String loginUrl = "";

	UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
    	response.sendRedirect("index.jsp");
    } else {
    	loginUrl = userService.createLoginURL(request.getRequestURI());
%><!DOCTYPE html>
<html>
    <head>
        <title>flophase</title>
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
        <link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css" rel="stylesheet">
        <style type="text/css">
			@media (min-width: 980px) {
				body {
			        padding-top: 60px;
			    }
		    }
		    .footer {
			    background: linear-gradient(to bottom, #f5f5f5 0%,#ffffff 100%);
			    border-top: 1px solid #E5E5E5;
			    margin-top: 70px;
			    padding: 30px 0;
			    text-align: center;
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
       			<a class="btn btn-primary" href="<%=loginUrl %>">Sign in with Google</a>
        	</div>
        </div>
        <footer class="footer">&copy; 2013 David Sewell</footer>
    </body>
</html>
<%
   }
%>