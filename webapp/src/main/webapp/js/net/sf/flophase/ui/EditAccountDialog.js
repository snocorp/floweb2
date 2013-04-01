/**
 * EditAccountDialog
 */
define([
"dijit/registry"
	], function(
			registry
			) {

	var acctKey;

    return {
    	onInit: function() {
    		var _this = this;
    		
    		//options button shows the hidden options
    		$('#'+floweb.editAccountDialog.moreNodeRef).click(function() {
    			$('#'+floweb.editAccountDialog.advancedOptionsNodeRef).show();
    			$(this).attr('disabled', 'disabled');
    		});
    		
    		//save button saves the account
    		$('#'+floweb.editAccountDialog.saveNodeRef).click(function() {
    			_this.save();
    		});
    	},
    	show: function(acctHeaderCellNodeRef, account) {
    		var _this = this;
    		
    		//save the account key
    		acctKey = account.key;
    		
            $('#'+floweb.editAccountDialog.deleteNodeRef).unbind();

            if (app.getCashflow().getAccounts().length == 1) {
                $('#'+floweb.editAccountDialog.deleteNodeRef).addClass('disabled');
            } else {
                $('#'+floweb.editAccountDialog.deleteNodeRef).removeClass('disabled');
                $('#'+floweb.editAccountDialog.deleteNodeRef).click([ acctKey ], function(event) {
                    app.deleteAccount(event.data);
                });
            }
            
            var stopBubble = function( event ) {
                //prevent bubbling so it doesnt hide
                event.stopPropagation();
            };

            var accountNameField = $('#'+floweb.editAccountDialog.nameNodeRef);
            accountNameField.unbind();
            accountNameField.keydown(
                function( event ) {
                    if (event.keyCode == 13) {
                        _this.save();
                    } else if (event.keyCode == 27) {
                        app.hideEditAccount();
                    }
                }
            );
            accountNameField.click(stopBubble);


            //set the existing values
            accountNameField.val( account.name );
            registry.byId(floweb.editAccountDialog.negativeThresholdNodeRef).set('value', account.negativeThreshold);
            registry.byId(floweb.editAccountDialog.positiveThresholdNodeRef).set('value', account.positiveThreshold);
            
            //update the width to match
            accountNameField.css( { "width": $('#'+acctHeaderCellNodeRef).width()+7 } );

            //find the position of the clicked cell
            var acctEditor = $("#accountEditor");
            var headerCellPos = $('#'+acctHeaderCellNodeRef).position();
            acctEditor.css( {
                "top": (headerCellPos.top-1) + 'px',
                "left": (headerCellPos.left-1) + 'px'
            } );
            acctEditor.unbind();
            acctEditor.click(stopBubble);

            $(acctEditor).fadeIn(250);

            //select the account name
            accountNameField.select();

            $(document).unbind();
            $(document).click(function() { app.hideEditAccount(); });
    	},
    	hide: function() {
    		$('#'+floweb.editAccountDialog.nodeRef).fadeOut(250);
    		
    		//reset the advanced options
			$('#'+floweb.editAccountDialog.advancedOptionsNodeRef).hide();
			$('#'+floweb.editAccountDialog.moreNodeRef).removeAttr('disabled');
    	},
    	save: function() {
    		var accountName = $('#'+floweb.editAccountDialog.nameNodeRef).val();
    		var negativeThreshold = registry.byId(floweb.editAccountDialog.negativeThresholdNodeRef).get('value');
    		var positiveThreshold = registry.byId(floweb.editAccountDialog.positiveThresholdNodeRef).get('value');
			
			app.editAccount(acctKey, accountName, negativeThreshold, positiveThreshold);
    	}
    };
});