package queries;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Displays menu for the creator of SQL scripts, especially queries
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_Menu extends JFrame {

    static final long serialVersionUID = 1L;

    ResourceBundle titles;
    String selRun, parApp, crtEdit, expScr, impScr, titMenu;

    ResourceBundle buttons;
    String run, param, edit, exp, imp;

    ResourceBundle locMessages;
    String curDir, wait;

    int windowWidth = 500;
    int windowHeight = 350;
    int windowHeightUser = 200;

    Container cont = getContentPane();
    GridBagLayout gridBagLayout = new GridBagLayout();
    BorderLayout borderLayout = new BorderLayout();

    // Object with limiting conditions
    GridBagConstraints gbc = new GridBagConstraints();

    JPanel titlePanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JPanel messagePanel = new JPanel();
    JPanel globalPanel = new JPanel();

    JMenuBar menuBar;
    JMenu helpMenu;
    JMenuItem helpMenuItemEN;
    JMenuItem helpMenuItemCZ;

    JLabel title;

    JButton selectQueryButton;
    JButton parametersButton;
    JButton editScriptButton;
    JButton toAS400Button;
    JButton fromAS400Button;

    JLabel selectQueryLbl;
    JLabel parametersLbl;
    JLabel editScriptLbl;
    JLabel toAS400Lbl;
    JLabel fromAS400Lbl;

    JLabel emptyLabel = new JLabel();
    
    // Messages are in a list
    JList messageList = new JList();

    // The messageList is in scroll pane
    JScrollPane scrollMessagePane = new JScrollPane(messageList);
    ArrayList<String> msgVector = new ArrayList<>();
    String row;
    MessageScrollPaneAdjustmentListenerMax messageScrollPaneAdjustmentListenerMax;

    Q_ExportAllToAS400 exportAll;
    Q_ImportAllFromAS400 importAll;

    Path inPath = Paths.get(System.getProperty("user.dir"), "paramfiles", "Q_Parameters.txt");
    Path errPath = Paths.get(System.getProperty("user.dir"), "logfiles", "err.txt");
    Path outPath = Paths.get(System.getProperty("user.dir"), "logfiles", "out.txt");
    OutputStream errStream;
    OutputStream outStream;

    final Color DIM_BLUE = new Color(50, 60, 160);
    final Color DIM_RED = new Color(190, 60, 50);
    final Color DIM_PINK = new Color(170, 58, 128);

    /**
     * Creates window with all functions of the application
     *
     * @param fullMenu
     */
    public void createQ_Menu(boolean fullMenu) {


        // Get or set application properties
        // ---------------------------------
        Properties sysProp = System.getProperties();

        // Menu bar in Mac operating system will be in the system menu bar
        if (sysProp.get("os.name").toString().toUpperCase().contains("MAC")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        try {
            // If "paramfiles" directory doesn't exist, create one
            Path paramfilesPath = Paths.get(System.getProperty("user.dir"), "paramfiles");
            if (!Files.exists(paramfilesPath)) {
                Files.createDirectory(paramfilesPath);
            }
            // If "logfiles" directory doesn't exist, create one
            Path logfilesPath = Paths.get(System.getProperty("user.dir"), "logfiles");
            if (!Files.exists(logfilesPath)) {
                Files.createDirectory(logfilesPath);
            }
            // If "scriptfiles" directory doesn't exist, create one
            Path scriptfilesPath = Paths.get(System.getProperty("user.dir"), "scriptfiles");
            if (!Files.exists(scriptfilesPath)) {
                Files.createDirectory(scriptfilesPath);
            }
            // If "printfiles" directory doesn't exist, create one
            Path printfilesPath = Paths.get(System.getProperty("user.dir"), "printfiles");
            if (!Files.exists(printfilesPath)) {
                Files.createDirectory(printfilesPath);
            }
            // If "workfiles" directory doesn't exist, create one
            Path workfilesPath = Paths.get(System.getProperty("user.dir"), "workfiles");
            if (!Files.exists(workfilesPath)) {
                Files.createDirectory(workfilesPath);
            }

            // Redirect System.err, System.out to log files err.txt, out.txt in directory "logfiles"
            errStream = Files.newOutputStream(errPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            outStream = Files.newOutputStream(outPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintStream errPrintStream = new PrintStream(errStream);
        PrintStream outPrintStream = new PrintStream(outStream);
        // PrintStream console = System.out;
        System.setErr(errPrintStream);
        System.setOut(outPrintStream);
        // System.setOut(console);

        // Check existence of application parameters.
        // In case Parameters.txt does not exist, create default one.
        if (!Files.exists(inPath)) {
            Q_ParametersCreate.main();
        }

        // Get necessary properties
        Q_Properties prop = new Q_Properties();
        String language = prop.getProperty("LANGUAGE");
        Locale currentLocale = Locale.forLanguageTag(language);

        titles = ResourceBundle.getBundle("locales.L_TitleLabelBundle", currentLocale);
        buttons = ResourceBundle.getBundle("locales.L_ButtonBundle", currentLocale);
        locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);

        // Localized titles and labels
        titMenu = titles.getString("TitMenu");
        selRun = titles.getString("SelRun");
        parApp = titles.getString("ParApp");
        crtEdit = titles.getString("CrtEdit");
        expScr = titles.getString("ExpScr");
        impScr = titles.getString("ImpScr");

        title = new JLabel(titMenu);

        selectQueryLbl = new JLabel(selRun);
        parametersLbl = new JLabel(parApp);
        editScriptLbl = new JLabel(crtEdit);
        toAS400Lbl = new JLabel(expScr);
        fromAS400Lbl = new JLabel(impScr);

        // Localized button labels
        run = buttons.getString("Run");
        param = buttons.getString("Param");
        edit = buttons.getString("Edit");
        exp = buttons.getString("Exp");
        imp = buttons.getString("Imp");

        // Create buttons
        selectQueryButton = new JButton(run);
        parametersButton = new JButton(param);
        editScriptButton = new JButton(edit);
        toAS400Button = new JButton(exp);
        fromAS400Button = new JButton(imp);

        // Title in the window - color the same as panel color
        title.setBackground(titlePanel.getBackground());

        menuBar = new JMenuBar();
        helpMenu = new JMenu("Help");
        helpMenuItemEN = new JMenuItem("Help English");
        helpMenuItemCZ = new JMenuItem("Nápověda česky");

        helpMenu.add(helpMenuItemEN);
        helpMenu.add(helpMenuItemCZ);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar); // In macOS on the main system menu bar above, in Windows on the window menu bar

        // Colors of labels at buttons same as panel color
        selectQueryLbl.setBackground(buttonPanel.getBackground());
        parametersLbl.setBackground(buttonPanel.getBackground());
        editScriptLbl.setBackground(buttonPanel.getBackground());
        toAS400Lbl.setBackground(buttonPanel.getBackground());
        fromAS400Lbl.setBackground(buttonPanel.getBackground());

        selectQueryLbl.setFont(selectQueryButton.getFont());
        parametersLbl.setFont(selectQueryButton.getFont());
        editScriptLbl.setFont(selectQueryButton.getFont());
        toAS400Lbl.setFont(selectQueryButton.getFont());
        fromAS400Lbl.setFont(selectQueryButton.getFont());

        // Localized messages
        curDir = locMessages.getString("CurDir");
        wait = locMessages.getString("Wait");

        // Place title in label panel
        titlePanel.add(title);
        title.setFont(new Font("Helvetica", Font.PLAIN, 20));

        // Build button panel
        buttonPanel.setLayout(gridBagLayout);
        // Insets around components (top, left, bottom, right)
        gbc.insets = new Insets(5, 5, 5, 5);

        // Padding between components
        gbc.ipadx = 0; // horizontal
        gbc.ipady = 5; // vertical

        // gridx = -1, gridy = -1 at the beginning
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Place buttons in window
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 1; // column 0, line 1
        if (!fullMenu) {
            buttonPanel.add(selectQueryButton, gbc);
        }
        if (fullMenu) {
            gbc.gridy++;
            buttonPanel.add(toAS400Button, gbc);
            gbc.gridy++;
            buttonPanel.add(fromAS400Button, gbc);
        }
        gbc.gridy++;
        buttonPanel.add(parametersButton, gbc);
        gbc.gridy++;
        buttonPanel.add(emptyLabel, gbc);
        if (fullMenu) {
            gbc.gridy++;
            buttonPanel.add(editScriptButton, gbc);
        }

        // Register HelpWindow menu item listener
        helpMenuItemEN.addActionListener(ae -> {
            String command = ae.getActionCommand();
            if (command.equals("Help English")) {
                if (Desktop.isDesktopSupported()) {
                    String uri = Paths
                            .get(System.getProperty("user.dir"), "helpfiles", "IBMiSqlScriptsUserDocEn.pdf").toString();
                    // Replace backslashes by forward slashes in Windows
                    uri = uri.replace('\\', '/');
                    uri = uri.replace(" ", "%20");
                    try {
                        // Invoke the standard browser in the operating system
                        Desktop.getDesktop().browse(new URI("file://" + uri));
                    } catch (IOException | URISyntaxException exc) {
                        exc.printStackTrace();
                    }
                }
            }
        });
        // Register HelpWindow menu item listener
        helpMenuItemCZ.addActionListener((ActionEvent ae) -> {
            String command = ae.getActionCommand();
            if (command.equals("Nápověda česky")) {
                if (Desktop.isDesktopSupported()) {
                    String uri = Paths
                            .get(System.getProperty("user.dir"), "helpfiles", "IBMiSqlScriptsUserDocCz.pdf").toString();
                    // Replace backslashes by forward slashes in Windows
                    uri = uri.replace('\\', '/');
                    uri = uri.replace(" ", "%20");
                    try {
                        // Invoke the standard browser in the operating system
                        Desktop.getDesktop().browse(new URI("file://" + uri));
                    } catch (IOException | URISyntaxException exc) {
                        exc.printStackTrace();
                    }
                }
            }
        });

        // Button actions
        // --------------
        if (!fullMenu) {  // for common users
            // Create list of queries from script files and process queries
            selectQueryButton.addActionListener(ctb -> {
                Q_ScriptRunCall scriptRunCall = new Q_ScriptRunCall();
                scriptRunCall.buildScriptList();
            });
        }
        // Export to AS/400
        toAS400Button.addActionListener(sb -> {
            row = wait + "\n";
            exportAll = new Q_ExportAllToAS400(this);
            // Invoke the background worker task
            exportAll.execute();
            repaint();
        });
        // Import from AS/400
        fromAS400Button.addActionListener(sb -> {
            row = wait + "\n";
            //msgTextArea.setText(msgText);
            importAll = new Q_ImportAllFromAS400(this);
            // Invoke the background worker task
            importAll.execute();
            repaint();
        });
        // Parameters for application
        parametersButton.addActionListener(mb -> {
            if (fullMenu) {
                // Parameters for the Administrator
                new Q_ParametersEdit(fullMenu);
            } else {
                // Parameters for the User
                new Q_ParametersEdit(false);
            }
        });
        // Edit, maintain and run SQL scripts
        editScriptButton.addActionListener(tb -> {  // for administrator
            Q_ScriptEditCall scriptEditCall = new Q_ScriptEditCall();
        });

        // Place labels in window
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 1;
        gbc.gridy = 1;
        if (!fullMenu) {
            buttonPanel.add(selectQueryLbl, gbc);
        }
        if (fullMenu) {
            gbc.gridy++;
            buttonPanel.add(toAS400Lbl, gbc);
            gbc.gridy++;
            buttonPanel.add(fromAS400Lbl, gbc);
        }
        gbc.gridy++;
        buttonPanel.add(parametersLbl, gbc);
        gbc.gridy++;
        buttonPanel.add(emptyLabel, gbc);
        if (fullMenu) {
            gbc.gridy++;
            buttonPanel.add(editScriptLbl, gbc);
        }

        messagePanel.setPreferredSize(new Dimension(windowWidth, 170));

        // Send initial message: Current directory ...
        row = "- "+curDir + System.getProperty("user.dir") ;
        msgVector.add(row);
        showMessages();

        // List of messages to place into the message scroll pane.
        // Decide what color the message will get.
        messageList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value.toString().startsWith("-")) {
                    this.setForeground(DIM_BLUE);
                } else if (value.toString().startsWith("!")) {
                    this.setForeground(DIM_RED);
                } else if (value.toString().startsWith("I")) {
                    this.setForeground(Color.GRAY);
                } else if (value.toString().startsWith("?")) {
                    this.setForeground(DIM_PINK);
                } else {
                    this.setForeground(Color.BLACK);
                }
                return component;
            }
        });

        // This adjustment listener shows the scroll pane at the FIRST MESSAGE.
        messageScrollPaneAdjustmentListenerMax = new MessageScrollPaneAdjustmentListenerMax();

        // Build messageList and put it to scrollMessagePane and panelMessages
        buildMessageList();

        // Scroll pane for message text area
        scrollMessagePane.setBorder(null);
        scrollMessagePane.setPreferredSize(new Dimension(windowWidth, 200));

        // Build global panel and container
        globalPanel.setLayout(borderLayout);
        globalPanel.add(titlePanel, BorderLayout.NORTH);
        globalPanel.add(buttonPanel, BorderLayout.CENTER);
        if (fullMenu) {
            globalPanel.add(scrollMessagePane, BorderLayout.SOUTH);
        }
        globalPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cont.add(globalPanel);

        if (fullMenu) {
            setSize(windowWidth, windowHeight);
        } else {
            setSize(windowWidth, windowHeightUser);
        }

        setLocation(40, 50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    } // createQ_Menu

    private static Q_Menu reference = null;

    /**
     * Obtains and returns single object of this class by calling private
     * constructor.
     *
     * @return reference to the object of this class
     */
    public static synchronized Q_Menu getQ_Menu() {
        if (reference == null) {
            reference = new Q_Menu();
        }
        return reference;
    }

    /**
     * Build message list.
     */
    protected void buildMessageList() {
        messageList.setSelectionBackground(Color.WHITE);
        ArrayList<String> newMsgVector = new ArrayList<>();
        for (String message : msgVector) {
            newMsgVector.add(message);
            msgVector = newMsgVector;
        }
        // Fill message list with elements of the array list
        messageList.setListData(msgVector.toArray(new String[msgVector.size()]));
        // Make the message table visible in the message scroll pane
        scrollMessagePane.setViewportView(messageList);
    }

    /**
     * Show messages
     */
    protected void showMessages() {
        scrollMessagePane.getVerticalScrollBar()
                .addAdjustmentListener(messageScrollPaneAdjustmentListenerMax);
        buildMessageList();
        scrollMessagePane.getVerticalScrollBar()
                .removeAdjustmentListener(messageScrollPaneAdjustmentListenerMax);
        // Make the message table visible in the message scroll pane
        scrollMessagePane.setViewportView(messageList);
    }

    /**
     * Adjustment listener for MESSAGE scroll pane.
     */
    class MessageScrollPaneAdjustmentListenerMax implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent ae) {
            // Set scroll pane to the bottom - the last element
            ae.getAdjustable().setValue(ae.getAdjustable().getMaximum());
        }
    }

    /**
     * Main class creates the object of this class and calls method to create the
     * window.
     *
     * @param strings
     * not used
     */
    public static void main(String... strings) {
        Q_Menu cmn = new Q_Menu();
        try {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(Q_Menu.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException  exc) {
            exc.printStackTrace();
        }
        cmn.createQ_Menu(true);
    }
}
