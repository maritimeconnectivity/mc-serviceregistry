'use strict';

describe('Controller Tests', function() {

    describe('Doc Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockDoc, MockSpecification, MockDesign, MockInstance;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockDoc = jasmine.createSpy('MockDoc');
            MockSpecification = jasmine.createSpy('MockSpecification');
            MockDesign = jasmine.createSpy('MockDesign');
            MockInstance = jasmine.createSpy('MockInstance');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Doc': MockDoc,
                'Specification': MockSpecification,
                'Design': MockDesign,
                'Instance': MockInstance
            };
            createController = function() {
                $injector.get('$controller')("DocDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'mcsrApp:docUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
