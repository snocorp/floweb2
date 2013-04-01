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
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
        <link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css" rel="stylesheet">
        <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/dojo/1.7/dijit/themes/claro/claro.css" type="text/css" />        
        <link rel="stylesheet" href="css/floweb.css" type="text/css">
    </head>
    <body class="claro">
        <div class="navbar navbar-fixed-top">
   			<div class="navbar-inner">
  				<span class="brand">flophase [beta]</span>
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
   				<ul class="nav pull-right">
   					<li id="userIdentifier" class="navbar-text"><%=  user.getEmail() %></li>
   					<li><a id="logoutLink" href="<%=logoutUrl %>">logout</a></li>
   				</ul>
   			</div>
        </div>
       	<section id="gridSection">
            <div id="gridContainer"></div>
        </section>
        <footer class="footer">&copy; 2013 David Sewell</footer>
        <div id="notification" class="flo-notification" style="display: none;">
            <span id="notificationMsg"></span>
            <a class="jm-close" href="#"></a>
        </div>
        <div id="accountEditor" class="flo-editor flo-off-screen">
            <input class="accountname" type="text" id="accountName" data-dojo-type="dijit.form.TextBox"/><br/>
            <div id="accountEditorAdvanced" class="flo-advanced-options">
            	<label title="Negative Threshold">Negative</label>
            	<input type="text" id="negativeThreshold" data-dojo-type="dijit.form.CurrencyTextBox"/><br/>
            	<label title="Positive Threshold">Positive</label>
            	<input type="text" id="positiveThreshold" data-dojo-type="dijit.form.CurrencyTextBox"/><br/>
            </div>
            <button type="button" id="accountMore" class="btn">Options</button>
            <button type="button" id="accountSave" class="btn">Save</button>
            <button type="button" id="accountDelete" class="btn">Delete</button>
        </div>
        <div id="xactionEditor" class="flo-editor flo-off-screen">
            <input class="xactionname" type="text" id="xactionName" data-dojo-type="dijit.form.TextBox"/><br/>
            <button type="button" id="xactionCopy" class="btn">Copy</button>
            <button type="button" id="xactionDelete" class="btn">Delete</button>
        </div>
        <div id="addAccountDialog" class="flo-off-screen modal">
            <div class="modal-header">
				<h3>Add Account</h3>
			</div>
			<div class="modal-body">
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
            </div>
            <div class="modal-footer">
                <button id="addAccountOk" type="button" class="btn">OK</button>
                <button id="addAccountCancel" type="button" class="btn">Cancel</button>
            </div>
        </div>
        <div id="addTransactionDialog" class="flo-off-screen modal">
            <div class="modal-header">
				<h3>Add Transaction</h3>
			</div>
			<div class="modal-body">
	            <label for="newTransactionName">Description:</label>
	            <input 
	            	class="xactionname" 
	            	type="text" 
	            	id="newTransactionName" 
	            	name="name"
	                data-dojo-type="dijit.form.TextBox"/>
	            <label for="newTransactionDate">Date:</label>
	            <div id="newTransactionCalendar"></div>
	            <input type="hidden" id="newTransactionDate" name="date" />
	            <div id="addTransactionError" class="flo-dialog-error" style="display:none;"></div>
            </div>
            <div class="modal-footer">
                <button id="addTransactionOk" type="button" class="btn">OK</button>
                <button id="addTransactionCancel" type="button" class="btn">Cancel</button>
            </div>
        </div>
        <div id="copyTransactionDialog" class="flo-off-screen modal" tabindex="-1">
            <div class="modal-header">
				<h3>Copy Transaction</h3>
			</div>
			<div class="modal-body">
	        	<input type="hidden" id="copyTransactionKey" name="key" />
	            <label for="copyTransactionName">Description:</label>
	            <input 
	            	type="text" 
	            	id="copyTransactionName" 
	            	name="name"
	                data-dojo-type="dijit.form.TextBox"/>
	            <label for="copyTransactionDate">Date:</label>
	            <div id="copyTransactionCalendar"></div>
	            <input type="hidden" id="copyTransactionDate" name="date" />
	            <div id="copyTransactionError" class="flo-dialog-error" style="display:none;"></div>
            </div>
            <div class="modal-footer">
                <button id="copyTransactionOk" type="button" class="btn">OK</button>
                <button id="copyTransactionCancel" type="button" class="btn">Cancel</button>
            </div>
        </div>
        
        <!-- Placed at the end of the document so the page loads faster -->
    	<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
		<script type="text/javascript" src="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js"></script>
    	<script type="text/javascript" src="jquery/jquery.jmNotify.js"></script>
		
    	
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

        <!-- <script
        	src="http://download.dojotoolkit.org/release-1.7.2/dojo.js.uncompressed.js"
            type="text/javascript"></script> -->
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
