(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('XsdDeleteController',XsdDeleteController);

    XsdDeleteController.$inject = ['$uibModalInstance', 'entity', 'Xsd'];

    function XsdDeleteController($uibModalInstance, entity, Xsd) {
        var vm = this;

        vm.xsd = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Xsd.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
