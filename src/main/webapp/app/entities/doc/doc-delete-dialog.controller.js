(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('DocDeleteController',DocDeleteController);

    DocDeleteController.$inject = ['$uibModalInstance', 'entity', 'Doc'];

    function DocDeleteController($uibModalInstance, entity, Doc) {
        var vm = this;

        vm.doc = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Doc.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
