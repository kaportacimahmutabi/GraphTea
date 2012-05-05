// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

package graphtea.extensions.io;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.platform.parameter.Parameter;
import graphtea.platform.parameter.Parametrizable;
import graphtea.platform.core.exception.ExceptionHandler;
import graphtea.plugins.main.saveload.core.GraphIOException;
import graphtea.plugins.main.saveload.core.extension.GraphWriterExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author  Pooia Ronagh
 */
public class LatexCAD implements GraphWriterExtension, Parametrizable {

    public String getName() {
        return "LatexCAD";
    }

    public String getExtension() {
        return "pic";
    }

    public double min(double a, double b) {
        if (a < b) return a;
        else return b;
    }

    public double max(double a, double b) {
        if (a > b) return a;
        else return b;
    }

    @Parameter(name = "Width", description = "Width in percent.")
    public Double X = 0.0;
    @Parameter(name = "Height", description = "Height in percent.")
    public Double Y = 0.0;

    public void write(File file, GraphModel graph) throws GraphIOException {
        FileWriter output = null;
        try {
            output = new FileWriter(file);
            output.write("% Drawing generated by GraphTea - requires latexcad.sty which can be found in lib\\latexcad.zip, also you can get the latest version from internet\n" +
                    "% Pooya Ronagh (p.ronagh@gmail.com)\n");
//            X= Double.parseDouble((JOptionPane.showInputDialog(null, "Width in percent:")));
//            Y= Double.parseDouble((JOptionPane.showInputDialog(null, "Height in percent:")));
            X /= 100;
            Y /= 100;

            //set frame
            Iterator<Vertex> iv = graph.iterator();
            Vertex v = iv.next();
            double minX = v.getLocation().getX();
            double minY = v.getLocation().getY();
            double maxX = v.getLocation().getX();
            double maxY = v.getLocation().getY();
            while (iv.hasNext()) {
                v = iv.next();
                minX = min(minX, v.getLocation().getX());
                minY = min(minY, v.getLocation().getY());
                maxX = max(maxX, v.getLocation().getX());
                maxY = max(maxY, v.getLocation().getY());
            }
            double Dx = maxX - minX;
            double Dy = maxY - minY;

            //set picture
            output.write("\\vspace{1.cm}");
            output.write("\\begin{picture}(" + Dx * X + "," + Dy * Y + ")\n");

            //draw vertices
//\node[Nfill=y,fillcolor=Black,ExtNL=y,NLangle=0.0,NLdist=1.2,Nadjustdist=2.5
// ,Nw=5.03,Nh=4.5,Nmr=2.25](n17)(12.78,-4.47){jhgcvk}

            iv = graph.iterator();
            int i = 0;
            while (iv.hasNext()) {
                v = iv.next();
                i = i + 1;
                output.write("\\node");
                String attr = "";
                String disc = "";
                //check filled
                attr += "Nfill=y";
                //check color
                attr += ",fillcolor=Black";
                if (graph.isDrawVertexLabels()) {
                    attr += ",ExtNL=y,NLangle=0.0,NLdist=1.5,Nadjustdist=2.5";
                    disc += v.getLabel();
                }
                // check shape
                attr += ",Nw=" + 2.0;
                attr += ",Nh=" + 2.0;
                attr += ",Nmr=1.5";

                output.write("[" + attr + "]"
                        + "(n" + v.getId() + ")("
                        + X * v.getLocation().getX() + ","
                        + Y * v.getLocation().getY() + ")" +
                        "{" + disc + "}\n");
            }

            //draw edges
            Iterator<Edge> ie = graph.edgeIterator();
            while (ie.hasNext()) {
                Edge e = ie.next();
                output.write("\\drawedge");
                String attr = "";
                String disc = "";
                //check color
                attr += "linecolor=Black";
                //check width
                attr += ",linewidth=0.5";
                if (graph.isDirected()) {
                    attr += ",AHangle=29.74,AHLength=2.02,AHlength=1.75";
                } else {
                    attr += ",AHnb=0";
                }
                if (graph.isDrawEdgeLabels()) {
                    attr += ",ELdist=2.0";
                    disc += e.getLabel();
                }
                output.write("[" + attr + "]" +
                        "(n" + e.source.getId() + ",n" + e.target.getId() + ")" +
                        "{" + disc + "}\n");
            }

            // Edge.IsCurvedEdge()
            //Edge.CubicCurve.PathIterator


            output.write("\\end{picture}");
            output.close();

        } catch (IOException e) {
            ExceptionHandler.catchException(e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getDescription() {
        return "exports latex";
    }

    public String checkParameters() {
        if (X < 0 || Y < 0)
            return "X and Y should be positive";
        else
            return null;
    }
}