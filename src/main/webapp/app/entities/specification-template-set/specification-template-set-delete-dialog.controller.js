(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('SpecificationTemplateSetDeleteController',SpecificationTemplateSetDeleteController);

    SpecificationTemplateSetDeleteController.$inject = ['$uibModalInstance', 'entity', 'SpecificationTemplateSet'];

    function SpecificationTemplateSetDeleteController($uibModalInstance, entity, SpecificationTemplateSet) {
        var vm = this;

        vm.specificationTemplateSet = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            SpecificationTemplateSet.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
