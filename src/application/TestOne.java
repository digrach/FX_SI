package application;
import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;


public class TestOne {

	@Test
	public void testInit() {
		Ut ut = new Ut();
		assertNotNull("New UT", ut);
	}

	@Test
	public void testCanvasWidth() {
		Ut ut = new Ut();
		assertEquals(1000,ut.getCanvasWidth(),0);
	}

	@Test
	public void testCanvasHeight() {
		Ut ut = new Ut();
		assertEquals(500,ut.getCanvasHeight(),0);
	}

	@Test
	public void testBuffer() {
		Ut ut = new Ut();
		assertEquals(0,ut.getBuffer(),0);
	}

	@Test
	public void testCanvasStartX() {
		Ut ut = new Ut();
		assertEquals(0,ut.getCanvasStartX(),0);
	}

	@Test
	public void testCanvasStartY() {
		Ut ut = new Ut();
		assertEquals(0,ut.getCanvasStartY(),0);
	}

	@Test
	public void testgameAssetWidth() {
		Ut ut = new Ut();
		assertEquals(50, ut.getGameAssetWidth(), 0);
	}

	@Test
	public void testgameAssetHeight() {
		Ut ut = new Ut();
		assertEquals(37.5, ut.getGameAssetHeight(), 0);
	}

	@Test
	public void makeMiddleCoordinate() {
		Ut ut = new Ut();
		assertEquals(500, ut.makeMiddleCoordinate(), 0);
	}

	@Test
	public void testInitialiseSpaceshipPositionX() {
		Ut ut = new Ut();
		assertEquals(475,ut.getSpaceship().getX(),0);
	}

	@Test
	public void testInitialiseSpaceshipPositionY() {
		Ut ut = new Ut();
		assertEquals(425,ut.getSpaceship().getY(),0);
	}

	@Test
	public void testSpaceshipWidth() {
		Ut ut = new Ut();
		assertEquals(50,ut.getSpaceship().getWidth(),0);
	}

	@Test
	public void testSpaceshipHeight() {
		Ut ut = new Ut();
		assertEquals(37.5,ut.getSpaceship().getHeight(),0);
	}

	@Test
	public void calculateDistanceWest() {
		Ut ut = new Ut();
		assertEquals(100,ut.calculateDistanceWest(100),0);
	}

	@Test
	public void calculateDistanceEast() {
		Ut ut = new Ut();
		assertEquals(100,ut.calculateDistanceEast(900),0);
	}

	@Test
	public void calculateDistanceNorth() {
		Ut ut = new Ut();
		assertEquals(100,ut.calculateDistanceNorth(100),0);
	}

	@Test
	public void checkForWestEdge() {
		Ut ut = new Ut();
		assertEquals(true,ut.checkForWestEdge(0));
	}

	@Test
	public void checkForEaststEdge() {
		Ut ut = new Ut();
		assertEquals(true,ut.checkForEastEdge(950,50));
	}

	@Test
	public void updateSpaceshipPositionEast() {
		Ut ut = new Ut();
		ut.updateSpaceshipPosition("east");
		assertEquals(485,ut.getSpaceship().getX(),0);
	}

	@Test
	public void updateSpaceshipPositionEastEdge() {
		Ut ut = new Ut();
		ut.getSpaceship().setX(940);
		ut.updateSpaceshipPosition("east");
		assertEquals(950,ut.getSpaceship().getX(),0);
	}

	@Test
	public void updateSpaceshipPositionTryToGoFurtherEast() {
		Ut ut = new Ut();
		ut.getSpaceship().setX(950);
		ut.updateSpaceshipPosition("east");
		assertEquals(950,ut.getSpaceship().getX(),0);
	}

	@Test
	public void updateSpaceshipPositionWest() {
		Ut ut = new Ut();
		ut.updateSpaceshipPosition("west");
		assertEquals(465,ut.getSpaceship().getX(),0);
	}

	@Test
	public void updateSpaceshipPositionWestEdge() {
		Ut ut = new Ut();
		ut.getSpaceship().setX(5);
		ut.updateSpaceshipPosition("west");
		assertEquals(0,ut.getSpaceship().getX(),0);
	}

	@Test
	public void updateSpaceshipPositionWestTryToGoFurtherWest() {
		Ut ut = new Ut();
		ut.getSpaceship().setX(0);
		ut.updateSpaceshipPosition("west");
		assertEquals(0,ut.getSpaceship().getX(),0);
	}

	@Test
	public void testAlienFleetLength() {
		Ut ut = new Ut();
		assertEquals(15, ut.getAlienFleet().length);
	}

	@Test
	public void testFleetStartX() {
		Ut ut = new Ut();
		assertEquals(125,ut.getAlienFleet()[0].getX(),0);
	}

	@Test
	public void testFleetEndX() {
		Ut ut = new Ut();
		int len = ut.getAlienFleet().length;
		assertEquals(825,ut.getAlienFleet()[len - 1].getX(),0);
	}

	@Test
	public void testFleetStartY() {
		Ut ut = new Ut();
		assertEquals(37.5,ut.getAlienFleet()[0].getY(),0);
	}

	@Test
	public void testFleetEndY() {
		Ut ut = new Ut();
		int len = ut.getAlienFleet().length;
		assertEquals(187.5,ut.getAlienFleet()[len - 1].getY(),0);
	}

	@Test 
	public void updateAlienFleetPosition() {
		System.out.println("\nTESTING updateAlienFleetPosition");

		Ut ut = new Ut();
		ut.updateAlienFleetPosition();
		assertEquals(835.0,ut.getAlienFleet()[4].getX(),0);
	}

	@Test
	public void testAlienFleetTravelIncrement() {
		System.out.println("\nTESTING testAlienFleetTravelIncrement");

		Ut ut = new Ut();
		assertEquals(10.0,ut.getFleetTravelRate() * ut.makeAxisLength(ut.getCanvasStartX(), ut.getCanvasEndX()),0);
	}

	@Test 
	public void updateAlienFleetPositionEastEdge() {
		System.out.println("\nTESTING updateAlienFleetPositionEastEdge");

		Ut ut = new Ut();
		for (int x = 0; x < 100; x++) {
			ut.updateAlienFleetPosition();
			assertNotEquals(ut.getAlienFleet()[4].getX() > 950,ut.getAlienFleet()[4].getX());
		}

	}

	@Test 
	public void updateAlienFleetPositionWestEdge() {
		System.out.println("\nTESTING updateAlienFleetPositionWestEdge");

		Ut ut = new Ut();
		for (int x = 0; x < 50; x++) {
			ut.updateAlienFleetPosition();
			assertNotEquals(ut.getAlienFleet()[0].getX() < 0,ut.getAlienFleet()[4].getX());
		}

	}

	@Test 
	public void updateAlienFleetPositionTestMovedSouth() {
		System.out.println("\nTESTING updateAlienFleetPositionTestMovedSouth");

		Ut ut = new Ut();
		for (int x = 0; x < 13; x++) {
			ut.updateAlienFleetPosition();
		}
		assertEquals(112.5,ut.getAlienFleet()[4].getY(),0);

	}

	@Test
	public void fireProjectile() {
		System.out.println("\nTESTING fireProjectile");

		Ut ut = new Ut();
		SpaceShip s = ut.getSpaceship();
		for (int x = 0; x < 20; x ++) {
			ut.fireProjectile((Math.random() * 975), s.getY());
		}
		assertEquals(20,ut.getProjectiles().size());
	}

	@Test
	public void testProjectileTravelIncrement() {
		System.out.println("\nTESTING testProjectileTravelIncrement");

		Ut ut = new Ut();
		assertEquals(5.0,ut.getProjectileTravelRate() * ut.makeAxisLength(ut.getCanvasStartY(), ut.getCanvasEndY()),0);
	}

	@Test
	public void updateProjectilePostion() {
		System.out.println("\nTESTING updateProjectilePostion");
		Ut ut = new Ut();

		for (int x = 0; x < 1; x ++) {
			ut.fireProjectile((Math.random() * 975), (Math.random() * 300));
		}
		
		for (int x = 0; x < 100; x ++) {
			ut.updateProjectilePostion();
			for (Projectile p: ut.getProjectiles()) {
				System.out.println(p.getX() + " " + p.getY());
				assertTrue(p.getY() > 0);
			}
		}
	}

	@Test
	public void c1() {
		System.out.println("\nTESTING c1");

		Ut ut = new Ut();

		for (int x = 0; x < 1000; x ++) {
			ut.fireProjectile((Math.random() * 975), (Math.random() * 425));
		}

		for (int x = 0; x < 85; x ++) {
			ut.updateAlienFleetPosition();
			ut.updateProjectilePostion();
		}

		int count = 0;
		for (Alien a : ut.getAlienFleet()) {
			if (a != null) {
				count ++;
				//System.out.println("Alien coords: " + a.getX() + " " + a.getY());
			}
		}

		for (Projectile p : ut.getProjectiles()) {
			//System.out.println("Projectile coords: " + p.getX() + " " + p.getY());
		}

		System.out.println("Num of not null Aliens: " + count);
		System.out.println("getProjectiles().size() " + ut.getProjectiles().size());
	}
}
