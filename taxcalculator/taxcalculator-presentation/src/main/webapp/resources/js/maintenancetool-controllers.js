var maintenancetoolControllers = angular.module('maintenancetoolControllers', []);

maintenancetoolControllers
    .controller('LoginCtrl', ['$scope', 'LoginResource', 'EnvironmentResource',
        function ($scope, LoginResource, EnvironmentResource) {
            $scope.user = {};

            $scope.login = function(userToLogin) {
                var user = new LoginResource(userToLogin);
                user.$login({},function(response){
                    if(response.succeeded) {
                        window.location.href = 'applications.html';
                    } else {
                        $scope.response = response;
                    }
                });
            };

            EnvironmentResource.get({}, function(response) {
                $scope.environment = response.env;
            });
        }
    ])
    .controller('MaintenanceConfigRootCtrl', ['$scope', '$timeout', '$location', 'EnvironmentResource',
        function ($scope, $timeout, $location, EnvironmentResource) {
            $scope.$on('alert', function (event, alert) {
                alert['show'] = true;
                $scope.alert = alert;
                $timeout(function () {
                    alert['show'] = false;
                }, 5000);
            });

            EnvironmentResource.get({}, function(response) {
                $scope.environment = response.env;
            });

            $scope.isActive = function(route) {
                return $location.path().startsWith(route);
            }
        }
    ])
    .controller('UserOverviewCtrl', ['$scope', '$modal', 'UserResource',
        function ($scope, $modal, UserResource) {
            $scope.loadUsers = function() {
                $scope.users = UserResource.query({},
                    function (successData) {},
                    function (error) {
                        $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not retrieve user overview'});
                    }
                );
            };
            $scope.loadUsers();

            $scope.user = {};
            $scope.saveUser = function(userToSave) {
                var user = new UserResource(userToSave);
                user.$save({},function(response){
                    if(response.succeeded) {
                        $scope.$emit("alert", {'alertClass': 'alert-success', 'message': 'User successfully created'});
                        $scope.loadUsers();
                        $scope.user = {};
                    } else {
                        $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': response.message});
                    }
                });
            };

            $scope.deleteUser = function(userToDelete) {
                var modalInstance = $modal.open({
                    templateUrl: 'deleteUser.html',
                    controller: UserDeleteModalCtrl,
                    resolve: {
                        user: function () {
                            return userToDelete;
                        }
                    }
                });

                modalInstance.result.then(function (deleteUserOk) {
                    if(deleteUserOk) {
                        var user = new UserResource(userToDelete);
                        user.$delete({},function(response){
                            if(response.succeeded) {
                                $scope.$emit("alert", {'alertClass': 'alert-success', 'message': 'User successfully deleted'});
                                $scope.loadUsers();
                            } else {
                                $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': response.message});
                            }
                        });
                    }
                });
            }

            var UserDeleteModalCtrl = function ($scope, $modalInstance, user) {

                $scope.user = user;
                $scope.ok = function () {
                    $modalInstance.close(true);
                };

                $scope.cancel = function () {
                    $modalInstance.close(false);
                };
            };
        }
    ])
    .controller('ApplicationOverviewCtrl', ['$scope', 'ApplicationOverviewResource',
        function ($scope, ApplicationOverviewResource) {
            $scope.applications = ApplicationOverviewResource.query(
                {},
                function(successData) {},
                function(error) {
                    $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not retrieve application overview'})
                }
            );
        }
    ])
    .controller('ModalInstanceCtrl', ['$scope', '$modalInstance', 'applicationConfig', 'applicationConfigs', '$timeout', 'DateTimeIntervalPicker',
        function ($scope, $modalInstance, applicationConfig, applicationConfigs, $timeout, DateTimeIntervalPicker) {
            $scope.applicationConfig = applicationConfig;

            $scope.dateTimeIntervalPicker = DateTimeIntervalPicker.createNew(applicationConfig.startDate, applicationConfig.endDate);

            $scope.$on('alert', function (event, alert) {
                alert['show'] = true;
                $scope.alert = alert;
                $timeout(function () {
                    alert['show'] = false;
                }, 5000);
            });

            $scope.ok = function () {
                var errorFields = $scope.dateTimeIntervalPicker.validateInterval();
                errorFields = errorFields.concat($scope.applicationConfig.validateKey(applicationConfigs));
                if (errorFields.length === 0) {
                    $scope.applicationConfig.startDate = $scope.dateTimeIntervalPicker.startDateToJsonFormat();
                    $scope.applicationConfig.endDate = $scope.dateTimeIntervalPicker.endDateToJsonFormat();
                    $modalInstance.close();
                } else {
                    $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': '' + errorFields});
                }
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };

        }
    ])
    .controller('ApplicationDetailCtrl', ['$scope', '$routeParams', '$modal', 'ApplicationDetailResource',
        function ($scope, $routeParams, $modal, ApplicationDetailResource) {
            var applicationDetailController = this;
            this.onUpdateConfig = function (applicationConfig, originalApplicationConfig) {
                if (!angular.equals(applicationConfig, originalApplicationConfig)) {
                    applicationConfig.$save(
                        {applicationName: $routeParams.applicationName},
                        function (successData) {
                            $scope.$emit("alert", {'alertClass': 'alert-success', 'message': 'Changes for ' + applicationConfig.key + ' saved successfully'});
                            $scope.loadApplicationConfigs();
                        },
                        function (error) {
                            $scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not save changes for ' + applicationConfig.key})
                        }
                    );
                } else {
                    $scope.$emit("alert", {'alertClass': 'alert-info', 'message': 'No changes for ' + applicationConfig.key});
                }
            };

            $scope.applicationName = $routeParams.applicationName;
            $scope.loadApplicationConfigs = function() {
                $scope.applicationConfigs = ApplicationDetailResource.get(
                    {applicationName: $routeParams.applicationName},
                    function(successData) {},
                    function(error) {$scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not retrieve details for ' + $scope.applicationName})}
                )};
            $scope.loadApplicationConfigs();

            $scope.open = function(applicationConfig) {
                var originalApplicationConfig = angular.copy(applicationConfig);
                var modalInstance = $modal.open({
                    templateUrl: 'editApplicationDetails.html',
                    controller: 'ModalInstanceCtrl',
                    resolve: {
                        applicationConfig: function () { return applicationConfig;},
                        applicationConfigs: function () { return $scope.applicationConfigs;}
                    }
                });
                modalInstance.result.then(function() {applicationDetailController.onUpdateConfig(applicationConfig, originalApplicationConfig)});
            };
            $scope.createNew = function(applicationName) {
                var applicationConfig = new ApplicationDetailResource(   {
                    "application": applicationName,
                    "enabled": false,
                    "endDate": "9999-12-31T23:59:59.000",
                    "key": "",
                    "messageEN": "",
                    "messageFR": "",
                    "messageNL": "",
                    "startDate": "1000-01-01T00:00:00.000"
                });
                var modalInstance = $modal.open({
                    templateUrl: 'editApplicationDetails.html',
                    controller: 'ModalInstanceCtrl',
                    resolve: {
                        applicationConfig: function () { return applicationConfig; },
                        applicationConfigs: function () { return $scope.applicationConfigs;}
                    }
                });
                modalInstance.result.then(
                    function() {
                        applicationConfig
                            .$save(
                                {applicationName: $routeParams.applicationName},
                                function(successData) {
                                    $scope.$emit("alert", {'alertClass': 'alert-success', 'message': 'New setting ' + applicationConfig.key + ' saved successfully'});
                                    $scope.loadApplicationConfigs();
                                },
                                function(error) {$scope.$emit("alert", {'alertClass': 'alert-danger', 'message': 'Could not save new setting ' + applicationConfig.key})}
                        );
                    });
            };
        }
    ]);