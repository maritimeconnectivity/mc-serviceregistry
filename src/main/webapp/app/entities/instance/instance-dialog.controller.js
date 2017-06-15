(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('InstanceDialogController', InstanceDialogController);

    InstanceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'DataUtils', 'entity', 'Instance', 'Xml', 'Doc', 'SpecificationTemplate', 'Design'];

    function InstanceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, DataUtils, entity, Instance, Xml, Doc, SpecificationTemplate, Design) {
        var vm = this;

        vm.instance = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.instanceasxmls = Xml.query({filter: 'instance-is-null'});
        $q.all([vm.instance.$promise, vm.instanceasxmls.$promise]).then(function() {
            if (!vm.instance.instanceAsXml || !vm.instance.instanceAsXml.id) {
                return $q.reject();
            }
            return Xml.get({id : vm.instance.instanceAsXml.id}).$promise;
        }).then(function(instanceAsXml) {
            vm.instanceasxmls.push(instanceAsXml);
        });
        vm.instanceasdocs = Doc.query({filter: 'instance-is-null'});
        $q.all([vm.instance.$promise, vm.instanceasdocs.$promise]).then(function() {
            if (!vm.instance.instanceAsDoc || !vm.instance.instanceAsDoc.id) {
                return $q.reject();
            }
            return Doc.get({id : vm.instance.instanceAsDoc.id}).$promise;
        }).then(function(instanceAsDoc) {
            vm.instanceasdocs.push(instanceAsDoc);
        });
        vm.specificationtemplates = SpecificationTemplate.query();
        vm.designs = Design.query();
        vm.docs = Doc.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.instance.id !== null) {
                Instance.update(vm.instance, onSaveSuccess, onSaveError);
            } else {
                Instance.save(vm.instance, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mcsrApp:instanceUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setGeometry = function ($file, instance) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        instance.geometry = base64Data;
                        instance.geometryContentType = $file.type;
                    });
                });
            }
        };

    }
})();
