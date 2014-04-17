package application;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;




public class Ut {

	private double canvasWidth;
	private double canvasHeight;
	private double buffer;
	private double widthDivisor;
	private double heightPercentage;
	private double fleetTravelRate;
	private double spaceshipTravelRate;
	private double projectileTravelRate;
	private double fleetProjectileTravelRate;
	private int numOfAliens;
	private int numOfAssetsOnRow;

	// changable
	private String spaceshipDirection;
	private String fleetDirection;
	private int score;
	IntegerProperty oScore = new SimpleIntegerProperty();
	BooleanProperty oGameOver = new SimpleBooleanProperty();
	BooleanProperty oShot = new SimpleBooleanProperty();
	private int finishScore;
	private long lastShot;

	// Calculated
	private double canvasStartX;
	private double canvasStartY;
	private double canvasEndX;
	private double canvasEndY;
	private double spaceShipTravelRate;
	private double gameAssetWidth;
	private double gameAssetHeight;
	private double assetSpacerWidth;
	private double assetSpacerHeight;

	private Alien[] alienFleet; 
	private SpaceShip spaceship;
	private List<Projectile> spceshipProjectiles;
	private List<Projectile> fleetProjectiles;

	URL shootSound = getClass().getResource("laser.mp3");
	URL explosionSound = getClass().getResource("explosion.mp3");
	URL endGameSound = getClass().getResource("endgame.mp3");
	URL alienProjectileSound = getClass().getResource("alienprojectile.mp3");

	public Ut() {
		initVars(1000,500,0);
	}

	public Ut(double canvasWidth, double canvasHeight, double buffer) {
		initVars(canvasWidth,canvasHeight,buffer);
	}

	public void initVars(double canvasWidth, double canvasHeight, double buffer) {
		spceshipProjectiles = new ArrayList<Projectile>();
		fleetProjectiles = new ArrayList<Projectile>();
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.buffer = buffer;
		widthDivisor = 20;
		heightPercentage = 0.75;
		spaceshipDirection = "east";
		fleetDirection = "east";
		fleetTravelRate = 0.001;
		spaceshipTravelRate = 0.01;
		projectileTravelRate = 0.03;
		fleetProjectileTravelRate = 0.003;
		numOfAliens = 15;
		numOfAssetsOnRow = 5;
		finishScore = numOfAliens;
		makeCanvasDimensions();
		makeGameAssetDimensions();
		initialiseSpaceshipPosition();
		makeFleetStartX();
		makeFleetStartY();
		initialiseAlienFleetPosition();		
	}


	public void doGameLoop() {
		updateAlienFleetPosition();
		updateProjectilePostion();
		updateAlienProjectilePostion();
		detectAlienCollisionWithSpaceship();
		detectProjectileCollisionWithSpaceship();
	}

	public SpaceShip initialiseSpaceshipPosition() {
		double middleCoordinate = makeMiddleCoordinate();
		double x = middleCoordinate - (gameAssetWidth / 2);
		double y = canvasEndY - (gameAssetHeight * 2);
		spaceship = new SpaceShip(x,y,gameAssetWidth, gameAssetHeight);
		return spaceship;
	}

	public boolean detectAlienCollisionWithSpaceship() {
		for (Alien a: alienFleet) {
			if (a != null) {
				if (a.getX() + a.getWidth() >= spaceship.getX() 
						&& a.getX() <= spaceship.getX() +  spaceship.getWidth()
						&& a.getY() + a.getHeight() >= spaceship.getY()
						&& a.getY() <= spaceship.getY() + spaceship.getHeight()) {
					oGameOver.set(true);
					return true;
				}
			}
		}
		return false;
	}

	public boolean detectProjectileCollisionWithAlien(Alien a) {
		Iterator<Projectile> itr = spceshipProjectiles.iterator();
		while(itr.hasNext()) {
			Projectile p = itr.next();
			if (p.getX() + p.getWidth() >= a.getX() 
					&& p.getX() <= a.getX() + a.getWidth()
					&&
					p.getY() + p.getHeight() >= a.getY() && p.getY() <= a.getY() + a.getHeight()) {
				itr.remove();
				return true;
			}
		}
		return false;
	}

	public boolean detectProjectileCollisionWithSpaceship() {
		Iterator<Projectile> itr = fleetProjectiles.iterator();
		while(itr.hasNext()) {
			Projectile p = itr.next();
			if (p.getX() + p.getWidth() >= spaceship.getX() 
					&& p.getX() <= spaceship.getX() + spaceship.getWidth()
					&&
					p.getY() + p.getHeight() >= spaceship.getY() && p.getY() <= spaceship.getY() + spaceship.getHeight()) {
				itr.remove();
				oGameOver.set(true);
				return true;
			}
		}
		return false;
	}

	public void dropProjectile() {
		int numOfCols = numOfAssetsOnRow - 1;
		Alien ra = null;
		int index = 0;
		int randomCol = (int) (Math.random() * (numOfCols + 1));
		index = (10 + randomCol);
		while (ra == null && index >= 0) {
			ra = alienFleet[index];
			index -= 5;
		}
		if (ra != null) {
			fleetProjectiles.add(new Projectile(ra.getX() + (gameAssetWidth / 2), ra.getY() + (gameAssetHeight + 1),3,3));
		}
	}

	public void updateAlienFleetPosition() {
		double axisLength = makeAxisLength(canvasStartX, canvasEndX);
		double travelIncrement = fleetTravelRate * axisLength;

		//// East
		if (fleetDirection.equals("east")) {
			double mostEast = 0;
			for (Alien a : alienFleet) {
				if (a != null) {
					if (a.getX() > mostEast) {
						mostEast = a.getX();
					}
				}
			}
			double distanceEast = calculateDistanceEast(mostEast);
			if ((mostEast + gameAssetWidth) + travelIncrement > canvasEndX) {
				travelIncrement = distanceEast - gameAssetWidth;
				fleetDirection = "west";
				moveFleetSouth();
			}
			moveFleetEast(travelIncrement);
		}

		//// West
		if (fleetDirection.equals("west")) {
			double mostWest = canvasEndX;
			for (Alien a : alienFleet) {
				if (a != null) {
					if (a.getX() < mostWest) {
						mostWest = a.getX();
					}
				}
			}
			double distanceWest = calculateDistanceWest(mostWest);
			if (mostWest - travelIncrement < canvasStartX) {
				travelIncrement = distanceWest;
				fleetDirection = "east";
				moveFleetSouth();
			}
			moveFleetWest(travelIncrement);
		}
	}

	public void updateProjectilePostion() {
		double axisLength = makeAxisLength(canvasStartY, canvasEndY);
		double travelIncrement = projectileTravelRate * axisLength;
		Iterator<Projectile> itr = spceshipProjectiles.iterator();
		while(itr.hasNext()) {
			Projectile p = itr.next();
			double projectileY = p.getY();
			if (projectileY - travelIncrement < canvasStartY) {
				itr.remove();
			} else {
				p.setY(projectileY - travelIncrement);
			}
		}
	}

	public void fireProjectile(double sourceX, double sourceY) {
		spceshipProjectiles.add(new Projectile(sourceX, sourceY + 1, 3, 3));
	}

	public void updateAlienProjectilePostion() {
		double axisLength = makeAxisLength(canvasStartY, canvasEndY);
		double travelIncrement = fleetProjectileTravelRate * axisLength;
		Iterator<Projectile> itr = fleetProjectiles.iterator();
		while(itr.hasNext()) {
			Projectile p = itr.next();
			double projectileY = p.getY();
			if (projectileY + travelIncrement > canvasEndY) {
				itr.remove();
			} else {
				p.setY(projectileY + travelIncrement);
			}
		}
	}

	public void initialiseAlienFleetPosition() {
		alienFleet = new Alien[numOfAliens];
		assetSpacerHeight = gameAssetHeight;
		int alienCount = 0;
		int aliensOnRowCount = 0;
		double currentX = makeFleetStartX();
		double currentY = makeFleetStartY();
		while (alienCount < numOfAliens) {
			Alien a = new Alien(currentX, currentY, gameAssetWidth, gameAssetHeight);
			alienFleet[alienCount] = a;
			currentX += gameAssetWidth + assetSpacerWidth;
			aliensOnRowCount ++;
			alienCount ++;
			if (aliensOnRowCount > numOfAssetsOnRow - 1) {
				aliensOnRowCount = 0;
				currentX = makeFleetStartX();
				currentY += assetSpacerHeight * 2;
			}
		}
	}

	public void updateSpaceshipPosition(String direction) {
		double axisLength = makeAxisLength(canvasStartX, canvasEndX);
		double travelIncrement = spaceshipTravelRate * axisLength;
		double updatedX = 0;
		if (direction.equals("west")) {
			updatedX = spaceship.getX() - travelIncrement;
			if (checkForWestEdge(updatedX)) {
				spaceship.setX(canvasStartX);
				spaceshipDirection = "east";
			} else {
				spaceship.setX(updatedX);
			}
		}
		if (direction.equals("east")) {
			updatedX = spaceship.getX() + travelIncrement;
			if (checkForEastEdge(updatedX, spaceship.getWidth())) {
				spaceship.setX(canvasEndX - spaceship.getWidth());
				spaceshipDirection = "west";
			} else {
				spaceship.setX(updatedX);
			}
		}
	}

	public void makeCanvasDimensions() {
		canvasStartX = buffer;
		canvasStartY = buffer;
		canvasEndX = canvasWidth - buffer;
		canvasEndY = canvasHeight - buffer;
	}

	public double makeAxisLength(double startCoord, double endCoord) {
		double axisLength = endCoord - startCoord;
		return axisLength;
	}

	public void makeGameAssetDimensions() {
		gameAssetWidth = canvasWidth / widthDivisor;
		gameAssetHeight = heightPercentage * gameAssetWidth;
	}

	public double makeMiddleCoordinate() {
		double middleCoordinate = (canvasEndX - canvasStartX) / 2;
		return middleCoordinate;
	}

	public double makeFleetStartX() {
		double axisLength = makeAxisLength(canvasStartX, canvasEndX);
		double remainingWidthOnX = axisLength - (gameAssetWidth * numOfAssetsOnRow);
		assetSpacerWidth = remainingWidthOnX / (numOfAssetsOnRow + 1);
		double fleetStartX = assetSpacerWidth;
		return fleetStartX;
	}

	public double makeFleetStartY() {
		double fleetStartY = canvasStartX + gameAssetHeight;
		return fleetStartY;
	}

	public double calculateDistanceWest(double positionOnAxis) {
		double distanceWest = positionOnAxis - canvasStartX;
		return distanceWest;
	}

	public double calculateDistanceEast(double positionOnAxis) {
		double axisLength = makeAxisLength(canvasStartX, canvasEndX);
		double distanceEast = axisLength - positionOnAxis;
		return distanceEast;
	}

	public double calculateDistanceNorth(double positionOnAxis) {
		double distanceNorth = positionOnAxis - canvasStartY;
		return distanceNorth;
	}

	public boolean checkForWestEdge(double xCoord) {
		boolean detected = false;
		if (xCoord <= canvasStartX) {
			detected = true;
		}
		return detected;
	}

	public boolean checkForEastEdge(double xCoord, double widthToCheck) {
		boolean detected = false;
		if (xCoord  + widthToCheck >= canvasEndX) {
			detected = true;
		}
		return detected;
	}

	public void moveFleetEast(double travelIncrement) {
		for (int x = 0; x < alienFleet.length; x++) {
			if (alienFleet[x] != null) {
				double updatedX = alienFleet[x].getX() + travelIncrement;
				alienFleet[x].setX(updatedX);
				if (detectProjectileCollisionWithAlien(alienFleet[x])) {
					alienFleet[x] = null;
					updateScore();
				}
			}
		}
	}

	public void moveFleetWest(double travelIncrement) {
		for (int x = 0; x < alienFleet.length; x++) {
			if (alienFleet[x] != null) {
				double updatedX = alienFleet[x].getX() - travelIncrement;
				alienFleet[x].setX(updatedX);
				if (detectProjectileCollisionWithAlien(alienFleet[x])) {
					alienFleet[x] = null;
					updateScore();
				}
			}
		}
	}

	public void moveFleetSouth() {
		for (int x = 0; x < alienFleet.length; x++) {
			if (alienFleet[x] != null) {
				double updatedY = alienFleet[x].getY() + (gameAssetHeight / 2);
				alienFleet[x].setY(updatedY);
				if (detectProjectileCollisionWithAlien(alienFleet[x])) {
					alienFleet[x] = null;
					updateScore();
				}
			}
		}
	}

	public void updateScore() {
		score ++;
		oScore.setValue(score);
		if(score == finishScore) {
			oGameOver.set(true);
		}
	}

	// For use by UI
	public void moveSpaceShip(String direction) {
		updateSpaceshipPosition(direction);
	}

	public void Shoot(Long now) {
		if(now > lastShot + 300) {
			oShot.setValue(true);
			fireProjectile(spaceship.getX() + (gameAssetWidth / 2),spaceship.getY());
			lastShot = now;
		}
		oShot.setValue(false);
	}

	public double getCanvasWidth() {
		return canvasWidth;
	}

	public void setCanvasWidth(double canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public double getCanvasHeight() {
		return canvasHeight;
	}

	public void setCanvasHeight(double canvasHeight) {
		this.canvasHeight = canvasHeight;
	}

	public double getBuffer() {
		return buffer;
	}

	public void setBuffer(double buffer) {
		this.buffer = buffer;
	}

	public double getWidthDivisor() {
		return widthDivisor;
	}

	public void setWidthDivisor(double widthDivisor) {
		this.widthDivisor = widthDivisor;
	}

	public double getHeightPercentage() {
		return heightPercentage;
	}

	public void setHeightPercentage(double heightPercentage) {
		this.heightPercentage = heightPercentage;
	}

	public double getFleetTravelRate() {
		return fleetTravelRate;
	}

	public void setFleetTravelRate(double fleetTravelRate) {
		this.fleetTravelRate = fleetTravelRate;
	}

	public double getSpaceshipTravelRate() {
		return spaceshipTravelRate;
	}

	public void setSpaceshipTravelRate(double spaceshipTravelRate) {
		this.spaceshipTravelRate = spaceshipTravelRate;
	}

	public double getProjectileTravelRate() {
		return projectileTravelRate;
	}

	public void setProjectileTravelRate(double projectileTravelRate) {
		this.projectileTravelRate = projectileTravelRate;
	}

	public double getFleetProjectileTravelRate() {
		return fleetProjectileTravelRate;
	}

	public void setFleetProjectileTravelRate(double fleetProjectileTravelRate) {
		this.fleetProjectileTravelRate = fleetProjectileTravelRate;
	}

	public int getNumOfAliens() {
		return numOfAliens;
	}

	public void setNumOfAliens(int numOfAliens) {
		this.numOfAliens = numOfAliens;
	}

	public int getNumOfAssetsOnRow() {
		return numOfAssetsOnRow;
	}

	public void setNumOfAssetsOnRow(int numOfAssetsOnRow) {
		this.numOfAssetsOnRow = numOfAssetsOnRow;
	}

	public String getSpaceshipDirection() {
		return spaceshipDirection;
	}

	public void setSpaceshipDirection(String spaceshipDirection) {
		this.spaceshipDirection = spaceshipDirection;
	}

	public String getFleetDirection() {
		return fleetDirection;
	}

	public void setFleetDirection(String fleetDirection) {
		this.fleetDirection = fleetDirection;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public IntegerProperty getoScore() {
		return oScore;
	}

	public void setoScore(IntegerProperty oScore) {
		this.oScore = oScore;
	}

	public BooleanProperty getoGameOver() {
		return oGameOver;
	}

	public void setoGameOver(BooleanProperty oGameOver) {
		this.oGameOver = oGameOver;
	}

	public int getFinishScore() {
		return finishScore;
	}

	public void setFinishScore(int finishScore) {
		this.finishScore = finishScore;
	}

	public long getLastShot() {
		return lastShot;
	}

	public void setLastShot(long lastShot) {
		this.lastShot = lastShot;
	}

	public double getCanvasStartX() {
		return canvasStartX;
	}

	public void setCanvasStartX(double canvasStartX) {
		this.canvasStartX = canvasStartX;
	}

	public double getCanvasStartY() {
		return canvasStartY;
	}

	public void setCanvasStartY(double canvasStartY) {
		this.canvasStartY = canvasStartY;
	}

	public double getCanvasEndX() {
		return canvasEndX;
	}

	public void setCanvasEndX(double canvasEndX) {
		this.canvasEndX = canvasEndX;
	}

	public double getCanvasEndY() {
		return canvasEndY;
	}

	public void setCanvasEndY(double canvasEndY) {
		this.canvasEndY = canvasEndY;
	}

	public double getSpaceShipTravelRate() {
		return spaceShipTravelRate;
	}

	public void setSpaceShipTravelRate(double spaceShipTravelRate) {
		this.spaceShipTravelRate = spaceShipTravelRate;
	}

	public double getGameAssetWidth() {
		return gameAssetWidth;
	}

	public void setGameAssetWidth(double gameAssetWidth) {
		this.gameAssetWidth = gameAssetWidth;
	}

	public double getGameAssetHeight() {
		return gameAssetHeight;
	}

	public void setGameAssetHeight(double gameAssetHeight) {
		this.gameAssetHeight = gameAssetHeight;
	}

	public double getAssetSpacerWidth() {
		return assetSpacerWidth;
	}

	public void setAssetSpacerWidth(double assetSpacerWidth) {
		this.assetSpacerWidth = assetSpacerWidth;
	}

	public double getAssetSpacerHeight() {
		return assetSpacerHeight;
	}

	public void setAssetSpacerHeight(double assetSpacerHeight) {
		this.assetSpacerHeight = assetSpacerHeight;
	}

	public Alien[] getAlienFleet() {
		return alienFleet;
	}

	public void setAlienFleet(Alien[] alienFleet) {
		this.alienFleet = alienFleet;
	}

	public SpaceShip getSpaceship() {
		return spaceship;
	}

	public void setSpaceship(SpaceShip spaceship) {
		this.spaceship = spaceship;
	}

	public List<Projectile> getSpceshipProjectiles() {
		return spceshipProjectiles;
	}

	public void setSpceshipProjectiles(List<Projectile> spceshipProjectiles) {
		this.spceshipProjectiles = spceshipProjectiles;
	}

	public List<Projectile> getFleetProjectiles() {
		return fleetProjectiles;
	}

	public void setFleetProjectiles(List<Projectile> fleetProjectiles) {
		this.fleetProjectiles = fleetProjectiles;
	}

	public URL getShootSound() {
		return shootSound;
	}

	public void setShootSound(URL shootSound) {
		this.shootSound = shootSound;
	}

	public URL getExplosionSound() {
		return explosionSound;
	}

	public void setExplosionSound(URL explosionSound) {
		this.explosionSound = explosionSound;
	}

	public URL getEndGameSound() {
		return endGameSound;
	}

	public void setEndGameSound(URL endGameSound) {
		this.endGameSound = endGameSound;
	}

	public URL getAlienProjectileSound() {
		return alienProjectileSound;
	}

	public void setAlienProjectileSound(URL alienProjectileSound) {
		this.alienProjectileSound = alienProjectileSound;
	}



	// Get / set


}