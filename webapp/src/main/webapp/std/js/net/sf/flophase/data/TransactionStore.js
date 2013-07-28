/**
 * TransactionStore
 *
 * This class is the main interface for the cash flow store to transaction
 * related data.
 */
define([], function() {

    return {
        /**
         * Gets the transactions from the server.
         *
         * @param options.month The month in YYYY-MM format to be loaded
         * @param options.success The function to invoke upon success. Takes an
         *                        array of transactions as a parameter
         * @param options.error The function to invoke upon error, takes a single
         *                      string parameter
         */
        getTransactions: function(options) {
            dojo.xhrGet({
                url: "/xaction/q",
                content: {
                    month: options.month
                },
                handleAs: "json",
                load: function(jsonData) {
                    if (jsonData.result == 1) { //SUCCESS
                        options.success(jsonData.content);
                    } else {
                        options.error({
                        	messages:jsonData.messages,
                        	cause:"Error /xaction/q"
                        });
                    }
               },
               error: function(msg) {
                   options.error({
                	   messages:["Unable to load transactions."],
                	   cause: msg
                   });
               }
            });
        },
        /**
         * Adds a transaction.
         *
         * @param options.name The name of the transaction
         * @param options.date The date of the transaction
         * @param options.success The function to invoke upon success. Takes the new
         *                        new transaction as a parameter
         * @param options.error The function to invoke upon error, takes a single
         *                      string parameter
         */
        addTransaction: function(options) {
            dojo.xhrGet({
                url: "/xaction/add",
                content: {
                    name: options.name,
                    date: options.date
                },
                handleAs: "json",
                load: function(jsonData) {
                    if (jsonData.result == 1) { //SUCCESS
                        options.success(jsonData.content);
                    } else {
                        options.error({
                        	messages:jsonData.messages,
                        	cause:"Error /xaction/add"
                        });
                    }
               },
               error: function(msg) {
                   options.error({
                	   messages:["Unable to add transaction."],
                	   cause:msg
                   });
               }
            });
        },
        /**
         * Copies a transaction.
         *
         * @param options.key The key of the transaction to be copied
         * @param options.name The name of the transaction
         * @param options.date The date of the transaction
         * @param options.success The function to invoke upon success. Takes the new
         *                        new transaction as a parameter
         * @param options.error The function to invoke upon error, takes a single
         *                      string parameter
         */
        copyTransaction: function(options) {
            dojo.xhrGet({
                url: "/xaction/copy",
                content: {
                	key: options.key,
                    name: options.name,
                    date: options.date
                },
                handleAs: "json",
                load: function(jsonData) {
                    if (jsonData.result == 1) { //SUCCESS
                        options.success(jsonData.content);
                    } else {
                        options.error({
                        	messages:jsonData.messages,
                        	cause:"Error /xaction/copy"
                        });
                    }
               },
               error: function(msg) {
                   options.error({
                	   messages:["Unable to copy transaction."],
                	   cause:msg
                   });
               }
            });
        },
        /**
         * Deletes a transaction.
         *
         * @param options.key The transaction key
         * @param options.success The function to invoke upon success
         * @param options.error The function to invoke upon error, takes a single
         *                      string parameter
         */
        deleteTransaction: function(options) {
            dojo.xhrGet({
                url: "/xaction/delete",
                content: {
                    key: options.key
                },
                handleAs: "json",
                load: function(jsonData) {
                    if (jsonData.result == 1) { //SUCCESS
                        options.success();
                    } else {
                        options.error({
                        	messages:jsonData.messages,
                        	cause:"Error /xaction/delete"
                        });
                    }
               },
               error: function(msg) {
                   options.error({
                	   messages:["Unable to delete transaction."],
                	   cause:msg
                   });
               }
            });
        },
        /**
         * Edits a transaction.
         *
         * @param options.xaction The updated transaction
         * @param options.success The function to invoke upon success. Takes the new
         *                        edited transaction as a parameter
         * @param options.error The function to invoke upon error, takes a single
         *                      string parameter
         */
        editTransaction: function(options) {
            dojo.xhrGet({
                url: "/xaction/edit",
                content: {
                    key: options.key,
                    name: options.name,
                    date: options.date
                },
                handleAs: "json",
                load: function(jsonData) {
                    if (jsonData.result == 1) { //SUCCESS
                        options.success(options.xaction);
                    } else {
                        options.error({
                        	messages:jsonData.messages,
                        	cause:"Error /xaction/edit"
                        });
                    }
               },
               error: function(msg) {
                   options.error({
                	   messages:["Unable to edit transaction."],
                	   cause:msg
                   });
               }
            });
        },
        /**
         * Edits an existing entry.
         *
         * @param options.acctKey The account key of the entry
         * @param options.xactionKey The transaction key of the entry
         * @param options.amount The amount of the entry
         * @param options.success The function to call upon success. Takes the
         *                        edited entry as a parameter.
         * @param options.error The function to call upon error, takes a single
         *                      string parameter
         */
        editEntry: function(options) {
            dojo.xhrGet({
                url: "/entry/edit",
                content: {
                	account: options.acctKey,
                	xaction: options.xactionKey,
                    amount: options.amount
                },
                handleAs: "json",
                load: function(jsonData) {
                    if (jsonData.result == 1) { //SUCCESS
                        options.success(jsonData.content);
                    } else {
                        options.error({
                        	messages:jsonData.messages,
                        	cause:"Error /entry/edit"
                        });
                    }
               },
               error: function(msg) {
                   options.error({
                	   messages:["Unable to edit entry"],
                	   cause:msg
                   });
               }
            });
        }
    };
});