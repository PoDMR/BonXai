package eu.fox7.jedit.action;

import java.io.IOException;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSFile;

import eu.fox7.jedit.FoxlibAction;
import eu.fox7.schematoolkit.SchemaConverter;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

public class ConvertSchema extends FoxlibAction {
	private static final String[] ACTIONS;
	
	static {
		ACTIONS = new String[SchemaLanguage.values().length];
		for (int i=0; i< SchemaLanguage.values().length; i++)
			ACTIONS[i] = "convert2" + SchemaLanguage.values()[i].name();
	}
	
	@Override
	public String[] getActions() {
		return ACTIONS;
	}

	/* (non-Javadoc)
	 * @see eu.fox7.jedit.FoxlibAction#handleBrowserAction(org.gjt.sp.jedit.View, org.gjt.sp.jedit.io.VFSFile[], java.lang.String)
	 */
	@Override
	public void handleBrowserAction(View view, VFSFile[] files,
			String actionName) {
		for (VFSFile file: files) {
			try {
				SchemaHandler schemaHandler = this.parseFile(file);
				SchemaLanguage sourceLanguage = schemaHandler.getSchemaLanguage();
				SchemaLanguage targetLanguage = this.getTargetLanguage(actionName);
				schemaHandler.getSchemaLanguage().getConverter(targetLanguage);
				SchemaConverter converter = sourceLanguage.getConverter(targetLanguage);
				SchemaHandler target = converter.convert(schemaHandler);
				this.openSchemaInNewBuffer(target, view);
			} catch (ConversionFailedException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (SchemaToolkitException e) {
				throw new RuntimeException(e);
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see eu.fox7.jedit.FoxlibAction#handleBufferAction(org.gjt.sp.jedit.View, java.lang.String)
	 */
	@Override
	public void handleBufferAction(View view, String actionName) {
		SchemaHandler schemaHandler = this.getSchemaHandler(view.getBuffer());
		SchemaLanguage sourceLanguage = schemaHandler.getSchemaLanguage();
		SchemaLanguage targetLanguage = this.getTargetLanguage(actionName);
		schemaHandler.getSchemaLanguage().getConverter(targetLanguage);
		SchemaConverter converter = sourceLanguage.getConverter(targetLanguage);
		if (converter==null)
			throw new RuntimeException("Schemaconverter from " + sourceLanguage + " to " + targetLanguage + " not found.");
		try {
			SchemaHandler target = converter.convert(schemaHandler);
			this.openSchemaInNewBuffer(target, view);
		} catch (ConversionFailedException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SchemaToolkitException e) {
			throw new RuntimeException(e);
		}
	}

	private SchemaLanguage getTargetLanguage(String actionName) {
		String language = actionName.substring(actionName.indexOf('2')+1);
		return SchemaLanguage.valueOf(language);
	}
	
	

}
