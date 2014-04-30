var maintenancetoolServices = angular.module('maintenancetoolServices', ['ngResource']);

maintenancetoolServices
    .factory('UserResource', [ '$resource',
        function($resource) {
            return $resource('/maintenancetool/rest/private/users/:userName',{userName:'@userName'},{
                'query': {method:'GET', isArray: true}
            });
        }
    ])
    .factory('LoginResource', [ '$resource',
        function($resource) {
            return $resource('/maintenancetool/rest/public/login',{userName:'@userName'},{
                'login': {method:'POST', url:'/maintenancetool/rest/public/login'}
            });
        }
    ])
    .factory('EnvironmentResource', [ '$resource',
        function($resource) {
            return $resource('/maintenancetool/rest/public/environment',{},{});
        }
    ])
    .factory('ApplicationOverviewResource', [ '$resource',
        function ($resource) {
            return $resource('/maintenancetool/rest/private/applications', {}, {'query': {method: 'GET', isArray: true}});
        }
    ])
    .factory('ApplicationDetailResource', [ '$resource',
        function ($resource) {
            var Config = $resource('/maintenancetool/rest/private/v1/app/:applicationName/configs', {}, {'get': {method: 'GET', isArray: true}});
            Config.prototype.isActive = function () {
                return this.enabled && this.startDate < new Date().toISOString() && new Date().toISOString() < this.endDate;
            };
            Config.prototype.isNew = function () {
                return !this.createdOn;
            };
            Config.prototype.validateKey = function (applicationConfigs) {
                var errorFields = [];
                var keyAlreadyInUse = function(key,applicationConfigs){
                    return _.find(applicationConfigs,function(applicationConfig) {
                        return applicationConfig.key === key;
                    });
                };
                if (!this.key || this.key.length < 5) {
                    errorFields.push(" 'Key' field should be at least five characters long");
                }
                if (this.isNew() && keyAlreadyInUse(this.key,applicationConfigs)) {
                    errorFields.push(" Key is already in use");
                }
                return errorFields;
            };
            return Config;
        }
    ])
    .factory('DateTimePicker',
    function () {
        return {
            createNew: function (dateString) {
                var formatFunction = function (dateString) {
                    return new Date(
                        dateString.substr(0, 4),        // year
                        dateString.substr(5, 2) - 1,    // month
                        dateString.substr(8, 2),        // day
                        dateString.substr(11, 2),       // hour
                        dateString.substr(14, 2),       // minute
                        dateString.substr(17, 2));      // seconds
                }

                return {
                    date: formatFunction(dateString),
                    format: 'yyyy-MM-dd',
                    hourStep: 1,
                    minuteStep: 10,
                    opened: false,
                    min: '1000-01-01 00:00:00',
                    max: '9999-12-31 23:59:59',
                    dateOptions: {
                        'year-format': "'yy'",
                        'starting-day': 1,
                        'show-weeks': false
                    },

                    today: function () {
                        this.date = new Date();
                    },
                    setToMin: function () {
                        this.date = formatFunction(this.min);
                    },
                    setToMax: function () {
                        this.date = formatFunction(this.max);
                    },
                    open: function ($event) {
                        $event.preventDefault();
                        $event.stopPropagation();

                        this.opened = true;
                    },
                    isValidDate: function () {
                        if (Object.prototype.toString.call(this.date) !== "[object Date]") {
                            return false;
                        }
                        return !isNaN(this.date.getTime());
                    },
                    isAfter: function (otherDateTimePicker) {
                        if (this.isValidDate() && otherDateTimePicker.isValidDate()) {
                            return this.date > otherDateTimePicker.date;
                        }
                        return false;
                    },
                    toJsonFormat: function () {
                        var hourOffset = this.date.getTimezoneOffset() / 60;
                        this.date.setHours(this.date.getHours() - hourOffset);
                        return this.date.toJSON().replace("Z", "");
                    }

                }

            }

        }
    })
    .factory('DateTimeIntervalPicker', [ 'DateTimePicker',
    function (DateTimePicker) {
        return {
            createNew: function(startDateString, endDateString) {
                return {
                    startDate: DateTimePicker.createNew(startDateString),
                    endDate: DateTimePicker.createNew(endDateString),
                    startDateToJsonFormat: function () {
                      return this.startDate.toJsonFormat();
                    },
                    endDateToJsonFormat: function () {
                      return this.endDate.toJsonFormat();
                    },
                    validateInterval: function() {
                        var errorFields = [];
                        if (!this.startDate.isValidDate()) {
                            errorFields.push(" 'From' field has an invalid date");
                        }
                        if (!this.endDate.isValidDate()) {
                            errorFields.push(" 'Until' field has an invalid date");
                        }
                        if (this.startDate.isValidDate() && this.endDate.isValidDate()) {
                            if (!this.endDate.isAfter(this.startDate)) {
                                errorFields.push(" 'Until' date has to be after the 'From' date");
                            }
                        }
                        return errorFields;
                    }
                }
            }
        }
    }]);

