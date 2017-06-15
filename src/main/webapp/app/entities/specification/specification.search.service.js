(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .factory('SpecificationSearch', SpecificationSearch);

    SpecificationSearch.$inject = ['$resource'];

    function SpecificationSearch($resource) {
        var resourceUrl =  'api/_search/specifications/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
