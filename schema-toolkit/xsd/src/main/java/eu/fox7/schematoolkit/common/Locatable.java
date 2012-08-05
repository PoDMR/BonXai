package eu.fox7.schematoolkit.common;

public interface Locatable {
	public Position getStartPosition();
	public Position getEndPosition();
	public void setStartPosition(Position position);
	public void setEndPosition(Position position);
}
