(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('design', {
            parent: 'entity',
            url: '/design?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.design.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/design/designs.html',
                    controller: 'DesignController',
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
                    $translatePartialLoader.addPart('design');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('design-detail', {
            parent: 'entity',
            url: '/design/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.design.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/design/design-detail.html',
                    controller: 'DesignDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('design');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Design', function($stateParams, Design) {
                    return Design.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'design',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('design-detail.edit', {
            parent: 'design-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/design/design-dialog.html',
                    controller: 'DesignDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Design', function(Design) {
                            return Design.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('design.new', {
            parent: 'design',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/design/design-dialog.html',
                    controller: 'DesignDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                version: null,
                                comment: null,
                                designId: null,
                                organisationId: null,
                                status: null,
                                organizationId: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('design', null, { reload: true });
                }, function() {
                    $state.go('design');
                });
            }]
        })
        .state('design.edit', {
            parent: 'design',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/design/design-dialog.html',
                    controller: 'DesignDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Design', function(Design) {
                            return Design.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('design', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('design.delete', {
            parent: 'design',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/design/design-delete-dialog.html',
                    controller: 'DesignDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Design', function(Design) {
                            return Design.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('design', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
