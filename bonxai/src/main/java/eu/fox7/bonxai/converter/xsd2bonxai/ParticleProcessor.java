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
package eu.fox7.bonxai.converter.xsd2bonxai;

import java.util.LinkedList;

import eu.fox7.bonxai.bonxai.*;
import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.XSDSchema.Qualification;

class ParticleProcessor {

    /**
     * USPL group symbol table.
     *
     * Symbol table of Bonxai element groups. Used to get references to
     * corresponding groups in USPL when an XSD GroupRef is discovered.
     */
    private SymbolTableFoundation<ElementGroupElement> elementGroupSymbolTable;

    private LinkedList<ParticleProcessListener> listeners = new LinkedList<ParticleProcessListener>();

    private Qualification elementFormDefault;
    
    /**
     * Creates a new ParticleProcessor.
     *
     * Particle processing relies on the given {@link SymbolTable}s for
     * GroupRef transformation. The given elementGroupSymbolTable must be the
     * one used in the target {@link Bonxai} instance.
     */
    public ParticleProcessor(SymbolTableFoundation<ElementGroupElement> elementGroupSymbolTable, Qualification elementFormDefault) {
        this.elementGroupSymbolTable = elementGroupSymbolTable;
        this.elementFormDefault = elementFormDefault;
    }

    /**
     * Add listener to be notified on Particles.
     */
    public void attachListener(ParticleProcessListener listener) {
        listeners.add(listener);
    }

    /**
     * Dispatches to the conversion of the given particle to the corresponding
     * methods and returns the result.
     */
    public Particle convertParticle(Particle particle) {
        Particle resultParticle;

        if (particle instanceof ParticleContainer) {
            resultParticle = convertParticleContainer((ParticleContainer) particle);
        } else if (particle instanceof AnyPattern) {
            resultParticle = convertAnyPattern((AnyPattern) particle);
        } else if (particle instanceof eu.fox7.bonxai.xsd.Element) {
            resultParticle = convertElement((eu.fox7.bonxai.xsd.Element) particle);
        } else if (particle instanceof ElementRef) {
            resultParticle = convertElementRef((ElementRef) particle);
        } else if (particle instanceof GroupRef) {
            resultParticle = convertGroupRef((GroupRef) particle);
        } else {
            throw new RuntimeException("Unknown Particle of class " + particle.getClass());
        }

        return resultParticle;
    }

    /**
     * Converts particleContainer, depending on its realization.
     *
     * There are no dedicated methods for the different ParticleContainers,
     * since they do not need to be converted themselves but only the contained
     * Particles.
     */
    public ParticleContainer convertParticleContainer(ParticleContainer particleContainer) {
        ParticleContainer result;

        notifyListeners(particleContainer);

        if (particleContainer instanceof AllPattern) {

            result = new AllPattern();

        } else if (particleContainer instanceof ChoicePattern) {

            result = new ChoicePattern();

        } else if (particleContainer instanceof CountingPattern) {

            result = new CountingPattern(
                ((CountingPattern) particleContainer).getMin(),
                ((CountingPattern) particleContainer).getMax()
            );

        } else if (particleContainer instanceof SequencePattern) {

            result = new SequencePattern();

        } else {
            throw new RuntimeException("Unknown ParticleContainer of class " + particleContainer.getClass());
        }

        for (Particle nestedParticle : particleContainer.getParticles()) {
            result.addParticle(convertParticle(nestedParticle));
        }

        return result;
    }

    /**
     * Converts a single Element.
     *
     * @TODO: Handling of namespaces should finally be clarified!
     */
    public eu.fox7.bonxai.bonxai.Element convertElement(eu.fox7.bonxai.xsd.Element xsdElement) {
        notifyListeners(xsdElement);

        eu.fox7.bonxai.bonxai.Element bonxaiElement = new eu.fox7.bonxai.bonxai.Element(
            xsdElement.getNamespace(),
            xsdElement.getLocalName(),
            ( xsdElement.getType() instanceof SimpleType
                ? new BonxaiType(xsdElement.getType().getNamespace(), xsdElement.getType().getLocalName())
                : null ),
            xsdElement.isNillable()
        );

        if (xsdElement.getFixed() != null) {
            bonxaiElement.setFixed(xsdElement.getFixed());
        }
        if (xsdElement.getDefault() != null) {
            bonxaiElement.setDefault(xsdElement.getDefault());
        }

        return bonxaiElement;
    }

    /**
     * Converts an ElementRef to an Element, since Bonxai does not know Element
     * references.
     */
    public eu.fox7.bonxai.bonxai.Element convertElementRef(ElementRef elementRef) {
        notifyListeners(elementRef);

        eu.fox7.bonxai.xsd.Element element =
            (eu.fox7.bonxai.xsd.Element) elementRef.getElement();
        if (element == null) {
            throw new RuntimeException("Element reference cannot be resolved. Target does not exist.");
        }
        return convertElement(element);
    }

    /**
     * Converts a GroupRef.
     *
     * Attention, this method receives the {@link SymbolTableRef} for the group
     * to reference from the {@link SymbolTable} given in the ctor. It is
     * neccessary that this is the SymbolTable used in the target Bonxai for
     * group management!
     */
    public GroupRef convertGroupRef(GroupRef groupRef) {
        notifyListeners(groupRef);

        eu.fox7.bonxai.xsd.Group group =
            (eu.fox7.bonxai.xsd.Group) groupRef.getGroup();
        if (group == null) {
            throw new RuntimeException("Group reference cannot be resolved. Target does not exist.");
        }

        return new GroupRef(elementGroupSymbolTable.getReference(group.getLocalName()));
    }

    /**
     * Converts the AnyPattern.
     */
    public AnyPattern convertAnyPattern(AnyPattern anyPattern) {
        notifyListeners(anyPattern);
        // Replace all occurrences of " " in the namespace attribute with ",".
        String namespaceList = "";
        if (anyPattern.getNamespace() == null) {
            namespaceList = "any";
        } else if (anyPattern.getNamespace().equals("##any")) {
            namespaceList = "any";
        } else if (anyPattern.getNamespace().equals("##other")) {
            namespaceList = "other";
        } else {
            String[] namespaceArray = anyPattern.getNamespace().split(" ");
            for (String namespace : namespaceArray) {
                if (namespace.equals("##local")) {
                    namespace = "local";
                } else if (namespace.equals("##targetNamespace")) {
                    namespace = "targetNamespace";
                } else {
                    namespaceList = "any";
                }
                if (!namespaceList.equals("any")) {
                    if (namespaceList.equals("")) {
                        namespaceList = namespace;
                    } else {
                        namespaceList = "," + namespace;
                    }
                }
            }
        }
        anyPattern = new AnyPattern(anyPattern.getProcessContentsInstruction(), namespaceList);
        return anyPattern;
    }

    /**
     * Notifies all attached listeners.
     */
    private void notifyListeners(Particle particle) {
        for (ParticleProcessListener listener : listeners) {
            listener.notify(particle);
        }
    }
}
