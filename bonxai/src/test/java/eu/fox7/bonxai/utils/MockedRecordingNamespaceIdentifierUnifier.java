package eu.fox7.bonxai.utils;

import java.util.LinkedList;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.utils.NamespaceIdentifierUnifier;
import eu.fox7.bonxai.xsd.*;

class MockedRecordingNamespaceIdentifierUnifier extends NamespaceIdentifierUnifier {
    public LinkedList<NamespaceList> appliedEnvironments = new LinkedList<NamespaceList>();

    public void applyEnvironment( NamespaceList environment ) {
        this.appliedEnvironments.add( environment );
        super.applyEnvironment( environment );
    }
}
