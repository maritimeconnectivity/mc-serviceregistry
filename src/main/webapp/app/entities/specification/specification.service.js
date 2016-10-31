(function() {
    'use strict';
    angular
        .module('mcsrApp')
        .factory('Specification', Specification);

    Specification.$inject = ['$resource'];

    function Specification ($resource) {
        var resourceUrl =  'api/specifications/:id';

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
