/*jslint browser: true, indent: 2 */
/*globals define*/
define([
  'AppController'
], function (app) {
  "use strict";

  var initialize = function (angModule) {
    angModule.controller('AppController', app);
  };


  return {
    initialize: initialize
  };
});