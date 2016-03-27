package de.static_interface.sinkpetitions.database;

import de.static_interface.sinklibrary.database.AbstractTable;
import de.static_interface.sinklibrary.database.Database;

public class RankTable extends AbstractTable<RankRow> {

	public static final String TABLE_NAME = "ranks";

	public RankTable(Database db) {
		super(TABLE_NAME, db);
	}

}
