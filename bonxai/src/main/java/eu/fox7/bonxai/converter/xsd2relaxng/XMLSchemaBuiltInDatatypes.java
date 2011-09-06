package eu.fox7.bonxai.converter.xsd2relaxng;

/**
 * XML XSDSchema: built in datatypes
 *
 * To compare given strings with the values within this enumeration, they have
 * to be converted to upper case. This is necessary, because some values are
 * keywords in Java, too.
 *
 * @author Lars Schmidt
 */
enum XMLSchemaBuiltInDatatypes {

        STRING,
        BOOLEAN,
        FLOAT,
        DOUBLE,
        DECIMAL,
        DATETIME,
        DURATION,
        HEXBINARY,
        BASE64BINARY,
        ANYURI,
        ID,
        IDREF,
        ENTITY,
        NOTATION,
        NORMALIZEDSTRING,
        TOKEN,
        LANGUAGE,
        IDREFS,
        ENTITIES,
        NMTOKEN,
        NMTOKENS,
        NAME,
        QNAME,
        NCNAME,
        INTEGER,
        NONNEGATIVEINTEGER,
        POSITIVEINTEGER,
        NONPOSITIVEINTEGER,
        NEGATIVEINTEGER,
        BYTE,
        INT,
        LONG,
        SHORT,
        UNSIGNEDBYTE,
        UNSIGNEDINT,
        UNSIGNEDLONG,
        UNSIGNEDSHORT,
        DATE,
        TIME,
        GYEARMONTH,
        GYEAR,
        GMONTHDAY,
        GDAY,
        GMONTH,
        ANYSIMPLETYPE;
    }
