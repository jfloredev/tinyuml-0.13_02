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
package test.tinyuml.umldraw.structure;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.model.ElementType;
import org.tinyuml.model.RelationType;
import org.tinyuml.umldraw.shared.NoteElement;
import org.tinyuml.umldraw.shared.UmlConnection;
import org.tinyuml.umldraw.shared.UmlNode;
import org.tinyuml.umldraw.structure.DiagramElementFactory;
import org.tinyuml.umldraw.structure.SelectionInfo;
import org.tinyuml.umldraw.structure.StructureDiagram;

/**
 * Test class for SelectionInfo (RF-002 and RF-003).
 * @version 1.0
 */
public class SelectionInfoTest extends TestCase {

  private static final String NO_SELECTION = "Sin selecci\u00f3n";
  private StructureDiagram diagram;
  private DiagramElementFactory factory;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() {
    diagram = new StructureDiagram(null);
    factory = diagram.getElementFactory();
  }

  /**
   * Creates a SelectionInfo for the specified elements.
   * @param elements the selected elements
   * @return the SelectionInfo
   */
  private SelectionInfo info(DiagramElement... elements) {
    List<DiagramElement> list = new ArrayList<DiagramElement>();
    for (DiagramElement element : elements) {
      list.add(element);
    }
    return new SelectionInfo(list, diagram);
  }

  /**
   * Nothing selected: the text is "Sin selecci\u00f3n" and the count is zero.
   */
  public void testNothingSelected() {
    SelectionInfo info = info();
    assertEquals(NO_SELECTION, info.getDescriptionText());
    assertEquals(0, info.getCount());
    assertEquals("Seleccionados: 0", info.getCountText());
  }

  /**
   * Clicking the empty area selects the diagram, which is not an element.
   */
  public void testDiagramIsNotAnElement() {
    SelectionInfo info = info(diagram);
    assertEquals(NO_SELECTION, info.getDescriptionText());
    assertEquals(0, info.getCount());
  }

  /**
   * One class: category and name are shown.
   */
  public void testSingleClass() {
    UmlNode node = factory.createNode(ElementType.CLASS);
    SelectionInfo info = info(node);
    assertEquals("Selecci\u00f3n: Class - Class 1", info.getDescriptionText());
    assertEquals(1, info.getCount());
    assertEquals("Seleccionados: 1", info.getCountText());
    node.getModelElement().setName("Customer");
    assertEquals("Selecci\u00f3n: Class - Customer",
      info(node).getDescriptionText());
  }

  /**
   * Package and component.
   */
  public void testPackageAndComponent() {
    assertEquals("Selecci\u00f3n: Package - Package 1",
      info(factory.createNode(ElementType.PACKAGE)).getDescriptionText());
    assertEquals("Selecci\u00f3n: Component - Component 1",
      info(factory.createNode(ElementType.COMPONENT)).getDescriptionText());
  }

  /**
   * Notes do not have a name, their text is used instead.
   */
  public void testNote() {
    NoteElement note = (NoteElement) factory.createNode(ElementType.NOTE);
    // other tests modify the shared prototype, so do not rely on its text
    note.setLabelText("");
    assertEquals("Selecci\u00f3n: Note - (sin nombre)",
      info(note).getDescriptionText());
    note.setLabelText("Remember this\nsecond line");
    assertEquals("Selecci\u00f3n: Note - Remember this",
      info(note).getDescriptionText());
    note.setLabelText("0123456789012345678901234567890123456789");
    assertEquals("Selecci\u00f3n: Note - 012345678901234567890123456...",
      info(note).getDescriptionText());
  }

  /**
   * Unnamed or blank names are reported as unnamed.
   */
  public void testBlankName() {
    UmlNode node = factory.createNode(ElementType.CLASS);
    node.getModelElement().setName("   ");
    assertEquals("Selecci\u00f3n: Class - (sin nombre)",
      info(node).getDescriptionText());
    node.getModelElement().setName(null);
    assertEquals("Selecci\u00f3n: Class - (sin nombre)",
      info(node).getDescriptionText());
  }

  /**
   * A connection can be selected as well and is reported as a relation.
   */
  public void testConnection() {
    UmlNode node1 = factory.createNode(ElementType.CLASS);
    UmlNode node2 = factory.createNode(ElementType.CLASS);
    UmlConnection assoc = factory.createConnection(RelationType.ASSOCIATION,
      node1, node2);
    assertEquals("Selecci\u00f3n: Association - (sin nombre)",
      info(assoc).getDescriptionText());
    UmlConnection dep = factory.createConnection(RelationType.DEPENDENCY,
      node1, node2);
    assertTrue(info(dep).getDescriptionText().startsWith(
      "Selecci\u00f3n: Dependency - "));
  }

  /**
   * Several elements: the count is shown, the text does not name one element.
   */
  public void testMultipleSelection() {
    SelectionInfo info = info(factory.createNode(ElementType.CLASS),
      factory.createNode(ElementType.NOTE),
      factory.createNode(ElementType.PACKAGE));
    assertEquals(3, info.getCount());
    assertEquals("Seleccionados: 3", info.getCountText());
    assertEquals("Selecci\u00f3n: Varios elementos", info.getDescriptionText());
  }

  /**
   * The diagram is not counted when it is part of a larger selection.
   */
  public void testDiagramInMultipleSelectionIsIgnored() {
    SelectionInfo info = info(diagram, factory.createNode(ElementType.CLASS));
    assertEquals(1, info.getCount());
    assertEquals("Selecci\u00f3n: Class - Class 1", info.getDescriptionText());
  }
}
