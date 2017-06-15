(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .factory('DesignSearch', DesignSearch);

    DesignSearch.$inject = ['$resource'];

    function DesignSearch($resource) {
        var resourceUrl =  'api/_search/designs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
