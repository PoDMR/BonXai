package eu.fox7.schematoolkit.bonxai.om;

public class BonxaiLocation {
	public final int startRow, startColumn, endRow, endColumn;
	public BonxaiLocation(int startRow, int startColumn, int endRow, int endColumn) {
		this.startRow = startRow;       
		this.startColumn = startColumn;       
		this.endRow = endRow;       
		this.endColumn = endColumn;
	}
}
