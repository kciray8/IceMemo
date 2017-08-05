package org.icememo.convert

import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.icememo.utils.RegexpUtils

class Converter {
    Thread thread
    String src
    String subtitleLang = "eng"

    String ffmpegPath

    Converter(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath
    }

    private String getSubtitleId() {
        ProcessBuilder pb =
                new ProcessBuilder(ffmpegPath, "-i", src);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if ((line.contains("Stream")) && (line.contains("Subtitle")) && (StringUtils.containsIgnoreCase(line, subtitleLang))) {
                String subtitleId = RegexpUtils.findOne("(?<=#)\\d:\\d", line)
                return subtitleId
            }
        }
        return "0:3"
    }

    private void extractSubtitles(String subId) {
        subtitleFile = new File(FilenameUtils.removeExtension(src) + ".srt")
        if (subtitleFile.exists()) {
            subtitleFile.delete()
        }

        ProcessBuilder pb =
                new ProcessBuilder(ffmpegPath, "-i", src, "-an", "-vn", "-map", subId, "-c:s:0", "srt", subtitleFile.absolutePath);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            //println(line)
        }
    }
    File webmFile
    File subtitleFile
    File srcFile
    Process process

    private void createWebm() {
        webmFile = new File(FilenameUtils.removeExtension(src) + ".webm")
        if (webmFile.exists()) {
            webmFile.delete()
        }

        ProcessBuilder pb =
                new ProcessBuilder(ffmpegPath, "-i", src, "-c:v", "libvpx", "-crf", "4", "-b:v", "1M", "-c:a", "libvorbis", webmFile.absolutePath);
        pb.redirectErrorStream(true);
        process = pb.start();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            println(line)
        }
    }

    public void convert(Runnable onFinish = null) {
        srcFile = new File(src)
        println("Converting... $src")
        String subId = getSubtitleId()

        extractSubtitles(subId)
        createWebm()

        if (onFinish != null) {
            onFinish.run()
        }
    }

    public void delete() {
        if(process != null){
            process.destroy()
        }
        srcFile.delete()
        deleteTarget();
    }

    public void deleteTarget(){
        webmFile.delete()
        if (subtitleFile.exists()) {
            subtitleFile.delete()
        }
    }
}
