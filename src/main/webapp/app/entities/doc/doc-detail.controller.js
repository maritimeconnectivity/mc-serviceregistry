(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('DocDetailController', DocDetailController);

    DocDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Doc', 'Specification', 'Design', 'Instance'];

    function DocDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Doc, Specification, Design, Instance) {
        var vm = this;

        vm.doc = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('mcsrApp:docUpdate', function(event, result) {
            vm.doc = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
