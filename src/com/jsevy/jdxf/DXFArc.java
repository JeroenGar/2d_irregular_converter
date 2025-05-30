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
 * Graphical entity representing a circular arc.
 *
 * @author jsevy
 */
public class DXFArc extends DXFEntity {
    protected RealPoint center;
    protected double radius;
    protected Color color;
    protected double linewidth;
    protected DXFLinetype linetype;
    private double startAngleRadians;
    private double endAngleRadians;
    private boolean isCounterclockwise;


    /**
     * Create a circular arc from the specified parameters.
     *
     * @param center             Center of the circle, as a RealPoint
     * @param radius             Radius of the circle
     * @param startAngleRadians  Starting angle of the circle, in radians, counterclockwise from the positive x axis
     * @param endAngleRadians    Starting angle of the circle, in radians, counterclockwise from the positive x axis
     * @param isCounterclockwise Indicate direction of arc, clockwise or counterclockwise
     * @param graphics           The graphics object specifying parameters for the arc (color, thickness)
     */
    public DXFArc(RealPoint center, double radius, double startAngleRadians, double endAngleRadians, boolean isCounterclockwise, DXFGraphics graphics) {
        // assign the layer to DXFEntity
        super(graphics.getLayer());

        this.startAngleRadians = startAngleRadians;
        this.endAngleRadians = endAngleRadians;
        this.isCounterclockwise = isCounterclockwise;
        this.center = new RealPoint(center);
        this.radius = radius;
        this.color = graphics.getColor();
        this.linewidth = graphics.getLineWidth();
        this.linetype = graphics.addLinetype();

    }

    /**
     * Implementation of DXFObject interface method; creates DXF text representing the circular arc.
     */
    public String toDXFString() {
        StringBuilder result = new StringBuilder("0\nARC\n");

        // print out handle and superclass marker(s) and data
        result.append(super.toDXFString());

        // print out subclass marker
        result.append("100\nAcDbCircle\n");

        // center
        result.append("10\n" + setPrecision(center.x) + "\n");
        result.append("20\n" + setPrecision(center.y) + "\n");
        result.append("30\n" + setPrecision(center.z) + "\n");

        // radius
        result.append("40\n" + setPrecision(radius) + "\n");

        // print out subclass marker
        result.append("100\nAcDbArc\n");

        // angles - which are in degrees for circular arcs
        double startAngleDegrees = (startAngleRadians * 180 / Math.PI);
        double endAngleDegrees = (endAngleRadians * 180 / Math.PI);
        result.append("50\n" + setPrecision(startAngleDegrees) + "\n");
        result.append("51\n" + setPrecision(endAngleDegrees) + "\n");

        // add linetype
        result.append("6\n" + linetype.getName() + "\n");

        // add thickness; specified in Java in pixels at 72 pixels/inch; needs to be in 1/100 of mm for DXF, and restricted range of values
        result.append("370\n" + getDXFLineWeight(linewidth) + "\n");

        // add color number
        result.append("62\n" + DXFColor.getClosestDXFColor(color.getRGB()) + "\n");

        return result.toString();
    }


    public String getDXFHatchInfo() {
        // circular arc
        StringBuilder result = new StringBuilder("72\n" + "2" + "\n");

        // center
        result.append("10\n" + setPrecision(center.x) + "\n");
        result.append("20\n" + setPrecision(center.y) + "\n");

        // radius
        result.append("40\n" + setPrecision(radius) + "\n");

        // start/end angles - IN DEGREES FOR HATCH!
        double startAngleDegrees = (startAngleRadians * 180 / Math.PI);
        double endAngleDegrees = (endAngleRadians * 180 / Math.PI);

        // do some stuff to accommodate LibreCAD, which ignores counterclockwise flag and doesn't like negative angles or those
        // outside the range 0-360 - not sure how much to try to accommodate...
        // make 'em positive
        while (startAngleDegrees < 0)
            startAngleDegrees += 360;
        while (endAngleDegrees < 0)
            endAngleDegrees += 360;

        // stupid stuff for float/double rounding
        if (startAngleDegrees >= 360)
            startAngleDegrees -= 360;
        if (endAngleDegrees >= 360)
            endAngleDegrees -= 360;

        // LibreCAD draws counterclockwise if start < end, clockwise if end < start
        if (isCounterclockwise) {
            if (endAngleDegrees < startAngleDegrees)
                endAngleDegrees += 360;
        }

        if (!isCounterclockwise) {
            // need to reverse start and end angles
            double temp = startAngleDegrees;
            startAngleDegrees = endAngleDegrees;
            endAngleDegrees = temp;
        }

        result.append("50\n" + setPrecision(startAngleDegrees) + "\n");
        result.append("51\n" + setPrecision(endAngleDegrees) + "\n");

        // counterclockwise flag
        if (isCounterclockwise)
            result.append("73\n" + "1" + "\n");
        else
            result.append("73\n" + "0" + "\n");

        return result.toString();
    }

}