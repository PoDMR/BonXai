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
