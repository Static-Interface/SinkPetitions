package de.static_interface.sinkpetitions.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PetitionWithRankRow implements Comparable<PetitionWithRankRow> {

	private int petitionId;
	private String groupName;
	private String petitionText;

	public PetitionWithRankRow(int petitionId, String groupName, String petitionText) {
		this.petitionId = petitionId;
		this.groupName = groupName;
		this.petitionText = petitionText;
	}

	public int getPetitionId() {
		return this.petitionId;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public String getPetitionText() {
		return this.petitionText;
	}

	@Override
	public int compareTo(PetitionWithRankRow o) {
		if (o == null)
			return 1;
		return Integer.compare(o.petitionId, this.petitionId);
	}

	public static List<PetitionWithRankRow> fromResultSet(PetitionTable petitionTable, GroupTable groupTable, ResultSet set) {
		String petitionsTableName = petitionTable.getName();
		String groupTableName = groupTable.getName();
		String petitionIdColumn = petitionsTableName.concat(".ID");
		String groupNameColumn = groupTableName.concat(".groupName");
		String petitionTextColumn = petitionsTableName.concat(".text");

		int petitionId;
		String groupName;
		String petitionText;
		PetitionWithRankRow petitionWithRankRow;
		List<PetitionWithRankRow> petitionWithRankRows = new ArrayList<PetitionWithRankRow>();
		try {
			while (set.next()) {
				petitionId = set.getInt(petitionIdColumn);
				groupName = set.getString(groupNameColumn);
				petitionText = set.getString(petitionTextColumn);
				petitionWithRankRow = new PetitionWithRankRow(petitionId, groupName, petitionText);
				petitionWithRankRows.add(petitionWithRankRow);
			}
			Collections.sort(petitionWithRankRows);
			return petitionWithRankRows;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
