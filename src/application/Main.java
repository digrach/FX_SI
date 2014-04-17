package application;

import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;


public class Main extends Application{// implements ChangeListener<Integer>, InvalidationListener {

	double width = 1000, height = 500, buffer = 0;
	//double width = 500, height = 250, buffer = 0;
	Ut ut = new Ut(width,height,buffer);
	FlowPane root;
	Scene scene;
	GraphicsContext gc;
	Canvas canvas;
	AnimationTimer timer;
	int countdown = 50;

	AudioClip shootClip = new AudioClip(ut.getShootSound().toString());
	AudioClip explosionClip = new AudioClip(ut.getExplosionSound().toString());
	AudioClip endGameClip = new AudioClip(ut.getEndGameSound().toString());
	AudioClip alienProjectileSoundClip = new AudioClip(ut.getAlienProjectileSound().toString());
	
	final BooleanProperty spacePressed = new SimpleBooleanProperty(false);
	final BooleanProperty rightPressed = new SimpleBooleanProperty(false);
	final BooleanProperty leftPressed = new SimpleBooleanProperty(false);
	final BooleanBinding spaceAndRightPressed = spacePressed.and(rightPressed);
	final BooleanBinding spaceAndLeftPressed = spacePressed.and(leftPressed);

	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.initStyle(StageStyle.UNDECORATED);
			root = new FlowPane();
			scene = new Scene(root,ut.getCanvasWidth(),ut.getCanvasHeight());
			canvas = new Canvas();
			canvas.setWidth(ut.getCanvasWidth());
			canvas.setHeight(ut.getCanvasHeight());
			gc = canvas.getGraphicsContext2D();

			preGameInit();

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					System.out.println("Stage is closing");

				}
			});        

			root.getChildren().add(canvas);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			startScreen();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void preGameInit() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, ut.getCanvasWidth(), ut.getCanvasHeight());
		drawAliens();
		drawSpaceship();
		drawScore();
		drawMenu();
		addHandlers();
	}

	public void restart() {
		endGameClip.stop();
		rightPressed.set(false);
		leftPressed.set(false);

		ut = new Ut(width,height,buffer);
		drawAliens();
		drawSpaceship();
		addHandlers();
		startTimer();
	}

	public void startTimer() {
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				gc.setFill(Color.BLACK);
				gc.fillRect(0, 0, ut.getCanvasWidth(), ut.getCanvasHeight());

				ut.doGameLoop();
				drawOnCanvas();
				
				if (rightPressed.getValue()) {
					ut.moveSpaceShip("east");
				}
				if (leftPressed.getValue()) {
					ut.moveSpaceShip("west");
				}
				if (spacePressed.getValue()) {
					ut.Shoot(System.currentTimeMillis());
					//shootClip.play();
				}

				if (countdown == 0){
					countdown = (int) (100 + (Math.random() * 50));
					ut.dropProjectile();
					alienProjectileSoundClip.play();
				}
				countdown --;
			}
		};
		timer.start();
	}

	public void drawOnCanvas() {
		drawAliens();
		drawSpaceship();
		drawProjectiles();
		drawAlienProjectiles();
		drawScore();
		drawMenu();
	}

	public void startScreen() {
		final Stage dialog = new Stage();
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.setOpacity(.9);
		dialog.initModality(Modality.WINDOW_MODAL);

		VBox vb = new VBox();
		vb.setPadding(new Insets(10));
		vb.setSpacing(8);
		vb.setAlignment(Pos.CENTER);

		Text title = new Text("Invade my space!");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		title.setFill(Color.CHARTREUSE);
		vb.getChildren().add(title);

		Label lblYourScoreLabel = new Label("Choose your destiny!");
		lblYourScoreLabel.setFont(new Font("Arial", 30));
		lblYourScoreLabel.setTextFill(Color.CHARTREUSE);

		VBox.setMargin(lblYourScoreLabel, new Insets(0, 0, 0, 8));
		vb.getChildren().add(lblYourScoreLabel);

		Button btnPlay = new Button();
		btnPlay.setText("Play");
		btnPlay.setFont(new Font("Arial", 25));

		btnPlay.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				//restart();
				dialog.close();
				startTimer();
			}
		});

		//VBox.setMargin(lblYourScoreLabel, new Insets(0, 0, 0, 8));
		vb.getChildren().add(btnPlay);

		Button btnExit = new Button();
		btnExit.setText("Exit");
		btnExit.setFont(new Font("Arial", 25));

		btnExit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});

		//VBox.setMargin(lblYourScoreLabel, new Insets(0, 0, 0, 8));
		vb.getChildren().add(btnExit);

		Scene dialogScene = new Scene(vb,ut.getCanvasWidth() / 3,ut.getCanvasHeight() / 3);

		// Capture space keypress as it is happening to prevent
		// default behaviour.
		dialogScene.addEventFilter( KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {

				if (event.getEventType() == KeyEvent.KEY_PRESSED){
					// Consume Event before Bubbling Phase
					if ( event.getCode() == KeyCode.SPACE ){ 
						event.consume();
					}
				}
			}
		});

		dialogScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.X) {
					Platform.exit();
				}
				if (arg0.getCode() == KeyCode.P) {
					startTimer();
					endGameClip.stop();
					dialog.close();
				}
			}

		});

		dialogScene.setFill(Color.rgb(0, 0, 0, 1));
		dialog.setScene(dialogScene);
		dialog.show();
		endGameClip.play();
	}

	public void gameOver() {
		timer.stop();
		shootClip.stop();
		explosionClip.stop();
		alienProjectileSoundClip.stop();
		endGameClip.play();
		

		final Stage dialog = new Stage();
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.setOpacity(.9);
		dialog.initModality(Modality.WINDOW_MODAL);

		VBox vb = new VBox();
		vb.setPadding(new Insets(10));
		vb.setSpacing(8);
		vb.setAlignment(Pos.CENTER);

		Text title = new Text("Game Over FOOL!");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		title.setFill(Color.CHARTREUSE);
		vb.getChildren().add(title);

		Label lblYourScoreLabel = new Label("Your score: " + ut.getScore());
		lblYourScoreLabel.setFont(new Font("Arial", 40));
		lblYourScoreLabel.setTextFill(Color.CHARTREUSE);

		VBox.setMargin(lblYourScoreLabel, new Insets(0, 0, 0, 8));
		vb.getChildren().add(lblYourScoreLabel);

		Button btnPlayAgain = new Button();
		btnPlayAgain.setText("Play again");
		btnPlayAgain.setFont(new Font("Arial", 25));

		btnPlayAgain.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				restart();
				dialog.close();
			}
		});

		VBox.setMargin(lblYourScoreLabel, new Insets(0, 0, 0, 8));
		vb.getChildren().add(btnPlayAgain);

		Button btnExit = new Button();
		btnExit.setText("Exit");
		btnExit.setFont(new Font("Arial", 25));

		btnExit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});

		VBox.setMargin(lblYourScoreLabel, new Insets(0, 0, 0, 8));
		vb.getChildren().add(btnExit);

		Scene dialogScene = new Scene(vb,ut.getCanvasWidth() / 3,ut.getCanvasHeight() / 3);

		// Capture space keypress as it is happening to prevent
		// default behaviour.
		dialogScene.addEventFilter( KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {

				if (event.getEventType() == KeyEvent.KEY_PRESSED){
					// Consume Event before Bubbling Phase
					if ( event.getCode() == KeyCode.SPACE ){ 
						event.consume();
					}
				}
			}
		});

		dialogScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.X) {
					Platform.exit();
				}
				if (arg0.getCode() == KeyCode.P) {
					restart();
					dialog.close();
				}
			}

		});

		dialogScene.setFill(Color.rgb(0, 0, 0, 1));
		dialog.setScene(dialogScene);
		dialog.show();
		
	}

	public void addHandlers() {

		


		ut.oScore.addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				explosionClip.play();				
			}
		});

		ut.oGameOver.addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue == true) {
					ut.oGameOver.removeListener(this);
					gameOver();				
				}
			}
		});
		
		ut.oShot.addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue == true) {
					shootClip.play();				
				}
			}
		});

//		rightPressed.addListener(new ChangeListener<Boolean>() {
//			@Override
//			public void changed(ObservableValue<? extends Boolean> observable,
//					Boolean oldValue, Boolean newValue) {
//				if (newValue) {
//					//ut.moveSpaceShip("east");
//				}
//			}
//		});
//
//		leftPressed.addListener(new ChangeListener<Boolean>() {
//			@Override
//			public void changed(ObservableValue<? extends Boolean> observable,
//					Boolean oldValue, Boolean newValue) {
//				if (newValue) {
//					//ut.moveSpaceShip("west");
//				}
//			}
//		});
//
//		spacePressed.addListener(new ChangeListener<Boolean>() {
//			@Override
//			public void changed(ObservableValue<? extends Boolean> observable,
//					Boolean oldValue, Boolean newValue) {
//				if (newValue) {
////					ut.Shoot(System.currentTimeMillis());
////					shootClip.play();
//				}
//			}
//		});
//
//		spaceAndRightPressed.addListener(new ChangeListener<Boolean>() {
//			@Override
//			public void changed(ObservableValue<? extends Boolean> observable,
//					Boolean oldValue, Boolean newValue) {
//				if (newValue) {
//					ut.moveSpaceShip("east");
//					ut.Shoot(System.currentTimeMillis());
//				}
//			}
//		});
//
//		spaceAndLeftPressed.addListener(new ChangeListener<Boolean>() {
//			@Override
//			public void changed(ObservableValue<? extends Boolean> observable,
//					Boolean oldValue, Boolean newValue) {
//				if (newValue) {
//					ut.moveSpaceShip("west");
//					ut.Shoot(System.currentTimeMillis());
//				}
//			}
//		});

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.SPACE) {
					spacePressed.set(true);
				} else if (ke.getCode() == KeyCode.RIGHT) {
					rightPressed.set(true);
				} else if (ke.getCode() == KeyCode.LEFT) {
					leftPressed.set(true);
				} else if (ke.getCode() == KeyCode.X) {
					gameOver();
				} 
			}
		});

		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.SPACE) {
					spacePressed.set(false);
				} else if (ke.getCode() == KeyCode.RIGHT) {
					rightPressed.set(false);
				} else if (ke.getCode() == KeyCode.LEFT) {
					leftPressed.set(false);
				} 
			}
		});

	}

	public void drawAliens() {
		gc.setFill(Color.CHARTREUSE);
		Alien[] aliens = ut.getAlienFleet();
		for (Alien a: aliens) {
			if (a != null) {
				gc.fillRect(a.getX(), a.getY(), a.getWidth(), a.getHeight());
			}
		}
	}

	public void drawSpaceship() {
		gc.setFill(Color.WHITE);
		SpaceShip s = ut.getSpaceship();
		gc.fillRect(s.getX(), s.getY(), s.getWidth(), s.getHeight());
	}

	public void drawProjectiles() {
		gc.setFill(Color.RED);
		List<Projectile> projectiles = ut.getSpceshipProjectiles();
		for (Projectile p : projectiles) {
			gc.fillRect(p.getX(), p.getY(), p.getWidth(), p.getHeight());
		}
	}

	public void drawAlienProjectiles() {
		gc.setFill(Color.CHARTREUSE);
		List<Projectile> projectiles = ut.getFleetProjectiles();
		for (Projectile p : projectiles) {
			gc.fillRect(p.getX(), p.getY(), p.getWidth(), p.getHeight());
		}
	}

	public void drawScore() {
		gc.setFill(Color.FUCHSIA);
		gc.fillText(Integer.toString(ut.getScore()), ut.getCanvasEndX() - 100, ut.getCanvasEndY() - 20);
	}

	public void drawMenu() {
		gc.setFill(Color.ORANGE);
		gc.fillText("Exit = x", ut.getCanvasStartX() + 100, ut.getCanvasEndY() - 20);
	}

	public static void main(String[] args) {
		launch(args);
	}






}
