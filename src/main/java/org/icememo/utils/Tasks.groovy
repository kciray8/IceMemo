package org.icememo.utils

import org.icememo.api.BaseController
import org.icememo.entity.Video
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class Tasks {
    BaseController controller
    Tasks(BaseController controller) {
        this.controller = controller
    }

    String baseUrl = "http://home.versatel.nl/friendspic0102/html/season06.html";
    String baseBaseUrl = "http://home.versatel.nl/friendspic0102/html/";
    public String loadFriendsTranscripts(){
        Document doc = Jsoup.connect(baseUrl).get();
        Elements elements = doc.select("tr");

        List<Video> videos = controller.videoDao.getForSeason(5);

        File baseFolder = new File("C:\\Users\\kciray\\OneDrive\\IceSystem\\Work Place")

        for(Element element: elements){
            String name = element.getElementsByTag("td").getAt(3)?.text()
            if((name == null)||(!name.startsWith("The"))){
                continue
            }
            def url = element.getElementsByTag("td").getAt(1)?.getElementsByAttribute("href")?.attr("href");
            String textUrl = baseBaseUrl + url;

            Document doc2 = Jsoup.connect(textUrl).get();

            Video video = findVideo(videos, name);

            File trFile = new File(baseFolder, video.src.replaceAll(".webm","") + ".htm")
            if(trFile.exists()){
                trFile.delete();
            }

            FileUtils.write(trFile, JavaUtils.getText(textUrl))

            System.out.println("=Pure file=" + trFile)
            System.out.println(JavaUtils.getText(textUrl))
        }

        return "OK"
    }

    private Video findVideo(List<Video> videos, String text){

        if(text == "The One That Could Have Been, Part I & II"){
            text = "The One that Could Have Been, Part 1"
        }
        if(text == "The One With The Proposal, Part I & II"){
            text = "The One with the Proposal, Part 1"
        }

        if(text == "The One With The Thanksgiving Flashbacks"){
            text = "The One with All the Thanksgivings"
        }
        if(text == "The One With Rachel's Inadvertant Kiss"){
            text = "The One with Rachel's Inadvertent Kiss"
        }
        if(text == "The One With The Ride Along"){
            text = "The One with the Ride-Along"
        }
        if(text == "The One In Vegas, Part I The One In Vegas, Part II"){
            text = "The One in Vegas, Part One"
        }

        for(Video video: videos){
            if(StringUtils.containsIgnoreCase(video.name, text)){
                return video
            }
        }
        System.out.println("CAN'T FIND!! " + text)
    }
}
