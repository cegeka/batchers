var maintenancetool_app = angular.module('taxcalculatorApp', ['taxcalculatorServices','taxcalculatorControllers', 'taxcalculatorDirectives', 'ui.bootstrap', 'ngRoute']);

maintenancetool_app.config(['$routeProvider', function($routeProvider) {
	  $routeProvider
	  	  .when('/employees', {templateUrl: 'partials/employees-overview.html', controller: 'EmployeesOverviewCtrl'})
	      .otherwise({redirectTo: '/'});
	}]);