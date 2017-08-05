package org.icememo.dao

import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.icememo.entity.ConvertTask
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class ConvertTaskDao extends BaseDao {
    void save(ConvertTask obj) {
        getSess().save(obj)
    }

    void delete(ConvertTask obj) {
        getSess().delete(obj)
        File tempFolder = new File(getMainFolder(), "temp");
        File tempFile = new File(tempFolder, obj.tempFileName)
        if(tempFile.exists()){
            tempFile.delete()
        }
    }

    void update(ConvertTask obj) {
        getSess().update(obj)
    }

    ConvertTask get(Integer id) {
        ConvertTask obj = getSess().get(ConvertTask, id);
        if (obj.user != userId) {
            return null;
        }

        return obj;
    }

    List<ConvertTask> getAll() {
        List<ConvertTask> tasks = getSess().createCriteria(ConvertTask)
                .add(Restrictions.eq("user", userId))
                .addOrder(Order.asc("id"))
                .list();

        return tasks;
    }
    List<ConvertTask> getAllGlobal() {
        List<ConvertTask> tasks = getSess().createCriteria(ConvertTask)
                .addOrder(Order.asc("id"))
                .list();

        return tasks;
    }

}