(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .factory('SpecificationTemplateSetSearch', SpecificationTemplateSetSearch);

    SpecificationTemplateSetSearch.$inject = ['$resource'];

    function SpecificationTemplateSetSearch($resource) {
        var resourceUrl =  'api/_search/specification-template-sets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
