package org.icememo.dao

import org.icememo.entity.Series
import org.hibernate.criterion.Restrictions
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class SeriesDao extends BaseDao {
    void save(Series obj) {
        getSess().save(obj)
    }

    void delete(Series obj) {
        getSess().delete(obj)
    }

    Series get(Integer id) {
        Series obj = getSess().get(Series, id);
        if (obj.user != userId) {
            return null;
        }

        return obj;
    }

    List<Series> getAll() {
        List<Series> objects = getSess().createCriteria(Series.class)
                .add(Restrictions.eq("user", userId))
                .list();

        return objects;
    }

    void update(Series obj) {
        getSess().update(obj)
    }
}

