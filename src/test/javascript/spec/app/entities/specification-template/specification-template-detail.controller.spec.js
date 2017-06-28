'use strict';

describe('Controller Tests', function() {

    describe('SpecificationTemplate Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockSpecificationTemplate, MockDoc, MockXsd;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockSpecificationTemplate = jasmine.createSpy('MockSpecificationTemplate');
            MockDoc = jasmine.createSpy('MockDoc');
            MockXsd = jasmine.createSpy('MockXsd');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'SpecificationTemplate': MockSpecificationTemplate,
                'Doc': MockDoc,
                'Xsd': MockXsd
            };
            createController = function() {
                $injector.get('$controller')("SpecificationTemplateDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'mcsrApp:specificationTemplateUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
