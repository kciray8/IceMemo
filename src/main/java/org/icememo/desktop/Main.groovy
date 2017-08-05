package org.icememo.desktop

import org.icememo.lang.LangDB
import org.icememo.lang.Language

import javax.swing.*
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class Main {
    Server server

    public static void main(String[] args) {
        SwingUtilities.invokeLater({
            new Main()
        });
    }

    final PopupMenu popup = new PopupMenu();
    final TrayIcon trayIcon =
            new TrayIcon(Res.logo16, "IceMemo");
    final SystemTray tray = SystemTray.getSystemTray();

    private void addMenuItem(String name, Runnable task) {
        MenuItem menuItem = new MenuItem(name);

        menuItem.addActionListener({ a ->
            task.run()
        })
        popup.add(menuItem)
    }

    ControlPanel controlPanel

    void openControlPanel() {
        Utils.show(controlPanel.frame)
    }

    public static Language lang;

    Main() {
        String langStr = OS.prefs.get("lang", "rus")
        if(langStr == "rus"){
            lang = LangDB.instance.rus
        }
        if(langStr == "eng"){
            lang = LangDB.instance.eng
        }

        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());

        String exeDir = System.getenv("exedir");
        server = new Server(exeDir)
        controlPanel = new ControlPanel(server)

        trayIcon.setPopupMenu(popup);
        //tray.add(trayIcon);//Tray is disable!

        /*addMenuItem("Open system", {
            controlPanel.openSystem()
        })*/
        addMenuItem("Open control panel", {
            openControlPanel();
        })
        popup.addSeparator()
        addMenuItem("Open console", {
            if (Console.instance == null) {
                Console console = new Console();
                console.frame.show()
                console.setText(server.logStr.toString())
            } else {
                Utils.show(Console.instance.frame)
            }
        })
        popup.addSeparator()
        addMenuItem("Exit", {
            if(server != null){
                server.stop()
            }
            System.exit(0)
        })

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getClickCount() == 1) && (e.button == 1)) {
                    openControlPanel();
                }
            }
        })

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("In shutdown hook");
                if (server.status != Server.Status.READY) {
                    //System.out.println("Stopping server...");
                    //server.stop()
                }
                System.out.println("Shutdown hook end");
            }
        }, "Shutdown-thread"));

        String lastMainFolder = OS.prefs.get(OS.MAIN_FOLDER, null)
        if (lastMainFolder == null) {
            //First launch
            trayIcon.displayMessage(lang.get("d_first_launch"), lang.get("d_first_launch_text"), TrayIcon.MessageType.INFO);
        }

        Utils.show(controlPanel.frame)
    }
}
