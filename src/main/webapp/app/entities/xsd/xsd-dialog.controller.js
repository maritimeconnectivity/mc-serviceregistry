(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('XsdDialogController', XsdDialogController);

    XsdDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Xsd'];

    function XsdDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Xsd) {
        var vm = this;

        vm.xsd = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.xsd.id !== null) {
                Xsd.update(vm.xsd, onSaveSuccess, onSaveError);
            } else {
                Xsd.save(vm.xsd, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mcsrApp:xsdUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setContent = function ($file, xsd) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        xsd.content = base64Data;
                        xsd.contentContentType = $file.type;
                    });
                });
            }
        };

    }
})();
