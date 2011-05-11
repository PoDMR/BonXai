package de.tudortmund.cs.bonxai.utils;

import org.junit.Test;
import java.util.Iterator;

import de.tudortmund.cs.bonxai.common.AllPattern;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.ElementRef;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.utils.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.Group;

public class ContentModelValidationTest extends junit.framework.TestCase {

    // (b(c|d))
    @Test
    public void testContenModelValidation1() {
        SequencePattern seq = new SequencePattern();
        Element b = new Element("{www.example.com/foo}b");
        seq.addParticle(b);
        ChoicePattern choice = new ChoicePattern();
        Element c = new Element("{www.example.com/foo}c");
        Element d = new Element("{www.example.com/foo}d");
        choice.addParticle(c);
        choice.addParticle(d);
        seq.addParticle(choice);
        try {
            ContentModelValidator cmv = new ContentModelValidator(seq);
            Iterator<ContentModelValidator.Transition> it = cmv.newTransitions.iterator();
            ContentModelValidator.Transition t = it.next();
            assertEquals(t.from, 7);
            assertEquals(t.label, "{www.example.com/foo}b_5");
            assertEquals(t.to, 8);
            t = it.next();
            assertEquals(t.from, 8);
            assertEquals(t.label, "{www.example.com/foo}c_3");
            assertEquals(t.to, 9);
            t = it.next();
            assertEquals(t.from, 8);
            assertEquals(t.label, "{www.example.com/foo}d_4");
            assertEquals(t.to, 9);
            cmv.checkDeterminism();
        } catch (InvalidContentModelException e) {
            fail(e.getMessage());
        }
    }

    // ((bc)|(bd))
    @Test(expected=UPAViolationException.class)
    public void testContenModelValidation2() {
        ChoicePattern choice = new ChoicePattern();
        SequencePattern seq = new SequencePattern();
        Element b = new Element("{www.example.com/foo}b");
        Element c = new Element("{www.example.com/foo}c");
        seq.addParticle(b);
        seq.addParticle(c);
        choice.addParticle(seq);
        SequencePattern seq2 = new SequencePattern();
        Element d = new Element("{www.example.com/foo}d");
        seq2.addParticle(b);
        seq2.addParticle(d);
        choice.addParticle(seq2);
        try {
            ContentModelValidator cmv = new ContentModelValidator(choice);
            Iterator<ContentModelValidator.Transition> it = cmv.newTransitions.iterator();
            ContentModelValidator.Transition t = it.next();
            assertEquals(t.from, 9);
            assertEquals(t.label, "{www.example.com/foo}b_4");
            assertEquals(t.to, 10);
            t = it.next();
            assertEquals(t.from, 10);
            assertEquals(t.label, "{www.example.com/foo}c_3");
            assertEquals(t.to, 11);
            t = it.next();
            assertEquals(t.from, 9);
            assertEquals(t.label, "{www.example.com/foo}b_7");
            assertEquals(t.to, 12);
            t = it.next();
            assertEquals(t.from, 12);
            assertEquals(t.label, "{www.example.com/foo}d_6");
            assertEquals(t.to, 11);
            cmv.checkDeterminism();
        } catch (InvalidContentModelException e) {
            assertEquals(e.getMessage(), "Element \"www.example.com/foo\":b and element \"www.example.com/foo\":b violate the Unique Particle Attribution constraint. During validation against this schema, ambiguity would be created for those two particles.");
        }
    }

    // (a{0,2}a{0,2})
    @Test(expected=UPAViolationException.class)
    public void testContenModelValidation3() {
        SequencePattern seq = new SequencePattern();
        Element el = new Element("{www.example.com/foo}a");
        SymbolTableRef<Element> ref = new SymbolTableRef<Element>("{www.example.com/foo}a", el);
        ElementRef elr = new ElementRef(ref);
        CountingPattern cp = new CountingPattern(0, 2);
        cp.addParticle(elr);
        seq.addParticle(cp);
        seq.addParticle(cp);
        try {
            ContentModelValidator cmv = new ContentModelValidator(seq);
            cmv.checkDeterminism();
        } catch (InvalidContentModelException e) {
            assertEquals(e.getMessage(), "Element \"www.example.com/foo\":a and element \"www.example.com/foo\":a violate the Unique Particle Attribution constraint. During validation against this schema, ambiguity would be created for those two particles.");
        }
    }

    // (a{1,3})
    @Test
    public void testContenModelValidation4() {
        Element el = new Element("{www.example.com/foo}a");
        CountingPattern cp = new CountingPattern(1, 3);
        cp.addParticle(el);
        try {
            ContentModelValidator cmv = new ContentModelValidator(cp);
            cmv.checkDeterminism();
        } catch (InvalidContentModelException e) {
            fail(e.getMessage());
        }
    }

    // (a{1,3}a{1,3})
    @Test(expected=UPAViolationException.class)
    public void testContenModelValidation5() {
        Element el = new Element("{www.example.com/foo}a");
        CountingPattern cp = new CountingPattern(1, 3);
        SequencePattern seq = new SequencePattern();
        cp.addParticle(el);
        seq.addParticle(cp);
        seq.addParticle(cp);
        try {
            ContentModelValidator cmv = new ContentModelValidator(seq);
            cmv.checkDeterminism();
        } catch (InvalidContentModelException e) {
            assertEquals(e.getMessage(), "Element \"www.example.com/foo\":a and element \"www.example.com/foo\":a violate the Unique Particle Attribution constraint. During validation against this schema, ambiguity would be created for those two particles.");
        }
    }

    // ({www.example.com/foo}a | ANY with namespace www.example.com/foo)
    @Test(expected=UPAViolationException.class)
    public void testContenModelValidation6() {
        Element el = new Element("{www.example.com/foo}a");
        ChoicePattern choice = new ChoicePattern();
        choice.addParticle(el);
        AnyPattern any = new AnyPattern(ProcessContentsInstruction.Lax, "www.example.com/foo");
        choice.addParticle(any);
        try {
            ContentModelValidator cmv = new ContentModelValidator(choice);
            cmv.checkDeterminism();
        } catch (InvalidContentModelException e) {
            assertEquals(e.getMessage(), "Element \"www.example.com/foo\":a and wildcard for namespace \"www.example.com/foo\" violate the Unique Particle Attribution constraint. During validation against this schema, ambiguity would be created for those two particles.");
        }
    }

    // (a{0,null}a{0,null})
    @Test(expected=UPAViolationException.class)
    public void testContenModelValidation7() {
        SequencePattern seq = new SequencePattern();
        Element el = new Element("{www.example.com/foo}a");
        SymbolTableRef<Element> ref = new SymbolTableRef<Element>("{www.example.com/foo}a", el);
        ElementRef elr = new ElementRef(ref);
        CountingPattern cp = new CountingPattern(0, null);
        cp.addParticle(elr);
        seq.addParticle(cp);
        seq.addParticle(cp);
        try {
            ContentModelValidator cmv = new ContentModelValidator(seq);
            cmv.checkDeterminism();
        } catch (InvalidContentModelException e) {
            assertEquals(e.getMessage(), "Element \"www.example.com/foo\":a and element \"www.example.com/foo\":a violate the Unique Particle Attribution constraint. During validation against this schema, ambiguity would be created for those two particles.");
        }
    }

    // Recursive group defintion
    @Test(expected=RecursiveContentModelException.class)
    public void testContenModelValidation8() {
        SymbolTableRef<Group> ref = new SymbolTableRef<Group>("{}someGroup");
        GroupRef gref = new GroupRef(ref);
        SequencePattern seq = new SequencePattern();
        seq.addParticle(gref);
        Group group = new Group("{}someGroup", seq);
        ref.setReference(group);
        try {
            ContentModelValidator cmv = new ContentModelValidator(seq);
            cmv.checkDeterminism();
        } catch (InvalidContentModelException e) {
            assertEquals(e.getMessage(), "Group \"\":someGroup contains a reference to itself.");
        }
    }

    // ((bc)&(bd))
    @Test(expected=UPAViolationException.class)
    public void testContenModelValidation9() {
        AllPattern all = new AllPattern();
        SequencePattern seq = new SequencePattern();
        Element b = new Element("{}b");
        Element c = new Element("{}c");
        seq.addParticle(b);
        seq.addParticle(c);
        all.addParticle(seq);
        SequencePattern seq2 = new SequencePattern();
        Element d = new Element("{}d");
        seq2.addParticle(b);
        seq2.addParticle(d);
        all.addParticle(seq2);
        try {
            ContentModelValidator cmv = new ContentModelValidator(all);
            cmv.checkDeterminism();
        } catch (InvalidContentModelException e) {
            assertEquals(e.getMessage(), "Element \"\":b and element \"\":b violate the Unique Particle Attribution constraint. During validation against this schema, ambiguity would be created for those two particles.");
        }
    }

    // (someGroup, someGroup)
    @Test
    public void testContenModelValidation10() {
        Group gr = new Group("{}someGroup", null);
        SymbolTableRef<Group> ref = new SymbolTableRef<Group>("{}someGroup");
        ref.setReference(gr);
        GroupRef gref = new GroupRef(ref);
        SequencePattern seq = new SequencePattern();
        seq.addParticle(gref);
        seq.addParticle(gref);

        try {
            ContentModelValidator cmv = new ContentModelValidator(seq);
            cmv.checkDeterminism();
        } catch (InvalidContentModelException e) {
            fail(e.getMessage());
        }

    }

}
