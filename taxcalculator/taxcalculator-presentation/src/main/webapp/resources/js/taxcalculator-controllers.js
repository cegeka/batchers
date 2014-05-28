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
  .controller('GenerateEmployeesCtrl', ['$scope', 'GenerateEmployeesResource', '$routeParams',
    function ($scope, GenerateEmployeesResource, $routeParams) {
      $scope.GenerateEmployeesModel = {employeesCount: $routeParams.employeesCount};
      $scope.generateEmployees = function(employeesCount){
//          GenerateEmployeesResource.query(
//              {},
//              function (successData) {
//              },
//              function (error) {
//                  $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not generate employees'})
//              }
//          );
          console.log(employeesCount)
      }
    }
  ]);
