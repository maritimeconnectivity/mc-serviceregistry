(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('SpecificationDialogController', SpecificationDialogController);

    SpecificationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Specification', 'Xml', 'Doc', 'SpecificationTemplate'];

    function SpecificationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Specification, Xml, Doc, SpecificationTemplate) {
        var vm = this;

        vm.specification = entity;
        vm.clear = clear;
        vm.save = save;
        vm.specasxmls = Xml.query({filter: 'specification-is-null'});
        $q.all([vm.specification.$promise, vm.specasxmls.$promise]).then(function() {
            if (!vm.specification.specAsXml || !vm.specification.specAsXml.id) {
                return $q.reject();
            }
            return Xml.get({id : vm.specification.specAsXml.id}).$promise;
        }).then(function(specAsXml) {
            vm.specasxmls.push(specAsXml);
        });
        vm.specasdocs = Doc.query({filter: 'specification-is-null'});
        $q.all([vm.specification.$promise, vm.specasdocs.$promise]).then(function() {
            if (!vm.specification.specAsDoc || !vm.specification.specAsDoc.id) {
                return $q.reject();
            }
            return Doc.get({id : vm.specification.specAsDoc.id}).$promise;
        }).then(function(specAsDoc) {
            vm.specasdocs.push(specAsDoc);
        });
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
            if (vm.specification.id !== null) {
                Specification.update(vm.specification, onSaveSuccess, onSaveError);
            } else {
                Specification.save(vm.specification, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mcsrApp:specificationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
