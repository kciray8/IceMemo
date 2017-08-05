package org.icememo.dao

import org.icememo.entity.Season
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class SeasonDao extends BaseDao {
    void save(Season obj) {
        getSess().save(obj)
    }

    void delete(Season obj) {
        getSess().delete(obj)
    }


    Season get(Integer id) {
        Season obj = getSess().get(Season, id);
        if (obj.user != userId) {
            return null;
        }

        return obj;
    }

    List<Season> getAll() {
        List<Season> objects = getSess().createCriteria(Season.class)
                .add(Restrictions.eq("user", userId))
                .list();

        return objects;
    }

    List<Season> getForSeries(Integer id) {
        List<Season> objects = getSess().createCriteria(Season.class)
                .add(Restrictions.eq("user", userId))
                .add(Restrictions.eq("series", id))
                .addOrder(Order.asc("name"))
                .list();

        return objects;
    }

    void update(Season obj) {
        getSess().update(obj)
    }
}

