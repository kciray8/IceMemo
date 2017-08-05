var Common = function() {
    this.click = function(element){
        return browser.wait(function() {
            return element.click().then(
                function() {
                    return true;
                },
                function() {
                    console.log('not clickable');
                    return false;
                });
        });
    };

    this.waitElement = function(element){
        var EC = protractor.ExpectedConditions;
        browser.wait(EC.visibilityOf(element), 5000);
    };
};

module.exports = Common;


