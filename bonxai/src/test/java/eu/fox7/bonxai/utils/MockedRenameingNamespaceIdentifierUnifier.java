package eu.fox7.bonxai.utils;

import java.util.HashMap;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.utils.NamespaceIdentifierUnifier;
import eu.fox7.bonxai.xsd.*;

class MockedRenameingNamespaceIdentifierUnifier extends NamespaceIdentifierUnifier {
    private HashMap<String, String> mockedNameMapping;

    public MockedRenameingNamespaceIdentifierUnifier( HashMap<String,String> nameMapping ) {
        this.mockedNameMapping = nameMapping;
    }

    public String getUnifiedIdentifier( String identifier ) {
        return this.mockedNameMapping.get( identifier );
    }
}
