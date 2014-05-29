var taxcalculatorServices = angular.module('taxcalculatorServices', ['ngResource']);

taxcalculatorServices
  .factory('EmployeesOverviewResource', [ '$resource',
    function ($resource) {
      return $resource('/taxcalculator/rest/employees', {}, {'query': {method: 'GET', isArray: true}});
    }
  ])
  .factory('RunJobResource', [ '$resource',
    function ($resource) {
      return $resource('/taxcalculator/rest/runJob', {}, {'query': {method: 'POST'}});
    }
  ])
  .factory('GenerateEmployeesResource', ['$resource',
    function ($resource) {
      return $resource('/taxcalculator/rest/generateEmployees/', {}, {'post': {method: 'POST'}});
    }
  ]);
