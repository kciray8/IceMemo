package org.icememo.dao

import org.icememo.entity.Video
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.icememo.api.VideoController
import org.icememo.utils.JarUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class VideoDao extends BaseDao {
    void save(Video video) {
        if(JarUtils.demo){
            if(all.size() > 5){
                return;
            }
        }

        getSess().save(video)
    }

    @Autowired
    VideoController videoController

    void delete(Video video) {
        //Delete all video files
        for(File file: mainData.getAllVideoFiles(video)){
            file.delete()
        }

        getSess().delete(video)
    }

    Video create() {
        Video block = new Video(user: userId);

        return block
    }

    Video get(Integer id) {
        Video obj = getSess().get(Video, id);
        if (obj.user != userId) {
            return null;
        }

        return obj;
    }

    List<Video> getAll() {
        List<Video> videos = getSess().createCriteria(Video.class)
                .add(Restrictions.eq("user", userId))
                .list();

        return videos;
    }

    List<Video> getForSeason(Integer seasonId) {
        Criteria criteria = getSess().createCriteria(Video.class)
                .addOrder(Order.asc("num"))
                .add(Restrictions.eq("user", userId))
                .add(Restrictions.eq("season", seasonId));


        return criteria.list();
    }

    void update(Video obj) {
        getSess().update(obj)
    }
}
