/**
 * Import Cash Flow Dialog
 */
define([
	], function(
			) {

    return {
    	onInit: function() {
    		//ok button
    		$('#'+floweb.importCashflowDialog.okNodeRef).click(function() {
    			var cashflowData = $('#'+floweb.importCashflowDialog.dataNodeRef).val();
    			app.importCashFlow(cashflowData);
    		});
    		
    		//cancel button
    		$('#'+floweb.importCashflowDialog.cancelNodeRef).click(function() {
    			app.hideImportCashFlow();
    		});
            
            app.hideImportCashFlow();
            $('#'+floweb.importCashflowDialog.nodeRef).removeClass('flo-off-screen');
    	},
    	show: function() {
            //hide any existing errors
            $('#'+floweb.importCashflowDialog.errorNodeRef).hide();

            //reset the values
            $('#'+floweb.importCashflowDialog.dataNodeRef).val('');

            $("#"+floweb.importCashflowDialog.nodeRef).show();
    	},
    	hide: function() {

            //reset the values
    		$('#'+floweb.importCashflowDialog.dataNodeRef).val('');
            
    		$('#'+floweb.importCashflowDialog.nodeRef).hide();
    	},
        /**
         * Displays one or more error messages in an error container.
         *
         * @param messages The array of messages to be displayed.
         */
        showError: function(messages) {
            var errorContainer = $('#'+floweb.importCashflowDialog.errorNodeRef);

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

                var list = $('#'+floweb.importCashflowDialog.errorNodeRef+' ul');
                for (var i in messages) {
                    list.append('<li>'+messages[i]+'</li>');
                }
            }

            errorContainer.show();
        },
        /**
         * Displays the import progress.
         * @param done The number done
         * @param total The total to be done
         */
        showProgress: function(done, total) {
        	if (total < 0) {
        		$('#'+floweb.importCashflowDialog.progressNodeRef).hide();
        	} else {
        		$('#'+floweb.importCashflowDialog.progressNodeRef).show();
            	
            	if (total == 0 || done >= total) {
            		$('#'+floweb.importCashflowDialog.progressBarNodeRef).css('width', '100%');
            	} else {
            		var percent = done / total;
            		
            		$('#'+floweb.importCashflowDialog.progressBarNodeRef).css('width', (percent*100)+'%');
            	}
        	}
        }
    };
});