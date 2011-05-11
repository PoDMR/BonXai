/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudortmund.cs.bonxai.converter.bonxai2xsd.old;


/**
 * This class has nothing to do with the conversion from Bonxai to XSD
 * This is just for testing and debugging purposes
 */
public class DebugUtil {

    public void printTreeNodeToSystemOut(TreeNode node) {
        System.out.print("+--- Start of Test Case -----------------------------+\n\n");
        this.recurseTreeNodeToSystemOut(node, 0);
        System.out.print("\n+--- End ------------------------------------------+\n\n\n");
    }

    private void recurseTreeNodeToSystemOut(TreeNode node, int level) {
        String spacer = new String("");
        for (int i = 1; i < level; ++i) {
            spacer += " |   ";
        }
        if (level > 0) {
            System.out.print(spacer + " |" + "\n" + spacer + " +-->");
        }

//        System.out.print(" " + node.hashCode() + " " + node.getId() + "\n");
        if (node instanceof ElementTreeNode) {
            System.out.print(" Element: " + ((ElementTreeNode)node).getName() + ((ElementTreeNode)node).getId() + "\n");
        }

        if (node instanceof GroupTreeNode) {
            System.out.print(" Group: " + ((GroupTreeNode)node).getName() + ((GroupTreeNode)node).getId() + "\n");
        }


        if (node instanceof ElementTreeNode && !((ElementTreeNode) node).getConstraints().isEmpty()) {
            System.out.println(spacer + " |" + "    constraints: " + ((ElementTreeNode) node).getConstraints());
        } else if (node instanceof GroupTreeNode && (((GroupTreeNode) node).getGroupRef() != null)) {
            System.out.println(spacer + " |" + "    GroupName: " + ((GroupTreeNode) node).getName());
            System.out.println(spacer + " |" + "    Namespaces: " + ((GroupTreeNode) node).getNamespaces());
            System.out.println(spacer + " |" + "    GroupRef: " + ((GroupTreeNode) node).getGroupRef());

        }


        level++;
        for (TreeNode currentChild : node.getChilds()) {
            recurseTreeNodeToSystemOut(currentChild, level);
        }
    }
}
