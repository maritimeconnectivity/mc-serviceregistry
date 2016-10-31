(function() {
    'use strict';
    angular
        .module('mcsrApp')
        .factory('Design', Design);

    Design.$inject = ['$resource'];

    function Design ($resource) {
        var resourceUrl =  'api/designs/:id';

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
