package org.icememo.api

import org.icememo.Result
import org.icememo.entity.Video
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.icememo.utils.DateUtils
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Controller
class ResourceController extends BaseController{
    @RequestMapping("favicon.ico")
    String favicon() {
        return "forward:/res/favicon.ico";
    }

    @RequestMapping(value = "/sub/{id}.srt", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getVideoSRT(@PathVariable("id") int id) {
        Video video = videoDao.get(id);
        File file = mainData.getSubtitlesFile(video);

        return new FileSystemResource(file)
    }

    @RequestMapping(value = "/img/{name:.+}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getIMG(@PathVariable String name) {
        File folder = new File(getMainFolder(), "img");
        File file = new File(folder, name);

        return new FileSystemResource(file)
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload-img")
    @ResponseBody
    public Result<String> uploadIMG(@RequestParam MultipartFile file) {
        File imgFolder = new File(getMainFolder(), "img");
        imgFolder.mkdirs()
        String fileName = DateUtils.uniqueFileName + ".png";

        File imgFile = new File(imgFolder, fileName);

        FileOutputStream outputStream = new FileOutputStream(imgFile);
        IOUtils.copy(file.getInputStream(), outputStream)
        outputStream.close()

        return new Result<String>(data: fileName)
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload-subtitles")
    @ResponseBody
    public Result uploadSubtitles(Integer videoId,
                                  @RequestParam MultipartFile file) {
        Video video = videoDao.get(videoId)

        File videoFolder = new File(getMainFolder(), "video");
        File subtitleFile = new File(videoFolder, FilenameUtils.removeExtension(video.src) + ".srt")

        IOUtils.copy(file.getInputStream(), new FileOutputStream(subtitleFile))

        return Result.getOk()
    }
}
