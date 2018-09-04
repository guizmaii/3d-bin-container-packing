package com.github.skjolberg.packing;

import java.util.ArrayList;
import java.util.List;

/**
 * A level within a container
 * 
 */

public class Level {

	private final List<Placement> placements;

	Level() {
		this.placements = new ArrayList<>();
	}

	private Level(List<Placement> placements) {
		this.placements = placements;
	}

	Level add(Placement placement) {
		List<Placement> newPlacements = new ArrayList<>(placements);
		newPlacements.add(placement);

		return new Level(newPlacements);
	}

	Placement get(int index) {
		return placements.get(index);
	}

	int size() {
		return placements.size();
	}

	public List<Placement> getPlacements() {
		return placements;
	}

	public int getHeight() {
		int height = 0;

		for(Placement placement : placements) {
			Box box = placement.getBox();
			if(box.getHeight() > height) {
				height = box.getHeight();
			}
		}
		
		return height;
	}
	
	/**
	 * 
	 * Check whether placement is valid, i.e. no overlaps.
	 * 
	 */

	void validate() {
		for(int i = 0; i < placements.size(); i++) {
			for(int j = 0; j < placements.size(); j++) {
				if(j == i) {
					if(!placements.get(i).intercets(placements.get(j))) {
						throw new IllegalArgumentException();
					}
				} else {
					if(placements.get(i).intercets(placements.get(j))) {
						throw new IllegalArgumentException(i + " vs " + j);
					}
				}
			}
		}		
	}
	
}