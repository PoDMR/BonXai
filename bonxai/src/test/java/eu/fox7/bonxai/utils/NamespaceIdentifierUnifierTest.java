package eu.fox7.bonxai.utils;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.utils.NamespaceIdentifierUnifier;

public class NamespaceIdentifierUnifierTest extends junit.framework.TestCase {

    private NamespaceIdentifierUnifier getUnifierFixture() {
        return new NamespaceIdentifierUnifier();
    }

    private NamespaceList getInitialNamespaceListFixture() {
        NamespaceList namespaceList = new NamespaceList( new DefaultNamespace( "http://www.example.com/default" ) );
        namespaceList.addIdentifiedNamespace( new IdentifiedNamespace( "foo", "http://www.example.com/foo" ) );
        namespaceList.addIdentifiedNamespace( new IdentifiedNamespace( "bar", "http://www.example.com/bar" ) );
        return namespaceList;
    }

    private NamespaceList getNonCollidingNamespaceListFixture() {
        NamespaceList namespaceList = new NamespaceList( new DefaultNamespace( "http://www.example.com/default" ) );
        namespaceList.addIdentifiedNamespace( new IdentifiedNamespace( "baz", "http://www.example.com/baz" ) );
        return namespaceList;
    }

    private NamespaceList getCollidingNamespaceListFixture() {
        NamespaceList namespaceList = new NamespaceList( new DefaultNamespace( "http://www.example.com/default" ) );
        namespaceList.addIdentifiedNamespace( new IdentifiedNamespace( "foo", "http://www.example.com/another_foo" ) );
        return namespaceList;
    }

    public final void testDefaultNamespaceAfterFirstEnvironment() {
        NamespaceIdentifierUnifier unifier = this.getUnifierFixture();
        unifier.applyEnvironment( this.getInitialNamespaceListFixture() );
        NamespaceList unifiedList = unifier.getUnifiedEnvironment();

        assertEquals( "http://www.example.com/default", unifiedList.getDefaultNamespace().getUri() );
    }

    public final void testNoEnvironmentChangeAfterInit() {
        NamespaceIdentifierUnifier unifier = this.getUnifierFixture();
        unifier.applyEnvironment( this.getInitialNamespaceListFixture() );

        NamespaceList unifiedList = unifier.getUnifiedEnvironment();

        assertEquals( 2, unifiedList.getIdentifiedNamespaces().size() );
        assertEquals( "http://www.example.com/foo", unifiedList.getNamespaceByIdentifier( "foo" ).getUri() );
        assertEquals( "http://www.example.com/bar", unifiedList.getNamespaceByIdentifier( "bar" ).getUri() );
    }

    public final void testNoRenameChangeAfterInit() {
        NamespaceIdentifierUnifier unifier = this.getUnifierFixture();
        unifier.applyEnvironment( this.getInitialNamespaceListFixture() );

        assertEquals( "foo", unifier.getUnifiedIdentifier( "foo" ) );
        assertEquals( "bar", unifier.getUnifiedIdentifier( "bar" ) );
    }

    public final void testEnvironmentNonCollidingIdentifier() {
        NamespaceIdentifierUnifier unifier = this.getUnifierFixture();
        unifier.applyEnvironment( this.getInitialNamespaceListFixture() );
        unifier.applyEnvironment( this.getNonCollidingNamespaceListFixture() );

        NamespaceList unifiedList = unifier.getUnifiedEnvironment();

        assertEquals( 3, unifiedList.getIdentifiedNamespaces().size() );
        assertEquals( "http://www.example.com/foo", unifiedList.getNamespaceByIdentifier( "foo" ).getUri() );
        assertEquals( "http://www.example.com/bar", unifiedList.getNamespaceByIdentifier( "bar" ).getUri() );
        assertEquals( "http://www.example.com/baz", unifiedList.getNamespaceByIdentifier( "baz" ).getUri() );

    }

    public final void testRenameNonCollidingIdentifier() {
        NamespaceIdentifierUnifier unifier = this.getUnifierFixture();
        unifier.applyEnvironment( this.getInitialNamespaceListFixture() );
        unifier.applyEnvironment( this.getNonCollidingNamespaceListFixture() );

        assertEquals( "foo", unifier.getUnifiedIdentifier( "foo" ) );
        assertEquals( "bar", unifier.getUnifiedIdentifier( "bar" ) );
        assertEquals( "baz", unifier.getUnifiedIdentifier( "baz" ) );
    }

    public final void testRenameSameNamespace() {
        NamespaceIdentifierUnifier unifier = this.getUnifierFixture();
        unifier.applyEnvironment( this.getInitialNamespaceListFixture() );
        unifier.applyEnvironment( this.getInitialNamespaceListFixture() );

        assertEquals( "foo", unifier.getUnifiedIdentifier( "foo" ) );
        assertEquals( "bar", unifier.getUnifiedIdentifier( "bar" ) );
    }

    public final void testEnvironmentSameNamespace() {
        NamespaceIdentifierUnifier unifier = this.getUnifierFixture();
        unifier.applyEnvironment( this.getInitialNamespaceListFixture() );
        unifier.applyEnvironment( this.getInitialNamespaceListFixture() );

        NamespaceList unifiedList = unifier.getUnifiedEnvironment();

        assertEquals( 2, unifiedList.getIdentifiedNamespaces().size() );
        assertEquals( "http://www.example.com/foo", unifiedList.getNamespaceByIdentifier( "foo" ).getUri() );
        assertEquals( "http://www.example.com/bar", unifiedList.getNamespaceByIdentifier( "bar" ).getUri() );
    }

    public final void testEnvironmentCollidingIdentifier() {
        NamespaceIdentifierUnifier unifier = this.getUnifierFixture();
        unifier.applyEnvironment( this.getInitialNamespaceListFixture() );
        unifier.applyEnvironment( this.getCollidingNamespaceListFixture() );

        NamespaceList unifiedList = unifier.getUnifiedEnvironment();

        assertEquals( 3, unifiedList.getIdentifiedNamespaces().size() );
        assertEquals( "http://www.example.com/foo", unifiedList.getNamespaceByIdentifier( "foo" ).getUri() );
        assertEquals( "http://www.example.com/bar", unifiedList.getNamespaceByIdentifier( "bar" ).getUri() );
        assertEquals( "http://www.example.com/another_foo", unifiedList.getNamespaceByIdentifier( "a-foo" ).getUri() );

    }

    public final void testRenameCollidingIdentifier() {
        NamespaceIdentifierUnifier unifier = this.getUnifierFixture();

        unifier.applyEnvironment( this.getInitialNamespaceListFixture() );
        assertEquals( "foo", unifier.getUnifiedIdentifier( "foo" ) );
        assertEquals( "bar", unifier.getUnifiedIdentifier( "bar" ) );

        unifier.applyEnvironment( this.getCollidingNamespaceListFixture() );
        assertEquals( "a-foo", unifier.getUnifiedIdentifier( "foo" ) );
    }
}
