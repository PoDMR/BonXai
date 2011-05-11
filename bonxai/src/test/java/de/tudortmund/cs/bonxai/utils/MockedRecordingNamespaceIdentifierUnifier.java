package de.tudortmund.cs.bonxai.utils;

import java.util.LinkedList;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;

class MockedRecordingNamespaceIdentifierUnifier extends NamespaceIdentifierUnifier {
    public LinkedList<NamespaceList> appliedEnvironments = new LinkedList<NamespaceList>();

    public void applyEnvironment( NamespaceList environment ) {
        this.appliedEnvironments.add( environment );
        super.applyEnvironment( environment );
    }
}
