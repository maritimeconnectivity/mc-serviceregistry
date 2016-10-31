(function() {
    'use strict';
    angular
        .module('mcsrApp')
        .factory('Doc', Doc);

    Doc.$inject = ['$resource'];

    function Doc ($resource) {
        var resourceUrl =  'api/docs/:id';

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
