/*
 * Created on Mar 16, 2007
 * Modified on $Date: 2009-05-06 13:52:49 $
 */
package eu.fox7.xml.xsdanalyser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={ElementaryAnalyserTest.class,
                     RestrictionTest.class,
                     RegexAnalysisTest.class})
public class TestAllXSDAnalyser {}
