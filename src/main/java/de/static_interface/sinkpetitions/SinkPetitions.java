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
import de.static_interface.sinklibrary.database.query.Query;
import de.static_interface.sinklibrary.database.query.impl.WhereQuery;
import de.static_interface.sinkpetitions.commands.PetitionCommand;
import de.static_interface.sinkpetitions.database.GroupRow;
import de.static_interface.sinkpetitions.database.GroupTable;
import de.static_interface.sinkpetitions.database.PetitionTable;

public class SinkPetitions extends JavaPlugin {

	private SinkPetitionsConfiguration sinkPetitionsConfiguration;
	private LanguageConfiguration languageConfiguration;
	private DatabaseConfiguration databaseConfiguration;

	private Database database;
	private GroupTable groupTable;
	private PetitionTable petitionTable;

	private PetitionCommand petitionCommand;

	public static Logger LOGGER;

	public static int idBase = -1;

	@Override
	public void onEnable() {
		LOGGER = getLogger();

		this.sinkPetitionsConfiguration = new SinkPetitionsConfiguration(this);
		LOGGER.setLevel(this.sinkPetitionsConfiguration.debugEnabled() ? Level.ALL : Level.CONFIG);
		idBase = this.sinkPetitionsConfiguration.getIdBase();

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
		this.petitionCommand = new PetitionCommand(this, this.groupTable, this.petitionTable);
		SinkLibrary.getInstance().registerCommand("petition", this.petitionCommand);
	}

	private void connectToDatabase(String databaseType) throws SQLException {
		switch (databaseType) {
			case "H2":
				LOGGER.log(Level.FINE, "Selected H2 database");
				File file = new File(this.getDataFolder(), "sinkpetitions");
				this.database = new H2Database(file, this.databaseConfiguration.getTablePrefix());
				break;
			case "MYSQL":
				LOGGER.log(Level.FINE, "Selected MYSQL database");
				this.database = new MySqlDatabase(this.databaseConfiguration);
				break;
			default:
				LOGGER.log(Level.FINE, "Illegal database type selected.");
				throw new IllegalArgumentException(databaseType + " is no valid database type.");
		}
		LOGGER.log(Level.FINE, "Connecting to database.");
		this.database.connect();
		LOGGER.log(Level.FINE, "Creating tables...");
		this.groupTable = new GroupTable(this.database);
		this.groupTable.create();
		WhereQuery<GroupRow> groupQuery = Query.from(this.groupTable).select().where("groupName", Query.eq("?"));
		String genericGroupName = this.languageConfiguration.get("SinkPetitions.Group.Generic").toString();
		GroupRow[] groupRows = groupQuery.getResults(genericGroupName);
		if (groupRows.length < 1) {
			GroupRow groupRow = new GroupRow();
			groupRow.groupName = genericGroupName;
			this.groupTable.insert(groupRow);
		}
		this.petitionTable = new PetitionTable(this.database);
		this.petitionTable.create();
	}

	@Override
	public void onDisable() {
		if (this.database != null) {
			try {
				LOGGER.log(Level.CONFIG, "Closing database connection.");
				this.database.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (this.languageConfiguration != null) {
			LOGGER.log(Level.CONFIG, "Saving language configuration.");
			this.languageConfiguration.save();
		}
		if (this.databaseConfiguration != null) {
			LOGGER.log(Level.CONFIG, "Saving database configuration.");
			this.languageConfiguration.save();
		}
		if (this.sinkPetitionsConfiguration != null) {
			LOGGER.log(Level.CONFIG, "Saving SinkPetitions configuration.");
			this.sinkPetitionsConfiguration.save();
		}
	}

}
