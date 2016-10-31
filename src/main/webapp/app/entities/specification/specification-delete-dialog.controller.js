(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('SpecificationDeleteController',SpecificationDeleteController);

    SpecificationDeleteController.$inject = ['$uibModalInstance', 'entity', 'Specification'];

    function SpecificationDeleteController($uibModalInstance, entity, Specification) {
        var vm = this;

        vm.specification = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Specification.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
