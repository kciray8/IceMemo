package org.icememo.api

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.icememo.Result
import org.icememo.entity.Season
import org.icememo.entity.Series
import org.icememo.entity.Video
import org.icememo.model.WebVideo
import org.icememo.utils.DateUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import sun.misc.BASE64Encoder

import javax.json.Json
import javax.json.JsonArray
import javax.json.JsonObject
import javax.json.JsonReader

@Controller
@Transactional
@RequestMapping("download")
class DownloadController extends BaseController {
    @RequestMapping(method = RequestMethod.GET, value = "parse")
    @ResponseBody
    List<WebVideo> parse(String url) {
        String seriesNum = url.split("#")[1];
        println("NUM" + seriesNum)

        List<WebVideo> videos = []
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select(".tab-pane").select("[id=$seriesNum]");
        println(doc)

        return videos
    }

    class TVSeriesWeb {
        int id
        String name
        String posterUrl
    }

    List<TVSeriesWeb> seriesWebList
    String cashedUser = "test@example.com"
    String cashedPassword = ""

    @RequestMapping(method = RequestMethod.GET, value = "find")
    @ResponseBody
    Result<List<TVSeriesWeb>> find(String name, String user, String password) {
        Result<List<TVSeriesWeb>> result = Result.getOk()

        result.data = []

        try {
            loadWebList(user, password);

            for (TVSeriesWeb tvSeriesWeb : seriesWebList) {
                if (StringUtils.containsIgnoreCase(tvSeriesWeb.name, name)) {
                    result.data << tvSeriesWeb
                }
                if (result.data.size() >= 6) {
                    break
                }
            }
        } catch (Throwable t) {
            t.printStackTrace()
            return Result.getError("Exception " + t.message)
        }

        return result
    }

    private HttpURLConnection createConnection(String urlStr, String user, String password) {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "IceMemo");

        String userPassword = "$user:$password";
        String encoding = new BASE64Encoder().encode(userPassword.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encoding);

        return connection
    }

    private void loadWebList(String user, String password) {
        println("LoadWebList... $user $password")

        if ((seriesWebList == null) || (cashedUser != user) || (cashedPassword != password)) {
            HttpURLConnection connection = createConnection("https://ororo.tv/api/v2/shows", user, password)
            JsonReader rdr = Json.createReader(connection.inputStream)

            JsonObject obj = rdr.readObject();
            JsonArray results = obj.getJsonArray("shows");
            seriesWebList = []
            for (JsonObject shows : results.getValuesAs(JsonObject.class)) {
                TVSeriesWeb seriesWeb = new TVSeriesWeb()
                seriesWeb.name = shows.getString("name", "");
                seriesWeb.id = shows.getInt("id", 0);
                seriesWeb.posterUrl = shows.getString("poster_thumb", "");
                seriesWebList << seriesWeb
            }

            cashedUser = user
            cashedPassword = password
        }
    }

    class SeasonWeb {
        int num
        List<VideoWeb> list = []
    }

    class VideoWeb {
        int id
        String name
        int num
        boolean check = true
    }


    @RequestMapping(method = RequestMethod.GET, value = "get-seasons")
    @ResponseBody
    Result<List<SeasonWeb>> getSeasons(String seriesId, String user, String password) {
        List<SeasonWeb> res = []

        try {
            HttpURLConnection connection = createConnection("https://ororo.tv/api/v2/shows/$seriesId", user, password)
            JsonReader rdr = Json.createReader(connection.inputStream)

            JsonObject obj = rdr.readObject();
            JsonArray results = obj.getJsonArray("episodes");
            for (JsonObject shows : results.getValuesAs(JsonObject.class)) {
                VideoWeb video = new VideoWeb()
                video.id = shows.getInt("id", 0);
                video.name = shows.getString("name", "");

                String number = shows.getString("number", "88")
                video.num = new Integer(number);

                int seasonId = shows.getInt("season", 0);
                SeasonWeb currentSeason;
                for (SeasonWeb seasonWeb : res) {
                    if (seasonWeb.num == seasonId) {
                        currentSeason = seasonWeb
                        break
                    }
                }
                if (currentSeason == null) {
                    currentSeason = new SeasonWeb()
                    currentSeason.num = seasonId
                    res << currentSeason
                }
                currentSeason.list << video
            }

            res.sort(new Comparator<SeasonWeb>() {
                int compare(SeasonWeb o1, SeasonWeb o2) {
                    return o1.num - o2.num
                }
            })

            for (SeasonWeb seasonWeb : res) {
                seasonWeb.list.sort(new Comparator<VideoWeb>() {
                    int compare(VideoWeb o1, VideoWeb o2) {
                        return o1.num - o2.num
                    }
                })
            }
        }catch (Throwable t){
            t.printStackTrace()
            return Result.getError(t.message)
        }

        return Result.getOk(res)
    }

    class OroroTask {
        enum STATUS {
            DOWNLOADING, ADDED
        }
        int progress = 0
        STATUS status = STATUS.DOWNLOADING
        int videoId
    }
    List<OroroTask> tasks = []

    @RequestMapping(method = RequestMethod.GET, value = "ororo-task-list")
    @ResponseBody
    List<OroroTask> ororoTaskList() {
        return tasks
    }


    private static final int BUFFER_SIZE = 4096;

    @RequestMapping(method = RequestMethod.GET, value = "ororo-task")
    @ResponseBody
    Result downloadFromOroroTask(String episodeId, String user, String password) {

        OroroTask task = new OroroTask()
        tasks << task

        HttpURLConnection connection = createConnection("https://ororo.tv/api/v2/episodes/$episodeId", user, password)
        JsonReader rdr = Json.createReader(connection.inputStream)
        JsonObject obj = rdr.readObject();
        println(obj)
        JsonArray subtitles = obj.getJsonArray("subtitles");

        JsonObject enSub = subtitles.getValuesAs(JsonObject.class).get(0)//Default
        for (JsonObject sub : subtitles.getValuesAs(JsonObject.class)) {
            String url = sub.getString("url", "")
            String lang = sub.getString("lang", "")
            if (lang == "en") {
                enSub = sub
            }
        }
        String subUrl = enSub.getString("url", "")

        String videoUrlStr = obj.getString("webm_url", null)
        String videoExt = ".webm"
        if (videoUrlStr == null) {
            videoUrlStr = obj.getString("mp4_url", null)
            videoExt = ".mp4"
        }
        String videoName = obj.getString("name", null)
        String videoNumber = obj.getString("number", null)
        int videoId = obj.getInt("id", 0)
        task.videoId = videoId

        String videoNameCleared = videoName.replaceAll("[^A-Za-z0-9]", "");
        String videoUnique = videoNameCleared + "_" + DateUtils.uniqueFileName
        File subtitleFile = new File(getVideoFolder(), videoUnique + ".srt")
        File videoFile = new File(getVideoFolder(), videoUnique + videoExt)
        def videoOutStream = new FileOutputStream(videoFile)

        InputStream subtitleStream = new URL(subUrl).openStream();
        def subOutStream = new FileOutputStream(subtitleFile)
        IOUtils.copy(subtitleStream, subOutStream);
        subOutStream.close()

        URL videoUrl = new URL(videoUrlStr);

        int contentLength = videoUrl.openConnection().getContentLength();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        long totalBytesRead = 0;
        int percentCompleted = 0;

        for (; ;) {

            HttpURLConnection videoConn = (HttpURLConnection) videoUrl.openConnection();
            //videoConn.setRequestProperty("Range", "bytes=" + totalBytesRead + "-");
            InputStream videoIS = videoConn.getInputStream();

            while ((bytesRead = videoIS.read(buffer)) != -1) {
                videoOutStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                percentCompleted = (int) (totalBytesRead * 100 / contentLength);

                println("Download percent - $percentCompleted")
                task.progress = percentCompleted
            }

            if (totalBytesRead != contentLength) {
                println("Connection interrupted!")

                continue
            }

            break
        }

        println("VV $videoUrl")

        videoOutStream.close()

        String showName = obj.getString("show_name", null)
        String seasonName = Integer.toString(obj.getInt("season", 0))

        Series currentSeries = null;
        for (Series series : seriesDao.all) {
            if (StringUtils.equalsIgnoreCase(series.name, showName)) {
                currentSeries = series
                break
            }
        }
        if (currentSeries == null) {
            currentSeries = new Series(user: UID)
            currentSeries.name = showName
            seriesDao.save(currentSeries)
        }

        Season currentSeason = null
        for (Season season : seasonDao.getForSeries(currentSeries.id)) {
            if (StringUtils.equalsIgnoreCase(season.name, seasonName)) {
                currentSeason = season
                break
            }
        }
        if (currentSeason == null) {
            currentSeason = new Season(user: UID)
            currentSeason.name = seasonName
            currentSeason.series = currentSeries.id
            seasonDao.save(currentSeason)
        }

        Video video = new Video(user: UID)
        video.name = videoName
        video.season = currentSeason.id
        video.src = videoUnique + videoExt
        video.num = Integer.parseInt(videoNumber)

        videoDao.save(video)

        data.user.lastSeason = Integer.toString(currentSeason.id)
        data.user.lastSeries = Integer.toString(currentSeries.id)

        userDao.update(data.user)

        task.status = OroroTask.STATUS.ADDED

        return Result.getOk()
    }

    @RequestMapping(method = RequestMethod.GET, value = "create-cash")
    @ResponseBody
    Result createCash() {
        new Thread({
            //loadWebList("test@example.com", "password")
        }).start()

        return Result.getOk()
    }


}
