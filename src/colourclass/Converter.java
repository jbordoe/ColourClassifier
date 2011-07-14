package colourclass;

/**
 *
 * @author Jesse Bordoe
 */
public class Converter {
    /**
     * map from RGB colour space to CIE(L*,a,b)
     * @return array containing L*, a and b components
     */
    public static double[] RGBtoCIE(double r, double g, double b){
        double[] xyz = RGBtoXYZ(r,g,b);
        return XYZtoCIE(xyz);
    }
    /**
     * map from CIE(L*,a,b) colour space to RGB
     * @return array containing R, G and B components
     */
    public static double[] CIEtoRGB(double l, double a, double b){
        double[] xyz = CIEtoXYZ(l,a,b);
        return XYZtoRGB(xyz);
    }

    /**
     * map from RGB colour space to XYZ
     * @return array containing X, Y and Z components
     */
    public static double[] RGBtoXYZ(double r, double g, double b){
        double red = r/255;
        double green = g/255;
        double blue = b/255;

        if ( red > 0.04045 ){
            red = Math.pow(( red + 0.055 ) / 1.055 ,2.4);
        } else {
            red = red / 12.92;
        }
        if ( green > 0.04045 ){
            green = Math.pow(( green + 0.055 ) / 1.055 ,2.4);
        } else {
            green = green / 12.92;
        }
        if ( blue > 0.04045 ) {
            blue = Math.pow(( blue + 0.055 ) / 1.055 ,2.4);
        } else {
            blue = blue / 12.92;
        }
        red = red * 100;
        green = green * 100;
        blue = blue * 100;
        double x = red * 0.4124 + green * 0.3576 + blue * 0.1805;
        double y = red * 0.2126 + green * 0.7152 + blue * 0.0722;
        double z = red * 0.0193 + green * 0.1192 + blue * 0.9505;

        return new double[]{x, y, z};
    }

     /**
     * map from XYZ colour space to CIE 1976(L*,a*,b*)
     * @return array containing L*, a and b components
     */
    public static double[] XYZtoCIE(double[]xyz){
        double ref_X =  95.047;
        double ref_Y = 100.000;
        double ref_Z = 108.883;

        double x = xyz[0] / ref_X;
        double y = xyz[1] / ref_Y;
        double z = xyz[2] / ref_Z;

        if ( x > 0.008856 ) {
            x = Math.pow(x,(double)1/3);
        } else {
            x = ( 7.787 * x ) + ( (double)16 / 116 );
        }
        if ( y > 0.008856 ) {
            y = Math.pow(y,(double)1/3);
        } else {
            y = ( 7.787 * y ) + ( (double)16 / 116 );
        }
        if ( z > 0.008856 ) {
            z = Math.pow(z,(double)1/3);
        } else {
            z = ( 7.787 * z ) + ( (double)16 / 116 );
        }
        double CIE_l = ( 116 * y ) - 16;
        double CIE_a = 500 * ( x - y );
        double CIE_b = 200 * ( y - z );
        return new double[]{CIE_l,CIE_a,CIE_b};
    }


    /**
     * Map this point from CIE(l*,a,b) colour space to XYZ
     * @return array containing X, Y and Z components
     */
    public static double[] CIEtoXYZ(double CIE_l, double CIE_a, double CIE_b) {
        double y = (CIE_l + 16) / 116;
        double x = CIE_a / 500 + y;
        double z = y - CIE_b / 200;

        if (Math.pow(y, 3) > 0.008856) {
            y = Math.pow(y, 3);
        } else {
            y = ( y - (double)16 / 116 ) / 7.787;
        }
        if ( Math.pow(x,3) > 0.008856 ) {
            x = Math.pow(x,3);
        } else {
            x = ( x - (double)16 / 116 ) / 7.787;
        }
        if ( Math.pow(z,3) > 0.008856 ) {
            z = Math.pow(z,3);
        } else {
            z = ( z - (double)16 / 116 ) / 7.787;
        }
        double ref_X =  95.047;
        double ref_Y = 100.000;
        double ref_Z = 108.883;

        x = ref_X * x;
        y = ref_Y * y;
        z = ref_Z * z;
        return new double[]{x,y,z};
    }

    /**
     * map this point from XYZ colour space to RGB
     * @param xyz
     * @return array containing R, G and B components
     */
    public static double[] XYZtoRGB(double[] xyz){
        double x = xyz[0];
        double y = xyz[1];
        double z = xyz[2];

        x = x/100;
        y = y/100;
        z = z/100;

        double red = x *  3.2406 + y * -1.5372 + z * -0.4986;
        double green = x * -0.9689 + y *  1.8758 + z *  0.0415;
        double blue = x *  0.0557 + y * -0.2040 + z *  1.0570;

        if ( red > 0.0031308 ) {
            red = 1.055 * Math.pow(red, (double)1 / 2.4 ) - 0.055;
        } else {
            red = 12.92 * red;
        }
        if ( green > 0.0031308 ) {
            green = 1.055 * Math.pow(green, (double)1 / 2.4 ) - 0.055;
        } else {
            green = 12.92 * green;
        }
        if ( blue > 0.0031308 ) {
            blue = 1.055 * Math.pow(blue, (double)1 / 2.4 ) - 0.055;
        } else {
            blue = 12.92 * blue;
        }
        red = red * 255;
        green = green * 255;
        blue = blue * 255;
        double[] rgb = {red,green,blue};

        for(int i = 0; i <3; i++){
            if(rgb[i] > 255){
                rgb[i] = 255;
            } else if(rgb[i] < 0){
                rgb[i] = 0;
            }
        }
        return rgb;
    }

}
