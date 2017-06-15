(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('SpecificationTemplateDialogController', SpecificationTemplateDialogController);

    SpecificationTemplateDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'SpecificationTemplate', 'Doc', 'Xsd'];

    function SpecificationTemplateDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, SpecificationTemplate, Doc, Xsd) {
        var vm = this;

        vm.specificationTemplate = entity;
        vm.clear = clear;
        vm.save = save;
        vm.docs = Doc.query();
        vm.xsds = Xsd.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.specificationTemplate.id !== null) {
                SpecificationTemplate.update(vm.specificationTemplate, onSaveSuccess, onSaveError);
            } else {
                SpecificationTemplate.save(vm.specificationTemplate, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mcsrApp:specificationTemplateUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
