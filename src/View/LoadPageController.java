package View;

import Model.IModel;
import Model.Model;
import ViewModel.MyViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoadPageController{
    @FXML
    private AnchorPane rootPane;
    private IModel model;
    MyViewController myViewController;

    public void StartGame()  throws Exception{
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../View/MyView.fxml"));
        Parent pane =fxmlLoader.load();
        Scene scene = new Scene( pane,rootPane.getScene().getWidth(),rootPane.getScene().getHeight() );
        model = new Model();
        MyViewModel myViewModel = new MyViewModel(model);
        myViewController = fxmlLoader.getController();
        myViewController.setMyViewModel(myViewModel);
        myViewModel.addObserver(myViewController);
        stage.setScene(scene);
    }

    public void exitProgram()
    {
        if (model!=null)
            myViewController.ExitClick();
    }
}
