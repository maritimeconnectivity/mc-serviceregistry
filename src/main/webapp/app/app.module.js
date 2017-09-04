(function() {
    'use strict';

    angular
        .module('mcsrApp', [
            'ngStorage', 
            'tmh.dynamicLocale',
            'pascalprecht.translate', 
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ngFileUpload',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router',
            'infinite-scroll',
            // jhipster-needle-angularjs-add-module JHipster will add new module here
            'angular-loading-bar'
        ])
        .run(run);

    run.$inject = ['stateHandler', 'translationHandler', '$rootScope', 'APP_NAME'];

    function run(stateHandler, translationHandler, $rootScope, APP_NAME) {
        stateHandler.initialize();
        translationHandler.initialize();
        $rootScope.currentYear = (new Date()).getFullYear();
        $rootScope.APP_NAME = APP_NAME;
    }
})();
