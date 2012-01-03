package eu.fox7.schematoolkit.common;

import java.util.LinkedList;
import java.util.List;

public abstract class NodeX {
	private List<NodeX> children = new LinkedList<NodeX>();
	private NodeX parent = null;

	private Position start = null;
	private Position end = null;

	/**
	 * @return the children
	 */
	public List<NodeX> getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	protected void setChildren(List<NodeX> children) {
		this.children = children;
	}
	
	protected void addChildren(List<NodeX> children) {
		this.children.addAll(children);
	}
	
	protected void removeChildren(List<NodeX> children) {
		this.children.removeAll(children);
	}
	
	protected void addChild(NodeX child) {
		this.children.add(child);
	}
	
	/**
	 * @return the parent
	 */
	public NodeX getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	protected void setParent(NodeX parent) {
		this.parent = parent;
	}
	/**
	 * @return the start
	 */
	public Position getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(Position start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public Position getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(Position end) {
		this.end = end;
	}
}
