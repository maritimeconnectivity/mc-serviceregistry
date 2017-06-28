'use strict';

describe('Controller Tests', function() {

    describe('Specification Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockSpecification, MockXml, MockDoc, MockSpecificationTemplate;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockSpecification = jasmine.createSpy('MockSpecification');
            MockXml = jasmine.createSpy('MockXml');
            MockDoc = jasmine.createSpy('MockDoc');
            MockSpecificationTemplate = jasmine.createSpy('MockSpecificationTemplate');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Specification': MockSpecification,
                'Xml': MockXml,
                'Doc': MockDoc,
                'SpecificationTemplate': MockSpecificationTemplate
            };
            createController = function() {
                $injector.get('$controller')("SpecificationDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'mcsrApp:specificationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
