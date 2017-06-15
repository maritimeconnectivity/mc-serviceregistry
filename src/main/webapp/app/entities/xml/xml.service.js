(function() {
    'use strict';
    angular
        .module('mcsrApp')
        .factory('Xml', Xml);

    Xml.$inject = ['$resource'];

    function Xml ($resource) {
        var resourceUrl =  'api/xmls/:id';

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
