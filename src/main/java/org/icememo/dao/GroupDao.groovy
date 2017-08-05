package org.icememo.dao

import org.hibernate.criterion.Restrictions
import org.icememo.entity.MyGroup
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class GroupDao extends BaseDao {
    void save(MyGroup obj) {
        getSess().save(obj)
    }

    void delete(MyGroup obj) {
        getSess().delete(obj)
    }

    void update(MyGroup obj) {
        getSess().update(obj)
    }

    List<MyGroup> getAll() {
        List<MyGroup> objects = getSess().createCriteria(MyGroup)
                .add(Restrictions.eq("user", userId))
                .list();

        return objects;
    }

    MyGroup get(Integer id) {
        MyGroup obj = getSess().get(MyGroup, id);
        if (obj.user != userId) {
            return null;
        }
        return obj
    }
}