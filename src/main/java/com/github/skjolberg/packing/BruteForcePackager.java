package com.github.skjolberg.packing;

import java.util.List;

/**
 * Fit boxes into container, i.e. perform bin packing to a single container. 
 * <br><br>
 * This attempts a brute force approach, which is very demanding in terms of resources. 
 * For use in scenarios with 'few' boxes, where the complexity of a 'few' can be measured 
 * for a specific set of boxes and containers using 
 * {@linkplain PermutationRotationIterator#countPermutations()} * {@linkplain PermutationRotationIterator#countRotations()}.
 * <br><br>
 * Thread-safe implementation.
 */

public class BruteForcePackager extends Packager {

	/**
	 * Constructor
	 * 
	 * @param containers list of containers
	 * @param rotate3D whether boxes can be rotated in all three directions (two directions otherwise)
	 * @param binarySearch if true, the packager attempts to find the best box given a binary search. Upon finding a container that can hold the boxes, given time, it also tries to find a better match.
	 */
	
	public BruteForcePackager(List<? extends Dimension> containers, boolean rotate3D, boolean binarySearch) {
		super(containers, rotate3D, binarySearch);
	}
	
	/**
	 * Constructor
	 * 
	 * @param containers list of containers
	 */
	
	public BruteForcePackager(List<? extends Dimension> containers) {
		this(containers, true, true);
	}
	
	protected Container pack(List<Placement> placements, Dimension dimension, PermutationRotation[] rotations, long deadline) {
		
		PermutationRotationIterator rotator = new PermutationRotationIterator(dimension, rotations);

		return pack(placements, dimension, rotator, deadline);
	}

	public Container pack(Dimension container, PermutationRotationIterator rotator, long deadline) {
		return pack(getPlacements(rotator.length()), container, rotator, deadline);
	}

	public Container pack(List<Placement> placements, Dimension container, PermutationRotationIterator rotator, long deadline) {
		
		Container holder = new Container(container);

		// iterator over all permutations
		do {
			if(System.currentTimeMillis() > deadline) {
				break;
			}
			// iterator over all rotations
			
			fit:
			do {
				Dimension remainingSpace = container;

				int index = 0;
				while(index < rotator.length()) {
					if(System.currentTimeMillis() > deadline) {
						// fit2d below might have returned due to deadline
						return null;
					}
					
					if(!rotator.isWithinHeight(index, remainingSpace.getHeight())) {
						// clean up
						holder.clear();
						
						continue fit;
					}

					Box box = rotator.get(index);
					
					Placement placement = placements.get(index);
					Space levelSpace = placement.getSpace();
					levelSpace.width = container.getWidth();
					levelSpace.depth = container.getDepth();
					levelSpace.height = remainingSpace.getHeight();
					
					placement.setBox(box);
					
					levelSpace.x = 0;
					levelSpace.y = 0;
					levelSpace.z = holder.getStackHeight();
					
					levelSpace.setParent(null);
					levelSpace.getRemainder().setParent(null);
					
					holder.addLevel();

					index++;
					
					index = fit2D(rotator, index, placements, holder, placement, deadline);
					
					// update remaining space
					remainingSpace = holder.getFreeSpace();
				}

				if(accept(holder)) {
					return holder;
				}
				holder.clear();
			} while(rotator.nextRotation());
		} while(rotator.nextPermutation());
		
		return null;
	}

	protected boolean accept(Container container) {
		return true;
	}

	protected int fit2D(PermutationRotationIterator rotator, int index, List<Placement> placements, Container holder, Placement usedSpace, long deadline) {
		// add used space box now
		// there is up to possible 2 free spaces
		holder.add(usedSpace);

		if(index >= rotator.length()) {
			return index;
		}

		if(System.currentTimeMillis() > deadline) {
			return index;
		}

		Box nextBox = rotator.get(index);
		Placement nextPlacement = placements.get(index);

		nextPlacement.setBox(nextBox);
		
		if(!isFreespace(usedSpace.getSpace(), usedSpace.getBox(), nextPlacement)) {
			// no additional boxes
			// just make sure the used space fits in the free space
			return index;
		} 
		
		index++;
		// the correct space dimensions is copied into the next placement

		// attempt to fit in the remaining (usually smaller) space first
		
		// stack in the 'sibling' space - the space left over between the used box and the selected free space
		if(index < rotator.length()) {
			Space remainder = nextPlacement.getSpace().getRemainder();
			if(remainder.nonEmpty()) {
				Box box = rotator.get(index);

				if(remainder.getWidth() >= box.getWidth() && remainder.getHeight() >= box.getHeight() && remainder.getDepth() >= box.getDepth()) {
					Placement placement = placements.get(index);
					placement.setBox(box);

					index++;
					
					placement.getSpace().copyOf(remainder);
					placement.getSpace().setParent(remainder);
					placement.getSpace().getRemainder().setParent(remainder);
					
					index = fit2D(rotator, index, placements, holder, placement, deadline);
				}
			}
		}
	
		// fit the next box in the selected free space
		return fit2D(rotator, index, placements, holder, nextPlacement, deadline);
	}	

	protected boolean isFreespace(Space freespace, Box used, Placement target) {

		// Two free spaces, on each rotation of the used space. 
		// Height is always the same, used box is assumed within free space height.
		// First:
		// ........................  ........................  .............
		// .                      .  .                      .  .           .
		// .                      .  .                      .  .           .
		// .          A           .  .          A           .  .           .
		// .                      .  .                      .  .           .
		// .                B     .  .                      .  .    B      .
		// ............           .  ........................  .           .
		// .          .           .                            .           .
		// .          .           .                            .           .
		// ........................                            .............
        //
		// So there is always a 'big' and a 'small' leftover area (the small is not shown).
		if(freespace.getWidth() >= used.getWidth() && freespace.getDepth() >= used.getDepth()) {
			
			// if B is empty, then it is sufficient to work with A and the other way around
			int b = (freespace.getWidth() - used.getWidth()) * freespace.getDepth();
			int a = freespace.getWidth() * (freespace.getDepth() - used.getDepth());
			
			// pick the one with largest footprint.
			if(b >= a) {
				if(b > 0 && b(freespace, used, target)) {
					return true;
				}
				
				return a > 0 && a(freespace, used, target);
			} else {
				if(a > 0 && a(freespace, used, target)) {
					return true;
				}

				return b > 0 && b(freespace, used, target);
			}
		}
		return false;
	}

	private boolean a(Space freespace, Box used, Placement target) {
		if(freespace.getWidth() >= target.getBox().getWidth() && freespace.getHeight() >= target.getBox().getHeight() && freespace.getDepth() - used.getDepth() >= target.getBox().getDepth()) {
			target.getSpace().copyOf(
					freespace.getWidth(), freespace.getDepth() - used.getDepth(), freespace.getHeight(),
					freespace.getX(), freespace.getY() + used.getDepth(), freespace.getHeight()
				);
			target.getSpace().getRemainder().copyOf(
					freespace.getWidth() - used.getWidth(), used.getDepth(), freespace.getHeight(),
					freespace.getX() + used.getWidth(), freespace.getY(), freespace.getZ()
				);
			target.getSpace().setParent(freespace);
			target.getSpace().getRemainder().setParent(freespace);
			
			return true;
		}
		return false;
	}

	private boolean b(Space freespace, Box used, Placement target) {
		if(freespace.getWidth() - used.getWidth() >= target.getBox().getWidth() && freespace.getHeight() >= target.getBox().getHeight() && freespace.getDepth() >= target.getBox().getDepth()) {
			// we have a winner
			target.getSpace().copyOf(
					freespace.getWidth() - used.getWidth(), freespace.getDepth(), freespace.getHeight(),
					freespace.getX() + used.getWidth(), freespace.getY(), freespace.getZ()
					);
			
			target.getSpace().getRemainder().copyOf(
					used.getWidth(), freespace.getDepth() - used.getDepth(), freespace.getHeight(),
					freespace.getX(), freespace.getY()+ used.getDepth(), freespace.getZ()
					);
			
			target.getSpace().setParent(freespace);
			target.getSpace().getRemainder().setParent(freespace);
			return true;
		}
		return false;
	}

	@Override
	protected Adapter adapter(List<BoxItem> boxes) {
		// instead of placing boxes, work with placements
		// this very much reduces the number of objects created
		// performance gain is something like 25% over the box-centric approach
		
		final PermutationRotation[] rotations = PermutationRotationIterator.toRotationMatrix(boxes, rotate3D);
		
		int count = 0;
		for (PermutationRotation permutationRotation : rotations) {
			count += permutationRotation.getCount();
		}
		
		final List<Placement> placements = getPlacements(count);

		return new Adapter() {
			@Override
			public Container pack(List<BoxItem> boxes, Dimension dimension, long deadline) {
				return BruteForcePackager.this.pack(placements, dimension, rotations, deadline);
			}
		};
	}

}
