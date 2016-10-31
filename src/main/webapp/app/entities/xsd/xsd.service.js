(function() {
    'use strict';
    angular
        .module('mcsrApp')
        .factory('Xsd', Xsd);

    Xsd.$inject = ['$resource'];

    function Xsd ($resource) {
        var resourceUrl =  'api/xsds/:id';

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
