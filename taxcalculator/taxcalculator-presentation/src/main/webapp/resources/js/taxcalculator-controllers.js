var maintenancetoolControllers = angular.module('taxcalculatorControllers', []);

maintenancetoolControllers

    .controller('EmployeesOverviewCtrl', ['$scope', 'EmployeesOverviewResource',
        function ($scope, EmployeesOverviewResource) {
            $scope.employees = EmployeesOverviewResource.query(
                {},
                function(successData) {},
                function(error) {
                    $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not retrieve application overview'})
                }
            );
        }
    ])
