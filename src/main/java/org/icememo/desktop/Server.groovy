package org.icememo.desktop

import org.icememo.WebServer

class Server {
    enum Status {
        READY, STARTING, STARTED
    }

    Status status = Status.READY

    File mainFolder

    String programFolder;

    public String getMainFolderGui() {
        if (mainFolder != null) {
            return mainFolder.absolutePath
        } else {
            return ""
        }
    }

    String port = "80"

    Server server = this

    boolean exitAfterStop = false

    Server(String programFolder) {
        this.programFolder = programFolder
    }

    StringBuilder logStr = new StringBuilder()

    WebServer webServer;
    public void start(Runnable onDone, Runnable onStop) {
        status = Status.STARTING
        new Thread({
            webServer = new WebServer(new Integer(port))
            webServer.mainFolderStr = mainFolder.absolutePath
            webServer.onDone = {
                onDone.run()
                status = Status.STARTED
            }
            webServer.onStop = {
                onStop.run()
                status = Status.READY
            }
            webServer.programFolder = programFolder
            webServer.start()
        }).start()
    }

    public void log(String line) {
        logStr.append(line + System.lineSeparator())
        if (Console.instance != null) {
            Console.instance.append(line)
        }
    }

    public void stop() {
        if(webServer != null) {
            webServer.stop()
            status = Status.READY
        }
    }
}
