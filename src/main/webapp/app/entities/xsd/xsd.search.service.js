(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .factory('XsdSearch', XsdSearch);

    XsdSearch.$inject = ['$resource'];

    function XsdSearch($resource) {
        var resourceUrl =  'api/_search/xsds/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
