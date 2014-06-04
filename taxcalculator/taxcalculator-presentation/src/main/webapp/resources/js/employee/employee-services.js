var employeeService = angular.module('employee.services', ['ngResource']);

employeeService
  .factory('EmployeesOverviewResource', [ '$resource',
    function ($resource) {
      return $resource('/taxcalculator/rest/employees', {page: 0, pageSize: 10}, {'query': {method: 'GET', params: ["page", "pageSize"], isArray: true}});
    }
  ])
  .factory('GenerateEmployeesResource', ['$resource',
    function ($resource) {
      return $resource('/taxcalculator/rest/generateEmployees/', {}, {'post': {method: 'POST'}});
    }
  ]).factory('EmployeeDetailsResource', ['$resource', function ($resource) {
    return $resource('/taxcalculator/rest/employees/:id/details', {}, {query: {method: 'GET', params: {id: '@id'}}})
  }]);
