package org.icememo

import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.HandlerCollection
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.RequestLogHandler
import org.eclipse.jetty.util.component.LifeCycle
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.eclipse.jetty.util.thread.ThreadPool
import org.eclipse.jetty.webapp.WebAppContext
import org.icememo.desktop.Main
import org.icememo.desktop.Utils
import org.icememo.lang.Language

public class WebServer {
    private static final String APP = "META-INF/webapp"
    //private static final String WEB_XML = "META-INF/webapp/WEB-INF/web.xml";

    private Server server;
    private int port;

    Runnable onDone
    Runnable onStop

    String programFolder;

    Language lang

    public WebServer(int aPort) {
        port = aPort;
        lang = Main.lang
    }

    public void start() throws Exception {
        server = new Server(createThreadPool());

        ServerConnector serverConnector = new ServerConnector(server)
        serverConnector.port = port

        server.addConnector(serverConnector);

        server.setHandler(createHandlers());
        server.setStopAtShutdown(true);

        server.addLifeCycleListener(new LifeCycle.Listener() {
            @Override
            void lifeCycleStarting(LifeCycle lifeCycle) {

            }

            @Override
            void lifeCycleStarted(LifeCycle lifeCycle) {
                onDone?.run()
            }

            @Override
            void lifeCycleFailure(LifeCycle lifeCycle, Throwable throwable) {
                onStop?.run()
            }

            @Override
            void lifeCycleStopping(LifeCycle lifeCycle) {

            }

            @Override
            void lifeCycleStopped(LifeCycle lifeCycle) {
                onStop?.run()
            }
        })

        try {
            server.start();
        } catch (BindException e) {
            Utils.error("${lang.get('d_port')} ${port} ${lang.get('d_already_in_use')}", lang.get('d_select_another_port'))
        } catch (SocketException e) {
            Utils.error(lang.get('d_error') , lang.get('d_select_another_port'))
        }
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

    private ThreadPool createThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(10);
        threadPool.setMaxThreads(100);
        return threadPool;
    }

    private HandlerCollection createHandlers() {
        WebAppContext appContext = new WebAppContext();
        appContext.setContextPath("/");
        appContext.setResourceBase(getResource(APP).toString());

        if(programFolder == null){
            File jarFile = new File(WebServer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            String appFile = jarFile.parentFile.parent
            programFolder = appFile
        }

        appContext.setInitParameter("ICE_MEMO_MAIN_FOLDER", mainFolderStr)
        appContext.setInitParameter("ICE_MEMO_APP_FOLDER", programFolder)

        HandlerList contexts = new HandlerList();
        Handler[] handlersArray = [appContext]
        contexts.setHandlers(handlersArray);

        RequestLogHandler log = new RequestLogHandler();
        log.setRequestLog(createRequestLog());

        HandlerCollection result = new HandlerCollection();
        Handler[] handlerArray = [contexts, log]
        result.setHandlers(handlerArray);

        return result;
    }

    String mainFolderStr = null

    private RequestLog createRequestLog() {
        NCSARequestLog log = new NCSARequestLog();

        if (mainFolderStr == null) {
            mainFolderStr = System.getenv("ICE_MEMO_MAIN_FOLDER")
        }

        File mainFolder = new File((String) mainFolderStr)
        File logHolder = new File(mainFolder, "log")
        String logPath = new File(logHolder, "file.log");

        File logFIle = new File(logPath);
        logFIle.getParentFile().mkdirs();

        log.setFilename(logFIle.getPath());
        log.setRetainDays(90);
        log.setExtended(false);
        log.setAppend(true);
        log.setLogTimeZone("GMT");
        log.setLogLatency(true);
        return log;
    }

    private URL getResource(String aResource) {
        return Thread.currentThread().getContextClassLoader().getResource(aResource);
    }

    //For debug
    public static void main(String... anArgs) throws Exception {
        WebServer webServer = new WebServer(80)
        webServer.start()
        webServer.join()
    }
}
