package org.icememo.api

import org.apache.commons.io.FileUtils
import org.icememo.Result
import org.icememo.convert.Converter
import org.icememo.entity.ConvertTask
import org.icememo.entity.Video
import org.icememo.model.ConvertTaskModel
import org.icememo.utils.ReflectUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("convert")
class ConvertTaskController extends BaseController {
    @RequestMapping(value = "get-all", method = RequestMethod.GET)
    @ResponseBody
    public List<ConvertTaskModel> getAll() {
        List<ConvertTaskModel> models = []

        for (ConvertTask task : convertTaskDao.all) {
            def model = new ConvertTaskModel()
            ReflectUtils.copyProp(task, model)
            models << model
        }

        return models

    }

    @RequestMapping(method = RequestMethod.GET, value = "remove")
    @ResponseBody
    Result remove(Integer id) {
        ConvertTask convertTask = convertTaskDao.get(id)

        convertTaskDao.delete(convertTask)
        if(converter != null){
            converter.thread.stop()
            converter.delete()
            converter = null
        }

        return Result.getOk()
    }

    Converter converter

    private String getFFmpegPath(){
        File ffmpegFolder = new File(mainData.appFolder, "ffmpeg")
        File ffmpegBin = new File(ffmpegFolder, "bin")
        File ffmpegExe = new File(ffmpegBin, "ffmpeg.exe")
        return ffmpegExe
    }

    @RequestMapping(method = RequestMethod.GET, value = "start")
    @ResponseBody
    Result start(Integer id) {
        ConvertTask convertTask = convertTaskDao.get(id)
        convertTask.status = ConvertTask.Status.CONVERTING
        convertTaskDao.update(convertTask)

        File srcFile = new File(getTempFolder(), convertTask.tempFileName)
        converter = new Converter(getFFmpegPath())
        converter.src = srcFile.absolutePath
        converter.convert()

        Video video = videoDao.create();
        video.name = convertTask.name
        video.season = convertTask.seasonId
        video.num = convertTask.num
        videoDao.save(video)

        String fileNameLetters = video.name.replaceAll("[^A-Za-z0-9]", "");

        File videoFolder = new File(getMainFolder(), "video");
        String pureName = video.id + "_" + fileNameLetters
        String fileName = pureName + ".webm";

        File videoFile = new File(videoFolder, fileName);
        FileUtils.copyFile(converter.webmFile, videoFile)

        if (converter.subtitleFile.exists()) {
            File subFile = new File(videoFolder, pureName + ".srt")
            FileUtils.copyFile(converter.subtitleFile, subFile)
        }

        video.src = fileName
        videoDao.update(video)

        converter.delete()
        converter = null
        convertTaskDao.delete(convertTask)

        return Result.getOk()
    }
}