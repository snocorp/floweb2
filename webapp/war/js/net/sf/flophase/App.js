define([
        "dojo/dom",
        "dojo/date",
        "dijit/registry",
        "net/sf/flophase/data/CashFlowStore",
        "net/sf/flophase/ui/Grid",
        "net/sf/flophase/ui/AddAccountDialog",
        "net/sf/flophase/ui/AddTransactionDialog",
        "net/sf/flophase/ui/EditAccountDialog",
        "net/sf/flophase/ui/EditTransactionDialog",
        "net/sf/flophase/ui/Toolbar",
        "net/sf/flophase/EventBus"
    ], function(
    		dom, 
    		date,
    		registry, 
    		cashFlowStore, 
    		grid, 
    		addAccountDialog,
    		addTransactionDialog,
    		editAccountDialog,
    		editTransactionDialog,
    		toolbar,
    		eventBus) {
    
    var cashflow;

    return {
        /**
         * Initializes the application.
         */
    	init: function(options) {
        	var _this = this;
        	
        	eventBus.addListeners([
        	                       this,
        	                       grid,
        	                       addAccountDialog,
        	                       addTransactionDialog,
        	                       editAccountDialog,
        	                       editTransactionDialog,
        	                       toolbar
        	                       ]);
        	
            //load the initial cashflow
            cashFlowStore.getCashFlow({
                success: function(newCashflow) {
                    cashflow = newCashflow;
                    
                    eventBus.fireInit();
                },
                error: function(message) {
                	_this.notify(message);
                }
            });
        },
        /**
         * Gets the cashflow
         */
        getCashflow: function() {
            return cashflow;
        },
        /**
         * Load transactions from the next earlier month.
         */
        loadEarlier: function() {
        	var _this = this;
        	
        	var startMonth = cashflow.getStartMonth();
        	
        	//get the previous month
        	startMonth = date.add(startMonth, "month", -1);
        	
        	cashFlowStore.loadTransactions({
        		date: startMonth,
        		success: function(cashflow) {
        			eventBus.fireTransactionLoad(cashflow);
        		},
        		error: function(messages) {
        			_this.notify(messages);
        		}
        	});
        },
        /**
         * Load transactions from the next upcoming month.
         */
        loadUpcoming: function() {
        	var _this = this;
        	
        	var endMonth = cashflow.getEndMonth();
        	
        	//get the next month
        	endMonth = date.add(endMonth, "month", 1);
        	
        	cashFlowStore.loadTransactions({
        		date: endMonth,
        		success: function(cashflow) {
        			eventBus.fireTransactionLoad(cashflow);
        		},
        		error: function(messages) {
        			_this.notify(messages);
        		}
        	});
        },
        /**
         * Shows the add account dialog.
         */
        showAddAccount: function() {
        	addAccountDialog.show();
        },
        /**
         * Shows the add transaction dialog.
         */
        showAddTransaction: function() {
            addTransactionDialog.show();
        },
        /**
         * Shows the edit account popup.
         */
        showEditAccount: function( event, acctHeaderCellNodeRef, acctKey, acctName ) {
            editAccountDialog.show(acctHeaderCellNodeRef, acctKey, acctName);

            //prevent bubbling
            event.stopPropagation();
        },
        /**
         * Shows the edit transaction popup.
         */
        showEditTransaction: function( event, nameCellNodeRef, xactionKey, xactionName ) {
        	editTransactionDialog.show(nameCellNodeRef, xactionKey, xactionName);

            //prevent bubbling
            event.stopPropagation();
        },
        /**
         * Hides the add account dialog.
         */
        hideAddAccount: function() {
        	addAccountDialog.hide();
        },
        /**
         * Hides the add transaction dialog.
         */
        hideAddTransaction: function() {
        	addTransactionDialog.hide();
        },
        /**
         * Hides the edit account name popup.
         */
        hideEditAccount: function() {
        	editAccountDialog.hide();
        },
        /**
         * Hides the edit transaction popup.
         */
        hideEditTransaction: function() {
        	editTransactionDialog.hide();
        },
        /**
         * Adds a new account.
         */
        addAccount: function(name, balance) {
            var _this = this; //store a reference to this

            cashFlowStore.addAccount({
                "name": name,
                "balance": balance,
                "success": function(account) {
                    _this.hideAddAccount();

                    eventBus.fireAccountAdd(account);
                },
                "error": function(messages) { addAccountDialog.showError(messages); }
            });
        },
        /**
         * Adds a transaction.
         */
        addTransaction: function(name, date) {
        	var _this = this;

            cashFlowStore.addTransaction({
                "name": $("#newTransactionName").val(),
                "date": dom.byId("newTransactionDate").value,
                "success": function(xaction) {
                    _this.hideAddTransaction();
                	
                    eventBus.fireTransactionAdd(xaction);
                },
                "error": function(messages) { addTransactionDialog.showError(messages); }
            });
        },
        /**
         * Deletes an account.
         *
         * @param acctKey The key of the account.
         */
        deleteAccount: function(acctKey) {
            var _this = this; //store a reference to this

            cashFlowStore.deleteAccount({
               key: acctKey,
               success: function() {
                    _this.hideEditAccount();

                   eventBus.fireAccountDelete(acctKey);
               },
               error: function(message) {
                   _this.notify(message);
               }
            });
        },
        /**
         * Deletes a transaction.
         *
         * @param xactionKey The key of the transaction.
         */
        deleteTransaction: function(xactionKey) {
            var _this = this; //store a reference to this

            cashFlowStore.deleteTransaction({
                key: xactionKey,
                success: function() {
                    _this.hideEditTransaction();

                    eventBus.fireTransactionDelete(xactionKey);

                    eventBus.fireBalanceUpdate(_this.getCashflow().getTransactions());
                },
                error: function(message) { 
                	_this.notify(message); 
                }
            })
        },
        /**
         * Edits the current balance of an account.
         *
         * @param acctKey The key of the account.
         * @param balance The new balance.
         */
        editAccountBalance: function(acctKey, balance) {
            var _this = this; //store a reference to this

            cashFlowStore.editAccount({
                key:acctKey,
                balance:balance,
                success: function(account) {
                	eventBus.fireBalanceUpdate(_this.getCashflow().getTransactions(), account);
                },
                error: function(messages) { 
                	_this.notify(messages); 
                }
            });
        },
        /**
         * Edits the name of an account.
         *
         * @param acctKey The key of the account.
         * @param name The new name.
         */
        editAccountName: function(acctKey, name) {
            var _this = this; //store a reference to this

            cashFlowStore.editAccount({
                key:acctKey,
                name:name,
                success: function(account) {
                    _this.hideEditAccount();

                    eventBus.fireAccountUpdate(account);
                },
                error: function(messages) { 
                	_this.notify(messages); 
                }
            });
        },
        /**
         * Edits the amount of an entry.
         *
         * @param value The new amount.
         * @param xactionKey The key of the transaction.
         * @param acctKey The key of the account.
         * @param entryKey The key of the entry, optional if entry exists.
         */
        editEntryAmount: function(value, xactionKey, acctKey, entryKey) {
            var _this = this; //store a reference to this

            cashFlowStore.editEntry({
                acctKey: acctKey,
                xactionKey: xactionKey,
                amount: value,
                success: function(entry) {
                    eventBus.fireEntryEdit(entry, acctKey, xactionKey);

                    var xactions = _this.getCashflow().getTransactions();
                    var account = _this.getCashflow().getAccount(acctKey);
                    eventBus.fireBalanceUpdate(xactions, account);
                },
                error: function(message) { 
                	_this.notify(message); 
                }
            });
        },
        /**
         * Edits the date of the transaction.
         *
         * @param xactionKey The key of the transaction
         * @param newDate The new date of the transaction
         * @param origDate The original date of the transaction
         */
        editTransactionDate: function(xactionKey, newDate, origDate) {
            var _this = this; //store a reference to this
            
            newDate = dojo.date.stamp.toISOString(newDate, {selector: 'date'});

            if (origDate != newDate) {
                cashFlowStore.editTransaction({
                    key:xactionKey,
                    date:newDate,
                    success: function(xaction) {
                        eventBus.fireTransactionUpdate(xaction);

                        eventBus.fireBalanceUpdate(_this.getCashflow().getTransactions())
                    },
                    error: function(message) { 
                    	_this.notify(message); 
                    }
                });
            }
        },
        /**
         * Edits the name of the transaction
         *
         * @param xactionKey The key of the transaction
         */
        editTransactionName: function(xactionKey, name) {
        	var _this = this; //store a reference to this

            this.hideEditTransaction();

            cashFlowStore.editTransaction({
                key:xactionKey,
                name:name,
                success: function(xaction) {
                	eventBus.fireTransactionUpdate(xaction);
                },
                error: function(message) { 
                	_this.notify(message); 
                }
            });
        },
        /**
         * Pops up a notification.
         */
        notify: function(message) {
            $('#notificationMsg').empty().append(message);

            $('#notification').jmNotify();
        }
    };
});
