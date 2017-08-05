package org.icememo.api

import org.icememo.Result
import org.icememo.Time
import org.icememo.dao.CardDao
import org.icememo.dao.VideoDao
import org.icememo.entity.*
import org.icememo.model.DayPlan
import org.icememo.model.FlashCardModel
import org.icememo.model.FullCardModel
import org.icememo.model.GroupModel
import org.icememo.model.post.PostCard
import org.icememo.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("card")
public class CardController extends BaseController {
    @Autowired
    CardDao cardDao

    @Autowired
    VideoDao videoDao

    @RequestMapping("delete")
    @ResponseBody
    public Result delete(Integer id) {
        Card card = cardDao.get(id)
        cardDao.delete(card)

        return Result.getOk()
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result add(@RequestBody PostCard postCard) {
        Card card = new Card(user: data.user.id, timeStart: postCard.timeStart,
                timeEnd: postCard.timeEnd, text: postCard.text, video: postCard.video)
        card.duration = postCard.timeEnd - postCard.timeStart;

        Video video = videoDao.get(postCard.video);

        Season season = seasonDao.get(video.season)
        card.setSeason(video.season)
        card.setSeries(season.series)

        if (season.subtitlesOffset != postCard.subtitlesOffset) {
            season.subtitlesOffset = postCard.subtitlesOffset
            seasonDao.update(season)
        }

        card.setDate(Time.getMs())
        card.videoShortNameCache = video.shortName;
        card.num = cardDao.getCardCount(postCard.video) + 1

        cardDao.save(card)

        for (FlashCardModel model : postCard.flashcards) {
            FlashCard flashCard = flashCardDao.get(model.id);
            flashCard.front = model.front
            flashCard.back = model.back
            flashCard.cardId = card.id
            flashCard.videoId = card.video
            flashCardDao.update(flashCard)

            for(GroupModel groupModel: model.groups){
                MyGroup group = groupDao.get(groupModel.id)
                group.size++
                groupDao.update(group)

                flashCardDao.addGroupToFlashcard(flashCard.id, group.id)
            }
        }

        if (!video.done) {
            video.done = true;
            videoDao.update(video)
        }

        return Result.getOk()
    }

    class CardsData {
        List<FullCardModel> cards;
        Double sumDuration = 0;
    }

    @RequestMapping(value = "get-need-repeat", method = RequestMethod.GET)
    @ResponseBody
    public CardsData getNeedRepeat() {
        CardsData cardsData = new CardsData()
        cardsData.cards = cardDao.cardsNeedToRepeat;

        Collections.shuffle(cardsData.cards)

        for (FullCardModel card : cardsData.cards) {
            cardsData.sumDuration += card.duration;
        }

        return cardsData
    }


    public static final REPEAT_NOW = "repeat_now";
    public static final REPEAT_LATER = "repeat_later";
    public static final HARD = "hard";
    public static final GOOD = "good";
    public static final EASY = "easy";

    @RequestMapping(value = "update-interval", method = RequestMethod.GET)
    @ResponseBody
    public Card updateInterval(Integer cardId, Integer interval, String answer) {
        Card card = cardDao.get(cardId)

        long day = 24 * 60 * 60 * 1000

        if ((answer == HARD) || (answer == GOOD) || (answer == EASY)) {
            long nextRepMs = Time.getMs() + (day * interval);
            card.nextRepetitionDate = DateUtils.getDayId(nextRepMs)
            card.lastRepetition = interval
        } else {
            if (answer == REPEAT_NOW) {
                card.nextRepetitionDate = Time.ms + (1000 * 30);//30 sec
                card.lastRepetition = 0;
            }
            if (answer == REPEAT_LATER) {
                card.nextRepetitionDate = Time.ms + (1000 * 60 * 10);//10 min
                card.lastRepetition = 0;
            }
        }

        card.lastRepetitionDate = Time.getMs()

        int lastRepetitionNum = card.repetitionNum

        if (answer == REPEAT_NOW) {
            card.repetitionNum = 1;
        } else if (answer == REPEAT_LATER) {
            card.repetitionNum = 2;
        } else {
            if (card.repetitionNum < 2) {
                card.repetitionNum = 2;
            }

            card.repetitionNum++
        }

        cardDao.update(card)

        RepeatHistory repeatHistory = new RepeatHistory(user: UID)
        repeatHistory.cardId = card.id
        repeatHistory.repetitionNum = card.repetitionNum
        repeatHistory.lastRepetitionNum = lastRepetitionNum
        if (answer == REPEAT_NOW) {
            repeatHistory.answer = RepeatHistory.ANSWER_NOW
        }
        if (answer == REPEAT_LATER) {
            repeatHistory.answer = RepeatHistory.ANSWER_LATER
        }

        if ((answer == HARD) || (answer == GOOD) || (answer == EASY)) {
            if (answer == HARD) {
                repeatHistory.answer = RepeatHistory.ANSWER_HARD
            }
            if (answer == GOOD) {
                repeatHistory.answer = RepeatHistory.ANSWER_GOOD
            }
            if (answer == EASY) {
                repeatHistory.answer = RepeatHistory.ANSWER_EASY
            }
        }
        if(repeatHistory.repetitionNum < repeatHistory.lastRepetitionNum){
            repeatHistory.answer = RepeatHistory.ANSWER_RESET
        }

        repeatHistory.ms = Time.ms
        repeatHistory.dayId = DateUtils.getDayId(repeatHistory.ms)
        if(rHistoryDao.exist(card.id, repeatHistory.dayId)){
            repeatHistory.todayFirst = false
        }else{
            repeatHistory.todayFirst = true
        }

        rHistoryDao.save(repeatHistory)

        return card
    }


    @RequestMapping(value = "suspend", method = RequestMethod.GET)
    @ResponseBody
    public Result suspend(Integer id) {
        Card card = cardDao.get(id)
        cardDao.suspendCard(card)

        RepeatHistory repeatHistory = new RepeatHistory(user: UID)
        repeatHistory.cardId = card.id
        repeatHistory.repetitionNum = card.repetitionNum
        repeatHistory.lastRepetitionNum = card.repetitionNum
        repeatHistory.answer = RepeatHistory.ANSWER_SUSPEND
        repeatHistory.ms = Time.ms
        repeatHistory.dayId = DateUtils.getDayId(repeatHistory.ms)

        rHistoryDao.save(repeatHistory)

        return Result.ok
    }

    @RequestMapping(value = "mark", method = RequestMethod.GET)
    @ResponseBody
    public Result mark(Integer id) {
        Card card = cardDao.get(id)
        card.marked = !card.marked;
        cardDao.update(card)

        return Result.getOk()
    }


    @RequestMapping(value = "stat-week", method = RequestMethod.GET)
    @ResponseBody
    List<CardDao.CardStat> getStatWeek(){
        return cardDao.getStatWeek()
    }

    @RequestMapping(value = "day-plan", method = RequestMethod.GET)
    @ResponseBody
    DayPlan getDayPlan(){
        return cardDao.getDayPlan()
    }

}