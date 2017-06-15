(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .factory('SpecificationTemplateSearch', SpecificationTemplateSearch);

    SpecificationTemplateSearch.$inject = ['$resource'];

    function SpecificationTemplateSearch($resource) {
        var resourceUrl =  'api/_search/specification-templates/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
