/* Generated By:JJTree: Do not edit this line. ASTSimpleAPatternOr.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */

package de.tudortmund.cs.bonxai.bonxai.parser;


public
class ASTSimpleAPatternOr extends SimpleNode {
  public ASTSimpleAPatternOr(int id) {
    super(id);
  }

  public ASTSimpleAPatternOr(bonXaiTree p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(bonXaiTreeVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=170f6276229e6cc44848acda4bad2401 (do not edit this line) */
