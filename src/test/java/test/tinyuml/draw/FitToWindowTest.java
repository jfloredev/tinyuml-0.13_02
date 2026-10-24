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
package test.tinyuml.draw;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import junit.framework.TestCase;
import org.tinyuml.draw.FitToWindow;

/**
 * Test class for FitToWindow (RF-005).
 * @version 1.0
 */
public class FitToWindowTest extends TestCase {

  private static final Dimension VIEWPORT = new Dimension(800, 600);

  /**
   * Content that is smaller than the viewport is not enlarged.
   */
  public void testSmallContentIsNotEnlarged() {
    Rectangle2D content = new Rectangle2D.Double(30, 30, 100, 50);
    assertEquals(1.0, FitToWindow.computeScale(content, VIEWPORT));
  }

  /**
   * Content that is too wide is scaled down to the viewport width.
   */
  public void testWideContentIsScaledToWidth() {
    Rectangle2D content = new Rectangle2D.Double(0, 0, 1560, 100);
    double expected = 800.0 / (1560 + 2 * FitToWindow.MARGIN);
    assertEquals(expected, FitToWindow.computeScale(content, VIEWPORT), 1e-9);
  }

  /**
   * Content that is too high is scaled down to the viewport height.
   */
  public void testHighContentIsScaledToHeight() {
    Rectangle2D content = new Rectangle2D.Double(0, 0, 100, 2360);
    double expected = 600.0 / (2360 + 2 * FitToWindow.MARGIN);
    assertEquals(expected, FitToWindow.computeScale(content, VIEWPORT), 1e-9);
  }

  /**
   * The scaled content, including the margin, always fits into the viewport.
   */
  public void testScaledContentFitsViewport() {
    double[][] sizes = {{5000, 3000}, {3000, 5000}, {800, 600}, {10, 10000}};
    for (double[] size : sizes) {
      Rectangle2D content = new Rectangle2D.Double(40, 60, size[0], size[1]);
      double scale = FitToWindow.computeScale(content, VIEWPORT);
      Rectangle2D area = FitToWindow.getVisibleArea(content);
      assertTrue(area.getWidth() * scale <= VIEWPORT.width + 1e-9);
      assertTrue(area.getHeight() * scale <= VIEWPORT.height + 1e-9);
      assertTrue(scale > 0 && scale <= 1.0);
    }
  }

  /**
   * The view position is never negative and never leaves the scrollable area.
   */
  public void testViewPositionIsRestricted() {
    Rectangle2D content = new Rectangle2D.Double(0, 0, 50, 50);
    // content close to the origin: position is clamped to the left upper corner
    Point pos = FitToWindow.computeViewPosition(content, 1.0, VIEWPORT,
      new Dimension(1000, 800));
    assertEquals(new Point(0, 0), pos);
    // content at the far end: position is clamped to the end of the view
    content = new Rectangle2D.Double(990, 790, 50, 50);
    pos = FitToWindow.computeViewPosition(content, 1.0, VIEWPORT,
      new Dimension(1000, 800));
    assertEquals(new Point(200, 200), pos);
  }

  /**
   * The content is centered if the view is large enough to allow that.
   */
  public void testContentIsCentered() {
    Rectangle2D content = new Rectangle2D.Double(1000, 1000, 400, 200);
    Point pos = FitToWindow.computeViewPosition(content, 1.0, VIEWPORT,
      new Dimension(3000, 3000));
    // center of the content is (1200, 1100)
    assertEquals(1200 - 400, pos.x);
    assertEquals(1100 - 300, pos.y);
  }
}
