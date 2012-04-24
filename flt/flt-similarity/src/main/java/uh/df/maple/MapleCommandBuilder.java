package uh.df.maple;

import uh.df.combspec.CombinatorialSpecification;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class MapleCommandBuilder {

	public static final String LEFTBRACE = "__lb__";
	public static final String RIGHTBRACE = "__rb__";
	public static final String PROD = "Prod";
	public static final String UNION = "Union";

	/**
	 * 
	 * @param size
	 * @return
	 */
	public static String getDrawCommand(CombinatorialSpecification specification, int size) {
		StringBuilder cmd = new StringBuilder("");
		cmd.append("with (combstruct): ");
		cmd.append(specification.toMapleFormat());
		cmd.append(": ");
		cmd.append("draw ([");
		cmd.append(specification.startSymbol());
		cmd.append(", ");
		cmd.append(specification.getName());
		cmd.append("], size=");
		cmd.append(mapleSize(size));
		cmd.append(");\n");
		return cmd.toString();
	}

	/**
	 * 
	 * @param size
	 * @return
	 */
	public static String getCountCommand(CombinatorialSpecification specification, int size) {
		StringBuffer cmd = new StringBuffer("");
		cmd.append("with (combstruct): ");
		cmd.append(specification.toMapleFormat());
		cmd.append(": ");
		cmd.append("count ([");
		cmd.append(specification.startSymbol());
		cmd.append(", ");
		cmd.append(specification.getName());
		cmd.append("], size=");
		cmd.append(mapleSize(size));
		cmd.append(");\n");
		return cmd.toString();
	}

	/**
	 * 
	 * @param size
	 * @return
	 */
	public static String getAllstructsCommand(CombinatorialSpecification specification, int size) {
		StringBuffer cmd = new StringBuffer("");
		cmd.append("with (combstruct): ");
		cmd.append(specification.toMapleFormat());
		cmd.append(": ");
		cmd.append("allstructs ([");
		cmd.append(specification.startSymbol());
		cmd.append(", ");
		cmd.append(specification.getName());
		cmd.append("], size=");
		cmd.append(mapleSize(size));
		// cmd.append("):\n");
		cmd.append(");\n");
		return cmd.toString();
	}

	/**
	 * 
	 * @param size
	 * @return
	 */
	protected static int mapleSize(int size) {
		return (size * 3);
	}

}
