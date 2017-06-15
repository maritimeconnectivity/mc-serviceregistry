(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('DesignDeleteController',DesignDeleteController);

    DesignDeleteController.$inject = ['$uibModalInstance', 'entity', 'Design'];

    function DesignDeleteController($uibModalInstance, entity, Design) {
        var vm = this;

        vm.design = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Design.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
