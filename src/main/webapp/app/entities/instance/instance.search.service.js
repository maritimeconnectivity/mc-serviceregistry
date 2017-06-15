(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .factory('InstanceSearch', InstanceSearch);

    InstanceSearch.$inject = ['$resource'];

    function InstanceSearch($resource) {
        var resourceUrl =  'api/_search/instances/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
