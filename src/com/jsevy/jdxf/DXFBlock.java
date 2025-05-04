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


/**
 * Class representing a block for use in BLOCKs table.
 *
 * @author jsevy
 */
public class DXFBlock extends DXFEntity {
    private String name;

    /**
     * Create a BLOCK table record object with specified name.
     *
     * @param name name of table record
     */
    public DXFBlock(String name) {
        // just assign block end to default layer
        super(null);

        this.name = name;
    }


    /**
     * Implementation of DXFObject interface method; creates DXF text representing the object.
     */
    public String toDXFString() {
        StringBuilder result = new StringBuilder("0\nBLOCK\n");

        // print out handle and superclass marker(s)
        result.append(super.toDXFString());

        // print out subclass marker
        result.append("100\nAcDbBlockBegin\n");

        // print out name
        result.append("2\n" + name + "\n");

        // no flags set
        result.append("70\n0\n");

        // block left corner
        result.append("10\n0\n");
        result.append("20\n0\n");
        result.append("30\n0\n");

        // print out name again?
        result.append("3\n" + name + "\n");

        // xref path name - nothing
        result.append("1\n\n");


        return result.toString();
    }

}