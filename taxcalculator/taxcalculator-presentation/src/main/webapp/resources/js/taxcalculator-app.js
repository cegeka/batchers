var taxcalculator_app = angular.module('taxcalculatorApp', ['taxcalculatorServices', 'taxcalculatorControllers', 'taxcalculatorDirectives', 'ui.bootstrap', 'ngRoute']);

taxcalculator_app.config(['$routeProvider', function ($routeProvider) {
  $routeProvider
    .when('/employees', {templateUrl: 'partials/employees-overview.html', controller: 'EmployeesOverviewCtrl'})
    .when('/jobResults', {templateUrl: 'partials/job-results.html', controller: 'JobResultsCtrl'})
    .when('/generateEmployees/:employeesCount', {templateUrl: 'partials/generate-employees.html', controller: 'GenerateEmployeesCtrl'})
    .when('/employeeDetails/:employeeId', {templateUrl: 'partials/employee-details.html', controller: 'EmployeeDetailsCtrl'})
    .otherwise({redirectTo: '/'});
}]);