var taxcalculatorControllers = angular.module('taxcalculatorControllers', []);

taxcalculatorControllers

  .controller('EmployeesOverviewCtrl', ['$scope', 'EmployeesOverviewResource',
    function ($scope, EmployeesOverviewResource) {
      $scope.employees = EmployeesOverviewResource.query(
        {},
        function (successData) {
        },
        function (error) {
          $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not retrieve application overview'})
        }
      );
    }
  ])

  .controller('RunJobCtrl', ['$scope', 'RunJobResource',
    function ($scope, RunJobResource) {
      RunJobResource.query(
        {},
        function (successData) {
        },
        function (error) {
          $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not start job'})
        }
      );
    }
  ])

  .controller('JobResultsCtrl', ['$scope', 'JobResultsResource',
    function ($scope, JobResultsResource) {
      $scope.jobResults = JobResultsResource.query(
        {},
        function (successData) {
        },
        function (error) {
          $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not start job'})
        }
      );
    }
  ])
