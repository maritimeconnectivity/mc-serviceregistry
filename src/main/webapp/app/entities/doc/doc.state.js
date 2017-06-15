(function() {
    'use strict';

    angular
        .module('mcsrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('doc', {
            parent: 'entity',
            url: '/doc?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.doc.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/doc/docs.html',
                    controller: 'DocController',
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
                    $translatePartialLoader.addPart('doc');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('doc-detail', {
            parent: 'entity',
            url: '/doc/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'mcsrApp.doc.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/doc/doc-detail.html',
                    controller: 'DocDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('doc');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Doc', function($stateParams, Doc) {
                    return Doc.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'doc',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('doc-detail.edit', {
            parent: 'doc-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/doc/doc-dialog.html',
                    controller: 'DocDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Doc', function(Doc) {
                            return Doc.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('doc.new', {
            parent: 'doc',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/doc/doc-dialog.html',
                    controller: 'DocDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                comment: null,
                                mimetype: null,
                                filecontent: null,
                                filecontentContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('doc', null, { reload: true });
                }, function() {
                    $state.go('doc');
                });
            }]
        })
        .state('doc.edit', {
            parent: 'doc',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/doc/doc-dialog.html',
                    controller: 'DocDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Doc', function(Doc) {
                            return Doc.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('doc', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('doc.delete', {
            parent: 'doc',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/doc/doc-delete-dialog.html',
                    controller: 'DocDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Doc', function(Doc) {
                            return Doc.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('doc', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
