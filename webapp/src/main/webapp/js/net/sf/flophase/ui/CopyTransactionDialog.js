/**
 * CopyTransactionDialog
 */
define([
        "dijit/Calendar",
        "dojo/date",
        "dojo/date/stamp"
	], function(
			Calendar,
			date,
			stamp
			) {

	var calendar;

    return {
    	onInit: function() {
    		//calendar
    		calendar = new Calendar({
	    	        onChange: function(value) {
	    	        	dojo.byId(floweb.copyTransactionDialog.dateNodeRef).value =
	    	        		date.locale.format(
	    	        				value, 
	    	        				{datePattern:'yyyy-MM-dd', selector:'date'}
	    	        			);
	    	        	
	    	        }
    	        }, 
    	        floweb.copyTransactionDialog.calendarNodeRef
    	    );
    		
    		//ok button
    		$('#'+floweb.copyTransactionDialog.okNodeRef).click(function() {
    			var key = $('#'+floweb.copyTransactionDialog.keyNodeRef).val();
    			var name = $('#'+floweb.copyTransactionDialog.nameNodeRef).val();
                var date = $('#'+floweb.copyTransactionDialog.dateNodeRef).val();
                
                app.copyTransaction(key, name, date);
    		});
    		
    		//cancel button
    		$('#'+floweb.copyTransactionDialog.cancelNodeRef).click(function() {
    			app.hideCopyTransaction();
    		});
    		
    		//jQuery UI Dialog
            $('#'+floweb.copyTransactionDialog.nodeRef).dialog({ 
            	autoOpen: false,
            	title: "Copy Transaction",
            	modal: true,
            	resizable: false,
            	zIndex: 200
            });
            $('#'+floweb.copyTransactionDialog.nodeRef).removeClass('flo-off-screen');
    	},
    	/**
    	 * Shows the copy transaction dialog.
    	 * @param xactionKey The transaction key to be copied.
    	 */
    	show: function(xactionKey) {
            //hide any existing errors
            $('#'+floweb.copyTransactionDialog.errorNodeRef).hide();
            
            //find the transaction to copy
            var xaction = app.getCashflow().getTransaction(xactionKey);
    		
            //set the values
            $('#'+floweb.copyTransactionDialog.keyNodeRef).val(xactionKey);
    		$('#'+floweb.copyTransactionDialog.nameNodeRef).val(xaction.details.name);
            $('#'+floweb.copyTransactionDialog.dateNodeRef).val(xaction.details.date);            
            calendar.set('value', stamp.fromISOString(xaction.details.date));

            $('#'+floweb.copyTransactionDialog.nodeRef).dialog('open');
    	},
    	hide: function() {
    		$('#'+floweb.copyTransactionDialog.nodeRef).dialog('close');
    	},
        /**
         * Displays one or more error messages in an error container.
         *
         * @param messages The array of messages to be displayed.
         */
        showError: function(messages) {
            var errorContainer = $('#'+floweb.copyTransactionDialog.errorNodeRef);

            errorContainer.empty();

            if (typeof messages == "string") {
            	errorContainer.text(messages);
            }
            else if (typeof messages == "undefined") {
            	errorContainer.text("Unknown error occured");
            }
            else if (messages.length == 1) {
                errorContainer.text(messages[0]);
            } else if (messages.length > 1) {
                errorContainer.append("<ul></ul>");

                var list = $('#'+floweb.copyTransactionDialog.errorNodeRef+' ul');
                for (var i in messages) {
                    list.append('<li>'+messages[i]+'</li>');
                }
            }

            errorContainer.show();
        }
    };
});