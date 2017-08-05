var commonJS = require('../common.js');
var path = require('path');
var common = new commonJS();

var AddPage = function () {
    var addTvModal = element(by.id('addTvModal'));
    var addSeriesButton = element(by.id('addSeriesButton'));
    var newSeriesNameInput = element(by.id('newSeriesName'));
    var addSeriesDoneButton = element(by.id('addSeriesDoneButton'));

    var addSeasonModal = element(by.id('addSeasonModal'));
    var addSeasonButton = element(by.id('addSeasonButton'));
    var deleteSeasonButton = element(by.id('deleteSeasonButton'));
    var newSeasonNameInput = element(by.id('newSeasonNameInput'));
    var addSeasonDoneButton = element(by.id('addSeasonDoneButton'));

    var editSeasonButton = element(by.id('editSeasonButton'));
    var editSeasonName = element(by.id('editSeasonName'));
    var editSeasonVolume = element(by.id('editSeasonVolume'));
    var editSeasonDoneButton = element(by.id('editSeasonDoneButton'));
    var editSeasonModalCloseButton = element(by.id('editSeasonModalCloseButton'));

    var uploadVideoButton = element(by.id('uploadVideoButton'));
    var selectedFilesInput = element(by.id('selectedFiles'));

    this.addSeries = function (name) {
        addSeriesButton.click();
        common.waitElement(newSeriesNameInput);
        newSeriesNameInput.clear();
        newSeriesNameInput.sendKeys(name);
        addSeriesDoneButton.click();
    };

    this.addSeason = function (name) {
        common.click(addSeasonButton);
        common.waitElement(newSeasonNameInput);
        newSeasonNameInput.clear();
        newSeasonNameInput.sendKeys(name);
        addSeasonDoneButton.click();
    };

    this.deleteSelectedSeason = function () {
        common.click(deleteSeasonButton)

        var dialogOK = $('button[data-bb-handler="confirm"]');
        common.click(dialogOK)
    };

    this.editSeasonPanel = function () {
        this.getName = function () {
            return editSeasonName.getAttribute('value')
        };
        this.setName = function (name) {
            editSeasonName.clear()
            editSeasonName.sendKeys(name)
        };

        this.getVolume = function () {
            return editSeasonVolume.getAttribute('value')
        };
        this.setVolume = function (name) {
            editSeasonVolume.clear()
            editSeasonVolume.sendKeys(name)
        };
        this.save = function () {
            common.click(editSeasonDoneButton);
        };

        this.open = function () {
            common.click(editSeasonButton);
        };

        this.close = function () {
            common.click(editSeasonModalCloseButton)
        };

        return this
    };

    this.uploadVideoPanel = function () {
        this.open = function () {
            common.click(uploadVideoButton)
        };

        this.selectFile = function (name) {
            common.waitElement(selectedFilesInput);
            selectedFilesInput.sendKeys(name)
        };

        this.setVideoName = function (row, name) {
            var files = element.all(by.repeater('file in files'));
            var file = files.get(row);
            var nameInput = file.element(by.model('file.editableName'))
            nameInput.clear();
            nameInput.sendKeys(name);
        };

        return this
    };

};

module.exports = AddPage;