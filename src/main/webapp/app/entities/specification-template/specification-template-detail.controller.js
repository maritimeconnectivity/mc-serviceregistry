(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('SpecificationTemplateDetailController', SpecificationTemplateDetailController);

    SpecificationTemplateDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'SpecificationTemplate', 'Doc', 'Xsd'];

    function SpecificationTemplateDetailController($scope, $rootScope, $stateParams, previousState, entity, SpecificationTemplate, Doc, Xsd) {
        var vm = this;

        vm.specificationTemplate = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('mcsrApp:specificationTemplateUpdate', function(event, result) {
            vm.specificationTemplate = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
