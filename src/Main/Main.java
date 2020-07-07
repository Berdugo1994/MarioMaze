package Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

import View.LoadPageController;

public class Main extends Application {
    private static Stage primaryStage;
    private FXMLLoader fxmlLoader;
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage=primaryStage;
        fxmlLoader = new FXMLLoader(getClass().getResource("../View/LoadPage.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Maze Game");
        //primaryStage.setFullScreen(true);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int ScreenWidth = gd.getDisplayMode().getWidth();
        int ScreenHeight = gd.getDisplayMode().getHeight();
        Scene scene = new Scene(root,930,500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void stop() throws Exception {
        LoadPageController Controller = fxmlLoader.getController();
        Controller.exitProgram();
    }

    public static void main(String[] args) {
        launch(args);
    }
    public static Stage getPrimaryStage(){return  primaryStage;}

}
