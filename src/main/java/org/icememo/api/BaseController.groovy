package org.icememo.api

import org.hibernate.SessionFactory
import org.icememo.MainData
import org.icememo.SessionData
import org.icememo.dao.*
import org.springframework.beans.factory.annotation.Autowired

class BaseController {
    @Autowired
    VideoDao videoDao

    @Autowired
    CardDao cardDao

    @Autowired
    SeriesDao seriesDao

    @Autowired
    SeasonDao seasonDao

    @Autowired
    FlashCardDao flashCardDao

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    SessionData data;

    @Autowired
    UserDao userDao;

    @Autowired
    GeneralDao generalDao;

    @Autowired
    RepeatHistoryDao rHistoryDao

    @Autowired
    ConvertTaskDao convertTaskDao

    @Autowired
    GroupDao groupDao

    @Autowired
    MainData mainData;

    public File getMainFolder() {
        return new File(mainData.mainFolder);
    }
    public File getTempFolder(){
        return new File(getMainFolder(), "temp");
    }
    public File getVideoFolder(){
        return new File(getMainFolder(), "video");
    }

    public boolean debugMode() {
        return data.mainFolder.contains("Debug");
    }

    public getUID(){
        return data.user.id
    }
}
