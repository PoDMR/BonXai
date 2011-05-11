package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.HashMap;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;

class PreProcessorVisitorMock implements PreProcessorVisitor {
    public int counter = 0;

    public HashMap<Integer, XSDSchema> visitedSchemas = new HashMap<Integer, XSDSchema>();
    public HashMap<Integer, ComplexType> visitedComplexTypes = new HashMap<Integer, ComplexType>();

    public void visitSchema( XSDSchema schema ) {
        this.visitedSchemas.put( counter++, schema );
    }

    public void visitComplexType( ComplexType complexType ) {
        this.visitedComplexTypes.put( counter++, complexType );
    }
}
