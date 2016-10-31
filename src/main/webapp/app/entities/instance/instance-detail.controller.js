(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('InstanceDetailController', InstanceDetailController);

    InstanceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Instance', 'Xml', 'Doc', 'SpecificationTemplate', 'Design'];

    function InstanceDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Instance, Xml, Doc, SpecificationTemplate, Design) {
        var vm = this;

        vm.instance = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('mcsrApp:instanceUpdate', function(event, result) {
            vm.instance = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
