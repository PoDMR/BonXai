package eu.fox7.learning;

import java.io.File;
import java.io.IOException;

import eu.fox7.schematoolkit.bonxai.om.Bonxai;

public interface BonxaiLearner extends Learner {
	public Bonxai learnBonxai();
}
