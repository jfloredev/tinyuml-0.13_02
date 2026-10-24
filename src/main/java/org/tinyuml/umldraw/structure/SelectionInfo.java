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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.model.ElementType;
import org.tinyuml.model.NamedElement;
import org.tinyuml.model.UmlModelElement;
import org.tinyuml.umldraw.shared.NoteConnection;
import org.tinyuml.umldraw.shared.NoteElement;
import org.tinyuml.umldraw.shared.UmlDiagramElement;

/**
 * Describes the current selection of a diagram for the status bar (RF-002 and
 * RF-003): the category and the name of the selected element, and the number
 * of selected elements. The diagram itself is not an element, it is ignored
 * when it is part of the selection.
 *
 * @version 1.0
 */
public final class SelectionInfo {

  /** Text that is shown when nothing is selected. */
  public static final String NO_SELECTION = "Sin selecci\u00f3n";
  /** Text that is shown if more than one element is selected. */
  public static final String MULTIPLE = "Varios elementos";
  /** Name that is shown for elements that do not have a name. */
  public static final String UNNAMED = "(sin nombre)";
  private static final int MAX_NAME_LENGTH = 30;

  private final List<DiagramElement> selected = new ArrayList<DiagramElement>();

  /**
   * Constructor.
   * @param elements the selected elements
   * @param diagram the diagram, it is ignored if it is part of the selection
   */
  public SelectionInfo(Collection<DiagramElement> elements,
    DiagramElement diagram) {
    for (DiagramElement element : elements) {
      if (element != null && element != diagram) {
        selected.add(element);
      }
    }
  }

  /**
   * Returns the number of selected elements.
   * @return the number of selected elements
   */
  public int getCount() { return selected.size(); }

  /**
   * Returns the text that shows the selected element for the status bar.
   * It is "Sin selecci&oacute;n" if nothing is selected, the category and the
   * name for one element, e.g. "Selecci&oacute;n: Class - Class 1", and
   * "Selecci&oacute;n: Varios elementos" for a multiple selection.
   * @return the status bar text
   */
  public String getDescriptionText() {
    if (selected.isEmpty()) return NO_SELECTION;
    if (selected.size() > 1) return "Selecci\u00f3n: " + MULTIPLE;
    DiagramElement element = selected.get(0);
    return "Selecci\u00f3n: " + getCategory(element) + " - " + getName(element);
  }

  /**
   * Returns the text that shows the number of selected elements.
   * @return the status bar text
   */
  public String getCountText() {
    return "Seleccionados: " + selected.size();
  }

  /**
   * Returns the category of the specified element. The categories of the
   * nodes are the ones of the element count, connections are reported as
   * relations.
   * @param element the element
   * @return the category
   */
  static String getCategory(DiagramElement element) {
    ElementType type = ElementCounter.typeOf(element);
    if (type != null) return ElementCounter.getCategoryName(type);
    if (element instanceof NoteConnection) return "Note connection";
    if (element instanceof Association) return "Association";
    if (element instanceof Dependency) return "Dependency";
    if (element instanceof Inheritance) return "Inheritance";
    return "Relation";
  }

  /**
   * Returns the name of the specified element.
   * @param element the element
   * @return the name, UNNAMED if the element does not have one
   */
  static String getName(DiagramElement element) {
    String name = null;
    if (element instanceof NoteElement) {
      name = firstLine(((NoteElement) element).getLabelText());
    } else if (element instanceof UmlDiagramElement) {
      UmlModelElement model = ((UmlDiagramElement) element).getModelElement();
      if (model instanceof NamedElement) {
        name = ((NamedElement) model).getName();
      }
    }
    if (name == null || name.trim().length() == 0) return UNNAMED;
    return name.trim();
  }

  /**
   * Returns the shortened first line of the specified text.
   * @param text the text, can be null
   * @return the first line, at most MAX_NAME_LENGTH characters long
   */
  private static String firstLine(String text) {
    if (text == null) return null;
    String line = text.trim();
    int eol = line.indexOf('\n');
    if (eol >= 0) line = line.substring(0, eol).trim();
    if (line.length() > MAX_NAME_LENGTH) {
      line = line.substring(0, MAX_NAME_LENGTH - 3) + "...";
    }
    return line;
  }
}
