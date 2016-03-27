package de.static_interface.sinkpetitions.database;

import de.static_interface.sinklibrary.database.Row;
import de.static_interface.sinklibrary.database.annotation.Column;
import de.static_interface.sinklibrary.database.annotation.UniqueKey;

public class GroupRow implements Row {

	@Column(autoIncrement = true, primaryKey = true)
	public Integer ID;

	@Column
	@UniqueKey
	public String groupName;

}
