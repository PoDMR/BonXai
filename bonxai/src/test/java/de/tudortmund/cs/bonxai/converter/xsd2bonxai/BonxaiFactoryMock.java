package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.Vector;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.bonxai.Bonxai;

class BonxaiFactoryMock extends BonxaiFactory {

    public Bonxai convertReturn;

    public Integer converterCount = 0;

    public Vector<Converter> converters = new Vector<Converter>();

    public Integer convertCount = 0;

    public Vector<XSDSchema> schemas = new Vector<XSDSchema>();

    public Vector<TypeAutomaton> typeAutomatons = new Vector<TypeAutomaton>();

    public void addConverter(Converter converter) {
        converterCount++;
        converters.add(converter);
    }

    public Bonxai convert(XSDSchema schema, TypeAutomaton typeAutomaton) {
        convertCount++;
        schemas.add(schema);
        typeAutomatons.add(typeAutomaton);
        return convertReturn;
    }
}
