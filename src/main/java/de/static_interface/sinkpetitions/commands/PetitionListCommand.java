package de.static_interface.sinkpetitions.commands;

import static de.static_interface.sinkpetitions.LanguageConfiguration._;
import static de.static_interface.sinkpetitions.SinkPetitions.idBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bukkit.command.CommandSender;

import de.static_interface.sinklibrary.api.command.SinkSubCommand;
import de.static_interface.sinklibrary.api.command.annotation.Permission;
import de.static_interface.sinklibrary.api.command.annotation.Usage;
import de.static_interface.sinklibrary.util.StringUtil;
import de.static_interface.sinkpetitions.PetitionsUtils;
import de.static_interface.sinkpetitions.database.GroupTable;
import de.static_interface.sinkpetitions.database.PetitionTable;
import de.static_interface.sinkpetitions.database.PetitionWithRankRow;

@Permission("SinkPetitions.Petition.List")
@Usage("[--all] [--pattern] [--closed]")
public class PetitionListCommand extends SinkSubCommand<PetitionCommand> {

	private PetitionTable petitionTable;
	private GroupTable groupTable;

	public PetitionListCommand(PetitionCommand command, PetitionTable petitionTable, GroupTable groupTable) {
		super(command, "list");
		this.petitionTable = petitionTable;
		this.groupTable = groupTable;
	}

	@Override
	public void onRegistered() {
		Options options = this.getCommandOptions().getCliOptions();
		// @formatter:off
		Option closedOption = Option.builder("c")
						.desc(_("SinkPetitions.Petition.List.Closed"))
						.longOpt("closed")
						.build();
		// @formatter:on
		options.addOption(closedOption);
		// @formatter:off
		Option patternOption = Option.builder("p")
						.desc(_("SinkPetitions.Petition.List.Pattern"))
						.longOpt("pattern")
						.hasArg(true)
						.optionalArg(true)
						.build();
		// @formatter:on
		options.addOption(patternOption);
		// @formatter:off
		Option allOption = Option.builder("a")
						.desc(_("SinkPetitions.Petition.List.All"))
						.longOpt("all")
						.build();
		// @formatter:on
		options.addOption(allOption);
	}

	@Override
	protected boolean onExecute(CommandSender sender, String command, String[] parameters) throws ParseException {
		PreparedStatement stmt = PetitionsUtils.createSQLQuery(this.petitionTable, this.groupTable, this.getCommandLine());
		try {
			ResultSet result = stmt.executeQuery();
			List<PetitionWithRankRow> rows = PetitionWithRankRow.fromResultSet(this.petitionTable, this.groupTable, result);
			if (this.getCommandLine().hasOption('p')) {
				String optionValue = this.getCommandLine().getOptionValue('p');
				if ((optionValue == null) || (optionValue.length() < 0)) {
					sender.sendMessage(_("SinkPetitions.Petition.List.NoPattern"));
					return false;
				}
				rows = PetitionsUtils.applyPattern(rows, optionValue);
			}
			String message;
			int length = PetitionsUtils.getHighestNumberLength(this.petitionTable);
			for (PetitionWithRankRow row : rows) {
				message = _("SinkPetitions.Petition.List.Output");
				String printNumberSameLength = PetitionsUtils.printNumberSameLength(row.getPetitionId(), length, idBase);
				message = StringUtil.format(message, printNumberSameLength, row.getGroupName(), row.getPetitionText());
				sender.sendMessage(message);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
