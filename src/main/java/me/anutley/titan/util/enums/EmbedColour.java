package me.anutley.titan.util.enums;

import java.awt.*;

public enum EmbedColour {

    NEUTRAL("#008aff"),
    YES("#2ECC71"),
    NO("#FF0000"),
    MODERATION_LOGGING("#94C0DB");

    public final String colour;

    EmbedColour(String colour) {
        this.colour = colour;
    }

    public Color getColour() {
        return Color.decode(colour);
    }
}
