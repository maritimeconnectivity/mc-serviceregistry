(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('DocDialogController', DocDialogController);

    DocDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Doc', 'Specification', 'Design', 'Instance'];

    function DocDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Doc, Specification, Design, Instance) {
        var vm = this;

        vm.doc = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.specifications = Specification.query();
        vm.designs = Design.query();
        vm.instances = Instance.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.doc.id !== null) {
                Doc.update(vm.doc, onSaveSuccess, onSaveError);
            } else {
                Doc.save(vm.doc, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mcsrApp:docUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setFilecontent = function ($file, doc) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        doc.filecontent = base64Data;
                        doc.filecontentContentType = $file.type;
                    });
                });
            }
        };

    }
})();
