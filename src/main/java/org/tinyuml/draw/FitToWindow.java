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
package org.tinyuml.draw;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

/**
 * Calculations to fit the content of a diagram into the visible area of the
 * editor (RF-005). Only the view is affected: the zoom factor and the
 * scroll position. The elements themselves are never touched.
 *
 * @version 1.0
 */
public final class FitToWindow {

  /** Free space that is kept around the content, in diagram units. */
  public static final double MARGIN = 20;
  /** The content is never enlarged beyond 100 %. */
  public static final double MAX_SCALE = 1.0;
  /** Lower limit to prevent degenerated scale factors. */
  public static final double MIN_SCALE = 0.01;

  /**
   * Private constructor, this is a utility class.
   */
  private FitToWindow() { }

  /**
   * Returns the content bounds enlarged by the margin.
   * @param content the bounds of the content, in diagram units
   * @return the area that has to be visible
   */
  public static Rectangle2D getVisibleArea(Rectangle2D content) {
    return new Rectangle2D.Double(content.getX() - MARGIN,
      content.getY() - MARGIN, content.getWidth() + 2 * MARGIN,
      content.getHeight() + 2 * MARGIN);
  }

  /**
   * Computes the largest scale factor, not greater than 100 %, with which the
   * content fits completely into the specified viewport.
   * @param content the bounds of the content, in diagram units
   * @param viewport the size of the visible area, in pixels
   * @return the scale factor
   */
  public static double computeScale(Rectangle2D content, Dimension viewport) {
    Rectangle2D area = getVisibleArea(content);
    double scaleX = viewport.getWidth() / area.getWidth();
    double scaleY = viewport.getHeight() / area.getHeight();
    return Math.max(MIN_SCALE, Math.min(MAX_SCALE, Math.min(scaleX, scaleY)));
  }

  /**
   * Computes the view position (upper left corner of the visible area in
   * pixels) that centers the content in the viewport. The position is
   * restricted to the scrollable area.
   * @param content the bounds of the content, in diagram units
   * @param scale the scale factor that is used to display it
   * @param viewport the size of the visible area, in pixels
   * @param viewSize the size of the whole scrollable area, in pixels
   * @return the view position
   */
  public static Point computeViewPosition(Rectangle2D content, double scale,
    Dimension viewport, Dimension viewSize) {
    Rectangle2D area = getVisibleArea(content);
    double x = area.getX() * scale - (viewport.getWidth()
      - area.getWidth() * scale) / 2;
    double y = area.getY() * scale - (viewport.getHeight()
      - area.getHeight() * scale) / 2;
    int maxX = Math.max(0, viewSize.width - viewport.width);
    int maxY = Math.max(0, viewSize.height - viewport.height);
    return new Point(clamp((int) Math.round(x), maxX),
      clamp((int) Math.round(y), maxY));
  }

  /**
   * Restricts the value to the range from 0 to max.
   * @param value the value
   * @param max the upper limit
   * @return the restricted value
   */
  private static int clamp(int value, int max) {
    return Math.max(0, Math.min(max, value));
  }
}
