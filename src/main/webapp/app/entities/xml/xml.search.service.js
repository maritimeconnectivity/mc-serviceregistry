(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .factory('XmlSearch', XmlSearch);

    XmlSearch.$inject = ['$resource'];

    function XmlSearch($resource) {
        var resourceUrl =  'api/_search/xmls/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
