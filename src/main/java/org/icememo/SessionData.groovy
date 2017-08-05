package org.icememo

import org.icememo.dao.ConvertTaskDao
import org.icememo.dao.UserDao
import org.icememo.entity.User
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct

class SessionData implements Serializable {
    @Autowired
    UserDao userDao;

    @Autowired
    ConvertTaskDao convertTaskDao

    User user

    public File getFilesDir() {
        String rootPath = System.getProperty("catalina.home");
        return new File(rootPath, "files");
    }

    public boolean debugMode() {
        return mainFolder.contains("Debug");
    }

    public File getHomeDir() {
        File userHomeDir = new File(getFilesDir(), user.login);

        if (!userHomeDir.exists()) {
            userHomeDir.mkdirs();
        }
        return userHomeDir;
    }

    public int getUserId() {
        return user.id
    }

    String mainFolder;

    @PostConstruct
    private void init() {
        println("Init...")
        user = userDao.getLastUser();
        if (user == null) {
            user = new User()
            user.login = "User"
            user.lastLoginDate = Time.ms
            userDao.save(user)
        }
    }

}

