/**
 * AddAccountDialog
 */
define([
        "dijit/Calendar",
        "dojo/date"
	], function(
			Calendar,
			date
			) {

	var calendar;

    return {
    	onInit: function() {
    		//calendar
    		calendar = new Calendar({
	    	        onChange: function(value) {
	    	        	dojo.byId(floweb.addTransactionDialog.dateNodeRef).value =
	    	        		date.locale.format(
	    	        				value, 
	    	        				{datePattern:'yyyy-MM-dd', selector:'date'}
	    	        			);
	    	        	
	    	        }
    	        }, 
    	        floweb.addTransactionDialog.calendarNodeRef
    	    );
    		
    		//ok button
    		$('#'+floweb.addTransactionDialog.okNodeRef).click(function() {
    			var name = $('#'+floweb.addTransactionDialog.nameNodeRef).val();
                var date = $('#'+floweb.addTransactionDialog.dateNodeRef).val();
                
                app.addTransaction(name, date);
    		});
    		
    		//cancel button
    		$('#'+floweb.addTransactionDialog.cancelNodeRef).click(function() {
    			app.hideAddTransaction();
    		});
    		
    		//jQuery UI Dialog
            $('#'+floweb.addTransactionDialog.nodeRef).dialog({ 
            	autoOpen: false,
            	title: "Add Transaction",
            	modal: true,
            	resizable: false,
            	zIndex: 200
            });
            $('#'+floweb.addTransactionDialog.nodeRef).removeClass('flo-off-screen');
    	},
    	show: function() {
            //hide any existing errors
            $('#'+floweb.addTransactionDialog.errorNodeRef).hide();
    		
            //clear the values
    		$('#'+floweb.addTransactionDialog.nameNodeRef).val('');
            $('#'+floweb.addTransactionDialog.dateNodeRef).val('');            
            calendar.set('value', null);

            $('#'+floweb.addTransactionDialog.nodeRef).dialog('open');
    	},
    	hide: function() {
    		$('#'+floweb.addTransactionDialog.nodeRef).dialog('close');
    	},
        /**
         * Displays one or more error messages in an error container.
         *
         * @param messages The array of messages to be displayed.
         */
        showError: function(messages) {
            var errorContainer = $('#'+floweb.addTransactionDialog.errorNodeRef);

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

                var list = $('#'+floweb.addTransactionDialog.errorNodeRef+' ul');
                for (var i in messages) {
                    list.append('<li>'+messages[i]+'</li>');
                }
            }

            errorContainer.show();
        }
    };
});