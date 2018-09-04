package com.github.skjolberg.packing;

public class Space extends WithDimensions {

	private Space parent;
    private Space remainder;

    private String name;
    private int width;
    private int depth;
    private int height;

    private int x; // width
    private int y; // depth
    private int z; // height

    public Space() {}

	public Space(Space parent, String name, int w, int d, int h, int x, int y, int z) {
		this.parent = parent;

		this.x = x;
		this.y = y;
		this.z = z;

		this.name = name;
        this.width = w;
        this.depth = d;
        this.height = h;
	}
	
	public Space(int w, int d, int h, int x, int y, int z) {
		this(null, null, w, d, h, x, y, z);
	}

    public void setRemainder(Space dual) {
        this.remainder = dual;
    }

    public Space getRemainder() {
        return remainder;
    }

	@Override
	public String toString() {
		return "Space [name=" + name + ", " + x + "x" + y + "x" + z + ", width=" + width + ", depth=" + depth + ", height="
				+ height + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((remainder == null) ? 0 : remainder.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Space other = (Space) obj;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (remainder == null) {
			if (other.remainder != null)
				return false;
		} else if (!remainder.equals(other.remainder))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}


    public Space getParent() {
        return parent;
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


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
