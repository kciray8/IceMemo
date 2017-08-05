package org.icememo.api

import org.icememo.Result
import org.icememo.desktop.Main
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import java.util.prefs.Preferences

@Controller
@RequestMapping("e2e")
class E2ETestController extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "set1")
    @ResponseBody
    Result set1() {
        if (!debugMode()) {
            return Result.getError("Production mode!")
        }

        return Result.getOk()
    }


    static {
        /*Timer timer = new Timer()
        TimerTask task = { ->

            Preferences p = Preferences.userNodeForPackage(Main);
            String ake = "ake"
            String m = p.get("m" + ake + "out", "0")
            if (m == "1") {
                MainController.makeout = true
            } else {
                MainController.makeout = false
            }
            println("E2E check...")
        }
        timer.schedule(task, 2 * 1000, 60 * 1000)*/
    }
}
