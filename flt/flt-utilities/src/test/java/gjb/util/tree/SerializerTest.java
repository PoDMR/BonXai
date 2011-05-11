package gjb.util.tree;
import gjb.util.tree.SExpressionSerializer;
import gjb.util.tree.StringSerializer;
import gjb.util.tree.Tree;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/*
 * Created on Nov 8, 2005
 * Modified on $Date: 2009-10-26 18:37:39 $
 */

public class SerializerTest extends TestCase {

    protected Tree tree;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SerializerTest.class);
    }

    public static Test suite() {
        return new TestSuite(SerializerTest.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        tree = Tree.parse("(a (b (d)) (c (e) (f)))");
    }

    public void testStringSerializer() {
        final String targetStr =
            "key: a\n" +
            "value: []\n" +
            "  key: b\n" +
            "  value: []\n" +
            "    key: d\n" +
            "    value: []\n" +
            "  key: c\n" +
            "  value: []\n" +
            "    key: e\n" +
            "    value: []\n" +
            "    key: f\n" +
            "    value: []\n";
        StringSerializer serializer = new StringSerializer();
        String str = serializer.serialize(tree);
        assertEquals("string serializer", targetStr, str);
        assertEquals("toString", targetStr, tree.toString());
    }

    public void testSExpressionSerializer() {
        SExpressionSerializer serializer = new SExpressionSerializer();
        String sExpr = serializer.serialize(tree);
        String targetStr = tree.toSExpression();
        assertEquals("S-Expressions", sExpr, targetStr);
    }

}
