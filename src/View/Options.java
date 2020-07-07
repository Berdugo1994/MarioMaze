package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;

public class Options {
    @FXML
    private ChoiceBox choiceBox;
    @FXML
    private ChoiceBox choiceBox2;
    private Stage Mystage;
    MyViewController myViewController;
    public void setMyViewController(MyViewController myViewController) {
        this.myViewController = myViewController;
    }
    public void setOptionStage(Stage s){
        Mystage=s;
    }



    public void BackTo() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../View/MyView.fxml"));
    Parent pane =fxmlLoader.load();
    String TypeGenerator = choiceBox.getSelectionModel().getSelectedItem().toString();
    String TypeSolver = choiceBox2.getSelectionModel().getSelectedItem().toString();
    myViewController.getProp().setProperty("MazeGenerator",TypeGenerator);
    System.out.println(myViewController.getProp().getProperty("MazeGenerator"));
    myViewController.getProp().setProperty("MazeSolver",TypeSolver);
    myViewController.getProp();
    System.out.println(myViewController.getProp().getProperty("MazeSolver"));
    myViewController.getProp().store(new FileWriter("src/Resources/config.properties"), "store to properties file");
    myViewController.playFirst=false;
    Mystage.close();

}
public void setChoiceBox(){
    choiceBox.valueProperty().setValue(myViewController.getProp().getProperty("MazeGenerator","MyMaze"));
    }
    public void setChoiceBox2(){
        choiceBox2.valueProperty().setValue(myViewController.getProp().getProperty("MazeSolver","BestFS"));
    }
}
