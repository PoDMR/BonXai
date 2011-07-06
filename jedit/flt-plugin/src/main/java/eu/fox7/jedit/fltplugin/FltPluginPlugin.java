package eu.fox7.jedit.fltplugin;

import gjb.flt.regex.infer.rwr.NoOpportunityFoundException;
import java.io.Writer;
import gjb.util.xml.ConfigurationException;
import gjb.flt.regex.infer.crx.LargeSampleCRXInferrer;
import gjb.flt.regex.infer.crx.SmallSampleCRXInferrer;
import gjb.flt.regex.infer.rwr.RewriteEngine;
import gjb.flt.regex.infer.rwr.Rewriter;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.schema.infer.ixsd.XsdLearner;
import gjb.flt.treeautomata.impl.ContextAutomaton;
import gjb.util.sampling.SampleException;
import gjb.util.xml.acstring.ExampleParsingException;
import gjb.util.xml.acstring.XMLtoAncestorChildrenConverter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSFile;
import static eu.fox7.jedit.fltplugin.FltFacility.*;

public class FltPluginPlugin extends EditPlugin {
	public void handleBrowserAction(View view, VFSFile[] files, String protocol) {
		if (files == null) {
			view.getToolkit().beep();
			return;
		}

		for (int i = 0; i < files.length; i++) {
			String outputFileName = null;
			
			if ("chare".equals(protocol)) {
				outputFileName = chareInfer(files[i].getPath());
			} else if ("sore".equals(protocol)) {
				outputFileName = soreInfer(files[i].getPath());
			} else if ("xsdinf".equals(protocol)) {
				outputFileName = xsdInfer(files[i].getPath());
			}
			
			if (outputFileName != null) {
				jEdit.openFile(view, outputFileName);
			} else {
				JOptionPane.showMessageDialog(view, "Operation failed");
			}
		}
		
	}
}

class FltFacility {
	/*
	 * Wrapper for actions
	 */

	public static String chareInfer(String fileName) {
		String outputFileName = fileName + ".chareinfer";
		String result = chareInfer(fileToCharArrays(fileName));
		writeToFile(outputFileName, result);
		return outputFileName;
	}
	
	public static String soreInfer(String fileName) {
		String outputFileName = fileName + ".soreinfer";
		String result = soreInfer(fileToCharArrays(fileName));
		writeToFile(outputFileName, result);
		return outputFileName;
	}
	
	public static String xsdInfer(String fileName) {
		String outputFileName = fileName + ".xsdinfer";
		String result = xsdInferString(fileName);
		writeToFile(outputFileName, result);
		return outputFileName;
	}
	
	/*
	 * IO
	 */
	
	private static String[][] fileToCharArrays(String fileName) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(fileName);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			List<List<String>> resultList = new LinkedList<List<String>>();
			String s = br.readLine();
			while (s != null) {
				List<String> lineList = new LinkedList<String>();
				lineList.addAll(Arrays.asList(s.split(";")));
				resultList.add(lineList);
				s = br.readLine();
			}
			br.close();

			String[][] result = new String[resultList.size()][];
			for (int i = 0; i < result.length; i++) {
				List<String> lineList = resultList.get(i);
				result[i] = new String[lineList.size()];
				for (int j = 0; j < result[i].length; j++) {
					result[i][j] = lineList.get(j);
				}
			}
			return result;
		} catch (FileNotFoundException e) {
			fail(e);
		} catch (IOException e) {
			fail(e);
		} finally {
			try {
				if (br != null) br.close();
				if (isr != null) isr.close();
				if (fis != null) fis.close();
			} catch (IOException e) {
				fail(e);
			}
		}
		return null;
	}
	
	private static void writeToFile(String fileName, String string) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(fileName);
			osw = new OutputStreamWriter(fos);
			bw = new BufferedWriter(osw);
			bw.write(string);
			bw.close();
		} catch (FileNotFoundException e) {
			fail(e);
		} catch (IOException e) {
			fail(e);
		} finally {
			try {
				if (bw != null) bw.close();
				if (osw != null) osw.close();
				if (fos != null) fos.close();
			} catch (IOException e) {
				fail(e);
			}
		}
	}
	
	/*
	 * Actions
	 */
	
	private static String chareInfer(String[][] samples) {
		SmallSampleCRXInferrer deriver = new SmallSampleCRXInferrer();
		for (String[] example : samples) {
			deriver.addExample(example);
		}
		String reg = deriver.infer();
		return reg;
	}
	
	private static String soreInfer(String[][] samples) {
		GraphAutomatonFactory factory = new GraphAutomatonFactory();
		Automaton automaton = factory.create(samples);
		RewriteEngine rewriter = new Rewriter();
		try {
			return rewriter.rewriteToRegex(automaton);
		} catch (NoOpportunityFoundException e) {
			fail(e);
			return "No opportunity found: " + Arrays.deepToString(samples);
		}
	}
	
	private static String xsdInferString(String inputXMLFileName) {
		File inputFile = new File(inputXMLFileName);
		String ancestorChildTextFileName = inputXMLFileName + ".act";
		try {
			Writer writer = new FileWriter(ancestorChildTextFileName);
			XMLtoAncestorChildrenConverter converter = new XMLtoAncestorChildrenConverter(writer);
			converter.parse(inputFile);
			writer.toString();
			File tempFile = new File(ancestorChildTextFileName);
			XsdLearner learner = new XsdLearner();
			ContextAutomaton contextAutomaton = learner.learn(tempFile, 30);
			return contextAutomaton.toString();
		} catch (ConfigurationException e){
			fail(e);
		} catch (IOException e){
			fail(e);
		} catch (SampleException e) {
			fail(e);
		} catch (ExampleParsingException e) {
			fail(e);
		}
		return null;
	}

	private static void fail(Exception e) {
		e.printStackTrace();
	}
}