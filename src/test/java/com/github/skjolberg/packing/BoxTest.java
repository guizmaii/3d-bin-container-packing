package com.github.skjolberg.packing;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Assert;
import org.junit.Test;

public class BoxTest {

	@Test
	public void testCanHold() {
		
		Box box = new Box(350, 150, 400);
		Assert.assertTrue(box.canHold3D(350, 50, 400));
		Assert.assertTrue(box.canHold3D(50, 400, 350));
		Assert.assertTrue(box.canHold3D(400, 350, 50));
		
		Assert.assertTrue(box.canHold3D(50, 350, 400));
		Assert.assertTrue(box.canHold3D(400, 50, 350));
		Assert.assertTrue(box.canHold3D(350, 400, 50));
	}
	
	@Test
	public void testfitInFootprintRotate() {
		
		Box box = new Box(1, 6, 3);
		
		Assert.assertNotNull(box.fitRotate2D(1, 6));
		assertThat(box.getWidth(), is(1));
		assertThat(box.getDepth(), is(6));
		Assert.assertNotNull(box.fitRotate2D(6, 1));
		assertThat(box.getWidth(), is(6));
		assertThat(box.getDepth(), is(1));
		
		Assert.assertNull(box.fitRotate2D(1, 3));
		Assert.assertNull(box.fitRotate2D(3, 1));

	}
	
	@Test
	public void testSmallestFootprintMinimum() {
		
		Box box = new Box(1, 1, 10);
		
		Assert.assertNotNull(box.fitRotate3DSmallestFootprint(1, 1, 10));
		assertThat(box.getWidth(), is(1));
		assertThat(box.getDepth(), is(1));
		assertThat(box.getHeight(), is(10));

		Assert.assertNotNull(box.fitRotate3DSmallestFootprint(10, 1, 1));
		assertThat(box.getWidth(), is(10));
		assertThat(box.getDepth(), is(1));
		assertThat(box.getHeight(), is(1));

		Assert.assertNotNull(box.fitRotate3DSmallestFootprint(1, 10, 1));
		assertThat(box.getWidth(), is(1));
		assertThat(box.getDepth(), is(10));
		assertThat(box.getHeight(), is(1));

	}

	@Test
	public void testSmallestFootprintMinimum2() {
		
		Box box = new Box(1, 1, 10);
		
		Assert.assertNotNull(box.fitRotate3DSmallestFootprint(1, 5, 10)); // standing
		assertThat(box.getFootprint(), is(1));
		assertThat(box.getWidth(), is(1));
		assertThat(box.getDepth(), is(1));

		Assert.assertNotNull(box.fitRotate3DSmallestFootprint(10, 1, 5)); // lie down
		assertThat(box.getFootprint(), is(10));
		assertThat(box.getWidth(), is(10));
		assertThat(box.getDepth(), is(1));

		Assert.assertNotNull(box.fitRotate3DSmallestFootprint(5, 10, 1)); // lie down
		assertThat(box.getFootprint(), is(10));
		assertThat(box.getWidth(), is(1));
		assertThat(box.getDepth(), is(10));

		Assert.assertNotNull(box.fitRotate3DSmallestFootprint(5, 10, 10)); // standing
		assertThat(box.getFootprint(), is(1));
		assertThat(box.getWidth(), is(1));
		assertThat(box.getDepth(), is(1));

	}

}
