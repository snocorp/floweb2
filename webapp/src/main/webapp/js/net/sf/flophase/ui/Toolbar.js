/**
 * Toolbar
 */
define([
        "dojo/date",
        "dojo/date/locale"
	], function(
			date,
			locale
			) {
	
	return {
		onInit: function() {
			var cashflow = app.getCashflow();
			
			var startMonth = date.add(cashflow.getStartMonth(), "month", -1);
			var endMonth = date.add(cashflow.getEndMonth(), "month", 1);
			
			var pattern;
			if (startMonth.getFullYear() != endMonth.getFullYear()) {
				pattern = "MMMM, yyyy";
			} else {
				pattern = "MMMM";
			}
			
			var earlierMonth = locale.format(startMonth, {selector:'date', datePattern: pattern});
			var upcomingMonth = locale.format(endMonth, {selector:'date', datePattern: pattern});
			
			
			var loadEarlierButton = $('#'+floweb.toolbar.loadEarlierRef);
			loadEarlierButton.html(earlierMonth);
			loadEarlierButton.click(function() {
				app.loadEarlier(); 
				return false;
			});
			var loadUpcomingButton = $('#'+floweb.toolbar.loadUpcomingRef);
			loadUpcomingButton.html(upcomingMonth);
			loadUpcomingButton.click(function() {
				app.loadUpcoming();
				return false;
			});
			
			$('#'+floweb.toolbar.dateButtonGroupRef).show();
		},
		onTransactionLoad: function(cashflow) {
			
			var startMonth = date.add(cashflow.getStartMonth(), "month", -1);
			var endMonth = date.add(cashflow.getEndMonth(), "month", 1);
			
			var pattern;
			if (startMonth.getFullYear() != endMonth.getFullYear()) {
				pattern = "MMMM yyyy";
			} else {
				pattern = "MMMM";
			}
			
			var earlierMonth = locale.format(startMonth, {selector:'date', datePattern: pattern});
			var upcomingMonth = locale.format(endMonth, {selector:'date', datePattern: pattern});
			
			
			var loadEarlierButton = $('#'+floweb.toolbar.loadEarlierRef);
			loadEarlierButton.html(earlierMonth);
			var loadUpcomingButton = $('#'+floweb.toolbar.loadUpcomingRef);
			loadUpcomingButton.html(upcomingMonth);
		}
    };
});