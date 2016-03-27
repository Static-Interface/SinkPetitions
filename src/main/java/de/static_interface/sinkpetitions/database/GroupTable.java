package de.static_interface.sinkpetitions.database;

import de.static_interface.sinklibrary.database.AbstractTable;
import de.static_interface.sinklibrary.database.Database;

public class GroupTable extends AbstractTable<GroupRow> {

	public static final String TABLE_NAME = "groups";

	public GroupTable(Database db) {
		super(TABLE_NAME, db);
	}

}
