package albano;

import json.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlbanoParser {

    static List<Double> allowedOrientations = Arrays.asList(0.0, 180.0);
    static Double stripHeight = 4900.0;

    public static Instance parseInstance(File file) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        String name = "albano";

        NodeList lotList = doc.getElementsByTagName("lot");

        List<Item> items = new ArrayList<>();
        if (lotList.item(0).getNodeType() == Node.ELEMENT_NODE) {
            Element lot = (Element) lotList.item(0);

            NodeList pieces = lot.getChildNodes();

            for (int i = 0; i < pieces.getLength(); i++) {
                Node piece = pieces.item(i);
                if (piece.getNodeType() == Node.ELEMENT_NODE) {
                    Element pieceElement = (Element) piece;
                    Integer quantity = Integer.parseInt(pieceElement.getAttribute("quantity"));
                    String polygonID = ((Element) pieceElement.getElementsByTagName("component").item(0))
                            .getAttribute("idPolygon");

                    System.out.println(polygonID);

                    NodeList polygons = doc.getElementsByTagName("polygon");
                    Element polygon = null;
                    for (int j = 0; j < polygons.getLength(); j++) {
                        Element p = (Element) polygons.item(j);
                        if (p.getAttribute("id").equals(polygonID)) {
                            polygon = p;
                            break;
                        }
                    }
                    Element lines = (Element) polygon.getElementsByTagName("lines").item(0);
                    NodeList segments = lines.getChildNodes();

                    ArrayList<Point> points = new ArrayList<>();
                    for(int j = 0; j < segments.getLength(); j++){
                        if (segments.item(j).getNodeType() != Node.ELEMENT_NODE) continue;
                        Element segment = (Element) segments.item(j);
                        double x = Double.parseDouble(segment.getAttribute("x0"));
                        double y = Double.parseDouble(segment.getAttribute("y0"));
                        points.add(new Point(x, y));
                    }

                    Shape shape = new Shape(points, new ArrayList<>());
                    Item item = new Item(quantity, quantity, null, null, allowedOrientations, null, shape);
                    items.add(item);
                }
            }
        }
        Strip strip = new Strip(stripHeight);
        return new Instance(name, items, strip);
    }
}
