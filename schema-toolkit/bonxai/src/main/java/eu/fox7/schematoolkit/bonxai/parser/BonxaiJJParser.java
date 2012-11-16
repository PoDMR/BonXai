/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.bonxai.parser;

import java.util.*;
import eu.fox7.schematoolkit.bonxai.om.*;
import eu.fox7.schematoolkit.bonxai.om.Element;
import eu.fox7.schematoolkit.bonxai.om.ElementRef;
import eu.fox7.schematoolkit.bonxai.om.Annotation;
import eu.fox7.schematoolkit.common.*;

public class BonxaiJJParser extends BonxaiJJParserBase implements BonxaiJJParserConstants {
  private int identifierEndLine=0;
  private int identifierEndColumn=0;
  private int aPatternBeginLine=0;
  private int aPatternBeginColumn=0;
  private int aPatternEndLine=0;
  private int aPatternEndColumn=0;


  private void setState(int state) {
    if (state != token_source.curLexState) {
      Token root = new Token(), last=root;
      root.next = null;

      // First, we build a list of tokens to push back, in backwards order
      while (token.next != null) {
        Token t = token;
        // Find the token whose token.next is the last in the chain
        while (t.next != null && t.next.next != null)
          t = t.next;

        // put it at the end of the new chain
        last.next = t.next;
        last = t.next;

        // If there are special tokens, these go before the regular tokens,
        // so we want to push them back onto the input stream in the order
        // we find them along the specialToken chain.

        if (t.next.specialToken != null) {
          Token tt=t.next.specialToken;
          while (tt != null) {
            last.next = tt;
            last = tt;
            tt.next = null;
            tt = tt.specialToken;
          }
        }
        t.next = null;
      }

      while (root.next != null) {
        token_source.backup(root.next.image.length());
        root.next = root.next.next;
      }
      jj_ntk = -1;
      token_source.SwitchTo(state);
    }
  }

// Start symbol bonXai(); called by parser function
// Main entry point of the Bonxai grammar
//
// Bonxai              ::= Decl* GroupBlock? Block ConstraintBlock?
  final public Bonxai bonxai() throws ParseException {
    trace_call("bonxai");
    try {
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DEFAULT_NAMESPACE:
        case NAMESPACE:
          ;
          break;
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
        Decl();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case GROUPS:
        GroupBlock();
        break;
      default:
        jj_la1[1] = jj_gen;
        ;
      }
      Block();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CONSTRAINTS:
        ConstraintBlock();
        break;
      default:
        jj_la1[2] = jj_gen;
        ;
      }
      jj_consume_token(0);
          {if (true) return this.bonxai;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("bonxai");
    }
  }

// Decl                ::= "namespace" Identifier "=" NamespaceUriLiteral
//                      | "default" "namespace" NamespaceUriLiteral
//                      | "datatypes" Identifier "=" URL
//                      | "import" NamespaceUriLiteral ( "=" URL )?
  final public void Decl() throws ParseException {
    trace_call("Decl");
    try {
              String uri="" ; String ident=""; String url="";
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NAMESPACE:
        jj_consume_token(NAMESPACE);
        ident = Identifier();
        jj_consume_token(EQUALS);
        uri = NamespaceUriLiteral();
                                                                     this.bonxai.addNamespace(new IdentifiedNamespace(ident,uri));
        break;
      case DEFAULT_NAMESPACE:
        jj_consume_token(DEFAULT_NAMESPACE);
        uri = NamespaceUriLiteral();
                                                  this.bonxai.setDefaultNamespace(new DefaultNamespace(uri));
        break;
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    setState(DEFAULT);
    } finally {
      trace_return("Decl");
    }
  }

  final public String NamespaceUriLiteral() throws ParseException {
    trace_call("NamespaceUriLiteral");
    try {
                               Token t;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LABEL:
        t = jj_consume_token(LABEL);
        break;
      case URI:
        t = jj_consume_token(URI);
        break;
      case QUOTATION:
        t = jj_consume_token(QUOTATION);
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
          {if (true) return t.image;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("NamespaceUriLiteral");
    }
  }

// ConstraintBlock     ::= "constraints" "{" Constraint* "}"
  final public void ConstraintBlock() throws ParseException {
    trace_call("ConstraintBlock");
    try {
      jj_consume_token(CONSTRAINTS);
      jj_consume_token(OPENING_CURLY_BRACKET);
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case UNIQUE:
        case KEY:
        case KEYREF:
          ;
          break;
        default:
          jj_la1[5] = jj_gen;
          break label_2;
        }
        Constraint();
      }
      jj_consume_token(CLOSING_CURLY_BRACKET);
    } finally {
      trace_return("ConstraintBlock");
    }
  }

//Constraint          ::= UniqueConstraint | KeyConstraint | KeyRefConstraint
  final public void Constraint() throws ParseException {
    trace_call("Constraint");
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case UNIQUE:
        UniqueConstraint();
        break;
      case KEY:
        KeyConstraint();
        break;
      case KEYREF:
        KeyRefConstraint();
        break;
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
          setState(DEFAULT);
    } finally {
      trace_return("Constraint");
    }
  }

//UniqueConstraint    ::= "unique" APattern "=" "{" ConstraintSelector ConstraintFields "}"
  final public void UniqueConstraint() throws ParseException {
    trace_call("UniqueConstraint");
    try {
                          AncestorPattern aPattern, selector; List<AncestorPattern> fields;
      jj_consume_token(UNIQUE);
      aPattern = APattern();
      jj_consume_token(EQUALS);
      jj_consume_token(OPENING_CURLY_BRACKET);
      selector = APattern();
      fields = ConstraintFields();
      jj_consume_token(CLOSING_CURLY_BRACKET);
        bonxai.addConstraint(new UniqueConstraint(aPattern, selector, fields));
    } finally {
      trace_return("UniqueConstraint");
    }
  }

// KeyConstraint       ::= "key" Name APattern "{" ConstraintSelector ConstraintFields "}"
  final public void KeyConstraint() throws ParseException {
    trace_call("KeyConstraint");
    try {
                       QualifiedName name; AncestorPattern aPattern, selector; List<AncestorPattern> fields;
      jj_consume_token(KEY);
      name = Name();
      aPattern = APattern();
      jj_consume_token(EQUALS);
      jj_consume_token(OPENING_CURLY_BRACKET);
      selector = APattern();
      fields = ConstraintFields();
      jj_consume_token(CLOSING_CURLY_BRACKET);
        bonxai.addConstraint(new KeyConstraint(name, aPattern, selector, fields));
    } finally {
      trace_return("KeyConstraint");
    }
  }

// KeyRefConstraint    ::= "keyref" Name APattern "{" ConstraintSelector ConstraintFields "}"
  final public void KeyRefConstraint() throws ParseException {
    trace_call("KeyRefConstraint");
    try {
                          QualifiedName name; AncestorPattern aPattern, selector; List<AncestorPattern> fields;
      jj_consume_token(KEYREF);
      name = Name();
      aPattern = APattern();
      jj_consume_token(EQUALS);
      jj_consume_token(OPENING_CURLY_BRACKET);
      selector = APattern();
      fields = ConstraintFields();
      jj_consume_token(CLOSING_CURLY_BRACKET);
        bonxai.addConstraint(new KeyRefConstraint(name, aPattern, selector, fields));
    } finally {
      trace_return("KeyRefConstraint");
    }
  }

// ConstraintFields    ::= "{" XPath ( "," XPath )* "}"
  final public List<AncestorPattern> ConstraintFields() throws ParseException {
    trace_call("ConstraintFields");
    try {
                                           List<AncestorPattern> fields = new LinkedList<AncestorPattern>(); AncestorPattern field;
      jj_consume_token(OPENING_CURLY_BRACKET);
      field = APattern();
                                                    fields.add(field);
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[7] = jj_gen;
          break label_3;
        }
        jj_consume_token(COMMA);
        field = APattern();
                                                                                                        fields.add(field);
      }
      jj_consume_token(CLOSING_CURLY_BRACKET);
          {if (true) return fields;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("ConstraintFields");
    }
  }

// GroupBlock          ::= "groups" "{" GroupRule* "}"
  final public void GroupBlock() throws ParseException {
    trace_call("GroupBlock");
    try {
      jj_consume_token(GROUPS);
      jj_consume_token(OPENING_CURLY_BRACKET);
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case GROUP:
        case ATTRIBUTEGROUP:
          ;
          break;
        default:
          jj_la1[8] = jj_gen;
          break label_4;
        }
        GroupRule();
      }
      jj_consume_token(CLOSING_CURLY_BRACKET);
    } finally {
      trace_return("GroupBlock");
    }
  }

// GroupRule           ::= "group" Name "=" Regex
//                      | "attribute-group" Name "=" "{" AttributePattern "}"
  final public void GroupRule() throws ParseException {
    trace_call("GroupRule");
    try {
                    QualifiedName name; Particle particle; AttributePattern attributePattern;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case GROUP:
        jj_consume_token(GROUP);
        name = Name();
        jj_consume_token(EQUALS);
                                               setState(DEFAULT);
        jj_consume_token(OPENING_CURLY_BRACKET);
        particle = Regex();
        jj_consume_token(CLOSING_CURLY_BRACKET);
                                                                                                                                       this.bonxai.addGroup(new BonxaiGroup(name, particle));
        break;
      case ATTRIBUTEGROUP:
        jj_consume_token(ATTRIBUTEGROUP);
        name = Name();
        jj_consume_token(EQUALS);
                                                        setState(DEFAULT);
        jj_consume_token(OPENING_CURLY_BRACKET);
        attributePattern = AttributePattern();
        jj_consume_token(CLOSING_CURLY_BRACKET);
                                                                                                                                                                   this.bonxai.addGroup(new BonxaiAttributeGroup(name, attributePattern));
        break;
      default:
        jj_la1[9] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
          setState(DEFAULT);
    } finally {
      trace_return("GroupRule");
    }
  }

//Block               ::= "grammar" "{" Expr* "}"
  final public void Block() throws ParseException {
    trace_call("Block");
    try {
      jj_consume_token(GRAMMAR);
      jj_consume_token(OPENING_CURLY_BRACKET);
      RootElements();
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OPENING_ROUND_BRACKET:
        case AT:
        case DOUBLESLASH:
        case SLASH:
        case LABEL:
          ;
          break;
        default:
          jj_la1[10] = jj_gen;
          break label_5;
        }
        Expr();
      }
      jj_consume_token(CLOSING_CURLY_BRACKET);
          setState(DEFAULT);
    } finally {
      trace_return("Block");
    }
  }

// RootElements
  final public void RootElements() throws ParseException {
    trace_call("RootElements");
    try {
                      QualifiedName name;
      jj_consume_token(ROOTS);
      jj_consume_token(OPENING_CURLY_BRACKET);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LABEL:
        name = Name();
                                                        this.bonxai.addRootElementName(name);
        label_6:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case COMMA:
            ;
            break;
          default:
            jj_la1[11] = jj_gen;
            break label_6;
          }
          jj_consume_token(COMMA);
          name = Name();
                                                                                                                        this.bonxai.addRootElementName(name);
        }
        break;
      default:
        jj_la1[12] = jj_gen;
        ;
      }
      jj_consume_token(CLOSING_CURLY_BRACKET);
    } finally {
      trace_return("RootElements");
    }
  }

// Expr                ::= Annotations? APattern "=" CPattern
  final public void Expr() throws ParseException {
    trace_call("Expr");
    try {
              aPatternBeginLine=0; AncestorPattern aPattern; ChildPattern cPattern; List<Annotation> annotations=new LinkedList<Annotation>();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AT:
        annotations = Annotations();
        break;
      default:
        jj_la1[13] = jj_gen;
        ;
      }
      aPattern = APattern();
      jj_consume_token(EQUALS);
                                                                   setState(DEFAULT);
      cPattern = CPattern();
          this.bonxai.addExpression(new Expression(annotations, aPattern, cPattern, new BonxaiLocation(aPatternBeginLine,aPatternBeginColumn,aPatternEndLine,aPatternEndColumn))); setState(NAME);
    } finally {
      trace_return("Expr");
    }
  }

// Annotations         ::= ( Annotation )+
  final public List<Annotation> Annotations() throws ParseException {
    trace_call("Annotations");
    try {
                                  Annotation annotation; List<Annotation> annotations = new LinkedList<Annotation>();
      label_7:
      while (true) {
        annotation = Annotation();
                            annotations.add(annotation);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case AT:
          ;
          break;
        default:
          jj_la1[14] = jj_gen;
          break label_7;
        }
      }
          {if (true) return annotations;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Annotations");
    }
  }

// old: Annotation            ::= AnnotationName AnnotationValue
// new: Annotation            ::= "@" AnnotationName (" " AnnotationValue)
  final public Annotation Annotation() throws ParseException {
    trace_call("Annotation");
    try {
                          String name; String value;
      jj_consume_token(AT);
      name = AnnotationNameValue();
      jj_consume_token(EQUALS);
      value = AnnotationNameValue();
          {if (true) return new Annotation(name,value);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Annotation");
    }
  }

// AnnotationName      ::= "@" ["a"-"z", "A"-"Z"]+
  final public String AnnotationNameValue() throws ParseException {
    trace_call("AnnotationNameValue");
    try {
                                Token t; String name="";
      label_8:
      while (true) {
        t = jj_consume_token(LABEL);
                    name=name + t.image;
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LABEL:
          ;
          break;
        default:
          jj_la1[15] = jj_gen;
          break label_8;
        }
      }
          {if (true) return name;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("AnnotationNameValue");
    }
  }

// old:APattern            ::= SimpleAPattern FullAPattern? | FullAPattern
// new: APattern            ::= SimpleAPattern | FullAPattern
// ignoring simple aPatterns for now!
  final public AncestorPattern APattern() throws ParseException {
    trace_call("APattern");
    try {
                             AncestorPattern aPattern; QualifiedName name;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPENING_ROUND_BRACKET:
      case DOUBLESLASH:
      case SLASH:
        aPattern = FullAPattern();
        break;
      case LABEL:
        name = Name();
                         aPattern=new DoubleSlashPrefixedContainer(new AncestorPatternElement(name));
        break;
      default:
        jj_la1[16] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      {if (true) return aPattern;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("APattern");
    }
  }

//SimpleAPattern       ::= Name | "@"Name | "(" SimpleAPatternOr ")"
  final public AncestorPattern SimpleAPattern() throws ParseException {
    trace_call("SimpleAPattern");
    try {
                                   QualifiedName name; AncestorPattern a;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LABEL:
        name = Name();
                        a=new AncestorPatternElement(name);
        break;
      case AT:
        jj_consume_token(AT);
        name = Name();
                                     a=new AncestorPatternElement(name);
        break;
      case OPENING_ROUND_BRACKET:
        jj_consume_token(OPENING_ROUND_BRACKET);
        a = SimpleAPatternOr();
        jj_consume_token(CLOSING_ROUND_BRACKET);
        break;
      default:
        jj_la1[17] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      {if (true) return a;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("SimpleAPattern");
    }
  }

// old: SimpleAPatternOr     ::= SimpleAPattern ("|" SimpleAPattern)*
// new: SimpleAPatternOr     ::= SimpleAPattern ("|" SimpleAPattern)*
  final public OrExpression SimpleAPatternOr() throws ParseException {
    trace_call("SimpleAPatternOr");
    try {
                                  AncestorPattern a; OrExpression or = new OrExpression();
      a = SimpleAPattern();
                            or.addChild(a);
      label_9:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OR:
          ;
          break;
        default:
          jj_la1[18] = jj_gen;
          break label_9;
        }
        jj_consume_token(OR);
        a = SimpleAPattern();
                                                                       or.addChild(a);
      }
      {if (true) return or;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("SimpleAPatternOr");
    }
  }

// old: FullAPattern       ::=    PathSeparator SimpleAPattern (FullAPattern)? | "(" FullAPatternOr ")" Operator?
// new: FullAPattern       ::=    PathSeparator SimpleAPattern | "(" FullAPatternOr ")" Operator? | FullAPattern FullAPattern
// without left recursion  ::=    PathSeparator SimpleAPattern | "(" FullAPatternOr ")" Operator? (FullAPattern)?
// correct without left recursion ::=   ( PathSeparator SimpleAPattern | "(" FullAPatternOr ")" Operator? ) (FullAPattern)?
// new: FullAPattern 		::=		(FAPX)+
//		FAPX				::=		/SAP | //SAP | (FAPO)
//		SAP					::=		EP | (SAPO)
//		SAPO				::=		SAP (| SAP)*
//		FAPO				::=		FAP (| FAP)*
  final public AncestorPattern FullAPattern() throws ParseException {
    trace_call("FullAPattern");
    try {
                                  SequenceExpression seq = new SequenceExpression(); AncestorPattern aPattern;
      label_10:
      while (true) {
        aPattern = FullAPatternX();
                                       seq.addChild(aPattern);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OPENING_ROUND_BRACKET:
        case DOUBLESLASH:
        case SLASH:
          ;
          break;
        default:
          jj_la1[19] = jj_gen;
          break label_10;
        }
      }
                if (seq.getChildren().size()==1)
                        {if (true) return aPattern;}
                else
                        {if (true) return seq;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("FullAPattern");
    }
  }

  final public AncestorPattern FullAPatternX() throws ParseException {
    trace_call("FullAPatternX");
    try {
  boolean separator=false;
  boolean sequence=false;
  boolean or=false;
  AncestorPattern aPattern, aPattern1;
  Token t = null;
  Token s = null;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SLASH:
        t = jj_consume_token(SLASH);
        aPattern = SimpleAPattern();
        break;
      case DOUBLESLASH:
        t = jj_consume_token(DOUBLESLASH);
        aPattern1 = SimpleAPattern();
                                                         aPattern=new DoubleSlashPrefixedContainer(aPattern1);
        break;
      case OPENING_ROUND_BRACKET:
        t = jj_consume_token(OPENING_ROUND_BRACKET);
        aPattern1 = FullAPatternOr();
                                                                  aPattern=aPattern1;
        s = jj_consume_token(CLOSING_ROUND_BRACKET);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case STAR:
        case PLUS:
        case QUESTION_MARK:
          aPattern = Operator(aPattern1);
                                                                                                                                                s=null;
          break;
        default:
          jj_la1[20] = jj_gen;
          ;
        }
        break;
      default:
        jj_la1[21] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      if (aPatternBeginLine == 0) {
            aPatternBeginLine = t.beginLine;
            aPatternBeginColumn = t.beginColumn;
      }
      if (s == null) {
            aPatternEndLine = identifierEndLine;
            aPatternEndColumn = identifierEndColumn;
          } else {
            aPatternEndLine = s.endLine;
            aPatternEndColumn = s.endColumn;
          }

      {if (true) return aPattern;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("FullAPatternX");
    }
  }

// FullAPatternOr     ::= FullAPattern ("|" FullAPattern)*
  final public AncestorPattern FullAPatternOr() throws ParseException {
    trace_call("FullAPatternOr");
    try {
                                    AncestorPattern aPattern; OrExpression or=new OrExpression();
      aPattern = FullAPattern();
                                 or.addChild(aPattern);
      label_11:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OR:
          ;
          break;
        default:
          jj_la1[22] = jj_gen;
          break label_11;
        }
        jj_consume_token(OR);
        aPattern = FullAPattern();
                                                                                        or.addChild(aPattern);
      }
          {if (true) return or;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("FullAPatternOr");
    }
  }

// Operator           ::= "*" | "+" | "?"
  final public AncestorPattern Operator(AncestorPattern aPattern) throws ParseException {
    trace_call("Operator");
    try {
                                                     int min=0; Integer max = null; Token t;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STAR:
        t = jj_consume_token(STAR);
        break;
      case PLUS:
        t = jj_consume_token(PLUS);
                               min=1;
        break;
      case QUESTION_MARK:
        t = jj_consume_token(QUESTION_MARK);
                                                           max=1;
        break;
      default:
        jj_la1[23] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
          identifierEndLine = t.endLine;
          identifierEndColumn = t.endColumn;
          {if (true) return new CardinalityParticle(aPattern,min,max);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Operator");
    }
  }

// CPattern            ::= "{" ( AttributePattern "," )? ElementPattern "}"
  final public ChildPattern CPattern() throws ParseException {
    trace_call("CPattern");
    try {
                           boolean mixed = false; AttributePattern attributePattern = null; ElementPattern elementPattern;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case MIXED:
        jj_consume_token(MIXED);
                   mixed = true;
        break;
      default:
        jj_la1[24] = jj_gen;
        ;
      }
      jj_consume_token(OPENING_CURLY_BRACKET);
      if (jj_2_1(2)) {
        attributePattern = AttributePattern();
        jj_consume_token(COMMA);
      } else {
        ;
      }
      elementPattern = ElementPattern();
      jj_consume_token(CLOSING_CURLY_BRACKET);
      {if (true) return new ChildPattern(attributePattern, elementPattern, mixed);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("CPattern");
    }
  }

// ElementPattern      ::= "empty"
//                      | "missing"? MixedRegex
  final public ElementPattern ElementPattern() throws ParseException {
    trace_call("ElementPattern");
    try {
                                  boolean empty=false; boolean missing=false; Particle particle=null; BonxaiType type=null;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EMPTY:
        jj_consume_token(EMPTY);
                   particle=new EmptyPattern();
        break;
      case TYPE:
      case GROUP:
      case MISSING:
      case ELEMENTREF:
      case ELEMENT:
      case OPENING_ROUND_BRACKET:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case MISSING:
          jj_consume_token(MISSING);
                       missing=true;
          jj_consume_token(OR);
          break;
        default:
          jj_la1[25] = jj_gen;
          ;
        }
        if (jj_2_2(2147483647)) {
          particle = All();
        } else {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case GROUP:
          case ELEMENTREF:
          case ELEMENT:
          case OPENING_ROUND_BRACKET:
            particle = Regex();
            break;
          case TYPE:
            type = BonxaiType();
            break;
          default:
            jj_la1[26] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
        }
        break;
      default:
        jj_la1[27] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
                if (particle!=null) {
                        {if (true) return new ElementPattern(particle, missing);}
        } else {
                        {if (true) return new ElementPattern(type, missing);}
        }
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("ElementPattern");
    }
  }

//All                 ::= NamedType "?"? ( "&" NamedType "?"? )+
  final public AllPattern All() throws ParseException {
    trace_call("All");
    try {
                   Particle particle; List<Particle> particles=new LinkedList<Particle>();
      particle = NamedType();
                              particles.add(particle);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case QUESTION_MARK:
        jj_consume_token(QUESTION_MARK);
        break;
      default:
        jj_la1[28] = jj_gen;
        ;
      }
      label_12:
      while (true) {
        jj_consume_token(ANDLITERAL);
        particle = NamedType();
                                                                                                                 particles.add(particle);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case QUESTION_MARK:
          jj_consume_token(QUESTION_MARK);
          break;
        default:
          jj_la1[29] = jj_gen;
          ;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case ANDLITERAL:
          ;
          break;
        default:
          jj_la1[30] = jj_gen;
          break label_12;
        }
      }
          {if (true) return new AllPattern(particles);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("All");
    }
  }

//Grammar rule:
//# Regex               ::= Regex ( "*" | "+" | "?" | "[" Number ( "," Number )? "]" )?
//#                       | Regex "," Regex
//#                       | Regex "|" Regex
//#                       | "(" Regex ")"
//#                       | NamedType
//R::= RC ("|" RC)*
//RC::= RX ("," RX)* 
//RX::=("(" R ")" | NT) OP?
  final public Particle Regex() throws ParseException {
    trace_call("Regex");
    try {
                    Particle r; List<Particle> rs = new LinkedList<Particle>();
      r = RegexC();
                    rs.add(r);
      label_13:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OR:
          ;
          break;
        default:
          jj_la1[31] = jj_gen;
          break label_13;
        }
        jj_consume_token(OR);
        r = RegexC();
                                                  rs.add(r);
      }
                if (rs.size()==1)
                        {if (true) return r;}
                else
                        {if (true) return new ChoicePattern(rs);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Regex");
    }
  }

  final public Particle RegexC() throws ParseException {
    trace_call("RegexC");
    try {
                     Particle r; List<Particle> rs = new LinkedList<Particle>();
      r = RegexX();
                    rs.add(r);
      label_14:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[32] = jj_gen;
          break label_14;
        }
        jj_consume_token(COMMA);
        r = RegexX();
                                                     rs.add(r);
      }
                if (rs.size()==1)
                        {if (true) return r;}
                else
                        {if (true) return new SequencePattern(rs);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("RegexC");
    }
  }

  final public Particle RegexX() throws ParseException {
    trace_call("RegexX");
    try {
                     Particle r;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPENING_ROUND_BRACKET:
        jj_consume_token(OPENING_ROUND_BRACKET);
        r = Regex();
        jj_consume_token(CLOSING_ROUND_BRACKET);
        break;
      case GROUP:
      case ELEMENTREF:
      case ELEMENT:
        r = NamedType();
                                setState(DEFAULT);
        break;
      default:
        jj_la1[33] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPENING_SQUARED_BRACKET:
      case STAR:
      case PLUS:
      case QUESTION_MARK:
        r = RegexOp(r);
        break;
      default:
        jj_la1[34] = jj_gen;
        ;
      }
          {if (true) return r;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("RegexX");
    }
  }

  final public Particle RegexOp(Particle r) throws ParseException {
    trace_call("RegexOp");
    try {
                                int min=0; Integer max=null;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STAR:
        jj_consume_token(STAR);
        break;
      case PLUS:
        jj_consume_token(PLUS);
                                  min=1;
        break;
      case QUESTION_MARK:
        jj_consume_token(QUESTION_MARK);
                                                               max=1;
        break;
      case OPENING_SQUARED_BRACKET:
        jj_consume_token(OPENING_SQUARED_BRACKET);
        min = Number();
                                                         max=min;
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          jj_consume_token(COMMA);
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case CONSTANT:
            max = Number();
            break;
          case STAR:
            jj_consume_token(STAR);
                                                                                                        max=null;
            break;
          default:
            jj_la1[35] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
          break;
        default:
          jj_la1[36] = jj_gen;
          ;
        }
        jj_consume_token(CLOSING_SQUARED_BRACKET);
        break;
      default:
        jj_la1[37] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
                {if (true) return new CountingPattern(r, min, max);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("RegexOp");
    }
  }

//NamedType           ::= "element" Name ( ("{" "missing" "|")? BonxaiType "}" )?
//                      | "group" Name
//                      | Foreign "element" "*" ( "[" Number ( "," Number )? "]" )? NameSpaceList
//TODO: foreign
  final public Particle NamedType() throws ParseException {
    trace_call("NamedType");
    try {
                       Token t; BonxaiType type; Particle p; QualifiedName name; boolean missing=false;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ELEMENT:
        t = jj_consume_token(ELEMENT);
        name = Name();
                                          p = new Element(name, new BonxaiLocation(t.beginLine, t.beginColumn, identifierEndLine, identifierEndColumn));
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OPENING_CURLY_BRACKET:
          jj_consume_token(OPENING_CURLY_BRACKET);
                                                    setState(DEFAULT);
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case MISSING:
            jj_consume_token(MISSING);
            jj_consume_token(OR);
                                                                                            missing=true;
            break;
          default:
            jj_la1[38] = jj_gen;
            ;
          }
          type = BonxaiType();
          jj_consume_token(CLOSING_CURLY_BRACKET);
                          ((Element) p).setType(type,missing); setState(DEFAULT);
          break;
        default:
          jj_la1[39] = jj_gen;
          ;
        }
        break;
      case GROUP:
        jj_consume_token(GROUP);
        name = Name();
                                         p = new GroupReference(name); setState(DEFAULT);
        break;
      case ELEMENTREF:
        t = jj_consume_token(ELEMENTREF);
        name = Name();
                                               p = new ElementRef(name, new BonxaiLocation(t.beginLine, t.beginColumn, identifierEndLine, identifierEndColumn));
        break;
      default:
        jj_la1[40] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
          {if (true) return p;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("NamedType");
    }
  }

//AttributePattern    ::= Foreign "attribute" "*" NameSpaceList ( "," AttributeList )? | AttributeList
//TODO: foreign
  final public AttributePattern AttributePattern() throws ParseException {
    trace_call("AttributePattern");
    try {
                                      AttributeParticle a;  AttributePattern attributePattern = new AttributePattern();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ATTRIBUTEGROUP:
        a = AttributeGroupRef();
        break;
      case ATTRIBUTE:
        a = Attribute();
        break;
      case ATTRIBUTEREF:
        a = AttributeRef();
        break;
      default:
        jj_la1[41] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
                                                                       attributePattern.addAttribute(a);
      label_15:
      while (true) {
        if (jj_2_3(2147483647)) {
          ;
        } else {
          break label_15;
        }
        jj_consume_token(COMMA);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case ATTRIBUTEGROUP:
          a = AttributeGroupRef();
          break;
        case ATTRIBUTE:
          a = Attribute();
          break;
        case ATTRIBUTEREF:
          a = AttributeRef();
          break;
        default:
          jj_la1[42] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
                                                                                                                                                       attributePattern.addAttribute(a);
      }
          {if (true) return attributePattern;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("AttributePattern");
    }
  }

  final public AttributeRef AttributeRef() throws ParseException {
    trace_call("AttributeRef");
    try {
                               QualifiedName name; boolean required=true; String fixedValue=null; String defaultValue=null;
      jj_consume_token(ATTRIBUTEREF);
      name = Name();
                               setState(DEFAULT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPENING_CURLY_BRACKET:
        jj_consume_token(OPENING_CURLY_BRACKET);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case FIXED:
          jj_consume_token(FIXED);
          fixedValue = Quotation();
          break;
        case DEFAULTT:
          jj_consume_token(DEFAULTT);
          defaultValue = Quotation();
          break;
        default:
          jj_la1[43] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        jj_consume_token(CLOSING_CURLY_BRACKET);
        break;
      default:
        jj_la1[44] = jj_gen;
        ;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case QUESTION_MARK:
        jj_consume_token(QUESTION_MARK);
                       required=false;
        break;
      default:
        jj_la1[45] = jj_gen;
        ;
      }
    {if (true) return new AttributeRef(name, fixedValue, defaultValue, required);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("AttributeRef");
    }
  }

  final public AttributeGroupReference AttributeGroupRef() throws ParseException {
    trace_call("AttributeGroupRef");
    try {
                                               QualifiedName name;
      jj_consume_token(ATTRIBUTEGROUP);
      name = Name();
          setState(DEFAULT); {if (true) return new AttributeGroupReference(name);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("AttributeGroupRef");
    }
  }

  final public Attribute Attribute() throws ParseException {
    trace_call("Attribute");
    try {
                         QualifiedName name; BonxaiType type=null; boolean required=true;
      jj_consume_token(ATTRIBUTE);
      name = AttributeName();
                                           setState(DEFAULT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPENING_CURLY_BRACKET:
        jj_consume_token(OPENING_CURLY_BRACKET);
        type = BonxaiType();
        jj_consume_token(CLOSING_CURLY_BRACKET);
        break;
      default:
        jj_la1[46] = jj_gen;
        ;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case QUESTION_MARK:
        jj_consume_token(QUESTION_MARK);
                            required=false;
        break;
      default:
        jj_la1[47] = jj_gen;
        ;
      }
          {if (true) return new Attribute(name, type, required);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Attribute");
    }
  }

// Foreign             ::= "strict" | "lax" | "skip"
  final public String Foreign() throws ParseException {
    trace_call("Foreign");
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STRICT:
        jj_consume_token(STRICT);
                    {if (true) return "strict";}
        break;
      case LAX:
        jj_consume_token(LAX);
                                               {if (true) return "lax";}
        break;
      case SKIPP:
        jj_consume_token(SKIPP);
                                                                         {if (true) return "skip";}
        break;
      default:
        jj_la1[48] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Foreign");
    }
  }

// BonxaiType          ::= "type" Name
  final public BonxaiType BonxaiType() throws ParseException {
    trace_call("BonxaiType");
    try {
                           QualifiedName typename; String value; BonxaiType type;
      jj_consume_token(TYPE);
      typename = Name();
                                 type=new BonxaiType(typename); setState(DEFAULT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DEFAULTT:
      case FIXED:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case FIXED:
          jj_consume_token(FIXED);
          value = Quotation();
                                        type.setFixedValue(value);
          break;
        case DEFAULTT:
          jj_consume_token(DEFAULTT);
          value = Quotation();
                                           type.setDefaultValue(value);
          break;
        default:
          jj_la1[49] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[50] = jj_gen;
        ;
      }
          {if (true) return type;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("BonxaiType");
    }
  }

// Identifier          ::= ( NCName \ Keywords )
//                      | QuotedIdentifier
  final public String Identifier() throws ParseException {
    trace_call("Identifier");
    try {
                      Token t;
      t = jj_consume_token(LABEL);
          identifierEndLine = t.endLine;
          identifierEndColumn = t.endColumn;
          {if (true) return t.image;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Identifier");
    }
  }

  final public String Quotation() throws ParseException {
    trace_call("Quotation");
    try {
                     Token t;
      t = jj_consume_token(QUOTATION);
          {if (true) return t.image;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Quotation");
    }
  }

// Name                ::= ( (Letter | "_" | ":") (NameChar)* ) \ keywords  | QuotedName
// added: productions like Name:Name; first Name: Namespace (group.bonxai example!)
  final public QualifiedName Name() throws ParseException {
    trace_call("Name");
    try {
                       String s=null ; String t=null;
      s = Identifier();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COLON:
        jj_consume_token(COLON);
        t = Identifier();
        break;
      default:
        jj_la1[51] = jj_gen;
        ;
      }
                if (t == null) {
                {if (true) return new QualifiedName(this.bonxai.getDefaultNamespace(),s);}
                } else {
                {if (true) return new QualifiedName(this.bonxai.getNamespaceByIdentifier(s),t);}
                }
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Name");
    }
  }

  final public QualifiedName AttributeName() throws ParseException {
    trace_call("AttributeName");
    try {
                                String s=null ; String t=null;
      s = Identifier();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COLON:
        jj_consume_token(COLON);
        t = Identifier();
        break;
      default:
        jj_la1[52] = jj_gen;
        ;
      }
                if (t == null) {
                {if (true) return new QualifiedName(Namespace.EMPTY_NAMESPACE,s);}
                } else {
                {if (true) return new QualifiedName(this.bonxai.getNamespaceByIdentifier(s),t);}
                }
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("AttributeName");
    }
  }

// Number              ::= Digit+
  final public int Number() throws ParseException {
    trace_call("Number");
    try {
                 Token value;
      value = jj_consume_token(CONSTANT);
          {if (true) return Integer.valueOf(value.image).intValue();}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Number");
    }
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  final private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  final private boolean jj_3R_17() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_21()) {
    jj_scanpos = xsp;
    if (jj_3R_22()) {
    jj_scanpos = xsp;
    if (jj_3R_23()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3R_19() {
    if (jj_3R_25()) return true;
    return false;
  }

  final private boolean jj_3R_25() {
    if (jj_scan_token(ATTRIBUTE)) return true;
    if (jj_3R_29()) return true;
    return false;
  }

  final private boolean jj_3R_27() {
    if (jj_3R_30()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_31()) jj_scanpos = xsp;
    return false;
  }

  final private boolean jj_3R_24() {
    if (jj_scan_token(ATTRIBUTEGROUP)) return true;
    if (jj_3R_27()) return true;
    return false;
  }

  final private boolean jj_3R_37() {
    if (jj_scan_token(QUOTATION)) return true;
    return false;
  }

  final private boolean jj_3_2() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(49)) jj_scanpos = xsp;
    if (jj_scan_token(ANDLITERAL)) return true;
    return false;
  }

  final private boolean jj_3_3() {
    if (jj_scan_token(COMMA)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(23)) {
    jj_scanpos = xsp;
    if (jj_scan_token(21)) {
    jj_scanpos = xsp;
    if (jj_scan_token(22)) return true;
    }
    }
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_3R_16()) return true;
    return false;
  }

  final private boolean jj_3R_30() {
    if (jj_scan_token(LABEL)) return true;
    return false;
  }

  final private boolean jj_3R_32() {
    if (jj_scan_token(MISSING)) return true;
    if (jj_scan_token(OR)) return true;
    return false;
  }

  final private boolean jj_3R_28() {
    if (jj_scan_token(OPENING_CURLY_BRACKET)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_32()) jj_scanpos = xsp;
    if (jj_3R_33()) return true;
    if (jj_scan_token(CLOSING_CURLY_BRACKET)) return true;
    return false;
  }

  final private boolean jj_3R_36() {
    if (jj_scan_token(DEFAULTT)) return true;
    if (jj_3R_37()) return true;
    return false;
  }

  final private boolean jj_3R_18() {
    if (jj_3R_24()) return true;
    return false;
  }

  final private boolean jj_3R_35() {
    if (jj_scan_token(FIXED)) return true;
    if (jj_3R_37()) return true;
    return false;
  }

  final private boolean jj_3R_26() {
    if (jj_scan_token(ATTRIBUTEREF)) return true;
    if (jj_3R_27()) return true;
    return false;
  }

  final private boolean jj_3R_16() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_18()) {
    jj_scanpos = xsp;
    if (jj_3R_19()) {
    jj_scanpos = xsp;
    if (jj_3R_20()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3R_34() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_35()) {
    jj_scanpos = xsp;
    if (jj_3R_36()) return true;
    }
    return false;
  }

  final private boolean jj_3R_23() {
    if (jj_scan_token(ELEMENTREF)) return true;
    if (jj_3R_27()) return true;
    return false;
  }

  final private boolean jj_3R_22() {
    if (jj_scan_token(GROUP)) return true;
    if (jj_3R_27()) return true;
    return false;
  }

  final private boolean jj_3R_33() {
    if (jj_scan_token(TYPE)) return true;
    if (jj_3R_27()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_34()) jj_scanpos = xsp;
    return false;
  }

  final private boolean jj_3R_20() {
    if (jj_3R_26()) return true;
    return false;
  }

  final private boolean jj_3R_31() {
    if (jj_scan_token(COLON)) return true;
    if (jj_3R_30()) return true;
    return false;
  }

  final private boolean jj_3R_21() {
    if (jj_scan_token(ELEMENT)) return true;
    if (jj_3R_27()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_28()) jj_scanpos = xsp;
    return false;
  }

  final private boolean jj_3R_29() {
    if (jj_3R_30()) return true;
    return false;
  }

  public BonxaiJJParserTokenManager token_source;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[53];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_0();
      jj_la1_1();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x600,0x80000,0x8000,0x600,0x0,0x70000,0x70000,0x0,0x300000,0x300000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x1000000,0x4000000,0x18100800,0x1e100800,0x0,0x0,0x0,0x0,0x0,0x18100000,0x0,0x0,0x0,0x0,0x4000000,0x0,0x18100000,0xe00000,0xe00000,0x1000,0x0,0x0,0x0,0x0,0xe0000000,0x1000,0x1000,0x0,0x0,};
   }
   private static void jj_la1_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x34000000,0x0,0x0,0x200,0x0,0x0,0x4006404,0x200,0x4000000,0x400,0x400,0x4000000,0x4006004,0x4000404,0x800,0x6004,0x38000,0x6004,0x800,0x38000,0x0,0x0,0x4,0x4,0x20000,0x20000,0x1000,0x800,0x200,0x4,0x38010,0x8002,0x200,0x38010,0x0,0x40,0x0,0x0,0x0,0x1,0x40,0x20000,0x40,0x20000,0x0,0x1,0x1,0x40000,0x40000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[3];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public BonxaiJJParser(CharStream stream) {
    token_source = new BonxaiJJParserTokenManager(stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 53; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(CharStream stream) {
    token_source.ReInit(stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 53; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public BonxaiJJParser(BonxaiJJParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 53; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(BonxaiJJParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 53; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      trace_token(token, "");
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
      trace_token(token, " (in getNextToken)");
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[63];
    for (int i = 0; i < 63; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 53; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 63; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  private int trace_indent = 0;
  private boolean trace_enabled = true;

  final public void enable_tracing() {
    trace_enabled = true;
  }

  final public void disable_tracing() {
    trace_enabled = false;
  }

  final private void trace_call(String s) {
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.println("Call:   " + s);
    }
    trace_indent = trace_indent + 2;
  }

  final private void trace_return(String s) {
    trace_indent = trace_indent - 2;
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.println("Return: " + s);
    }
  }

  final private void trace_token(Token t, String where) {
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.print("Consumed token: <" + tokenImage[t.kind]);
      if (t.kind != 0 && !tokenImage[t.kind].equals("\"" + t.image + "\"")) {
        System.out.print(": \"" + t.image + "\"");
      }
      System.out.println(" at line " + t.beginLine + " column " + t.beginColumn + ">" + where);
    }
  }

  final private void trace_scan(Token t1, int t2) {
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.print("Visited token: <" + tokenImage[t1.kind]);
      if (t1.kind != 0 && !tokenImage[t1.kind].equals("\"" + t1.image + "\"")) {
        System.out.print(": \"" + t1.image + "\"");
      }
      System.out.println(" at line " + t1.beginLine + " column " + t1.beginColumn + ">; Expected token: <" + tokenImage[t2] + ">");
    }
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 3; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
