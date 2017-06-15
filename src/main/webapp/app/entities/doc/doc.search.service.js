(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .factory('DocSearch', DocSearch);

    DocSearch.$inject = ['$resource'];

    function DocSearch($resource) {
        var resourceUrl =  'api/_search/docs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
