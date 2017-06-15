(function() {
    'use strict';
    angular
        .module('mcsrApp')
        .factory('SpecificationTemplateSet', SpecificationTemplateSet);

    SpecificationTemplateSet.$inject = ['$resource'];

    function SpecificationTemplateSet ($resource) {
        var resourceUrl =  'api/specification-template-sets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
