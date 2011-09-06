package eu.fox7.flt.regex.infer;

import eu.fox7.flt.regex.Regex;

public interface Inferrer {

	abstract public String infer() throws InferenceException;

	abstract public Regex inferRegex() throws InferenceException;

	public void addExample(String[] example);

	public int getNumberOfExamples();

}