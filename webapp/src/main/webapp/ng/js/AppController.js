define([
], function () {
  "use strict";

  var appController = ["$scope", "$http", "$log", "$filter", "$locale", 
  function ($scope, $http, $log, $filter, $locale) {
  	
  	/**
  	 * Months of the year. 
  	 * 
  	 * TODO: Convert into locale specific values.
  	 */
  	var MONTHS = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
  		
  	/**
  	 * List of accounts. Loaded from the back end.
  	 */
		$scope.accounts = [];
		
		/**
		 * List of transactions. Loaded from the back end.
		 */
		$scope.transactions = [];
		
		/**
		 * Label for the button to load the previous month into the scope.
		 */
		$scope.loadEarlierLabel = "Load Earlier";
		
		/**
		 * Label for the button to load the next month into the scope.
		 */
		$scope.loadLaterLabel = "Load Later";
		
		/**
		 * Filter for only historic transactions.
		 */
		$scope.historic = function(item) {
			return item.details.date <= $scope.currDate;
		};
		
		/**
		 * Filter for only upcoming transactions.
		 */
		$scope.upcoming = function(item) {
			return item.details.date > $scope.currDate;
		};
  	
		/**
		 * Formats a number into currency format. If v is a string, function will try to 
		 * strip formatting before reformatting it.
		 */
		$scope.format = function(v) {
			if (typeof v == "string") {
				v = $scope.unformat(v); //in case the user add their own $
			}
			
			var f = $filter('currency')(v);
			
			// if the filter returned a formatted value
			if (f) {
				return f;
			}
			
			//return a formatted zero
  		return $filter('currency')(0);
  	};
  	
  	/**
  	 * Strips formatting from the given string and returns the number left over.
  	 */
  	$scope.unformat = function(v) {
  		if (v) {
  	  	var formats = $locale.NUMBER_FORMATS;
  	  	
  	  	// strip the currency symbol (e.g. $) and the separator (e.g. ,)
  			var n = new Number(v.replace(formats.CURRENCY_SYM, "", "g").replace(formats.GROUP_SEP, "", "g"));
  			
  			//return the value of the number
  			return n.valueOf();
  		}
  		
  		return 0;
  	};
		
  	/**
  	 * Function to call the back end when an account changes.
  	 */
		$scope.handleAccountChange = function(account) {
			$log.log("Updating account");
			
			var data = {
					key: account.key,
					name: account.name,
					balance: account.balance,
					neg: account.negativeThreshold,
					pos: account.positiveThreshold
			};
			
			$http.get("/account/edit", {params: data})
			.success(function(data, status, headers, config) {
				$log.log("Account updated");
				$log.log(data.content);
			})
			.error(function(data, status, headers, config) {
				$log.error("Error updating account");
				$log.error(status);
			});
		};
		
		$scope.handleTransactionChange = function(xaction) {
			$log.log("Updating transaction");
			
			var data = {
					key: xaction.details.key,
					name: xaction.details.name,
					date: xaction.details.date
			};
			
			$http.get("/xaction/edit", {params: data})
			.success(function(data, status, headers, config) {
				$log.log("Transaction updated");
				$log.log(data.content);
			})
			.error(function(data, status, headers, config) {
				$log.error("Error updating transaction");
				$log.error(status);
			});
		};
		
		$scope.handleEntryChange = function(xaction, entry) {
			$log.log("Updating transaction");
			
			var data = {
					account: entry.account,
					xaction: xaction.details.key,
					amount: entry.amount
			};
			
			$http.get("/entry/edit", {params: data})
			.success(function(data, status, headers, config) {
				$log.log("Entry updated");
				$log.log(data.content);
			})
			.error(function(data, status, headers, config) {
				$log.error("Error updating entry");
				$log.error(status);
			});
		};
		
		var updateDateScope = function(month) {
			var updateFlag = false;
			
			if (!$scope.startMonth || $scope.startMonth > month) {
				$scope.startMonth = month;
				updateFlag = true;
			}
			if (!$scope.endMonth || $scope.endMonth < month) {
				$scope.endMonth = month;
				updateFlag = true;
			}
			
			if (updateFlag) {
				$log.log("Updating date scope: "+$scope.startMonth+" <=> "+$scope.endMonth);
				
				//update the load earlier and later buttons
				$scope.prevMonth = 
					new Date(
							parseInt($scope.startMonth.substr(0,4), 10), 
							parseInt($scope.startMonth.substr(5,2), 10)-2,
							1
							);
				$scope.nextMonth = 
					new Date(
							parseInt($scope.endMonth.substr(0,4), 10), 
							parseInt($scope.endMonth.substr(5,2), 10),
							1
							);
				
				$log.log("Previous month: "+$scope.prevMonth);
				$log.log("Next month:     "+$scope.nextMonth);
				
				if ($scope.prevMonth.getFullYear() < $scope.nextMonth.getFullYear()) {
					$scope.loadEarlierLabel = MONTHS[$scope.prevMonth.getMonth()] + " " + $scope.prevMonth.getFullYear();
					$scope.loadLaterLabel = MONTHS[$scope.nextMonth.getMonth()] + " " + $scope.nextMonth.getFullYear();
				}
				else {
					$scope.loadEarlierLabel = MONTHS[$scope.prevMonth.getMonth()];
					$scope.loadLaterLabel = MONTHS[$scope.nextMonth.getMonth()];
				}
			}
		};
		
		$scope.update = function() {
			var historic = [];
			var upcoming = [];
			var balances;
			
			var i, j;
			
			$log.log('Updating balances');
			
			//build the lists of transactions
			for (i=0; i < $scope.transactions.length; i++) {
				var xaction = $scope.transactions[i];
				var xactionMonth = xaction.details.date.substr(0,7);
				
				//if transaction is out of visible scope, remove it from the scope
				if (xactionMonth < $scope.startMonth || xactionMonth > $scope.endMonth) {
					$log.log("Removing transaction out of scope: " + xaction.details.date);
					
					$scope.transactions.splice(i, 1);
					i--;
				} else {
					if ($scope.historic(xaction)) {
						historic.push(xaction);
					} else {
						upcoming.push(xaction);
					}
				}
			}
		
			//sort the transactions
			historic.sort(function(a, b) {return -1*compareTransactions(a,b);});
			upcoming.sort(compareTransactions);
			
			//build the initial balance
			balances = getAccountBalances();
		
			for (i in historic) {
				var entries = historic[i].entries;
				for (j in $scope.accounts) {
					var accountId = $scope.accounts[j].key;
					if (angular.isDefined(entries[accountId])) {
						balances[accountId] -= parseFloat(entries[accountId].amount);
					}
					historic[i].balances[accountId] = balances[accountId]; 
				}
			}
		
			//reset the balances
			balances = getAccountBalances();
		
			for (i in upcoming) {
				var entries = upcoming[i].entries;
				for (j in $scope.accounts) {
					var accountId = $scope.accounts[j].key;
					if (angular.isDefined(entries[accountId])) {
						balances[accountId] += parseFloat(entries[accountId].amount);
					}
					upcoming[i].balances[accountId] = balances[accountId]; 
				}
			}
		};
		
		$scope.updateMenus = function(params) {
			var i;
			if (params) {
				if (params.accounts) {
					var disableDeleteAccount = true;
					for (i in $scope.accounts) {
						if ($scope.accounts[i].selected) {
							disableDeleteAccount = false;
							break;
						}
					}
					
					if (disableDeleteAccount) {
						$('#deleteAccountMenu').addClass('disabled');
					} else {
						$('#deleteAccountMenu').removeClass('disabled');
					}
				}
				
				if (params.transactions) {
					var selectedTransactionCount = 0;
					
					for (i in $scope.transactions) {
						if ($scope.transactions[i].selected) {
							selectedTransactionCount++;
							
							if (selectedTransactionCount > 1) {
								break;
							}
						}
					}
					
					if (selectedTransactionCount == 0) {
						$('#deleteTransactionMenu').addClass('disabled');
						$('#copyTransactionMenu').addClass('disabled');
					} else if (selectedTransactionCount == 1) {
						$('#deleteTransactionMenu').removeClass('disabled');
						$('#copyTransactionMenu').removeClass('disabled');
					} else if (selectedTransactionCount > 1) {
						$('#deleteTransactionMenu').removeClass('disabled');
						$('#copyTransactionMenu').addClass('disabled');
					}
				}
			}
		};
		
		
		$scope.openAddTransactionDialog = function() {
			$('#addTransactionDialog').dialog( 'open' );
		};
		
		
		$scope.openCopyTransactionDialog = function() {
			var i;
			for (i in $scope.transactions) {
				if ($scope.transactions[i].selected) {
					resetCopyTransaction($scope.transactions[i]);
					
					break;
				}
			}
			
			$('#copyTransactionDialog').dialog( 'open' );
		};
		
		
		var copyTransaction = function() {
			var i;
			
			$log.log("Attempting to copy a transaction");
			$log.log($scope.copyTransaction);
			
			var data = {
					key: $scope.copyTransaction.key,
					name: $scope.copyTransaction.name,
					date: $scope.copyTransaction.date
			};
			
			$http.get("/xaction/copy", {params: data})
			.success(function(data, status, headers, config) {
				if (data.result = 1) {
					$log.log("Transaction copied");
					
					var transaction = data.content;
					
					populateEntries(transaction);
					
					handleTransaction(transaction);
					
					$scope.update();
					
					resetNewTransaction();
					
					$('#copyTransactionDialog').dialog( 'close' );
				} else {
					$log.error("Error copying transaction");
					for (i in data.messages) {
						$log.error(messages[i]);
					}
					
					if (data.messages.length > 0) {
						$scope.copyTransaction.messages = data.messages;
					} else {
						$scope.copyTransaction.messages = ["An unknown error occured. Please try again."];
					}
				}
			})
			.error(function(data, status, headers, config) {
				$log.error("Error adding transaction");
				$log.error(status);
			});
		};
		
		
		var addTransaction = function() {
			var i;
			
			$log.log("Attempting to add a new transaction");
			$log.log($scope.newTransaction);
			
			var data = {
					name: $scope.newTransaction.name,
					date: $scope.newTransaction.date
			};
			
			$http.get("/xaction/add", {params: data})
			.success(function(data, status, headers, config) {
				if (data.result = 1) {
					$log.log("Transaction added");
					
					var transaction = data.content;
					
					populateEntries(transaction);
					
					handleTransaction(transaction);
					
					$scope.update();
					
					resetNewTransaction();
					
					$('#addTransactionDialog').dialog( 'close' );
				} else {
					$log.error("Error adding transaction");
					for (i in data.messages) {
						$log.error(messages[i]);
					}
					
					if (data.messages.length > 0) {
						$scope.newTransaction.messages = data.messages;
					} else {
						$scope.newTransaction.messages = ["An unknown error occured. Please try again."];
					}
				}
			})
			.error(function(data, status, headers, config) {
				$log.error("Error adding transaction");
				$log.error(status);
			});
		};
		
		
		$scope.deleteTransactions = function() {
			var i;
			for (i in $scope.transactions) {
				if ($scope.transactions[i].selected) {
					$log.log("Deleting transaction: " + $scope.transactions[i].details.name);
					
					deleteTransaction($scope.transactions[i]);
				}
			}
		};
		
		var deleteTransaction  = function(transaction) {
			
			var data = {
					key: transaction.details.key
			};
			
			$http.get("/xaction/delete", {params: data})
			.success(function(data, status, headers, config) {
				if (data.result = 1) {
					$log.log("Transaction deleted");
					
					clearTransaction(transaction);
				} else {
					$log.error("Error deleting transaction");
					for (i in data.messages) {
						$log.error(messages[i]);
					}
				}
			})
			.error(function(data, status, headers, config) {
				$log.error("Error deleting transaction");
				$log.error(status);
			});
		};
		
		var clearTransaction = function(transaction) {
			var i;
			for (i in $scope.transactions) {
				if ($scope.transactions[i].details.key == transaction.details.key) {
					$scope.transactions.splice(i, 1); //remove the transaction
					break;
				}
			}
		};
		
		
		$scope.openAddAccountDialog = function() {
			$('#addAccountDialog').dialog( 'open' );
		};
		
		
		$scope.deleteAccounts = function() {
			var i;
			for (i in $scope.accounts) {
				if ($scope.accounts[i].selected) {
					$log.log("Deleting account: " + $scope.accounts[i].name);
					
					deleteAccount($scope.accounts[i]);
				}
			}
		};
		
		var addAccount = function() {
			var i;
			
			$log.log("Attempting to add a new account");
			$log.log($scope.newAccount);
			
			var data = {
					name: $scope.newAccount.name,
					balance: $scope.newAccount.balance
			};
			
			$http.get("/account/add", {params: data})
			.success(function(data, status, headers, config) {
				if (data.result = 1) {
					$log.log("Account added");
					
					handleAccounts([data.content]);
					
					populateAllEntries($scope.transactions);
					
					$scope.update();
					
					resetNewAccount();
					
					$('#addAccountDialog').dialog( 'close' );
				} else {
					$log.error("Error adding account");
					for (i in data.messages) {
						$log.error(messages[i]);
					}
					
					if (data.messages.length > 0) {
						$scope.newAccount.messages = data.messages;
					} else {
						$scope.newAccount.messages = ["An unknown error occured. Please try again."];
					}
				}
			})
			.error(function(data, status, headers, config) {
				$log.error("Error adding account");
				$log.error(status);
			});
		};
		
		var deleteAccount  = function(account) {
			
			var data = {
					key: account.key
			};
			
			$http.get("/account/delete", {params: data})
			.success(function(data, status, headers, config) {
				if (data.result = 1) {
					$log.log("Account deleted");
					
					clearAccount(account);
				} else {
					$log.error("Error deleting account");
					for (i in data.messages) {
						$log.error(messages[i]);
					}
				}
			})
			.error(function(data, status, headers, config) {
				$log.error("Error deleting account");
				$log.error(status);
			});
		};
		
		var clearAccount = function(account) {
			var i, j;
			for (i in $scope.accounts) {
				if ($scope.accounts[i].key == account.key) {
					$scope.accounts.splice(i, 1); //remove the account
					break;
				}
			}
			
			for (j in $scope.transactions) {
				for (i in $scope.transactions[j].entries) {
					if (i == account.key) {
						delete $scope.transactions[j].entries[i];
						break;
					}
				}
			}
		};
		
		var getAccountBalances = function() {
			var balances = {};
			for (var i in $scope.accounts) {
				balances[$scope.accounts[i].key] = parseFloat($scope.accounts[i].balance);
			}
			return balances;
		};
		
		var compareTransactions = function(a, b) {
			if (a.details.date < b.details.date) {
				return -1;		
			} else if (a.details.date > b.details.date) {
				return 1;
			} else if (a.details.key < b.details.key) {
				return -1;
			} else if (a.details.key > b.details.key) {
				return 1;
			}
			return 0;
		};
		
		var populateAllEntries = function(transactions) {
			var i;
			var entries;
			
			$log.log("Populating missing entries in new transactions");
			
			for (i in transactions) {
				populateEntries(transactions[i]);
			}
		}; 
		
		
		var populateEntries = function(transaction) {
			var j;
			var entries = transaction.entries;
			
			for (j in $scope.accounts) {
				if (!angular.isDefined(entries[$scope.accounts[j].key])) {
					//create an empty entry
					entries[$scope.accounts[j].key] = {
						account: $scope.accounts[j].key, 
						amount:0,
						formattedAmount:$scope.format(0)
					};
				} else {
					entries[$scope.accounts[j].key].formattedAmount = $scope.format(entries[$scope.accounts[j].key].amount);
				}
			}
		};
		
		
		var handleAccounts = function(accounts) {
			var i;
			
			$log.log("Adding "+accounts.length+" accounts to scope");
			
			for (i in accounts) {
				//add the formatted balance
				accounts[i].formattedBalance = $scope.format(accounts[i].balance);
				
				$scope.accounts.push(accounts[i]);
			}
		};
		
		
		var loadAccounts = function() {
			$log.log('Requesting all accounts');
			
			var get = $http.get('/account/q');
			get.success(function(data, status, headers, config) {
				handleAccounts(data.content);
			});
			get.error(function(data, status, headers, config) {
				$log.error("Error loading accounts");
				$log.error(status);
			});
			
			return get;
		}; 
		
		
		var handleTransactions = function(transactions, month) {
			$log.log("Adding "+transactions.length+" transactions to scope for "+month);
			
			//populate any missing entries
			populateAllEntries(transactions);
			
			var i;
			for (i in transactions) {
				handleTransaction(transactions[i]);
			}
			
			updateDateScope(month);
		
			//update the scope balances
			$scope.update();
		};
		
		var handleTransaction = function(transaction) {
			transaction.balances = {}; //create an empty balance map
			$scope.transactions.push(transaction);
		};
		
		$scope.loadXactions = function(month) { 
			loadTransactions(formatDate(month).substr(0,7));
		};		
		
		var loadTransactions = function(month) {
			$log.log('Requesting transactions for '+month);
			
			var get = $http.get('/xaction/q', {params: { month: month }});
			get.success(function(month) { return function(data, status, headers, config) {
				handleTransactions(data.content, month);
			};}(month));
			get.error(function(month) { return function(data, status, headers, config) {
				$log.error("Error loading transactions for "+month);
				$log.error(status);
			}; }(month));
		};
		
		
		var formatDate = function(date) {
			var day = date.getDate();
      if (day < 10) {
      	day = "0"+day;
      }
			var month = date.getMonth() + 1;
      if (month < 10) {
      	month = "0"+month;
      }
			return date.getFullYear() + "-" + month + "-" + day;
		};
		
		var resetNewAccount = function() {
			if (!$scope.newAccount) {
				$scope.newAccount = {};
			}
			
			$scope.newAccount.name = "";
			$scope.newAccount.balance = 0;
			$scope.newAccount.formattedBalance = $scope.format(0);
		};
		
		
		var resetCopyTransaction = function(transaction) {
			if (!$scope.copyTransaction) {
				$scope.copyTransaction = {};
			}
			
			$scope.copyTransaction.key = transaction.details.key;
			$scope.copyTransaction.name = transaction.details.name;
			$scope.copyTransaction.date = transaction.details.date;
		};
		
		
		var resetNewTransaction = function() {
			if (!$scope.newTransaction) {
				$scope.newTransaction = {};
			}
			
			$scope.newTransaction.name = "";
			$scope.newTransaction.date = "";
		};
		
		
		var init = function() {
			$scope.currDate = formatDate(new Date());
			
			var currMonth = $scope.currDate.substr(0,7);
			
			loadAccounts()
			.success(function(data, status, headers, config) {
				loadTransactions(currMonth);
			});
			
			resetNewAccount();
			
			$( "#addAccountDialog" ).dialog({
	      buttons: {
	        "Add": addAccount,
	        "Cancel": function() {
	          $( this ).dialog( "close" );
	        }
	      },
	      close: function() {
	      	resetNewAccount();
	      }
	    });
			
			resetNewTransaction();
			
			$( "#addTransactionDialog" ).dialog({
	      buttons: {
	        "Add": addTransaction,
	        "Cancel": function() {
	          $( this ).dialog( "close" );
	        }
	      },
	      close: function() {
	      	resetNewTransaction();
	      }
	    });
			
			$( "#copyTransactionDialog" ).dialog({
	      buttons: {
	        "Copy": copyTransaction,
	        "Cancel": function() {
	          $( this ).dialog( "close" );
	        }
	      }
	    });
		};
		
		init();
	}];

  return appController;
});