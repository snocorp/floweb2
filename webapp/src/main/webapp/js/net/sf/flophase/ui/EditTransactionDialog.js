/**
 * EditTransactionDialog
 */
define([
        "dijit/registry"
	], function(
			registry
			) {


    return {
    	show: function(nameCellNodeRef, xactionKey, xactionName) {
            //reset and bind the delete button
    		$('#'+floweb.editTransactionDialog.deleteNodeRef).unbind();
            $('#'+floweb.editTransactionDialog.deleteNodeRef).click([ xactionKey ], function(event) {
                app.deleteTransaction(event.data);
            });
            
            //reset and bind the copy button
            $('#'+floweb.editTransactionDialog.copyNodeRef).unbind();
            $('#'+floweb.editTransactionDialog.copyNodeRef).click([ xactionKey ], function(event) {
                app.showCopyTransaction(event.data);
            });

            var xactionNameField = $('#'+floweb.editTransactionDialog.nameNodeRef);
            xactionNameField.unbind();
            xactionNameField.keydown(
                xactionKey,
                function( event ) {
                    //return
                    if (event.keyCode == 13) {
                        app.editTransactionName(event.data, this.value);
                    }
                    //escape
                    else if (event.keyCode == 27) {
                        app.hideEditTransaction();
                    }
                }
            );

            var nameCell = $('#'+nameCellNodeRef);


            //set the existing values
            xactionNameField.val( xactionName );
            xactionNameField.css( { "width": nameCell.width()+7 } );

            //find the position of the clicked cell
            var xactionEditor = $('#'+floweb.editTransactionDialog.nodeRef);
            var nameCellPos = nameCell.position();
            xactionEditor.css( {
                "top": (nameCellPos.top-1) + 'px',
                "left": (nameCellPos.left-1) + 'px'
            } );

            xactionEditor.fadeIn(250);

            //select the transaction name
            xactionNameField.select();

            $(document.body).unbind();
            $(document.body).click(function() { app.hideEditTransaction(); });
    	},
    	hide: function() {
            $('#'+floweb.editTransactionDialog.nodeRef).fadeOut(250);
    	}
    };
});