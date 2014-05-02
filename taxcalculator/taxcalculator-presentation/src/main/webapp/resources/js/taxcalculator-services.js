var maintenancetoolServices = angular.module('taxcalculatorServices', ['ngResource']);

maintenancetoolServices
    .factory('EmployeesOverviewResource', [ '$resource',
        function ($resource) {
            return $resource('/taxcalculator/rest/employees', {}, {'query': {method: 'GET', isArray: true}});
        }
    ])
