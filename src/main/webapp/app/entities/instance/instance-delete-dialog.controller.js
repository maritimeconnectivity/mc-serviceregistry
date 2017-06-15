(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('InstanceDeleteController',InstanceDeleteController);

    InstanceDeleteController.$inject = ['$uibModalInstance', 'entity', 'Instance'];

    function InstanceDeleteController($uibModalInstance, entity, Instance) {
        var vm = this;

        vm.instance = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Instance.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
