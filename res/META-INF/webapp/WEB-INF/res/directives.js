app.directive('popover', function () {
    return function (scope, elem) {
        elem.popover();
    }
});

app.directive('tooltip', function () {
    return function (scope, elem) {
        elem.tooltip();
    }
});

app.directive('contenteditable', ['$sce', function ($sce) {
    return {
        restrict: 'A', // only activate on element attribute
        require: '?ngModel', // get a hold of NgModelController
        link: function (scope, element, attrs, ngModel) {
            if (!ngModel) return; // do nothing if no ng-model

            // Specify how UI should be updated
            ngModel.$render = function () {
                element.html($sce.getTrustedHtml(ngModel.$viewValue || ''));
            };

            // Listen for change events to enable binding
            element.on('blur keyup change', function () {
                scope.$evalAsync(read);
            });
            //read(); // initialize

            // Write data to the model
            function read() {
                var html = element.html();

                // When we clear the content editable the browser leaves a <br> behind
                // If strip-br attribute is provided then we strip this out
                if (attrs.stripBr && html == '<br>') {
                    html = '';
                }
                ngModel.$setViewValue(html);
            }
        }
    };
}]);

app.directive('help', function () {
    return {
        templateUrl: 'res/templates/help.html',
        replace: true,
        scope: {
            text: '@'
        }
    }
});

app.directive('dayPlan', function () {
    return {
        templateUrl: 'res/templates/dayPlan.html',
        replace: true
    }
});

app.directive('baseTree', function () {
    return {
        templateUrl: 'res/templates/baseTree.html',
        replace: true,
        scope: {
            terms: '='
        }
    }
});

app.directive('customOnChange', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var onChangeHandler = scope.$eval(attrs.customOnChange);
            element.bind('change', onChangeHandler);
        }
    };
});

function getKeyText(keyCode) {
    if (keyCode == 13) {
        return "Enter"
    }

    if (keyCode == 32) {
        return "Space"
    }

    if (keyCode == 27) {
        return "Esc"
    }

    if (keyCode == 192) {
        return "~"
    }

    return String.fromCharCode(keyCode);
}

app.directive('svideo', function () {
    return {
        link: function (scope, element, attrs) {
            var video = scope.video;

            if (scope.videoInit != null) {
                scope.videoInit(element, video);
            }

            element[0].onvolumechange = function (e) {
                scope.$apply(function () {
                    video.seasonVolume = e.target.volume;
                });
            };
        }
    }
});

app.directive('smartButton', function ($window, $interval) {
    return {
        templateUrl: 'res/templates/smart-button.html',
        replace: true,
        transclude: true,
        scope: {
            glyph: '@',
            eventHandler: '&ngClick',
            keyCode: '@',
            ngClass: "=",
            red: '@'
        },
        link: function (scope, element, attrs) {
            scope.active = false;

            var keyCode = scope.keyCode;
            var allowed = true;

            if (keyCode != undefined) {
                element[0].title = "HotKey: " + getKeyText(keyCode);

                angular.element($window).on('keydown', function (e) {
                    if (!element.is(':visible')) {
                        return;
                    }

                    if (event.repeat != undefined) {
                        allowed = !event.repeat;
                    }
                    if (!allowed) return;
                    allowed = false;

                    if (e.keyCode == keyCode) {
                        e.preventDefault();
                        scope.active = true;

                        scope.stop = $interval(function () {
                            scope.active = false;
                        }, 1000, 1);
                    }
                });
                angular.element($window).on('keyup', function (e) {
                    allowed = true;

                    if (e.keyCode == keyCode) {
                        if (scope.active) {
                            if (!element.is(':visible')) {
                                return;
                            }
                            element.triggerHandler('click');
                        }

                        e.preventDefault();
                        scope.active = false;

                        $interval.cancel(scope.stop);
                        scope.stop = undefined;
                    }
                });
            }
        }
    }
});

app.directive('starRating', function () {
    return {
        scope: {
            rating: '=',
            maxRating: '@',
            readOnly: '@',
            click: "&",
            mouseHover: "&",
            mouseLeave: "&"
        },
        restrict: 'EA',
        templateUrl: 'res/templates/starTemplate.html',
        compile: function (element, attrs) {
            if (!attrs.maxRating || (Number(attrs.maxRating) <= 0)) {
                attrs.maxRating = '5';
            }
        },
        controller: function ($scope, $element, $attrs) {
            $scope.maxRatings = [];

            for (var i = 1; i <= $scope.maxRating; i++) {
                $scope.maxRatings.push({});
            }

            $scope._rating = $scope.rating;

            $scope.$watch('rating', function (newValue, oldValue) {
                $scope._rating = $scope.rating;
            });

            $scope.isolatedClick = function (param) {
                if ($scope.readOnly == 'true') return;

                $scope.rating = $scope._rating = param;
                $scope.hoverValue = 0;
                $scope.click({
                    param: param
                });
            };

            $scope.isolatedMouseHover = function (param) {
                if ($scope.readOnly == 'true') return;

                $scope._rating = 0;
                $scope.hoverValue = param;
                $scope.mouseHover({
                    param: param
                });
            };

            $scope.isolatedMouseLeave = function (param) {
                if ($scope.readOnly == 'true') return;

                $scope._rating = $scope.rating;
                $scope.hoverValue = 0;
                $scope.mouseLeave({
                    param: param
                });
            };
        }
    };
});

app.directive('flashcardCompact', function () {
    return {
        scope: {
            front: '@',
            back: '@'
        },
        templateUrl: 'res/templates/flashcard-compact.html',
        replace: true,
        link: function (scope, element, attrs) {
            $(element).popover({html: true, trigger: 'hover', placement: 'left'})
        }
    }
});