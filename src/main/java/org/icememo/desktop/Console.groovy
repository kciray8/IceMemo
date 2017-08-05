package org.icememo.desktop

import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class Console {
    JFrame frame
    JTextArea textArea
    static Console instance

    Console() {
        instance = this
        frame = new JFrame()
        frame.setTitle("Console")

        textArea = new JTextArea(editable: false)
        textArea.setFont(new Font("Courier new", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        frame.add(scroll)
        frame.setIconImages(Res.logoImages)
        frame.setSize(1000, 800)
        Utils.toScreenCenter(frame)

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                instance = null
            }
        });
    }

    public void setText(String text) {
        textArea.setText(text)
    }

    public void append(String string){
        textArea.append(string + System.lineSeparator())
    }
}
