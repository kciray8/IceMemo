app.controller('bodyController', function ($scope, $rootScope, $location, $resource) {
    $rootScope.getActive = function (type) {
        if (type == $location.path()) {
            return true;
        } else {
            return false;
        }
    };

});

app.controller('mainController', function ($scope, $rootScope, $resource, $routeParams, $window, Page) {
        $rootScope.settings.then(function (settings) {
            Page.setTitle($scope.l.menu_about);

            $resource("./info").get({}, function (res) {
                $scope.info = res;
            });

            $scope.selectedPage = "about"
        });
    }
);
app.controller('htmlController', function ($scope, $rootScope, $resource, $routeParams, $window, Page) {
        $rootScope.settings.then(function (settings) {
            $scope.Page = Page;


            $scope.input = {};
            $scope.input.key = "";

            if (!settings.demo) {
                $resource("./is-activated").get({}, function (res) {
                    if (res.correct) {
                        var makeout = localStorage.getItem("makeout");
                        if (makeout == null) {
                            $scope.showActivationDialog();
                        }
                    } else {
                        $scope.showActivationDialog();
                    }
                });
            }

            $scope.showActivationDialog = function () {
                $('#activationModal').modal({
                    keyboard: false,
                    show: true,
                    backdrop: 'static'
                })
            };

            $scope.wrongKeyMessage = "";

            $scope.activate = function () {
                localStorage.setItem("makeout", "1");
                location.reload()

                /*
                $resource("http://kciray.com/check.php").get({key: $scope.input.key}, function (res) {
                    if (res.correct) {
                        $scope.wrongKeyMessage = "";
                        $resource("./activate").get({key: $scope.input.key}, function (res) {
                            if (res.correct) {
                                localStorage.setItem("makeout", "1");
                                location.reload()
                            }
                        });
                    } else {
                        $scope.wrongKeyMessage = $scope.l.menu_key_incorrect;
                    }
                });*/
            }
        });
    }
);
app.controller('infoController', function ($scope, $rootScope, $resource, $routeParams, $window, Page) {
        $rootScope.settings.then(function (settings) {
            Page.setTitle($scope.l.menu_info);

            $resource("./info").get({}, function (res) {
                $scope.info = res;
            });

            $scope.selectedPage = "general"
        });
    }
);
app.controller('todayPlanController', function ($scope, $rootScope, $resource, $routeParams, $window, Page) {
        $resource("./card/day-plan").get({}, function (res) {
            $scope.dayPlan = res;
        });
    }
);


app.controller('settingsController', function ($scope, $rootScope, $resource, $routeParams, $window, Page) {
        $rootScope.settings.then(function (settings) {
            Page.setTitle($scope.l.menu_settings);

            $scope.selectedPage = "general";


            $scope.settings.then(function (settings) {
                $scope.lang = settings.language.code;
            });

            $scope.selectLang = function (newLang) {
                $scope.lang = newLang;

                $resource("./set-lang").get({lang: $scope.lang}, function (res) {
                    location.reload()
                });
            }
        });
    }
);

app.controller('loginController', function ($scope, $rootScope, $resource, $routeParams, $window, Page) {
        $rootScope.settings.then(function (settings) {
            Page.setTitle($scope.l.menu_login);

            $resource("./user/get-all").query({}, function (res) {
                $scope.users = res;
            });

            $scope.selectUser = function (id) {
                $resource("./user/select").get({id: id}, function (res) {
                    location.href = "./";
                });
            };
            $scope.newUser = {};

            $scope.createUser = function () {
                $resource("./user/create").get({name: $scope.newUser.name}, function (res) {
                    $scope.users.push(res);
                    $scope.newUser.name = "";
                });
            }
        });
    }
);

app.controller('watchController', function ($scope, $rootScope, $resource, $routeParams, $window, Page, $location) {
        $rootScope.settings.then(function (settings) {
            Page.setTitle($scope.l.menu_watch);

            $scope.settings.then(function (settings) {

                $scope.seasons = [];
                $scope.videos = [];
                $scope.seriesArray = [];

                $resource("./series/get-all").query({}, function (res) {
                    $scope.seriesArray = res;

                    if (settings.user.lastSeries != -1) {
                        for (var i = 0; i < $scope.seriesArray.length; i++) {
                            var obj = $scope.seriesArray[i];
                            if (obj.id == settings.user.lastSeries) {
                                $scope.selectSeries(obj);
                                break;
                            }
                        }
                    }
                });

                $scope.gotoAddPage = function () {
                    $location.path('/add')
                };

                $scope.updateUser = function (field, vl) {
                    $resource("./gen/update_property").save({
                        name: field,
                        id: settings.user.id,
                        cls: "User",
                        value: vl
                    }, function (res) {
                        settings.user[field] = vl
                        console.log(settings)
                    });
                };

                $scope.selectSeries = function (series) {
                    $scope.selectedSeries = series;
                    $scope.videos = [];

                    $resource("./season/get-array").query({
                        seriesId: $scope.selectedSeries.id
                    }, function (res) {
                        $scope.seasons = res;

                        if ((settings.user.lastSeason != -1) && (settings.user.lastSeries == $scope.selectedSeries.id)) {
                            for (var i = 0; i < $scope.seasons.length; i++) {
                                var obj = $scope.seasons[i];
                                if (obj.id == settings.user.lastSeason) {
                                    $scope.selectSeason(obj);
                                    break;
                                }
                            }
                        } else {
                            $scope.selectSeason($scope.seasons[$scope.seasons.length - 1]);
                        }
                        $scope.updateUser("lastSeries", $scope.selectedSeries.id);
                    });
                };

                $scope.selectSeason = function (season) {
                    $scope.selectedSeason = season;

                    $resource("./video/get-array").query({
                        seasonId: $scope.selectedSeason.id
                    }, function (res) {
                        $scope.videos = res;
                    });

                    $scope.updateUser("lastSeason", $scope.selectedSeason.id);
                };

            });
        });
    }
);

app.controller('debugController', function ($scope, $rootScope, $resource, $routeParams, $window, $http) {
        $scope.getHttpError = function () {
            $resource("./debug/exception").get({}, function (res) {
            });
        };

        $scope.test = function () {
            $http({
                method: "GET", url: "./sub/7.srt",
                transformResponse: function (value) {
                    alert(value);
                }
            })
        };


        $scope.states = ["Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut"];

    }
);

function getMs() {
    var day = 1000 * 60 * 60 * 24;
    var d = new Date();
    return d.getTime() + 0 * day;
}

function del(array, element) {
    var index = array.indexOf(element);
    array.splice(index, 1);
}

function copyProp(from, to) {
    for (var k in from) to[k] = from[k];
}

app.controller('statController', function ($scope, $rootScope, $resource, $routeParams, $document, Page) {
    $rootScope.settings.then(function (settings) {
        Page.setTitle($scope.l.menu_stat);

        $scope.stat = "season";
        $scope.currentSeries = null;
        $scope.currentSeason = null;

        $scope.selectTVSeries = function (series) {
            $scope.currentSeries = series;

            $resource("./season/get-array").query({seriesId: series.id}, function (data) {
                $scope.seasons = data;

                $scope.selectSeason($scope.seasons[$scope.seasons.length - 1]);
            });
        };


        $scope.selectSeason = function (season) {
            $scope.currentSeason = season;

            $resource("./video/get-stat").query({seasonId: season.id}, function (data) {
                $scope.videoStat = data;
            });
        };

        $resource("./series/get-all").query({}, function (data) {
            $scope.seriesArray = data;
            $scope.selectTVSeries($scope.seriesArray[$scope.seriesArray.length - 1])
        });

        $resource("./series/get-all-stat").get({}, function (data) {
            $scope.allStat = data;
            console.log($scope.allStat)
        });
    });
});

app.controller('flashcardReviewController', function ($scope, $rootScope, $resource, $routeParams, $document) {
    $resource("./flashcard/get-to-review").query({}, function (data) {
        $scope.flashcards = data;
    });
});

app.controller('adjustVolumeController', function ($scope, $rootScope, $resource, $routeParams, $document, $sce) {
    $resource("./video/get-examples").query({}, function (data) {
        $scope.videos = data;
        $scope.getSrc = function (video, event) {
            console.log(event)

            return $sce.trustAsResourceUrl('./video/' + video.id)
        };
    });

    $scope.videoInit = function (element, video) {
        element[0].volume = video.seasonVolume
    }

    $scope.updateSeasons = function () {
        for (var i = 0; i < $scope.videos.length; i++) {
            var video = $scope.videos[i];

            $resource("./gen/update_property").save({
                name: "volume",
                id: video.season,
                cls: "Season",
                value: video.seasonVolume
            }, function (res) {

            });
        }
    }
});


