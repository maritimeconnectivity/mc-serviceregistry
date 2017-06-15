(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('DesignDetailController', DesignDetailController);

    DesignDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Design', 'Xml', 'Doc', 'SpecificationTemplate', 'Specification', 'Instance'];

    function DesignDetailController($scope, $rootScope, $stateParams, previousState, entity, Design, Xml, Doc, SpecificationTemplate, Specification, Instance) {
        var vm = this;

        vm.design = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('mcsrApp:designUpdate', function(event, result) {
            vm.design = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
