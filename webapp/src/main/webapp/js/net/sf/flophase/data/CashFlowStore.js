/**
 * CashFlowStore
 *
 * This class is a persistent object that allows the application to access and
 * update the data.
 */
define([
        "dojo/date/stamp",
        "net/sf/flophase/model/CashFlow",
        "net/sf/flophase/data/AccountStore",
        "net/sf/flophase/data/TransactionStore"], 
    function(
		stamp, 
		cashflow, 
		accountStore, 
		xactionStore) {


    /**
     * Sorts the given transaction based on their dates.
     *
     * @param a The first transaction
     * @param b The second transaction
     */
    var _xactionSorter = function(a, b) {
    	if (a.details.date > b.details.date) {
    		return 1;
    	} else if (a.details.date < b.details.date) {
    		return -1;
    	} else if (a.details.key > b.details.key) {
    		return 1;
    	} else if (a.details.key < b.details.key) {
    		return -1;
    	} else {
    		return 0;
    	}
    };

    return {
        /**
         * Loads and stores the cashflow from the server. Gets the data
         * asyncronously by first getting the accounts, then the historic
         * transactions, then the upcoming transactions. Finally it updates the
         * balances.
         *
         * @param options.success The function to invoke upon success. Takes a
         *                        reference to the cashflow as an argument.
         * @param options.error
         */
        getCashFlow: function(options)  {
            var _this = this; //store a reference to this for use in the callback

            var currDate = new Date();
            
            var month = currDate.getMonth() + 1;
            if (month < 10) {
            	month = "0"+month;
            }
            var currMonth = currDate.getFullYear() + "-" + month;
            
            //get the first day in the current month
            var startDate = new Date(currDate.getFullYear(), currDate.getMonth(), 1);

            accountStore.getAccounts({
                success: function(accounts) {
                    _this.handleAccounts(accounts);

                    xactionStore.getTransactions({
                        month: currMonth,
                        success: function(xactions) {
                            _this.handleTransactions(xactions, startDate);

                            _this.updateBalances();

                            options.success(cashflow);
                        },
                        error: options.error
                    });
                },
                error: options.error
            });
        },
        /**
         * Handles the accounts sent from the server. Sets the cashflow's accounts.
         *
         * @param accounts The accounts sent from the server.
         */
        handleAccounts: function(accounts) {
            cashflow.setAccounts(accounts);
        },
        /**
         * Handles transactions from the server. Adds these transactions to
         * the current set of transactions and re-sorts them.
         *
         * @param xactions The transactions from the server.
         * @param date The first day of the month for which transactions were loaded
         */
        handleTransactions: function(xactions, date) {
            var origXactions = cashflow.getTransactions();

            xactions = xactions.concat(origXactions);
            xactions.sort(_xactionSorter);

            cashflow.setTransactions(xactions);
            
            if (!cashflow.getStartMonth() || date < cashflow.getStartMonth()) {
            	cashflow.setStartMonth(date);
            }
            
            if (!cashflow.getEndMonth() || date > cashflow.getEndMonth()) {
				cashflow.setEndMonth(date);
			}
        },
        /**
         * Adds a new account to the cashflow.
         *
         * @param options.name The account name
         * @param options.balance The current balance
         * @param options.success The function to call upon success. Takes the new
         *                        account as a parameter.
         * @param options.error The function to call upon error, takes an array of
         *                      strings
         */
        addAccount: function(options) {
            accountStore.addAccount({
                name: options.name,
                balance: options.balance,
                success: function(account) {
                    var accounts = cashflow.getAccounts();
                    accounts.push(account);
                    cashflow.setAccounts(accounts);

                    options.success(account);
                },
                error: options.error
            });
        },
        /**
         * Deletes an existing account from the cashflow.
         *
         * @param options.key The account key
         * @param options.success The function to call upon success
         * @param options.error The function to call upon error
         */
        deleteAccount: function(options) {
            accountStore.deleteAccount({
                key: options.key,
                success: function() {
                    var i;

                    //remove the account
                    var accounts = cashflow.getAccounts();
                    for (i in accounts) {
                        if (accounts[i].key == options.key) {
                            accounts.splice(i, 1);
                            break;
                        }
                    }
                    cashflow.setAccounts(accounts);

                    //remove any entries for the account
                    var xactions = cashflow.getTransactions();
                    for (i in xactions) {
                        //remove entry if it exists
                        if (xactions[i].entries[options.key]) {
                            delete xactions[i].entries[options.key];
                        }
                    }

                    options.success();
                },
                error: options.error
            });
        },
        /**
         * Edits an existing account. Updates one or both of the name and the
         * balance. If the balance is updated, updates all balances.
         *
         * @param options.key The key of the account
         * @param options.name The new name, optional
         * @param options.balance The new balance, optional
         * @param options.success The function to call upon success
         * @param options.error The function to call upon error, takes an array of
         *                      strings
         */
        editAccount: function(options) {
            var _this = this; //store a reference to this for use in the callback

            var account = cashflow.getAccount(options.key);
            
            if (typeof options.name === "undefined") { 
            	options.name = account.name; 
            }
            if (typeof options.balance === "undefined") {
            	options.balance = account.balance; 
            }

            accountStore.editAccount({
                key: options.key,
                name: options.name,
                balance: options.balance,
                success: function() {
                    //update the account object
                    account.name = options.name;
                    
                    if (options.balance !== account.balance) {
                        account.balance = options.balance;

                        _this.updateBalances();
                    }
                    options.success(account);
                },
                error: options.error
            });
        },
        /**
         * Edits an entry amount. Updates the balances.
         *
         * @param options.acctKey The key of the account
         * @param options.xactionKey The key of the transaction
         * @param options.amount The amount of the entry
         * @param options.success The function to call upon success. Takes the
         *                        edited entry as a parameter.
         * @param options.error The function to call upon error
         */
        editEntry: function(options) {
            var _this = this; //store a reference to this for use in the callback

            xactionStore.editEntry({
            	acctKey: options.acctKey,
            	xactionKey: options.xactionKey,
                amount: options.amount,
                success: function(entry) {
                    //update the entry
                    var xaction = cashflow.getTransaction(options.xactionKey);
                    if (xaction.entries[options.acctKey]) {
                    	entry = xaction.entries[options.acctKey];
                    } else {
                    	xaction.entries[options.acctKey] = entry;
                    }
                    entry.amount = options.amount;

                    _this.updateBalances();

                    options.success(entry);
                },
                error: options.error
            });
        },
        /**
         * Adds a transaction to the cashflow.
         *
         * @param options.name The name of the transaction
         * @param options.date The date of the transaction
         * @param options.success The function to invoke upon success. Takes the new
         *                        new transaction as a parameter
         * @param options.error The function to invoke upon error
         */
        addTransaction: function(options) {
            var _this = this; //store a reference to this for use in the callback

            xactionStore.addTransaction({
                name: options.name,
                date: options.date,
                success: function(xaction) {
                    var xactions = cashflow.getTransactions();
                    xactions.push(xaction);
                    xactions.sort(_xactionSorter);
                    cashflow.setTransactions(xactions);

                    _this.updateBalances();

                    options.success(xaction);
                },
                error: options.error
            });
        },
        /**
         * Copies a transaction from the cashflow.
         *
         * @param options.key The key of the transaction to be copied
         * @param options.name The name of the transaction
         * @param options.date The date of the transaction
         * @param options.success The function to invoke upon success. Takes the new
         *                        new transaction as a parameter
         * @param options.error The function to invoke upon error
         */
        copyTransaction: function(options) {
            var _this = this; //store a reference to this for use in the callback

            xactionStore.copyTransaction({
            	key: options.key,
                name: options.name,
                date: options.date,
                success: function(xaction) {
                	var dateRange = cashflow.getCurrentDateRange();
                	
                	//if the date is inside the current range
                	if (xaction.details.date >= dateRange.start && xaction.details.date < dateRange.end) {
                		
                		//add it to the stored set
	                    var xactions = cashflow.getTransactions();
	                    xactions.push(xaction);
	                    xactions.sort(_xactionSorter);
	                    cashflow.setTransactions(xactions);
	
	                    _this.updateBalances();
                	}

                    options.success(xaction);
                },
                error: options.error
            });
        },
        /**
         * Deletes a transaction from the cashflow.
         *
         * @param options.key The transaction key
         * @param options.success The function to invoke upon success
         * @param options.error The function to invoke upon error
         */
        deleteTransaction: function(options) {
            var _this = this; //store a reference to this for use in the callback

            xactionStore.deleteTransaction({
                key: options.key,
                success: function() {
                    var xactions = cashflow.getTransactions();
                    for (var i in xactions) {
                        if (xactions[i].details.key == options.key) {
                            xactions.splice(i, 1);
                            break;
                        }
                    }
                    cashflow.setTransactions(xactions);

                    _this.updateBalances();

                    options.success();
                },
                error: options.error
            });
        },
        /**
         * Edits a transaction. Updates one or both of the name and date.
         *
         * @param options.key The key of the transaction
         * @param options.name The new name, optional
         * @param options.date The new date, optional
         * @param options.success The function to call upon success. Takes the
         *                        transaction as a parameter.
         * @param options.error The function to call upon error
         */
        editTransaction: function(options) {
        	var _this = this;
        	
            var xaction = cashflow.getTransaction(options.key);

            if (!options.name) { options.name = xaction.details.name; }
            if (!options.date) { options.date = xaction.details.date; }
            
            xactionStore.editTransaction({
                key: options.key,
                name: options.name,
                date: options.date,
                success: function() {
                	//update the transaction
                	xaction.details.name = options.name;
                	xaction.details.date = options.date;
                    
                    //re-sort the transactions
                    var xactions = cashflow.getTransactions();
                    xactions.sort(_xactionSorter);
                    
                    var dateRange = cashflow.getCurrentDateRange();
                    
                    //if the date is outside the current range
                    if (xaction.details.date < dateRange.start || xaction.details.date >= dateRange.end) {
                    	
                    	//remove the transaction from the stored set
                    	var xactions = cashflow.getTransactions();
                        for (var i in xactions) {
                            if (xactions[i].details.key == options.key) {
                                xactions.splice(i, 1);
                                break;
                            }
                        }
                        cashflow.setTransactions(xactions);
                    }

                    _this.updateBalances();
                	
                	options.success(xaction);
                },
                error: options.error
            });
        },
        /**
         * Loads the transactions for the given month into the cashflow.
         * 
		 * @param options.date The first date in the month to load
         * @param options.success The function to call upon success. Takes the
         *                        cash flow as a parameter.
         * @param options.error The function to call upon error
         */
        loadTransactions: function(options) {
        	var _this = this;
        	
        	//switch from 0-11 to 1-12
        	var month = options.date.getMonth() + 1;
            if (month < 10) {
            	month = "0"+month;
            }
            var formattedMonth = options.date.getFullYear() + "-" + month;
        	
        	xactionStore.getTransactions({
                "month": formattedMonth,
                "success": function(xactions) {
                    _this.handleTransactions(xactions, options.date);

                    _this.updateBalances();

                    options.success(cashflow);
                },
                "error": options.error
            });
        },
        /**
         * Updates the balances of all the transactions.
         */
        updateBalances: function() {
            var xactions = cashflow.getTransactions();
            var accounts = cashflow.getAccounts();

            var currentBalance = {};
            for (var j in accounts) {
                currentBalance[accounts[j].key] = accounts[j].balance;
            }

            var historic = []; //holder for historic transactions
            var now = new Date();
            var amount;
            var entry;

            for (var i in xactions) {
                var xactionDate = stamp.fromISOString(xactions[i].details.date);
                if (xactionDate < now) {
                    historic.unshift(xactions[i]);
                } else {
                    if (!xactions[i].balances) {
                        xactions[i].balances = {};
                    }
                    for (j in accounts) {
                        entry = xactions[i].entries[accounts[j].key];
                        if (entry) {
                            amount = entry.amount;
                        } else {
                            amount = 0;
                        }
                        currentBalance[accounts[j].key] += amount;
                        xactions[i].balances[accounts[j].key] = currentBalance[accounts[j].key];
                    }
                }
            }

            //reset the balances
            for (j in accounts) {
                currentBalance[accounts[j].key] = accounts[j].balance;
            }

            //now deal with the historic transactions
            for (i in historic) {
                if (!historic[i].balances) {
                    historic[i].balances = {};
                }
                for (j in accounts) {
                    entry = historic[i].entries[accounts[j].key];
                    if (entry) {
                        amount = entry.amount;
                    } else {
                        amount = 0;
                    }
                    currentBalance[accounts[j].key] -= amount;
                    historic[i].balances[accounts[j].key] = currentBalance[accounts[j].key];
                }
            }
        }
    };
});