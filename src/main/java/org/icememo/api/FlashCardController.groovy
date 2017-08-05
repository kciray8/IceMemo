package org.icememo.api

import org.icememo.entity.FlashCard
import org.icememo.entity.Video
import org.icememo.model.FlashCardModel
import org.icememo.model.VideoContent
import org.icememo.utils.ReflectUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("flashcard")
class FlashCardController extends BaseController {
    @RequestMapping(value = "add", method = RequestMethod.GET)
    @ResponseBody
    public FlashCard add() {
        FlashCard flashCard = flashCardDao.create();
        flashCardDao.save(flashCard)

        return flashCard;
    }


    @RequestMapping(value = "similar", method = RequestMethod.GET)
    @ResponseBody
    public List<FlashCard> getSimilar(String fronts) {
        return flashCardDao.getSimilar(fronts)
    }

    @RequestMapping(value = "get-to-review", method = RequestMethod.GET)
    @ResponseBody
    public List<FlashCardModel> getFlashcardsToReview() {
        List<FlashCardModel> models = []
        List<FlashCard> flashCards = flashCardDao.all
        for (FlashCard flashCard : flashCards) {
            def model = new FlashCardModel()
            ReflectUtils.copyProp(flashCard, model)
            models << model
        }

        return models
    }

    private static String fixForAnki(String str) {
        return str.replace(",", "&#44;");
    }

    @RequestMapping(value = "data.csv")
    public ResponseEntity<String> getCSVFlashcards(HttpServletResponse response, Integer seasonId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/csv; charset=utf-8");

        StringBuilder builder = new StringBuilder()

        for (Video video : videoDao.getForSeason(seasonId)) {
            List<FlashCard> flashCards = flashCardDao.getByVideoId(video.id)
            for (FlashCard flashCard : flashCards) {
                builder.append(fixForAnki(flashCard.front))
                builder.append(",")
                builder.append(fixForAnki(flashCard.back))
                builder.append(System.getProperty("line.separator"))
            }
        }

        return new ResponseEntity<String>(builder.toString(), responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "all", method = RequestMethod.GET)
    @ResponseBody
    List<VideoContent> all(){
        return flashCardDao.videoContent
    }
}
