package org.icememo

import org.apache.commons.io.FilenameUtils
import org.icememo.dao.ConvertTaskDao
import org.icememo.entity.ConvertTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
public class GlobalEventListener {
    @Autowired
    ConvertTaskDao convertTaskDao

    @Autowired
    MainData mainData

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        for (ConvertTask convertTask : convertTaskDao.allGlobal) {
            switch (convertTask.status) {
                case ConvertTask.Status.READY://Loading error
                case ConvertTask.Status.UPLOADING:
                    convertTaskDao.delete(convertTask)
                    break
                case ConvertTask.Status.CONVERTING://Convert error
                    File tempFolder = new File(mainData.getMainFolder(), "temp");
                    String fileName = FilenameUtils.getBaseName(convertTask.tempFileName)
                    File webmFile = new File(tempFolder, fileName + ".webm")
                    File subFile = new File(tempFolder, fileName + ".srt")
                    webmFile.delete()
                    subFile.delete()
                    convertTask.status = ConvertTask.Status.UPLOADED;
                    convertTaskDao.update(convertTask)

                    break;
                case ConvertTask.Status.UPLOADED://OK - waiting for convert
                    break;
            }
        }
    }
}
