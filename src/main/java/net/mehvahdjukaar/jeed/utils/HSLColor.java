package net.mehvahdjukaar.jeed.utils;

import net.minecraft.util.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class HSLColor {

    public static int getProcessedColor(int rgb) {
        float[] hsl = HSLColor.rgbToHsl(rgb);

        hsl = HSLColor.postProcess(hsl);
        float h = hsl[0];
        float s = hsl[1];
        float l = hsl[2];
        s *= 0.81f;
        return HSLColor.hslToRgb(h, s, l);
    }

    public static float[] postProcess(float[] hsl) {
        float h = hsl[0];
        float s = hsl[1];
        float l = hsl[2];
        //map one to one. no effect on its own
        float c = 1 - Math.abs((2 * l) - 1);
        if (s > c) s = c;

        //remove darker colors
        float minLightness = 0.47f;
        l = Math.max(l, minLightness);

        //saturate dark colors
        float j = (1 - l);
        //if(s<j)s=j;


        //desaturate blue
        float scaling = 0.15f;
        float angle = 90;
        float n = (float) (scaling * Math.exp(-angle * Math.pow((h - 0.6666f), 2)));
        s -= n;


        return new float[]{h, s, l};
    }


    //https://stackoverflow.com/questions/2353211/hsl-to-rgb-color-conversion

    /**
     * Converts an HSL color value to RGB. Conversion formula
     * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
     * Assumes h, s, and l are contained in the set [0, 1] and
     * returns r, g, and b in the set [0, 255].
     *
     * @param h The hue
     * @param s The saturation
     * @param l The lightness
     * @return int array, the RGB representation
     */
    public static int hslToRgb(float h, float s, float l) {
        float r, g, b;

        if (s == 0f) {
            r = g = b = l; // achromatic
        } else {
            float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1f / 3f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f / 3f);
        }
        return ColorHelper.PackedColor.color(
                MathHelper.floor(255), MathHelper.floor(r * 255), MathHelper.floor(g * 255), MathHelper.floor(b * 255));
    }


    /**
     * Helper method that converts hue to rgb
     */
    public static float hueToRgb(float p, float q, float t) {
        if (t < 0f)
            t += 1f;
        if (t > 1f)
            t -= 1f;
        if (t < 1f / 6f)
            return p + (q - p) * 6f * t;
        if (t < 1f / 2f)
            return q;
        if (t < 2f / 3f)
            return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }

    public static float[] rgbToHsl(int rgb) {
        int r = ColorHelper.PackedColor.red(rgb);
        int g = ColorHelper.PackedColor.green(rgb);
        int b = ColorHelper.PackedColor.blue(rgb);
        return rgbToHsl(r, g, b);
    }

    /**
     * Converts an RGB color value to HSL. Conversion formula
     * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
     * Assumes pR, pG, and bpBare contained in the set [0, 255] and
     * returns h, s, and l in the set [0, 1].
     *
     * @param pR The red color value
     * @param pG The green color value
     * @param pB The blue color value
     * @return float array, the HSL representation
     */
    public static float[] rgbToHsl(int pR, int pG, int pB) {
        float r = pR / 255f;
        float g = pG / 255f;
        float b = pB / 255f;

        float max = (r > g && r > b) ? r : Math.max(g, b);
        float min = (r < g && r < b) ? r : Math.min(g, b);

        float h, s, l;
        l = (max + min) / 2.0f;

        if (max == min) {
            h = s = 0.0f;
        } else {
            float d = max - min;
            s = (l > 0.5f) ? d / (2.0f - max - min) : d / (max + min);

            if (r > g && r > b)
                h = (g - b) / d + (g < b ? 6.0f : 0.0f);

            else if (g > b)
                h = (b - r) / d + 2.0f;

            else
                h = (r - g) / d + 4.0f;

            h /= 6.0f;
        }
        return new float[]{h, s, l};
    }

}
