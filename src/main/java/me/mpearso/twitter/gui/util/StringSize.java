package me.mpearso.twitter.gui.util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class StringSize {

    private final static FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(
            new AffineTransform(), true, true);

    public final static Font DEFAULT_FONT = new Font(null, Font.PLAIN, 20);

    public static Rectangle2D getStringBounds(String string, Font font) {
        return font.getStringBounds(string, FONT_RENDER_CONTEXT);
    }
}
