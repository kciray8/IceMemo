app.controller('videoWatchController', function ($timeout, $scope, $rootScope, $resource, $routeParams,
                                                 $document, $window, $http, $sce, $interval, Page) {

        $scope.groups = [];


        $scope.updateGroups = function () {
            $resource("./group/get-all").query({}
                , function (res) {
                    $scope.groups = res;

                    for (var i = 0; i < $scope.groups.length; i++) {
                        var group = $scope.groups[i];
                        group.label = group.name + " (" + group.size + ")";
                    }
                });
        };
        $scope.updateGroups();

        $rootScope.settings.then(function (settings) {
            $('#addNewCard').on('hidden.bs.modal', function () {
                cardPlayer.pause();
                $scope.editorOpen = false;
            });

            $scope.editorOpen = false;

            $scope.keyTarget = null;
            $scope.step = 2;
            $scope.subStep = 0.1;

            $scope.rightPanel = "subtitles";


            $scope.addGroupToSet = function (group) {
                var exist = false;
                for (var i = 0; i < $scope.currentGroups.length; i++) {
                    if ($scope.currentGroups[i].id == group.id) {
                        exist = true;
                    }
                }

                if (!exist) {
                    $scope.currentGroups.push(group);

                    group.fc = [];

                    $resource("./group/get-content").query({id: group.id}
                        , function (res) {
                            group.fc = res;
                        });

                    $scope.rightPanel = 'groups';
                }
            };

            $scope.onGroupSelect = function (flashcard, group) {
                var exist = false;
                for (var i = 0; i < flashcard.groups.length; i++) {
                    if (flashcard.groups[i].id == group.id) {
                        exist = true;
                    }
                }

                if (!exist) {
                    flashcard.newGroupName = "";
                    flashcard.groups.push(group);
                    $scope.addGroupToSet(group)
                } else {
                    flashcard.newGroupName = "";
                }
            };

            $scope.onGroupEnter = function (flashcard) {
                if (flashcard.newGroupName != "") {
                    $resource("./group/add").get({name: flashcard.newGroupName}
                        , function (res) {
                            flashcard.newGroupName = "";

                            //$scope.groups.push(res);
                            flashcard.groups.push(res);
                            //$scope.addGroupToSet(res)

                            $scope.updateGroups();
                        });
                }
            };


            angular.element($window).on('keydown', function (e) {
                $scope.$apply(function () {
                    if (e.keyCode == 17) {//CTRL
                        $scope.step = 1;
                        $scope.subStep = 0.5;
                    }

                    if (e.keyCode == 40) {
                        var nextSubtitles = null;
                        for (var i = 0; i < $scope.subtitlesStack.length; i++) {
                            var sub = $scope.subtitlesStack[i]
                            if (sub.obscured) {
                                nextSubtitles = sub;
                                break
                            }
                        }

                        if (nextSubtitles != null) {
                            nextSubtitles.view = nextSubtitles.text;
                            nextSubtitles.obscured = false
                        }
                        if ((nextSubtitles == null) && ($scope.subtitlesStack.length > 0)) {
                            //$scope.subtitlesStack.splice(0,1)
                        }
                    }

                    if (e.keyCode == 37) {
                        //$scope.subtitlesStack.splice(0, 1)
                    }

                    if ((e.keyCode == 32) && (!$scope.editorOpen)) {
                        var mainPlayer = $document.find("#mainPlayer")[0];

                        if (mainPlayer.paused) {
                            mainPlayer.play();
                        } else {
                            mainPlayer.pause();
                        }
                        e.preventDefault()
                    }
                    //console.log(e.keyCode);

                    if (e.keyCode == 38) {//UP
                        $scope.keyTarget = "left"
                    }
                    if (e.keyCode == 40) {//DOWN
                        $scope.keyTarget = "right"
                    }


                    if (e.keyCode == 37) {//LEFT
                        if ($scope.keyTarget == "left") {
                            //$scope.moveLeftLeft();
                        } else {
                            //$scope.moveRightLeft();
                        }
                    }
                    if (e.keyCode == 39) {//RIGHT
                        if ($scope.keyTarget == "left") {
                            //$scope.moveLeftRight();
                        } else {
                            //$scope.moveRightRight();
                        }
                    }

                    if (e.ctrlKey) {
                        var mainPlayer = $document.find("#mainPlayer")[0];
                        var step = 2;
                        if (e.keyCode == 37) {//LEFT
                            mainPlayer.currentTime -= step;
                        }
                        if (e.keyCode == 39) {//RIGHT
                            mainPlayer.currentTime += step;
                        }
                    }
                });
            });

            angular.element($window).on('keyup', function (e) {
                $scope.$apply(function () {
                    if (e.keyCode == 17) {//CTRL
                        $scope.step = 2;
                        $scope.subStep = 0.1;
                    }
                });
            });

            var videoId = $routeParams.id;
            var mainPlayer = $document.find("#mainPlayer")[0];
            var cardPlayer = $document.find("#cardPlayer")[0];

            mainPlayer.onerror = function (e) {
                switch (e.target.error.code) {
                    case e.target.error.MEDIA_ERR_DECODE:
                    case e.target.error.MEDIA_ERR_SRC_NOT_SUPPORTED:
                        $("#update-browser").modal();

                        break;
                    default:
                        break;
                }
            };

            $scope.similar = [];

            $interval(function () {
                var fronts = "";
                for (var i = 0; i < $scope.card.flashcards.length; i++) {
                    fronts += $scope.card.flashcards[i].front
                    if (i != $scope.card.flashcards.length - 1) {
                        fronts += ",";
                    }
                }

                $resource("./flashcard/similar").query({fronts: fronts}
                    , function (res) {
                        $scope.similar = res;
                    });
            }, 1000);

            mainPlayer.addEventListener('ended', function () {
                $scope.$apply(function () {
                    $scope.onVideoEnd();
                });
            }, false);

            $scope.onVideoEnd = function () {
                $resource("./do-backup").get();
                bootbox.alert("Video is ended!");
            };

            $scope.paused = true;

            $scope.input = {};
            $scope.input.pauseOnSubtitles = false;
            $scope.input.charsNumber = 20;

            $scope.pauseOnSubtitlesChange = function () {
                $resource("./pauseOnSubtitles").get({value: $scope.input.pauseOnSubtitles}
                    , function (res) {

                    });

            };

            $resource("./pauseOnSubtitles").get({}
                , function (res) {
                    $scope.input.pauseOnSubtitles = res.data;
                });


            cardPlayer.onplay = function () {
                $scope.paused = false;
            };
            cardPlayer.onpause = function () {
                $scope.paused = true;
            };

            var subtitle = null;

            $scope.pl = {};
            $scope.pl.timeStart = 0;
            $scope.pl.timeEnd = 0;
            $scope.pl.timeCurrent = 0;

            $scope.card = {};
            $scope.card.flashcards = [];

            $scope.cardsCounter = 0;

            $scope.currentSubArray = [];

            $scope.onAddFlashcard = function () {
                $resource("./flashcard/add").get({}, function (res) {
                    var flashcard = {};
                    flashcard.front = "";
                    flashcard.back = "";
                    flashcard.id = res.id;
                    flashcard.groups = []

                    $scope.card.flashcards.push(flashcard);
                });

            };

            $scope.loadTranscriptions = function () {
                $resource("./trans/exist").get({id: videoId}, function (res) {
                    if (res.res == "OK") {
                        $http({method: "GET", url: "./trans/" + videoId + ".htm"})
                            .success(function (data) {
                                    $scope.transcripts = data
                                    //REPLACE REGEXP
                                    //(\(.*?\))   <span style='background-color:yellow'>$1</span>
                                }
                            );
                    }
                });
            };
            $scope.loadTranscriptions();


            $scope.onAddCard = function () {
                $scope.currentGroups = [];

                $scope.editorOpen = true;
                $('#addNewCard').modal({
                    keyboard: false,
                    show: true,
                    backdrop: 'static'
                });

                mainPlayer.pause();
                cardPlayer.volume = mainPlayer.volume

                if ($scope.currentSubtitles != null) {
                    $scope.pl.timeStart = $scope.currentSubtitles.startTime;
                    $scope.pl.timeEnd = $scope.currentSubtitles.endTime;
                } else {
                    $scope.pl.timeStart = (mainPlayer.currentTime - 2);
                    $scope.pl.timeEnd = mainPlayer.currentTime;
                }

                if ($scope.pl.timeStart < 0) {
                    $scope.pl.timeStart = 0;
                }

                cardPlayer.src = $scope.video.src;
                cardPlayer.currentTime = $scope.pl.timeStart;

                $scope.showSubtitles()
            };

            $scope.subtitlesOffset = 0;


            $scope.moveSubLeft = function () {
                $scope.subtitlesOffset -= $scope.subStep;
                $scope.showSubtitles()
            };

            $scope.moveSubRight = function () {
                $scope.subtitlesOffset += $scope.subStep;
                $scope.showSubtitles()
            };

            $scope.showSubtitles = function () {
                var realTime = mainPlayer.currentTime + $scope.subtitlesOffset;

                $scope.currentSubArray = [];

                if (subtitle != null) {
                    var currentSub = $scope.currentSubtitles;
                    $scope.card.text = currentSub.text;

                    addSubToArray(currentSub.i - 3, subtitle, $scope.currentSubArray, false);
                    addSubToArray(currentSub.i - 2, subtitle, $scope.currentSubArray, false);
                    addSubToArray(currentSub.i - 1, subtitle, $scope.currentSubArray, false);
                    addSubToArray(currentSub.i, subtitle, $scope.currentSubArray, true);
                }
            };

            var addSubToArray = function (index, subtitle, arr, current) {
                if (index >= 0 && index < subtitle.length) {
                    var data = {};
                    data.text = subtitle[index].text;
                    data.current = current;
                    arr.push(data);
                }
            };

            function parseSubtitle(text) {
                var data = parser_old.fromSrt(text, true);
                if (data.length == 0) {//WebVTT format
                    var parser = new WebVTT.Parser(window, WebVTT.StringDecoder());

                    parser.oncue = function (cue) {
                        var cueObj = {};
                        cueObj.text = cue.text;
                        cueObj.startTime = cue.startTime;
                        cueObj.endTime = cue.endTime;

                        data.push(cueObj);
                    };

                    parser.parse(text);
                    parser.flush();
                } else {
                    for (var index = 0; index < data.length; ++index) {
                        data[index].startTime /= 1000;
                        data[index].endTime /= 1000;
                    }
                }
                subtitle = data;

                for (var i = 0; i < subtitle.length; ++i) {
                    var sub = subtitle[i];
                    sub.i = i;
                }
            }

            $scope.haveSubtitles = function () {
                if (subtitle != null) {
                    return true;
                } else {
                    return false;
                }
            };

            $scope.uploadFile = function (event) {
                var files = event.target.files;
                var file = files[0]
                var reader = new FileReader();

                reader.onload = function (e) {
                    var text = reader.result;

                    parseSubtitle(text)

                    var fd2 = new FormData();
                    fd2.append('file', file);
                    fd2.append('videoId', videoId);
                    $http.post("./upload-subtitles", fd2, {
                            transformRequest: angular.identity,
                            headers: {'Content-Type': undefined}
                        })
                        .success(function (res) {

                        });
                };

                reader.readAsText(file, "UTF8");
            };

            $scope.onCreateCard = function () {
                cardPlayer.pause();

                $resource("./card/add").save({
                    text: $scope.card.text,
                    timeStart: $scope.pl.timeStart,
                    timeEnd: $scope.pl.timeEnd,
                    video: videoId,
                    flashcards: $scope.card.flashcards,
                    subtitlesOffset: $scope.subtitlesOffset
                }, function (res) {
                    if (res.res == "OK") {
                        for (var i = 0; i < $scope.subtitlesStack.length; i++) {
                            var sub = $scope.subtitlesStack[i];
                            if (sub.obscured) {
                                sub.obscured = false;
                                sub.view = sub.text;
                            }
                        }

                        $scope.cardsCounter++;
                        $('#addNewCard').modal('hide');
                        $scope.card.flashcards = [];

                        mainPlayer.currentTime = $scope.pl.timeEnd - 1;
                        mainPlayer.play();

                        $scope.updateGroups();
                    }
                });
            };

            var scrollPos = 0;
            $scope.gotoPanel = function (newPanel) {

                var lastPanel = $scope.rightPanel;
                if (lastPanel == "script") {
                    scrollPos = $("#rightPanel").scrollTop()
                }
                $scope.rightPanel = newPanel;

                if (newPanel == "script") {
                    $timeout(function () {
                        $("#rightPanel").scrollTop(scrollPos)
                    });
                }
            };


            $scope.play = function () {
                cardPlayer.currentTime = $scope.pl.timeStart;
                cardPlayer.play();
            };

            $scope.stop = function () {
                cardPlayer.currentTime = $scope.pl.timeStart;
                cardPlayer.pause();
            };

            $scope.video = {};


            $scope.setRate = function (script, param) {
                script.rate = param;

                $resource("./script-answer").get({
                    scriptId: script.id,
                    rate: script.rate,
                    attemptId: $scope.attempt.id
                }, function (res) {
                    console.log(res)
                });

            };

            $scope.showScript = function (script) {
                script.speechVisible = !script.speechVisible;

                if (script.speechVisible) {
                    script.speechView = script.speech;
                } else {
                    script.speechView = script.speechStar;
                }
            };

            $resource("./video/get").get({
                id: videoId
            }, function (video) {
                $scope.cardsCounter = video.cardCount;
                $scope.video = video;

                video.src = video.src;

                //Download subtitles
                var input = video.src;
                var subtitleSrc = "./sub/" + video.id + ".srt";

                $http({
                    method: "GET", url: subtitleSrc,
                    transformResponse: function (value) {
                        parseSubtitle(value);
                    }
                });


                Page.setTitle($scope.video.name)

                $resource("./season/get").get({
                    id: $scope.video.season
                }, function (season) {
                    if (season.subtitlesOffset != undefined) {
                        $scope.subtitlesOffset = season.subtitlesOffset
                    }
                });
            });

            $scope.closeCardDialog = function () {
                cardPlayer.pause();
            };

            $scope.currentSub = null;

            $scope.selectedRate = 0;


            $scope.fix = 0.5;

            $scope.slowSpeed = function () {
                if (mainPlayer.playbackRate == 1) {
                    mainPlayer.playbackRate = 0.5;
                } else {
                    mainPlayer.playbackRate = 1;
                }

            };

            $scope.currentSubtitles = null;
            $scope.subtitlesStack = [];

            $scope.pausedSubtitles = null;
            $scope.pausedSubtitlesSum = 0;

            $interval(function () {
                if (cardPlayer.currentTime > $scope.pl.timeEnd) {
                    cardPlayer.pause();
                }

                if ((subtitle != null)) {
                    var realTime = mainPlayer.currentTime + $scope.subtitlesOffset;

                    /*
                     if ($scope.currentSubtitles != undefined) {
                     if (realTime > $scope.currentSubtitles.endTime) {
                     if ($scope.currentSubtitles != $scope.pausedSubtitles) {
                     if ($scope.input.pauseOnSubtitles) {
                     $scope.pausedSubtitlesSum += $scope.currentSubtitles.text.length;
                     if ($scope.pausedSubtitlesSum > 20) {
                     $scope.pausedSubtitlesSum = 0;
                     mainPlayer.pause();
                     $scope.pausedSubtitles = $scope.currentSubtitles;
                     return
                     } else {
                     $scope.pausedSubtitles = $scope.currentSubtitles;
                     }
                     }
                     }
                     }
                     }*/

                    var lastSub = null;
                    for (var i = 1; i < subtitle.length; ++i) {
                        var currentSubtitles = subtitle[i];

                        var half = (currentSubtitles.endTime - currentSubtitles.startTime) / 2;

                        if (realTime < (currentSubtitles.startTime + half)) {
                            lastSub = subtitle[i - 1];
                            break
                        }
                    }
                    if (lastSub == null) {
                        lastSub = subtitle[subtitle.length - 1];
                    }

                    if (lastSub != null) {
                        $scope.currentSubtitles = lastSub;

                        if (!mainPlayer.paused) {
                            var subtitlesElement = {};
                            subtitlesElement.text = $scope.currentSubtitles.text;
                            subtitlesElement.i = $scope.currentSubtitles.i;

                            subtitlesElement.view = $scope.currentSubtitles.text
                            /*.replace(/<b>/g, "")
                             .replace(/<\/b>/g, "")
                             .replace(/<i>/g, "")
                             .replace(/<\/i>/g, "")
                             .replace(/[a-zA-Z0-9]/g, "*")
                             .replace(/&#...;/g, "*");*/
                            subtitlesElement.obscured = true;

                            var weHave = false;
                            for (var j = 0; j < $scope.subtitlesStack.length; ++j) {
                                var element = $scope.subtitlesStack[j];
                                if (element.i == lastSub.i) {
                                    weHave = true;
                                    break
                                }
                            }
                            if (!weHave) {
                                $scope.subtitlesStack.push(subtitlesElement);
                            }

                            if ($scope.subtitlesStack.length > 4) {
                                $scope.subtitlesStack.splice(0, 1)
                            }
                        }
                    }

                }
            }, 20);

            var backStep = 0.5;
            $scope.moveLeftLeft = function () {
                $scope.pl.timeStart -= $scope.step / 10;
                cardPlayer.currentTime = $scope.pl.timeStart;
                cardPlayer.play();
            };
            $scope.moveLeftRight = function () {
                $scope.pl.timeStart += $scope.step / 10;
                cardPlayer.currentTime = $scope.pl.timeStart;
                cardPlayer.play();
            };
            $scope.moveRightLeft = function () {
                $scope.pl.timeEnd -= $scope.step / 10;

                cardPlayer.currentTime = $scope.pl.timeEnd - backStep;
                cardPlayer.play();
            };
            $scope.moveRightRight = function () {
                $scope.pl.timeEnd += $scope.step / 10;

                cardPlayer.currentTime = $scope.pl.timeEnd - backStep;
                cardPlayer.play();
            };

            $scope.onPaste = function (e) {
                var event = e.originalEvent;

                if (event.clipboardData == false) return false; //empty
                var items = event.clipboardData.items;
                if (items == undefined) return false;

                for (var i = 0; i < items.length; i++) {
                    if (items[i].type.indexOf("image") != -1) {

                        var blob = items[i].getAsFile();

                        var fd = new FormData();
                        fd.append('file', blob);
                        $http.post("./upload-img", fd, {
                                transformRequest: angular.identity,
                                headers: {'Content-Type': undefined}
                            })
                            .success(function (res) {
                                var html = "<img width='250' src='./img/" + res.data + "'/>"

                                document.execCommand("insertHTML", false, html);
                            })
                            .error(function () {
                            });

                        event.preventDefault()
                    }

                    if (items[i].type.indexOf("html") != -1) {
                        event.preventDefault();

                        var text = event.clipboardData.getData("text/plain");
                        document.execCommand("insertHTML", false, text);
                    }
                }
            };

            $scope.addTranscripts = function () {
                var inputJQ = $("#selectedTranscripts");
                var input = inputJQ[0];
                var file = input.files[0];

                var fd = new FormData();
                fd.append('file', file);
                fd.append('videoId', videoId);

                $http.post("./trans/upload", fd, {
                        transformRequest: angular.identity,
                        headers: {'Content-Type': undefined}
                    })
                    .success(function (res) {
                        $scope.loadTranscriptions();
                    });
            };

            function insertTextAtCursor(text) {
                var sel, range, html;
                if (window.getSelection) {
                    sel = window.getSelection();
                    if (sel.getRangeAt && sel.rangeCount) {
                        range = sel.getRangeAt(0);
                        range.deleteContents();
                        range.insertNode(document.createTextNode(text));
                    }
                } else if (document.selection && document.selection.createRange) {
                    document.selection.createRange().text = text;
                }
            }
        });
    }
)
;
