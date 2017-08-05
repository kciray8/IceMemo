package org.icememo.dao

import org.hibernate.Criteria
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions
import org.icememo.Time
import org.icememo.entity.Card
import org.icememo.entity.FlashCard
import org.icememo.model.DayPlan
import org.icememo.model.FlashCardModel
import org.icememo.model.FullCardModel
import org.icememo.model.SearchResult
import org.icememo.utils.DateUtils
import org.icememo.utils.ReflectUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

import java.text.SimpleDateFormat

@Repository
@Transactional
class CardDao extends BaseDao {
    public static String SEASON_CMD = "season:";
    public static String SERIES_CMD = "series:";
    public static String VIDEO_CMD = "video:";
    public static String LIMIT_CMD = "limit:";
    public static String IS_CMD = "is:";

    @Autowired
    FlashCardDao flashCardDao

    void save(Card obj) {
        getSess().save(obj)
    }

    void delete(Card obj) {
        getSess().delete(obj)
    }

    void update(Card obj) {
        getSess().update(obj)
    }

    Card create() {
        Card obj = new Card(user: userId);

        return obj
    }

    Card get(Integer id) {
        Card obj = getSess().get(Card, id);
        if ((obj == null) || (obj.user != userId)) {
            return null;
        }

        return obj;
    }

    List<Card> getAll() {
        List<Card> cards = getSess().createCriteria(Card.class)
                .add(Restrictions.eq("user", userId))
                .list();

        return cards;
    }

    SearchResult search(String query, int limit) {
        return FullVersionFunctions.search(this, query, limit);
    }

    List<FullCardModel> getCardsNeedToRepeat() {
        List<FullCardModel> cardModels = new ArrayList<>();


        Criteria criteria = getSess().createCriteria(Card.class);

        criteria = criteria.add(Restrictions.eq("user", userId))
                .add(Restrictions.ne("suspended", true))
                .setMaxResults(1000)
                .add(Restrictions.lt("date", DateUtils.getDayId(Time.ms)))
                .add(Restrictions.lt("nextRepetitionDate", Time.getMs() + 1000 * 60 * 10))

        if (sessionData.user.offSR) {
            criteria = criteria.add(Restrictions.lt("repetitionNum", 3))
        }

        if (sessionData.user.beginTime != null) {
            criteria = criteria.add(Restrictions.gt("date", sessionData.user.beginTime))
        }

        List<Card> cards = criteria.list();

        for (Card card : cards) {
            FullCardModel fullCardModel = new FullCardModel()
            ReflectUtils.copyProp(card, fullCardModel)

            for (FlashCard innerFlashCard : flashCardDao.getByCardId(card.id)) {
                FlashCardModel flashCardModel = new FlashCardModel()
                ReflectUtils.copyProp(innerFlashCard, flashCardModel)
                fullCardModel.flashcards.add(flashCardModel)
            }

            cardModels.add(fullCardModel)
        }

        return cardModels;
    }

    DayPlan getDayPlan() {
        DayPlan dayPlan = new DayPlan()

        Criteria newClipsCriteria = getSess().createCriteria(Card.class)
                .add(Restrictions.eq("user", userId))
                .add(Restrictions.ne("suspended", true))
                .add(Restrictions.lt("date", DateUtils.getDayId(Time.ms)))
                .add(Restrictions.eq("nextRepetitionDate", 0L))

        fixCriteriaWithBeginTime(newClipsCriteria)

        dayPlan.newClips = (int) (newClipsCriteria.setProjection(Projections.rowCount()).uniqueResult());


        Criteria criteria = getSess().createCriteria(Card.class)
                .add(Restrictions.eq("user", userId))
                .add(Restrictions.ne("suspended", true))
                .add(Restrictions.lt("date", DateUtils.getDayId(Time.ms)))
                .add(Restrictions.ne("nextRepetitionDate", 0L))
                .add(Restrictions.lt("nextRepetitionDate", Time.getMs() + 1000 * 60 * 10))

        fixCriteriaWithBeginTime(criteria)

        dayPlan.repeatClips = (int) (criteria.setProjection(Projections.rowCount()).uniqueResult());


        Criteria criteria2 = getSess().createCriteria(Card.class)
                .add(Restrictions.eq("user", userId))
                .add(Restrictions.ne("suspended", true))
                .add(Restrictions.gt("date", DateUtils.getDayId(Time.ms)))

        fixCriteriaWithBeginTime(criteria2)


        dayPlan.createdClips = (int) (criteria2.setProjection(Projections.rowCount()).uniqueResult());

        return dayPlan
    }

    void fixCriteriaWithBeginTime(Criteria criteria) {
        if (sessionData.user.beginTime != null) {
            criteria = criteria.add(Restrictions.gt("date", sessionData.user.beginTime))
        }
    }

    int getCardCount(Integer videoId, Boolean suspended = null) {
        Criteria criteria = getSess().createCriteria(Card.class).
                add(Restrictions.eq("video", videoId));

        if (suspended != null) {
            criteria = criteria.add(Restrictions.eq("suspended", suspended))
        }

        return (int) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }

    int getAllCardCount() {
        Criteria criteria = getSess().createCriteria(Card.class).
                add(Restrictions.eq("user", userId))

        return (int) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }

    void suspendCard(Card card) {
        card.suspended = true;
        update(card)
    }

    class CardStat {
        int num
        int fcNum
        String periodStr
    }

    List<CardStat> getStatWeek() {
        List<CardStat> stats = []

        long weekStart = DateUtils.weekFirstMs()
        long weekEnd = DateUtils.weekLastMs()

        int allCard = getAllCardCount()
        int cards = 0

        loop:
        {
            Criteria criteria = getSess().createCriteria(Card)
                    .add(Restrictions.eq("user", userId))
                    .add(Restrictions.between("date", weekStart, weekEnd));
            int cardCount = (int) criteria.setProjection(Projections.rowCount()).uniqueResult();

            Criteria criteria2 = getSess().createCriteria(FlashCard)
                    .add(Restrictions.eq("user", userId))
                    .add(Restrictions.between("date", weekStart, weekEnd));
            int fcCount = (int) criteria2.setProjection(Projections.rowCount()).uniqueResult();

            CardStat cardStat = new CardStat()
            cardStat.num = cardCount
            cardStat.fcNum = fcCount
            String startDate = new SimpleDateFormat("dd MMM").format(new Date(weekStart));
            String endDate = new SimpleDateFormat("dd MMM").format(new Date(weekEnd));
            cardStat.periodStr = "$startDate - $endDate"
            stats << cardStat

            println(startDate)
            println(cards)
            println(allCard)

            cards += cardCount;
            if (cards < allCard) {
                weekStart -= DateUtils.WEEK_MS
                weekEnd -= DateUtils.WEEK_MS

                continue loop
            }
        }

        Collections.reverse(stats)

        return stats
    }
}
