package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.Vector;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;

class TypeAutomatonFactoryMock extends TypeAutomatonFactory {

    public TypeAutomaton createTypeAutomatonReturn;

    public int createTypeAutomatonCount = 0;

    public Vector<XSDSchema> schemas = new Vector<XSDSchema>();

    public TypeAutomaton createTypeAutomaton(XSDSchema schema) {
        createTypeAutomatonCount++;
        schemas.add(schema);
        return createTypeAutomatonReturn;
    }
}
