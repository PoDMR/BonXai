package gjb.flt.regex.infer;

import gjb.flt.regex.Regex;

public interface Inferrer {

	abstract public String infer() throws InferenceException;

	abstract public Regex inferRegex() throws InferenceException;

	public void addExample(String[] example);

	public int getNumberOfExamples();

}