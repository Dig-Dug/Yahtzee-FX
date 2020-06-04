package de.cbw.jav.yahtzee.fx;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * FX Application class. Note that FX application classes must feature
 * a public no-arg constructor because the FX launch() method uses the
 * former to create an instance of said class!
 * @author Sascha Baumeister
 */
public class YahtzeeFxApp extends Application  {
	static private final String TITLE = "Play Yahtzee!!";
	static private final double WIDTH = 800;
	static private final double HEIGHT = 600;
	static private final String PACKAGE_PATH = YahtzeeFxApp.class.getPackage().getName().replace('.', '/');
	static private final String MAIN_FXML_PATH  = PACKAGE_PATH + "/main.fxml";
	static private final String MAIN_CSS_PATH  = PACKAGE_PATH + "/main.css";
	static private final String MAIN_ICON_PATH = PACKAGE_PATH + "/main1.png";

	private MainController controller = null;
	

	/**
	 * Application entry point. Calls the launch() method to create an instance of
	 * this application class, and invoke the start() method.
	 * @param args the runtime arguments
	 */
	static public void main (String[] args) {
		YahtzeeFxApp.launch(args);
	}
//////////MAKE NEW WINDOW WITH TOP SCORE/////////////

	/**
	 * Configures the primary window and it's components, and displays the former.
	 * @param primaryWindow the primary application window
	 * @throws IOException if there is an I/O related problem
	 * @throws SQLException if there is an SQL related problem
	 */
	@Override
	public void start (final Stage primaryWindow) throws IOException, SQLException   {
		final Image icon = newImage(MAIN_ICON_PATH);
		final VBox pane = newPane(MAIN_FXML_PATH);
		this.controller = new MainController(primaryWindow, pane);

		final Scene sceneGraph = new Scene(pane, WIDTH, HEIGHT);
		sceneGraph.getStylesheets().add(MAIN_CSS_PATH);

		primaryWindow.setResizable(false);
		primaryWindow.setTitle(TITLE);
		primaryWindow.getIcons().add(icon);
		primaryWindow.setScene(sceneGraph);
		primaryWindow.setWidth(sceneGraph.getWidth());
		primaryWindow.setHeight(sceneGraph.getHeight());
		primaryWindow.show();
	}


	/**
	 * Closes any resources upon closing the primary window.
	 * @throws SQLException if there is an SQL related problem
	 */
	@Override
	public void stop () throws SQLException {
		this.controller.close();
	}


	/**
	 * Creates a new image and returns it.
	 * @param imagePath the image path
	 * @return the image
	 * @throws IOException if there is an I/O related problem
	 */
	static public Image newImage (String imagePath) throws IOException {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream byteSource = classLoader.getResourceAsStream(imagePath)) {
			return new Image(byteSource);
		}
	}


	/**
	 * Creates a new pane and returns it.
	 * @param <T> the resulting pane type
	 * @param fxmlPath the FXML path
	 * @return the pane
	 * @throws IOException 
	 */
	static public <T extends Pane> T newPane (final String fxmlPath) throws IOException {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream byteSource = classLoader.getResourceAsStream(fxmlPath)) {
			return new FXMLLoader().load(byteSource);
		}
	}
}