package edu.harvard.i2b2.timeline.external;

public interface BevelStyle {
    // --------------------------------------------------
    // constants
    // --------------------------------------------------

    /**
     * Defines the "lowered" shape border style.
     */
    public static final int BEVEL_LOWERED = 0;

    /**
     * Defines the "raised" shape border style.
     */
    public static final int BEVEL_RAISED = 1;

    /**
     * Defines the "line" shape border style.
     */
    public static final int BEVEL_LINE = 2;

    /**
     * Defines the "line" shape border style.
     */
    public static final int BEVEL_NONE = 3;

    // --------------------------------------------------
    // methods
    // --------------------------------------------------

    public void setBevelStyle(int style);

    public int getBevelStyle();
}
