package org.icememo.api

import org.icememo.MainData
import org.icememo.Result
import org.icememo.entity.Video
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("trans")
class TranscriptsController extends BaseController{

    @RequestMapping(value = "{id}.htm", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource get(@PathVariable("id") int id) {
        Video video = videoDao.get(id);
        File file = mainData.getTranscriptionsFile(video);
        if(file.exists()) {
            return new FileSystemResource(file)
        }else{
            throw new Exception("File ${file.absolutePath} not found")
        }
    }

    @RequestMapping(value = "exist", method = RequestMethod.GET)
    @ResponseBody
    public Result exist(int id) {
        Video video = videoDao.get(id);
        File file = mainData.getTranscriptionsFile(video);
        if(file.exists()) {
            return Result.getOk()
        }else{
            return Result.getError("Transcription not exists!")
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "upload")
    @ResponseBody
    public Result upload(Integer videoId,
                                  @RequestParam MultipartFile file) {
        Video video = videoDao.get(videoId)

        File videoFolder = new File(getMainFolder(), MainData.VIDEO_FOLDER);
        File htmlFile = new File(videoFolder, FilenameUtils.removeExtension(video.src) + ".htm")

        IOUtils.copy(file.getInputStream(), new FileOutputStream(htmlFile))

        return Result.getOk()
    }
}
