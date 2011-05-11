package eu.fox7.learning;

import java.io.File;
import java.io.IOException;

import de.tudortmund.cs.bonxai.Schema;

public interface BonxaiLearner extends Learner {
	public Schema learnBonxai();
}
