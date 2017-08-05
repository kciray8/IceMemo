//Init

function getNumEnding(iNumber, aEndings)//RUS
{
    console.log("---" + iNumber);

    var sEnding, i;
    iNumber = iNumber % 100;
    if (iNumber>=11 && iNumber<=19) {
        sEnding=aEndings[2];
    }
    else {
        i = iNumber % 10;
        switch (i)
        {
            case (1): sEnding = aEndings[0]; break;
            case (2):
            case (3):
            case (4): sEnding = aEndings[1]; break;
            default: sEnding = aEndings[2];
        }
    }
    return sEnding;
}

var app = angular.module("MyApp", ["ngMessages", "ngResource", "ngRoute", "ngSanitize", "RecursionHelper", "ui.bootstrap"])
    .run(function ($rootScope, $resource, $q) {
        $rootScope.C = {};
        $rootScope.C.REPEAT_NOW = "repeat_now";
        $rootScope.C.REPEAT_LATER = "repeat_later";
        $rootScope.C.HARD = "hard";
        $rootScope.C.GOOD = "good";
        $rootScope.C.EASY = "easy";

        $rootScope.C.DAY_MS = 1000 * 60 * 60 * 24;

        $rootScope.plu = function (str, num) {
            var code = $rootScope.settings_.language.code;
            if((code == "rus")&&(str == "day")){
                return getNumEnding(num, ["день","дня","дней"])
            }

            if (num == 1) {
                return str;
            } else {
                return str + "s";
            }
        };

        bootbox.setDefaults({backdrop: true});

        $rootScope.updateSettings = function(){
            var deferred = $q.defer();
            $rootScope.settings = deferred.promise;

            $resource("./get-settings").get({}, function (data) {
                deferred.resolve(data);

                $rootScope.l = {};

                for (var i = 0; i < data.language.list.length; i++) {
                    var langEl = data.language.list[i];
                    $rootScope.l[langEl.key] = langEl.value;
                }
            });
        };
        $rootScope.updateSettings();

        $rootScope.settings.then(function (settings) {
            $rootScope.settings_ = settings;
        })
    });


app.config(function ($routeProvider, $httpProvider) {
    $routeProvider
        .when("/video/:id", {
            templateUrl: "res/pages/video_watch.html",
            controller: 'videoWatchController'
        })
        .when("/repeat", {
            templateUrl: "res/pages/repeat.html",
            controller: 'repeatController'
        })
        .when("/stat", {
            templateUrl: "res/pages/stat.html",
            controller: 'statController'
        })
        .when("/browser", {
            templateUrl: "res/pages/browser.html",
            controller: 'browserController'
        })

        .when("/add", {
            templateUrl: "res/pages/add.html",
            controller: 'addController'
        })
        .when("/flashcard-review", {
            templateUrl: "res/pages/flashcard-review.html",
            controller: 'flashcardReviewController'
        })

        .when("/debug", {
            templateUrl: "res/pages/debug.html",
            controller: 'debugController'
        })

        .when("/adjust-volume", {
            templateUrl: "res/pages/adjust-volume.html",
            controller: 'adjustVolumeController'
        })

        .when("/", {
            templateUrl: "res/pages/main.html",
            controller: 'mainController'
        })

        .when("/watch", {
            templateUrl: "res/pages/watch.html",
            controller: 'watchController'
        })
        .when("/login", {
            templateUrl: "res/pages/login.html",
            controller: 'loginController'
        })
        .when("/info", {
            templateUrl: "res/pages/info.html",
            controller: 'infoController'
        })
        .when("/settings", {
            templateUrl: "res/pages/settings.html",
            controller: 'settingsController'
        });


    $httpProvider.interceptors.push(function ($q, $rootScope) {
        return {
            'requestError': function (rejection) {
                alert("ERROR1");
                console.log(rejection);

                return $q.reject(rejection);
            },

            'responseError': function (rejection) {

                $('#errorModal').modal('show');
                console.log(rejection);

                $rootScope.globalErrorName = "HTTP response error";
                $rootScope.globalErrorHtml = rejection.data;

                if (rejection.data == null) {
                    $rootScope.globalErrorName = "Server is offline";
                    $rootScope.globalErrorHtml = "Please, run server with starter";
                }else{
                    if(rejection.data.message != null){
                        $rootScope.globalErrorHtml = "<pre>" + rejection.data.message + "</pre>";
                    }
                }

                return $q.reject(rejection);
            }
        };
    });

});