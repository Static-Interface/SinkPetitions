package de.static_interface.sinkpetitions;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import de.static_interface.sinklibrary.api.configuration.Configuration;

public class LanguageConfiguration extends Configuration {

	private static LanguageConfiguration INSTANCE;

	public LanguageConfiguration(Plugin plugin) {
		super(new File(plugin.getDataFolder(), "language.yml"), true);
		SinkPetitions.LOGGER.log(Level.INFO, "Loaded language configuration.");
		INSTANCE = this;
	}

	public static String _(String key) {
		return INSTANCE.get(key).toString();
	}

	@Override
	public void addDefaults() {
		this.addDefault("SinkPetitions.Vault.NotAvailable", "Vault is not available. Only limited functionality will be provided.");
		this.addDefault("SinkPetitions.Vault.Available", "Vault is available. You will be able to assign specific ranks to petitions.");

		this.addDefault("SinkPetitions.Rank.Generic", "General");
		this.addDefault("SinkPetitions.Rank.NotFound", "Rank ${0} not found!");

		this.addDefault("SinkPetitions.Petition.Create.Group", "Attaching a special group to the petition.");

		this.addDefault("SinkPetitions.Petition.Created", "Your petition has been created. The ID is ${0}.");
		this.addDefault("SinkPetitions.Petition.Deleted", "Your petition (ID ${0}) has been deleted by ${1}.");
		this.addDefault("SinkPetitions.Petition.Processed", "Your petition (ID ${0}) has been processed by ${1}.");
	}

}
