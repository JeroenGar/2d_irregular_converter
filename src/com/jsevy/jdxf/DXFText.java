/*
 * JDXF Library
 *
 *   Copyright (C) 2018, Jonathan Sevy <jsevy@jsevy.com>
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 *
 */

package com.jsevy.jdxf;

import java.awt.*;


/**
 * Class representing DXF text, located at a specified position and having specified style (font, size).
 *
 * @author jsevy
 */
public class DXFText extends DXFEntity {
    private RealPoint basePoint;
    private String text;
    private Color color;
    private double rotationAngle;
    private double obliqueAngle;
    private DXFStyle style;

    private double capHeight;


    /**
     * Create a text object at the specified position.
     *
     * @param text      Text string
     * @param basePoint Location of the base point of the text
     * @param capHeight The height of the font (generally the height of the capital H in the font)
     * @param style     Text style (font)
     * @param graphics  The graphics object specifying parameters for this entity (color, thickness)
     */
    public DXFText(String text, RealPoint basePoint, double capHeight, DXFStyle style, DXFGraphics graphics) {
        this(text, basePoint, capHeight, 0, style, graphics);
    }


    /**
     * Create a text object at the specified position.
     *
     * @param text          Text string
     * @param basePoint     Location of the base point of the text
     * @param capHeight     The height of the font (generally the height of the capital H in the font)
     * @param rotationAngle Angle by which the text baseline is rotated from the horizontal
     * @param style         Text style (font)
     * @param graphics      The graphics object specifying parameters for this entity (color, thickness)
     */
    public DXFText(String text, RealPoint basePoint, double capHeight, double rotationAngle, DXFStyle style, DXFGraphics graphics) {
        this(text, basePoint, capHeight, rotationAngle, 0, style, graphics);
    }


    /**
     * Create a text object at the specified position.
     *
     * @param text          Text string
     * @param basePoint     Location of the base point of the text
     * @param capHeight     The height of the font (generally the height of the capital H in the font)
     * @param rotationAngle Angle by which the text baseline is rotated from the horizontal
     * @param obliqueAngle  Angle by which the text is slanted from the baseline
     * @param style         Text style (font)
     * @param graphics      The graphics object specifying parameters for this entity (color, thickness)
     */
    public DXFText(String text, RealPoint basePoint, double capHeight, double rotationAngle, double obliqueAngle, DXFStyle style, DXFGraphics graphics) {
        // assign the layer to DXFEntity
        super(graphics.getLayer());

        this.text = text;
        this.basePoint = new RealPoint(basePoint);
        this.capHeight = capHeight;
        this.rotationAngle = rotationAngle;
        this.obliqueAngle = obliqueAngle;
        this.style = style;
        graphics.getFont();
        graphics.getFontMetrics();
        this.color = graphics.getColor();

    }


    /**
     * Implementation of DXFObject interface method; creates DXF text representing the text object.
     */
    public String toDXFString() {
        StringBuilder result = new StringBuilder("0\nTEXT\n");

        // print out handle and superclass marker(s)
        result.append(super.toDXFString());

        // print out subclass marker
        result.append("100\nAcDbText\n");

        // the string contents; replace newlines with spaces so doesn't trash the DXF processor
        text.replace('\n', ' ');
        result.append("1\n" + text + "\n");

        // add text style info
        result.append("7\n" + style.getStyleName() + "\n");

        // we use the same base point whether adding alignment or not
        result.append("10\n" + basePoint.x + "\n");
        result.append("20\n" + basePoint.y + "\n");
        result.append("30\n" + basePoint.z + "\n");

        result.append("11\n" + basePoint.x + "\n");
        result.append("21\n" + basePoint.y + "\n");
        result.append("31\n" + basePoint.z + "\n");

        // include text size; DXF expects cap height; approximate with height of capital "H"
        result.append("40\n" + capHeight + "\n");

        // add rotation if any
        result.append("50\n" + rotationAngle + "\n");

        // add oblique angle if any
        result.append("51\n" + obliqueAngle + "\n");

        // add color number
        result.append("62\n" + DXFColor.getClosestDXFColor(color.getRGB()) + "\n");

        // print out subclass marker again... why? To make AutoCAD happy... grr...
        result.append("100\nAcDbText\n");

        return result.toString();
    }
}