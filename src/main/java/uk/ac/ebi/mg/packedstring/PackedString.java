package uk.ac.ebi.mg.packedstring;

import uk.ac.ebi.mg.packedstring.DualBandString.PackingImpossibleException;


public abstract class PackedString {

    private static boolean packingEnabled = false;
    private static int packingThreshold = 10;

    private static final String enableProperty = "packedstring.enable";
    private static final String thresholdProperty = "packedstring.threshold";

    static {
        String val = System.getProperty(enableProperty);

        if (val != null && (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes") || val.equals("1"))) {
            packingEnabled = true;
        }

        val = System.getProperty(thresholdProperty);

        if (val != null) {
            try {
                packingThreshold = Integer.parseInt(val);

                if (packingThreshold < 0) {
                    packingThreshold = 0;
                }
            } catch (Exception e) {
            }
        }
    }

    protected PackedString() {
    }

    public abstract int length();

    public static Object pack(String str) {
        if (!packingEnabled) {
            return str;
        }

        int len = str.length();

        if (len < packingThreshold) {
            return str;
        }

        char top, bottom;

        bottom = top = str.charAt(0);

        for (int i = 1; i < len; i++) {
            char ch = str.charAt(i);

            if (ch < bottom) {
                bottom = ch;
            } else if (ch > top) {
                top = ch;
            }
        }

        if (top - bottom < 256) {
            return new SingleBandString(str, bottom);
        }

        try {
            return new DualBandString(str, bottom, top);
        } catch (PackingImpossibleException e) {
        }

        return str;
    }


}
