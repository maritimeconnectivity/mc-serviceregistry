var HtmlScreenshotReporter = require("protractor-jasmine2-screenshot-reporter");
var JasmineReporters = require('jasmine-reporters');

var prefix = 'src/test/javascript/'.replace(/[^/]+/g,'..');

exports.config = {
    //seleniumAddress: "http://localhost:4444/wd/hub",
    seleniumServerJar: prefix + 'node_modules/protractor/node_modules/webdriver-manager/selenium/selenium-server-standalone-3.5.3.jar',
    geckoDriver: prefix + 'node_modules/protractor/node_modules/webdriver-manager/selenium/geckodriver-v0.18.0',
    chromeDriver: prefix + 'node_modules/protractor/node_modules/webdriver-manager/selenium/chromedriver_2.31',
    allScriptsTimeout: 11000,
    
    suites: {
        account: './e2e/account/*.js',
        admin: './e2e/admin/*.js',
        entity: './e2e/entities/*.js'
    },

    directConnect: true,
    capabilities: {
        'browserName': 'firefox',
        'version': '47',
        'phantomjs.binary.path': require('phantomjs-prebuilt').path,
        'phantomjs.ghostdriver.cli.args': ['--loglevel=DEBUG']
    },
    
    baseUrl: 'http://localhost:8080/',

    framework: 'jasmine2',
    jasmineNodeOpts: {
        showColors: true,
        defaultTimeoutInterval: 30000
    },
    
    onPrepare: function() {
        // Disable animations so e2e tests run more quickly
        var disableNgAnimate = function() {
            angular
                .module('disableNgAnimate', [])
                .run(['$animate', function($animate) {
                    $animate.enabled(false);
                }]);
        };

        var disableCssAnimate = function() {
            angular
                .module('disableCssAnimate', [])
                .run(function() {
                    var style = document.createElement('style');
                    style.type = 'text/css';
                    style.innerHTML = 'body * {' +
                        '-webkit-transition: none !important;' +
                        '-moz-transition: none !important;' +
                        '-o-transition: none !important;' +
                        '-ms-transition: none !important;' +
                        'transition: none !important;' +
                        '}';
                    document.getElementsByTagName('head')[0].appendChild(style);
                });
        };

        browser.addMockModule('disableNgAnimate', disableNgAnimate);
        browser.addMockModule('disableCssAnimate', disableCssAnimate);

        // Crash
        //browser.driver.manage().window().setSize(1280, 1024);
        jasmine.getEnv().addReporter(new JasmineReporters.JUnitXmlReporter({
            savePath: 'target/reports/e2e',
            consolidateAll: false
        }));
        jasmine.getEnv().addReporter(new HtmlScreenshotReporter({
            dest: "target/reports/e2e/screenshots"
        }));
    }

};
