package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.Vector;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.bonxai.Bonxai;

class ConverterMock implements Converter {

    public Integer convertCount = 0;

    public Vector<XSDSchema> schemas = new Vector<XSDSchema>();

    public Vector<TypeAutomaton> automatons = new Vector<TypeAutomaton>();

    public Vector<Bonxai> bonxais = new Vector<Bonxai>();

    public Bonxai convert(XSDSchema schema, TypeAutomaton automaton, Bonxai bonxai) {
        convertCount++;
        schemas.add(schema);
        automatons.add(automaton);
        bonxais.add(bonxai);
        return bonxai;
    }

}
