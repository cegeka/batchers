var jobresultController = angular.module('jobresult.controllers', []);

jobresultController

  .controller('JobResultsCtrl', ['$scope', 'JobResultsResource', 'RunJobResource', 'alertsManager',
    function ($scope, JobResultsResource, RunJobResource, alertsManager) {
      $scope.alerts = alertsManager.alerts;

      $scope.isReportReady = function (jobExecution) {
        return jobExecution != undefined && jobExecution.status != undefined && (jobExecution.status == 'FAILED' || jobExecution.status == 'COMPLETED');
      }

      $scope.formatDuration = function (millis) {
        if (millis === null) return "NOT FINISHED";
        duration = moment.duration(millis);
        return duration.hours() + "h " + duration.minutes() + "m " + duration.seconds() + "s " + duration.milliseconds() + "ms"
      }
      $scope.refreshJobResultsList = function () {
        JobResultsResource.query(
          {},
          function (successData) {
            $scope.jobResults = successData;
          },
          function (error) {
            alertsManager.addAlert('Could not start job', 'alert-danger');
          }
        );
      }

      $scope.runJob = function (job) {
        RunJobResource.run({year: job.jobStartParams.year, month: job.jobStartParams.month},
          function(data) {}, function() {
            alertsManager.addAlert('Web service down. Could not start job', 'alert-danger');
          });
      }

      $scope.model = {
        connected: false,
        transport: 'unknown',
        messages: []
      };

      var socket = new SockJS('/taxcalculator/rest/jobinfo');
      var client = Stomp.over(socket);

      client.connect({}, function (frame) {
        console.log("connected");
        $scope.model.connected = true;
        $scope.model.transport = "sockjs";
        $scope.$apply();

        client.subscribe("/jobinfo-updates", function (message) {
          var message = angular.fromJson(message.body);
          if (message.status) {
            $scope.refreshJobResultsList();
          }
          if (message.stepName) {
            $scope.percentageComplete = message.percentageComplete;
            $scope.stepName = message.stepName;
            function withNameYearAndMonth(element, index, array) {
              if (element.jobStartParams.year == message.jobStartParams.year
                && element.jobStartParams.month == message.jobStartParams.month) {
                return true;
              }
              return false;
            };


            var jobResult = $scope.jobResults.filter(withNameYearAndMonth)[0];
            if (jobResult) {
              jobResult.progress = message;
              jobResult.progress.visible = jobResult.progress.percentageComplete <= 100;
            }
          }
          $scope.$apply();
        });

        client.send("/app/launch-job", {}, JSON.stringify({ 'message': 'test' }));
      });
      $scope.refreshJobResultsList();
    }
  ]);
