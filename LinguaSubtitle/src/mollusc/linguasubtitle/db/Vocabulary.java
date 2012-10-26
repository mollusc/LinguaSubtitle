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
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + nameDB
					+ ".db");
			statement = connection.createStatement();
			statement.setQueryTimeout(30);

			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS Stems(Stem VARCHAR PRIMARY KEY ASC, Word VARCHAR, Remember INTEGER, Meeting INTEGER, Translate VARCHAR)");
		} catch (SQLException e) {
			System.err.println("Error: " + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Close a database connection
	 */
	public void closeConnection() {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			// connection close failed.
			System.err.println("Error: " + e.getMessage());
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
			if (rs.next())
				return true;
		} catch (SQLException e) {
			System.err.println("Error: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Add steam to the table.
	 * 
	 * @param stem
	 * @param remember
	 *            true if user remember this word, otherwise false
	 * @return true if stem add successfully, otherwise false
	 */
	public boolean addStem(String stem, String word, String translate,
			boolean remember, boolean updateMeeting) {
		try {
			int val = remember ? 1 : 0;
			if (updateMeeting)
				statement.executeUpdate("INSERT INTO Stems VALUES('"
						+ escapeCharacter(stem) + "','" + escapeCharacter(word)
						+ "'," + val + ",1, '" + escapeCharacter(translate)
						+ "')");
			else
				statement.executeUpdate("INSERT INTO Stems VALUES('"
						+ escapeCharacter(stem) + "','" + escapeCharacter(word)
						+ "'," + val + ",0, '" + escapeCharacter(translate)
						+ "')");
		} catch (SQLException e) {
			System.err.println("Error: " + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Update values of word, translate, remember, updateMeeting
	 * 
	 * @param stem
	 * @param word
	 * @param translate
	 * @param remember
	 * @param updateMeeting
	 * @return
	 */
	private boolean updateStem(String stem, String word, String translate,
			boolean remember, Boolean updateMeeting) {
		try {
			int val = remember ? 1 : 0;
			if (updateMeeting)
				statement.executeUpdate("UPDATE Stems SET Word='"
						+ escapeCharacter(word) + "', Translate='"
						+ escapeCharacter(translate) + "', Remember=" + val
						+ ", Meeting=Meeting+1 WHERE Stem='"
						+ escapeCharacter(stem) + "'");
			else
				statement.executeUpdate("UPDATE Stems SET Word='"
						+ escapeCharacter(word) + "', Translate='"
						+ escapeCharacter(translate) + "', Remember=" + val
						+ " WHERE Stem='" + escapeCharacter(stem) + "'");
		} catch (SQLException e) {
			System.err.println("Error: " + e.getMessage());
			return false;
		}
		return true;
	}

	public void updateValues(String stem, String word, String translate,
			boolean remember, boolean updateMeeting) {
		if (isStemExist(stem))
			updateStem(stem, word, translate, remember, updateMeeting);
		else
			addStem(stem, word, translate, remember, updateMeeting);
	}

	/**
	 * Get translation of the stem
	 * 
	 * @param stem
	 * @return
	 */
	public String getTranslate(String stem) {
		try {
			ResultSet rs = statement
					.executeQuery("SELECT Translate FROM Stems WHERE Translate IS NOT NULL AND Stem='"
							+ escapeCharacter(stem) + "'");
			while (rs.next()) {
				return rs.getString("Translate");
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
	public boolean getRemember(String stem) {
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
	}

	/**
	 * Had stem met?
	 * @param stem
	 * @return
	 */
	public int getMeeting(String stem) {
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
	}

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
			e.printStackTrace();
		}

	}

	public Map<String, String> getSittings() {
		Map<String, String> result = new HashMap<String, String>();
		ResultSet rs;
		try {
			rs = statement
					.executeQuery("SELECT Parameter, Value FROM Sittings");
			while (rs.next())
				result.put(rs.getString("Parameter"), rs.getString("Value"));
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String escapeCharacter(String string) {
		string = string.replace("'", "''");
		return string;
	}
}
