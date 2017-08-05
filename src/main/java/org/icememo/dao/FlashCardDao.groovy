package org.icememo.dao

import org.hibernate.Criteria
import org.hibernate.criterion.Criterion
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions
import org.icememo.Time
import org.icememo.entity.FCToGroup
import org.icememo.entity.FlashCard
import org.icememo.entity.Video
import org.icememo.model.VideoContent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class FlashCardDao extends BaseDao {
    void save(FlashCard obj) {
        getSess().save(obj)
    }

    void update(FlashCard obj) {
        getSess().update(obj)
    }

    FlashCard create() {
        FlashCard obj = new FlashCard(user: userId);
        obj.date = Time.ms

        return obj
    }

    FlashCard get(Integer id) {
        FlashCard obj = getSess().get(FlashCard, id);
        if (obj.user != userId) {
            return null;
        }

        return obj;
    }

    List<FlashCard> getByCardId(Integer cardId) {
        List<FlashCard> flashCards = getSess().createCriteria(FlashCard.class)
                .add(Restrictions.eq("user", userId))
                .add(Restrictions.eq("cardId", cardId))
                .list();

        return flashCards;
    }

    List<FlashCard> getByVideoId(Integer videoId) {
        List<FlashCard> flashCards = getSess().createCriteria(FlashCard.class)
                .add(Restrictions.eq("user", userId))
                .add(Restrictions.eq("videoId", videoId))
                .list();

        return flashCards;
    }

    Integer getCount(Integer videoId) {
        return getSess().createCriteria(FlashCard).
                add(Restrictions.eq("videoId", videoId)).
                setProjection(Projections.rowCount()).uniqueResult();
    }

    List<FlashCard> getAll() {
        List<FlashCard> flashCards = getSess().createCriteria(FlashCard)
                .add(Restrictions.eq("user", userId))
                .list();

        return flashCards;
    }

    void addGroupToFlashcard(int fcId, int groupId) {
        FCToGroup fcToGroup = new FCToGroup(user: userId)

        fcToGroup.flashcard = fcId
        fcToGroup.myGroup = groupId

        getSess().save(fcToGroup)
    }

    List<FlashCard> getGroupContent(int groupId) {
        List<FlashCard> res = []

        List<FCToGroup> fcToGroups = getSess().createCriteria(FCToGroup.class)
                .add(Restrictions.eq("user", userId))
                .add(Restrictions.eq("myGroup", groupId))
                .list();

        for (FCToGroup fcToGroup : fcToGroups) {
            FlashCard flashCard = get(fcToGroup.flashcard)
            res << flashCard
        }

        return res
    }

    List<FlashCard> getSimilar(String fronts) {
        if(fronts == ""){
            return []
        }

        Criteria criteria = getSess().createCriteria(FlashCard)
        criteria.setMaxResults(10)
        criteria.add(Restrictions.eq("user", userId))

        List<String> frontsList = fronts.split(",")

        List<Criterion> criterions = []
        for (String front : frontsList) {
            String str = front
            if (str.length() > 3) {
                str = str.substring(0, 3)
            }
            if(str != "") {
                criterions << Restrictions.ilike("front", str, MatchMode.ANYWHERE)
            }
        }

        criteria.add(Restrictions.or((Criterion[])criterions.toArray()))

        return criteria.list()
    }

    @Autowired
    VideoDao videoDao

    List<VideoContent> getVideoContent(){
        List<VideoContent> res = []

        for(Video video: videoDao.all){
            VideoContent videoContent = new VideoContent()
            videoContent.name = video.src

            for(FlashCard flashCard: getByVideoId(video.id)){
                videoContent.cardsShort += ", " + flashCard.front
            }

            res << videoContent
        }

        return res
    }

}