package org.icememo.dao

import org.hibernate.Criteria
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions
import org.icememo.Time
import org.icememo.entity.FlashCard
import org.icememo.entity.RepeatHistory
import org.icememo.model.RepeatStatModel
import org.icememo.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class RepeatHistoryDao extends BaseDao {
    void save(RepeatHistory obj) {
        getSess().save(obj)
    }

    void delete(RepeatHistory obj) {
        getSess().delete(obj)
    }

    void update(RepeatHistory obj) {
        getSess().update(obj)
    }

    List<RepeatHistory> getAll() {
        List<RepeatHistory> objects = getSess().createCriteria(RepeatHistory)
                .add(Restrictions.eq("user", userId))
                .list();

        return objects;
    }

    RepeatHistory get(Integer id) {
        RepeatHistory obj = getSess().get(RepeatHistory, id);
        if (obj.user != userId) {
            return null;
        }

        return obj;
    }

    boolean exist(Integer cardId, Long dayId) {
        Criteria criteria = getSess().createCriteria(RepeatHistory);
        criteria.add(Restrictions.eq("cardId", cardId));
        criteria.add(Restrictions.eq("dayId", dayId));
        criteria.setProjection(Projections.rowCount());
        long count = (Long) criteria.uniqueResult();
        long countZero = 0
        if (count == countZero) {
            return false
        } else {
            return true
        }
    }

    RepeatStatModel getRepeatStat() {
        def model = new RepeatStatModel()
        long dayId = DateUtils.getDayId(Time.ms)

        List result = getSess().createCriteria(RepeatHistory)
                .add(Restrictions.eq("user", userId))
                .add(Restrictions.gt("lastRepetitionNum", 2))
                .add(Restrictions.eq("dayId", dayId))
                .setProjection(Projections.projectionList()
                .add(Projections.groupProperty("answer"))
                .add(Projections.rowCount())).list();
        for (Object[] objects : result) {
            int answer = (int) objects[0]
            long count = (int) objects[1]
            if (answer == RepeatHistory.ANSWER_EASY) {
                model.easy = count
            }
            if (answer == RepeatHistory.ANSWER_GOOD) {
                model.good = count
            }
            if (answer == RepeatHistory.ANSWER_HARD) {
                model.hard = count
            }
            if (answer == RepeatHistory.ANSWER_RESET) {
                model.reset = count
            }
            if (answer == RepeatHistory.ANSWER_SUSPEND) {
                model.suspended = count
            }
        }

        return model
    }

    @Autowired
    FlashCardDao flashCardDao

    @Autowired
    CardDao cardDao

    List<FlashCard> getTodayFlashcards() {
        long dayId = DateUtils.getDayId(Time.ms)
        def flashcards = []

        List<Integer> ids = getSess().createCriteria(RepeatHistory)
                .add(Restrictions.eq("user", userId))
                .add(Restrictions.eq("dayId", dayId))
                .setProjection(Projections.distinct(Projections.property("cardId")))
                .list()

        for(Integer cardId: ids){
            List<FlashCard> fc = flashCardDao.getByCardId(cardId)
            flashcards.addAll(fc)
        }

        return flashcards
    }
}
