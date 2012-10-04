/**
 * AddAccountDialog
 */
define([
        "dijit/registry"
	], function(
			registry
			) {


    return {
    	onInit: function() {
    		//ok button
    		$('#'+floweb.addAccountDialog.okNodeRef).click(function() {
    			var name = $('#'+floweb.addAccountDialog.nameNodeRef).val();
                var balance = registry.byId(floweb.addAccountDialog.balanceNodeRef).get('value');
                
                app.addAccount(name, balance);
    		});
    		
    		//cancel button
    		$('#'+floweb.addAccountDialog.cancelNodeRef).click(function() {
    			app.hideAddAccount();
    		});
    		
    		//jQuery UI dialog
            $('#'+floweb.addAccountDialog.nodeRef).dialog({ 
            	autoOpen: false,
            	title: "Add Account",
            	modal: true,
            	resizable: false,
            	zIndex: 200
            });
            $('#'+floweb.addAccountDialog.nodeRef).removeClass('flo-off-screen');
    	},
    	show: function() {
            //hide any existing errors
            $('#'+floweb.addAccountDialog.errorNodeRef).hide();

            //reset the values
            $('#'+floweb.addAccountDialog.nameNodeRef).val('');
            var balanceInput = registry.byId(floweb.addAccountDialog.balanceNodeRef);
            balanceInput.set('value', 0.0);

            $("#"+floweb.addAccountDialog.nodeRef).dialog('open');
    	},
    	hide: function() {

            //reset the values
            $('#'+floweb.addAccountDialog.nameNodeRef).val('');
            var balanceInput = registry.byId(floweb.addAccountDialog.balanceNodeRef);
            balanceInput.set('value', 0.0);
            
    		$('#'+floweb.addAccountDialog.nodeRef).dialog('close');
    	},
        /**
         * Displays one or more error messages in an error container.
         *
         * @param messages The array of messages to be displayed.
         */
        showError: function(messages) {
            var errorContainer = $('#'+floweb.addAccountDialog.errorNodeRef);

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

                var list = $('#'+floweb.addAccountDialog.errorNodeRef+' ul');
                for (var i in messages) {
                    list.append('<li>'+messages[i]+'</li>');
                }
            }

            errorContainer.show();
        }
    };
});