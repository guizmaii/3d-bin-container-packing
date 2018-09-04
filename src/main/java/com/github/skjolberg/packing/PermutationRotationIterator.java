package com.github.skjolberg.packing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * Rotation and permutations built into the same class. Minimizes the number of
 * rotations. <br>
 * <br>
 * The maximum number of combinations is n! * 6^n, however after accounting for
 * bounds and sides with equal lengths the number can be a lot lower (and this
 * number can be obtained before starting the calculation). <br>
 * <br>
 * Assumes a do-while approach:
 * 
 * <pre>{@code 
 * do {
 * 	do {
 * 		for (int i = 0; i < n; i++) {
 * 			Box box = instance.get(i);
 * 			// .. your code here
 * 		}
 * 	} while (instance.nextRotation());
 * } while (instance.nextPermutation());
 * 
 * }</pre>
 * 
 * @see <a href=
 *      "https://www.nayuki.io/page/next-lexicographical-permutation-algorithm"
 *      target="_top">next-lexicographical-permutation-algorithm</a>
 */

public final class PermutationRotationIterator {

	public static PermutationRotation[] toRotationMatrix(List<BoxItem> list, boolean rotate3D) {
		PermutationRotation[] boxes = new PermutationRotation[list.size()];
		for(int i = 0; i < list.size(); i++) {
			Box box0 = list.get(i).getBox();
			
			List<Box> result = new ArrayList<>();
			if(rotate3D) {
				result.add(box0);
				
				if(!box0.isSquare3D()) {
					
					Box box1 = box0.rotate3D();
					result.add(box1);
	
					Box box2 = box1.rotate3D();
					result.add(box2);
	
					if(!box0.isSquare2D() && !box1.isSquare2D() && !box2.isSquare2D()) {
						Box box3 = box2.rotate2D3D();
						result.add(box3);
						
						Box box4 = box3.rotate3D();
						result.add(box4);
		
						Box box5 = box4.rotate3D();
						result.add(box5);
					}
				}
			} else {
				result.add(box0);
				
				// do not rotate 2d if square
				if(!box0.isSquare2D()) {
					result.add(box0.rotate2D());
				}
			}

			boxes[i] = new PermutationRotation(list.get(i).getCount(), result.toArray(new Box[0]));
		}
		return boxes;
	}
	
	private final PermutationRotation[] matrix;
	private final int[] reset;
	private final int[] rotations; // 2^n or 6^n
	private final int[] permutations; // n!

	public PermutationRotationIterator(List<BoxItem> list, Dimension bound, boolean rotate3D) {
		this(bound, PermutationRotationIterator.toRotationMatrix(list, rotate3D));
	}

	public PermutationRotationIterator(Dimension bound, PermutationRotation[] unconstrained) {
		List<Integer> types = new ArrayList<>(unconstrained.length * 2);

		PermutationRotation[] matrix = new PermutationRotation[unconstrained.length];
		for(int i = 0; i < unconstrained.length; i++) {
			Box[] boxes =
				Arrays
					.stream(unconstrained[i].getBoxes())
					.filter(box -> bound.getWidth() >= box.getWidth() && bound.getHeight() >= box.getHeight() && bound.getDepth() >= box.getDepth())
					.toArray(Box[]::new);

			if(boxes.length == 0) {
				throw new IllegalArgumentException("Box at " + i + " does not fit") ;
			}
			matrix[i] = new PermutationRotation(unconstrained[i].getCount(), boxes);

			for(int k = 0; k < unconstrained[i].getCount(); k++) {
				types.add(i);
			}
		}

		this.matrix = matrix;

		// permutations is a 'pointer' list
		// keep the the number of permutations tight; 
		// identical boxes need not be interchanged
		permutations = new int[types.size()];
		
		for(int i = 0; i < permutations.length; i++) {
			permutations[i] = types.get(i);
		}
		
		reset = new int[permutations.length];
		rotations = new int[permutations.length];
	}
	
	public boolean nextRotation() {
		// next rotation
		for(int i = 0; i < rotations.length; i++) {
			while(rotations[i] < matrix[permutations[i]].getBoxes().length - 1) {
				rotations[i]++;
				
				// reset all previous counters 
				System.arraycopy(reset, 0, rotations, 0, i);
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isWithinHeight(int fromIndex, int height) {
		for(int i = fromIndex; i < permutations.length; i++) {
			if(get(i).getHeight() > height) {
				return false;
			}
		}
		return true;
	}
	
	protected void resetRotations() {
		System.arraycopy(reset, 0, rotations, 0, rotations.length);
	}
	
	public long countRotations() {
		long n = 1;
		for(int i = 0; i < rotations.length; i++) {
			if(Long.MAX_VALUE / matrix[rotations[i]].getBoxes().length <= n) {
				return -1L;
			}

			n = n * matrix[rotations[i]].getBoxes().length;
		}
		return n;
	}
	
	public long countPermutations() {
		// reduce permutations for boxes which are duplicated
		
		int maxCount = 0;
		for(int i = 0; i < matrix.length; i++) {
			if(maxCount < matrix[i].getCount()) {
				maxCount = matrix[i].getCount();
			}
		}

		long n = 1;
		if(maxCount > 1) {
			int[] factors = new int[maxCount];
			for(int i = 0; i < matrix.length; i++) {
				for(int k = 0; k < matrix[i].getCount(); k++) {
					factors[k]++;
				}
			}

			for(long i = 0; i < permutations.length; i++) {
				if(Long.MAX_VALUE / (i + 1) <= n) {
					return -1L;
				}
				
				n = n * (i + 1);
				
				for(int k = 1; k < maxCount; k++) {
					while(factors[k] > 0 && n % (k + 1) == 0) {
						n = n / (k + 1);
						
						factors[k]--;
					}
				}
			}
			
			for(int k = 1; k < maxCount; k++) {
				while(factors[k] > 0) {
					n = n / (k + 1);
					
					factors[k]--;
				}
			}
		} else {
			for(long i = 0; i < permutations.length; i++) {
				if(Long.MAX_VALUE / (i + 1) <= n) {
					return -1L;
				}
				n = n * (i + 1);
			}
		}
		return n;
	}

	
	public Box get(int index) {
		return matrix[permutations[index]].getBoxes()[rotations[index]];
	}
	
	public boolean nextPermutation() {
		resetRotations();
		
	    // Find longest non-increasing suffix
		
	    int i = permutations.length - 1;
	    while (i > 0 && permutations[i - 1] >= permutations[i])
	        i--;
	    // Now i is the head index of the suffix
	    
	    // Are we at the last permutation already?
	    if (i <= 0) {
	        return false;
	    }
	    
	    // Let array[i - 1] be the pivot
	    // Find rightmost element that exceeds the pivot
	    int j = permutations.length - 1;
	    while (permutations[j] <= permutations[i - 1])
	        j--;
	    // Now the value array[j] will become the new pivot
	    // Assertion: j >= i
	    
	    // Swap the pivot with j
	    int temp = permutations[i - 1];
	    permutations[i - 1] = permutations[j];
	    permutations[j] = temp;
	    
	    // Reverse the suffix
	    j = permutations.length - 1;
	    while (i < j) {
	        temp = permutations[i];
	        permutations[i] = permutations[j];
	        permutations[j] = temp;
	        i++;
	        j--;
	    }
	    
	    // Successfully computed the next permutation
	    return true;
	}
	
	public int length() {
		return rotations.length;
	}

}
