package mollusc.linguasubtitle.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Vocabulary {

    /**
     * Version of the database
     */
    private final int versionDB = 1;
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
	    connection = DriverManager.getConnection("jdbc:sqlite:" + nameDB
		    + ".db");
	    statement = connection.createStatement();
	    statement.setQueryTimeout(30);
	    statement
		    .executeUpdate("CREATE  TABLE  IF NOT EXISTS Stems (Stem VARCHAR PRIMARY KEY  NOT NULL , Word VARCHAR NOT NULL , Remember INTEGER NOT NULL  DEFAULT 0, Meeting INTEGER NOT NULL  DEFAULT 0, Study INTEGER NOT NULL  DEFAULT 0, Translate VARCHAR)");
	    correctVersion();
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
     * Is stem exist?
     *
     * @param stem
     * @return true if stem exist, otherwise false
     */
    public boolean isStemExist(String stem) {
	try {
	    ResultSet rs = statement
		    .executeQuery("SELECT Stem FROM Stems WHERE Stem='"
		    + escapeCharacter(stem) + "'");
	    if (rs.next()) {
		return true;
	    }
	} catch (SQLException e) {
	    System.err.println(e.getMessage());
	}
	return false;
    }
    
    /**
     * Get hard words
     * @return 
     */
    public ArrayList<String> getHardWords()
    {
	try {
	    ArrayList<String> stems = new ArrayList<String>();
	    ResultSet rs = statement
		    .executeQuery("SELECT Stem FROM Stems WHERE Remember=0 AND Study= 0 AND Meeting >10 ORDER BY Meeting DESC LIMIT 10");
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
     * @param stem
     * @param word
     * @param translate
     * @param isKnown
     * @param isStudy
     * @param updateMeeting 
     */
    public void updateValues(String stem, String word, String translate, boolean isKnown, boolean isStudy, boolean updateMeeting) {
	try {
	    String query = "INSERT OR REPLACE INTO Stems (Stem, Word, Translate, Remember, Study, Meeting)  VALUES ("
		    + "'" + escapeCharacter(stem) + "',"
		    + "'" + escapeCharacter(word) + "',"
		    + "'" + escapeCharacter(translate) + "',"
		    + boolToInt(isKnown) + ","
		    + boolToInt(isStudy) + ","
		    + "COALESCE((SELECT Meeting FROM Stems WHERE";
	    if (updateMeeting) {
		query += " Stem='" + escapeCharacter(stem) + "') + 1,1))";
	    } else {
		query += " Stem='" + escapeCharacter(stem) + "'),1))";
	    }
	    statement.executeUpdate(query);
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	}
    }

    public ItemVocabulary getItem(String stem) {
	try {
	    ResultSet rs = statement
		    .executeQuery("SELECT * FROM Stems WHERE Stem='" + escapeCharacter(stem) + "'");
	    if (rs.next()) {
		String word = rs.getString("Word");
		String translate = rs.getString("Translate");
		boolean remember = "1".equals(rs.getString("Remember")) ? true : false;
		boolean study = "1".equals(rs.getString("Study")) ? true : false;
		int meeting = Integer.parseInt(rs.getString("Meeting"));
		return new ItemVocabulary(stem, word, translate, remember, meeting, study);
	    }
	} catch (SQLException e) {
	    System.err.println(e.getMessage());
	}
	return null;
    }
    
    /**
     * Update sittings
     * @param hideKnownDialog
     * @param colorTranslateWords
     * @param colorUnknownWords
     * @param colorKnownWords
     * @param colorStudiedWords
     * @param colorNameWords
     * @param colorHardWord 
     */
    public void updatSettings(boolean hideKnownDialog,
	    String colorTranslateWords,
	    String colorUnknownWords,
	    String colorKnownWords,
	    String colorStudiedWords,
	    String colorNameWords,
	    String colorHardWord) {
	try {
	    statement
		    .executeUpdate("CREATE TABLE IF NOT EXISTS Sittings(Parameter VARCHAR PRIMARY KEY ASC, Value VARCHAR)");

	    statement
		    .executeUpdate("REPLACE INTO Sittings VALUES ('hideKnownDialog','"
		    + hideKnownDialog + "')");
	    
	    statement
		    .executeUpdate("REPLACE INTO Sittings VALUES ('colorTranslateWords','"
		    + colorTranslateWords + "')");

	    statement
		    .executeUpdate("REPLACE INTO Sittings VALUES ('colorUnknownWords','"
		    + colorUnknownWords + "')");
	    
	    statement
		    .executeUpdate("REPLACE INTO Sittings VALUES ('colorKnownWords','"
		    + colorKnownWords + "')");

	    statement
		    .executeUpdate("REPLACE INTO Sittings VALUES ('colorStudiedWords','"
		    + colorStudiedWords + "')");
	    
	    statement
		    .executeUpdate("REPLACE INTO Sittings VALUES ('colorNameWords','"
		    + colorNameWords + "')");
	    
	    statement
		    .executeUpdate("REPLACE INTO Sittings VALUES ('colorHardWord','"
		    + colorHardWord + "')");
	    
	} catch (SQLException e) {
	    System.err.println(e.getMessage());
	}

    }

    public Map<String, String> getSettings() {
	Map<String, String> result = new HashMap<String, String>();
	ResultSet rs;
	try {
	    rs = statement
		    .executeQuery("SELECT Parameter, Value FROM Sittings");
	    while (rs.next()) {
		result.put(rs.getString("Parameter"), rs.getString("Value"));
	    }
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

    private void correctVersion() {
	Map<String, String> sittings = getSettings();
	if (!sittings.containsKey("versionDB")) {
	    // From version 0 to version 1
	    try {
		// Add parameter in the table Sittings
		statement.executeUpdate("REPLACE INTO Sittings VALUES ('versionDB','" + versionDB + "')");

		// Add column in the table Stems
		statement.executeUpdate("ALTER TABLE Stems ADD COLUMN Study INTEGER NOT NULL  DEFAULT 0");

		// Updata Study
		statement.executeUpdate("UPDATE Stems SET Study=1 WHERE Translate=\"\"");

	    } catch (SQLException e) {
		System.err.println(e.getMessage());
	    }
	}
    }
}
