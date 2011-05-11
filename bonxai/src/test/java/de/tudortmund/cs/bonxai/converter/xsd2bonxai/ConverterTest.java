package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.bonxai.*;

import org.junit.Test;

abstract public class ConverterTest extends junit.framework.TestCase {

    protected XSDSchema getSchemaFixture()
    {
        return new XSDSchema();
    }

    protected TypeAutomaton getTypeAutomatonFixture()
    {
        return new TypeAutomaton(
            new TypeAutomatonState(
                new ComplexType(
                    "{}fooType",
                    new SimpleContentType()

                )
            )
        );
    }

    protected Bonxai getBonxaiFixture()
    {
        return new Bonxai();
    }

}

