'use strict';

describe('E2E: tests for the page routing', function() {

    describe('Employee Page', function() {

        beforeEach(function () {
            browser().navigateTo('/taxcalculator/#/employees');
        });

        it('should generate 1 employees', function() {
            input('GenerateEmployeesModel.employeesCount').enter('1');
            element('#generateEmployeesSubmit').click();
            expect(element('[ng-view] table tr.employeeRow').count()).toBe(1);
        });

        it('should generate 4 employees', function() {
            input('GenerateEmployeesModel.employeesCount').enter('4');
            element('#generateEmployeesSubmit').click();
            expect(element('[ng-view] table tr.employeeRow').count()).toBe(4);
        });
    });
});