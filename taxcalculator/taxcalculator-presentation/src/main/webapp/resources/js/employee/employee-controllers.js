var taxcalculatorControllers = angular.module('employee.controllers', []);

taxcalculatorControllers

  .controller('EmployeesOverviewCtrl', ['$scope', '$http', 'EmployeesOverviewResource',
    function ($scope, $http, EmployeesOverviewResource) {
      $scope.refreshEmployee = function () {
        $scope.currentPage = 0;
        $scope.itemsPerPage = 10;

        $scope.setPage = function (page) {
          if (page >= 0 && page <= $scope.pageCount() - 1)
            $scope.currentPage = page;
        };

        $scope.firstPage = function () {
          $scope.currentPage = 0;
        };

        $scope.prevPage = function () {
          if ($scope.currentPage > 0) {
            $scope.currentPage--;
          }
        };

        $scope.prevPageDisabled = function () {
          return $scope.currentPage === 0 ? "disabled" : "";
        };

        $scope.nextPage = function () {
          if ($scope.currentPage < $scope.pageCount() - 1) {
            $scope.currentPage++;
          }
        };

        $scope.nextPageDisabled = function () {
          return $scope.currentPage === $scope.pageCount() - 1 ? "disabled" : "";
        };

        $scope.lastPage = function () {
          $scope.currentPage = $scope.pageCount() - 1;
        };

        $scope.pageCount = function () {
          return Math.ceil($scope.total / $scope.itemsPerPage);
        };

        $scope.pageRange = function () {
          var rangeSize = 5;
          var ret = [];
          var start;

          if (rangeSize > $scope.pageCount()) {
            rangeSize = $scope.pageCount();
          }

          start = $scope.currentPage - 2;
          if (start < 0) {
            start = 0;
          }
          if (start > $scope.pageCount() - rangeSize) {
            start = $scope.pageCount() - rangeSize;
          }

          for (var i = start; i < start + rangeSize; i++) {
            ret.push(i);
          }
          return ret;

        };


        $scope.$watch("currentPage", function (newValue, oldValue) {
          $scope.employees = EmployeesOverviewResource.query(
            {page: newValue, pageSize: $scope.itemsPerPage},
            function (successData) {
            },
            function (error) {
              $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not retrieve application overview'})
            }
          );
          $http.get('/taxcalculator/rest/employees/count').
            success(function (data) {
              $scope.total = data;
            });
        });
      };
      $scope.refreshEmployee();
    }
  ])

  .controller('GenerateEmployeesCtrl', ['$scope', 'GenerateEmployeesResource', '$routeParams',
    function ($scope, GenerateEmployeesResource, $routeParams) {
      $scope.GenerateEmployeesModel = {employeesCount: $routeParams.employeesCount};
      $scope.generateEmployees = function (employeesCount) {

        $scope.generatingEmployees = true;
        GenerateEmployeesResource.post(
          {employeesCount: employeesCount}, "",
          function (successData) {
            $scope.generatingEmployees = false;
            $scope.refreshEmployee();
          },
          function (error) {
            $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not generate employees'})
          }
        );
      }
    }
  ]);

taxcalculatorControllers.controller('EmployeeDetailsCtrl', ['$scope', '$routeParams', 'EmployeeDetailsResource', 'EmployeeTaxes',
  function ($scope, $routeParams, EmployeeDetailsResource, EmployeeTaxes) {

    EmployeeDetailsResource.query({id: $routeParams.employeeId}, function (successData) {
      $scope.employee = successData;
    });

    EmployeeTaxes.query({id: $routeParams.employeeId}, function (successData) {
      $scope.taxes = successData;
    })
  }
]);
