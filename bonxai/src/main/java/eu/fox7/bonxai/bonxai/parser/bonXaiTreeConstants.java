/* Generated By:JJTree&JavaCC: Do not edit this line. bonXaiTreeConstants.java */
package eu.fox7.bonxai.bonxai.parser;

public interface bonXaiTreeConstants {

  int EOF = 0;
  int CONSTANT = 5;
  int OPENING_ROUND_BRACKET = 6;
  int CLOSING_ROUND_BRACKET = 7;
  int OPENING_SQUARED_BRACKET = 8;
  int CLOSING_SQUARED_BRACKET = 9;
  int OPENING_CURLY_BRACKET = 10;
  int CLOSING_CURLY_BRACKET = 11;
  int EQUALS = 12;
  int COMMA = 13;
  int AT = 14;
  int OR = 15;
  int ANDLITERAL = 16;
  int SLASH = 17;
  int DOUBLESLASH = 18;
  int BACKSLASH = 19;
  int STAR = 20;
  int PLUS = 21;
  int QUESTION_MARK = 22;
  int QUOTE = 23;
  int UNDERSCORE = 24;
  int COLON = 25;
  int DBCOLON = 26;
  int POINT = 27;
  int DBPOINT = 28;
  int MINUS = 29;
  int ANNOTATION_VALUE = 30;
  int ROOTS = 31;
  int GRAMMAR = 32;
  int DEFAULT_NAMESPACE = 33;
  int NAMESPACE = 34;
  int TYPE = 35;
  int DEFAULTT = 36;
  int DATATYPES = 37;
  int IMPORT = 38;
  int CONSTRAINTS = 39;
  int UNIQUE = 40;
  int KEY = 41;
  int KEYREF = 42;
  int GROUPS = 43;
  int GROUP = 44;
  int ATTRIBUTEGROUP = 45;
  int ATTRIBUTE = 46;
  int MIXED = 47;
  int EMPTY = 48;
  int MISSING = 49;
  int MISSING_OR = 50;
  int ELEMENT = 51;
  int ELEMENT_STAR = 52;
  int STRICT = 53;
  int LAX = 54;
  int SKIPP = 55;
  int FIXED = 56;
  int RETURN = 57;
  int FOR = 58;
  int IN = 59;
  int DOLLAR = 60;
  int SOME = 61;
  int EVERY = 62;
  int SATISFIES = 63;
  int IF = 64;
  int THEN = 65;
  int ELSE = 66;
  int AND = 67;
  int TO = 68;
  int DIV = 69;
  int IDIV = 70;
  int MOD = 71;
  int UNION = 72;
  int INTERSECT = 73;
  int EXCEPT = 74;
  int INSTANCE = 75;
  int OF = 76;
  int TREAT = 77;
  int AS = 78;
  int CASTABLE = 79;
  int CAST = 80;
  int NOTEQUAL = 81;
  int LESSTHAN = 82;
  int LESSEQUAL = 83;
  int GREATERTHAN = 84;
  int GREATEREQUAL = 85;
  int EQ = 86;
  int NE = 87;
  int LT = 88;
  int LE = 89;
  int GT = 90;
  int GE = 91;
  int IS = 92;
  int DBLEFT = 93;
  int DBRIGHT = 94;
  int CHILD = 95;
  int DESCENDANT = 96;
  int SELF = 97;
  int DESCENDANTORSELF = 98;
  int FOLLOWINGSIBLING = 99;
  int FOLLOWING = 100;
  int PARENT = 101;
  int ANCESTOR = 102;
  int PRECEDINGSIBLING = 103;
  int PRECEDING = 104;
  int ANCESTORORSELF = 105;
  int EMPTYSEQUENCE = 106;
  int ITEM = 107;
  int NODE = 108;
  int DOCUMENTNODE = 109;
  int TEXT = 110;
  int COMMENT = 111;
  int PROCESSINGINSTRUCTION = 112;
  int SCHEMAATTRIBUTE = 113;
  int SCHEMAELEMENT = 114;
  int DIGIT = 115;
  int BASECHAR = 116;
  int IDEOGRAPHIC = 117;
  int COMBINING_CHAR = 118;
  int EXTENDER = 119;
  int HTTP = 120;
  int FTP = 121;
  int URI_LABEL = 122;
  int URI_POINT = 123;
  int URI_SLASH = 124;
  int URI_CONSTANT = 125;
  int URI_WS = 126;
  int URI_ROW = 127;
  int URI_LINE = 128;
  int VAR_LABEL = 129;
  int VAR_CONSTANT = 130;
  int VAR_OPENING_ROUND_BRACKET = 131;
  int VAR_AT = 132;
  int VAR_COLON = 133;
  int VAROR_LABEL = 138;
  int VAROR_CONSTANT = 139;
  int VAROR_OR = 140;
  int VAROR_COMMA = 141;
  int VAR_CLOSING_ROUND_BRACKET = 142;
  int VAR_OPENING_CURLY_BRACKET = 143;
  int VAR_CLOSING_CURLY_BRACKET = 144;
  int VAROR_AT = 145;
  int VAROR_COLON = 146;
  int QUOT_LABEL = 151;
  int QUOT_CONSTANT = 152;
  int QOUT_WS = 153;
  int QUOT_QUOTE = 154;
  int LIST_LABEL = 159;
  int START_LIST = 160;
  int LIST_COMMA = 161;
  int END_LIST = 162;

  int DEFAULT = 0;
  int URI = 1;
  int VARIABLE = 2;
  int VARIABLE_OR = 3;
  int QUOTATION = 4;
  int NAMESPACE_LIST = 5;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<CONSTANT>",
    "\"(\"",
    "\")\"",
    "\"[\"",
    "\"]\"",
    "\"{\"",
    "\"}\"",
    "\"=\"",
    "\",\"",
    "\"@\"",
    "\"|\"",
    "\"&\"",
    "\"/\"",
    "\"//\"",
    "\"\\\\\"",
    "\"*\"",
    "\"+\"",
    "\"?\"",
    "\"\\\"\"",
    "\"_\"",
    "\":\"",
    "\"::\"",
    "\".\"",
    "\"..\"",
    "\"-\"",
    "\"annotaion\"",
    "\"roots\"",
    "\"grammar\"",
    "\"default namespace\"",
    "\"namespace\"",
    "\"type\"",
    "\"default\"",
    "\"datatypes \"",
    "\"import\"",
    "\"constraints\"",
    "\"unique\"",
    "\"key\"",
    "\"keyref \"",
    "\"groups\"",
    "\"group\"",
    "\"attribute-group\"",
    "\"attribute\"",
    "\"mixed\"",
    "\"empty\"",
    "\"missing\"",
    "\"missing |\"",
    "\"element\"",
    "\"element *\"",
    "\"strict\"",
    "\"lax\"",
    "\"skip\"",
    "\"fixed\"",
    "\"return\"",
    "\"for\"",
    "\"in\"",
    "\"$\"",
    "\"some\"",
    "\"every\"",
    "\"satisfies\"",
    "\"if\"",
    "\"then\"",
    "\"else\"",
    "\"and\"",
    "\"to\"",
    "\"div\"",
    "\"idiv\"",
    "\"mod\"",
    "\"union\"",
    "\"intersect\"",
    "\"except\"",
    "\"instance\"",
    "\"of\"",
    "\"treat\"",
    "\"as\"",
    "\"castable\"",
    "\"cast\"",
    "\"!=\"",
    "\"<\"",
    "\"<=\"",
    "\">\"",
    "\">=\"",
    "\"eq\"",
    "\"ne\"",
    "\"lt\"",
    "\"le\"",
    "\"gt\"",
    "\"ge\"",
    "\"is\"",
    "\"<<\"",
    "\">>\"",
    "\"child\"",
    "\"descendant\"",
    "\"self\"",
    "\"descendant-or-self\"",
    "\"following-sibling\"",
    "\"following\"",
    "\"parent\"",
    "\"ancestor\"",
    "\"preceding-sibling\"",
    "\"preceding\"",
    "\"ancestor-or-self\"",
    "\"empty-sequence\"",
    "\"item\"",
    "\"node\"",
    "\"document-node\"",
    "\"text\"",
    "\"comment\"",
    "\"processing-instruction\"",
    "\"schema-attribute\"",
    "\"schema-element\"",
    "<DIGIT>",
    "<BASECHAR>",
    "<IDEOGRAPHIC>",
    "<COMBINING_CHAR>",
    "<EXTENDER>",
    "\"http://\"",
    "\"ftp://\"",
    "<URI_LABEL>",
    "\".\"",
    "\"/\"",
    "<URI_CONSTANT>",
    "\" \"",
    "\"\\r\"",
    "\"\\n\"",
    "<VAR_LABEL>",
    "<VAR_CONSTANT>",
    "\"(\"",
    "\"@\"",
    "\":\"",
    "\" \"",
    "\"\\r\"",
    "\"\\n\"",
    "\"\\t\"",
    "<VAROR_LABEL>",
    "<VAROR_CONSTANT>",
    "\"|\"",
    "\",\"",
    "\")\"",
    "\"{\"",
    "\"}\"",
    "\"@\"",
    "\":\"",
    "\" \"",
    "\"\\r\"",
    "\"\\n\"",
    "\"\\t\"",
    "<QUOT_LABEL>",
    "<QUOT_CONSTANT>",
    "\" \"",
    "\"\\\"\"",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<LIST_LABEL>",
    "\"{\"",
    "\",\"",
    "\"}\"",
  };

}