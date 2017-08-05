package org.icememo.api

import org.icememo.entity.FlashCard
import org.icememo.entity.RepeatHistory
import org.icememo.model.RepeatStatModel
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("repeat-history")
class RepeatHistoryController extends BaseController{
    @RequestMapping(value = "get-all", method = RequestMethod.GET)
    @ResponseBody
    public List<RepeatHistory> getAll() {
        return rHistoryDao.all
    }

    @RequestMapping(value = "repeat-stat", method = RequestMethod.GET)
    @ResponseBody
    public RepeatStatModel repeatStat() {
        return rHistoryDao.getRepeatStat()
    }

    @RequestMapping(value = "today-flashcards", method = RequestMethod.GET)
    @ResponseBody
    public List<FlashCard> getTodayFlashcards() {
        return rHistoryDao.todayFlashcards
    }
}

