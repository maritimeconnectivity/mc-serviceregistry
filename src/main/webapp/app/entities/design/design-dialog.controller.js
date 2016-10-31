(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('DesignDialogController', DesignDialogController);

    DesignDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Design', 'Xml', 'Doc', 'SpecificationTemplate', 'Specification', 'Instance'];

    function DesignDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Design, Xml, Doc, SpecificationTemplate, Specification, Instance) {
        var vm = this;

        vm.design = entity;
        vm.clear = clear;
        vm.save = save;
        vm.designasxmls = Xml.query({filter: 'design-is-null'});
        $q.all([vm.design.$promise, vm.designasxmls.$promise]).then(function() {
            if (!vm.design.designAsXml || !vm.design.designAsXml.id) {
                return $q.reject();
            }
            return Xml.get({id : vm.design.designAsXml.id}).$promise;
        }).then(function(designAsXml) {
            vm.designasxmls.push(designAsXml);
        });
        vm.designasdocs = Doc.query({filter: 'design-is-null'});
        $q.all([vm.design.$promise, vm.designasdocs.$promise]).then(function() {
            if (!vm.design.designAsDoc || !vm.design.designAsDoc.id) {
                return $q.reject();
            }
            return Doc.get({id : vm.design.designAsDoc.id}).$promise;
        }).then(function(designAsDoc) {
            vm.designasdocs.push(designAsDoc);
        });
        vm.specificationtemplates = SpecificationTemplate.query();
        vm.specifications = Specification.query();
        vm.docs = Doc.query();
        vm.instances = Instance.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.design.id !== null) {
                Design.update(vm.design, onSaveSuccess, onSaveError);
            } else {
                Design.save(vm.design, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mcsrApp:designUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
