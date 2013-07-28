/**
 * AccountStore
 *
 * This class is the main interface for the cash flow store to account-related
 * data.
 */
define([], function() {

    return {
        /**
         * Loads the accounts from the server.
         *
         * @param options.success The function to invoke upon success
         * @param options.error The function to invoke upon error
         */
        getAccounts: function(options) {
            dojo.xhrGet({
                url: "/account/q",
                handleAs: "json",
                load: function(jsonData) {
                    if (jsonData.result == 1) { //SUCCESS
                        options.success(jsonData.content);
                    } else {
                        options.error({
                        	messages:jsonData.messages,
                        	cause:"Error: /account/q"
                        });
                    }
               },
               error: function(msg) {
                   options.error({
                	   messages:["Unable to load accounts."],
                	   cause:msg
                   });
               }
            });
        },
        /**
         * Adds a new account.
         *
         * @param options.name The account name
         * @param options.balance The current balance
         * @param options.success The function to call upon success
         * @param options.error The function to call upon error, takes an array of
         *                      strings
         */
        addAccount: function(options) {
            dojo.xhrGet({
                url: "/account/add",
                content: {
                    name: options.name,
                    balance: options.balance
                },
                handleAs: "json",
                load: function (jsonData) {
                    if (jsonData.result == 1) { //SUCCESS
                        options.success(jsonData.content);
                    } else {
                        options.error({
                        	messages:jsonData.messages,
                        	cause: "Error: /account/add"
                        });
                    }
                },
                error: function(msg) {
                    options.error({
                    	messages:["Unable to add account."],
                    	cause:msg
                    });
                }
            });
        },
        /**
         * Deletes an existing account.
         *
         * @param options.key The account key
         * @param options.success The function to call upon success
         * @param options.error The function to call upon error, takes a single string parameter
         */
        deleteAccount: function(options) {
            dojo.xhrGet({
                url: "/account/delete",
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
                        	cause:"Error /account/delete"
                        });
                    }
                },
                error: function(msg) {
                    options.error({
                    	messages:["Unable to delete account."],
                    	cause:msg
                    });
                }
            });
        },
        /**
         * Edits an existing account.
         *
         * @param options.key The key of the account object
         * @param options.name The updated account name, optional
         * @param options.balance The updated account balance, optional
         * @param options.success The function to call upon success
         * @param options.error The function to call upon error, takes an array of
         *                      strings
         */
        editAccount: function(options) {
            dojo.xhrGet({
                url: "/account/edit",
                content: {
                    key: options.key,
                    name: options.name,
                    balance: options.balance,
                    neg: options.negativeThreshold,
                    pos: options.positiveThreshold
                },
                handleAs: "json",
                load: function(jsonData) {
                    if (jsonData.result == 1) { //SUCCESS
                        options.success();
                    } else {
                        options.error({
                        	messages:jsonData.messages,
                        	cause:"Error /account/edit"
                        });
                    }
                },
                error: function(msg) {
                    options.error({
                    	messages:["Unable to edit account."],
                    	cause:msg
                    });
                }
            });
        }
    };
});