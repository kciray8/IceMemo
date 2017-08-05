app.controller('browserController', function ($scope, $interval, $rootScope, $resource, $routeParams, $window, $http, $q, Page, $document) {
        $rootScope.settings.then(function (settings) {
            Page.setTitle($scope.l.bro_bro);

            $scope.currentSeries = null;
            $scope.currentSeason = null;
            $scope.currentVideo = null;

            $scope.data = {};
            $scope.data.query = "";

            $scope.appendMode = false;

            $scope.selectTVSeries = function (series) {
                $scope.currentSeries = series;

                $resource("./season/get-array").query({seriesId: series.id}, function (data) {
                    $scope.seasons = data

                    $scope.selectSeason($scope.seasons[0]);
                });
            };

            $scope.selectSeason = function (season) {
                $scope.currentSeason = season;

                $resource("./video/get-array").query({
                    seasonId: $scope.currentSeason.id
                }, function (res) {
                    $scope.videos = res;
                    $scope.selectVideo($scope.videos[0]);
                });
            };

            $scope.selectVideo = function (video) {
                $scope.currentVideo = video;
            };


            $resource("./series/get-all").query({}, function (data) {
                $scope.seriesArray = data;

                $scope.selectTVSeries($scope.seriesArray[0]);
            });

            $scope.addSelectedVideo = function () {
                var query = "video:" + $scope.currentVideo.id;

                if ($scope.appendMode) {
                    if ($scope.data.query != "") {
                        $scope.data.query += " ";
                    }

                    $scope.data.query += query;
                } else {
                    $scope.data.query = query;
                    $scope.search()
                }
            };

            $scope.addSelectedSeries = function () {
                var query = "series:" + $scope.currentSeries.id;

                if ($scope.appendMode) {
                    if ($scope.data.query != "") {
                        $scope.data.query += " ";
                    }

                    $scope.data.query += query;
                } else {
                    $scope.data.query = query;
                    $scope.search()
                }
            };

            $scope.addSelectedSeason = function () {
                var query = "season:" + $scope.currentSeason.id;

                if ($scope.appendMode) {
                    if ($scope.data.query != "") {
                        $scope.data.query += " ";
                    }

                    $scope.data.query += query;
                } else {
                    $scope.data.query = query;
                    $scope.search()
                }
            };

            $scope.showSuspended = function () {
                $scope.data.query = "is:suspended";
                $scope.search()
            };
            $scope.showMarked = function () {
                $scope.data.query = "is:marked";
                $scope.search()
            };

            $scope.search = function () {
                $resource("./search").get({query: $scope.data.query}, function (res) {
                    $scope.queryRes = res;
                });
            };

            $scope.getLeftDays = function (nextRepetitionDate) {
                return ((nextRepetitionDate - getMs()) / (1000 * 60 * 60 * 24));
            };


            $scope.openCard = function (card) {
                $('#modalClipDetails').modal();

                $scope.selectedCard = card;

                cardPlayer.src = "./video/" + card.video;
                cardPlayer.currentTime = card.timeStart;
            };

            $scope.paused = true;

            var cardPlayer = $document.find("#cardPlayer")[0];
            cardPlayer.onplay = function () {
                $scope.paused = false;
            };
            cardPlayer.onpause = function () {
                $scope.paused = true;
            };

            $scope.play = function () {
                cardPlayer.currentTime = $scope.selectedCard.timeStart;
                cardPlayer.play();
            };

            $scope.stop = function () {
                cardPlayer.currentTime = $scope.selectedCard.timeStart;
                cardPlayer.pause();
            };

            $interval(function () {
                if ($scope.selectedCard != undefined) {
                    if (cardPlayer.currentTime > $scope.selectedCard.timeEnd) {
                        cardPlayer.pause();
                    }
                }
            });

            $scope.onDeleteClip = function () {
                bootbox.confirm($scope.l.bro_sure_del_clip, function (result) {
                    if (result) {
                        $resource("./card/delete").get({id: $scope.selectedCard.id}, function (res) {
                            $('#modalClipDetails').modal('hide');

                            $scope.queryRes.cards.splice($scope.queryRes.cards.indexOf($scope.selectedCard), 1);
                        });
                    }
                });
            }
        });
    }
);
