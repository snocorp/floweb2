/**
 * CashFlow
 *
 * This class stores the data about the cashflow. It keeps a list of the
 * accounts and the transactions as well as key value maps of accounts and
 * transactions.
 */
define([], function() {
	
	/**
	 * The first day in the earliest month that is loaded.
	 */
	var startMonth;
	
	/**
	 * The first day in the last month that is loaded.
	 */
	var endMonth;

    var accounts = [];
    var accountMap = {};
    var transactions = [];
    var transactionMap = {};

    return {
        /**
         * Returns the account with the given key.
         *
         * @param acctKey The key of the account
         */
        getAccount: function(acctKey) {
            return accountMap[acctKey];
        },
        /**
         * Returns the list of accounts.
         */
        getAccounts: function() {
            return accounts;
        },
        /**
         * Sets the last month that is loaded.
         */
        getEndMonth: function() {
        	return endMonth;
        },
        /**
         * Sets the first month that is loaded.
         */
        getStartMonth: function() {
        	return startMonth; 
        },
        /**
         * Returns the transaction with the given key.
         *
         * @param xactionKey The key of the transaction
         */
        getTransaction: function(xactionKey) {
            return transactionMap[xactionKey];
        },
        /**
         * Returns the list of transactions.
         */
        getTransactions: function() {
            return transactions;
        },
        /**
         * Sets the list of accounts. Re-builds the account map.
         */
        setAccounts: function(accts) {
            accounts = accts.slice(0);

            accountMap = {}; //clear the map
            for (var i in accounts) {
                accountMap[accounts[i].key] = accounts[i];
            }
        },
        /**
         * Sets the last month that is loaded.
         */
        setEndMonth: function(month) {
        	endMonth = month;
        },
        /**
         * Sets the first month that is loaded.
         */
        setStartMonth: function(month) {
        	startMonth = month; 
        },
        /**
         * Sets the list of transactions. Re-builds the transaction map.
         */
        setTransactions: function(xactions) {
            transactions = xactions.slice(0);

            transactionMap = {}; //clear the map
            for (var i in transactions) {
                transactionMap[transactions[i].details.key] = transactions[i];
            }
        }
    };
});