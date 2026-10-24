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

import junit.framework.TestCase;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.model.ElementType;
import org.tinyuml.model.RelationType;
import org.tinyuml.umldraw.shared.UmlConnection;
import org.tinyuml.umldraw.shared.UmlNode;
import org.tinyuml.umldraw.structure.DiagramElementFactory;
import org.tinyuml.umldraw.structure.ElementCounter;
import org.tinyuml.umldraw.structure.StructureDiagram;

/**
 * Test class for ElementCounter (RF-001).
 * @version 1.0
 */
public class ElementCounterTest extends TestCase {

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
   * Creates a node of the specified type and adds it to the diagram.
   * @param type the element type
   * @return the node
   */
  private UmlNode add(ElementType type) {
    UmlNode node = factory.createNode(type);
    diagram.addChild(node);
    return node;
  }

  /**
   * An empty diagram shows zero for the total and for every category.
   */
  public void testEmptyDiagram() {
    ElementCounter counter = new ElementCounter(diagram);
    assertEquals(0, counter.getTotal());
    for (ElementType type : ElementType.values()) {
      assertEquals(0, counter.getCount(type));
    }
    assertEquals("Total Items: 00 | Package: 00 | Class: 00 | Component: 00"
      + " | Note: 00", counter.toStatusText());
  }

  /**
   * The example given in the requirement.
   */
  public void testRequirementExample() {
    add(ElementType.PACKAGE);
    add(ElementType.PACKAGE);
    add(ElementType.CLASS);
    add(ElementType.CLASS);
    add(ElementType.COMPONENT);
    add(ElementType.COMPONENT);
    assertEquals("Total Items: 06 | Package: 02 | Class: 02 | Component: 02"
      + " | Note: 00", new ElementCounter(diagram).toStatusText());
  }

  /**
   * Creating an element updates the total and its category only.
   */
  public void testAddElement() {
    add(ElementType.CLASS);
    add(ElementType.NOTE);
    ElementCounter counter = new ElementCounter(diagram);
    assertEquals(2, counter.getTotal());
    assertEquals(1, counter.getCount(ElementType.CLASS));
    assertEquals(1, counter.getCount(ElementType.NOTE));
    assertEquals(0, counter.getCount(ElementType.PACKAGE));
    assertEquals(0, counter.getCount(ElementType.COMPONENT));
  }

  /**
   * Removing an element updates the total and its category, the others are
   * not touched.
   */
  public void testRemoveElement() {
    UmlNode clazz = add(ElementType.CLASS);
    add(ElementType.CLASS);
    add(ElementType.COMPONENT);
    diagram.removeChild(clazz);
    ElementCounter counter = new ElementCounter(diagram);
    assertEquals(2, counter.getTotal());
    assertEquals(1, counter.getCount(ElementType.CLASS));
    assertEquals(1, counter.getCount(ElementType.COMPONENT));
  }

  /**
   * Relations are not elements and do not take part in the count.
   */
  public void testConnectionsAreNotCounted() {
    UmlNode node1 = add(ElementType.CLASS);
    UmlNode node2 = add(ElementType.CLASS);
    UmlNode note = add(ElementType.NOTE);
    UmlConnection assoc = factory.createConnection(RelationType.ASSOCIATION,
      node1, node2);
    UmlConnection noteConn = factory.createConnection(
      RelationType.NOTE_CONNECTOR, note, node1);
    diagram.addChild(assoc);
    diagram.addChild(noteConn);
    assertEquals(5, diagram.getChildren().size());
    ElementCounter counter = new ElementCounter(diagram);
    assertEquals(3, counter.getTotal());
    assertEquals(2, counter.getCount(ElementType.CLASS));
    assertEquals(1, counter.getCount(ElementType.NOTE));
  }

  /**
   * Elements that are nested in a package belong to the diagram as well.
   */
  public void testNestedElementsAreCounted() {
    UmlNode pkg = add(ElementType.PACKAGE);
    DiagramElement nested = factory.createNode(ElementType.CLASS);
    ((org.tinyuml.draw.CompositeElement) pkg).addChild(nested);
    ElementCounter counter = new ElementCounter(diagram);
    assertEquals(2, counter.getTotal());
    assertEquals(1, counter.getCount(ElementType.PACKAGE));
    assertEquals(1, counter.getCount(ElementType.CLASS));
  }

  /**
   * Counts of different diagrams are independent from each other.
   */
  public void testCountIsPerDiagram() {
    add(ElementType.CLASS);
    StructureDiagram other = new StructureDiagram(null);
    assertEquals(1, new ElementCounter(diagram).getTotal());
    assertEquals(0, new ElementCounter(other).getTotal());
  }
}
