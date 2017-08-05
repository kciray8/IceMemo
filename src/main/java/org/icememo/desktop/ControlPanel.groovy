package org.icememo.desktop

import org.icememo.api.MainController
import org.icememo.lang.Language
import org.icememo.utils.JarUtils

import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.text.SimpleDateFormat

class ControlPanel {
    JFrame frame
    Server server
    JLabel mainPath;
    JLabel info;
    JPanel serverPanel
    JProgressBar startProgress
    JButton changeButton
    JButton stopButton
    JButton startButton
    JButton open
    JTextField portField

    void updateMainPath() {
        mainPath.setText("${lang.get('d_path')}: " + server.mainFolderGui)
    }

    String debugFolder;

    JLabel urlHint

    Language lang = Main.lang

    void updateUrlHint(){
        urlHint.setText("<html><font color='blue'>${lang.get('d_or_you_can_type')} <u>http://localhost:${server.port}</u> ${lang.get('d_in_browser')}</font></html>")
    }


    ControlPanel(Server server) {
        this.server = server

        frame = new JFrame()

        String title = "${lang.get('d_cp')} " + MainController.appVersion
        if(JarUtils.demo){
            title+= " (${lang.get('d_demo')})"
        }else{
            title+= " (${lang.get('d_full')})"
        }
        frame.setTitle(title)
        //frame.setSize(600, 400)
        frame.setIconImages(Res.logoImages)
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if(server.status == Server.Status.READY){
                    System.exit(0);
                }else{
                    Utils.ask("${lang.get('d_stop_server')}", "${lang.get('d_sure_stop_exit')}", {
                        server.stop()
                        System.exit(0);
                    })
                }
            }
        });

        mainPath = new JLabel();
        updateMainPath()

        changeButton = new JButton(lang.get('d_change'))
        changeButton.addActionListener({ a ->
            changePath();
        })

        JPanel pathPanel = Utils.createVPanel(mainPath, changeButton)
        Utils.setSpecialBorder(pathPanel, lang.get('d_main_folder'), true)

        info = new JLabel(lang.get('d_please_select_main_folder'))
        JPanel infoPanel = Utils.createVPanel(info)
        Utils.setSpecialBorder(infoPanel, lang.get('d_info'))

        startButton = new JButton(lang.get('d_start'));
        startButton.addActionListener { a ->
            start()
        }

        stopButton = new JButton(lang.get('d_stop'));
        stopButton.addActionListener { a ->
            server.stop()
        }

        startProgress = new JProgressBar(indeterminate: true)

        open = new JButton(lang.get('d_open'))
        open.addActionListener({a->
            openSystem()
        })

        JLabel portLabel = new JLabel(lang.get('d_port'))
        portField = new JTextField("8080")
        portField.setMaximumSize(new Dimension(50, 100))

        urlHint = new JLabel()
        updateUrlHint()

        serverPanel = Utils.createVPanel(Utils.createHPanelInner(portLabel, portField),
                Utils.createHPanelInner(startButton, stopButton, open), startProgress, urlHint)
        Utils.hide(startProgress)
        Utils.hide(stopButton)
        Utils.hide(open)
        Utils.hide(urlHint)
        Utils.setSpecialBorder(serverPanel, lang.get('d_server'))

        frame.setLayout(new GridBagLayout())
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        frame.add(pathPanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        frame.add(infoPanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0;
        frame.add(serverPanel, c);

        Utils.enable(serverPanel, false)

        String lastMainFolder = OS.prefs.get(OS.MAIN_FOLDER, null)

        debugFolder = System.getenv("debugMainFolder");
        if(debugFolder != null){
            lastMainFolder = debugFolder
        }

        if (lastMainFolder != null) {
            openFolder(new File(lastMainFolder))
        }

        frame.setMinimumSize(new Dimension(600, 0))
        frame.pack()
        Utils.toScreenCenter(frame)
    }

    public String openSystem() {
        OS.openURL("http://localhost:${server.port}")
    }

    private void start() {
        Utils.show(startProgress)
        Utils.enable(startButton, false)
        Utils.enable(changeButton, false)
        Utils.enable(portField, false)

        server.port = portField.getText()
        server.start({
            Utils.hide(startProgress)
            Utils.show(stopButton)
            Utils.show(open)

            updateUrlHint()
            Utils.show(urlHint)
        }, {
            Utils.hide(startProgress)
            Utils.hide(stopButton)
            Utils.hide(open)
            Utils.hide(urlHint)
            Utils.enable(startButton, true)
            Utils.enable(changeButton, true)
            Utils.enable(portField, true)
        })
    }

    long createDate = -1
    String name;

    void successInfo(String infoStr) {
        info.setText("<html><font color='green'>$infoStr</font></html>")
    }

    public static interface PROP {
        public static String CREATED = "Created"
        public static String NAME = "Name"
    }

    private void changePath() {
        JFileChooser chooser = new JFileChooser();
        File userHome = chooser.getFileSystemView().getDefaultDirectory();
        File defaultFile = new File(userHome, "IceMemo")
        //chooser.setCurrentDirectory(userHome);
        chooser.setDialogTitle(lang.get("d_choose_main_folder"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile()

            if ((selectedFile != null) && (selectedFile.list() != null)) {

                if(selectedFile.absolutePath == userHome.absolutePath){
                    selectedFile = defaultFile
                    selectedFile.mkdirs()
                }

                if (selectedFile.list().length > 0) {
                    openFolder(selectedFile)
                } else {
                    Utils.ask(lang.get("d_create_new_db"), lang.get("d_create_new_db_sure"), {
                        Utils.input(lang.get("d_input_name"), lang.get("d_name"), { name ->
                            File propFile = new File(selectedFile, "main.xml")
                            Properties prop = new Properties();
                            createDate = System.currentTimeMillis()
                            String msStr = Long.toString(createDate)
                            prop.setProperty(PROP.CREATED, msStr)
                            prop.setProperty(PROP.NAME, name)
                            this.name = name

                            def stream = new FileOutputStream(propFile)
                            prop.storeToXML(stream, "")
                            stream.close()

                            createEmptyDB(selectedFile)
                            selectMainFolder(selectedFile);
                            frame.pack()
                        }, "Main")
                    })
                }
            } else {
                Utils.error(lang.get("d_error"), lang.get("d_cannot_open"))
            }
        }
    }

    private createEmptyDB(File folder) {
        File videoFile = new File(folder, "video")
        videoFile.mkdirs()
        videoFile.createNewFile()
    }

    private void openFolder(File folder) {
        try {
            Properties prop = new Properties();
            File propFile = new File(folder, "main.xml")
            prop.loadFromXML(new FileInputStream(propFile))
            String createdStr = prop.getProperty(PROP.CREATED)
            createDate = new Long(createdStr)
            name = prop.getProperty(PROP.NAME)

            selectMainFolder(folder);
        } catch (e) {
            Utils.error(lang.get("d_error"), lang.get("d_error_select"))
        }
    }
    private selectMainFolder(File file) {
        def dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        String date = dateFormat.format(new Date(createDate))
        successInfo("${lang.get('d_db_selected')} <br> ${lang.get('d_name')} - <b>$name</b>  " +
                "<br> ${lang.get('d_creation_date')} - $date")

        server.mainFolder = file
        updateMainPath()
        Utils.enable(serverPanel, true)

        if(debugFolder == null){
            OS.prefs.put(OS.MAIN_FOLDER, file.absolutePath)
        }
    }

}
