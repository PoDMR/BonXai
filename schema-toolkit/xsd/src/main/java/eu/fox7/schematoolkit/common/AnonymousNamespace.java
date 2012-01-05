package eu.fox7.schematoolkit.common;


public class AnonymousNamespace extends Namespace {
	public AnonymousNamespace(String uri) {
		super(uri);
	}

	@Override
	public String getPrefix() {
		throw new RuntimeException("GetPrefix() called on anonymous namespace.");
	}

	@Override
	public String getIdentifier() {
		throw new RuntimeException("GetIdentifier() called on anonymous namespace.");
	}

}
