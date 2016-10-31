(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('XsdDetailController', XsdDetailController);

    XsdDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Xsd'];

    function XsdDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Xsd) {
        var vm = this;

        vm.xsd = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('mcsrApp:xsdUpdate', function(event, result) {
            vm.xsd = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
