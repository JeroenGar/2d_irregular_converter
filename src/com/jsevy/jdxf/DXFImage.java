package com.jsevy.jdxf;

import java.awt.image.BufferedImage;

public class DXFImage extends DXFEntity {

    private RealPoint basePoint;
    private RealPoint uVector;
    private RealPoint vVector;
    private String imagePath;

    private int imageWidth;
    private int imageHeight;


    public DXFImage(BufferedImage image, RealPoint basePoint, RealPoint uVector, RealPoint vVector, DXFGraphics graphics) {
        // assign the layer to DXFEntity
        super(graphics.getLayer());

        this.basePoint = basePoint;
        this.uVector = uVector;
        this.vVector = vVector;

        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
    }


    /**
     * Implementation of DXFObject interface method; creates DXF text representing the DXF image.
     */
    public String toDXFString() {
        StringBuilder result = new StringBuilder("0\nIMAGE\n");

        // print out handle and superclass marker(s)
        result.append(super.toDXFString());

        // print out subclass marker
        result.append("100\nAcDbRasterImage\n");

        result.append("10\n" + setPrecision(basePoint.x) + "\n");
        result.append("20\n" + setPrecision(basePoint.y) + "\n");
        result.append("30\n" + setPrecision(basePoint.z) + "\n");

        result.append("11\n" + setPrecision(uVector.x) + "\n");
        result.append("21\n" + setPrecision(uVector.y) + "\n");
        result.append("31\n" + setPrecision(uVector.z) + "\n");

        result.append("12\n" + setPrecision(vVector.x) + "\n");
        result.append("22\n" + setPrecision(vVector.y) + "\n");
        result.append("32\n" + setPrecision(vVector.z) + "\n");

        result.append("13\n" + imageWidth + "\n");
        result.append("23\n" + imageHeight + "\n");

        // set image to be displayed
        result.append("70\n" + "1\n");

        // turn clipping off
        result.append("280\n" + "0\n");

        return result.toString();
    }

}
