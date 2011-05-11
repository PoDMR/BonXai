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

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;

/**
 * PreProcessorVisitor flattening all kinds of ComplexType inheritance
 *
 * This PreProcessor will flatten all ComplexType inheritance definitions, by
 * inlining the inherited information into the type itself.
 *
 * After this processor has been run no ComplexType inheritance will exist in
 * the XSDSchema anymore.
 */
public class InheritancePreProcessorVisitor implements PreProcessorVisitor {

    /**
     * XSDSchema currently beeing processed by the visitor
     */
    private XSDSchema currentSchema = null;

    /**
     * Called whenever the visiting of new schema starts.
     *
     * To be able to properly manipulate the schema structure during complex
     * type visiting, the schema reference is stored for each processed schema.
     */
    public void visitSchema(XSDSchema schema) {
        this.currentSchema = schema;
    }

    /**
     * Visit every ComplexType in the XSDSchema and flatten its inheritance
     *
     * Every ComplexType is visited. If an inheritance is encountered the found
     * base type is used to flatten the visited type. This flattening is called
     * recursively to ensure all needed information is available for flattening
     * in deep inheritance chains.
     */
    public void visitComplexType(ComplexType type) {
        this.flattenInheritance(type);
    }

    /**
     * Flatten the inheritance of a given type.
     *
     * SimpleTypes will be fully ignored, while ComplexTypes will be flattened.
     */
    private void flattenInheritance(Type type) {
        if (type instanceof ComplexType) {
            flattenInheritance((ComplexType) type);
        }
    }

    /**
     * Flatten the inheritance of a specified ComplexType
     *
     * The ComplexType may be empty in which case no flattening is done.
     *
     * The Content may define no inheritance in which case no flattening is
     * done either.
     */
    private void flattenInheritance(ComplexType type) {
        if (type.getContent() == null) {
            // "Empty" ComplexTypes are possible. We do not need to flatten them.
            // They are just skipped.
            return;
        }

        if (!this.hasInheritance(type.getContent())) {
            // If we do not have any defined inheritance in this
            // ComplexContent, there is no need to flatten the inheritance
            // structure.
            return;
        }

        Content content = type.getContent();

        // Decide which flattening method to call
        if (isRestriction(content)) {
            flattenRestriction(content, type);
        } else if (isExtension(content)) {
            flattenExtension(content, type);
        }
    }

    /**
     * Flatten a restriction of a Simple- or ComplexContentType
     */
    private void flattenRestriction(Content content, ComplexType type) {
        if (content instanceof ComplexContentType) {
            flattenRestriction((ComplexContentType) content, type);
        } else if (content instanceof SimpleContentType) {
            flattenRestriction((SimpleContentType) content, type);
        }
    }

    /**
     * Flatten the restriction inheritance of a ComplexContent object.
     */
    private void flattenRestriction(ComplexContentType content, ComplexType type) {
        // ComplexContent restriction simply eliminates all elements and
        // attributes not redefined in the restricting type. Therefore the
        // inheritance is resolved recursively and than simply removed, as all
        // restricted elements/attributes have to be redefined in the current
        // type.
        flattenInheritance(content.getInheritance().getBase());

        if (!content.getInheritance().getAttributes().isEmpty()) {
            for (AttributeParticle attributeParticle : content.getInheritance().getAttributes()) {
                type.addAttribute(attributeParticle);
            }
        }

        content.setInheritance(null);
    }

    /**
     * Flatten the restriction inheritance of a SimpleContent object.
     */
    private void flattenRestriction(SimpleContentType content, ComplexType type) {
        // SimpleContentTypes may only inherit from SimpleTypes. Therefore
        // currently nothing needs to be done here, as all possible attributes
        // are already defined in the ComplexType itself.
        //
        // @TODO: I wasn't able to find a specific answer to the question if
        // inheritance of SimpleContent is only possible from SimpleTypes.  I
        // wasn't able to find a proper definition on how to handle the merging
        // if it is allowed to be something else. Because of this I assume it
        // is not allowed to do so for the moment.
    }

    /**
     * Flatten a extension of a Simple- or ComplexContentType
     */
    private void flattenExtension(Content content, ComplexType type) {


        if (content instanceof ComplexContentType) {
            flattenExtension((ComplexContentType) content, type);
        } else if (content instanceof SimpleContentType) {
            flattenExtension((SimpleContentType) content, type);
        }
    }

    /**
     * Flatten the extension inheritance of a ComplexContent object.
     */
    private void flattenExtension(ComplexContentType content, ComplexType type) {
        // Flattening an extension types implies the inlining of all defined
        // parent elements/attributes as well as keeping the extended (newly
        // defined) ones. This has to be done recursively to ensure full
        // inheritance propagation.

        flattenInheritance(content.getInheritance().getBase());

        // If the inheritance base is a SimpleType no merging needs to be done
        // I am not even sure this can happen, but the XSDSchema documentation
        // isn't that clear about this as always.
        if (content.getInheritance().getBase() instanceof SimpleType) {
            return;
        }

        ComplexType baseType = (ComplexType) content.getInheritance().getBase();

        // Inline attributes.
        // Redefinition is not allowed and therefore isn't checked here
        for (AttributeParticle attributeParticle : baseType.getAttributes()) {
            type.addAttribute(attributeParticle);
        }

        if (!content.getInheritance().getAttributes().isEmpty()) {
            for (AttributeParticle attributeParticle : content.getInheritance().getAttributes()) {
                if (!type.getAttributes().contains(attributeParticle)) {
                    type.addAttribute(attributeParticle);
                }
            }
        }

        // Particles are inlined using a seqential order. Old ones first, new
        // ones last.
        SequencePattern sequence = new SequencePattern();
        Particle baseParticle = null;
        if (baseType.getContent() != null) {
            // The Content does only have particles if it is a
            // ComplexContentType
            if (baseType.getContent() instanceof ComplexContentType) {
                baseParticle = ((ComplexContentType) baseType.getContent()).getParticle();
            }
        }

        if (baseParticle != null && baseParticle instanceof SequencePattern) {
            SequencePattern baseSequencePattern = (SequencePattern) baseParticle;
            for (Particle currentBaseParticle : baseSequencePattern.getParticles()) {
                sequence.addParticle(currentBaseParticle);
            }
        } else if (baseParticle != null) {
            sequence.addParticle(baseParticle);
        }

        if (content.getParticle() != null && content.getParticle() instanceof SequencePattern) {
            SequencePattern baseSequencePattern = (SequencePattern) content.getParticle();
            for (Particle currentBaseParticle : baseSequencePattern.getParticles()) {
                sequence.addParticle(currentBaseParticle);
            }
        } else if (content.getParticle() != null) {
            sequence.addParticle(content.getParticle());
        }

        // Set the merged sequence as new particle
        content.setParticle(sequence);

        // Inheritance is fully inlined
        content.setInheritance(null);
    }

    /**
     * Flatten the extension inheritance of a SimpleContent object.
     */
    private void flattenExtension(SimpleContentType content, ComplexType type) {
        // Couldn't find information about this in the XSDSchema documentation
        // see comments in flattenRestriction for SimpleContent for details.
    }

    /**
     * Check if a given Simple- or ComplexContentType defines an inheritance
     */
    private boolean hasInheritance(Content content) {
        if (content instanceof ComplexContentType) {
            return hasInheritance((ComplexContentType) content);


        } else if (content instanceof SimpleContentType) {
            return hasInheritance((SimpleContentType) content);


        }
        return false;


    }

    /**
     * Check if a given ComplexContentType defines an inheritance
     */
    private boolean hasInheritance(ComplexContentType content) {
        return (content.getInheritance() != null);


    }

    /**
     * Check if a given SimpleContentType defines an inheritance
     */
    private boolean hasInheritance(SimpleContentType content) {
        return (content.getInheritance() != null);


    }

    /**
     * Check if the inheritance of a Complex- or SimpleContentType is a
     * restriction
     */
    private boolean isRestriction(Content content) {
        return ((content instanceof ComplexContentType)
                ? (isRestriction((ComplexContentType) content))
                : ((content instanceof SimpleContentType)
                ? (isRestriction((SimpleContentType) content))
                : false));


    }

    /**
     * Check if the inheritance of a ComplexContentType is defined as
     * Restriction
     */
    private boolean isRestriction(ComplexContentType content) {
        return (content.getInheritance() != null
                && content.getInheritance() instanceof ComplexContentRestriction);


    }

    /**
     * Check if the inheritance of a SimpleContentType is defined as
     * Restriction
     */
    private boolean isRestriction(SimpleContentType content) {
        return (content.getInheritance() != null
                && content.getInheritance() instanceof SimpleContentRestriction);


    }

    /**
     * Check if the inheritance of a Complex- or SimpleContentType is an
     * extension
     */
    private boolean isExtension(Content content) {
        return ((content instanceof ComplexContentType)
                ? (isExtension((ComplexContentType) content))
                : ((content instanceof SimpleContentType)
                ? (isExtension((SimpleContentType) content))
                : false));


    }

    /**
     * Check if the inheritance of a ComplexContentType is defined as Extension
     */
    private boolean isExtension(ComplexContentType content) {
        return (content.getInheritance() != null
                && content.getInheritance() instanceof ComplexContentExtension);


    }

    /**
     * Check if the inheritance of a SimpleContentType is defined as Extension
     */
    private boolean isExtension(SimpleContentType content) {
        return (content.getInheritance() != null
                && content.getInheritance() instanceof SimpleContentExtension);

    }
}
