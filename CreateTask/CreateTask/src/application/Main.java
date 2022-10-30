package application;
	
import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import java.util.Timer;
import java.util.TimerTask;



public class Main extends Application {
	private Node dottedLine;
	private Node player;
	private Node laserBullet;
	private Node bossLaserBullet;
	private Node boss;
	private Group root;
	private Image playerIconImage = new Image("player.png");
	private ImageView playerIcon = new ImageView(playerIconImage);
	private Image bossIconImage = new Image("boss.png");
	private ImageView bossIcon = new ImageView(bossIconImage);
	private Image winIconImage = new Image("you won.png");
	private ImageView winIcon = new ImageView(winIconImage);
	private Image loseIconImage = new Image("you lose.png");
	private ImageView loseIcon = new ImageView(loseIconImage);
	private Image retryIconImage = new Image("retry.png");
	private ImageView retryIcon = new ImageView(retryIconImage);
	private Image playerHealthIconImage = new Image("player health.png");
	private ImageView playerHealthIcon = new ImageView(playerHealthIconImage);
	private Image bossHealthIconImage = new Image("boss health.png");
	private ImageView bossHealthIcon = new ImageView(bossHealthIconImage);
	private Image dottedLineIconImage = new Image("dotted line.png");
	private ImageView dottedLineIcon = new ImageView(dottedLineIconImage);
	private Image startIconImage = new Image("start.png");
	private ImageView startIcon = new ImageView(startIconImage);
	private Image MenuIconImage = new Image("Main Menu.png");
	private ImageView MenuIcon = new ImageView(MenuIconImage);
	private Image instrIconImage = new Image("instructions.png");
	private ImageView instrIcon = new ImageView(instrIconImage);
	private Image backIconImage = new Image("back.png");
	private ImageView backIcon = new ImageView(backIconImage);
	private Image directionsIconImage = new Image("directions.png");
	private ImageView directionsIcon = new ImageView(directionsIconImage);
	private Image noteIconImage = new Image("Note.png");
	private ImageView noteIcon = new ImageView(noteIconImage);
	
	private final int WIDTH = 1000;
	private final int HEIGHT = 1000;
	private boolean goUp, goLeft, goDown, goRight, boost, laser;
	private boolean winLoseCondition = true, bossLaserCondition = false, bossTimerCondition = false;
	private int delta, dx, dy, bossDelta = 6, newPlayerHpWidth, newBossHpWidth;
	double randomNumber;	
	private ArrayList<Node> lasers = new ArrayList<Node>();
	private ArrayList<Node> bossLasers = new ArrayList<Node>();
	private final int laserVelocity = 10;
	private int bossHealth, playerHealth, bossInterval=500;
	private final int startingBossHealth = 100, startingPlayerHealth = 100;
	private Scene scene;
	//background image from https://www.deviantart.com/driftwoodbones/art/Animated-Pixel-Stars-Box-Background-482071849
	private final Image background = new Image("background2.gif");
	private final ImageView backgroundImage = new ImageView(background);
	Timer bossTimer;
	Timer bossLaserTimer;
	
	//This animation timer is responsible for the "smooth" movements, any code in here is
	//constantly being detected and executed.
	AnimationTimer gameTimer = new AnimationTimer() {
		@Override
		public void handle(long arg0) {
			double currentX = player.getLayoutX();
			double currentY = player.getLayoutY();
			dx = 0;
			dy = 0;
			//basically just changes the speed (delta) of the sprite if boost == true, which is if the user is clicking SHIFT
			if(boost) {
				delta = 10;
			}else {
				delta = 5;
			}
			//moves the player across the window when clicking arrow keys
			if(goUp == true) {
				dy -= delta;
			}
			if(goLeft == true) {
				dx -= delta;
			}
			if(goDown == true) {
				dy += delta;
			}
			if(goRight == true) {
				dx += delta;
			}
			
			playerCollision(currentX + dx, currentY + dy);
			shootLaser();
			
			//Causes the boss to move left and right, bossCollision() checks to see if the boss has hit either edge of the window and will move it in the opposite direction.
			boss.relocate(boss.getLayoutX() + bossDelta, 100);
			bossCollision();

			
			
			if(!bossLaserCondition) {
					Image bossLaserIconImage = new Image("boss laser.png");
					ImageView bossLaserIcon = new ImageView(bossLaserIconImage);
					bossLaserBullet = bossLaserIcon;
					bossLaserBullet.relocate(boss.getLayoutX() + bossIconImage.getWidth()/2 - bossLaserIconImage.getWidth()/2, boss.getLayoutY() + bossIconImage.getHeight()/1.5);
					bossLasers.add(bossLaserBullet);
					root.getChildren().add(bossLaserBullet);
					bossLasers.add(bossLaserBullet);
					bossLaserCondition = true;
			}
			bossShoot();
		}
	};
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		
		root = new Group();
		scene = new Scene(root, Color.BLACK);
		backgroundImage.setFitHeight(HEIGHT);
		backgroundImage.setFitWidth(WIDTH);
		backgroundImage.relocate(0,0);
		
		Image windowIcon = new Image("smiley-face.png");
		stage.getIcons().add(windowIcon);
		stage.setHeight(HEIGHT);
		stage.setWidth(WIDTH);
		stage.setResizable(false);
		stage.setTitle("AP CS Create Task");
	
		root.getChildren().add(backgroundImage);
		MainMenu();
		stage.setScene(scene);
		stage.show();
		

	}
	
	public void MainMenu() {
		startIcon.relocate(WIDTH/2 - (startIconImage.getWidth()/2), HEIGHT/2 - 200);
		startIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				startGame();
				root.getChildren().remove(startIcon);
				root.getChildren().remove(instrIcon);
			}

		});
		
		instrIcon.relocate(WIDTH/2 - (instrIconImage.getWidth()/2), HEIGHT/2 - 100);
		backIcon.relocate(10, HEIGHT/2);
		instrIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				root.getChildren().remove(startIcon);
				root.getChildren().remove(instrIcon);

				root.getChildren().add(directionsIcon);
				root.getChildren().add(backIcon);
			}
			
		});
		
		backIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				root.getChildren().remove(directionsIcon);
				root.getChildren().remove(backIcon);
				root.getChildren().add(startIcon);
				root.getChildren().add(instrIcon);
				
			}
			
		});
		root.getChildren().add(startIcon);
		root.getChildren().add(instrIcon);
	} 
	
	//Adds all of the basics into the game including the positioning of the sprites, size of the sprites, and the sprites' health.
	public void startGame() {
		bossDelta = 6;
		bossInterval = 500;
		bossTimerStart();
		bossLaserTimerStart();
		gameTimer.stop();
		player = playerIcon;
		player.relocate(WIDTH/2 - playerIconImage.getWidth(), HEIGHT/2);
		
		boss = bossIcon;
		bossIcon.relocate(WIDTH/2 - bossIconImage.getWidth()/2, 100);
		
		dottedLine = dottedLineIcon;
		dottedLineIcon.relocate(0,400);
		dottedLineIcon.setFitWidth(1000);
		
		playerHealthIcon.setFitWidth(1000);
		playerHealthIcon.relocate(0, HEIGHT - 50);
		
		bossHealthIcon.setFitWidth(1000);
		bossHealthIcon.relocate(0,0);
		bossHealth = startingBossHealth;
		playerHealth = startingPlayerHealth;

		
		
		
		if(winLoseCondition = true) {
		//uses a simple switch method to detect if the user is pressing any of the given controls
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch(event.getCode()) {
				case W: goUp = true; break;
				case A: goLeft = true; break;
				case S: goDown = true; break;
				case D: goRight = true; break;
				case SHIFT: boost = true; break;
				//When SPACE is pressed, a new laser image will be added to the existing lasers array. the boolean "laser" is originally
				//set to false, so IF laser is false, it'll spawn a new laser in when pressing SPACE. After the laser is spawned in, the 
				//boolean will then be set to true, and back to false on keyRelease. This prevents players from holding down the 
				//space bar to shoot very fast
				case SPACE: 
					if(!laser && winLoseCondition == true) {
					Image laserIconImage = new Image("laser.png");
					ImageView laserIcon = new ImageView(laserIconImage);
					laserBullet = laserIcon;
					laserBullet.relocate(player.getLayoutX() + 15, player.getLayoutY() - 50);
					lasers.add(laserBullet);
					root.getChildren().add(laserBullet);
					laser = true;
				} break;
				}
			}
		});
		}
		
		//Uses a simple switch method to set the keys' booleans to false on release
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				switch(event.getCode()) {
				case W: goUp = false; break;
				case A: goLeft = false; break;
				case S: goDown = false; break;
				case D: goRight = false; break;
				case SHIFT: boost = false; break;
				case SPACE: laser = false; break;
				}
			}
			
		});
		
		
		gameTimer.start();
		
		
		
		//Adds the background image and player sprite to the scene.
		root.getChildren().add(bossIcon);
		root.getChildren().add(player);
		root.getChildren().add(playerHealthIcon);
		root.getChildren().add(bossHealthIcon);
		root.getChildren().add(dottedLineIcon);
	}
	//all playerCollsion() does is check if the player sprite is within the window boundaries, and if it hits the border,
	//it'll ignore the key presses that are needed to move.
	public void  playerCollision(double x, double y) {
		if(x - 5 > 0 && x + playerIconImage.getWidth() + 20 < WIDTH && y + playerIconImage.getHeight() + 45 < HEIGHT && y > dottedLine.getLayoutY() + 10) {
			player.relocate(x, y);
		}
		if(player.getLayoutX() >= boss.getLayoutX() && player.getLayoutX() + 5 <= boss.getLayoutX() + bossIconImage.getWidth() && player.getLayoutY() - playerIconImage.getHeight() <= boss.getLayoutY() && player.getLayoutY() >= boss.getLayoutY() - bossIconImage.getHeight()/2) {
			playerHealth -= 50;
		}
	}
	
	public void bossCollision() {
		if(boss.getLayoutX() <= 0) {
			bossDelta = -bossDelta;
		}else if(boss.getLayoutX() + bossIconImage.getWidth() >= 1000) {
			bossDelta = -bossDelta;
		}
	}
	
	//Iterates through every laser that is "spawned" in the array. And for every laser that is spawned, it will move the laser
	//across the Y value to make it look like it's shooting upwards. If the laser touches the boss, the laser will be removed and the boss's 
	//health will be deducted 10 health.
	
	public void shootLaser() {
		for(int i = 0; i < lasers.size(); i++) {
			if(lasers.get(i).getLayoutY() <= boss.getLayoutY() + bossIconImage.getHeight() 
			&& lasers.get(i).getLayoutY() >= boss.getLayoutY() - bossIconImage.getHeight()/2  
			&& lasers.get(i).getLayoutX() >= boss.getLayoutX()  - 10 
			&& lasers.get(i).getLayoutX() <= boss.getLayoutX() + bossIconImage.getWidth()) {
				root.getChildren().remove(lasers.get(i));
				lasers.remove(i);
				bossHealthBarDrain(-10);
			}else if(lasers.get(i).getLayoutY() > 0) {
				lasers.get(i).relocate(lasers.get(i).getLayoutX(), lasers.get(i).getLayoutY() - laserVelocity);
				}else {
				root.getChildren().remove(lasers.get(i));
				lasers.remove(i);
				playerHealthBarDrain(-5);
				
				
			}
		}
	}
	//Creates the laser for the boss and repositions it to line up with the boss model's position. Also adds the newly created laser to an array list.
	public void createBossLaser() {
		Image bossLaserIconImage = new Image("boss laser.png");
		ImageView bossLaserIcon = new ImageView(bossLaserIconImage);
		bossLaserBullet = bossLaserIcon;
		bossLaserBullet.relocate(boss.getLayoutX() + bossIconImage.getWidth()/2 - bossLaserIconImage.getWidth()/2, boss.getLayoutY() + bossIconImage.getHeight()/1.5);
		bossLasers.add(bossLaserBullet);
		root.getChildren().add(bossLaserBullet);
		bossLasers.add(bossLaserBullet);
	}


	//very similar to bossHealthBarDrain
	public void playerHealthBarDrain(int z) {
		playerHealth += z;
		int x;
		int y = WIDTH / startingPlayerHealth;
		for(var i = 0; i < playerHealth; i++) {
			if(playerHealth == playerHealth - i) {
				x = playerHealth - i;
				newPlayerHpWidth = x * y;
				playerHealthIcon.setFitWidth(newPlayerHpWidth);
			}
		}
		if(playerHealth == 0) {
			playerHealthIcon.setFitWidth(0.1);
			lose();
		}
	}
	
	//Explained in writing portion
	public void bossHealthBarDrain(int z) {
		bossHealth += z;
		int x;
		int y = WIDTH / startingBossHealth;
		for(var i = 0; i < bossHealth; i++) {
			if(bossHealth <= bossHealth - i) {
				x = bossHealth - i;
				newBossHpWidth = x * y;
				bossHealthIcon.setFitWidth(newBossHpWidth);
			}
		}
		if(bossHealth <= 70 && bossHealth > 30) {
			bossDelta = 7;
		}
		else if(bossHealth <= 30 && bossHealth > 10) {
			bossDelta = 8;
			updateBossLaserTimer(300);
		}
		else if(bossHealth <= 10 && bossHealth > 0) {
			bossDelta = 9;
			updateBossLaserTimer(200);
		}
		else if(bossHealth <= 0) {
			bossHealthIcon.setFitWidth(0.1);
			win();
		}
	}

//When you restart the game it removes and adds different sprites, or reset the value of some variables.
	public void restart() {
		bossDelta = 6;
		bossInterval = 500;
		bossTimerStart();
		bossLaserTimerPause();
		bossLaserTimerStart();
		root.getChildren().remove(winIcon);
		root.getChildren().remove(MenuIcon);
		player.relocate(WIDTH/2 - playerIconImage.getWidth(), HEIGHT/2);
		root.getChildren().add(player);
		root.getChildren().add(bossIcon);
		root.getChildren().add(playerHealthIcon);
		root.getChildren().add(bossHealthIcon);
		root.getChildren().add(dottedLineIcon);
		bossHealth = startingBossHealth;
		playerHealth = startingPlayerHealth;
		playerHealthBarDrain(0);
		bossHealthBarDrain(0);
		winLoseCondition = true;
		bossTimerCondition = false;
	}
	
//When you win the game it removes and adds different sprites, or reset the value of some variables.
	public void win() {
		bossTimerPause();
		bossLaserTimerPause();
		player.relocate(-100,500);
		root.getChildren().remove(bossIcon);
		root.getChildren().remove(player);
		root.getChildren().remove(playerHealthIcon);
		root.getChildren().remove(bossHealthIcon);
		root.getChildren().remove(dottedLineIcon);

		winIcon.setFitWidth(500);
		winIcon.setFitHeight(140);
		winIcon.relocate(WIDTH/2 - winIconImage.getWidth(),50);
		root.getChildren().add(winIcon);       
		laser = true;
		
		root.getChildren().add(MenuIcon);
		MenuIcon.relocate(WIDTH/2 - MenuIconImage.getWidth()/2, HEIGHT/2 - MenuIconImage.getHeight()/2 + 50);
		MenuIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				MainMenu();
				root.getChildren().remove(MenuIcon);
				root.getChildren().remove(winIcon);
				root.getChildren().remove(retryIcon);
				
			}
		});
		
		retryIcon.relocate(WIDTH/2 - retryIconImage.getWidth()/2, HEIGHT/2 - retryIconImage.getHeight()/2);
		retryIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				root.getChildren().remove(retryIcon);
				root.getChildren().remove(loseIcon);
				restart();
			}

			
		});
		root.getChildren().add(retryIcon);
	}
	
//When you lose the game it removes and adds different sprites, or reset the value of some variables.
	public void lose() {
		bossLaserTimerPause();
		bossTimerPause();
		player.relocate(-100,500);
		root.getChildren().remove(bossIcon);
		root.getChildren().remove(player);
		root.getChildren().remove(playerHealthIcon);
		root.getChildren().remove(bossHealthIcon);
		root.getChildren().remove(dottedLineIcon);
		loseIcon.setFitWidth(500);
		loseIcon.setFitHeight(140);
		loseIcon.relocate(WIDTH/2 - loseIconImage.getWidth(), 50);
		root.getChildren().add(loseIcon);
		root.getChildren().add(MenuIcon);
		MenuIcon.relocate(WIDTH/2 - MenuIconImage.getWidth()/2 - 50, HEIGHT/2 - MenuIconImage.getHeight()/2 + 50);
		MenuIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				MainMenu();
				root.getChildren().remove(MenuIcon);
				root.getChildren().remove(loseIcon);
				root.getChildren().remove(retryIcon);
				
			}
			
		});
		
		
		retryIcon.relocate(WIDTH/2 - retryIconImage.getWidth(), HEIGHT/2 - retryIconImage.getHeight()/2);
		retryIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				root.getChildren().remove(retryIcon);
				root.getChildren().remove(loseIcon);
				root.getChildren().remove(MenuIcon);
				restart();
			}

			
		});
		root.getChildren().add(retryIcon);
			
	}
	//First portion detects if the laser collides with the player sprite, and if it does, it removes the laser and drains the player's hp
	//The 2 else if statements just detects if the laser is in bound of the window, and if it is in the game window, it moves, if not, it gets removed.
	public void bossShoot() {
		for(int i = 0; i < bossLasers.size(); i ++) {
				if(bossLasers.get(i).getLayoutY() <= player.getLayoutY() + playerIconImage.getHeight() 
				&& bossLasers.get(i).getLayoutY() >= player.getLayoutY() - playerIconImage.getHeight()/2 
				&& bossLasers.get(i).getLayoutX() >= player.getLayoutX()  - 10 
				&& bossLasers.get(i).getLayoutX() <= player.getLayoutX() + playerIconImage.getWidth()) {
					root.getChildren().remove(bossLasers.get(i));
					playerHealthBarDrain(-5);
					bossLasers.remove(i);
				}else if(bossLasers.get(i).getLayoutY() < HEIGHT) {
					bossLasers.get(i).relocate(bossLasers.get(i).getLayoutX(), bossLasers.get(i).getLayoutY() + laserVelocity/2);
				}else if(bossLasers.get(i).getLayoutY() >= 1000) {
					root.getChildren().remove(bossLasers.get(i));
					bossLasers.remove(i);
				}
				

		}
	}
	
	public void bossTimerPause() {
		bossTimer.cancel();
		bossLaserTimer.cancel();
	}
	
	public void bossLaserTimerPause() {
		bossLaserTimer.cancel();
	}
	
	public void bossTimerStart() {
		bossTimer = new Timer();
		int n = bossInterval;
		bossTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				randomNumber = Math.floor(Math.random()*3);
				if(randomNumber == 1.0) {
					bossDelta = -bossDelta;
				}
			}
		}, 0, 600);
		
	}
	
	public void bossLaserTimerStart() {
		bossLaserTimer = new Timer();
		int n = bossInterval;
		bossLaserTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				bossLaserCondition = false;
			}
			
		}, 0, n);
	}
	
	public void updateBossLaserTimer(int x) {
		bossLaserTimerPause();
		bossInterval = x;
		bossLaserTimerStart();
	}
	
}


