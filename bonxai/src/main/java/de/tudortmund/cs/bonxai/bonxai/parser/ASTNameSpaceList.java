/* Generated By:JJTree: Do not edit this line. ASTNameSpaceList.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */

package de.tudortmund.cs.bonxai.bonxai.parser;

public
class ASTNameSpaceList extends SimpleNode {
  public ASTNameSpaceList(int id) {
    super(id);
  }

  public ASTNameSpaceList(bonXaiTree p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(bonXaiTreeVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=aeefd4b4af05bf4353bf761808b65d40 (do not edit this line) */
