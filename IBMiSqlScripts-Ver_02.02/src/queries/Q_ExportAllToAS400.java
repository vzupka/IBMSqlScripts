package queries;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400FTP;
import com.ibm.as400.access.AS400JPing;
import com.ibm.as400.access.FTP;
import com.ibm.as400.access.IFSFile;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * Transfers ALL files from the directory "scriptfiles" to IBM i.
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_ExportAllToAS400 extends SwingWorker<String, String> {

    static Q_Menu menu;
    // Path to result filter files (coming from GUI programs)
    static Path inPath = Paths.get(System.getProperty("user.dir"), "scriptfiles");
    static ResourceBundle locMessages;
    static String language;
    static String host;
    static String userName;
    static String ifsDirectory;
    static AS400 as400Host;
    static AS400FTP client;
    //static ArrayList<String> messages = new ArrayList<>();
    static String ioError, file, wasExported, transferEnd, noFiles;
    static String row;

    /**
     * Constructor
     *
     * @param menu
     */
    Q_ExportAllToAS400(Q_Menu menu) {
        Q_ExportAllToAS400.menu = menu;
    }

    /**
     * Perform method transferAllToAS400() and return a message text.
     *
     * @return
     */
    @Override
    public String doInBackground() {
        transferAllToAS400(menu);
        return row;
    }

    /**
     * Concludes the SwingWorker task getting the message text (task's result).
     */
    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        try {
            row = get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assembles published intermediate messages
     *
     * @param msgValues
     */
    @Override
    protected void process(List<String> msgValues) {
        // Does nothing
    }

    /**
     * Obtains connection to IBM i and creates an FTP client Program reads files from directory
     * "scriptfiles" one after another and puts them to the IFS directory given in application
     * parameters.
     *
     * @param menu
     * @return
     */
    @SuppressWarnings("UseSpecificCatch")
    public String transferAllToAS400(Q_Menu menu) {
        Q_Properties prop = new Q_Properties();
        language = prop.getProperty("LANGUAGE");
        host = prop.getProperty("HOST");
        userName = prop.getProperty("USER_NAME");
        ifsDirectory = prop.getProperty("IFS_DIRECTORY");
        // Append forward slash if not not present at the end of the path
        int len = ifsDirectory.length();
        if (len == 0) {
            ifsDirectory = "/";
            len = 1;
        }
        if (!ifsDirectory.substring(len - 1, len).equals("/")) {
            ifsDirectory += "/";
        }

        Locale currentLocale = Locale.forLanguageTag(language);
        locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);
        // Localized messages
        ioError = locMessages.getString("IOError");
        file = locMessages.getString("File");
        wasExported = locMessages.getString("WasExported");
        transferEnd = locMessages.getString("TransferEnd");
        noFiles = locMessages.getString("NoFiles");

        // Try ping on the server if connection is possible. If not, return message.
        AS400JPing pingObj = new AS400JPing(host);
        long timeoutMilliscconds = 8000;
        pingObj.setTimeout(timeoutMilliscconds);
        if (!pingObj.ping()) {
            row = "! Server " + host + " timed out "
                    + timeoutMilliscconds + " milliseconds.";
            menu.msgVector.add(row);
            menu.showMessages();
            return row;
        }

        // Get access to AS400
        as400Host = new AS400(host, userName);

        // Create an FTP client
        client = new AS400FTP(as400Host);

        // Read files from the directory "scriptfiles" and put them
        // to an AS400 directory using FTP
        if (Files.isDirectory(inPath)) {
            String[] fileNames = inPath.toFile().list();
            int nbrFiles = 0;
            try {
                client.connect();
                for (String fileName : fileNames) {
                    // Files beginning with . in its name are ignored
                    if (!fileName.substring(0, 1).equals(".")) {
                        // Transfer all files from the local directory to IFS directory
                            // Path to input script file
                            Path filePath = Paths.get(System.getProperty("user.dir"), "scriptfiles", fileName);
                            // Path to output IFS script file
                            IFSFile ifsFile = new IFSFile(as400Host, ifsDirectory + fileName);
                            // Create new empty file if it doesn't exist
                            if (!ifsFile.exists()) {
                                ifsFile.createNewFile();
                            }
                            // Set CCSID 1208 (UTF-8)
                            ifsFile.setCCSID(1208);

                            client.setDataTransferType(FTP.BINARY);
                            // FTP put
                            client.put(filePath.toString(), ifsDirectory + fileName);
                            row = "I "+file + fileName + wasExported + ifsDirectory;
                            menu.msgVector.add(row);
                            menu.showMessages();

                            nbrFiles++;
                    }
                }
                client.disconnect();
            } catch (IOException ioe) {
                System.out.println(ioError + ioe.toString());
                row = "! "+ioError + ioe.toString();
                menu.showMessages();
                menu.msgVector.add(row);
                return row;
            }
            if (nbrFiles == 0) {
                row = "? "+noFiles + inPath;
                menu.msgVector.add(row);
                menu.showMessages();
            }
            row = "- "+transferEnd;
            menu.msgVector.add(row);
            menu.showMessages();
        }
        return row;
    }
}
