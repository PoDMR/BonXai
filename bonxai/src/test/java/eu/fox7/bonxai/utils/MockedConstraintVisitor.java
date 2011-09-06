package eu.fox7.bonxai.utils;

import java.util.LinkedList;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.utils.ConstraintVisitor;
import eu.fox7.bonxai.xsd.*;

class MockedConstraintVisitor extends ConstraintVisitor {
    public LinkedList<Constraint> visitedConstraints = new LinkedList<Constraint>();

    protected void visitConstraint( Constraint constraint ) {
        this.visitedConstraints.add( constraint );
    }
}
