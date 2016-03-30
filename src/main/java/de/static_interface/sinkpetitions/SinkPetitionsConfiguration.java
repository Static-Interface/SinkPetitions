package de.static_interface.sinkpetitions;

import java.io.File;

import org.bukkit.plugin.Plugin;

import de.static_interface.sinklibrary.api.configuration.Configuration;

public class SinkPetitionsConfiguration extends Configuration {

	public SinkPetitionsConfiguration(Plugin plugin) {
		super(new File(plugin.getDataFolder(), "config.yml"), true);
	}

	public boolean debugEnabled() {
		return (this.get("debugEnabled") != null) && Boolean.parseBoolean(this.get("debugEnabled").toString());
	}

	public int getIdBase() {
		return Integer.parseInt(this.get("idBase").toString());
	}

	@Override
	public void addDefaults() {
		this.addDefault("debugEnabled", true);
		this.addDefault("idBase", 10);
	}

}
