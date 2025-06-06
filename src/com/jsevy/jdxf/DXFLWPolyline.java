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
import java.util.Vector;


/**
 * Class representing a set of line segments defining a (possibly closed) polygon.
 *
 * @author jsevy
 */
public class DXFLWPolyline extends DXFEntity {
    protected double linewidth;
    protected DXFLinetype linetype;
    private int numVertices;
    private Vector<RealPoint> vertices;
    private boolean closed;
    private Color color;


    /**
     * Create a set of line segments that connects the specified points, including a segment from the last
     * to the first if closed is indicated.
     *
     * @param numVertices The number of vertices specified in the vertex list
     * @param vertices    The vertices
     * @param closed      If true, adds a segment between the last and first points
     * @param graphics    The graphics object specifying parameters for this entity (color, thickness)
     */
    public DXFLWPolyline(int numVertices, Vector<RealPoint> vertices, boolean closed, DXFGraphics graphics) {
        // assign the layer to DXFEntity
        super(graphics.getLayer());

        this.numVertices = numVertices;
        this.vertices = vertices;
        this.closed = closed;
        this.color = graphics.getColor();
        this.linewidth = graphics.getLineWidth();
        this.linetype = graphics.addLinetype();
    }


    /**
     * Implementation of DXFObject interface method; creates DXF text representing the polyline.
     */
    public String toDXFString() {
        StringBuilder result = new StringBuilder("0\nLWPOLYLINE\n");

        // print out handle and superclass marker(s)
        result.append(super.toDXFString());

        // print out subclass marker
        result.append("100\nAcDbPolyline\n");

        // include number of vertices
        result.append("90\n" + numVertices + "\n");

        // indicate if closed
        if (closed) {
            result.append("70\n1\n");
        } else {
            result.append("70\n0\n");
        }

        // include list of vertices
        for (int i = 0; i < vertices.size(); i++) {
            RealPoint point = vertices.elementAt(i);
            result.append("10\n" + setPrecision(point.x) + "\n");
            result.append("20\n" + setPrecision(point.y) + "\n");
            result.append("30\n" + setPrecision(point.z) + "\n");
        }

        // add linetype
        result.append("6\n" + linetype.getName() + "\n");

        // add thickness; specified in Java in pixels at 72 pixels/inch; needs to be in 1/100 of mm for DXF, and restricted range of values
        result.append("370\n" + getDXFLineWeight(linewidth) + "\n");

        // add color number
        result.append("62\n" + DXFColor.getClosestDXFColor(color.getRGB()) + "\n");

        return result.toString();
    }
}