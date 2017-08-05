package org.icememo.dao

import org.hibernate.criterion.Order
import org.icememo.entity.User
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class UserDao extends BaseDao {
    User getLastUser(){
        User user = getSess()
                .createCriteria(User)
                .addOrder(Order.desc("lastLoginDate"))
                .setMaxResults(1)
                .uniqueResult();

        return user
    }

    void save(User obj) {
        getSess().save(obj)
    }

    User get(Integer id) {
        User obj = getSess().get(User, id);

        return obj;
    }

    void update(User obj) {
        getSess().update(obj)
    }

    List<User> getAll() {
        List<User> users = getSess().createCriteria(User)
                .list();

        return users;
    }

}

