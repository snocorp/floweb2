/**
 * EventBus
 * 
 * This is the central event bus. Modules added as listeners and are notified of events.
 */
define([
    ], function() {
    
    var listeners = new Array();

    return {
    	addListener: function(listener) {
    		listeners.push(listener);
    	},
    	addListeners: function(newListeners) {
    		listeners = listeners.concat(newListeners);
    	},
    	removeListener: function(listener) {
    		for (var i in listeners) {
    			if (listeners[i] === listener) {
    				listeners.splice(i, 1);
    				break;
    			}
    		}
    	},
    	fireAccountAdd: function(account) {
    		for (var i in listeners) {
    			if (listeners[i].onAccountAdd) {
    				listeners[i].onAccountAdd(account);
    			}
    		}
    	},
    	fireAccountDelete: function(acctKey) {
    		for (var i in listeners) {
    			if (listeners[i].onAccountDelete) {
    				listeners[i].onAccountDelete(acctKey);
    			}
    		}
    	},
    	fireAccountUpdate: function(account) {
    		for (var i in listeners) {
    			if (listeners[i].onAccountUpdate) {
    				listeners[i].onAccountUpdate(account);
    			}
    		}
    	},
    	fireBalanceUpdate: function(xactions, account) {
    		for (var i in listeners) {
    			if (listeners[i].onBalanceUpdate) {
    				listeners[i].onBalanceUpdate(xactions, account);
    			}
    		}
    	},
    	fireEntryEdit: function(entry, acctKey, xactionKey) {
    		for (var i in listeners) {
    			if (listeners[i].onEntryEdit) {
    				listeners[i].onEntryEdit(entry, acctKey, xactionKey);
    			}
    		}
    	},
    	fireInit: function() {
    		for (var i in listeners) {
    			if (listeners[i].onInit) {
    				listeners[i].onInit();
    			}
    		}
    	}, 
    	fireTransactionAdd: function(xaction) {
    		for (var i in listeners) {
    			if (listeners[i].onTransactionAdd) {
    				listeners[i].onTransactionAdd(xaction);
    			}
    		}
    	},
    	fireTransactionDelete: function(xactionKey) {
    		for (var i in listeners) {
    			if (listeners[i].onTransactionDelete) {
    				listeners[i].onTransactionDelete(xactionKey);
    			}
    		}
    	},
    	fireTransactionLoad: function(cashflow) {
    		for (var i in listeners) {
    			if (listeners[i].onTransactionLoad) {
    				listeners[i].onTransactionLoad(cashflow);
    			}
    		}
    	},
    	fireTransactionUpdate: function(xaction) {
    		for (var i in listeners) {
    			if (listeners[i].onTransactionUpdate) {
    				listeners[i].onTransactionUpdate(xaction);
    			}
    		}
    	}
    };
});
