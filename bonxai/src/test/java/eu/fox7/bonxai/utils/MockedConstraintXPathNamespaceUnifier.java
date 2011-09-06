package eu.fox7.bonxai.utils;

import java.util.LinkedList;

import eu.fox7.bonxai.utils.ConstraintXPathNamespaceUnifier;
import eu.fox7.bonxai.utils.NamespaceIdentifierUnifier;
import eu.fox7.bonxai.xsd.*;

class MockedConstraintXPathNamespaceUnifier extends ConstraintXPathNamespaceUnifier {
    public LinkedList<XSDSchema> visitedSchemas = new LinkedList<XSDSchema>();

    public MockedConstraintXPathNamespaceUnifier( NamespaceIdentifierUnifier unifier ) {
        super( unifier );
    }

    public void visitSchema( XSDSchema schema ) {
        this.visitedSchemas.add( schema );
        super.visitSchema( schema );
    }
}
