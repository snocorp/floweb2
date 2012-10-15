<%@ page contentType="text/html;charset=UTF-8" language="java" 
%><%@ page import="com.google.appengine.api.users.User" 
%><%@ page import="com.google.appengine.api.users.UserService" 
%><%@ page import="com.google.appengine.api.users.UserServiceFactory" 
%><%
    String logoutUrl = "";

	UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null) {
    	response.sendRedirect("index.jsp");
    } else {
    	logoutUrl = userService.createLogoutURL(request.getRequestURI());
%><!DOCTYPE html>
<html>
    <head>
        <title>flophase</title>
        <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" href="jquery/redmond/jquery-ui-1.8.17.custom.css">
        <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.7/dijit/themes/claro/claro.css" />        
        <link rel="stylesheet" href="css/floweb.css">
    </head>
    <body class="claro">
        <div class="navbar navbar-fixed-top">
   			<div class="navbar-inner">
   				<div class="container">
	   				<span class="brand">flophase [beta]</span>
	   				<!-- <ul class="nav" style="display: none;">
	   					<li class="active"><a href="#" onclick="app.notify('table mode'); return false;">table</a></li>
	   					<li><a href="#" onclick="app.notify('calendar mode'); return false;">calendar</a></li>
	   				</ul> -->
	   				<ul class="nav pull-right">
	   					<li id="userIdentifier" class="navbar-text"><%=  user.getEmail() %></li>
	   					<li><a id="logoutLink" href="<%=logoutUrl %>">logout</a></li>
	   				</ul>
   				</div>
   			</div>
        </div>
        <div class="container">
        	<header class="jumbotron subhead">
	        	<div class="subnav">
	        		<div class="btn-toolbar">
			        	<div class="btn-group">
			        		<a id="addAccount"
			        		    href="#"
			        			class="btn disabled">add account</a>
			        		<a id="addTransaction"
			        		    href="#"
			        			class="btn disabled">add transaction</a>
			        	</div>
			        	<div class="btn-group" id="dateButtonGroup" style="display:none;">
			        		<a id="loadEarlier"
			        			href="#"
			        			class="btn"></a>
			        		<a id="loadUpcoming"
			        			href="#" 
			        			class="btn"></a>
			        	</div>
		        	</div>
	        	</div>
        	</header>
        	<section id="gridSection">
	            <div class="span12">
	            	<div id="gridContainer"></div>
	            </div>
            </section>
        </div>
        <div id="notification" class="flo-notification" style="display: none;">
            <span id="notificationMsg"></span>
            <a class="jm-close" href="#"></a>
        </div>
        <div id="accountEditor" class="flo-editor flo-off-screen">
            <input class="accountname" type="text" id="accountName" data-dojo-type="dijit.form.TextBox"/><br/>
            <button data-dojo-type="dijit.form.Button" type="button" id="accountDelete">Delete</button>
        </div>
        <div id="xactionEditor" class="flo-editor flo-off-screen">
            <input class="xactionname" type="text" id="xactionName" data-dojo-type="dijit.form.TextBox"/><br/>
            <button data-dojo-type="dijit.form.Button" type="button" id="xactionDelete" class="deletebutton">Delete</button>
        </div>
        <div id="addAccountDialog" class="flo-off-screen">
            <label for="newAccountName">Name:</label>
            <input
                class="accountname"
                type="text"
                id="newAccountName"
                name="newAccountName"
                data-dojo-type="dijit.form.TextBox"/><br/>
            <label for="newAccountBalance">Balance:</label>
            <input
                class="accountbalance"
                type="text"
                id="newAccountBalance"
                name="newAccountBalance"
                data-dojo-type="dijit.form.CurrencyTextBox"
    			data-dojo-props="required:true, constraints:{fractional:true}, currency:'USD'"
                value="0"/><br/>
            <div id="addAccountError" class="flo-dialog-error" style="display:none;"></div>
            <div class="flo-dialog-buttonbar">
                <button id="addAccountOk" type="button" data-dojo-type="dijit.form.Button">OK</button>
                <button id="addAccountCancel" type="button" data-dojo-type="dijit.form.Button">Cancel</button>
            </div>
        </div>
        <div id="addTransactionDialog" class="flo-off-screen">
            <label for="newTransactionName">Description:</label>
            <input class="xactionname" type="text" id="newTransactionName" name="name"/><br/>
            <label for="newTransactionDate">Date:</label><br/>
            <div id="newTransactionCalendar"></div>
            <input type="hidden" id="newTransactionDate" name="date" />
            <div id="addTransactionError" class="flo-dialog-error" style="display:none;"></div>
            <div class="flo-dialog-buttonbar">
                <button id="addTransactionOk" type="button" data-dojo-type="dijit.form.Button">OK</button>
                <button id="addTransactionCancel" type="button" data-dojo-type="dijit.form.Button">Cancel</button>
            </div>
        </div>
        
        <!-- Placed at the end of the document so the page loads faster -->
    	<script src="jquery/jquery-1.7.1.min.js" type="text/javascript"></script>
    	<script src="jquery/jquery-ui-1.8.17.custom.min.js" type="text/javascript"></script>
    	<script src="jquery/jquery.jmNotify.js" type="text/javascript"></script>
		
    	
    	<!-- Configure Dojo -->
        <script>
            var dojoConfig = (function(){
                
                return {
                    tlmSiblingOfDojo: false,
                    async: true,
                    packages: [
					{ 
						name: "dojo", 
						location: "//ajax.googleapis.com/ajax/libs/dojo/1.7.2/dojo" 
					}, { 
						name: "dijit", 
						location: "//ajax.googleapis.com/ajax/libs/dojo/1.7.2/dijit" 
					}, { 
						name: "dijit/form", 
						location: "//ajax.googleapis.com/ajax/libs/dojo/1.7.2/dijit/form" 
					}, {
                        name: "net/sf/flophase",
                        location: "/js/net/sf/flophase"
                    }, {
                        name: "net/sf/flophase/data",
                        location: "/js/net/sf/flophase/data"
                    }, {
                        name: "net/sf/flophase/model",
                        location: "/js/net/sf/flophase/model"
                    }, {
                        name: "net/sf/flophase/ui",
                        location: "/js/net/sf/flophase/ui"
                    }]
                };
            })();
        </script>

        <script
            src="https://ajax.googleapis.com/ajax/libs/dojo/1.7.2/dojo/dojo.js"
            type="text/javascript"></script>
        <script
            src="js/floweb.config.js"
            type="text/javascript"></script>
        <script type="text/javascript">

	        //global reference to the application
	        var app;
	
	        /**
	         * Initialization script (Don't access DOM until ready)
	         */
	        require(["net/sf/flophase/App", "dijit/registry", "dojo/parser", "dojo/domReady!"], function(application, registry, parser) {
	            
	            parser.parse();
	            
	            app = application;
	            app.init();
	        });
	
	    </script>
    </body>
</html>
<%
   }
%>
