package queries;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 * Enables editing of application parameters.
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_ParametersEdit extends JDialog {

    static final long serialVersionUID = 1L;

    ResourceBundle buttons;
    ResourceBundle titles;
    ResourceBundle locMessages;

    // Name of the localized button
    String sav, ret;
    // Names of localized labels
    String defParApp, adrSvr, usrName, libList, ifsDir, autWin, winWidth, winHeight;
    String nullMark, colSpaces, fontSize, decPattern, orEnter;
    // Localized messages
    String curDir, parSaved;

    // Constants for properties
    final String LANGUAGE = "LANGUAGE";
    final String HOST = "HOST";
    final String USER_NAME = "USER_NAME";
    final String LIBRARY_LIST = "LIBRARY_LIST";
    final String IFS_DIRECTORY = "IFS_DIRECTORY";
    final String AUTO_WINDOW_SIZE = "AUTO_WINDOW_SIZE";
    final String RESULT_WINDOW_WIDTH = "RESULT_WINDOW_WIDTH";
    final String RESULT_WINDOW_HEIGHT = "RESULT_WINDOW_HEIGHT";
    final String NULL_PRINT_MARK = "NULL_PRINT_MARK";
    final String COLUMN_SEPARATING_SPACES = "COLUMN_SEPARATING_SPACES";
    final String FONT_SIZE = "FONT_SIZE";
    final String EDIT_FONT_SIZE = "EDIT_FONT_SIZE";
    final String DECIMAL_PATTERN = "DECIMAL_PATTERN";

    Path parPath = Paths.get(System.getProperty("user.dir"), "paramfiles", "Q_Parameters.txt");
    Q_Properties prop;

    Container cont = getContentPane();
    GridBagLayout gridBagLayout = new GridBagLayout();

    GridBagConstraints gbc = new GridBagConstraints();

    JPanel titlePanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JPanel dataPanel = new JPanel();
    JPanel messagePanel = new JPanel();
    JPanel globalPanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(globalPanel, BoxLayout.Y_AXIS);

    JLabel title;

    JRadioButton englishButton = new JRadioButton("English");
    JRadioButton czechButton = new JRadioButton("Česky");
    JCheckBox autoSizeButton = new JCheckBox("");

    // Initial parameter values - not to be empty when the application is
    // installed
    String language;
    JTextField hostTf = new JTextField();
    JTextField userNameTf = new JTextField();
    JTextField librariesTf = new JTextField();
    JTextField ifsDirectoryTf = new JTextField();
    String autoWindowSize = new String();
    JTextField windowWidthTf = new JTextField();
    JTextField windowHeightTf = new JTextField();
    JTextField nullPrintMarkTf = new JTextField();
    JTextField colSpacesTf = new JTextField();
    JTextField fontSizeTf = new JTextField();
    JTextField editFontSizeTf = new JTextField();
    JTextField decPatternTf = new JTextField();

    // These labels are NOT localized
    JLabel englishLbl = new JLabel(
            "Application language. Restart the application after change.");
    JLabel czechLbl = new JLabel("Jazyk aplikace. Po změně spusťte aplikaci znovu.");

    // Labels for text fields to localize
    JLabel hostLbl;
    JLabel userNameLbl;
    JLabel librariesLbl;
    JLabel ifsDirectoryLbl;
    JLabel autoSizeLbl;
    JLabel windowWidthLbl;
    JLabel windowHeightLbl;
    JLabel nullPrintMarkLbl;
    JLabel colSpacesLbl;
    JLabel fontSizeLbl;
    JLabel decPatternLbl;

    // Button for saving data to parameter properties
    JButton saveButton;
    // Button for returning to previous window
    JButton returnButton;

    // Messages are in a list
    JList messageList = new JList();

    // The messageList is in scroll pane
    JScrollPane scrollMessagePane = new JScrollPane(messageList);
    ArrayList<String> msgVector = new ArrayList<>();
    String row;
    MessageScrollPaneAdjustmentListenerMax messageScrollPaneAdjustmentListenerMax;

    final Color DIM_BLUE = new Color(50, 60, 160);
    final Color DIM_RED = new Color(190, 60, 50);
    final Color DIM_PINK = new Color(170, 58, 128);

    boolean fullMenu;
    
    int windowWidth = 640;
    int windowHeight = 600;

    /**
     * Constructor creates the window with application parameters
     */
    Q_ParametersEdit(boolean fullMenu) {
        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        this.fullMenu = fullMenu;

        if (!Files.exists(parPath)) {
            Q_ParametersCreate.main();
        }
        // Get necessary properties
        prop = new Q_Properties();

        language = prop.getProperty("LANGUAGE");
        Locale currentLocale = Locale.forLanguageTag(language);

        // Get resource bundle classes
        titles = ResourceBundle.getBundle("locales.L_TitleLabelBundle", currentLocale);
        buttons = ResourceBundle.getBundle("locales.L_ButtonBundle", currentLocale);
        locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);

        defParApp = titles.getString("DefParApp");
        adrSvr = titles.getString("AdrSvr");
        usrName = titles.getString("UsrName");
        libList = titles.getString("LibList");
        ifsDir = titles.getString("IfsDir");
        autWin = titles.getString("AutWin");
        winWidth = titles.getString("WinWidth");
        winHeight = titles.getString("WinHeight");
        nullMark = titles.getString("NullMark");
        colSpaces = titles.getString("ColSpaces");
        fontSize = titles.getString("FontSize");
        decPattern = titles.getString("DecPattern");

        // Title of the window
        title = new JLabel(defParApp);

        // Labels for text fields
        hostLbl = new JLabel(adrSvr);
        userNameLbl = new JLabel(usrName);
        librariesLbl = new JLabel(libList);
        ifsDirectoryLbl = new JLabel(ifsDir);
        autoSizeLbl = new JLabel(autWin);
        windowWidthLbl = new JLabel(winWidth);
        windowHeightLbl = new JLabel(winHeight);
        colSpacesLbl = new JLabel(colSpaces);
        nullPrintMarkLbl = new JLabel(nullMark);
        fontSizeLbl = new JLabel(fontSize);
        decPatternLbl = new JLabel(decPattern);

        // Labels will have the same colors as background
        englishLbl.setBackground(titlePanel.getBackground());
        czechLbl.setBackground(titlePanel.getBackground());
        hostLbl.setBackground(titlePanel.getBackground());
        userNameLbl.setBackground(titlePanel.getBackground());
        librariesLbl.setBackground(titlePanel.getBackground());
        ifsDirectoryLbl.setBackground(titlePanel.getBackground());
        autoSizeLbl.setBackground(titlePanel.getBackground());
        windowWidthLbl.setBackground(titlePanel.getBackground());
        windowHeightLbl.setBackground(titlePanel.getBackground());
        nullPrintMarkLbl.setBackground(titlePanel.getBackground());
        colSpacesLbl.setBackground(titlePanel.getBackground());
        fontSizeLbl.setBackground(titlePanel.getBackground());
        decPatternLbl.setBackground(titlePanel.getBackground());

        // Localized button labels
        sav = buttons.getString("Sav");
        ret = buttons.getString("Ret");

        // Button for saving data to parameter properties
        saveButton = new JButton(sav);

        // Button for saving data to parameter properties
        returnButton = new JButton(ret);

        // Labels will have the same font as the first button
        englishLbl.setFont(saveButton.getFont());
        czechLbl.setFont(saveButton.getFont());
        hostLbl.setFont(saveButton.getFont());
        userNameLbl.setFont(saveButton.getFont());
        librariesLbl.setFont(saveButton.getFont());
        ifsDirectoryLbl.setFont(saveButton.getFont());
        autoSizeLbl.setFont(saveButton.getFont());
        windowWidthLbl.setFont(saveButton.getFont());
        windowHeightLbl.setFont(saveButton.getFont());
        nullPrintMarkLbl.setFont(saveButton.getFont());
        colSpacesLbl.setFont(saveButton.getFont());
        fontSizeLbl.setFont(saveButton.getFont());
        decPatternLbl.setFont(saveButton.getFont());

        title.setBackground(titlePanel.getBackground());

        // Localized messages
        curDir = locMessages.getString("CurDir");
        parSaved = locMessages.getString("ParSaved");

        // Language radio buttons
        englishButton.setMnemonic(KeyEvent.VK_E);
        englishButton.setActionCommand("English");
        englishButton.setSelected(true);
        englishButton.setHorizontalTextPosition(SwingConstants.LEFT);

        czechButton.setMnemonic(KeyEvent.VK_C);
        czechButton.setActionCommand("Česky");
        czechButton.setSelected(false);
        czechButton.setHorizontalTextPosition(SwingConstants.LEFT);

        // Radio and check buttons listeners
        // ---------------------------------
        // Set on English, set off Czech
        englishButton.addActionListener(ae -> {
            englishButton.setSelected(true);
            czechButton.setSelected(false);
            language = "en-US";
            System.out.println(ae.getActionCommand());
            //System.out.println(language);
        });

        // Set on Czech, set off English
        czechButton.addActionListener(ae -> {
            czechButton.setSelected(true);
            englishButton.setSelected(false);
            language = "cs-CZ";
            System.out.println(ae.getActionCommand());
            //System.out.println(language);
        });

        // Select or deselect automatic window size
        autoSizeButton.addItemListener(il -> {
            Object source = il.getSource();
            if (source == autoSizeButton) {
                if (autoSizeButton.isSelected()) {
                    autoWindowSize = "Y";
                } else {
                    autoWindowSize = "N";
                }
            }
        });

        // Get parameter properties
        // ------------------------
        // This parameter comes from radio buttons
        language = prop.getProperty(LANGUAGE);
        if (language.equals("en-US")) {
            englishButton.setSelected(true);
            czechButton.setSelected(false);
        } else if (language.equals("cs-CZ")) {
            czechButton.setSelected(true);
            englishButton.setSelected(false);
        }

        // The following parameters are editable
        hostTf.setText(prop.getProperty(HOST));
        userNameTf.setText(prop.getProperty(USER_NAME));
        librariesTf.setText(prop.getProperty(LIBRARY_LIST));
        ifsDirectoryTf.setText(prop.getProperty(IFS_DIRECTORY));
        // String "Y" or "N"
        autoWindowSize = prop.getProperty(AUTO_WINDOW_SIZE);
        windowWidthTf.setText(prop.getProperty(RESULT_WINDOW_WIDTH));
        windowHeightTf.setText(prop.getProperty(RESULT_WINDOW_HEIGHT));
        nullPrintMarkTf.setText(prop.getProperty(NULL_PRINT_MARK));
        colSpacesTf.setText(prop.getProperty(COLUMN_SEPARATING_SPACES));
        fontSizeTf.setText(prop.getProperty(FONT_SIZE));
        editFontSizeTf.setText(prop.getProperty(EDIT_FONT_SIZE));
        decPatternTf.setText(prop.getProperty(DECIMAL_PATTERN));

        // Automatic size of the window with script results
        if (autoWindowSize.equals("Y")) {
            autoSizeButton.setSelected(true);
        } else {
            autoSizeButton.setSelected(false);
        }
        autoSizeButton.setHorizontalTextPosition(SwingConstants.LEFT);

        // Build the window
        // ----------------
        // Place title in label panel
        titlePanel.add(title);
        title.setFont(new Font("Helvetica", Font.PLAIN, 20));

        buttonPanel.add(saveButton);
        buttonPanel.add(returnButton);

        dataPanel.setLayout(gridBagLayout);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        // internal padding of components
        gbc.ipadx = 0; // vodorovně
        gbc.ipady = 0; // svisle

        // Place text fields in column 0
        gbc.anchor = GridBagConstraints.EAST;

        gbc.gridx = 0;
        gbc.gridy = 0;

        dataPanel.add(englishButton, gbc);
        gbc.gridy++;
        dataPanel.add(czechButton, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(hostTf, gbc);
            gbc.gridy++;
        }
        dataPanel.add(userNameTf, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(librariesTf, gbc);
            gbc.gridy++;
            dataPanel.add(ifsDirectoryTf, gbc);
            gbc.gridy++;
        }
        dataPanel.add(autoSizeButton, gbc);
        gbc.gridy++;
        dataPanel.add(windowWidthTf, gbc);
        gbc.gridy++;
        dataPanel.add(windowHeightTf, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(nullPrintMarkTf, gbc);
            gbc.gridy++;
            dataPanel.add(colSpacesTf, gbc);
            gbc.gridy++;
            dataPanel.add(fontSizeTf, gbc);
            gbc.gridy++;
            dataPanel.add(decPatternTf, gbc);
            gbc.gridy++;
        }

        // Place labels in column 1
        gbc.anchor = GridBagConstraints.WEST;
        // gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 1;
        gbc.gridy = 0;

        dataPanel.add(englishLbl, gbc);
        gbc.gridy++;
        dataPanel.add(czechLbl, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(hostLbl, gbc);
            gbc.gridy++;
        }
        dataPanel.add(userNameLbl, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(librariesLbl, gbc);
            gbc.gridy++;
            dataPanel.add(ifsDirectoryLbl, gbc);
            gbc.gridy++;
        }
        dataPanel.add(autoSizeLbl, gbc);
        gbc.gridy++;
        dataPanel.add(windowWidthLbl, gbc);
        gbc.gridy++;
        dataPanel.add(windowHeightLbl, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(nullPrintMarkLbl, gbc);
            gbc.gridy++;
            dataPanel.add(colSpacesLbl, gbc);
            gbc.gridy++;
            dataPanel.add(fontSizeLbl, gbc);
            gbc.gridy++;
            dataPanel.add(decPatternLbl, gbc);
            gbc.gridy++;
        }

        hostTf.addActionListener(al -> {
        });
        userNameTf.addActionListener(al -> {
        });
        librariesTf.addActionListener(al -> {
        });
        ifsDirectoryTf.addActionListener(al -> {
        });
        windowWidthTf.addActionListener(al -> {
        });
        windowHeightTf.addActionListener(al -> {
        });
        nullPrintMarkTf.addActionListener(al -> {
        });
        colSpacesTf.addActionListener(al -> {
        });
        fontSizeTf.addActionListener(al -> {
        });
        editFontSizeTf.addActionListener(al -> {
        });
        decPatternTf.addActionListener(al -> {
        });

        // On click the button Save data
        saveButton.addActionListener(al -> {
            saveData();
            setVisible(true);
        });

        // On click the button Return
        returnButton.addActionListener(al -> {
            dispose();
        });

        // Place message area in message panel
        messagePanel.setPreferredSize(new Dimension(windowWidth, 100));

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

        // Build messageList and put it to scrollMessagePane and panelMessages
        buildMessageList();

        // Scroll pane for message text area
        scrollMessagePane.setBorder(null);
        scrollMessagePane.setPreferredSize(new Dimension(windowWidth, 200));

        // Build content pane
        globalPanel.setLayout(boxLayout);
        globalPanel.add(titlePanel);
        globalPanel.add(dataPanel);
        globalPanel.add(buttonPanel);
        globalPanel.add(scrollMessagePane);
        globalPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        cont.add(globalPanel);
        
        // Enable ENTER key to save action
        globalPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ENTER"), "save");
        globalPanel.getActionMap().put("save", new SaveAction());

        setSize(windowWidth, windowHeight);
        setLocation(200, 50);
        pack();
        setVisible(true);
    }
    
    /**
     * Build message list.
     */
    final protected void buildMessageList() {
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
    final protected void showMessages() {
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
     * Class for saving data
     */
    class SaveAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            saveData();
            setVisible(true);
        }
    }

    /**
     * Saves input data to the parameters file
     */
    private void saveData() {
        // Check numeric values of some parameters
        String newWindowWidth = checkNumber(windowWidthTf.getText());
        String newWindowHeight = checkNumber(windowHeightTf.getText());
        String colSpace = checkNumber(colSpacesTf.getText());
        String fontSiz = checkNumber(fontSizeTf.getText());

        // Set properties with input values
        prop.setProperty(LANGUAGE, language);
        prop.setProperty(HOST, hostTf.getText());
        prop.setProperty(USER_NAME, userNameTf.getText());
        prop.setProperty(LIBRARY_LIST, librariesTf.getText());
        prop.setProperty(IFS_DIRECTORY, ifsDirectoryTf.getText());
        prop.setProperty(AUTO_WINDOW_SIZE, autoWindowSize);
        prop.setProperty(RESULT_WINDOW_WIDTH, newWindowWidth);
        prop.setProperty(RESULT_WINDOW_HEIGHT, newWindowHeight);
        prop.setProperty(NULL_PRINT_MARK, nullPrintMarkTf.getText());
        prop.setProperty(COLUMN_SEPARATING_SPACES, colSpace);
        prop.setProperty(FONT_SIZE, fontSiz);
        prop.setProperty(DECIMAL_PATTERN, decPatternTf.getText());

        // Put corrected parameters back to input fields for display
        windowWidthTf.setText(newWindowWidth);
        windowHeightTf.setText(newWindowHeight);
        colSpacesTf.setText(colSpace);
        fontSizeTf.setText(fontSiz);
        row = "- " + parSaved;
        msgVector.add(row);
        showMessages();
    }

    /**
     * Check input of a text field if it is numeric
     *
     * @param charNumber
     * @return String - echo if correct, "0" if not integer
     */
    protected String checkNumber(String charNumber) {
        try {
            Integer.valueOf(charNumber);
        } catch (NumberFormatException nfe) {
            charNumber = "0";
        }
        return charNumber;
    }
}
