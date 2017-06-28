'use strict';

describe('Controller Tests', function() {

    describe('Instance Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockInstance, MockXml, MockDoc, MockSpecificationTemplate, MockDesign;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockInstance = jasmine.createSpy('MockInstance');
            MockXml = jasmine.createSpy('MockXml');
            MockDoc = jasmine.createSpy('MockDoc');
            MockSpecificationTemplate = jasmine.createSpy('MockSpecificationTemplate');
            MockDesign = jasmine.createSpy('MockDesign');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Instance': MockInstance,
                'Xml': MockXml,
                'Doc': MockDoc,
                'SpecificationTemplate': MockSpecificationTemplate,
                'Design': MockDesign
            };
            createController = function() {
                $injector.get('$controller')("InstanceDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'mcsrApp:instanceUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
