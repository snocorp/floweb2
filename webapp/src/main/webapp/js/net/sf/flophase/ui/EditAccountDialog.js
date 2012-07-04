/**
 * EditAccountDialog
 */
define([
        "dijit/registry"
	], function(
			registry
			) {


    return {
    	show: function(acctHeaderCellNodeRef, acctKey, acctName) {
            $('#'+floweb.editAccountDialog.deleteNodeRef).unbind();

            if (app.getCashflow().getAccounts().length == 1) {
                registry.byId(floweb.editAccountDialog.deleteNodeRef).set('disabled', true);
            } else {
                registry.byId(floweb.editAccountDialog.deleteNodeRef).set('disabled', false);
                $('#'+floweb.editAccountDialog.deleteNodeRef).click([ acctKey ], function(event) {
                    app.deleteAccount(event.data);
                });
            }

            var accountNameField = $('#'+floweb.editAccountDialog.nameNodeRef);
            accountNameField.unbind();
            accountNameField.keydown(
                [ acctKey ],
                function( event ) {
                    if (event.keyCode == 13) {
                        app.editAccountName(event.data, this.value);
                    } else if (event.keyCode == 27) {
                        app.hideEditAccount();
                    }
                }
            );


            //set the existing values
            accountNameField.val( acctName );
            accountNameField.css( { "width": $('#'+acctHeaderCellNodeRef).width()+7 } );

            //find the position of the clicked cell
            var acctEditor = $("#accountEditor");
            var headerCellPos = $('#'+acctHeaderCellNodeRef).position();
            acctEditor.css( {
                "top": (headerCellPos.top-1) + 'px',
                "left": (headerCellPos.left-1) + 'px'
            } );
            acctEditor.unbind();
            acctEditor.click(
                function( event ) {
                    //prevent bubbling so it doesnt hide
                    event.stopPropagation();
                }
            );

            $(acctEditor).fadeIn(250);

            //select the account name
            accountNameField.select();

            $(document).unbind();
            $(document).click(function() { app.hideEditAccount(); });
    	},
    	hide: function() {
    		$('#'+floweb.editAccountDialog.nodeRef).fadeOut(250);
    	}
    };
});