'use strict';

describe('Controller Tests', function() {

    describe('SpecificationTemplateSet Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockSpecificationTemplateSet, MockSpecificationTemplate, MockDoc;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockSpecificationTemplateSet = jasmine.createSpy('MockSpecificationTemplateSet');
            MockSpecificationTemplate = jasmine.createSpy('MockSpecificationTemplate');
            MockDoc = jasmine.createSpy('MockDoc');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'SpecificationTemplateSet': MockSpecificationTemplateSet,
                'SpecificationTemplate': MockSpecificationTemplate,
                'Doc': MockDoc
            };
            createController = function() {
                $injector.get('$controller')("SpecificationTemplateSetDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'mcsrApp:specificationTemplateSetUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
