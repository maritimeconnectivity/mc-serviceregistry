(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('SpecificationDetailController', SpecificationDetailController);

    SpecificationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Specification', 'Xml', 'Doc', 'SpecificationTemplate'];

    function SpecificationDetailController($scope, $rootScope, $stateParams, previousState, entity, Specification, Xml, Doc, SpecificationTemplate) {
        var vm = this;

        vm.specification = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('mcsrApp:specificationUpdate', function(event, result) {
            vm.specification = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
