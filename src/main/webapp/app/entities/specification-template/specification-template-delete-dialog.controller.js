(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('SpecificationTemplateDeleteController',SpecificationTemplateDeleteController);

    SpecificationTemplateDeleteController.$inject = ['$uibModalInstance', 'entity', 'SpecificationTemplate'];

    function SpecificationTemplateDeleteController($uibModalInstance, entity, SpecificationTemplate) {
        var vm = this;

        vm.specificationTemplate = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            SpecificationTemplate.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
