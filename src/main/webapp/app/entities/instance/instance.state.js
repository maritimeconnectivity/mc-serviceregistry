(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('instance', {
            parent: 'entity',
            url: '/instance?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.instance.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/instance/instances.html',
                    controller: 'InstanceController',
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
                    $translatePartialLoader.addPart('instance');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('instance-detail', {
            parent: 'entity',
            url: '/instance/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.instance.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/instance/instance-detail.html',
                    controller: 'InstanceDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('instance');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Instance', function($stateParams, Instance) {
                    return Instance.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'instance',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('instance-detail.edit', {
            parent: 'instance-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/instance/instance-dialog.html',
                    controller: 'InstanceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Instance', function(Instance) {
                            return Instance.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('instance.new', {
            parent: 'instance',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/instance/instance-dialog.html',
                    controller: 'InstanceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                version: null,
                                comment: null,
                                geometry: null,
                                geometryContentType: null,
                                instanceId: null,
                                keywords: null,
                                status: null,
                                organizationId: null,
                                unlocode: null,
                                endpointUri: null,
                                endpointType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('instance', null, { reload: true });
                }, function() {
                    $state.go('instance');
                });
            }]
        })
        .state('instance.edit', {
            parent: 'instance',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/instance/instance-dialog.html',
                    controller: 'InstanceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Instance', function(Instance) {
                            return Instance.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('instance', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('instance.delete', {
            parent: 'instance',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/instance/instance-delete-dialog.html',
                    controller: 'InstanceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Instance', function(Instance) {
                            return Instance.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('instance', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
