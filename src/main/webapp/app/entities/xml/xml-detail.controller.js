(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('XmlDetailController', XmlDetailController);

    XmlDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Xml', 'Base64'];

    function XmlDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Xml, Base64) {
        var vm = this;

        vm.xml = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.Base64 = Base64;

        var unsubscribe = $rootScope.$on('mcsrApp:xmlUpdate', function(event, result) {
            vm.xml = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
