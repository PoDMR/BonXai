/* Generated By:JJTree: Do not edit this line. ASTFullAPatternHead.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */

package de.tudortmund.cs.bonxai.bonxai.parser;


public
class ASTFullAPatternHead extends SimpleNode {

  String name;

  public ASTFullAPatternHead(int id) {
    super(id);
  }

  public ASTFullAPatternHead(bonXaiTree p, int id) {
    super(p, id);
  }



    public void setName(String name){
    this.name = name;
  }


  public String getName(){
    return name;
  }

  /** Accept the visitor. **/
  public Object jjtAccept(bonXaiTreeVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=783b0263d4e80323af5ce66cd7bb27fe (do not edit this line) */
