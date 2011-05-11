package de.tudortmund.cs.bonxai.common;

import org.junit.*;

public class AnyAttributeTest extends junit.framework.TestCase {

    @Test
    public void testCreateAnyAttributeBothValues() {
        AnyAttribute anyAttribute = new AnyAttribute(ProcessContentsInstruction.Lax, "someNamespace");
        assertEquals(anyAttribute.processContentsInstruction, ProcessContentsInstruction.Lax);
        assertEquals(anyAttribute.namespace, "someNamespace");
    }

    @Test
    public void testCreateAnyAttributeNamespaceOnly() {
        AnyAttribute anyAttribute = new AnyAttribute("someNamespace");
        assertEquals(anyAttribute.processContentsInstruction, ProcessContentsInstruction.Strict);
        assertEquals(anyAttribute.namespace, "someNamespace");
    }

    @Test
    public void testCreateAnyAttributeProcessingInstructionOnly() {
        AnyAttribute anyAttribute = new AnyAttribute(ProcessContentsInstruction.Lax);
        assertEquals(anyAttribute.processContentsInstruction, ProcessContentsInstruction.Lax);
        assertEquals(anyAttribute.namespace, "##any");
    }

    @Test
    public void testCreateAnyAttributeProcessingNoValues() {
        AnyAttribute anyAttribute = new AnyAttribute();
        assertEquals(anyAttribute.processContentsInstruction, ProcessContentsInstruction.Strict);
        assertEquals(anyAttribute.namespace, "##any");
    }

    @Test
    public void testGetNamespace() {
        AnyAttribute anyAttribute = new AnyAttribute(ProcessContentsInstruction.Lax, "someNamespace");
        assertEquals(anyAttribute.getNamespace(), "someNamespace");
    }

    @Test
    public void testGetProcessContentsInstruction() {
        AnyAttribute anyAttribute = new AnyAttribute(ProcessContentsInstruction.Lax, "someNamespace");
        assertEquals(anyAttribute.getProcessContentsInstruction(), ProcessContentsInstruction.Lax);
    }

}
