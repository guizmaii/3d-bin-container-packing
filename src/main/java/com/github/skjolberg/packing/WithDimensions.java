package com.github.skjolberg.packing;

abstract class WithDimensions {

    abstract int getWidth();
    abstract int getDepth();
    abstract int getHeight();

    /**
     *
     * Check whether a dimension fits within the current dimensions, rotated in 3D.
     *
     * @param dimension the space to fit
     * @return true if any rotation of the argument can be placed inside this
     *
     */
    public boolean canHold3D(WithDimensions dimension) {
        return canHold3D(dimension.getWidth(), dimension.getDepth(), dimension.getHeight());
    }

    boolean canHold3D(int w, int d, int h) {
        return (w <= getWidth() && h <= getHeight() && d <= getDepth()) ||
                (h <= getWidth() && d <= getHeight() && w <= getDepth()) ||
                (d <= getWidth() && w <= getHeight() && h <= getDepth()) ||
                (h <= getWidth() && w <= getHeight() && d <= getDepth()) ||
                (d <= getWidth() && h <= getHeight() && w <= getDepth()) ||
                (w <= getWidth() && d <= getHeight() && h <= getDepth());
    }

    boolean isSquare2D() {
        return getWidth() == getDepth();
    }

    boolean isSquare3D() {
        return getWidth() == getDepth() && getWidth() == getHeight();
    }

    /**
     *
     * Check whether a dimension fits within the current object, rotated in 2D.
     *
     * @param dimension the dimension to fit
     * @return true if any rotation of the argument can be placed inside this
     *
     */

    boolean canHold2D(WithDimensions dimension) {
        return canHold2D(dimension.getWidth(), dimension.getDepth(), dimension.getHeight());
    }

    private boolean canHold2D(int w, int d, int h) {
        if(h > getHeight()) {
            return false;
        }
        return (w <= getWidth() && d <= getDepth()) || (d <= getWidth() && w <= getDepth());
    }

    boolean nonEmpty() {
        return getWidth() > 0 && getDepth() > 0 && getHeight() > 0;
    }

}
