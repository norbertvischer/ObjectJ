package ij.gui;

import java.awt.*;
import java.awt.image.*;
import ij.*;

/** Freehand region of interest or freehand line of interest*/
public class FreehandRoi extends PolygonRoi {

	public FreehandRoi(int sx, int sy, ImagePlus imp) {
		super(sx, sy, imp);
		if (Toolbar.getToolId()==Toolbar.FREEROI)
			type = FREEROI;
		else
			type = FREELINE;
		if (nPoints==2) nPoints--;
	}

	protected void grow(int sx, int sy) {
		if (subPixelResolution() && xpf!=null) {
			growFloat(sx, sy);
			return;
		}
		int ox = ic.offScreenX(sx);
		int oy = ic.offScreenY(sy);
		if (ox<0) ox = 0;
		if (oy<0) oy = 0;
		if (ox>xMax) ox = xMax;
		if (oy>yMax) oy = yMax;
		if (ox!=xp[nPoints-1]+x || oy!=yp[nPoints-1]+y) {
			xp[nPoints] = ox-x;
			yp[nPoints] = oy-y;
			nPoints++;
                        if (IJ.altKeyDown())
                            wipeBack();
			if (nPoints==xp.length)
				enlargeArrays();
			drawLine();
		}
	}

    //Mouse behaves like an eraser when moved backwards with alt key down
    //mouse is at point p3
    //go back and find point p1 (where path entered correction circle last time) 
    //check if any intermediate vertex forms a sharp angle (p1-p2-p3), and if so remove it
    //repeat this with new path until all sharp vertices are removed
    //N. Vischer
    private void wipeBackOldAndGood() {
        double correctionRadius =  20/ic.getMagnification();
        boolean found = false;
        int p3 = nPoints - 1;
        int p1 = p3 - 1;
        while (p1 > 1 && !found) {
            double dx = xp[p3] - xp[p1];
            double dy = yp[p3] - yp[p1];
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist > correctionRadius) {
                found = true;
            } else {
                p1--;
            }
        }
        if (found) {
            int sharpPt = 0;
            do {
                sharpPt = 0;
                for (int p2 = p1 + 1; p2 < p3 && sharpPt == 0; p2++) {
                    double dotproduct = (xp[p3] - xp[p2]) * (xp[p1] - xp[p2]) + (yp[p3] - yp[p2]) * (yp[p1] - yp[p2]);
                    double crossproduct = (xp[p3] - xp[p2]) * (yp[p1] - yp[p2]) - (yp[p3] - yp[p2]) * (xp[p1] - xp[p2]);

                    double angle = (180 / Math.PI) * Math.atan2(crossproduct, dotproduct);

                    if (Math.abs(angle) <= 90) {
                        sharpPt = p2;
                    }
                }
                if (sharpPt > 0) {
                    xp[sharpPt] = xp[p3];
                    yp[sharpPt] = yp[p3];
                    p3 = sharpPt;
                    nPoints = sharpPt + 1;
                }
            } while (sharpPt > 0);
        }
    }

    
    //Mouse behaves like an eraser when moved backwards with alt key down
    //mouse is at point p3
    //go back and find point p1 (where path entered correction circle last time) 
    //check if any intermediate vertex forms a sharp angle (p1-p2-p3), and if so remove it
    //repeat this with new path until all sharp vertices are removed
    //N. Vischer
    private void wipeBackBetter() {
        double correctionRadius = 20 / ic.getMagnification();
        boolean found = false;
        int p3 = nPoints - 1;
        int p1 = p3 - 1;
        while (p1 > 1 && !found) {
            double dx = xp[p3] - xp[p1];
            double dy = yp[p3] - yp[p1];
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist > correctionRadius) {
                found = true;
            } else {
                p1--;
            }
        }
        if (found) {
            for (int b = p1 + 1; b < p3; b++) {
                int a = b - 1;
                int c = b + 1;
                double dotproduct = (xp[c] - xp[b]) * (xp[a] - xp[b]) + (yp[c] - yp[b]) * (yp[a] - yp[b]);
                double crossproduct = (xp[c] - xp[b]) * (yp[a] - yp[b]) - (yp[c] - yp[b]) * (xp[a] - xp[b]);

                double angle = (180 / Math.PI) * Math.atan2(crossproduct, dotproduct);

                if (Math.abs(angle) <= 90 || crossproduct == 0) {
                    
                    xp[b] = xp[p3];
                yp[b] = yp[p3];
                nPoints = b+1;
                }
            }
           
    }
}

        
                
                
	private void growFloat(int sx, int sy) {
		double ox = ic.offScreenXD(sx);
		double oy = ic.offScreenYD(sy);
		if (ox<0.0) ox = 0.0;
		if (oy<0.0) oy = 0.0;
		if (ox>xMax) ox = xMax;
		if (oy>yMax) oy = yMax;
		double xbase = getXBase();
		double ybase = getYBase();
		if (ox!=xpf[nPoints-1]+xbase || oy!=ypf[nPoints-1]+ybase) {
			xpf[nPoints] = (float)(ox-xbase);
			ypf[nPoints] = (float)(oy-ybase);
			nPoints++;
			if (nPoints==xpf.length)
				enlargeArrays();
			drawLine();
		}
	}
	
	void drawLine() {
		int x1, y1, x2, y2;
		if (xpf!=null) {
			x1 = (int)xpf[nPoints-2]+x;
			y1 = (int)ypf[nPoints-2]+y;
			x2 = (int)xpf[nPoints-1]+x;
			y2 = (int)ypf[nPoints-1]+y;
		} else {
			x1 = xp[nPoints-2]+x;
			y1 = yp[nPoints-2]+y;
			x2 = xp[nPoints-1]+x;
			y2 = yp[nPoints-1]+y;
		}
		int xmin = Math.min(x1, x2);
		int xmax = Math.max(x1, x2);
		int ymin = Math.min(y1, y2);
		int ymax = Math.max(y1, y2);
		int margin = 4;
		if (lineWidth>margin && isLine())
			margin = lineWidth;
		if (ic!=null) {
			double mag = ic.getMagnification();
			if (mag<1.0) margin = (int)(margin/mag);
		}
		imp.draw(xmin-margin, ymin-margin, (xmax-xmin)+margin*2, (ymax-ymin)+margin*2);
	}

	protected void handleMouseUp(int screenX, int screenY) {
		if (state==CONSTRUCTING) {
            addOffset();
			finishPolygon();
        }
		state = NORMAL;
	}

}
