package mollusc.linguasubtitle.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		    .executeUpdate("CREATE  TABLE  IF NOT EXISTS Stems (Stem VARCHAR PRIMARY KEY  NOT NULL , Word VARCHAR NOT NULL , Remember INTEGER NOT NULL  DEFAULT 0, Meeting INTEGER NOT NULL  DEFAULT 0, Learning INTEGER NOT NULL  DEFAULT 0, Translate VARCHAR)");
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
	    System.err.println(e.getMessage());;
	}
	return false;
    }

    private void updateValues(String stem, String word, String translate, boolean remember, boolean learning, boolean updateMeeting) {
	try {
	    String query = "INSERT OR REPLACE INTO Stems (Stem, Word, Translate, Remember, Learning, Meeting)  VALUES ("
		    + "'" + escapeCharacter(stem) + "',"
		    + "'" + escapeCharacter(word) + "',"
		    + "'" + escapeCharacter(translate) + "',"
		    + boolToInt(remember) + ","
		    + boolToInt(remember) + ","
		    + "COALESCE((SELECT Meeting FROM Stems WHERE";
	    if (updateMeeting) {
		query += " Stem = " + escapeCharacter(stem) + ") + 1,1))";
	    } else {
		query += " Stem = " + escapeCharacter(stem) + "),1))";
	    }
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
		boolean learning = "1".equals(rs.getString("Learning")) ? true : false;
		int meeting = Integer.parseInt(rs.getString("Meeting"));
		return new ItemVocabulary(stem, word, translate, remember, meeting, learning);
	    }
	} catch (SQLException e) {
	    System.err.println(e.getMessage());
	}
	return null;
    }
    /*
     /**
     * Get translation of the stem
     * 
     * @param stem
     * @return
     */
    /*public String getTranslate(String stem) {
     try {
     ResultSet rs = statement
     .executeQuery("SELECT Translate FROM Stems WHERE Translate IS NOT NULL AND Stem='"
     + escapeCharacter(stem) + "'");
     if (rs.next()) {
     String word = rs.getString("Word");
     String translate = rs.getString("Translate");
     boolean remember = rs.getString("Remember") == "1" ? true : false;
     boolean learning = rs.getString("Learning") == "1" ? true : false;
     int meeting = Integer.parseInt(rs.getString("Word"));
     ItemVocabulary item = new ItemVocabulary(stem, word, translate, remember, meeting, learning);
     return item;
     }
     } catch (SQLException e) {
     System.err.println("Error: " + e.getMessage());
     }
     return null;
     }

     /**
     * Is stem known?
     * @param stem
     * @return true if stem is known, otherwise false
     */
    /*public boolean getRemember(String stem) {
     try {
     ResultSet rs = statement
     .executeQuery("SELECT Remember FROM Stems WHERE Stem='"
     + escapeCharacter(stem) + "'");
     while (rs.next()) {
     String str = rs.getString("Remember");
     return str.equals("1");
     }
     } catch (SQLException e) {
     System.err.println("Error: " + e.getMessage());
     }
     return false;
     }*/

    /**
     * Had stem met?
     *
     * @param stem
     * @return
     */
    /*	public int getMeeting(String stem) {
     try {
     ResultSet rs = statement
     .executeQuery("SELECT Meeting FROM Stems WHERE Stem='"
     + escapeCharacter(stem) + "'");
     while (rs.next()) {
     String str = rs.getString("Meeting");
     return Integer.parseInt(str);
     }
     } catch (SQLException e) {
     System.err.println("Error: " + e.getMessage());
     }
     return 0;
     }

     public String getWord(String stem) {
     try {
     ResultSet rs = statement
     .executeQuery("SELECT Word FROM Stems WHERE Stem='"
     + escapeCharacter(stem) + "'");
     while (rs.next()) {
     return rs.getString("Word");
     }
     } catch (SQLException e) {
     // if the error message is "out of memory",
     // it probably means no database file is found
     System.err.println(e.getMessage());
     return null;
     }
     return null;
     }*/
    public void updateSittings(boolean hideKnownDialog,
	    String colorTranslateWords, String colorStudiedWords,
	    String colorFamiliarWords, String colorKnownWords) {
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
		    .executeUpdate("REPLACE INTO Sittings VALUES ('colorStudiedWords','"
		    + colorStudiedWords + "')");

	    statement
		    .executeUpdate("REPLACE INTO Sittings VALUES ('colorFamiliarWords','"
		    + colorFamiliarWords + "')");

	    statement
		    .executeUpdate("REPLACE INTO Sittings VALUES ('colorKnownWords','"
		    + colorKnownWords + "')");
	} catch (SQLException e) {
	    System.err.println(e.getMessage());
	}

    }

    public Map<String, String> getSittings() {
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
	Map<String, String> sittings = getSittings();
	if (!sittings.containsKey("versionDB")) {
	    // From version 0 to version 1
	    try {
		// Add parameter in the table Sittings
		statement.executeUpdate("REPLACE INTO Sittings VALUES ('versionDB','" + versionDB + "')");

		// Add column in the table Stems
		statement.executeUpdate("ALTER TABLE Stems ADD COLUMN Learning INTEGER NOT NULL  DEFAULT 0");

		// Updata Learning
		statement.executeUpdate("UPDATE Stems SET Learning=1 WHERE Translate=\"\"");

	    } catch (SQLException e) {
		System.err.println(e.getMessage());
	    }
	}
    }
}
