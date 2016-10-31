(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('specification-template-set', {
            parent: 'entity',
            url: '/specification-template-set?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.specificationTemplateSet.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/specification-template-set/specification-template-sets.html',
                    controller: 'SpecificationTemplateSetController',
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
                    $translatePartialLoader.addPart('specificationTemplateSet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('specification-template-set-detail', {
            parent: 'entity',
            url: '/specification-template-set/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.specificationTemplateSet.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/specification-template-set/specification-template-set-detail.html',
                    controller: 'SpecificationTemplateSetDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('specificationTemplateSet');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'SpecificationTemplateSet', function($stateParams, SpecificationTemplateSet) {
                    return SpecificationTemplateSet.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'specification-template-set',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('specification-template-set-detail.edit', {
            parent: 'specification-template-set-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification-template-set/specification-template-set-dialog.html',
                    controller: 'SpecificationTemplateSetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SpecificationTemplateSet', function(SpecificationTemplateSet) {
                            return SpecificationTemplateSet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('specification-template-set.new', {
            parent: 'specification-template-set',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification-template-set/specification-template-set-dialog.html',
                    controller: 'SpecificationTemplateSetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                version: null,
                                comment: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('specification-template-set', null, { reload: true });
                }, function() {
                    $state.go('specification-template-set');
                });
            }]
        })
        .state('specification-template-set.edit', {
            parent: 'specification-template-set',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification-template-set/specification-template-set-dialog.html',
                    controller: 'SpecificationTemplateSetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SpecificationTemplateSet', function(SpecificationTemplateSet) {
                            return SpecificationTemplateSet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('specification-template-set', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('specification-template-set.delete', {
            parent: 'specification-template-set',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification-template-set/specification-template-set-delete-dialog.html',
                    controller: 'SpecificationTemplateSetDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['SpecificationTemplateSet', function(SpecificationTemplateSet) {
                            return SpecificationTemplateSet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('specification-template-set', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
