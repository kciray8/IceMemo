package org.icememo.dao

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.icememo.MainData
import org.icememo.SessionData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@Transactional
public class BaseDao {
    @Autowired
    protected SessionFactory sessionFactory;

    @Autowired
    SessionData sessionData;

    Session getSess(){
        return sessionFactory.getCurrentSession()
    }

    int getUserId(){
        return sessionData.user.id;
    }

    @Autowired
    MainData mainData;


    public File getMainFolder() {
        return new File(mainData.mainFolder);
    }

    public void commit(){
        getSess().getTransaction().commit();
    }
}
