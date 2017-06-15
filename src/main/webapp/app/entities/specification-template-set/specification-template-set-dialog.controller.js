(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('SpecificationTemplateSetDialogController', SpecificationTemplateSetDialogController);

    SpecificationTemplateSetDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'SpecificationTemplateSet', 'SpecificationTemplate', 'Doc'];

    function SpecificationTemplateSetDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, SpecificationTemplateSet, SpecificationTemplate, Doc) {
        var vm = this;

        vm.specificationTemplateSet = entity;
        vm.clear = clear;
        vm.save = save;
        vm.specificationtemplates = SpecificationTemplate.query();
        vm.docs = Doc.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.specificationTemplateSet.id !== null) {
                SpecificationTemplateSet.update(vm.specificationTemplateSet, onSaveSuccess, onSaveError);
            } else {
                SpecificationTemplateSet.save(vm.specificationTemplateSet, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mcsrApp:specificationTemplateSetUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
