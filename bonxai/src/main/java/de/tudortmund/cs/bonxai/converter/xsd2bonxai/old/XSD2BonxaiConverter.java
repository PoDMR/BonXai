/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudortmund.cs.bonxai.converter.xsd2bonxai.old;

import java.util.HashMap;

import de.tudortmund.cs.bonxai.bonxai.Bonxai;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.utils.*;

/**
 * Main class to perform transformation from {@link de.tudortmund.cs.bonxai.xsd.XSDSchema} to {bonxai.Bonxai}.
 *
 * An instance of this class is used to transform an {@link de.tudortmund.cs.bonxai.xsd.XSDSchema},
 * generated by the XML XSDSchema parser to a valid {@link de.tudortmund.cs.bonxai.bonxai.Bonxai} instance. It
 * is meant to be used directly in the tool that will perform this conversion.
 *
 * The instance of this class, used for conversion, is configurable in its
 * behaviour. By default, it will use a standard set of {@link PreProcessor},
 * {@link ForeignSchemaFlattener}, {@link TypeAutomatonFactory} and {@link
 * BonxaiFactory}, each configured accurately to perform a standard conversion.
 *
 * Each object involved in the conversion can be replaced by a custom
 * configured object, using the corresponding methods (dependency injection).
 */
public class XSD2BonxaiConverter {

    /**
     * The schema to convert.
     */
    protected XSDSchema schema;

    /*
     * The preprocessor to use.
     */
    protected PreProcessor preProcessor;

    /**
     * The foreign schema flattener to use.
     */
    protected ForeignSchemaFlattener foreignSchemaFlattener;

    /**
     * The factory to use for {@link TypeAutomaton} creation.
     */
    protected TypeAutomatonFactory typeAutomatonFactory;

    /**
     * The Bonxai factory used to perform the transformation.
     */
    protected BonxaiFactory bonxaiFactory;

    /**
     * Creates a new XSD2BonxaiConverter for the given schema.
     */
    public XSD2BonxaiConverter( XSDSchema schema ) {
        this.schema = schema;
    }

    /**
     * Sets a custom pre-processor.
     *
     * If this method is used to set a custom pre-processor object, no default
     * one will be created.
     */
    public void setPreProcessor( PreProcessor preProcessor ) {
        this.preProcessor = preProcessor;
    }

    /**
     * Sets a custom foreign schema flattener.
     *
     * If this method is used to set a custom foreign schema flattener object,
     * no default one will be created.
     */
    public void setForeignSchemaFlattener( ForeignSchemaFlattener foreignSchemaFlattener ) {
        this.foreignSchemaFlattener = foreignSchemaFlattener;
    }

    /**
     * Sets a custom {@link TypeAutomaton} factory.
     *
     * If this method is used to set a custom {@link TypeAutomaton} factory
     * object, no default one will be created.
     */
    public void setTypeAutomatonFactory( TypeAutomatonFactory typeAutomatonFactory ) {
        this.typeAutomatonFactory = typeAutomatonFactory;
    }

    /**
     * Sets a custom {@link de.tudortmund.cs.bonxai.bonxai.Bonxai} factory.
     *
     * If this method is used to set a custom {@link de.tudortmund.cs.bonxai.bonxai.Bonxai} factory object,
     * no default one will be created.
     * @param bonxaiFactory
     */
    public void setBonxaiFactory( BonxaiFactory bonxaiFactory ) {
        this.bonxaiFactory = bonxaiFactory;
    }

    /**
     * Returns the converted Bonxai.
     *
     * This method is the actual worker in the class. It instantiates the
     * default objects needed for conversion, if not dependency injection has
     * happened and performs the actual conversion. The newly created Bonxai
     * instance ist returned.
     *
     * In case the given {@link de.tudortmund.cs.bonxai.xsd.XSDSchema} contains custom {@link
     * xsd.SimpleTypes}s or {@link de.tudortmund.cs.bonxai.xsd.ComplexType}s with {@link
     * de.tudortmund.cs.bonxai.xsd.SimpleContentType}, an additional type library is generated. This
     * type library can be retrieved using {@link #getTypeLibrary}.
     *
     * A program utilizing an instance of this class to generate an {@link
     * de.tudortmund.cs.bonxai.bonxai.Bonxai} is highly recommended to retrieve the type library. It needs
     * to inject the importing code for the type library manually, once it
     * determined its path and filename.
     * @return Bonxai
     */
    public Bonxai getBonxai() {
        init();
//        try {
//            foreignSchemaFlattener.flatten(this.schema);
//        } catch (IllegalRedefinitionException e) {
//            throw new RuntimeException("Flattening XSD failed.", e);
//        }

        checkSchema(this.schema);

        this.preProcessor.process(this.schema);

        //XSDWriter xsd_Writer = new XSDWriter(schema);
        //System.out.println(xsd_Writer.getXSDString());

        TypeAutomaton typeAutomaton = this.typeAutomatonFactory.createTypeAutomaton(
            this.schema
        );
        typeAutomaton.trim();

        return this.bonxaiFactory.convert(this.schema, typeAutomaton);
    }

    /**
     * Returns the type library {@link de.tudortmund.cs.bonxai.xsd.XSDSchema} for the generated Bonxai, or null.
     *
     * This method returns the type libraries ({@link XSDSchema.Schema}) for the Bonxai
     * generated by {@link #getBonxai}. If no type library is needed, this method
     * returns an empty HashMap. If {@link #getBonxai} has not been called, yet,
     * an exception is thrown.
     *
     * Note that multiple type libraries might be needed by a converted Bonxai,
     * in case the converted XSDSchema referenced foreign schemas. The returned
     * HashMap associates each type library XSDSchema to its target namespace.
     *
     * @TODO Define exception and throw it, if {@link #getBonxai} has not been
     *       called.
     */
    public HashMap<String,XSDSchema> getTypeLibraries() {
        TypeLibraryExtractor extractor = new TypeLibraryExtractor();
        return extractor.extractTypeLibraries(this.schema);
    }

    /**
     * Initialize default objects, if no dependency injection has been used.
     *
     * So called lazy initialization mixed with dependency injection.
     */
    protected void init() {
        if (this.preProcessor == null) {
            this.preProcessor = new PreProcessor();
        }
        if (this.foreignSchemaFlattener == null) {
            this.foreignSchemaFlattener = new ForeignSchemaFlattener();
        }
        if (this.typeAutomatonFactory == null) {
            this.typeAutomatonFactory = new TypeAutomatonFactory();
        }
        if (this.bonxaiFactory == null) {
            this.bonxaiFactory = new BonxaiFactory();

            preProcessor.addVisitor(new InheritancePreProcessorVisitor());
            preProcessor.addVisitor(new SimpleContentPreProcessorVisitor());

            bonxaiFactory.addConverter(new DeclarationsConverter());
            bonxaiFactory.addConverter(new ExpressionsConverter());
            bonxaiFactory.addConverter(new GroupsConverter());
            bonxaiFactory.addConverter(new ConstraintsConverter());
        }
    }

    /**
     * Checks the schemas SymbolTables.
     */
    private void checkSchema(XSDSchema schema) {
        schema.getTypeSymbolTable().checkReferences();
        schema.getAttributeGroupSymbolTable().checkReferences();
        schema.getGroupSymbolTable().checkReferences();
        schema.getAttributeSymbolTable().checkReferences();
        schema.getElementSymbolTable().checkReferences();
        schema.getKeyAndUniqueSymbolTable().checkReferences();
    }
}
