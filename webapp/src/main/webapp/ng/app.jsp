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
<html ng-controller="AppController">
<head>
  <meta charset="utf-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

  <title>flophase</title>
  <meta name="description" content="Cash flow tracking application" />
  <meta name="author" content="David Sewell" />
  
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/redmond/jquery-ui.css">
  <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.0/css/bootstrap.min.css">
  <link rel="stylesheet" href="css/style.css">
</head>
<body>
	<div class="navbar navbar-inverse navbar-fixed-top">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <span class="navbar-brand">flophase</span>
        </div>
        <div class="collapse navbar-collapse">
        	<ul class="nav navbar-nav">
            	<li><a
					id="loadEarlier"
					href="javascript:void(0);"
					ng-bind="loadEarlierLabel"
					ng-click="loadXactions(prevMonth)"></a></li>
            	<li><a
					id="loadLater"
					href="javascript:void(0);"
					ng-bind="loadLaterLabel"
					ng-click="loadXactions(nextMonth)"></a></li>
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">Accounts <b class="caret"></b></a>
					<ul class="dropdown-menu">
		                <li><a href="#" ng-click="openAddAccountDialog();">Add</a></li>
		                <li id="deleteAccountMenu" class="disabled"><a href="#" ng-click="deleteAccounts();">Delete</a></li>
		            </ul>
				</li>
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">Transactions <b class="caret"></b></a>
					<ul class="dropdown-menu">
		                <li><a href="#" ng-click="openAddTransactionDialog();">Add</a></li>
		                <li id="copyTransactionMenu" class="disabled"><a href="#" ng-click="openCopyTransactionDialog();">Copy</a></li>
		                <li id="deleteTransactionMenu" class="disabled"><a href="#" ng-click="deleteTransactions();">Delete</a></li>
		            </ul>
				</li>
          	</ul>
        </div>
	</div>
	<div class="container">
		<div class="transactions historic">
			<div 
				class="transaction" 
				ng-repeat="transaction in transactions | filter: historic | orderBy: ['+details.date','+details.key']"
				ng-class="transaction.selected ? 'selected' : ''" 
				ng-click="transaction.selected = !transaction.selected; updateMenus({transactions:true});">
				<div class="row">
					<div class="col-sm-3">
						<input 
							class="title form-control" 
							ng-model="transaction.details.name" 
							ng-change="handleTransactionChange(transaction);"/>
					</div>
					<div class="col-sm-3">
						<input 
							class="date form-control" 
							ui-date="{ dateFormat: 'yy-mm-dd' }" 
							ui-date-format="yy-mm-dd"
							ng-model="transaction.details.date" 
							ng-change="update(); handleTransactionChange(transaction);"/>
					</div>
				</div>
				<div class="entries">
					<div class="entry row" ng-repeat="account in accounts">
						<div class="col-sm-2 col-sm-offset-1">
						   <div class="account">{{account.name}}</div>
						</div>
						<div class="col-sm-2">
							<input 
								type="text" 
								class="amount form-control" 
								ng-model="transaction.entries[account.key].formattedAmount" 
								ng-focus="transaction.entries[account.key].formattedAmount = transaction.entries[account.key].amount" 
								ng-blur="transaction.entries[account.key].formattedAmount = format(transaction.entries[account.key].formattedAmount)" 
								ng-change="transaction.entries[account.key].amount = unformat(transaction.entries[account.key].formattedAmount); update(); handleEntryChange(transaction, transaction.entries[account.key]);"/>
						</div>
						<div class="balance col-sm-2">{{transaction.balances[account.key] | currency}}</div>
				   	</div>
				</div>
			</div>
		</div>
	  	<div class="current">
			<div class="details row">
				<span class="title col-sm-3">Current</span>
				<span class="date col-sm-3" ng-bind="currDate"></span>
			</div>
			<div 
				class="account row" 
				ng-repeat="account in accounts" 
				ng-class="account.selected ? 'selected' : ''" 
				ng-click="account.selected = !account.selected; updateMenus({accounts:true});">
				<div class="col-sm-2 col-sm-offset-1">
					<input 
						class="name form-control" 
						required
						ng-model="account.name" 
						ng-change="handleAccountChange(account);"/>
				</div>
				<div class="col-sm-2">
					<input 
						type="text" 
						class="balance form-control" 
						required
						ng-model="account.formattedBalance" 
						ng-focus="account.formattedBalance = account.balance" 
						ng-blur="account.formattedBalance = format(account.formattedBalance)" 
						ng-change="account.balance = unformat(account.formattedBalance); update(); handleAccountChange(account);">
				</div>
			</div>
		</div>
		<div class="transactions upcoming">
			<div 
				class="transaction" 
				ng-repeat="transaction in transactions | filter: upcoming | orderBy: ['+details.date','+details.key']"
				ng-class="transaction.selected ? 'selected' : ''" 
				ng-click="transaction.selected = !transaction.selected; updateMenus({transactions:true});">
				<div class="row">
					<div class="col-sm-3">
						<input 
							class="title form-control" 
							ng-model="transaction.details.name" 
							ng-change="handleTransactionChange(transaction);"/>
					</div>
					<div class="col-sm-3">
						<input 
							class="date form-control" 
							ui-date="{ dateFormat: 'yy-mm-dd' }" 
							ui-date-format="yy-mm-dd"
							ng-model="transaction.details.date" 
							ng-change="update(); handleTransactionChange(transaction);"/>
					</div>
				</div>
				<div class="entries">
					<div class="entry row" ng-repeat="account in accounts">
						<div class="col-sm-2 col-sm-offset-1">
						   <div class="account">{{account.name}}</div>
						</div>
						<div class="col-sm-2">
							<input 
								type="text" 
								class="amount form-control" 
								ng-model="transaction.entries[account.key].formattedAmount" 
								ng-focus="transaction.entries[account.key].formattedAmount = transaction.entries[account.key].amount" 
								ng-blur="transaction.entries[account.key].formattedAmount = format(transaction.entries[account.key].formattedAmount)" 
								ng-change="transaction.entries[account.key].amount = unformat(transaction.entries[account.key].formattedAmount); update(); handleEntryChange(transaction, transaction.entries[account.key]);"/>
						</div>
						<div class="balance col-sm-2">{{transaction.balances[account.key] | currency}}</div>
				   	</div>
				</div>
			</div>
		</div>
	</div>
	<div id="addAccountDialog" class="flo-hidden" tabindex="-1" role="dialog" aria-labelledby="Add Account" aria-hidden="true" title="Add Account">
		<div class="form-group">
      		<label for="newAccountName">Name</label>
      		<input 
      			id="newAccountName" 
      			class="form-control" 
      			required
      			placeholder="Account Name" 
      			ng-model="newAccount.name"/>
      	</div>
       	<div class="form-group">
       		<label for="newAccountBalance">Balance</label>
       		<input 
       			id="newAccountBalance" 
       			class="form-control" 
       			required
       			placeholder="Initial Balance" 
       			ng-model="newAccount.formattedBalance"
				ng-focus="newAccount.formattedBalance = newAccount.balance" 
				ng-blur="newAccount.formattedBalance = format(newAccount.formattedBalance)" 
				ng-change="newAccount.balance = unformat(newAccount.formattedBalance);"/>
       	</div>
      	<div class="alert alert-danger" ng-show="newAccount.messages">
      		<ul>
      			<li ng-repeat="message in newAccount.messages">{{message}}</li>
      		</ul>
      	</div>
  	</div>
	<div id="addTransactionDialog" class="flo-hidden" tabindex="-1" role="dialog" aria-labelledby="Add Transaction" aria-hidden="true" title="Add Transaction">
		<div class="form-group">
      		<label for="newTransactionName">Name</label>
      		<input 
      			id="newTransactionName" 
      			class="form-control" 
      			required
      			placeholder="Transaction Name" 
      			ng-model="newTransaction.name"/>
      	</div>
       	<div class="form-group">
       		<label for="newTransactionBalance">Date</label>
       		<input 
       			id="newTransactionDate" 
       			class="form-control" 
       			required
       			placeholder="Transaction Date"
				ui-date="{ dateFormat: 'yy-mm-dd' }" 
				ui-date-format="yy-mm-dd" 
       			ng-model="newTransaction.date"/>
       	</div>
      	<div class="alert alert-danger" ng-show="newTransaction.messages">
      		<ul>
      			<li ng-repeat="message in newTransaction.messages">{{message}}</li>
      		</ul>
      	</div>
  	</div>
	<div id="copyTransactionDialog" class="flo-hidden" tabindex="-1" role="dialog" aria-labelledby="Copy Transaction" aria-hidden="true" title="Copy Transaction">
		<div class="form-group">
      		<label for="copyTransactionName">Name</label>
      		<input 
      			id="copyTransactionName" 
      			class="form-control" 
      			required
      			placeholder="Transaction Name" 
      			ng-model="copyTransaction.name"/>
      	</div>
       	<div class="form-group">
       		<label for="copyTransactionBalance">Date</label>
       		<input 
       			id="copyTransactionDate" 
       			class="form-control" 
       			required
       			placeholder="Transaction Date"
				ui-date="{ dateFormat: 'yy-mm-dd' }" 
				ui-date-format="yy-mm-dd" 
       			ng-model="copyTransaction.date"/>
       	</div>
      	<div class="alert alert-danger" ng-show="copyTransaction.messages">
      		<ul>
      			<li ng-repeat="message in copyTransaction.messages">{{message}}</li>
      		</ul>
      	</div>
  	</div>
    <script data-main="js/bootstrap" src="//cdnjs.cloudflare.com/ajax/libs/require.js/2.1.8/require.min.js"></script> 
</body>
</html>
<%
   }
%>