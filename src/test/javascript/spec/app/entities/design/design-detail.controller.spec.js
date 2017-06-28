'use strict';

describe('Controller Tests', function() {

    describe('Design Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockDesign, MockXml, MockDoc, MockSpecificationTemplate, MockSpecification, MockInstance;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockDesign = jasmine.createSpy('MockDesign');
            MockXml = jasmine.createSpy('MockXml');
            MockDoc = jasmine.createSpy('MockDoc');
            MockSpecificationTemplate = jasmine.createSpy('MockSpecificationTemplate');
            MockSpecification = jasmine.createSpy('MockSpecification');
            MockInstance = jasmine.createSpy('MockInstance');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Design': MockDesign,
                'Xml': MockXml,
                'Doc': MockDoc,
                'SpecificationTemplate': MockSpecificationTemplate,
                'Specification': MockSpecification,
                'Instance': MockInstance
            };
            createController = function() {
                $injector.get('$controller')("DesignDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'mcsrApp:designUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
