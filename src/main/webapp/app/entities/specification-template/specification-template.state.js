(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('specification-template', {
            parent: 'entity',
            url: '/specification-template?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.specificationTemplate.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/specification-template/specification-templates.html',
                    controller: 'SpecificationTemplateController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('specificationTemplate');
                    $translatePartialLoader.addPart('specificationTemplateType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('specification-template-detail', {
            parent: 'entity',
            url: '/specification-template/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.specificationTemplate.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/specification-template/specification-template-detail.html',
                    controller: 'SpecificationTemplateDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('specificationTemplate');
                    $translatePartialLoader.addPart('specificationTemplateType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'SpecificationTemplate', function($stateParams, SpecificationTemplate) {
                    return SpecificationTemplate.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'specification-template',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('specification-template-detail.edit', {
            parent: 'specification-template-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification-template/specification-template-dialog.html',
                    controller: 'SpecificationTemplateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SpecificationTemplate', function(SpecificationTemplate) {
                            return SpecificationTemplate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('specification-template.new', {
            parent: 'specification-template',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification-template/specification-template-dialog.html',
                    controller: 'SpecificationTemplateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                version: null,
                                type: null,
                                comment: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('specification-template', null, { reload: true });
                }, function() {
                    $state.go('specification-template');
                });
            }]
        })
        .state('specification-template.edit', {
            parent: 'specification-template',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification-template/specification-template-dialog.html',
                    controller: 'SpecificationTemplateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SpecificationTemplate', function(SpecificationTemplate) {
                            return SpecificationTemplate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('specification-template', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('specification-template.delete', {
            parent: 'specification-template',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification-template/specification-template-delete-dialog.html',
                    controller: 'SpecificationTemplateDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['SpecificationTemplate', function(SpecificationTemplate) {
                            return SpecificationTemplate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('specification-template', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
