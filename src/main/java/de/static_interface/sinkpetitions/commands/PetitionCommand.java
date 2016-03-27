package de.static_interface.sinkpetitions.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.static_interface.sinklibrary.api.command.SinkCommand;
import de.static_interface.sinklibrary.api.command.annotation.Aliases;
import de.static_interface.sinklibrary.api.command.annotation.DefaultPermission;
import de.static_interface.sinklibrary.database.query.Query;
import de.static_interface.sinklibrary.database.query.impl.SelectQuery;
import de.static_interface.sinkpetitions.database.PetitionTable;
import de.static_interface.sinkpetitions.database.RankRow;
import de.static_interface.sinkpetitions.database.RankTable;

@Aliases("pe")
@DefaultPermission
public class PetitionCommand extends SinkCommand {

	private RankTable rankTable;
	private PetitionTable petitionTable;

	private PetitionCreateCommand petitionCreateCommand;

	public PetitionCommand(Plugin plugin, RankTable rankTable, PetitionTable petitionTable) {
		super(plugin);

		this.rankTable = rankTable;
		this.petitionTable = petitionTable;
	}

	@Override
	public void onRegistered() {
		System.out.println("Registered petition command.");
		SelectQuery<RankRow> ranks = Query.from(this.rankTable).select();
		ranks.execute();
		RankRow[] rankRows = ranks.getResults();

		List<String> aliases = this.getCommand().getAliases();
		List<String> newAliases = new ArrayList<String>();
		for (String alias : aliases) {
			for (RankRow rankRow : rankRows) {
				newAliases.add(alias.concat(":").concat(rankRow.rankName));
			}
		}
		this.getCommand().setAliases(newAliases);
		for (String alias : newAliases) {
			System.out.println(alias);
		}

		this.petitionCreateCommand = new PetitionCreateCommand(this, this.petitionTable, this.rankTable);
		this.registerSubCommand(this.petitionCreateCommand);
	}

	@Override
	protected boolean onExecute(CommandSender arg0, String arg1, String[] arg2) throws ParseException {
		return false;
	}

}
