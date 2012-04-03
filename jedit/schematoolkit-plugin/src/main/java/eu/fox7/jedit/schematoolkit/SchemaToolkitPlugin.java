package eu.fox7.jedit.schematoolkit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.SchemaToolkitException;

public class SchemaToolkitPlugin extends EditPlugin {
	protected static Map<JEditTextArea, SchemaLanguage> schemaTypeMap = new HashMap<JEditTextArea, SchemaLanguage>();
	
	public void handleBrowserAction(View view, VFSFile[] files, String protocol) {
		if (files == null) {
			view.getToolkit().beep();
			return;
		}
		
		String firstFileName = null;
		List<SchemaHandler> schemas = new LinkedList<SchemaHandler>();
		for (VFSFile entry : files) {
			SchemaHandler schemaHandler = readSchema(entry.getPath());
			if (schemaHandler != null) {
				schemas.add(schemaHandler);
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

		SchemaLanguage schemaLanguage = getSchemaType(buffer);
		SchemaHandler schemaHandler = schemaLanguage.getSchemaHandler();
		try {
			schemaHandler.parseSchema(textArea.getText());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		doAction(view, schemaHandler, protocol, buffer.getDirectory());
	}
	
	private SchemaLanguage getSchemaType(Buffer buffer) {
		SchemaLanguage schemaLanguage = null;
		String type = buffer.getStringProperty("eu.fox7.schemalanguage");
		if (type != null)
		  schemaLanguage = SchemaLanguage.valueOf(type);
		else {
			String path = buffer.getPath();
			String extension = path.substring(path.indexOf('.')+1);
			if (extension.equals("bonxai")) schemaLanguage=SchemaLanguage.BONXAI; else
			if (extension.equals("xsd")) schemaLanguage=SchemaLanguage.XMLSCHEMA; else
			if (extension.equals("rng")) schemaLanguage=SchemaLanguage.RELAXNG; else
			if (extension.equals("xml") || extension.equals("dtd")) schemaLanguage=SchemaLanguage.DTD;
		}
		return schemaLanguage;
	}

	private void doAction(View view, SchemaHandler schemaHandler, String protocol, String workingDir) {
		List<SchemaHandler> schemas = new LinkedList<SchemaHandler>();
		schemas.add(schemaHandler);
		doAction(view, schemas, protocol, workingDir);
	}

	private void doAction(View view, List<SchemaHandler> schemas, String protocol, String workingDir) {
//		SchemaHandler result = null;
//		try {
//			if ("union".equals(protocol)) {
//				result = SchemaHandler.union(schemas, workingDir);
//			} else if ("intersect".equals(protocol)) {
//				result = SchemaHandler.intersect(schemas, workingDir);
//			} else if ("difference".equals(protocol)) {
//				// Subtract all schemas from first
//				if (schemas.size() <= 0)
//					throw new RuntimeException("No schemas selected");
//				result = schemas.get(0);
//				schemas.remove(result);
//				for (SchemaHandler s : schemas)
//					result = result.substract(s, workingDir);
//			}
//
//			if ("xsd".equals(protocol)) {
//				result = convert(schemas, SchemaLanguage.XMLSCHEMA);
//			} else if ("dtd".equals(protocol)) {
//				result = convert(schemas, SchemaLanguage.DTD);
//			} else if ("rng".equals(protocol)) {
//				result = convert(schemas, SchemaLanguage.RELAXNG);
//			} else if ("bonxai".equals(protocol)) {
//				result = convert(schemas, SchemaLanguage.BONXAI);
//			}
//			
//			if ("removeEmptyTypes".equals(protocol)) {
//				SchemaHandler schemaHandler = schemas.get(0);
//				schemaHandler.removeEmptyTypes();
//				result = schemaHandler;
//			} else if ("removeUnreachableTypes".equals(protocol)) {
//				SchemaHandler schemaHandler = schemas.get(0);
//				schemaHandler.removeUnreachableTypes();
//				result = schemaHandler;
//			}
//		} catch (ConversionFailedException e) {
//			throw new RuntimeException(e);
//		}
//
//		// TODO: Replace quick output filename hacks
//		// String outputFileName = firstFileName + "." + protocol;
//		Collection<SchemaHandler> collection = result.getCollection();
//		
//		if (collection != null) {
//			// int i = 0;
//			for (SchemaHandler schemaHandler : collection) {
//				// write(schemaHandler, outputFileName + "." + i++);
//				openSchema(view, schemaHandler);
//			}
//			// jEdit.openFile(view, outputFileName + "." + "0");
//		} else {
//			// write(result, outputFileName);
//			// jEdit.openFile(view, outputFileName);
//			openSchema(view,result);
//		}
	}

	protected static void openSchema(View view, SchemaHandler schemaHandler) {
		JEditBuffer buffer = jEdit.newFile(view);
		// USE AWT-Thread?
		try {
			buffer.insert(0, schemaHandler.getSchemaString());
			buffer.setStringProperty("eu.fox7.schemalanguage", schemaHandler.getSchemaLanguage().name());
			view.getTextArea().goToBufferStart(false);
			view.getBuffer().setMode();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SchemaToolkitException e) {
			throw new RuntimeException(e);
		}
	}

	// TODO: Do batch conversion? For now, only convert first selection.
//	protected static SchemaHandler convert(List<SchemaHandler> schemas, SchemaLanguage st) throws ConversionFailedException {
//		if (schemas.size() <= 0)
//			throw new RuntimeException("No schemas to convert");
//		return schemas.get(0).convert(st);
//	}

	protected static void write(SchemaHandler schemaHandler, String outputFileName) {
		try {
			schemaHandler.writeSchema(new File(outputFileName));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (SchemaToolkitException e) {
			throw new RuntimeException(e);
		} 
	}

	protected static SchemaHandler readSchema(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
		
		SchemaLanguage language;
		
		if (extension.equals("xsd")) {
			language = SchemaLanguage.XMLSCHEMA;
		} else if (extension.equals("dtd") || extension.equals("xml")) {
			language = SchemaLanguage.DTD;
		} else if (extension.equals("rng")) {
			language = SchemaLanguage.RELAXNG;
		} else if (extension.equals("bonxai")) {
			language = SchemaLanguage.BONXAI;
		} else {
			throw new RuntimeException("Unknown extension: " + extension);
		}
		
		SchemaHandler schemaHandler = language.getSchemaHandler();

		try {
			schemaHandler.loadSchema(new File(fileName));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return schemaHandler;
	}
}