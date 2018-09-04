package com.github.skjolberg.packing;

public class Box extends WithDimensions {

    private final String name;
    private final int width;
    private final int depth;
    private final int height;
    private final long volume;
    private final int footPrint;

	public Box(Dimension dimension) {
	    this(null, dimension.getWidth(), dimension.getDepth(), dimension.getHeight());
	}

	public Box(int width, int depth, int height) {
        this(null, width, depth, height);
	}

	public Box(String name, int width, int depth, int height) {
	    this.name = name;
	    this.width = width;
        this.depth = depth;
	    this.height = height;

	    this.volume = width * depth * height;
	    this.footPrint = width * depth;
	}

    /**
     *
     * Rotate box, i.e. in 2 dimensions, keeping the height constant.
     *
     * @return this
     */
    public Box rotate2D() {
        return new Box(name, depth, width, height);
    }

	/**
	 * 
	 * Rotate box, i.e. in 3D
	 * src/main/java/com/github/skjolberg/packing/LargestAreaFitFirstPackager.java
	 * @return this instance
	 */

	public Box rotate3D() {
		return new Box(name, depth, height, width);
	}

    public Box rotate2D3D() {
        //rotate2D();
        // width -> depth
        // depth -> width

        //rotate3D();
        // height = width;
        // width = depth;
        // depth = height;

        // so
        // height -> width -> depth;
        // width -> depth -> width;
        // depth -> height;

        return new Box(name, width, height, depth);
    }

	protected Box clone() {
        return new Box(name, width, depth, height);
    }

    @Override
    public String toString() {
        return "Box [name=" + name + ", width=" + width + ", depth=" + depth + ", height=" + height + ", volume="
                + volume + "]";
    }

    Box fitRotate2D(int w, int d) {
        if(w >= width && d >= depth) {
            return this;
        }

        if(d >= width && w >= depth) {
            return this.rotate2D();
        }

        return null;
    }

    Box fitRotate3DSmallestFootprint(int w, int d, int h) {
        int a = Integer.MAX_VALUE;
        if(heightUp(w, d, h)) {
            a = width * depth;
        }

        int b = Integer.MAX_VALUE;
        if(widthUp(w, d, h)) {
            b = height * depth;
        }

        int c = Integer.MAX_VALUE;
        if(depthUp(w, d, h)) {
            c = width * height;
        }

        if(a == Integer.MAX_VALUE && b == Integer.MAX_VALUE && c == Integer.MAX_VALUE) {
            return null;
        }

        Box newBox;
        if(a < b && a < c) {
            newBox = this;
        } else if(b < c) {
            newBox = this.rotate3D();
        } else {
            newBox = this.rotate3D().rotate3D();
        }

        if(h < newBox.height) {
            throw new IllegalArgumentException("Expected height " + newBox.height + " to fit within height constraint " + h);
        }

        if(newBox.width > w || newBox.depth > d) {
            // use the other orientation
            newBox = newBox.rotate2D();
        }

        if(newBox.width > w || newBox.depth > d) {
            throw new IllegalArgumentException("Expected width " + newBox.width + " and depth " + newBox.depth + " to fit within constraint width " + w + " and depth " + d);
        }

        return newBox;
    }

    private boolean heightUp(int w, int d, int h) {
        if(h < height) {
            return false;
        }
        return (d >= width && w >= depth) || (w >= width && d >= depth);
    }

    private boolean widthUp(int w, int d, int h) {
        if(h < width) {
            return false;
        }
        return (d >= height && w >= depth) || (w >= height && d >= depth);
    }

    private boolean depthUp(int w, int d, int h) {
        if(h < depth) {
            return false;
        }
        return (d >= height && w >= width) || (w >= height && d >= width);
    }

    public String getName() {
        return name;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public long getVolume() {
        return volume;
    }

    public int getFootprint() {
        return footPrint;
    }
}