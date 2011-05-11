/* Generated By:JJTree: Do not edit this line. ASTBonxaiType.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */

package de.tudortmund.cs.bonxai.bonxai.parser;

public
class ASTBonxaiType extends SimpleNode {

  String name;
  String type;
  Boolean missing;
  Boolean fixed;
  Boolean def;

  public ASTBonxaiType(int id) {
    super(id);
  }

  public ASTBonxaiType(bonXaiTree p, int id) {
    super(p, id);
  }


    public void setName(String name){
    this.name = name;
  }


  public String getName(){
    return name;
  }


    public void setType(String type){
    this.type = type;
  }


  public String getType(){
    return type;
  }


    public void setMissing(Boolean missing) {
    this.missing = missing;
  }


  public Boolean getMissing(){
    return missing;
  }

    public void setFixed(Boolean fixed) {
    this.fixed = fixed;
  }


  public Boolean getFixed(){
    return fixed;
  }


    public void setDefault(Boolean def) {
    this.def = def;
  }


  public Boolean getDefault(){
    return def;
  }




  /** Accept the visitor. **/
  public Object jjtAccept(bonXaiTreeVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e51438987e4c67f28f93da0376b9d427 (do not edit this line) */
