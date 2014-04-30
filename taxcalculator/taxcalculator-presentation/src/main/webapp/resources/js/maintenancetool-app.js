var maintenancetool_app = angular.module('maintenancetool_app', ['maintenancetoolServices','maintenancetoolControllers', 'maintenancetool_directives', 'ui.bootstrap', 'ngRoute']);

maintenancetool_app.config(['$routeProvider', function($routeProvider) {
	  $routeProvider
	  	  .when('/applications', {templateUrl: 'partials/application-overview.html', controller: 'ApplicationOverviewCtrl'})
          .when('/applications/:applicationName', {templateUrl: 'partials/application-detail.html', controller: 'ApplicationDetailCtrl'})
          .when('/users', {templateUrl: 'partials/user-overview.html', controller: 'UserOverviewCtrl'})
	      .otherwise({redirectTo: '/applications'});
	}]);