/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package colourclass;

/**
 * Represents the center of a cluster. Unlike <code>DataPoint objetcs</code>, these are defined primarily in the CIE(L*,a,b)
 * colour space, with methods to convert into RGB for output
 * @author Jesse
 */
public class Center extends DataPoint {
    /* Unlike image pixels, centers may exist at non-integer intervals in each dimension */
    private double x_dim;
    private double y_dim;
    private double r_dim;
    private double g_dim;
    private double b_dim;



    public Center(int[] rgb, int x, int y){
        super(rgb,x,y);
        this.x_dim = x;
        this.y_dim = y;
        this.r_dim = rgb[0];
        this.g_dim = rgb[1];
        this.b_dim = rgb[2];
    }

    public Center(String cSpace, double[] vals, double x , double y){
        if(cSpace.equals("CIE")){
            this.CIE_l = vals[0];
            this.CIE_a = vals[1];
            this.CIE_b = vals[2];
            this.x_dim = x;
            this.y_dim = y;
        } else if (cSpace.equals("RGB")){

        }
    }
    @Override
     public void CIEtoRGB(){
        double[] rgb = Converter.CIEtoRGB(CIE_l,CIE_a,CIE_b);
        r_dim = rgb[0];
        g_dim = rgb[1];
        b_dim = rgb[2];
    }

     /**
     * @return String representation of RBG colour for use in CSS/HTML
     */
    @Override
    public String toHexTriplet(){
        CIEtoRGB();
        double[] rgb = {r_dim,g_dim,b_dim};
        String triplet = "#";
        for (int i=0; i<3; i++){
            int rounded = (int)Math.round(rgb[i]);
            if(rounded > 255){ rounded = 255;}
            else if(rounded < 0){ rounded = 0;}
            String col = java.lang.Integer.toHexString(rounded);
            if (col.length()==1){
                col = "0"+col;
            }
            triplet = triplet+col;
        }
        return triplet;
    }

    /**
     * @return the argb
     */
    @Override
    public int getArgb() {
        CIEtoRGB();
        int argb = (0xff << 24) | ((int)r_dim << 16) | ((int)g_dim << 8) | (int)b_dim;
        return argb;
    }




}
