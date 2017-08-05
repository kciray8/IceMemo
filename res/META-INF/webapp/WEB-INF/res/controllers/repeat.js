app.controller('repeatController', function ($scope, $rootScope, $resource, $routeParams, $document, $interval, $window, Page, $location) {
    $rootScope.settings.then(function (settings) {
        Page.setTitle($scope.l.repeat_repeat)
        var allVideos;
        var preloadCard;
        $scope.currentCard;
        $scope.showSub;

        var firstPlayer;
        var secondPlayer;
        var currentPlayer;
        var needToReload;

        $scope.mode = "";
        $scope.repeatStatShowed = false;
        $scope.repeatStatInit = false;

        $scope.firstIsMain = function () {
            if (currentPlayer == firstPlayer) {
                return true;
            } else {
                return false;
            }
        };

        $scope.play = function () {
            currentPlayer.playbackRate = 1.0;

            currentPlayer.currentTime = $scope.currentCard.timeStart;
            currentPlayer.play();
        };

        $scope.playSlow = function () {
            currentPlayer.playbackRate = 0.5;

            currentPlayer.currentTime = $scope.currentCard.timeStart;
            currentPlayer.play();
        };

        $scope.playSlow80 = function () {
            currentPlayer.playbackRate = 0.8;

            currentPlayer.currentTime = $scope.currentCard.timeStart;
            currentPlayer.play();
        };

        $scope.pause = function () {
            currentPlayer.pause();
        };

        function getHiddenPlayer() {
            if (currentPlayer == firstPlayer) {
                return secondPlayer;
            } else {
                return firstPlayer;
            }
        }

        $scope.getInterval = function (card, answer) {
            if (card == null) {
                return 0;
            }

            if (card.repetitionNum == 0) {
                return 1;
            }

            if (card.repetitionNum == 1) {
                return 1;
            }
            if (card.repetitionNum == 2) {
                return 1;
            }

            if (card.repetitionNum == 3) {
                if (answer == $rootScope.C.GOOD) {
                    return 4;
                }
                if (answer == $rootScope.C.EASY) {
                    return 8;
                }
                if (answer == $rootScope.C.HARD) {
                    return 2;
                }
            }

            if (answer == $rootScope.C.HARD) {
                return Math.round(card.lastRepetition * 1.1);
            }
            if (answer == $rootScope.C.GOOD) {
                return Math.round(card.lastRepetition * 2);
            }
            if (answer == $rootScope.C.EASY) {
                return Math.round(card.lastRepetition * 2.5);
            }
            if (answer == $rootScope.C.REPEAT_NOW) {
                return 1;
            }

            alert("ERROR!!! getInterval() ans" + answer)
        };

        $scope.repeatNowArray = [];

        var replay = false;

        var removeCard = function (card) {
            console.log("REMOVE")

            $scope.cards.splice(card.numInArray, 1);

            //Update indexes
            for (var i = 0; i < $scope.cards.length; i++) {
                $scope.cards[i].numInArray = i;
            }
        };

        angular.element($window).on('keydown', function (e) {
            console.log(e.keyCode)
        });

        $scope.repeatStatData = {};

        $scope.updateList = function () {
            console.log($scope.cards);

            $scope.newCards = 0;
            $scope.repCards = 0;
            $scope.learnCards = 0;
            $scope.releaseCards = 0;

            for (var i = 0; i < $scope.cards.length; i++) {
                var card = $scope.cards[i];

                if (card.nextRepetitionDate == 0) {
                    $scope.newCards++;
                } else {
                    if ((card.repetitionNum == 0) || (card.repetitionNum == 1)) {
                        $scope.learnCards++;
                    }

                    if (card.repetitionNum == 2) {
                        $scope.releaseCards++;
                    }
                    if (card.repetitionNum >= 3) {
                        $scope.repCards++;
                    }
                }
            }

            if (!$scope.repeatStatInit) {
                $scope.repeatStatInit = true;
                if ($scope.repCards == 0) {
                    $scope.repeatStatShowed = true;
                }
            }

            if (($scope.repCards == 0) && (!$scope.repeatStatShowed)) {
                $scope.repeatStatShowed = true;
                currentPlayer.pause();
                $scope.showRepeatStat();
            }
        };

        $scope.showRepeatStat = function () {
            $resource("./repeat-history/repeat-stat").get({}, function (res) {
                $scope.repeatStatData = res;
                $('#repeatStat').modal();

                $resource("./repeat-history/today-flashcards").query({}, function (res) {
                    $scope.flashcardReview = res;
                });
            });
        };

        $scope.repeatStatPercent = function (value) {
            if (($scope.repeatStatData.all != undefined) && ($scope.repeatStatData.all != 0)) {
                return (value * 100) / $scope.repeatStatData.all
            } else {
                return 0
            }
        };

        $scope.currentCardIsMarked = function () {
            if ($scope.currentCard == null) {
                return false;
            }
            return $scope.currentCard.marked
        };

        $scope.markCurrentCard = function () {
            $resource("./card/mark").get({id: $scope.currentCard.id}, function (res) {
                $scope.currentCard.marked = !$scope.currentCard.marked;
            });
        };

        $scope.toNextCard = function (answer) {
            if (answer != null) {
                var interval = $scope.getInterval($scope.currentCard, answer);

                var currentCard = $scope.currentCard;
                if (currentCard != null) {
                    //Dirt thing
                    if (answer == $scope.C.REPEAT_NOW) {
                        currentCard.nextRepetitionDate = getMs() + (1000 * 30);
                        currentCard.repetitionNum = 1;
                    }
                    if (answer == $scope.C.REPEAT_LATER) {
                        currentCard.nextRepetitionDate = getMs() + (1000 * 60 * 10);
                        currentCard.repetitionNum = 2;
                    }

                    //Async update
                    $resource("./card/update-interval").get({
                        cardId: currentCard.id,
                        interval: interval,
                        answer: answer
                    }, function (res) {
                        currentCard.nextRepetitionDate = res.nextRepetitionDate;
                        currentCard.repetitionNum = res.repetitionNum;

                        $scope.updateList();
                    });
                }

                if ((answer == "good") || (answer == "easy") || (answer == "hard")) {
                    removeCard($scope.currentCard);
                }

                if (answer == $scope.C.REPEAT_NOW) {
                    $scope.currentCard.icon = "repeat_now";
                }

                if (answer == $scope.C.REPEAT_LATER) {
                    $scope.currentCard.icon = "repeat_later";
                }
            }

            $scope.showSub = false;

            $scope.currentCard = preloadCard;
            if (currentPlayer == firstPlayer) {
                currentPlayer = secondPlayer;
            } else {
                currentPlayer = firstPlayer;
            }
            replay = true;
            getHiddenPlayer().pause();
            currentPlayer.play();

            //Preload
            var nextCard = null;
            MAIN_LOOP:while (true) {
                if ($scope.cards.length == 0) {
                    $scope.endRepeat();
                    return
                }
                if ($scope.cards.length == 1) {
                    nextCard = $scope.currentCard;
                }

                //At first - repetition
                if (nextCard == null) {
                    for (var i = 0; i < $scope.cards.length; i++) {
                        var card = $scope.cards[i];

                        if ((card.repetitionNum >= 3) && (card != $scope.currentCard)) {
                            nextCard = card;
                        }
                    }
                }

                //Find cards from "cash"
                if (nextCard == null) {
                    var bigTime = -1;
                    for (var i = 0; i < $scope.cards.length; i++) {
                        var card = $scope.cards[i];

                        if ((card.nextRepetitionDate > bigTime) && (card.nextRepetitionDate < getMs()) && (card != $scope.currentCard)) {
                            nextCard = card;
                            bigTime = card.nextRepetitionDate;
                            //alert(card.repetitionNum)
                        }
                    }
                }

                //From forward
                if (nextCard == null) {
                    var smallestRepDate = getMs() + ($scope.C.DAY_MS * 100);
                    for (var i = 0; i < $scope.cards.length; i++) {
                        var card = $scope.cards[i];
                        if ((card.nextRepetitionDate < smallestRepDate) && (card != $scope.currentCard)) {
                            nextCard = card;
                            smallestRepDate = card.nextRepetitionDate;
                        }
                    }
                }
                break;
            }

            if (nextCard != null) {
                preloadCard = nextCard;
                getHiddenPlayer().src = nextCard.videoObj.src;
                getHiddenPlayer().currentTime = nextCard.timeStart;
                getHiddenPlayer().load();

                getHiddenPlayer().volume = $scope.getVolumeForVideo(nextCard.videoObj.season);
            }


            $scope.good_days = $scope.plu("day", $scope.getInterval($scope.currentCard, $scope.C.GOOD));
            $scope.easy_days = $scope.plu("day", $scope.getInterval($scope.currentCard, $scope.C.EASY));
            $scope.hard_days = $scope.plu("day", $scope.getInterval($scope.currentCard, $scope.C.HARD));
        };

        $interval(function () {
            if ($scope.currentCard != null) {
                if ((currentPlayer.currentTime > $scope.currentCard.timeEnd) && (!currentPlayer.paused)) {
                    currentPlayer.pause();
                    if (replay) {
                        currentPlayer.currentTime = $scope.currentCard.timeStart;
                        currentPlayer.play();
                        replay = false;
                    } else {
                        if ($scope.currentCard.repetitionNum < 2) {
                            $scope.showSub = true;
                        }
                    }
                }
            }
        }, 20);

        $scope.endRepeat = function () {
            $resource("./do-backup").get();

            $interval(function() {
                $resource("./card/day-plan").get({}, function (res) {
                    $scope.dayPlan = res;
                });
            }, 500, 1);

            $scope.closePage();
            $scope.mode = "end";
        };

        $scope.closePage = function () {
            getHiddenPlayer().pause();
            currentPlayer.pause();
        };

        $scope.onSelectCard = function (card) {
            $scope.selectedCard = card;
        };

        $scope.suspendCard = function (card) {
            $resource("./card/suspend").get({id: card.id}, function (res) {
                removeCard(card);
                $scope.toNextCard(null);
                $scope.updateList();
            });
        };

        $scope.loadData = function () {
            $scope.currentCard = null;
            $scope.showSub = false;
            needToReload = false;

            firstPlayer = $document.find("#firstPlayer")[0];
            secondPlayer = $document.find("#secondPlayer")[0];
            currentPlayer = firstPlayer;

            $resource("./season/get-all").query({}, function (res) {
                $scope.seasonsArray = res;
            });

            $scope.getVolumeForVideo = function (seasonId) {
                for (var i = 0; i < $scope.seasonsArray.length; i++) {
                    var season = $scope.seasonsArray[i];
                    if (seasonId == season.id) {
                        return season.volume;
                    }
                }
            };

            $resource("./card/get-need-repeat").get({}, function (res) {
                //Fix background video play bug
                if($location.path() != "/repeat"){
                    console.log("Fixed background video play bug");
                    return;
                }

                $scope.cards = res.cards;

                $scope.sumDuration = res.sumDuration;

                $resource("./video/get-all").query({}, function (res) {
                    allVideos = res;

                    for (var i = 0; i < $scope.cards.length; i++) {
                        for (var j = 0; j < allVideos.length; j++) {
                            if (allVideos[j].id == $scope.cards[i].video) {
                                $scope.cards[i].videoObj = allVideos[j];
                                break;
                            }
                        }

                        $scope.cards[i].icon = "wait";
                        $scope.cards[i].numInArray = i;
                    }

                    $scope.mode = "repeat";

                    $scope.toNextCard(null);//Preload first
                    $scope.toNextCard(null);//Play first and preload second

                    $scope.updateList();
                });
            });
        };
        $scope.loadData();
    });
});