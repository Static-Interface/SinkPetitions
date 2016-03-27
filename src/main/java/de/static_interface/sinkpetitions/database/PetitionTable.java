package de.static_interface.sinkpetitions.database;

import de.static_interface.sinklibrary.database.AbstractTable;
import de.static_interface.sinklibrary.database.Database;

public class PetitionTable extends AbstractTable<PetitionRow> {

	public static final String TABLE_NAME = "petitions";

	public PetitionTable(Database db) {
		super(TABLE_NAME, db);
	}

}
