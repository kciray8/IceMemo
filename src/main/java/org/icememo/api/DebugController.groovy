package org.icememo.api

import org.icememo.Result
import org.icememo.Time
import org.icememo.entity.*
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("debug")
public class DebugController extends BaseController {
    @RequestMapping("exception")
    @ResponseBody
    public Result delete(Integer id) {
        throw new Exception("Test exception (Проверочка)")

        return Result.getOk()
    }

    @RequestMapping("exception-rus")
    @ResponseBody
    public Result exceptionRus() {
        throw new Exception("Исключение на русском");

        return Result.getOk()
    }

    @RequestMapping("rus")
    @ResponseBody
    public String rus() {
       return  "Строка";
    }

    @RequestMapping("zero")
    @ResponseBody
    public String zeroDivision() {
        int x = 0;
        return  232/x;
    }

    @RequestMapping("some-data")
    @ResponseBody
    public Result someData() {
        User user1 = new User(login: "A")
        userDao.save(user1)
        User user2 = new User(login: "B")
        userDao.save(user2)

        FlashCard flashCard = new FlashCard()
        flashCardDao.save(flashCard)

        return Result.getOk()
    }

    private void createDebugUser() {
        User user = new User()
        user.lastLoginDate = Time.ms
        userDao.save(user)
        user.login = "DebugUser${user.id}"
        userDao.update(user)

        data.user = user
    }

    private int createTestVideo() {
        Series series = new Series()
        series.user = data.userId
        series.name = "Series 12"
        seriesDao.save(series)

        Season season = new Season()
        season.name = "Season 1"
        season.user = data.userId
        season.series = series.id
        seasonDao.save(season)

        Video video = new Video()
        video.user = data.user.id
        video.name = "Video 1"
        video.season = season.id
        video.src = "30_2016_04_11__11_57_33_039.webm"
        videoDao.save(video)

        return video.id
    }

    private void createLearnCards(){
        int videoId = createTestVideo()

        Card card1 = createCard("Card 1 learn", 10, 12, videoId)
        cardDao.save(card1)
        Card card2 = createCard("Card 2 learn", 15, 18, videoId)
        cardDao.save(card2)
        Card card3 = createCard("Card 3 learn", 23, 26, videoId)
        cardDao.save(card3)
        Card card4 = createCard("Card 4 learn", 28, 33, videoId)
        cardDao.save(card4)
    }

    private void createRepeatCards(){
        int videoId = createTestVideo()

        Card card1 = createCard("Card 1", 10, 12, videoId)
        card1.lastRepetition = 4
        card1.lastRepetitionDate = Time.getMsMinusDays(3)
        card1.nextRepetitionDate = Time.ms
        card1.repetitionNum = 3
        cardDao.save(card1)

        Card card2 = createCard("Card 2", 15, 18, videoId)
        card2.lastRepetition = 10
        card2.lastRepetitionDate = Time.getMsMinusDays(20)
        card2.nextRepetitionDate = Time.ms
        card2.repetitionNum = 5
        card2.marked = true
        cardDao.save(card2)

        addFlashcard(card2.id, 1)
        addFlashcard(card2.id, 2)

        Card card3 = createCard("Card 3 ", 23, 26, videoId)
        card3.lastRepetition = 25
        card3.lastRepetitionDate = Time.getMsMinusDays(40)
        card3.nextRepetitionDate = Time.ms
        card3.repetitionNum = 5
        cardDao.save(card3)

        addFlashcard(card3.id, 3)

        Card card4 = createCard("Card 4 ", 30, 32, videoId)
        card4.lastRepetition = 10
        card4.lastRepetitionDate = Time.getMsMinusDays(20)
        card4.nextRepetitionDate = Time.ms
        card4.repetitionNum = 5
        cardDao.save(card4)
    }

    private void addFlashcard(int cardId, int num){
        FlashCard flashCard = new FlashCard(user: UID)
        flashCard.front = "front$num <b>b</b>"
        flashCard.back = "back$num <b>b</b>"
        flashCard.cardId = cardId
        flashCardDao.save(flashCard)
    }

    @RequestMapping("learn")
    @ResponseBody
    public Result learn() {
        if (!debugMode()) {
            return Result.getError("Only in debug mode")
        }
        createDebugUser()
        createLearnCards()

        return Result.getOk()
    }

    @RequestMapping("repeat")
    @ResponseBody
    public Result repeat() {
        if (!debugMode()) {
            return Result.getError("Only in debug mode")
        }
        createDebugUser()
        createRepeatCards()

        return Result.getOk()
    }

    @RequestMapping("repeat-and-learn")
    @ResponseBody
    public Result repeatAndLearn() {
        if (!debugMode()) {
            return Result.getError("Only in debug mode")
        }
        createDebugUser()
        createRepeatCards()
        createLearnCards()

        return Result.getOk()
    }

    @RequestMapping("hard-test")
    @ResponseBody
    public Result hardTest() {
        if (!debugMode()) {
            return Result.getError("Only in debug mode")
        }
        createDebugUser()
        Series series = new Series()
        series.user = data.userId
        series.name = "Series 12"
        seriesDao.save(series)

        Season season = new Season()
        season.name = "Season 1"
        season.user = data.userId
        season.series = series.id
        seasonDao.save(season)

        for (int i = 0; i < 30; i++) {

            Video video = new Video()
            video.user = data.user.id
            video.name = "Video $i"
            video.season = season.id
            video.src = "30_2016_04_11__11_57_33_039.webm"
            videoDao.save(video)

            Random rand = new Random();
            int cardCount = rand.nextInt(50) + 50;

            for (int n = 0; n < cardCount; n++) {
                createCard("Card $n", 10, 12, video.id)
            }

        }

        return Result.getOk()
    }

    private createCard(String text, Double start, Double end, int video) {
        Card card1 = new Card()
        card1.user = data.user.id
        card1.date = Time.getMs()
        card1.text = text
        card1.timeStart = start
        card1.timeEnd = end
        card1.duration = card1.timeEnd - card1.timeStart
        card1.video = video

        return card1
    }
}

