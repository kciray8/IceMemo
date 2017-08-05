package org.icememo.desktop

import javax.swing.*
import javax.swing.border.EmptyBorder
import java.awt.*
import java.util.function.Consumer

class Utils {
    private static int margin = 5;

    public static void toScreenCenter(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int) (dim.width / 2 - frame.getSize().width / 2), (int) (dim.height / 2 - frame.getSize().height / 2));
    }

    public static void setNimbusLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//new look & feel - from Java SE 6 Update 10 Beta
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static Image createImage(String path, String description) {
        URL imageURL = Main.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    public static JPanel createHPanelInner(Component... components) {
        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT)

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        for (Component component : components) {
            panel.add(component);
            panel.add(Box.createRigidArea(new Dimension(margin, 0)));
        }

        return panel;
    }

    public static JPanel createHPanel(Component... components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createRigidArea(new Dimension(margin, margin)));

        for (Component component : components) {
            panel.add(Box.createRigidArea(new Dimension(margin, 0)));
            panel.add(component);
        }
        // panel.add(Box.createRigidArea(new Dimension(margin, 0)));

        return panel;
    }

    public static JPanel createVPanel(JComponent... components) {
        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //panel.add(Box.createRigidArea(new Dimension(margin, margin)));
        for (JComponent component : components) {
            component.setAlignmentX(Component.LEFT_ALIGNMENT)
            panel.add(component);

            JComponent area = (JComponent) Box.createRigidArea(new Dimension(margin, margin))
            area.setAlignmentX(Component.LEFT_ALIGNMENT)
            panel.add(area);
        }

        return panel;
    }

    public static void enable(Container container, boolean value) {
        container.setEnabled(value)

        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(value);
            if (component instanceof Container) {
                enable((Container) component, value);
            }
        }
    }

    public static setSpecialBorder(JComponent component, String name, boolean topMargin = false) {
        int topMarginValue = 0;
        if (topMargin) {
            topMarginValue = 5
        }

        component.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(topMarginValue, 5, 5, 5),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(name), new EmptyBorder(5, 5, 5, 5))))
    }

    public static ask(String title, String question, Runnable onYes, Runnable onNo = null) {
        int dialogResult = JOptionPane.showConfirmDialog(null, question,
                title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (dialogResult == JOptionPane.YES_OPTION) {
            onYes.run()
        }
        if(onNo != null){
            if (dialogResult == JOptionPane.NO_OPTION) {
                onNo.run()
            }
        }
    }

    public static msg(String title, String question) {
        JOptionPane.showMessageDialog(null, question,
                title, JOptionPane.INFORMATION_MESSAGE);

    }

    public static error(String title, String question) {
        JOptionPane.showMessageDialog(null, question,
                title, JOptionPane.ERROR_MESSAGE);

    }

    public static input(String title, String message, Consumer<String> consumer, String defValue = "") {
        String res = JOptionPane.showInputDialog(null, message, title, JOptionPane.PLAIN_MESSAGE, null,
                null, defValue)
        if (res != null) {
            consumer.accept(res)
        }

    }

    public static hide(JComponent component) {
        changeVisible(component, false)
    }

    public static show(JComponent component) {
        changeVisible(component, true)
    }

    private static changeVisible(JComponent component, visible) {
        component.setVisible(visible)

        int areaIndex = getIndex(component) + 1
        JComponent area = (JComponent) component.getParent().getComponent(areaIndex)
        area.setVisible(visible)

        JFrame frame = (JFrame) SwingUtilities.getRoot(component);
        if (frame != null) {
            frame.pack()
        }
    }

    public static final int getIndex(Component component) {
        if (component != null && component.getParent() != null) {
            Container c = component.getParent();
            for (int i = 0; i < c.getComponentCount(); i++) {
                if (c.getComponent(i) == component)
                    return i;
            }
        }

        return -1;
    }

    public static void show(JFrame frame) {
        frame.show()
        if (frame.state == Frame.ICONIFIED) {
            frame.setState(Frame.NORMAL);
        }
    }
}
