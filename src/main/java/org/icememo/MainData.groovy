package org.icememo

import org.icememo.api.MainController
import org.icememo.desktop.OS
import org.icememo.entity.Video
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.jdbc.Work
import org.icememo.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

import javax.annotation.PostConstruct
import java.sql.Connection
import java.sql.SQLException

@Transactional
class MainData {
    String mainFolder;
    String appFolder
    public static String VIDEO_FOLDER = "video";

    @Autowired
    protected PlatformTransactionManager txManager;

    @Autowired
    SessionFactory sessionFactory;

    @PostConstruct
    private void init() {
        String makeout = OS.prefs.get("makeout", "0")
        if(makeout == "1"){
            MainController.makeout = true
        }

        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute({ TransactionStatus status ->
            dbInit();
        });
    }

    private void dbInit() {
        Timer timer = new Timer()
        TimerTask backupTask = { ->
            TransactionTemplate tmpl = new TransactionTemplate(txManager);
            tmpl.execute({ TransactionStatus status ->
                createBackup();
            });
        }
        int hours = 5;
        timer.schedule(backupTask, 0, 1000 * 60 * 60 * hours)
    }

    public boolean debugMode() {
        return mainFolder.contains("Debug");
    }

    public void createBackup() {
        if(debugMode()){
            println("Debug mode is on, backup won't be create")
            return
        }

        File folder = new File((String) getMainFolder(), "backup")
        File file = new File(folder, DateUtils.uniqueFileName + ".zip")
        String path = file.getAbsolutePath()

        File cloudFolder = new File("C:\\Users\\kciray\\Dropbox\\Backup\\REGULAR");//TODO FIXXXX
        File cloudFile = new File(cloudFolder, DateUtils.uniqueFileName + ".zip")
        String cloudPath = cloudFile.absoluteFile

        Session session = sessionFactory.getCurrentSession();

        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                connection.prepareStatement("BACKUP TO '$path'").executeUpdate()
                if(cloudFolder.exists()){
                    connection.prepareStatement("BACKUP TO '$cloudPath'").executeUpdate()
                }

                println("Backup done")
            }
        });
    }

    public File getTranscriptionsFile(Video video) {
        String name = video.src.substring(0, video.src.lastIndexOf('.')) + ".htm";
        File videoFolder = new File(getMainFolder(), VIDEO_FOLDER);
        File file = new File(videoFolder, name);

        return file
    }

    public File getSubtitlesFile(Video video) {
        String name = video.src.substring(0, video.src.lastIndexOf('.')) + ".srt";
        File videoFolder = new File(getMainFolder(), VIDEO_FOLDER);
        File file = new File(videoFolder, name);

        return file
    }

    public File getVideoFile(Video video) {
        File videoFolder = new File(getMainFolder(), "video");
        File videoFile = new File(videoFolder, video.src);
        return videoFile
    }

    public List<File> getAllVideoFiles(Video video) {
        def fileList = []
        File videoFile = getVideoFile(video)
        if (videoFile.exists()) {
            fileList << videoFile
        }

        File transFile = getTranscriptionsFile(video)
        if (transFile.exists()) {
            fileList << transFile
        }

        File subFile = getSubtitlesFile(video)
        if (subFile.exists()) {
            fileList << subFile
        }

        return fileList
    }
}
