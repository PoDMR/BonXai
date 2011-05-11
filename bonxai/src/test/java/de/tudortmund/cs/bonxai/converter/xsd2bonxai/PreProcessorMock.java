package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.Vector;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;

class PreProcessorMock extends PreProcessor {

    public Integer visitorCount = 0;

    public Vector<PreProcessorVisitor> visitors = new Vector<PreProcessorVisitor>();

    public Integer processCount = 0;

    public Vector<XSDSchema> schemas = new Vector<XSDSchema>();

    public void addVisitor(PreProcessorVisitor visitor) {
        visitorCount++;
        visitors.add(visitor);
    }

    public void process(XSDSchema schema) {
        processCount++;
        schemas.add(schema);
    }
}
