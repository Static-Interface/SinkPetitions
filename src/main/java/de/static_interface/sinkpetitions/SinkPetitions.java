package de.static_interface.sinkpetitions;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import de.static_interface.sinklibrary.SinkLibrary;
import de.static_interface.sinklibrary.database.Database;
import de.static_interface.sinklibrary.database.impl.database.H2Database;
import de.static_interface.sinklibrary.database.impl.database.MySqlDatabase;
import de.static_interface.sinkpetitions.commands.PetitionCommand;
import de.static_interface.sinkpetitions.database.PetitionTable;
import de.static_interface.sinkpetitions.database.RankTable;

public class SinkPetitions extends JavaPlugin {

	private LanguageConfiguration languageConfiguration;
	private DatabaseConfiguration databaseConfiguration;

	private Database database;
	private RankTable rankTable;
	private PetitionTable petitionTable;

	private PetitionCommand petitionCommand;

	public static Logger LOGGER;

	@Override
	public void onEnable() {
		LOGGER = getLogger();

		this.languageConfiguration = new LanguageConfiguration(this);
		this.databaseConfiguration = new DatabaseConfiguration(this);
		String databaseType = this.databaseConfiguration.getDatabaseType();
		try {
			this.connectToDatabase(databaseType);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		registerCommands();
	}

	private void registerCommands() {
		this.petitionCommand = new PetitionCommand(this, this.rankTable, this.petitionTable);
		SinkLibrary.getInstance().registerCommand("petition", this.petitionCommand);
	}

	private void connectToDatabase(String databaseType) throws SQLException {
		switch (databaseType) {
			case "H2":
				LOGGER.log(Level.CONFIG, "Selected H2 database");
				File file = new File(this.getDataFolder(), "sinkpetitions");
				this.database = new H2Database(file, this.databaseConfiguration.getTablePrefix());
				break;
			case "MYSQL":
				LOGGER.log(Level.CONFIG, "Selected MYSQL database");
				this.database = new MySqlDatabase(this.databaseConfiguration);
				break;
			default:
				LOGGER.log(Level.CONFIG, "Illegal database type selected.");
				throw new IllegalArgumentException(databaseType + " is no valid database type.");
		}
		LOGGER.log(Level.CONFIG, "Connecting to database.");
		this.database.connect();
		LOGGER.log(Level.CONFIG, "Creating tables...");
		this.rankTable = new RankTable(this.database);
		if (!this.rankTable.exists()) {
			this.rankTable.create();
			//			RankRow genericRank = new RankRow();
			//			genericRank.rankName = this.languageConfiguration.get("SinkPetitions.Rank.Generic").toString();
			//			this.rankTable.insert(genericRank);
		}
		this.petitionTable = new PetitionTable(this.database);
		this.petitionTable.create();
	}

	@Override
	public void onDisable() {
		if (this.database != null) {
			try {
				LOGGER.log(Level.INFO, "Closing database connection.");
				this.database.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (this.languageConfiguration != null) {
			LOGGER.log(Level.INFO, "Saving language configuration.");
			this.languageConfiguration.save();
		}
		if (this.databaseConfiguration != null) {
			LOGGER.log(Level.INFO, "Saving database configuration.");
			this.languageConfiguration.save();
		}
	}

}
