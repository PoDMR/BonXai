package de.tudortmund.cs.bonxai.utils;

import java.util.HashMap;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;

class MockedRenameingNamespaceIdentifierUnifier extends NamespaceIdentifierUnifier {
    private HashMap<String, String> mockedNameMapping;

    public MockedRenameingNamespaceIdentifierUnifier( HashMap<String,String> nameMapping ) {
        this.mockedNameMapping = nameMapping;
    }

    public String getUnifiedIdentifier( String identifier ) {
        return this.mockedNameMapping.get( identifier );
    }
}
