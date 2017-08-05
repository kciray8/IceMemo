package org.icememo.api

import org.icememo.entity.FlashCard
import org.icememo.entity.MyGroup
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("group")
class GroupController extends BaseController {
    @RequestMapping(value = "add", method = RequestMethod.GET)
    @ResponseBody
    public MyGroup add(String name) {
        MyGroup group = new MyGroup(user: getUID())
        group.name = name

        groupDao.save(group)

        return group;
    }

    @RequestMapping(value = "get-all", method = RequestMethod.GET)
    @ResponseBody
    public List<MyGroup> getAll() {
        return groupDao.all;
    }

    @RequestMapping(value = "get-content", method = RequestMethod.GET)
    @ResponseBody
    public List<FlashCard> getContent(Integer id) {
        List<FlashCard> flashCards = []

        return flashCardDao.getGroupContent(id)
    }
}