package de.static_interface.sinkpetitions.commands;

import static de.static_interface.sinkpetitions.LanguageConfiguration._;

import java.util.Date;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bukkit.command.CommandSender;

import de.static_interface.sinklibrary.api.command.SinkSubCommand;
import de.static_interface.sinklibrary.api.command.annotation.Permission;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.database.query.Query;
import de.static_interface.sinklibrary.database.query.impl.WhereQuery;
import de.static_interface.sinklibrary.util.StringUtil;
import de.static_interface.sinkpetitions.database.GroupRow;
import de.static_interface.sinkpetitions.database.GroupTable;
import de.static_interface.sinkpetitions.database.PetitionRow;
import de.static_interface.sinkpetitions.database.PetitionTable;

@Permission("SinkPetitions.Petition.Create")
@Usage("[-gGroup] <message>")
public class PetitionCreateCommand extends SinkSubCommand<PetitionCommand> {

	private PetitionTable petitionTable;
	private GroupTable groupTable;

	public PetitionCreateCommand(PetitionCommand command, PetitionTable petitionTable, GroupTable groupTable) {
		super(command, "create");
		this.petitionTable = petitionTable;
		this.groupTable = groupTable;
	}

	@Override
	public void onRegistered() {
		Options options = this.getCommandOptions().getCliOptions();
		// @formatter:off
		Option option = Option.builder("g")
				.hasArg()
				.longOpt("group")
				.desc(_("SinkPetitions.Petition.Create.Group"))
				.argName("<group>")
				.build();
		// @formatter:on
		options.addOption(option);
	}

	@Override
	protected boolean onExecute(CommandSender sender, String command, String[] parameters) throws ParseException {
		int groupId = -1;

		GroupRow[] rows;
		if (this.getCommandLine().hasOption('g')) {
			String groupName = this.getCommandLine().getOptionValue('g');
			WhereQuery<GroupRow> groupIdQuery = Query.from(this.groupTable).select().where("groupName", Query.eq("?"));
			rows = groupIdQuery.getResults(groupName);
			if (rows.length < 1) {
				sender.sendMessage(StringUtil.format(_("SinkPetitions.Rank.NotFound"), groupName));
			}
		} else {
			WhereQuery<GroupRow> genericRankIdQuery = Query.from(this.groupTable).select().where("ID", Query.eq("?"));
			rows = genericRankIdQuery.getResults(1);
			if (rows.length < 1) {
				sender.sendMessage(StringUtil.format(_("SinkPetitions.Rank.NotFound"), _("SinkPetitions.Rank.Generic")));
				throw new IllegalStateException("The generic group is not available. It is needed to run this plugin.");
			}
			groupId = rows[0].ID;
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (int x = 0; x < parameters.length; x++) {
			stringBuilder.append(parameters[x]);
			stringBuilder.append(' ');
		}

		PetitionRow petitionRow = new PetitionRow();
		petitionRow.FK_group = groupId;
		petitionRow.text = stringBuilder.toString();
		petitionRow.creationTimestamp = new Date().getTime();
		this.petitionTable.insert(petitionRow);

		int petitionId = petitionRow.ID;
		String message = StringUtil.format(_("SinkPetitions.Petition.Created"), Integer.toString(petitionId, 32));
		sender.sendMessage(message);
		return true;
	}

}
