(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('specification', {
            parent: 'entity',
            url: '/specification?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.specification.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/specification/specifications.html',
                    controller: 'SpecificationController',
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
                    $translatePartialLoader.addPart('specification');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('specification-detail', {
            parent: 'entity',
            url: '/specification/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.specification.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/specification/specification-detail.html',
                    controller: 'SpecificationDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('specification');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Specification', function($stateParams, Specification) {
                    return Specification.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'specification',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('specification-detail.edit', {
            parent: 'specification-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification/specification-dialog.html',
                    controller: 'SpecificationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Specification', function(Specification) {
                            return Specification.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('specification.new', {
            parent: 'specification',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification/specification-dialog.html',
                    controller: 'SpecificationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                version: null,
                                comment: null,
                                organisationId: null,
                                keywords: null,
                                specificationId: null,
                                status: null,
                                organizationId: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('specification', null, { reload: true });
                }, function() {
                    $state.go('specification');
                });
            }]
        })
        .state('specification.edit', {
            parent: 'specification',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification/specification-dialog.html',
                    controller: 'SpecificationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Specification', function(Specification) {
                            return Specification.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('specification', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('specification.delete', {
            parent: 'specification',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/specification/specification-delete-dialog.html',
                    controller: 'SpecificationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Specification', function(Specification) {
                            return Specification.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('specification', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
