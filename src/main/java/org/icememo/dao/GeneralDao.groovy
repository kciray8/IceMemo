package org.icememo.dao

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class GeneralDao extends BaseDao {
    void save(Object obj) {
        getSess().save(obj)
    }

    void update(Object obj) {
        getSess().update(obj)
    }

    Object get(Class clazz, Serializable id) {
        return getSess().get(clazz, id)
    }

    List<Object> getAll(Class clazz) {
        List<Object> objects = getSess().createCriteria(clazz)
                .list();

        return objects;
    }
}