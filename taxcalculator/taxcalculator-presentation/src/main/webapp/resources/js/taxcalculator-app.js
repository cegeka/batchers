var taxcalculator_app = angular.module('taxcalculatorApp', ['taxcalculatorServices', 'taxcalculatorControllers', 'taxcalculatorDirectives', 'ui.bootstrap', 'ngRoute']);

taxcalculator_app.config(['$routeProvider', function ($routeProvider) {
  $routeProvider
    .when('/employees', {templateUrl: 'partials/employees-overview.html', controller: 'EmployeesOverviewCtrl'})
    .when('/runJob', {templateUrl: 'partials/run-job.html', controller: 'RunJobCtrl'})
    .when('/jobResults', {templateUrl: 'partials/job-results.html', controller: 'JobResultsCtrl'})
    .when('/generateEmployees/:employeesCount', {templateUrl: 'partials/generate-employees.html', controller: 'GenerateEmployeesCtrl'})
    .otherwise({redirectTo: '/'});
}]);