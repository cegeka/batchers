var jobresultService = angular.module('jobresult.services', ['ngResource']);

jobresultService
  .factory('JobResultsResource', [ '$resource',
    function ($resource) {
      return $resource('/taxcalculator/rest/jobResults', {}, {'query': {method: 'GET', isArray: true}});
    }
  ])
  .factory('RunJobResource', [ '$resource',
    function ($resource) {
      return $resource('/taxcalculator/rest/runJob/:year/:month', {}, {run: {method: 'POST', params: {year: '@year', month: '@month'}}});
    }
  ]);
