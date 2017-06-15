(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .controller('XmlDialogController', XmlDialogController);

    XmlDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Xml', 'Base64'];

    function XmlDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Xml, Base64) {
        var vm = this;

        vm.xml = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.Base64 = Base64;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.xml.id !== null) {
                Xml.update(vm.xml, onSaveSuccess, onSaveError);
            } else {
                Xml.save(vm.xml, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mcsrApp:xmlUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setContent = function ($file, xml) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        xml.content = Base64.decode(base64Data);
                        xml.contentContentType = $file.type;
                    });
                });
            }
        };

    }
})();
