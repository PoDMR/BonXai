package eu.fox7.bonxai.bonxai.parser;

/* Generated By:JJTree: Do not edit this line. ASTFullAPattern.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTFullAPattern extends SimpleNode {

  String operator = "";
  boolean separator;
  boolean sequence;

  public ASTFullAPattern(int id) {
    super(id);
  }

  public ASTFullAPattern(bonXaiTree p, int id) {
    super(p, id);
  }



    public void setSeparator(boolean separator){
    this.separator = separator;
  }


  public Boolean getSeparator(){
    return separator;
  }




    public void setSequence(Boolean sequence){
    this.sequence = sequence;
  }


  public Boolean getSequence(){
    return sequence;
  }



    public void setOperator(String operator){
    this.operator = operator;
  }


  public String getOperator(){
    return operator;
  }



  /** Accept the visitor. **/
  public Object jjtAccept(bonXaiTreeVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=6a0fc2c927dc822e38e1705ea7f4e461 (do not edit this line) */