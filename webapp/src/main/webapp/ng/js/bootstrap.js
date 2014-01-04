/*jslint browser: true, indent: 2 */
/*globals require*/

require.config({
  paths: {
    'jquery': 'https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min',
    'jquery-ui': 'https://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min',
    'twitter-bootstrap': 'https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.0/js/bootstrap.min',
    'angular': 'https://ajax.googleapis.com/ajax/libs/angularjs/1.2.0-rc.2/angular.min',
    'angular-ui-date': 'libs/angular-ui-date/date'
  },
  shim: {
    'angular': {
      exports: 'angular'
    },
    'twitter-bootstrap': ['jquery'],
    'angular-ui-date': ['angular']
  }
});

require([
  // Standard Libs
  'require',
  'jquery',
  'angular',
  'jquery-ui',
  'twitter-bootstrap',
  'angular-ui-date'
], function (require, $, angular) {
  'use strict';

  require(['app'], function (App) {
  	
    //done first to avoid any DOM manipulation after angular initializes
    $("#addAccountDialog").dialog({
      autoOpen: false,
      modal: true
    });
    $("#addTransactionDialog").dialog({
      autoOpen: false,
      modal: true
    });
    $("#copyTransactionDialog").dialog({
      autoOpen: false,
      modal: true
    });
    
    App.initialize();

  });

});