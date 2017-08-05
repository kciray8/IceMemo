package org.icememo.api

import org.apache.commons.io.IOUtils
import org.icememo.MultipartFileSender
import org.icememo.Result
import org.icememo.entity.ConvertTask
import org.icememo.entity.Season
import org.icememo.entity.Series
import org.icememo.entity.Video
import org.icememo.model.VideoModel
import org.icememo.model.VideoStatModel
import org.icememo.utils.ReflectUtils
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("video")
class VideoController extends BaseController {
    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    public VideoModel get(Integer id) {
        Video video = videoDao.get(id)

        VideoModel model = new VideoModel()
        ReflectUtils.copyProp(video, model)
        model.src = "./video/${video.id}"
        model.cardCount = cardDao.getCardCount(id)

        return model
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    public Result delete(Integer id) {
        Video video = videoDao.get(id)
        int cardCount = cardDao.getCardCount(id)

        if (cardCount == 0) {
            videoDao.delete(video)
        } else {
            return Result.getError("notEmpty")
        }

        return Result.getOk()
    }

    @RequestMapping(value = "get-all", method = RequestMethod.GET)
    @ResponseBody
    public List<VideoModel> getAll() {
        List<VideoModel> videos = []

        for (Video video : videoDao.getAll()) {
            VideoModel model = new VideoModel()
            ReflectUtils.copyProp(video, model)
            model.src = "./video/${video.id}"
            videos << model
        }

        return videos
    }

    @RequestMapping(value = "get-stat", method = RequestMethod.GET)
    @ResponseBody
    public List<VideoStatModel> getStat(Integer seasonId) {
        List<VideoStatModel> list = []
        List<Video> videos = videoDao.getForSeason(seasonId);
        for (Video video : videos) {
            VideoStatModel model = new VideoStatModel()
            model.name = video.name
            model.cards = cardDao.getCardCount(video.id)
            model.flashcards = flashCardDao.getCount(video.id)
            list.add(model)
        }

        return list
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @ResponseBody
    public void getVideo(@PathVariable("id") int id,
                         HttpServletResponse response, HttpServletRequest httprequest) {

        File videoFile = mainData.getVideoFile(videoDao.get(id));

        //Dirt fix
        try {
            MultipartFileSender.fromPath(videoFile.toPath())
                    .with(httprequest)
                    .with(response)
                    .serveResource();
        } catch (e) {
        }
    }


    class VideoExampleModel {
        String name
        String season
        Double seasonVolume
        String id
        String seasonName
    }

    @RequestMapping(value = "get-examples", method = RequestMethod.GET)
    @ResponseBody
    public List<VideoExampleModel> getExamples() {
        List<Season> seasons = seasonDao.all
        List<VideoExampleModel> videos = [];
        for (Season season : seasons) {
            Video video = videoDao.getForSeason(season.id).getAt(0)
            def model = new VideoExampleModel()
            model.name = video.name
            model.season = video.season
            model.id = video.id
            model.seasonName = season.name
            model.seasonVolume = season.volume

            videos << model
        }

        return videos
    }

    @RequestMapping(value = "get-array", method = RequestMethod.GET)
    @ResponseBody
    public List<Video> getVideosForSeason(Integer seasonId) {
        List<Video> videos = videoDao.getForSeason(seasonId)

        return videos.toSorted { a, b -> a.num <=> b.num }
    }

    @RequestMapping(method = RequestMethod.POST, value = "upload")
    @ResponseBody
    public Result<Video> upload(Integer seasonId, String editableName, Integer editableNum,
                                @RequestParam MultipartFile file) {


        Video video = videoDao.create();
        video.name = editableName
        video.season = seasonId
        video.num = editableNum
        videoDao.save(video)

        String fileNameLetters = editableName.replaceAll("[^A-Za-z0-9]", "");

        File videoFolder = new File(getMainFolder(), "video");
        String fileName = video.id + "_" + fileNameLetters + ".webm";

        File videoFile = new File(videoFolder, fileName);
        IOUtils.copy(file.getInputStream(), new FileOutputStream(videoFile))

        video.src = fileName
        videoDao.update(video)

        return new Result<Video>(data: video)
    }

    Thread convertThread;

    @RequestMapping(method = RequestMethod.POST, value = "uploadToConvert")
    @ResponseBody
    @Transactional
    public Result uploadToConvert(Integer seasonId, String editableName, Integer editableNum,
                                  @RequestParam MultipartFile file) {

        String fileExt = file.getOriginalFilename().split("\\.")[1];

        Season season = seasonDao.get(seasonId)
        Series series = seriesDao.get(season.series)

        ConvertTask convertTask = new ConvertTask(user: getUID())
        convertTask.seasonId = seasonId
        convertTask.seasonName = series.name + " - " + season.name
        convertTask.name = editableName
        convertTask.num = editableNum
        convertTaskDao.save(convertTask)

        String fileNameLetters = editableName.replaceAll("[^A-Za-z0-9]", "");

        File tempFolder = new File(getMainFolder(), "temp");
        tempFolder.mkdirs()
        String fileName = convertTask.id + "_" + fileNameLetters + ".$fileExt";

        File videoFile = new File(tempFolder, fileName);

        convertTask.tempFileName = fileName
        convertTask.status = ConvertTask.Status.UPLOADING
        convertTaskDao.update(convertTask)
        FileOutputStream outputStream = new FileOutputStream(videoFile)
        IOUtils.copy(file.getInputStream(), outputStream)
        outputStream.close()
        convertTask.status = ConvertTask.Status.UPLOADED
        convertTaskDao.update(convertTask)

        return Result.getOk()
    }
}
