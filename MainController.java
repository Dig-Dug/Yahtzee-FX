package de.cbw.jav.yahtzee.fx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * Controller class handling events of the main pane.
 * 
 * @author Sascha Baumeister
 */
public class MainController implements AutoCloseable {
	static private final List<String> ROLL_STYLE_CLASSES = Arrays.asList("dice-button-0", "dice-button-1", "dice-button-2", "dice-button-3", "dice-button-4", "dice-button-5", "dice-button-6");
	static private final Random RANDOMIZER = new SecureRandom();

	private final Path hiscorePath;
	private final int[] dices;
	private final Yahtzee yahtzee;
	private int rollCount;
	private int scoreCount;

	// the pane controlled by a controller instance
	private Stage window;
	private final VBox pane;

	// children of above pane that provide processing information,
	private final TextField aliasField;
	private final ToggleButton[] diceButtons, upperButtons, lowerButtons;
	private final Label acesOutput, twosOutput, threesOutput, foursOutput, fivesOutput, sixesOutput;
	private final Label threeOfKindOutput, fourOfKindOutput, fullHouseOutput, smallStraightOutput, largeStraightOutput, fiveOfKindOutput, chanceOutput;
	private final Label upperSectionSumOutput, upperSectionBonusOutput, upperSectionScoreOutput, lowerSectionScoreOutput, lowerSectionTotalOutput;

	// bottom pane
	private final Button rollButton;


	/**
	 * Extracts the children of the main pane into instance variables, and registers
	 * the event handlers for the former.
	 * @throws NullPointerException if any of the given arguments is {@code null}
	 */
	public MainController (final Stage mainWindow, final VBox mainPane) throws IOException {
		this.hiscorePath = Paths.get("yahtzee-hiscore.txt").toAbsolutePath();
		Logger.getGlobal().log(Level.INFO, String.format("Highscore path is %s", this.hiscorePath));

		this.dices = new int[5];
		this.yahtzee = new Yahtzee();
		this.rollCount = this.scoreCount = 0;
		
		// initialize view
		this.window = Objects.requireNonNull(mainWindow); 
		this.pane = Objects.requireNonNull(mainPane);
		
		final HBox iconPane = (HBox) this.pane.getChildren().get(0);
		this.aliasField = (TextField) iconPane.getChildren().get(0);
		
		final ImageView image1 = (ImageView) iconPane.getChildren().get(1); // ==> Bear Logo
		image1.setManaged(false);
		image1.setX(200);
		image1.setY(280);

		final ImageView image2 = (ImageView) this.pane.getChildren().get(1); // ==> Yatzy Logo
		image2.setManaged(false);
		image2.setX(140);
		image2.setY(-10);
//		final ImageView image3 = (ImageView) this.pane.getChildren().get(2);
		
//		final Label copyright = new Label("just me myself and I");
//		copyright.setManaged(false);
//		copyright.setX(140);
//		copyright.setY(-10);
		

		final HBox dicePane = (HBox) this.pane.getChildren().get(2);
		final HBox sectionPane = (HBox) this.pane.getChildren().get(3);
		final GridPane upperSection = (GridPane) sectionPane.getChildren().get(0);
		final GridPane lowerSection = (GridPane) sectionPane.getChildren().get(1);

		// dice pane content
		this.diceButtons = new ToggleButton[] {
			(ToggleButton) dicePane.getChildren().get(0),
			(ToggleButton) dicePane.getChildren().get(1),
			(ToggleButton) dicePane.getChildren().get(2),
			(ToggleButton) dicePane.getChildren().get(3),
			(ToggleButton) dicePane.getChildren().get(4)
		};
		this.rollButton = (Button) dicePane.getChildren().get(5);

		// upper section content
		this.upperButtons = new ToggleButton[] {
			(ToggleButton) upperSection.getChildren().get(0),
			(ToggleButton) upperSection.getChildren().get(2),
			(ToggleButton) upperSection.getChildren().get(4),
			(ToggleButton) upperSection.getChildren().get(6),
			(ToggleButton) upperSection.getChildren().get(8),
			(ToggleButton) upperSection.getChildren().get(10)
		};
		this.acesOutput = (Label) upperSection.getChildren().get(1);
		this.twosOutput = (Label) upperSection.getChildren().get(3);
		this.threesOutput = (Label) upperSection.getChildren().get(5);
		this.foursOutput = (Label) upperSection.getChildren().get(7);
		this.fivesOutput = (Label) upperSection.getChildren().get(9);
		this.sixesOutput = (Label) upperSection.getChildren().get(11);
		this.upperSectionSumOutput = (Label) upperSection.getChildren().get(13);
		this.upperSectionBonusOutput = (Label) upperSection.getChildren().get(15);
		this.upperSectionScoreOutput = (Label) upperSection.getChildren().get(17);

		// bottom section content
		this.lowerButtons = new ToggleButton[] {
				(ToggleButton) lowerSection.getChildren().get(0),
				(ToggleButton) lowerSection.getChildren().get(2),
				(ToggleButton) lowerSection.getChildren().get(4),
				(ToggleButton) lowerSection.getChildren().get(6),
				(ToggleButton) lowerSection.getChildren().get(8),
				(ToggleButton) lowerSection.getChildren().get(10),
				(ToggleButton) lowerSection.getChildren().get(12)
		};
		this.threeOfKindOutput = (Label) lowerSection.getChildren().get(1);
		this.fourOfKindOutput = (Label) lowerSection.getChildren().get(3);
		this.fullHouseOutput = (Label) lowerSection.getChildren().get(5);
		this.smallStraightOutput = (Label) lowerSection.getChildren().get(7);
		this.largeStraightOutput = (Label) lowerSection.getChildren().get(9);
		this.fiveOfKindOutput = (Label) lowerSection.getChildren().get(11);
		this.chanceOutput = (Label) lowerSection.getChildren().get(13);
		this.lowerSectionScoreOutput = (Label) lowerSection.getChildren().get(15);
		this.lowerSectionTotalOutput = (Label) lowerSection.getChildren().get(17);

		// register event handlers
		this.rollButton.setOnAction(event -> { this.handleRollButtonAction(); event.consume(); });
		this.rollButton.setOnTouchPressed(event -> { this.handleRollButtonAction(); event.consume(); });

		this.upperButtons[0].setOnAction(event -> this.handleDiceToggleButtonAction(1));
		this.upperButtons[0].setOnTouchPressed(event -> this.handleDiceToggleButtonAction(1));
		this.upperButtons[1].setOnAction(event -> this.handleDiceToggleButtonAction(2));
		this.upperButtons[1].setOnTouchPressed(event -> this.handleDiceToggleButtonAction(2));
		this.upperButtons[2].setOnAction(event -> this.handleDiceToggleButtonAction(3));
		this.upperButtons[2].setOnTouchPressed(event -> this.handleDiceToggleButtonAction(3));
		this.upperButtons[3].setOnAction(event -> this.handleDiceToggleButtonAction(4));
		this.upperButtons[3].setOnTouchPressed(event -> this.handleDiceToggleButtonAction(4));
		this.upperButtons[4].setOnAction(event -> this.handleDiceToggleButtonAction(5));
		this.upperButtons[4].setOnTouchPressed(event -> this.handleDiceToggleButtonAction(5));
		this.upperButtons[5].setOnAction(event -> this.handleDiceToggleButtonAction(6));
		this.upperButtons[5].setOnTouchPressed(event -> this.handleDiceToggleButtonAction(6));

		this.lowerButtons[0].setOnAction(event -> this.handleThreeOfKindButtonAction());
		this.lowerButtons[0].setOnTouchPressed(event -> this.handleThreeOfKindButtonAction());
		this.lowerButtons[1].setOnAction(event -> this.handleFourOfKindButtonAction());
		this.lowerButtons[1].setOnTouchPressed(event -> this.handleFourOfKindButtonAction());
		this.lowerButtons[2].setOnAction(event -> this.handleFullHouseButtonAction());
		this.lowerButtons[2].setOnTouchPressed(event -> this.handleFullHouseButtonAction());
		this.lowerButtons[3].setOnAction(event -> this.handleSmallStraightButtonAction());
		this.lowerButtons[3].setOnTouchPressed(event -> this.handleSmallStraightButtonAction());
		this.lowerButtons[4].setOnAction(event -> this.handleLargeStraightButtonAction());
		this.lowerButtons[4].setOnTouchPressed(event -> this.handleLargeStraightButtonAction());
		this.lowerButtons[5].setOnAction(event -> this.handleFiveOfKindButtonAction());
		this.lowerButtons[5].setOnTouchPressed(event -> this.handleFiveOfKindButtonAction());
		this.lowerButtons[6].setOnAction(event -> this.handleChanceButtonAction());
		this.lowerButtons[6].setOnTouchPressed(event -> this.handleChanceButtonAction());

		this.startNewMatch();
	}


	/**
	 * Closes the resources if this controller, for example it's JDBC database
	 * connection, files, TCP ports etc.
	 */
	public void close () {}


	/**
	 * Returns the controlled pane.
	 * 
	 * @return the controlled pane
	 */
	public VBox getPane () {
		return this.pane;
	}


	private void handleRollButtonAction () {
		if (this.rollCount == 0 && this.scoreCount == 0) {
			for (final ToggleButton button : this.upperButtons)	button.setDisable(false);
			for (final ToggleButton button : this.lowerButtons)	button.setDisable(false);
		}

		for (int index = 0; index < this.dices.length; ++index) {
			final ToggleButton button = this.diceButtons[index];
			button.setDisable(false);
			if (button.isSelected()) continue;

			final int value = RANDOMIZER.nextInt(6) + 1;
			this.dices[index] = value;

			button.getStyleClass().removeAll(ROLL_STYLE_CLASSES);
			button.getStyleClass().add("dice-button-" + value);
		}

		if (++this.rollCount == 3) {
			for (final ToggleButton button : this.diceButtons)
				button.setDisable(true);

			this.rollButton.setDisable(true);
			this.rollCount = 0;
		}
	}


	private void handleDiceToggleButtonAction (final int value) throws IllegalArgumentException {
		switch (value) {
		case 1:
			this.yahtzee.setAces(this.dices);
			this.acesOutput.setText(Integer.toString(this.yahtzee.getAces()));
			break;
		case 2:
			this.yahtzee.setTwos(this.dices);
			this.twosOutput.setText(Integer.toString(this.yahtzee.getTwos()));
			break;
		case 3:
			this.yahtzee.setThrees(this.dices);
			this.threesOutput.setText(Integer.toString(this.yahtzee.getThrees()));
			break;
		case 4:
			this.yahtzee.setFours(this.dices);
			this.foursOutput.setText(Integer.toString(this.yahtzee.getFours()));
			break;
		case 5:
			this.yahtzee.setFives(this.dices);
			this.fivesOutput.setText(Integer.toString(this.yahtzee.getFives()));
			break;
		case 6:
			this.yahtzee.setSixes(this.dices);
			this.sixesOutput.setText(Integer.toString(this.yahtzee.getSixes()));
			break;
		default:
			throw new IllegalArgumentException();
		}

		this.displayScores();
		this.upperButtons[value - 1].setDisable(true);
		this.resetRollButtons();
		
		
	}


	private void handleThreeOfKindButtonAction () {
		this.yahtzee.setThreeOfKind(this.dices);
		this.threeOfKindOutput.setText(Integer.toString(this.yahtzee.getThreeOfKind()));
		this.displayScores();
		this.lowerButtons[0].setDisable(true);
		this.resetRollButtons();
	}


	private void handleFourOfKindButtonAction () {
		this.yahtzee.setFourOfKind(this.dices);
		this.fourOfKindOutput.setText(Integer.toString(this.yahtzee.getFourOfKind()));
		this.displayScores();
		this.lowerButtons[1].setDisable(true);
		this.resetRollButtons();
	}


	private void handleFullHouseButtonAction () {
		this.yahtzee.setFullHouse(this.dices);
		this.fullHouseOutput.setText(Integer.toString(this.yahtzee.getFullHouse()));
		this.displayScores();
		this.lowerButtons[2].setDisable(true);
		this.resetRollButtons();
	}


	private void handleSmallStraightButtonAction () {
		this.yahtzee.setSmallStraight(this.dices);
		this.smallStraightOutput.setText(Integer.toString(this.yahtzee.getSmallStraight()));
		this.displayScores();
		this.lowerButtons[3].setDisable(true);
		this.resetRollButtons();
	}


	private void handleLargeStraightButtonAction () {
		this.yahtzee.setLargeStraight(this.dices);
		this.largeStraightOutput.setText(Integer.toString(this.yahtzee.getLargeStraight()));
		this.displayScores();
		this.lowerButtons[4].setDisable(true);
		this.resetRollButtons();
	}


	private void handleFiveOfKindButtonAction () {
		this.yahtzee.setFiveOfKind(this.dices);
		this.fiveOfKindOutput.setText(Integer.toString(this.yahtzee.getFiveOfKind()));
		this.displayScores();
		this.lowerButtons[5].setDisable(true);
		this.resetRollButtons();
	}


	private void handleChanceButtonAction () {
		this.yahtzee.setChance(this.dices);
		this.chanceOutput.setText(Integer.toString(this.yahtzee.getChance()));
		this.displayScores();
		this.lowerButtons[6].setDisable(true);
		this.resetRollButtons();
	}


	private void displayScores () {
		final int upperSectionSum = this.yahtzee.upperSectionSum();
		final int upperSectionBonus = this.yahtzee.upperSectionBonus();
		final int upperSectionScore = this.yahtzee.upperSectionScore();
		final int lowerSectionScore = this.yahtzee.lowerSectionScore();
		final int lowerSectionTotal = this.yahtzee.total();

		this.upperSectionSumOutput.setText(Integer.toString(upperSectionSum));
		this.upperSectionBonusOutput.setText(Integer.toString(upperSectionBonus));
		this.upperSectionScoreOutput.setText(Integer.toString(upperSectionScore));
		this.lowerSectionScoreOutput.setText(Integer.toString(lowerSectionScore));
		this.lowerSectionTotalOutput.setText(Integer.toString(lowerSectionTotal));
	}
	
	
	public SortedSet<YahtzeeScore> loadHighscores () throws IOException {
		final SortedSet<YahtzeeScore> result = new TreeSet<>(Comparator.reverseOrder());

		if (!Files.isReadable(this.hiscorePath)) Files.createFile(this.hiscorePath);
		try (BufferedReader reader = Files.newBufferedReader(this.hiscorePath)) {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				line = line.trim();
				if(line.isEmpty()) continue;

				final YahtzeeScore score = YahtzeeScore.fromJsonString(line);
				result.add(score);
			}
		}

		return result;
	}


	public void saveHighscores (final SortedSet<YahtzeeScore> highscores)  throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(this.hiscorePath, StandardOpenOption.CREATE)) {
			for (final YahtzeeScore score : highscores) {
				writer.write(score.toJsonString());
				writer.newLine();
				continue;
			}
		}
	}


	public YahtzeeScore newScore () {
		final YahtzeeScore score = new YahtzeeScore();
		score.setUserAlias(this.aliasField.getText());
		score.setTotalScore(Integer.parseInt(this.lowerSectionTotalOutput.getText()));
		score.setUpperSectionScore(Integer.parseInt(this.upperSectionScoreOutput.getText()));
		score.setLowerSectionScore(Integer.parseInt(this.lowerSectionScoreOutput.getText()));
		score.setTimestamp(System.currentTimeMillis());
		return score;
	}


	private void resetRollButtons () throws UncheckedIOException {
		this.rollCount = 0;
		if (++this.scoreCount < 13) {
			this.rollButton.setDisable(false);

			for (final ToggleButton button : this.diceButtons) {
				button.getStyleClass().removeAll(ROLL_STYLE_CLASSES);
				button.getStyleClass().add("dice-button-0");
				button.setSelected(false);
			}
		} else {
			try {
				this.scoreCount = 0;
				this.handleGameOver();
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}


	private void handleGameOver () throws IOException {
		final SortedSet<YahtzeeScore> highscores = this.loadHighscores();
		final YahtzeeScore newScore = this.newScore();
		newScore.setUserAlias(this.aliasField.getText().trim());
		highscores.add(newScore);
		this.saveHighscores(highscores);

		final Image icon  = YahtzeeFxApp.newImage("de/cbw/jav/yahtzee/fx/main1.png");	
		final VBox pane = YahtzeeFxApp.newPane("de/cbw/jav/yahtzee/fx/main2.fxml");
		((Label) pane.getChildren().get(2)).setText(String.format("Upper Section Total: %d", this.yahtzee.upperSectionScore()));
		((Label) pane.getChildren().get(3)).setText(String.format("Lower Section Total: %d", this.yahtzee.lowerSectionScore()));
		((Label) pane.getChildren().get(4)).setText(String.format("Total: %d", this.yahtzee.total()));
		((Label) pane.getChildren().get(5)).setText(String.format("%Tc", System.currentTimeMillis()));

		final Stage window = openWindow(this.window, Modality.WINDOW_MODAL, pane, "de/cbw/jav/yahtzee/fx/main.css", "game over", icon, true, false, 270, 500);
		window.setOnCloseRequest(event -> this.startNewMatch());
		final Button scoreButton = (Button) pane.getChildren().get(6);
		scoreButton.setOnAction(event -> this.handleScoreButtonAction(this.aliasField.getText()));
	}


	//(((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((
	private void handleScoreButtonAction (final String userAlias) {
		try {
			final SortedSet<YahtzeeScore> highscores = this.loadHighscores();
			final Image icon  = YahtzeeFxApp.newImage("de/cbw/jav/yahtzee/fx/main1.png");	
			final VBox pane = YahtzeeFxApp.newPane("de/cbw/jav/yahtzee/fx/main3.fxml");
			openWindow(this.window, Modality.APPLICATION_MODAL, pane, "de/cbw/jav/yahtzee/fx/main.css", "highscore", icon, true, false, 700, 400);

			// display max. first 10 of the highscores
			final GridPane scorePane = (GridPane) pane.getChildren().get(1);
			
			final AtomicInteger rowCounter = new AtomicInteger();
			highscores.stream().limit(10).forEach(score -> {
				scorePane.add(new Label(score.getUserAlias()), 0, rowCounter.get());
				scorePane.add(new Label(Integer.toString(score.getUpperSectionScore())), 1, rowCounter.get());
				scorePane.add(new Label(Integer.toString(score.getLowerSectionScore())), 2, rowCounter.get());
				scorePane.add(new Label(Integer.toString(score.getTotalScore())), 3, rowCounter.get());
				scorePane.add(new Label(String.format("%tF %tT", score.getTimestamp(), score.getTimestamp())), 4, rowCounter.getAndIncrement());
			});
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	
	static private Stage openWindow (final Stage parentWindow, final Modality modality, final Pane pane, final String stylesheetPath, final String title, final Image icon, final boolean resizable, final boolean iconified, final int width, final int height) {
		final Scene sceneGraph = new Scene(pane, width, height);
		if (stylesheetPath != null) sceneGraph.getStylesheets().add(stylesheetPath);

		final Stage window = new Stage();
		window.setTitle(title);

		if (icon != null) window.getIcons().add(icon);
		window.setScene(sceneGraph);
		window.setWidth(sceneGraph.getWidth());
		window.setHeight(sceneGraph.getHeight());
//		window.setAlwaysOnTop(true);
		window.setResizable(resizable);
		window.setIconified(iconified);
		if (parentWindow != null & modality != Modality.NONE) {
			window.initOwner(parentWindow);
			window.initModality(modality); 
		}
		
		window.show();
		return window;
	}


	public void startNewMatch () {
		this.yahtzee.reset();
		this.scoreCount = 0;
		this.rollCount = 0;
		Arrays.fill(this.dices, 0);

		this.rollButton.setDisable(false);
		for (final ToggleButton button : this.diceButtons)	button.setDisable(true);
		Stream.of(this.lowerButtons, this.upperButtons)
		.flatMap(array -> Stream.of(array))
		.forEach(button -> {
			button.setDisable(true);
			button.setSelected(false);
		});

		this.acesOutput.setText("");
		this.twosOutput.setText("");
		this.threesOutput.setText("");
		this.foursOutput.setText("");
		this.fivesOutput.setText("");
		this.sixesOutput.setText("");
		this.upperSectionSumOutput.setText("");
		this.upperSectionBonusOutput.setText("");
		this.upperSectionScoreOutput.setText("");
		this.threeOfKindOutput.setText("");
		this.fourOfKindOutput.setText("");
		this.fullHouseOutput.setText("");
		this.smallStraightOutput.setText("");
		this.largeStraightOutput.setText("");
		this.fiveOfKindOutput.setText("");
		this.chanceOutput.setText("");
		this.lowerSectionScoreOutput.setText("");
		this.lowerSectionTotalOutput.setText("");
	}
}