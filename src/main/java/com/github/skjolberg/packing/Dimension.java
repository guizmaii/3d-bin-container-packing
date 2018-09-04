package com.github.skjolberg.packing;

public class Dimension extends WithDimensions  {

    public static final Dimension EMPTY = new Dimension(0, 0, 0);

    public static Dimension decode(String size) {
		String[] dimensions = size.split("x");
		
		return newInstance(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]), Integer.parseInt(dimensions[2]));
	}

	public static String encode(int width, int depth, int height) {
		return width + "x" + depth + "x" + height;
	}

	public static Dimension newInstance(int width, int depth, int height) {
		return new Dimension(width, depth, height);
	}
	
	private final int width; // x
    private final int depth; // y
    private final int height; // z
    private final long volume;
	
	private final String name;
	
	public Dimension(String name, int w, int d, int h) {
		this.name = name;
		
		this.depth = d;
		this.width = w;
		this.height = h;

		this.volume = ((long)depth) * ((long)width) * ((long)height);
	}
	
	public Dimension(int w, int d, int h) {
		this(null, w, d, h);
	}

	@Override
	public String toString() {
		return "Dimension [width=" + width + ", depth=" + depth + ", height=" + height + ", volume=" + volume + "]";
	}

	public String encode() {
		return encode(width, depth, height);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + depth;
		result = prime * result + height;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (volume ^ (volume >>> 32));
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dimension other = (Dimension) obj;
		if (depth != other.depth)
			return false;
		if (height != other.height)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (volume != other.volume)
			return false;
		if (width != other.width)
			return false;
		return true;
	}


	public long getVolume() {
		return volume;
	}

	public String getName() {
		return name;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getDepth() {
		return depth;
	}
}