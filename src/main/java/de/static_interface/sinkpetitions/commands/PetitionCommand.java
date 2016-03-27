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
import de.static_interface.sinkpetitions.database.GroupRow;
import de.static_interface.sinkpetitions.database.GroupTable;

@Aliases("pe")
@DefaultPermission
public class PetitionCommand extends SinkCommand {

	private GroupTable groupTable;
	private PetitionTable petitionTable;

	private PetitionCreateCommand petitionCreateCommand;

	public PetitionCommand(Plugin plugin, GroupTable groupTable, PetitionTable petitionTable) {
		super(plugin);

		this.groupTable = groupTable;
		this.petitionTable = petitionTable;
	}

	@Override
	public void onRegistered() {
		System.out.println("Registered petition command.");
		SelectQuery<GroupRow> groups = Query.from(this.groupTable).select();
		groups.execute();
		GroupRow[] groupRows = groups.getResults();

		List<String> aliases = this.getCommand().getAliases();
		List<String> newAliases = new ArrayList<String>();
		for (String alias : aliases) {
			for (GroupRow groupRow : groupRows) {
				newAliases.add(alias.concat(":").concat(groupRow.groupName));
			}
		}
		this.getCommand().setAliases(newAliases);
		for (String alias : newAliases) {
			System.out.println(alias);
		}

		this.petitionCreateCommand = new PetitionCreateCommand(this, this.petitionTable, this.groupTable);
		this.registerSubCommand(this.petitionCreateCommand);
	}

	@Override
	protected boolean onExecute(CommandSender arg0, String arg1, String[] arg2) throws ParseException {
		return false;
	}

}
