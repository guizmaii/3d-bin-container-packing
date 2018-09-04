package com.github.skjolberg.packing;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class Container extends WithDimensions {

	private final String name;

	private final int width;
	private final int depth;
	private final int height;

	private final int stackHeight;
	private final List<Level> levels;

	public Container(Dimension dimension) {
		this(dimension.getName(), dimension.getWidth(), dimension.getDepth(), dimension.getHeight());
	}

	public Container(int w, int d, int h) {
		this(null, w, d, h);
	}

	public Container(String name, int w, int d, int h) {
        this.name = name;
        this.width = w;
        this.depth = d;
        this.height = h;

        levels = new ArrayList<>();
        stackHeight = 0;
    }

    private Container(String name, int w, int d, int h, int stackHeight, List<Level> levels) {
        this.name = name;
        this.width = w;
        this.depth = d;
        this.height = h;

        this.levels = levels;
        this.stackHeight = stackHeight;
    }

	public Container add(Level element) {
        return add(levels.size(), element); // add at the end.
	}

    public Container add(int index, Level element) {
        int newStackHeight = stackHeight;

        if(!levels.isEmpty()) {
            newStackHeight += levels.get(levels.size() - 1).getHeight();
        }

        List<Level> newLevels = new ArrayList<>(levels);
        newLevels.add(index, element);

        return new Container(name, width, depth, height, newStackHeight, newLevels);
    }


    public Container add(Placement placement) {
	    int lastIndex = levels.size() - 1;
        Level newLevel = levels.get(lastIndex).add(placement);
        List<Level> newLevels = new ArrayList<>(levels);
        newLevels.set(lastIndex, newLevel);

        return new Container(name, width, depth, height, stackHeight, newLevels);
    }


	public void addLevel() {
		add(new Level());
	}
	
	public Dimension getFreeSpace() {
		int spaceHeight = height - stackHeight;
		if(spaceHeight < 0) {
			throw new IllegalArgumentException("Remaining free space is negative at " + spaceHeight);
		}
		return new Dimension(width, depth, spaceHeight);
	}
	
	List<Level> getLevels() {
		return levels;
	}
	
	public Placement get(int level, int placement) {
		return levels.get(level).get(placement);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((levels == null) ? 0 : levels.hashCode());
		result = prime * result + stackHeight;
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
		Container other = (Container) obj;
		if (levels == null) {
			if (other.levels != null)
				return false;
		} else if (!levels.equals(other.levels))
			return false;
		if (stackHeight != other.stackHeight)
			return false;
		return true;
	}
	
	public int getBoxCount() {
		int count = 0;
		for(Level level : levels) {
			count += level.size();
		}
		return count;
	}

	public Dimension getUsedSpace() {
		Dimension maxBox = Dimension.EMPTY;
		int height = 0;
		for (Level level : levels) {
			maxBox = getUsedSpace(level, maxBox, height);
			height += level.getHeight();
		}
		return maxBox;
	}

	private Dimension getUsedSpace(Level level, Dimension maxBox, int height) {
		for (Placement placement : level.getPlacements()) {
			maxBox = boundingBox(maxBox, getUsedSpace(placement, height));
		}
		return maxBox;
	}

	private Dimension getUsedSpace(Placement placement, int height) {
		final Box box = placement.getBox();
		final Space space = placement.getSpace();
		return new Dimension(
				space.getX() + box.getWidth(),
				space.getY() + box.getDepth(),
				height + box.getHeight());
	}

	private Dimension boundingBox(final Dimension b1, final Dimension b2) {
		return new Dimension(
				max(b1.getWidth(), b2.getWidth()),
				max(b1.getDepth(), b2.getDepth()),
				max(b1.getHeight(), b2.getHeight()));

	}

    @Override
    int getWidth() {
        return width;
    }

    @Override
    int getDepth() {
        return depth;
    }

    @Override
    int getHeight() {
        return height;
    }

    String getName() {
        return name;
    }
}