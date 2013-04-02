/**
 * Grid
 */
define(["dojo/dom",
        "dojo/on",
        "dojo/date",
        "dojo/date/locale",
        "dojo/date/stamp",
        "dijit/registry",
        "dijit/form/CurrencyTextBox",
        "dijit/form/DateTextBox"], function(dom, on, date, locale, stamp, registry, CurrencyTextBox, DateTextBox) {

	

    return {
    	/** An object in which we will store the event handles so they can be removed later. */
    	_entryHandles: {},
        /**
         * Initializes the grid and adds it to the given DOM node.
         *
         * @param options.cashflow The cashflow the will be represented by the grid
         * @param srcNodeRef The id of the DOM node
         */
        onInit: function onInit() {
            var gridContainer = dom.byId(floweb.grid.nodeRef);

            gridContainer.appendChild(this.createTable(app.getCashflow()));
        },
        /**
         * Creates a table using the given cashflow as the model.
         *
         * @param cashflow A reference to the cashflow
         */
        createTable: function(cashflow) {
            var accounts = cashflow.getAccounts();

            var table = document.createElement("table");
            table.id = 'cashFlowTable';
            table.className = 'flo-cashflow';
            
            //create the header
            var thead = document.createElement("thead");

            //create the header rows
            thead.appendChild(this.createFirstHeaderRow(accounts));
            thead.appendChild(this.createSecondHeaderRow(accounts));
            
            table.appendChild(thead);

            //create the historic table body
            var historicBody = document.createElement("tbody");
            historicBody.id = 'historicBody';
            table.appendChild(historicBody);

            //create the current table body
            var currentBody = document.createElement("tbody");
            currentBody.id = 'currentBody';

            //add the current info row
            currentBody.appendChild(this.createCurrentRow(accounts));
            table.appendChild(currentBody);

            //create the upcoming table body
            var upcomingBody = document.createElement("tbody");
            upcomingBody.id = 'upcomingBody';
            table.appendChild(upcomingBody);

            var activeBody = historicBody;

            var transactions = cashflow.getTransactions();
            var currentDate = new Date();
            var foundCurrentRow = false;
            for (var i in transactions) {

                //if we have not yet reached the row that should contain the current info
                if (!foundCurrentRow) {
                    var xactionDate = stamp.fromISOString(transactions[i].details.date);

                    //if this transactions date is after the current date
                    if (xactionDate > currentDate) {
                    	if ($(activeBody).children().length == 0) {
                    		$(activeBody).hide();
                    	}
                    	
                        //switch to the third table body
                        activeBody = upcomingBody;

                        foundCurrentRow = true;
                    }
                }

                //the transaction row
                activeBody.appendChild(this.createTransactionRow(transactions[i], accounts));
            }
            
            if ($(activeBody).children().length == 0) {
        		$(activeBody).hide();
        	}

            return table;
        },
        /**
         * Creates a row representing the given transaction.
         *
         * @param transaction The transaction for the row.
         * @param accounts The list of accounts.
         */
        createTransactionRow: function(transaction, accounts) {
            var xactionRow = document.createElement("tr");
            xactionRow.id = 'xaction_' + transaction.details.key;
            xactionRow.className = 'flo-xaction';

            //the cell for the transactions name
            xactionRow.appendChild(this.createTransactionNameCell(transaction));

            //loop through the accounts to create the entry cells
            var j;
            for (j in accounts) {
                var value;
                var entry = transaction.entries[accounts[j].key];
                if (entry) {
                    value = entry.amount;
                } else {
                    value = 0;
                }


                xactionRow.appendChild(
                    this.createTransactionEntryCell(value, transaction.details.key, accounts[j].key)
                );
            }

            //add the date cell
            xactionRow.appendChild(this.createTransactionDateCell(transaction));

            //loop through the accounts to create the balance cells
            for (j in accounts) {
                var balance;
                if (transaction.balances) {
                    balance = transaction.balances[accounts[j].key];
                } else {
                    balance = accounts[j].balance;
                }

                xactionRow.appendChild(this.createTransactionBalanceCell(transaction.details.key, accounts[j], balance));
            }

            return xactionRow;
        },
        /**
         * Creates a cell for the transaction name.
         *
         * @param transaction The transaction
         */
        createTransactionNameCell: function(transaction) {
            var xactionNameCell = document.createElement("td");
            xactionNameCell.id = 'name_' + transaction.details.key;
            xactionNameCell.className = 'flo-xactionname';

            //when the user clicks the cell, it will become editable
            xactionNameCell.onclick = function(key, value) {return function(event) {
                app.showEditTransaction(
                        event,
                        this.id,
                        key,
                        value);
            };}(transaction.details.key, transaction.details.name);

            //put the name in the cell
            xactionNameCell.appendChild(document.createTextNode(transaction.details.name));

            return xactionNameCell;
        },
        /**
         * Returns a function that handles the blur event.
         * 
         * @param value The initial value
         * @param xactionKey The transaction key
         * @param acctKey The account key
         * @returns A function to handle the blur event
         */
        handleEntryCellBlur: function(value, xactionKey, acctKey) {return function() {
            if (this.isValid()) {
            	if (this.get('value') != value) {
            		app.editEntryAmount(this.get('value'), xactionKey, acctKey);
            	}
            } else {
            	this.set('value', value);
            }
        };},
        /**
         * Returns a function that handles the keydown event.
         * 
         * @param value The initial value
         * @param xactionKey The transaction key
         * @param acctKey The account key
         * @returns A function to handle the keydown event
         */
        handleEntryCellKeyDown: function(value, xactionKey, acctKey) {return function( event ) {
            if (event.keyCode == 13) {
            	if (this.isValid() && this.get('value') != value) {
                    app.editEntryAmount(this.get('value'), xactionKey, acctKey);
                    dijit.byId('entryInput_'+acctKey+'_'+xactionKey).focusNode.blur();
                }
            } else if (event.keyCode == 27) {
            	//reset the value
            	this.set('value', value);
            	dijit.byId('entryInput_'+acctKey+'_'+xactionKey).focusNode.blur();
            }
        };},
        /**
         * Creates a cell for a transaction entry.
         *
         * @param value The value of the entry
         * @param xactionKey The key of the transaction
         * @param acctKey The key of the account
         */
        createTransactionEntryCell: function(value, xactionKey, acctKey) {
        	var xactionEntryCell = document.createElement("td");
            xactionEntryCell.id = 'entry_'+acctKey+'_'+xactionKey;
            xactionEntryCell.className = 'flo-entry';

            var props = {
                id: 'entryInput_'+acctKey+'_'+xactionKey,
                value: value,
                lang: 'en-us',
                currency: "USD",
                invalidMessage: "Invalid amount"
            };

            var entryAmountTextBox = new CurrencyTextBox(props);

            xactionEntryCell.appendChild(entryAmountTextBox.domNode);
            
            var blurHandle = on(entryAmountTextBox, "blur", this.handleEntryCellBlur(value, xactionKey, acctKey));
            var keydownHandle = on(entryAmountTextBox, "keydown", this.handleEntryCellKeyDown(value, xactionKey, acctKey));
            this._entryHandles[props.id] = {
            	"blur": blurHandle,
            	"keydown": keydownHandle
            };
            
            //set the initial class (pos/neg)
        	if (value < 0) {
        		$(entryAmountTextBox.domNode).addClass('negative');
        	} else if (value > 0) {
        		$(entryAmountTextBox.domNode).addClass('positive');
        	}

            return xactionEntryCell;
        },
        /**
         * Creates a cell for the transaction date.
         *
         * @param transaction The transaction
         */
        createTransactionDateCell: function(transaction) {
            var xactionDateCell = document.createElement("td");
            xactionDateCell.id = 'date_'+transaction.details.key;
            xactionDateCell.className = 'flo-xactiondate';

            //create the date selection text box
            var props = {
                id:'dateInput_'+transaction.details.key,
                value: transaction.details.date,
                onChange: function(xaction) {return function(newValue) {
                    app.editTransactionDate(xaction.details.key, newValue, xaction.details.date);
                };}(transaction)
            };
            var xactionDateTextBox = new DateTextBox(props);

            xactionDateCell.appendChild(xactionDateTextBox.domNode);

            return xactionDateCell;
        },
        /**
         * Creates a cell for the transaction balance
         *
         * @param xactionKey The key of the transaction
         * @param accountKey The key of the account
         * @param balance The balance
         */
        createTransactionBalanceCell: function(xactionKey, account, balance) {
            var xactionBalanceCell = document.createElement("td");
            xactionBalanceCell.id = 'balance_'+account.key+'_'+xactionKey;
            xactionBalanceCell.className = 'flo-xactionbalance';

            var balanceText = dojo.currency.format(balance, {currency:'USD'});
            xactionBalanceCell.appendChild(document.createTextNode(balanceText));
            
            if (balance < account.negativeThreshold) {
            	$(xactionBalanceCell).addClass('negative');
            } else if (balance > account.positiveThreshold) {
            	$(xactionBalanceCell).addClass('positive');
            }

            return xactionBalanceCell;
        },
        /**
         * Creates a table row for the first part of the header.
         *
         * @param accounts The list of accounts
         */
        createFirstHeaderRow: function(accounts) {
            var firstHeaderRow = document.createElement("tr");

            var transactionsHeader = document.createElement("th");
            transactionsHeader.id = 'xactionHeader';
            transactionsHeader.rowSpan = 2;
            transactionsHeader.appendChild(document.createTextNode("Transactions"));
            firstHeaderRow.appendChild(transactionsHeader);

            var entriesHeader = document.createElement("th");
            entriesHeader.id = 'entriesHeader';
            entriesHeader.colSpan = accounts.length;
            entriesHeader.appendChild(document.createTextNode("Entries"));
            firstHeaderRow.appendChild(entriesHeader);

            var dateHeader = document.createElement("th");
            dateHeader.id = 'dateHeader';
            dateHeader.rowSpan = 2;
            dateHeader.appendChild(document.createTextNode("Date"));
            firstHeaderRow.appendChild(dateHeader);

            var balanceHeader = document.createElement("th");
            balanceHeader.id = 'balanceHeader';
            balanceHeader.colSpan = accounts.length;
            balanceHeader.appendChild(document.createTextNode("Balance"));
            firstHeaderRow.appendChild(balanceHeader);

            return firstHeaderRow;
        },
        /**
         * Creates a table row for the second part of the header.
         *
         * @param accounts The list of accounts
         */
        createSecondHeaderRow: function(accounts) {
            var secondHeaderRow = document.createElement("tr");
            secondHeaderRow.id = 'accountHeaderRow';

            var accountHeader;
            var accountText;
            for (var i in accounts) {
                accountHeader = document.createElement("th");
                accountHeader.id = 'account_'+accounts[i].key;
                accountHeader.className = 'flo-accountheader';

                //when the user clicks the cell, it will become editable
                accountHeader.onclick = function(key) {return function(event) {
                    app.showEditAccount(
                            event,
                            this.id,
                            key);
                };}(accounts[i].key);

                accountText = document.createTextNode(accounts[i].name);

                accountHeader.appendChild(accountText);
                secondHeaderRow.appendChild(accountHeader);
            }
            for (i in accounts) {
                accountHeader = document.createElement("th");
                accountHeader.id = 'accountbal_'+accounts[i].key;
                accountHeader.className = 'flo-accountheader';

                //when the user clicks the cell, it will become editable
                accountHeader.onclick = function(key) {return function(event) {
                    app.showEditAccount(
                            event,
                            this.id,
                            key);
                };}(accounts[i].key);

                accountText = document.createTextNode(accounts[i].name);

                accountHeader.appendChild(accountText);
                secondHeaderRow.appendChild(accountHeader);
            }

            return secondHeaderRow;
        },
        /**
         * Creates a table row representing the current situation.
         *
         * @param accounts The list of accounts
         */
        createCurrentRow: function(accounts) {
            var currentRow = document.createElement("tr");
            currentRow.id = 'currentRow';

            var currentCell = document.createElement("td");
            currentCell.id = 'current';
            currentCell.className = 'flo-currentname';
            currentCell.colSpan = 1 + accounts.length;
            currentCell.appendChild(document.createTextNode("Current"));
            currentRow.appendChild(currentCell);

            var currentDateCell = document.createElement("td");
            currentDateCell.id = 'currentDate';
            currentDateCellclassName = 'flo-currentdate';
            currentDateCell.appendChild(document.createTextNode(locale.format(new Date(), {selector:'date',fullYear:true})));
            currentRow.appendChild(currentDateCell);

            for (var i in accounts) {
                currentRow.appendChild(this.createCurrentBalanceCell(accounts[i]));
            }

            return currentRow;
        },
        /**
         * Creates a cell for the current balance of an account.
         *
         * @param The account
         */
        createCurrentBalanceCell: function(account) {
            var currentBalanceCell = document.createElement("td");
            currentBalanceCell.id = 'currbal_'+account.key;
            currentBalanceCell.className = 'flo-currentbalance';

            var props = {
            	id:'currbalInput_'+account.key,
                value: account.balance,
                lang: 'en-us',
                currency: "USD",
                required: true,
                invalidMessage: "Invalid amount.",
                missingMessage: "Enter the current balance.",
                onBlur: function(acct) {return function() {
                    if (this.isValid() && this.get('value') != acct.balance) {
                        app.editAccountBalance(acct.key, this.get('value'));
                    }
                };}(account),
                onFocus: function(acct) {return function( event ) {
                	dijit.byId('currbalInput_'+acct.key).focusNode.select();
                };}(account),
                onKeyDown: function(acct) {return function( event ) {
                    if (event.keyCode == 13) {
                    	if (this.isValid() && this.get('value') != acct.balance) {
                    		app.editAccountBalance(acct.key, this.get('value'));
                            dijit.byId('currbalInput_'+acct.key).focusNode.blur();
                        }
                    } else if (event.keyCode == 27) {
                    	//reset the value
                    	this.set('value', acct.balance);
                    	dijit.byId('currbalInput_'+acct.key).focusNode.blur();
                    }
                };}(account)
            };
            var currentBalanceTextBox = new CurrencyTextBox(props);

            currentBalanceCell.appendChild(currentBalanceTextBox.domNode);

            return currentBalanceCell;
        },
        /**
         * This method is invoked when an account is added to the cashflow.
         *
         * @param account The account that was added.
         */
        onAccountAdd: function(account) {
            //increase the span of the entries header
            var entriesHeader = dom.byId('entriesHeader');
            entriesHeader.colSpan += 1;

            //increase the span of the balance header
            var balanceHeader = dom.byId('balanceHeader');
            balanceHeader.colSpan += 1;

            //increatease the span of the "current" cell
            var currentCell = dom.byId('current');
            currentCell.colSpan += 1;

            //create a function to handle clicks on the account cell
            var clickHandler = function(key) {return function(event) {
                app.showEditAccount(
                        event,
                        this.id,
                        key);
            };}(account.key);

            //create the entries header cell
            var accountHeader = document.createElement("th");
            accountHeader.id = 'account_'+account.key;
            accountHeader.className = 'flo-accountheader';

            //when the user clicks the cell, it will become editable
            accountHeader.onclick = clickHandler;

            //add the account name to the cell
            accountHeader.appendChild(document.createTextNode(account.name));

            //the header row has account cells for entries and balances so we need
            //to find the first balance and insert the new entry header before that
            //cell
            var accountHeaderRow = dom.byId('accountHeaderRow');
            var i;
            for (i=0; i < accountHeaderRow.cells.length; i++) {
                if (accountHeaderRow.cells[i].id.indexOf('accountbal_', 0) == 0) {
                    accountHeaderRow.insertBefore(
                        accountHeader,
                        accountHeaderRow.cells[i]
                    );

                    break;
                }
            }

            //create the balance cell
            accountHeader = document.createElement("th");
            accountHeader.id = 'accountbal_'+account.key;
            accountHeader.className = 'flo-accountheader';

            //when the user clicks the cell, it will become editable
            accountHeader.onclick = clickHandler;

            //add the account name to the cell
            accountHeader.appendChild(document.createTextNode(account.name));

            accountHeaderRow.appendChild(accountHeader);

            var currentRow = dom.byId('currentRow');

            //create the current balance cell
            currentRow.appendChild(this.createCurrentBalanceCell(account));

            //create the entries
            var tableBodies = [
                dom.byId('historicBody'), dom.byId('upcomingBody')
            ];
            for (i=0; i < tableBodies.length; i++) {
                for (var j=0; j < tableBodies[i].rows.length; j++) {
                    var xactionRow = tableBodies[i].rows[j];
                    var xactionKey = xactionRow.id.substr(8); // everything after xaction_

                    var xactionDateCell = dom.byId('date_'+xactionKey);

                    var key = account.key+'_'+xactionKey;

                    xactionRow.insertBefore(
                        this.createTransactionEntryCell(0, xactionKey, account.key),
                        xactionDateCell
                    );

                    //the balance cell is just the account balance
                    xactionRow.appendChild(this.createTransactionBalanceCell(xactionKey, account, account.balance));
                }
            }
        },
        /**
         * This method is invoked when an account is deleted from the cashflow.
         *
         * @param account The key of the account that was deleted.
         */
        onAccountDelete: function(acctKey) {
            //remove the account headers
            $('#account_'+acctKey).remove();
            $('#accountbal_'+acctKey).remove();

            //update header colspans
            var entriesHeader = dom.byId('entriesHeader');
            entriesHeader.colSpan -= 1;

            var balanceHeader = dom.byId('balanceHeader');
            balanceHeader.colSpan -= 1;

            //update current colspan
            var currentCell = dom.byId('current');
            currentCell.colSpan -= 1;

            //remove current balance
            $('#currbal_'+acctKey).remove();

            //remove all entries for the account
            $('td[id^="entry_'+acctKey+'"]').remove();

            //remove all balances for the account
            $('td[id^="balance_'+acctKey+'"]').remove();
        },
        /**
         * This method is invoked when an account is updated.
         *
         * @param account The account that was updated.
         */
        onAccountUpdate: function(account) {
            var accountHeader = dom.byId('account_'+account.key);
            accountHeader.onclick = function(key) {return function(event) {
                    app.showEditAccount(
                            event,
                            this.id,
                            key);
                };}(account.key);
            accountHeader.replaceChild(
                document.createTextNode(account.name),
                accountHeader.firstChild
            );

            accountHeader = dom.byId('accountbal_'+account.key);
            accountHeader.onclick = function(key) {return function(event) {
                    app.showEditAccount(
                            event,
                            this.id,
                            key);
                };}(account.key);
            accountHeader.replaceChild(
                document.createTextNode(account.name),
                accountHeader.firstChild
            );
            
            var transactions = app.getCashflow().getTransactions();
            for (var i in transactions) {
            	var $xactionBalanceCell = $('#balance_'+account.key+'_'+transactions[i].details.key);
                if (transactions[i].balances[account.key] < account.negativeThreshold) {
                	if (!$xactionBalanceCell.hasClass('negative')) {
                		$xactionBalanceCell.removeClass('positive');
                		$xactionBalanceCell.addClass('negative');
                	}
                } else if (transactions[i].balances[account.key] > account.positiveThreshold) {
                	if (!$xactionBalanceCell.hasClass('positive')) {
                		$xactionBalanceCell.removeClass('negative');
                		$xactionBalanceCell.addClass('positive');
                	}
                } else if ($xactionBalanceCell.hasClass('negative') || $xactionBalanceCell.hasClass('positive')) {
                	$xactionBalanceCell.removeClass('negative positive');
                }
            }
        },
        /**
         * This method is invoked when an entry is edited.
         *
         * @param entry The entry that was edited
         * @param acctKey The key of the account for the entry
         * @param xactionKey The key of the transaction for the entry
         */
        onEntryEdit: function onEntryEdit(entry, acctKey, xactionKey) {
        	var widgetId = 'entryInput_'+acctKey+'_'+xactionKey;
            var entryInput = registry.byId(widgetId);
            if (entryInput) {
                entryInput.set('id','entryInput_'+entry.key);
            } else {
                entryInput = registry.byId('entryInput_'+entry.key);
            }
            
            //update the class for the new value
            $(entryInput.domNode).removeClass('negative positive');
            if (entry.amount < 0) {
        		$(entryInput.domNode).addClass('negative');
        	} else if (entry.amount > 0) {
        		$(entryInput.domNode).addClass('positive');
        	}

            this._entryHandles[widgetId].blur.remove();
            this._entryHandles[widgetId].keydown.remove();
            
            var blurHandle = on(
            		entryInput, 
            		"blur", 
            		this.handleEntryCellBlur(entry.amount, xactionKey, acctKey, entry.key)
            	);
            var keydownHandle = on(
            		entryInput, 
            		"keydown", 
            		this.handleEntryCellKeyDown(entry.amount, xactionKey, acctKey, entry.key)
            	);
            this._entryHandles[widgetId] = {
            	"blur": blurHandle,
            	"keydown": keydownHandle
            };
        },
        /**
         * This method is invoked when any changes happen to the balances.
         *
         * @param transactions Array of transactions
         * @param account The account that was changed, optional if all were changed
         */
        onBalanceUpdate: function(transactions, account) {
            for (var i in transactions) {
                //if there are balances to be displayed
                if (transactions[i].balances) {
                    var xactionBalanceCell;
                    var balance;

                    //if an account was provided, only update balances for that account
                    if (account) {
                        xactionBalanceCell = dom.byId('balance_'+account.key+'_'+transactions[i].details.key);
                        balance = dojo.currency.format(transactions[i].balances[account.key], {currency:'USD'});
                        xactionBalanceCell.replaceChild(
                            document.createTextNode(balance),
                            xactionBalanceCell.firstChild
                        );
                        
                        var $xactionBalanceCell = $(xactionBalanceCell);
                        if (transactions[i].balances[account.key] < account.negativeThreshold) {
                        	if (!$xactionBalanceCell.hasClass('negative')) {
                        		$xactionBalanceCell.removeClass('positive');
                        		$xactionBalanceCell.addClass('negative');
                        	}
                        } else if (transactions[i].balances[account.key] > account.positiveThreshold) {
                        	if (!$xactionBalanceCell.hasClass('positive')) {
                        		$xactionBalanceCell.removeClass('negative');
                        		$xactionBalanceCell.addClass('positive');
                        	}
                        } else if ($xactionBalanceCell.hasClass('negative') || $xactionBalanceCell.hasClass('positive')) {
                        	$xactionBalanceCell.removeClass('negative positive');
                        }
                    } else {
                        for (var j in transactions[i].balances) {
                            xactionBalanceCell = dom.byId('balance_'+j+'_'+transactions[i].details.key);
                            balance = dojo.currency.format(transactions[i].balances[j], {currency:'USD'});
                            xactionBalanceCell.replaceChild(
                                document.createTextNode(balance),
                                xactionBalanceCell.firstChild
                            );
                            
                            var $xactionBalanceCell = $(xactionBalanceCell);
                            if (transactions[i].balances[j] < account.negativeThreshold) {
                            	if (!$xactionBalanceCell.hasClass('negative')) {
                            		$xactionBalanceCell.removeClass('positive');
                            		$xactionBalanceCell.addClass('negative');
                            	}
                            } else if (transactions[i].balances[j] > account.positiveThreshold) {
                            	if (!$xactionBalanceCell.hasClass('positive')) {
                            		$xactionBalanceCell.removeClass('negative');
                            		$xactionBalanceCell.addClass('positive');
                            	}
                            } else if ($xactionBalanceCell.hasClass('negative') || $xactionBalanceCell.hasClass('positive')) {
                            	$xactionBalanceCell.removeClass('negative positive');
                            }
                        }
                    }
                }
            }
        },
        /**
         * This method is invoked when a transaction is added.
         *
         * @param xaction The transaction that was added
         */
        onTransactionAdd: function(xaction) {
        	var dateRange = app.getCashflow().getCurrentDateRange();
        	
        	if (xaction.details.date >= dateRange.start && xaction.details.date < dateRange.end) {
	            //get the current list of accounts
	            var accounts = app.getCashflow().getAccounts();
	
	            var xactionRow = this.createTransactionRow(xaction, accounts);
	
	            var tbody = this.attachTransactionRow(xactionRow, xaction);
	            
	            $(tbody).show();
        	} else {
        		console.log('xaction created outside range: [' + dateRange.start + ' to ' + dateRange.end + ')');
        	}
        },
        /**
         * This method is invoked when a transaction is deleted.
         *
         * @param xaction The key of the transaction that was deleted
         */
        onTransactionDelete: function(xactionKey) {
            var xactionRow = dom.byId('xaction_'+xactionKey);

            xactionRow.parentNode.removeChild(xactionRow);
            
            var widgets = dijit.findWidgets(xactionRow);
            dojo.forEach(widgets, function(w) {
                w.destroyRecursive(true);
            });
        },
        /**
         * This method is invoked when a transaction is updated
         *
         * @param xaction The transaction that was updated
         */
        onTransactionUpdate: function(xaction) {
        	var xactionRow = dom.byId('xaction_'+xaction.details.key);
        	
        	var dateRange = app.getCashflow().getCurrentDateRange();
        	
        	//if the transaction is between the currently displayed dates
        	if (xaction.details.date >= dateRange.start && xaction.details.date < dateRange.end) {
	            //ensure the name is up to date
	            var xactionNameCell = dom.byId('name_'+xaction.details.key);
	            xactionNameCell.replaceChild(
	                    document.createTextNode(xaction.details.name),
	                    xactionNameCell.firstChild
	                );
	            
	            //first check if it's already in the right spot
	            var prevDate = null;
	            var nextDate = null;
	            
	            var prevRow = xactionRow.previousSibling;
	            if (prevRow != null) {
	            	var prevXactionKey = prevRow.id.substring(8); //evertything after 'xaction_'
	            	prevDate = dojo.date.stamp.toISOString(dijit.byId('dateInput_'+prevXactionKey).value, {selector: 'date'});
	            }
	            
	            var nextRow = xactionRow.nextSibling;
	            if (nextRow != null) {
	            	var nextXactionKey = nextRow.id.substring(8); //evertything after 'xaction_'
	            	nextDate = dojo.date.stamp.toISOString(dijit.byId('dateInput_'+nextXactionKey).value, {selector: 'date'});
	            }
	            
	            var currentDate = dojo.date.stamp.toISOString(new Date(), {selector: 'date'});
	            
	            
	            
	            //if the xaction is not between the right rows
	            if (
	            		(prevRow != null && prevDate > xaction.details.date) || 
	            		(nextRow != null && nextDate < xaction.details.date) ||
	            		(prevRow == null && xaction.details.date <= currentDate) ||
	            		(nextRow == null && xaction.details.date > currentDate)) {
	            
		            var tbody = xactionRow.parentNode;
		            tbody.removeChild(xactionRow);
		            
		            if (tbody.firstChild == null) {
		            	$(tbody).hide();
		            }
		            
		            //move the table row to the correct spot
		            this.attachTransactionRow(xactionRow, xaction);
	            }
        	} else {
        		var tbody = xactionRow.parentNode;
	            tbody.removeChild(xactionRow);
	            
	            var widgets = dijit.findWidgets(xactionRow);
	            dojo.forEach(widgets, function(w) {
	                w.destroyRecursive(true);
	            });
        	}
        },
        onTransactionLoad: function(cashflow) {
        	var xactions = cashflow.getTransactions();
        	
        	for (var i in xactions) {
        		var xactionRow = dom.byId('xaction_' + xactions[i].details.key);
        		
        		//if the transaction is already displayed
        		if (xactionRow) {
        			//update it
        			this.onTransactionUpdate(xactions[i]);
        		} else {
        			var accounts = app.getCashflow().getAccounts();
        			
        			xactionRow = this.createTransactionRow(xactions[i], accounts);
        			
        			this.attachTransactionRow(xactionRow, xactions[i]);
        		}
        	}
        },
        /**
         * Attaches a pre-built transaction row to the grid.
         * 
         * @param xactionRow A pre-built transaction row
         * @param xaction The transaction from which the row was built
         */
        attachTransactionRow: function(xactionRow, xaction) {
        	var found = false;
            var tbody;
            var currentDate = dojo.date.stamp.toISOString(new Date(), {selector: 'date'});
            if (xaction.details.date <= currentDate) {
                tbody = dom.byId('historicBody');
            } else {
                tbody = dom.byId('upcomingBody');
            }
            for (var i = 0; i < tbody.rows.length; i++) {
                var xactionKey = tbody.rows[i].id.substring(8); //evertything after 'xaction_'
                var date = dojo.date.stamp.toISOString(dijit.byId('dateInput_'+xactionKey).value, {selector: 'date'});
                if (xaction.details.date < date) {
                    tbody.insertBefore(xactionRow, tbody.rows[i]);
                    found = true;
                    break;
                } else if (xaction.details.date == date && xaction.details.key < xactionKey) {
                	tbody.insertBefore(xactionRow, tbody.rows[i]);
                    found = true;
                    break;
                }
            }

            //if the position wasn't found'
            if (!found) {
                //put the transaction at the end
                tbody.appendChild(xactionRow);
            }
            
            $(tbody).show();
            
            return tbody;
        }
    };
});