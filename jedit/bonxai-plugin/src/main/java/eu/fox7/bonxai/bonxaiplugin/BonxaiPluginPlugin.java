package eu.fox7.bonxai.bonxaiplugin;

import de.tudortmund.cs.bonxai.Schema;
import de.tudortmund.cs.bonxai.SchemaType;
import de.tudortmund.cs.bonxai.bonxai.parser.ParseException;
import de.tudortmund.cs.bonxai.converter.ConversionFailedException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class BonxaiPluginPlugin extends EditPlugin {
	protected static Map<JEditTextArea, SchemaType> schemaTypeMap = new HashMap<JEditTextArea, SchemaType>();
	
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
		doAction(view, schemas, protocol, workingDir);
	}
	
	public void handleBufferAction(View view, String protocol) {
		Buffer buffer = view.getBuffer();
		JEditTextArea textArea = view.getTextArea();

		SchemaType schemaType = getSchemaType(buffer);
		Schema schema = new Schema();
		try {
			schema.parseSchema(textArea.getText(), schemaType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		doAction(view, schema, protocol, buffer.getDirectory());
	}
	
	private SchemaType getSchemaType(Buffer buffer) {
		SchemaType schemaType = SchemaType.NONE;
		String type = buffer.getStringProperty("eu.fox7.schematype");
		if (type != null)
		  schemaType = SchemaType.valueOf(type);
		else {
			String path = buffer.getPath();
			String extension = path.substring(path.indexOf('.')+1);
			if (extension.equals("bonxai")) schemaType=SchemaType.BONXAI; else
			if (extension.equals("xsd")) schemaType=SchemaType.XSD; else
			if (extension.equals("rng")) schemaType=SchemaType.RELAXNG; else
			if (extension.equals("xml") || extension.equals("dtd")) schemaType=SchemaType.DTD;
		}
		return schemaType;
	}

	private void doAction(View view, Schema schema, String protocol, String workingDir) {
		List<Schema> schemas = new LinkedList<Schema>();
		schemas.add(schema);
		doAction(view, schemas, protocol, workingDir);
	}

	private void doAction(View view, List<Schema> schemas, String protocol, String workingDir) {
		Schema result = null;
		try {
			if ("union".equals(protocol)) {
				result = Schema.union(schemas, workingDir);
			} else if ("intersect".equals(protocol)) {
				result = Schema.intersect(schemas, workingDir);
			} else if ("difference".equals(protocol)) {
				// Subtract all schemas from first
				if (schemas.size() <= 0)
					throw new RuntimeException("No schemas selected");
				result = schemas.get(0);
				schemas.remove(result);
				for (Schema s : schemas)
					result = result.substract(s, workingDir);
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
			
			if ("removeEmptyTypes".equals(protocol)) {
				Schema schema = schemas.get(0);
				schema.removeEmptyTypes();
				result = schema;
			} else if ("removeUnreachableTypes".equals(protocol)) {
				Schema schema = schemas.get(0);
				schema.removeUnreachableTypes();
				result = schema;
			}
		} catch (ConversionFailedException e) {
			throw new RuntimeException(e);
		}

		// TODO: Replace quick output filename hacks
		// String outputFileName = firstFileName + "." + protocol;
		Collection<Schema> collection = result.getCollection();
		
		if (collection != null) {
			// int i = 0;
			for (Schema schema : collection) {
				// write(schema, outputFileName + "." + i++);
				openSchema(view, schema);
			}
			// jEdit.openFile(view, outputFileName + "." + "0");
		} else {
			// write(result, outputFileName);
			// jEdit.openFile(view, outputFileName);
			openSchema(view,result);
		}
	}

	protected static void openSchema(View view, Schema schema) {
		JEditBuffer buffer = jEdit.newFile(view);
		// USE AWT-Thread?
		buffer.insert(0, schema.getSchemaString());
		buffer.setStringProperty("eu.fox7.schematype", schema.getType().name());
	    view.getTextArea().goToBufferStart(false);
	    view.getBuffer().setMode();
	}

	// TODO: Do batch conversion? For now, only convert first selection.
	protected static Schema convert(List<Schema> schemas, SchemaType st) throws ConversionFailedException {
		if (schemas.size() <= 0)
			throw new RuntimeException("No schemas to convert");
		return schemas.get(0).convert(st);
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