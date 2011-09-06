package eu.fox7.bonxai.utils;

import eu.fox7.bonxai.utils.Base26Converter;

public class Base26ConverterTest extends junit.framework.TestCase {
    public final void testZeroConversionTo() {
        Base26Converter converter = new Base26Converter();
        assertEquals( "a", converter.convertToBase26( 0 ) );
    }

    public final void testZeroConversionFrom() {
        Base26Converter converter = new Base26Converter();
        assertEquals( 0, (int)converter.convertFromBase26( "a" ) );
    }

    public final void testInvaldNumberConversionFrom() {
        Base26Converter converter = new Base26Converter();
        assertEquals( 0, (int)converter.convertFromBase26( "423" ) );
    }

    public final void testInvalidCharacterConversionFrom() {
        Base26Converter converter = new Base26Converter();
        assertEquals( 0, (int)converter.convertFromBase26( "§$%" ) );
    }

    public final void testMaxBaseConversionTo() {
        Base26Converter converter = new Base26Converter();
        assertEquals( "z", converter.convertToBase26( 25 ) );
    }

    public final void testMaxBaseConversionFrom() {
        Base26Converter converter = new Base26Converter();
        assertEquals( 25, (int)converter.convertFromBase26( "z" ) );
    }

    public final void testAboveMaxBaseConversionTo() {
        Base26Converter converter = new Base26Converter();
        assertEquals( "ba", converter.convertToBase26( 26 ) );
    }

    public final void testAboveMaxBaseConversionFrom() {
        Base26Converter converter = new Base26Converter();
        assertEquals( 26, (int)converter.convertFromBase26( "ba" ) );
    }

    public final void testAnyNumberConversionTo() {
        Base26Converter converter = new Base26Converter();
        assertEquals( "foobar", converter.convertToBase26( 66051301 ) );
    }

    public final void testAnyNumberConversionFrom() {
        Base26Converter converter = new Base26Converter();
        assertEquals( 66051301, (int)converter.convertFromBase26( "foobar" ) );
    }

}
