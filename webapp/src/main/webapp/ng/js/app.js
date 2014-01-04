/*jslint browser: true, indent: 2 */
/*globals define*/
define([
  'angular',

  // Application Files
  'controllers',
  'directives'
], function (angular, controllers, directives) {
  "use strict";

  var initialize = function () {
    
    var mainModule = angular.module('floPhase', ['ui.date']);
    controllers.initialize(mainModule);
    directives.initialize(mainModule);

    angular.bootstrap(window.document, ['floPhase']);
          
  };

  return {
    initialize: initialize
  };
});