(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('xsd', {
            parent: 'entity',
            url: '/xsd?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.xsd.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/xsd/xsds.html',
                    controller: 'XsdController',
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
                    $translatePartialLoader.addPart('xsd');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('xsd-detail', {
            parent: 'entity',
            url: '/xsd/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.xsd.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/xsd/xsd-detail.html',
                    controller: 'XsdDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('xsd');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Xsd', function($stateParams, Xsd) {
                    return Xsd.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'xsd',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('xsd-detail.edit', {
            parent: 'xsd-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/xsd/xsd-dialog.html',
                    controller: 'XsdDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Xsd', function(Xsd) {
                            return Xsd.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('xsd.new', {
            parent: 'xsd',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/xsd/xsd-dialog.html',
                    controller: 'XsdDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                comment: null,
                                content: null,
                                contentContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('xsd', null, { reload: true });
                }, function() {
                    $state.go('xsd');
                });
            }]
        })
        .state('xsd.edit', {
            parent: 'xsd',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/xsd/xsd-dialog.html',
                    controller: 'XsdDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Xsd', function(Xsd) {
                            return Xsd.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('xsd', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('xsd.delete', {
            parent: 'xsd',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/xsd/xsd-delete-dialog.html',
                    controller: 'XsdDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Xsd', function(Xsd) {
                            return Xsd.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('xsd', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
