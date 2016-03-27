package de.static_interface.sinkpetitions;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import de.static_interface.sinklibrary.api.configuration.Configuration;
import de.static_interface.sinklibrary.database.DatabaseConnectionInfo;

public class DatabaseConfiguration extends Configuration implements DatabaseConnectionInfo {

	public DatabaseConfiguration(Plugin plugin) {
		super(new File(plugin.getDataFolder(), "database.yml"), true);
		SinkPetitions.LOGGER.log(Level.INFO, "Loaded database configuration.");
	}

	@Override
	public void addDefaults() {
		this.addDefault("databaseType", "H2");
		this.addDefault("server", "localhost");
		this.addDefault("port", "3306");
		this.addDefault("user", "root");
		this.addDefault("password", "");
		this.addDefault("databaseName", "SinkPetitions");
		this.addDefault("tableprefix", "sp_");
	}

	public String getDatabaseType() {
		return this.get("databaseType").toString();
	}

	@Override
	public String getAddress() {
		return this.get("server").toString();
	}

	@Override
	public String getDatabaseName() {
		return this.get("databaseName").toString();
	}

	@Override
	public String getPassword() {
		return this.get("password").toString();
	}

	@Override
	public int getPort() {
		return Integer.parseInt(this.get("port").toString());
	}

	@Override
	public String getTablePrefix() {
		return this.get("tableprefix").toString();
	}

	@Override
	public String getUsername() {
		return this.get("user").toString();
	}

}
