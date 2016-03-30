package de.static_interface.sinkpetitions;

import static de.static_interface.sinkpetitions.SinkPetitions.idBase;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.cli.CommandLine;

import de.static_interface.sinklibrary.database.query.Order;
import de.static_interface.sinklibrary.database.query.Query;
import de.static_interface.sinklibrary.database.query.impl.LimitQuery;
import de.static_interface.sinkpetitions.database.GroupTable;
import de.static_interface.sinkpetitions.database.PetitionRow;
import de.static_interface.sinkpetitions.database.PetitionTable;
import de.static_interface.sinkpetitions.database.PetitionWithRankRow;

public class PetitionsUtils {

	public static final String printNumberSameLength(int number, int length, int base) {
		String basedNumber = Integer.toString(number, base);
		if (basedNumber.length() == length)
			return basedNumber;
		else {
			char[] whiteSpaceArray = new char[length - basedNumber.length()];
			Arrays.fill(whiteSpaceArray, ' ');
			String whiteSpaces = String.valueOf(whiteSpaceArray);
			basedNumber = whiteSpaces + basedNumber;
			return basedNumber;
		}
	}

	private static final List<PetitionWithRankRow> applyRegex(List<PetitionWithRankRow> rows, Pattern pattern) {
		List<PetitionWithRankRow> filteredRows = new ArrayList<PetitionWithRankRow>();
		String idAsString;
		for (PetitionWithRankRow row : rows) {
			idAsString = Integer.toString(row.getPetitionId(), idBase);
			if (pattern.matcher(idAsString).matches())
				filteredRows.add(row);
			if (pattern.matcher(row.getGroupName()).matches())
				filteredRows.add(row);
			if (pattern.matcher(row.getPetitionText()).matches())
				filteredRows.add(row);
		}
		return filteredRows;
	}

	public static final List<PetitionWithRankRow> applyPattern(List<PetitionWithRankRow> rows, String pattern) {
		List<PetitionWithRankRow> filteredRows = new ArrayList<PetitionWithRankRow>();
		String idString;
		for (PetitionWithRankRow row : rows) {
			idString = Integer.toString(row.getPetitionId(), idBase);
			if (idString.contains(pattern) || row.getGroupName().contains(pattern) || row.getPetitionText().contains(pattern))
				filteredRows.add(row);
		}

		if (filteredRows.size() == 0) {
			try {
				Pattern regexPattern = Pattern.compile(pattern);
				return applyRegex(rows, regexPattern);
			} catch (PatternSyntaxException | NullPointerException e) {
				// Ignore
			}
		}

		return filteredRows;
	}

	public static final int getHighestNumberLength(PetitionTable petitionTable) {
		LimitQuery<PetitionRow> groupRow = Query.from(petitionTable).select("ID").orderBy("ID", Order.DESC).limit(1);
		PetitionRow highestPetitionNumberRow = groupRow.get();
		if (highestPetitionNumberRow == null || highestPetitionNumberRow.ID == null) {
			return -1;
		}
		int highestPetitionNumber = highestPetitionNumberRow.ID;
		String highestPetitionNumberBased = Integer.toString(highestPetitionNumber, idBase);
		int highestNumberLength = highestPetitionNumberBased.length();
		return highestNumberLength;
	}

	public static PreparedStatement createSQLQuery(PetitionTable petitionTable, GroupTable groupTable, CommandLine cmdLine) {
		String petitionsTableName = petitionTable.getName();
		String groupTableName = groupTable.getName();
		String petitionIdColumn = petitionsTableName.concat(".ID");
		String groupNameColumn = groupTableName.concat(".groupName");
		String petitionTextColumn = petitionsTableName.concat(".text");
		String petitionsForeignKeyColumn = petitionsTableName.concat(".FK_group");
		String groupIdColumn = groupTableName.concat(".ID");
		String isOpenColumn = petitionsTableName.concat(".isOpen");
		boolean isOpen = !cmdLine.hasOption('c');
		// @formatter:off
		String statement	=	"SELECT %s, %s, %s "
							+	"FROM %s, %s "
							+	"WHERE %s = %s "
							+	"AND %s = %s ";

		statement = String.format(	statement,
									petitionIdColumn, groupNameColumn, petitionTextColumn,
									petitionsTableName, groupTableName,
									petitionsForeignKeyColumn, groupIdColumn,
									isOpenColumn, isOpen);

		PreparedStatement preparedStatement = petitionTable.createPreparedStatement(statement);
		return preparedStatement;
		// @formatter:on
	}

}
