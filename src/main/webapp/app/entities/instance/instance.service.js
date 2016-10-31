(function() {
    'use strict';
    angular
        .module('mcsrApp')
        .factory('Instance', Instance);

    Instance.$inject = ['$resource'];

    function Instance ($resource) {
        var resourceUrl =  'api/instances/:id';

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
