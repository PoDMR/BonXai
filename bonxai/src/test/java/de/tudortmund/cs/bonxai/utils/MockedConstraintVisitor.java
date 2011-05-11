package de.tudortmund.cs.bonxai.utils;

import java.util.LinkedList;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;

class MockedConstraintVisitor extends ConstraintVisitor {
    public LinkedList<Constraint> visitedConstraints = new LinkedList<Constraint>();

    protected void visitConstraint( Constraint constraint ) {
        this.visitedConstraints.add( constraint );
    }
}
