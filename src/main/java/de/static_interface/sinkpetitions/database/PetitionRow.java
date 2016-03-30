package de.static_interface.sinkpetitions.database;

import de.static_interface.sinklibrary.database.Row;
import de.static_interface.sinklibrary.database.annotation.Column;
import de.static_interface.sinklibrary.database.annotation.ForeignKey;

public class PetitionRow implements Row {

	@Column(autoIncrement = true, primaryKey = true)
	public Integer ID;

	@Column
	public String text;

	@Column
	public Long creationTimestamp;

	@Column(keyLength = 36)
	public String creatorId;

	@Column
	@ForeignKey(table = GroupTable.class, column = "ID")
	public Integer FK_group;

	@Column
	public Boolean isOpen;

	@Column
	public Double xPosition;

	@Column
	public Double yPosition;

	@Column
	public Double zPosition;

	@Column
	public Double yaw;

	@Column
	public Double pitch;

	@Column(keyLength = 36)
	public String worldId;

}
