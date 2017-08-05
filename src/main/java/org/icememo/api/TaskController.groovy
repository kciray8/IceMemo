package org.icememo.api

import org.icememo.utils.Tasks
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("task")
class TaskController extends BaseController{
    Tasks tasks = new Tasks(this);

    @RequestMapping(method = RequestMethod.GET, value = "load-transcripts")
    @ResponseBody
    String loadFriendTranscrips() {
        return tasks.loadFriendsTranscripts()
    }
}
