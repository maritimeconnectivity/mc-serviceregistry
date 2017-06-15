(function() {
    'use strict';
    angular
        .module('mcsrApp')
        .factory('SpecificationTemplate', SpecificationTemplate);

    SpecificationTemplate.$inject = ['$resource'];

    function SpecificationTemplate ($resource) {
        var resourceUrl =  'api/specification-templates/:id';

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
