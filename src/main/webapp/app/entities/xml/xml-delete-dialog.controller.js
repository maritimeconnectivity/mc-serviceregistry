(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('XmlDeleteController',XmlDeleteController);

    XmlDeleteController.$inject = ['$uibModalInstance', 'entity', 'Xml'];

    function XmlDeleteController($uibModalInstance, entity, Xml) {
        var vm = this;

        vm.xml = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Xml.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
