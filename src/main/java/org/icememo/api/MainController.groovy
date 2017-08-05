package org.icememo.api

import org.hibernate.Version
import org.icememo.FixCollection
import org.icememo.Result
import org.icememo.SessionData
import org.icememo.Time
import org.icememo.desktop.OS
import org.icememo.lang.LangDB
import org.icememo.model.Info
import org.icememo.model.SearchResult
import org.icememo.model.SettingsModel
import org.icememo.utils.DateUtils
import org.icememo.utils.JarUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.SpringVersion
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonReader

@Controller
@Transactional
class MainController extends BaseController{
    public static String appVersion = "1.2"

    @Autowired
    SessionData data;


    FixCollection fix = new FixCollection(main: this)

    @RequestMapping("/")
    String root() {
        return "forward:/res/index.htm";
    }

    @RequestMapping(value = "/fix", method = RequestMethod.GET)
    @ResponseBody
    public Result fix() {
        fix.doFix()

        return Result.getOk()
    }

    @RequestMapping(value = "/get-settings", method = RequestMethod.GET)
    @ResponseBody
    public SettingsModel getSettings() {
        def model = new SettingsModel(user: data.user)
        if (debugMode()) {
            model.debug = true
        }
        model.demo = JarUtils.demo
        if(data.user.lang == "eng"){
            model.language = LangDB.instance.eng
        }
        if(data.user.lang == "rus"){
            model.language = LangDB.instance.rus
        }

        return model
    }

    @RequestMapping(value = "/set-lang", method = RequestMethod.GET)
    @ResponseBody
    public Result setLang(String lang) {
        data.user.lang = lang
        userDao.update(data.user)

        return Result.getOk()
    }
    @RequestMapping(value = "/pauseOnSubtitles", method = RequestMethod.GET)
    @ResponseBody
    public Result pauseOnSubtitles(Boolean value) {
        if(value != null){
            data.user.pauseOnSubtitles = value
            userDao.update(data.user)
        }

        return new Result(data: data.user.pauseOnSubtitles)
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public SearchResult search(String query, Integer limit) {
        if(limit == null){
            limit = 50
        }
        return cardDao.search(query, limit)
    }

    class KeyCorrect{
        boolean correct = false;
    }

    @RequestMapping(value = "/activate", method = RequestMethod.GET)
    @ResponseBody
    public KeyCorrect activate(String key) {
        URL url = new URL("http://kciray.com/checkx.php?key=$key");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        JsonReader rdr = Json.createReader(connection.inputStream)
        JsonObject obj = rdr.readObject();

        if(obj.getBoolean("correct")){
            makeout = true
            OS.prefs.put("makeout", "1");
            return new KeyCorrect(correct: true)
        }

        OS.prefs.put("makeout", "1");

        return new KeyCorrect(correct: true)
    }

    @RequestMapping(value = "/is-activated", method = RequestMethod.GET)
    @ResponseBody
    public KeyCorrect isActivated() {
        return new KeyCorrect(correct: true)
    }

    public static boolean makeout = true;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public Info info() {
        Info info = new Info()
        info.springVersion = SpringVersion.getVersion()
        info.hibernateVersion = Version.getVersionString()
        info.mainFolder = mainFolder.absolutePath
        info.buildDate = DateUtils.getNiceDate(DateUtils.jarBuildTime)
        info.appFolder = mainData.appFolder
        info.appVersion = appVersion;

        return info
    }

    @RequestMapping(value = "/do-backup", method = RequestMethod.GET)
    @ResponseBody
    public Result doBackup() {
        mainData.createBackup()

        return Result.getOk()
    }

    @RequestMapping(value = "/time", method = RequestMethod.GET)
    @ResponseBody
    public Result setTime(long days) {
        Time.offset = days
        return Result.getOk()
    }
}
