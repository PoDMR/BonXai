package eu.fox7.bonxai.bonxaiplugin;

import de.tudortmund.cs.bonxai.Schema;
import de.tudortmund.cs.bonxai.SchemaType;
import de.tudortmund.cs.bonxai.converter.ConversionFailedException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSFile;

public class BonxaiPluginPlugin extends EditPlugin {
	public void handleBrowserAction(View view, VFSFile[] files, String protocol) {
		if (files == null) {
			view.getToolkit().beep();
			return;
		}

		String firstFileName = null;
		List<Schema> schemas = new LinkedList<Schema>();
		for (VFSFile entry : files) {
			Schema schema = readSchema(entry.getPath());
			if (schema != null) {
				schemas.add(schema);
				if (firstFileName == null)
					firstFileName = entry.getPath();
			}
		}

		if (firstFileName == null) {
			view.getToolkit().beep();
			return;
		}

		String workingDir = new File(firstFileName).getPath();
		Schema result = null;
		if ("union".equals(protocol)) {
			try {
				result = Schema.union(schemas, workingDir);
			} catch (ConversionFailedException ex) {
				throw new RuntimeException(ex);
			}
		} else if ("intersect".equals(protocol)) {
			try {
				result = Schema.intersect(schemas, workingDir);
			} catch (ConversionFailedException ex) {
				throw new RuntimeException(ex);
			}
		} else if ("difference".equals(protocol)) {
			// Subtract all schemas from first
			try {
				if (schemas.size() <= 0)
					throw new RuntimeException("No schemas selected");
				result = schemas.get(0);
				schemas.remove(result);
				for (Schema s : schemas)
					result = result.substract(s, workingDir);
			} catch (ConversionFailedException ex) {
				throw new RuntimeException(ex);
			}
		}

		if ("xsd".equals(protocol)) {
			result = convert(schemas, SchemaType.XSD);
		} else if ("dtd".equals(protocol)) {
			result = convert(schemas, SchemaType.DTD);
		} else if ("rng".equals(protocol)) {
			result = convert(schemas, SchemaType.RELAXNG);
		} else if ("bonxai".equals(protocol)) {
			result = convert(schemas, SchemaType.BONXAI);
		}

		// TODO: Replace quick output filename hacks
		String outputFileName = firstFileName + "." + protocol;
		Collection<Schema> collection = result.getCollection();
		if (collection != null) {
			int i = 0;
			for (Schema schema : collection) {
				write(schema, outputFileName + "." + i++);
			}
			jEdit.openFile(view, outputFileName + "." + "0");
		} else if(result != null) {
			write(result, outputFileName);
			jEdit.openFile(view, outputFileName);
		} else {
			JOptionPane.showMessageDialog(view, "Operation failed");
		}
	}

	// TODO: Do batch conversion? For now, only convert first selection.
	protected static Schema convert(List<Schema> schemas, SchemaType st) {
		try {
			if (schemas.size() <= 0)
				throw new RuntimeException("No schemas to convert");
			return schemas.get(0).convert(st);
		} catch (ConversionFailedException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected static void write(Schema schema, String outputFileName) {
		try {
			schema.writeSchema(new File(outputFileName));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected static Schema readSchema(String fileName) {
		String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
		Schema schema = new Schema();
		try {
			schema.loadSchema(new File(fileName));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return schema;
	}
}