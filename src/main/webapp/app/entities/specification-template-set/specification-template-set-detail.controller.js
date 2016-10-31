(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('SpecificationTemplateSetDetailController', SpecificationTemplateSetDetailController);

    SpecificationTemplateSetDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'SpecificationTemplateSet', 'SpecificationTemplate', 'Doc'];

    function SpecificationTemplateSetDetailController($scope, $rootScope, $stateParams, previousState, entity, SpecificationTemplateSet, SpecificationTemplate, Doc) {
        var vm = this;

        vm.specificationTemplateSet = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('mcsrApp:specificationTemplateSetUpdate', function(event, result) {
            vm.specificationTemplateSet = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
