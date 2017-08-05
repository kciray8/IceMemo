app.controller('addController', function ($scope, $rootScope, $resource, $routeParams, $window, $http, $q, Page, $interval, $location) {
        $rootScope.settings.then(function (settings) {
            Page.setTitle($scope.l.add_add_title);

            $scope.seasons = [];
            $scope.seriesArray = [];
            $scope.newSeriesModel = {};
            $scope.newSeasonModel = {};

            $scope.videosSelectorModel = {};
            $scope.videos = [];
            $scope.files = [];

            $scope.uploadMethod = 'hdd';
            $scope.hddAdd = 'add';

            $scope.selectedSeries = null;
            $scope.editedSeries = {};
            $scope.editedSeason = {};
            $scope.selectedSeason = null;

            $scope.downloadFromWeb = {};
            $scope.downloadFromWeb.name = "Friends";
            $scope.downloadFromWeb.user = "";
            $scope.downloadFromWeb.password = "";

            $scope.seriesList = [];

            $scope.selectedSeriesForDownload = null;

            $scope.seasonsForDownload = [];

            $scope.selectSeriesForDownload = function (series) {
                $scope.selectedSeriesForDownload = series;

                $resource("./download/get-seasons").get({
                    seriesId: series.id,
                    password: $scope.downloadFromWeb.password,
                    user: $scope.downloadFromWeb.user
                }, function (res) {
                    if(res.res == "OK") {
                        $scope.seasonsForDownload = res.data
                    }else{
                        $scope.seasonsForDownload = [];
                        $scope.searchOroroError = "Произошла ошибка при попытке запроса. Скорее всего, вы неверно указали логин и пароль";
                    }
                });
            };

            $scope.selectAllSFD = function () {
                var list = $scope.selectedSeasonForDownload.list;

                for (var i = 0; i < list.length; i++) {
                    list[i].check = true;
                }
            };
            $scope.deselectAllSFD = function () {
                var list = $scope.selectedSeasonForDownload.list;

                for (var i = 0; i < list.length; i++) {
                    list[i].check = false;
                }
            };

            $scope.selectSeasonForDownload = function (season) {
                $scope.selectedSeasonForDownload = season
            };

            $scope.ororoTask = null

            $scope.startDownloadFromOroro = function () {
                var list = $scope.selectedSeasonForDownload.list;
                $scope.ororoTask = [];

                for (var i = 0; i < list.length; i++) {
                    var video = list[i];
                    video.status = "wait";
                    video.progress = 0;//%
                    if (video.check) {
                        $scope.ororoTask.push(video)
                    }
                }

                downloadNextVideo()
            };

            $scope.downloadFromOroroDone = false;

            function downloadNextVideo() {
                var nextVideo = null;
                for (var i = 0; i < $scope.ororoTask.length; i++) {
                    var task = $scope.ororoTask[i];
                    if (task.status == "wait") {
                        nextVideo = task;
                        break
                    }
                }
                if (nextVideo != null) {
                    nextVideo.status = "DOWNLOADING";
                    $resource("./download/ororo-task").get({
                        episodeId: nextVideo.id,
                        password: $scope.downloadFromWeb.password,
                        user: $scope.downloadFromWeb.user
                    }, function (res) {
                        getSeries();
                        downloadNextVideo();
                    });
                } else {
                    $scope.downloadFromOroroDone = true;
                }
            }

            $interval(function () {
                if ($scope.ororoTask != null) {
                    $resource("./download/ororo-task-list").query({}, function (res) {
                        for (var i = 0; i < res.length; i++) {
                            var task = res[i];

                            for (var j = 0; j < $scope.ororoTask.length; j++) {
                                var video = $scope.ororoTask[j];
                                if (video.id == task.videoId) {
                                    video.progress = task.progress;
                                    video.status = task.status

                                    break
                                }
                            }
                        }

                        /*
                         var allWait = true
                         for (var i = 0; i < $scope.ororoTask.length; i++) {
                         var task = $scope.ororoTask[i];
                         if (task.status == "DOWNLOADING") {
                         allWait = false;
                         break
                         }
                         }
                         if (allWait) {
                         downloadNextVideo();
                         getSeries();
                         $scope.selectSeries($scope.seriesArray.length - 1)
                         }*/
                    });
                }
            }, 100);

            $("#seriesNameInput").keypress(function (event) {
                if (event.which == 13) {
                    event.preventDefault();
                    $scope.$apply(
                        function () {
                            $scope.findInWeb();
                        });
                }
            });

            $scope.searchOroroError = "";

            $scope.findInWeb = function () {
                $scope.searchOroroError = "";

                $resource("./download/find").get({
                        name: $scope.downloadFromWeb.name,
                        user: $scope.downloadFromWeb.user,
                        password: $scope.downloadFromWeb.password
                    }, function (res) {
                        if(res.res == "OK") {
                            $scope.seriesList = res.data
                        }else{
                            $scope.seriesList = [];
                            $scope.searchOroroError = "Произошла ошибка при попытке запроса. Скорее всего, вы неверно указали логин и пароль";
                        }
                    }
                );
            };

            $scope.checkWebUrl = function () {
                var url = $scope.downloadFromWeb.url

                $resource("./download/parse").query({
                    url: url
                }, function (res) {
                    console.log(res)
                });
            };

            $scope.onAddNewSeries = function () {
                $('#addTvModal').modal('show');
            };

            $scope.onEditSeries = function () {
                $scope.editedSeries.name = $scope.selectedSeries.name;

                $('#editTvModal').modal('show');
            };

            $scope.openWatchPage = function () {
                $('#ororoModal').modal('hide');

                $rootScope.updateSettings();

                $('#ororoModal').on('hidden.bs.modal', function () {
                    $location.path("watch");
                })
            };

            $scope.onEditSeriesDone = function () {
                $scope.selectedSeries.name = $scope.editedSeries.name;

                $resource("./gen/update_property").save({
                    name: "name",
                    id: $scope.selectedSeries.id,
                    cls: "Series",
                    value: $scope.selectedSeries.name
                }, function (res) {
                    $('#editTvModal').modal('hide');
                });
            };

            $scope.onAddNewSeriesDone = function () {
                if ($scope.newSeriesModel.name != undefined) {
                    $resource("./series/add").save({
                        name: $scope.newSeriesModel.name
                    }, function (res) {
                        if (res.res == "OK") {
                            $('#addTvModal').modal('hide');
                            $scope.seriesArray.push(res.data)
                            $scope.selectSeries($scope.seriesArray[$scope.seriesArray.length - 1])
                        }
                    });
                } else {
                    bootbox.alert($scope.l.add_please_enter_name);
                }
            };

            $scope.onDeleteSeries = function () {
                if ($scope.seasons.length != 0) {
                    bootbox.alert($scope.l.add_tv_series_contain_seasons)
                } else {
                    bootbox.confirm($scope.l.c_sure, function (res) {
                        if (res) {
                            $resource("./series/delete").get({id: $scope.selectedSeries.id}, function (res) {
                                del($scope.seriesArray, $scope.selectedSeries);
                            });
                        }
                    });
                }
            };

            function getSeries() {
                $resource("./series/get-all").query({}, function (res) {
                    $scope.seriesArray = res;

                    if ($scope.seriesArray.length != 0) {
                        $scope.selectSeries($scope.seriesArray[0])
                    }
                });
            }

            getSeries();

            $scope.selectSeries = function (series) {
                $scope.selectedSeries = series;
                $scope.videos = [];

                $scope.selectedSeason = null;

                $resource("./season/get-array").query({
                    seriesId: $scope.selectedSeries.id
                }, function (res) {
                    $scope.seasons = res;
                    if ($scope.seasons.length > 0) {
                        $scope.selectSeason($scope.seasons[0]);
                    }
                });
            };

            //Seasons
            $scope.onAddNewSeason = function () {
                $('#addSeasonModal').modal('show');
            };

            $scope.onOroroDialogOpen = function () {
                $('#ororoModal').modal('show');
            };

            $scope.onAddNewSeasonDone = function () {
                $resource("./season/add").save({
                    name: $scope.newSeasonModel.name,
                    series: $scope.selectedSeries.id
                }, function (res) {
                    if (res.res == "OK") {
                        $('#addSeasonModal').modal('hide');
                        $scope.seasons.push(res.data)
                        $scope.selectSeason($scope.seasons[$scope.seasons.length - 1])
                    }
                });
            };

            $scope.onDeleteSeason = function () {
                if ($scope.videos.length != 0) {
                    bootbox.alert($scope.l.add_del_season_error)
                } else {
                    bootbox.confirm($scope.l.c_sure, function (res) {
                        if (res) {
                            $resource("./season/delete").get({id: $scope.selectedSeason.id}, function (res) {
                                del($scope.seasons, $scope.selectedSeason);
                                $scope.selectedSeason = $scope.seasons[$scope.seasons.length - 1]
                            });
                        }
                    });
                }
            };

            $scope.onEditSeason = function () {
                copyProp($scope.selectedSeason, $scope.editedSeason);
                $('#editSeasonModal').modal('show');
            };

            $scope.onEditSeasonDone = function () {
                copyProp($scope.editedSeason, $scope.selectedSeason);
                $('#editSeasonModal').modal('hide');

                $resource("./season/update").query({
                    id: $scope.selectedSeason.id,
                    name: $scope.selectedSeason.name,
                    volume: $scope.selectedSeason.volume
                }, function (res) {

                });
            };

            $scope.deleteVideo = function (video) {
                $resource("./video/delete").get({
                    id: video.id
                }, function (res) {
                    if (res.res == "OK") {
                        var index = $scope.videos.indexOf(video);
                        $scope.videos.splice(index, 1);
                    } else {
                        bootbox.alert($scope.l.add_del_clips_error);
                    }
                });
            };


            $scope.selectSeason = function (season) {
                $scope.selectedSeason = season;

                $resource("./video/get-array").query({
                    seasonId: $scope.selectedSeason.id
                }, function (res) {
                    $scope.videos = res;
                });
            };

            $scope.onUploadVideos = function () {
                $('#uploadVideosModal').modal('show');
            };
            $resource("./download/create-cash").get({});


            $scope.filesToConvert = [];
            $scope.onSelectFilesToConvert = function () {
                $scope.$apply(
                    function () {
                        var inputJQ = $("#selectedFilesToConvert");
                        var input = inputJQ[0];
                        var files = input.files;

                        for (var i = 0; i < files.length; i++) {
                            var file = files[i]
                            file.editableName = file.name;
                            file.editableNum = $scope.files.length + 1;
                            file.inProcess = false;
                            $scope.filesToConvert.push(file);
                            file.status = $scope.l.add_ready_to_upload;
                        }


                        inputJQ.replaceWith(inputJQ = inputJQ.clone(true));
                    }
                );
            };

            $scope.onSelectFiles = function () {
                $scope.$apply(
                    function () {
                        var inputJQ = $("#selectedFiles");
                        var input = inputJQ[0];
                        var files = input.files;
                        console.log(files);
                        var subtitles = [];

                        for (var i = 0; i < files.length; i++) {
                            var file = files[i]
                            file.editableName = file.name;
                            var extension = file.name.split('.').pop();

                            if (extension == "srt") {
                                subtitles.push(file)
                            } else {
                                file.editableNum = $scope.files.length + 1;
                                $scope.files.push(file);
                            }
                        }

                        //Assign subtitles
                        for (var i = 0; i < subtitles.length; i++) {
                            var subtitle = subtitles[i];

                            for (var j = 0; j < $scope.files.length; j++) {
                                var file = $scope.files[j]
                                var pureName = file.name.substr(0, file.name.lastIndexOf('.'));
                                var pureSubName = subtitle.name.substr(0, subtitle.name.lastIndexOf('.'));

                                if (pureName == pureSubName) {
                                    file.subtitle = subtitle;
                                }
                            }
                        }

                        inputJQ.replaceWith(inputJQ = inputJQ.clone(true));
                    }
                );
            };

            function uploadNextVideo() {
                var nextVideo = null;
                for (var i = 0; i < $scope.files.length; i++) {
                    var file = $scope.files[i];
                    if (!file.finished) {
                        nextVideo = file;
                        break;
                    }
                }
                if (nextVideo != null) {
                    var fd = new FormData();
                    fd.append('file', nextVideo);
                    fd.append('seasonId', $scope.selectedSeason.id);
                    fd.append('editableName', nextVideo.editableName);
                    fd.append('editableNum', nextVideo.editableNum);

                    $http.post("./video/upload", fd, {
                            transformRequest: angular.identity,
                            headers: {'Content-Type': undefined}
                        })
                        .success(function (res) {
                            var videoId = res.data.id;

                            if (nextVideo.subtitle == null) {
                                file.finished = true;
                                uploadNextVideo();
                            } else {
                                var fd2 = new FormData();
                                fd2.append('file', nextVideo.subtitle);
                                fd2.append('videoId', videoId);

                                $http.post("./upload-subtitles", fd2, {
                                        transformRequest: angular.identity,
                                        headers: {'Content-Type': undefined}
                                    })
                                    .success(function (res) {
                                        file.finished = true;
                                        uploadNextVideo();
                                    });
                            }
                        })
                        .error(function () {
                        });

                } else {
                    $scope.selectSeason($scope.selectedSeason);
                }
            }

            $scope.onUploadSelectedVideos = function () {
                uploadNextVideo();
            };

            $scope.removeVideoInProcess = function (video) {
                bootbox.confirm($scope.l.add_sure_del_from_queue, function (result) {
                    if (result) {
                        $resource("./convert/remove").get({id: video.id}, function (res) {
                            del($scope.videosInProcess, video)
                        });
                    }
                });
            };

            $scope.convertInProcess = false;

            $scope.startConvert = function () {
                $scope.convertInProcess = true;

                var nextVideo;
                for (var i = 0; i < $scope.videosInProcess.length; i++) {
                    var video = $scope.videosInProcess[i];
                    if (video.status == "UPLOADED") {
                        nextVideo = video;
                        break;
                    }
                }
                if (nextVideo != null) {
                    nextVideo.status = "CONVERTING";
                    $resource("./convert/start").get({id: nextVideo.id}, function (res) {
                        del($scope.videosInProcess, nextVideo);
                        $scope.selectSeason($scope.selectedSeason);//Update videos
                        $scope.startConvert();
                    });
                } else {
                    $("#uploadVideosModal").modal('hide')
                }
            };

            $scope.videosInProcess = [];
            $scope.updateConvertTasks = function () {
                $resource("./convert/get-all").query({}, function (res) {
                    $scope.videosInProcess = res;

                    $scope.convertInProcess = false;
                    for (var i = 0; i < $scope.videosInProcess.length; i++) {
                        var video = $scope.videosInProcess[i];
                        if (video.status == "CONVERTING") {
                            $scope.convertInProcess = true;
                            break;
                        }
                    }
                });
            };
            $scope.updateConvertTasks();

            /*$interval(function () {
             $scope.updateConvertTasks();
             }, 5000);*/

            function uploadNextVideoToConvert() {
                var nextVideo = null;
                for (var i = 0; i < $scope.filesToConvert.length; i++) {
                    var file = $scope.filesToConvert[i];
                    if (!file.inProcess) {
                        nextVideo = file;
                        break;
                    }
                }
                if (nextVideo != null) {
                    var fd = new FormData();
                    fd.append('file', nextVideo);
                    fd.append('seasonId', $scope.selectedSeason.id);
                    fd.append('editableName', nextVideo.editableName);
                    fd.append('editableNum', nextVideo.editableNum);

                    file.inProcess = true;
                    file.status = $scope.l.add_status_uploading;
                    $http.post("./video/uploadToConvert", fd, {
                            transformRequest: angular.identity,
                            headers: {'Content-Type': undefined}
                        })
                        .success(function (res) {
                            $scope.updateConvertTasks();
                            del($scope.filesToConvert, nextVideo);
                            uploadNextVideoToConvert();
                        })
                        .error(function () {
                        });

                } else {
                    //End
                }
            }

            $scope.onConvertSelectedVideos = function () {
                uploadNextVideoToConvert();
            }
        });
    }
)
;
