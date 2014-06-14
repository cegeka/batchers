var taxcalculator_app = angular.module('taxcalculatorApp', ['employee', 'jobresult', 'taxcalculatorDirectives', 'ui.bootstrap', 'ngRoute']);

taxcalculator_app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/', {templateUrl: 'partials/index.html'})
        .when('/employees', {templateUrl: 'partials/employees-overview.html', controller: 'EmployeesOverviewCtrl'})
        .when('/jobResults', {templateUrl: 'partials/job-results.html', controller: 'JobResultsCtrl'})
        .when('/generateEmployees/:employeesCount', {templateUrl: 'partials/generate-employees.html', controller: 'GenerateEmployeesCtrl'})
        .when('/employeeDetails/:employeeId', {templateUrl: 'partials/employee-details.html', controller: 'EmployeeDetailsCtrl'})
        .otherwise({redirectTo: '/'});
}]);

taxcalculator_app.run(function($rootScope, $location, $anchorScroll, $routeParams) {
    //when the route is changed scroll to the proper element.
    $rootScope.$on('$routeChangeSuccess', function(newRoute, oldRoute) {
        $location.hash($routeParams.scrollTo);
        $anchorScroll();
    });
});

taxcalculator_app.filter('shortMonth', function () {
    var shortMonths = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'];
    return function (input) {
        return shortMonths[input - 1];
    }
})
