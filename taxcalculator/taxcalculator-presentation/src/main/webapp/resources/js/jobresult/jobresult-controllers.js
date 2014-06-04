var jobresultController = angular.module('jobresult.controllers', []);

jobresultController

  .controller('JobResultsCtrl', ['$scope', 'JobResultsResource', 'RunJobResource',
    function ($scope, JobResultsResource, RunJobResource) {
      $scope.isReportReady = function (jobExecution) {
        return jobExecution != undefined && jobExecution.status != undefined && (jobExecution.status == 'FAILED' || jobExecution.status == 'COMPLETED');
      }

      $scope.formatDuration = function (millis) {
        if (millis === null) return "NOT FINISHED";
        duration = moment.duration(millis);
        return duration.hours() + "h " + duration.minutes() + "m " + duration.seconds() + "s " + duration.milliseconds() + "ms"
      }
        $scope.refreshJobResultsList = function () {
            $scope.jobResults = JobResultsResource.query(
                {},
                function (successData) {
                },
                function (error) {
                    $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not start job'})
                }
            );
        }

      $scope.runJob = function (job) {
        RunJobResource.run({year: job.jobStartParams.year, month: job.jobStartParams.month});
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
            if (message.percentageComplete) {
                $scope.percentageComplete = message.percentageComplete;
                $scope.stepName = message.stepName;
                function withNameYearAndMonth(element, index, array) {
                    if (element.jobStartParams.year == message.jobStartRequest.year
                        && element.jobStartParams.month == message.jobStartRequest.month) {
                        return true;
                    }
                    return false;
                };


                var jobResult = $scope.jobResults.filter(withNameYearAndMonth)[0];
                if (jobResult) {
                    jobResult.progress = message;
                    jobResult.progress.visible = jobResult.progress.percentageComplete < 100;
                }
            }
            $scope.$apply();
        });

        client.send("/app/launch-job", {}, JSON.stringify({ 'message': 'test' }));
      });
        $scope.refreshJobResultsList();
    }
  ]);
