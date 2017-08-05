package org.icememo.dao

import org.hibernate.Criteria
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions
import org.icememo.api.MainController
import org.icememo.api.SeasonController
import org.icememo.entity.Card
import org.icememo.entity.Season
import org.icememo.entity.Video
import org.icememo.model.SearchResult
import org.icememo.model.SeasonStatModel
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody


class FullVersionFunctions {
    static SearchResult search(CardDao cardDao, String query, int limit) {
        if(!MainController.makeout){
            return new SearchResult()
        }

        String[] limiters = query.split("\\s+");
        SearchResult result = new SearchResult()

        Criteria criteria = cardDao.getSess().createCriteria(Card.class)
        criteria.setMaxResults(limit)
        criteria.add(Restrictions.eq("user", cardDao.getUserId()))

        for (String limiter : limiters) {
            if (limiter.startsWith(cardDao.SEASON_CMD)) {
                int seasonId = limiter.replaceAll(cardDao.SEASON_CMD, "").toInteger()
                criteria.add(Restrictions.eq("season", seasonId))
                continue
            }

            if (limiter.startsWith(cardDao.SERIES_CMD)) {
                int id = limiter.replaceAll(cardDao.SERIES_CMD, "").toInteger()
                criteria.add(Restrictions.eq("series", id))
                continue
            }

            if (limiter.startsWith(cardDao.VIDEO_CMD)) {
                int id = limiter.replaceAll(cardDao.VIDEO_CMD, "").toInteger()
                criteria.add(Restrictions.eq("video", id))
                continue
            }

            if (limiter.startsWith(cardDao.LIMIT_CMD)) {
                limit = limiter.replaceAll(cardDao.LIMIT_CMD, "").toInteger()
                criteria.setMaxResults(limit)
                continue
            }

            if (limiter.startsWith(cardDao.IS_CMD)) {
                String function = limiter.replaceAll(cardDao.IS_CMD, "")
                switch (function) {
                    case "marked":
                        criteria.add(Restrictions.eq("marked", true))
                        break
                    case "suspended":
                        criteria.add(Restrictions.eq("suspended", true))
                        break
                }

                continue
            }

            criteria.add(Restrictions.ilike("text", limiter, MatchMode.ANYWHERE));
        }

        result.cards = criteria.list()

        criteria.setProjection(Projections.rowCount())
        criteria.setMaxResults(-1)

        result.length = (Long) criteria.uniqueResult();

        return result;
    }

    @RequestMapping(value = "get-stat", method = RequestMethod.GET)
    @ResponseBody
    public static SeasonStatModel getStat(SeasonController controller, Integer seasonId) {
        if(!MainController.makeout){
            return new SeasonStatModel(cards: -1, cardsAvg: -1, name: "Error")
        }

        def model = new SeasonStatModel()
        Season season = controller.seasonDao.get(seasonId)
        model.name = season.name

        int videosDoneCount = 0;

        List<Video> videos = controller.videoDao.getForSeason(seasonId);
        for (Video video : videos) {
            int cardNum = controller.cardDao.getCardCount(video.id)

            model.videos += video.volume

            if(cardNum > 0) {
                model.videosDone += video.volume
                model.flashcards += controller.flashCardDao.getCount(video.id)
                model.cards += cardNum
                videosDoneCount += video.volume
            }
        }

        model.cardsAvg = -1
        model.flashcardsAvg = -1

        if ((videosDoneCount != 0)&&(videosDoneCount == model.videos)) {
            model.cardsAvg = model.cards / videosDoneCount
            model.flashcardsAvg = model.flashcards / videosDoneCount
        }

        return model
    }
}
