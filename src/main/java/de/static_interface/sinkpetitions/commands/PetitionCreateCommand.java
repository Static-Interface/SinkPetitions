package de.static_interface.sinkpetitions.commands;

import static de.static_interface.sinkpetitions.LanguageConfiguration._;
import static de.static_interface.sinkpetitions.SinkPetitions.idBase;

import java.util.Date;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		this.getCommandOptions().setPlayerOnly(true);
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
			WhereQuery<GroupRow> genericRankIdQuery = Query.from(this.groupTable).select().where("groupName", Query.eq("?"));
			rows = genericRankIdQuery.getResults(_("SinkPetitions.Group.Generic"));
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
		if (stringBuilder.toString().trim().length() == 0)
			return true;

		Player player = (Player) sender;
		Location location = player.getLocation();
		double xPosition = location.getX();
		double yPosition = location.getY();
		double zPosition = location.getZ();
		double yaw = location.getYaw();
		double pitch = location.getPitch();
		String worldId = location.getWorld().getUID().toString();
		String creatorId = player.getUniqueId().toString();

		PetitionRow petitionRow = new PetitionRow();
		petitionRow.FK_group = groupId;
		petitionRow.text = stringBuilder.toString();
		petitionRow.isOpen = true;
		petitionRow.creationTimestamp = new Date().getTime();
		petitionRow.creatorId = creatorId;
		petitionRow.xPosition = xPosition;
		petitionRow.yPosition = yPosition;
		petitionRow.zPosition = zPosition;
		petitionRow.yaw = yaw;
		petitionRow.pitch = pitch;
		petitionRow.worldId = worldId;
		this.petitionTable.insert(petitionRow);

		int petitionId = petitionRow.ID;
		String message = StringUtil.format(_("SinkPetitions.Petition.Created"), Integer.toString(petitionId, idBase));
		sender.sendMessage(message);
		return true;
	}

}
