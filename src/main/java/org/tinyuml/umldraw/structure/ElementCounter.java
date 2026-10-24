/**
 * Copyright 2007 Wei-ju Wu
 *
 * This file is part of TinyUML.
 *
 * TinyUML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TinyUML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.tinyuml.umldraw.structure;

import java.util.EnumMap;
import java.util.Map;
import org.tinyuml.draw.AbstractCompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.model.ElementType;
import org.tinyuml.umldraw.shared.NoteElement;

/**
 * Counts the elements of a diagram by category (RF-001). The categories are
 * the ones defined by ElementType: package, class, component and note.
 * Relations between elements (connections) are not elements and are never
 * counted. Elements nested in other elements, e.g. a class inside a package,
 * are counted as well.
 *
 * @version 1.0
 */
public final class ElementCounter {

  private final Map<ElementType, Integer> counts =
    new EnumMap<ElementType, Integer>(ElementType.class);

  /**
   * Constructor. Takes a snapshot of the specified diagram's element counts.
   * @param diagram the diagram to count the elements of
   */
  public ElementCounter(StructureDiagram diagram) {
    for (ElementType type : ElementType.values()) {
      counts.put(type, 0);
    }
    countChildren(diagram);
  }

  /**
   * Counts the children of the specified node recursively.
   * @param parent the node whose children are counted
   */
  private void countChildren(AbstractCompositeNode parent) {
    for (DiagramElement child : parent.getChildren()) {
      if (child instanceof Connection) continue;
      ElementType type = typeOf(child);
      if (type != null) {
        counts.put(type, counts.get(type) + 1);
      }
      if (child instanceof AbstractCompositeNode) {
        countChildren((AbstractCompositeNode) child);
      }
    }
  }

  /**
   * Determines the category of the specified element.
   * @param element the element
   * @return the ElementType or null if the element is not in any category
   */
  public static ElementType typeOf(DiagramElement element) {
    if (element instanceof PackageElement) return ElementType.PACKAGE;
    if (element instanceof ClassElement) return ElementType.CLASS;
    if (element instanceof ComponentElement) return ElementType.COMPONENT;
    if (element instanceof NoteElement) return ElementType.NOTE;
    return null;
  }

  /**
   * Returns the name of the specified category as it is shown to the user.
   * @param type the category
   * @return the display name
   */
  public static String getCategoryName(ElementType type) {
    switch (type) {
      case PACKAGE: return "Package";
      case CLASS: return "Class";
      case COMPONENT: return "Component";
      default: return "Note";
    }
  }

  /**
   * Returns the number of elements of the specified category.
   * @param type the category
   * @return the number of elements
   */
  public int getCount(ElementType type) { return counts.get(type); }

  /**
   * Returns the total number of elements over all categories.
   * @return the total number of elements
   */
  public int getTotal() {
    int total = 0;
    for (int count : counts.values()) {
      total += count;
    }
    return total;
  }

  /**
   * Returns the text for the status bar, e.g.
   * "Total Items: 06 | Package: 02 | Class: 02 | Component: 02 | Note: 00".
   * @return the status bar text
   */
  public String toStatusText() {
    return String.format(
      "Total Items: %02d | Package: %02d | Class: %02d | Component: %02d"
      + " | Note: %02d", getTotal(), getCount(ElementType.PACKAGE),
      getCount(ElementType.CLASS), getCount(ElementType.COMPONENT),
      getCount(ElementType.NOTE));
  }
}
