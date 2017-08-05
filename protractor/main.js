var path = require('path');

getRandomString = function(length) {
    var string = '';
    var letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
    for (var i = 0; i < length; i++) {
        string += letters.charAt(Math.floor(Math.random() * letters.length));
    }
    return string;
};


var RootPage = function() {
    var navAddButton = element(by.id('navAddButton'));

    this.gotoAddPage = function() {
        navAddButton.click()
    };
};


describe('Login user 1', function() {
    var root = new RootPage();

    var AddPage = require('./pages/addPage');
    var addPage = new AddPage();

    var userName = getRandomString(6);

    it('init', function() {

    });

    it('create new user', function() {
        browser.get('http://localhost:8080/#/login');

        element(by.id("newUserName")).sendKeys(userName);
        element(by.id("createNewUserButton")).click();

        var users = element.all(by.repeater('user in users'));
        users.last().click();

        var userInHeader = element(by.id("userNameInHeader"));

        expect(userInHeader.getText()).toEqual(userName);
    });

    it('create season, series and video', function() {
        var seriesName = getRandomString(8);

        root.gotoAddPage();
        addPage.addSeries(seriesName);
        var series = element.all(by.repeater('series in seriesArray'));
        expect(series.count()).toEqual(1);
        var last = series.last();
        expect(last.getText()).toEqual(seriesName);

        var season1Name = getRandomString(8);
        var season2Name = getRandomString(8);
        addPage.addSeason(season1Name);
        addPage.addSeason(season2Name);

        var seasons = element.all(by.repeater('season in seasons'));
        expect(seasons.count()).toEqual(2);

        //Check "Delete season" function
        addPage.deleteSelectedSeason();
        expect(seasons.count()).toEqual(1);

        seasons = element.all(by.repeater('season in seasons'));

        expect(seasons.get(0).getText()).toEqual(season1Name);//Delete second season

        //Check edit season function and persistence after page reloading
        var panel = addPage.editSeasonPanel();
        panel.open();
        expect(panel.getName()).toEqual(season1Name);
        expect(panel.getVolume()).toEqual('1');

        season1Name = "E_" + getRandomString(4);
        var newVolume = "1.5";//!!!!!!!!!!!! FIX FORBIDDEN BUG !!!

        panel.setVolume(newVolume);
        panel.setName(season1Name);
        panel.save();

        browser.refresh();
        panel.open();
        expect(panel.getName()).toEqual(season1Name);
        expect(panel.getVolume()).toEqual(newVolume);
        panel.close();

        //Download video
        var uploadPanel = addPage.uploadVideoPanel();
        uploadPanel.open()

        uploadPanel.selectFile("C:\\Users\\kciray\\OneDrive\\IceSystem\\Projects\\Web\\SoundMemo\\Samples\\video1.webm\nC:\\Users\\kciray\\OneDrive\\IceSystem\\Projects\\Web\\SoundMemo\\Samples\\video1.srt");

        var video1Name = "V1_" + getRandomString(4);
        var video2Name = "V2_" + getRandomString(4);//For delete
        var video3Name = "V3_" + getRandomString(4);

        uploadPanel.setVideoName(0, video1Name);

        browser.pause()
    });

});

