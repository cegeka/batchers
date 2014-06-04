var jobresultController = angular.module('jobresult.controllers', []);

jobresultController

  .controller('JobResultsCtrl', ['$scope', 'JobResultsResource', 'RunJobResource',
    function ($scope, JobResultsResource, RunJobResource) {
      $scope.isReportReady = function (jobExecution) {
        return jobExecution != undefined && jobExecution.status != undefined && (jobExecution.status == 'FAILED' || jobExecution.status == 'COMPLETED');
      };

      $scope.formatDuration = function (millis) {
        if (millis === null) return "NOT FINISHED";
        duration = moment.duration(millis);
        return duration.hours() + "h " + duration.minutes() + "m " + duration.seconds() + "s " + duration.milliseconds() + "ms"
      };
      $scope.jobResults = JobResultsResource.query(
        {},
        function (successData) {
        },
        function (error) {
          $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not start job'})
        }
      );

      $scope.runJob = function (job) {
        RunJobResource.run({year: job.jobStartParams.year, month: job.jobStartParams.month});
      }
    }
  ]);
