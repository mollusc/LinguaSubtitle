package mollusc.linguasubtitle.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mollusc <MolluscLab@gmail.com>
 */
public class Vocabulary {

    /**
     * Version of the database
     */
    private final int versionDB = 2;
    /**
     * Name of the database
     */
    private String nameDB;
    /**
     * Connection to the database
     */
    private Connection connection;
    /**
     * Statement
     */
    private Statement statement;

    public Vocabulary(String name) {
        nameDB = name;
    }

    /**
     * Create a database connection
     */
    public boolean createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + nameDB + ".db");
            statement = connection.createStatement();
            statement.setQueryTimeout(30);
            correctVersion();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Stems (Stem VARCHAR NOT NULL , Word VARCHAR NOT NULL, Translate VARCHAR, Language VARCHAR NOT NULL, Known INTEGER NOT NULL  DEFAULT 0, Meeting INTEGER NOT NULL  DEFAULT 0, Study INTEGER NOT NULL  DEFAULT 0, PRIMARY KEY(Stem, Language))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Settings(Parameter VARCHAR PRIMARY KEY ASC, Value VARCHAR)");
            statement.executeUpdate("REPLACE INTO Settings VALUES ('versionDB','" + versionDB + "')");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Close a database connection
     */
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Get hard words
     *
     * @return List of hard words
     */
    public ArrayList<String> getHardWords() {
        try {
            ArrayList<String> stems = new ArrayList<String>();
            ResultSet rs = statement
                    .executeQuery("SELECT Stem FROM Stems WHERE Known=0 AND Study= 0 AND Meeting >10 ORDER BY Meeting DESC LIMIT 10");
            while (rs.next()) {
                String stem = rs.getString("Stem");
                stems.add(stem);
            }
            return stems;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Update values
     */
    public void updateValues(String stem, String word, String translate, String language, boolean isKnown, boolean isStudy, boolean updateMeeting) {
        try {
            String query = "INSERT OR REPLACE INTO Stems (Stem, Word, Translate, Language, Known, Study, Meeting)  VALUES ("
                    + "'" + escapeCharacter(stem) + "',"
                    + "'" + escapeCharacter(word) + "',"
                    + "'" + escapeCharacter(translate) + "',"
                    + "'" + escapeCharacter(language) + "',"
                    + boolToInt(isKnown) + ","
                    + boolToInt(isStudy) + ","
                    + "COALESCE((SELECT Meeting FROM Stems WHERE";
            if (updateMeeting) {
                query += " Stem='" + escapeCharacter(stem) + "' AND Language='" + escapeCharacter(language) + "') + 1,1))";
            } else {
                query += " Stem='" + escapeCharacter(stem) + "' AND Language='" + escapeCharacter(language) + "'),1))";
            }
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Get data from the database
     * @param stem is key for search
     */
    public ItemVocabulary getItem(String stem, String language) {
        try {
            ResultSet rs = statement
                    .executeQuery("SELECT * FROM Stems WHERE Stem='" + escapeCharacter(stem) + "' AND Language='"+language+"'");
            if (rs.next()) {
                String word = rs.getString("Word");
                String translate = rs.getString("Translate");
                boolean known = "1".equals(rs.getString("Known")) ? true : false;
                boolean study = "1".equals(rs.getString("Study")) ? true : false;
                int meeting = Integer.parseInt(rs.getString("Meeting"));
                return new ItemVocabulary(stem, word, translate, language, known, meeting, study);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Update Settings
     */
    public void updateSettings(boolean hideKnownDialog,
                               String colorTranslateWords,
                               String colorUnknownWords,
                               String colorKnownWords,
                               String colorStudiedWords,
                               String colorNameWords,
                               String colorHardWord,
                               String language) {
        try {
            statement.executeUpdate("REPLACE INTO Settings VALUES ('hideKnownDialog','"
                    + hideKnownDialog + "')");

            statement.executeUpdate("REPLACE INTO Settings VALUES ('colorTranslateWords','"
                    + colorTranslateWords + "')");

            statement.executeUpdate("REPLACE INTO Settings VALUES ('colorUnknownWords','"
                    + colorUnknownWords + "')");

            statement.executeUpdate("REPLACE INTO Settings VALUES ('colorKnownWords','"
                    + colorKnownWords + "')");

            statement.executeUpdate("REPLACE INTO Settings VALUES ('colorStudiedWords','"
                    + colorStudiedWords + "')");

            statement.executeUpdate("REPLACE INTO Settings VALUES ('colorNameWords','"
                    + colorNameWords + "')");

            statement.executeUpdate("REPLACE INTO Settings VALUES ('colorHardWord','"
                    + colorHardWord + "')");

            statement.executeUpdate("REPLACE INTO Settings VALUES ('language','"
                    + language + "')");

            statement.executeUpdate("REPLACE INTO Settings VALUES ('versionDB','"
                    + versionDB + "')");

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Get settings from the database
     * @return pairs parameter - value
     */
    public Map<String, String> getSettings() {
        Map<String, String> result = new HashMap<String, String>();
        ResultSet rs;
        try {
            rs = statement.executeQuery("SELECT Parameter, Value FROM Settings");

            while (rs.next())
                result.put(rs.getString("Parameter"), rs.getString("Value"));

            return result;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static String escapeCharacter(String string) {
        string = string.replace("'", "''");
        return string;
    }

    public static int boolToInt(boolean value) {
        return value ? 1 : 0;
    }

    /**
     * Correct version of the database
     */
    private void correctVersion() {
        Map<String, String> settings = getSettings();
        if (settings == null || !settings.containsKey("versionDB")) {
            // From version 0 to version 1
            try {
                // Rename field of the table Stems
                statement.executeUpdate("ALTER TABLE Stems RENAME TO tmp_Stems");
                statement.executeUpdate("CREATE TABLE Stems (Stem VARCHAR PRIMARY KEY  NOT NULL ,Word VARCHAR NOT NULL ,Known INTEGER NOT NULL  DEFAULT (0) ,Meeting INTEGER NOT NULL  DEFAULT (0) ,Translate VARCHAR)");
                statement.executeUpdate("INSERT INTO Stems SELECT Stem, Word, Remember, Meeting, Translate FROM tmp_Stems");
                statement.executeUpdate("DROP TABLE tmp_Stems");
                statement.execute("VACUUM");

                // Add column in the table Stems
                statement.executeUpdate("ALTER TABLE Stems ADD COLUMN Study INTEGER NOT NULL DEFAULT 0");

                // Update Study
                statement.executeUpdate("UPDATE Stems SET Study=1 WHERE Translate=\"\"");

                // Rename table from Sittings to Settings
                statement.executeUpdate("ALTER TABLE Sittings RENAME TO Settings");

                // Add parameter in the table Settings
                statement.executeUpdate("REPLACE INTO Settings VALUES ('versionDB','1')");

            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        settings = getSettings();
        if(settings != null && settings.containsKey("versionDB") && settings.get("versionDB").equals("1"))
        {
            try {
                // Rename field of the table Stems
                statement.executeUpdate("ALTER TABLE Stems RENAME TO tmp_Stems");
                statement.executeUpdate("CREATE TABLE Stems (Stem VARCHAR NOT NULL , Word VARCHAR NOT NULL, Translate VARCHAR, Language VARCHAR NOT NULL, Known INTEGER NOT NULL  DEFAULT 0, Meeting INTEGER NOT NULL  DEFAULT 0, Study INTEGER NOT NULL  DEFAULT 0, PRIMARY KEY(Stem, Language))");
                statement.executeUpdate("INSERT INTO Stems SELECT Stem, Word, Translate, 'english', Known, Meeting, Study FROM tmp_Stems");
                statement.executeUpdate("DROP TABLE tmp_Stems");
                statement.execute("VACUUM");

                // Add parameter in the table Settings
                statement.executeUpdate("REPLACE INTO Settings VALUES ('versionDB','2')");

            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
