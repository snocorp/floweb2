/**
 * Configuration File
 * 
 * This file defines all the configuration options used by scripts in the application.
 */
var floweb = {
	grid: {
		nodeRef:'gridContainer'
	},
	addAccountDialog: {
		nodeRef:'addAccountDialog',
		errorNodeRef:'addAccountError',
		nameNodeRef:'newAccountName',
		balanceNodeRef:'newAccountBalance',
		okNodeRef:'addAccountOk',
		cancelNodeRef:'addAccountCancel'
	},
    addTransactionDialog: {
    	nodeRef:'addTransactionDialog',
    	errorNodeRef:'addTransactionError',
    	nameNodeRef:'newTransactionName',
    	dateNodeRef:'newTransactionDate',
    	okNodeRef:'addTransactionOk',
    	cancelNodeRef:'addTransactionCancel',
    	calendarNodeRef:'newTransactionCalendar'
    },
    editAccountDialog: {
    	nodeRef:'accountEditor',
    	nameNodeRef:'accountName',
    	deleteNodeRef:'accountDelete'
    },
    editTransactionDialog: {
    	nodeRef:'xactionEditor',
    	nameNodeRef:'xactionName',
    	deleteNodeRef:'xactionDelete'
    },
    toolbar: {
    	dateButtonGroupRef:'dateButtonGroup',
    	addAccountRef:'addAccount',
    	addTransactionRef:'addTransaction',
    	loadEarlierRef:'loadEarlier',
    	loadUpcomingRef:'loadUpcoming'
    }
};