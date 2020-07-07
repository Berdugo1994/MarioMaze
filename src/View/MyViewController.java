package View;
import Main.Main;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.io.*;
import java.net.URL;
import java.util.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.util.Timer;
import java.util.TimerTask;

public class MyViewController implements Initializable, Observer {
    @FXML
    public TextField textField_mazeRows;
    @FXML
    public TextField textField_mazeCols;
    @FXML
    public Label lbl_player_row;
    @FXML
    public Label lbl_player_col;
    @FXML
    public MazeDisplayer mazeDisplayer;
    @FXML
    public Pane CenterPane;
    @FXML
    public BorderPane MainBP;
    @FXML
    public Label ButtonOptions;
    @FXML
    private ChoiceBox PlayerChoiceBox;
    @FXML
    public Label choiceBoxLbl;
    @FXML
    public Button btn_Generate;
    @FXML
    public Button bt_Generate1;
    public MyViewModel myViewModel;
    private Maze maze;
    private Solution mazeSolution;
    private final FileChooser fileChooser = new FileChooser();
    private Properties prop;
    public static boolean playFirst=true;
    private MediaPlayer MainMediaPlayer;

    public Properties getProp() {
        return prop;
    }
    public void initialize(URL location, ResourceBundle resources) {
        try {
            prop= new Properties();
            prop.load(new FileInputStream("src/Resources/config.properties"));
            if(playFirst) {
                Media sound = new Media(new File("./src/Resources/sounds/Super Mario Bros. medley.mp3").toURI().toString());
                MainMediaPlayer = new MediaPlayer(sound);
                MainMediaPlayer.play();
                playFirst=false;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        lbl_player_row.textProperty().bind(update_player_pos_row);
        lbl_player_col.textProperty().bind(update_player_pos_col);
        Stage primaryStage= Main.getPrimaryStage();
        primaryStage.widthProperty().addListener(Sizelistener);
        primaryStage.heightProperty().addListener(Sizelistener);
        textField_mazeRows.textProperty().addListener((observable, oldValue, Rows) -> CheckValue(Rows,textField_mazeCols.textProperty().getValue()));
        textField_mazeCols.textProperty().addListener((observable, oldValue, Cols) -> CheckValue(textField_mazeRows.textProperty().getValue(),Cols));
        bt_Generate1.setDisable(true);
        btn_Generate.setDisable(true);
        //save Load Properties
        String directory = System.getProperty("java.io.tmpdir") + "SavedMaze" + File.separator;
        new File(directory).mkdirs();
        File f = new File(directory);
        fileChooser.setInitialDirectory(f);
        //ChoiceBox
        choiceBoxLbl.setText("Choose Player");
        PlayerChoiceBox.getItems().addAll("Mario", "Luigi", "Peach", "Yoshi", "Eden");
        PlayerChoiceBox.setValue("Mario");
    }

    StringProperty update_player_pos_row = new SimpleStringProperty();
    StringProperty update_player_pos_col = new SimpleStringProperty();

    public void setUpdate_player_pos_row(String update_player_pos_row) {
        this.update_player_pos_row.set(update_player_pos_row);
    }

    public void setUpdate_player_pos_col(String update_player_pos_col) {
        this.update_player_pos_col.set(update_player_pos_col);
    }
    @FXML
    public void Generate() {
        myViewModel.generateMaze(textField_mazeRows.getText(), textField_mazeCols.getText());
    }
    @FXML
    public void Solve() {
        myViewModel.Solve();
        mazeDisplayer.requestFocus();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MyViewModel) {
            if (myViewModel.MazeChanged()) {
                maze = myViewModel.getMaze();
                bt_Generate1.setDisable(false);
                mazeDisplayer.widthProperty().bind(CenterPane.widthProperty());
                mazeDisplayer.heightProperty().bind(CenterPane.heightProperty());
                newRun();
                mazeDisplayer.DisplayMaze(maze);
                mazeDisplayer.requestFocus();
                setUpdate_player_pos_row(myViewModel.getRowChar() + "");
                setUpdate_player_pos_col(myViewModel.getColChar() + "");
                mazeDisplayer.requestFocus();
            }
            if (myViewModel.isAskForSolution()) {
                mazeSolution = myViewModel.getMazeSolution();
                mazeDisplayer.DisplaySolution(mazeSolution);
                mazeDisplayer.requestFocus();

            }

            if (myViewModel.isMazeSolved()){
                if(myViewModel.isCharChanged()) {
                    setUpdate_player_pos_row(myViewModel.getRowChar() + "");
                    setUpdate_player_pos_col(myViewModel.getColChar() + "");
                    mazeDisplayer.PreviousView();
                    if(myViewModel.isByMouse()){
                        mazeDisplayer.drawPlayerByMouse(myViewModel.getRowChar(),myViewModel.getColChar());
                    }
                    else {
                        mazeDisplayer.drawPlayer(myViewModel.getLastPress());
                    }
                    mazeDisplayer.requestFocus();
                }
                setUpdate_player_pos_row("");
                setUpdate_player_pos_col("");
                myViewModel.play("APPLAUSE");
                showAlert("Would you like To play again ?");
            }
            if (myViewModel.isCharChanged()) {
                setUpdate_player_pos_row(myViewModel.getRowChar() + "");
                setUpdate_player_pos_col(myViewModel.getColChar() + "");
                mazeDisplayer.PreviousView();
                if(myViewModel.isByMouse()){
                    mazeDisplayer.drawPlayerByMouse(myViewModel.getRowChar(),myViewModel.getColChar());
                }
                else {
                    mazeDisplayer.drawPlayer(myViewModel.getLastPress());
                }
                myViewModel.play("STEP");
                mazeDisplayer.requestFocus();
            }
            if (myViewModel.doZoomIn) {
                zoomIn(true);
                myViewModel.doZoomIn = false;
            }
            if (myViewModel.doZoomOut) {
                zoomIn(false);
                myViewModel.doZoomOut = false;
            }
        }
    }

    public void setMyViewModel(MyViewModel myViewModel) {
        this.myViewModel = myViewModel;
    }
    public void keyPressed(KeyEvent keyEvent) {
        MainBP.getScene().setOnKeyReleased(event -> {
            if(event.getCode()==KeyCode.CONTROL){
                mazeDisplayer.blockZoom=true;
            }
        });
        Scene myScene = MainBP.getScene();
        if(keyEvent.getCode()==KeyCode.CONTROL) {
            mazeDisplayer.blockZoom = false;
            myScene.setOnScroll(event ->
            {
                if (event.getDeltaY() > 1) {
                        mazeDisplayer.onZoom(true);
                }
                if (event.getDeltaY() < 1) {
                    mazeDisplayer.onZoom(false);
                }
            });
        }
        myViewModel.moveCharacter(keyEvent);
        keyEvent.consume();
    }
    public void MouseClicked(){
        myViewModel.play("MOUSE");
    }
    final ChangeListener<Number> Sizelistener = new ChangeListener<Number>()
    {
        final Timer timer = new Timer(true); // uses a timer to call your resize method
        TimerTask task = null; // task to execute after defined delay
        final long delayTime = 359; // delay that has to pass in order to consider an operation done
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue)
        {
            if (task != null)
            {
                task.cancel(); // cancel it
            }
            task = new TimerTask() // create new task that calls your resize operation
            {
                @Override
                public void run()
                {
                    // here you can place your resize code
                    mazeDisplayer.draw();
                    zoomIn(false);
                    myViewModel.doZoomOut = false;
                }
            };
            // schedule new task
            timer.schedule(task, delayTime);
        }
    };
    public void Random(){
        myViewModel.RandomSize();
        textField_mazeRows.setText(myViewModel.TotalRows+"");
        textField_mazeCols.setText(myViewModel.TotalCols+"");
    }

    public void showAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        Image image = new Image("Resources/Images/Peach.jpg");
        ImageView imageView = new ImageView(image);
        alert.setGraphic(imageView);
        alert.setHeaderText("congratulations ! You have Solved The Maze");
        Optional<ButtonType> result = alert.showAndWait();
        alert.setResizable(false);
        if(!result.isPresent());
            // alert is exited, no button has been pressed.
        else if(result.get() == ButtonType.OK)
        {
            mazeDisplayer.cleanScreen();
            lbl_player_row.requestFocus();
            textField_mazeRows.requestFocus();
        }
        else if(result.get() == ButtonType.CANCEL){
            Stage myStage= (Stage)MainBP.getScene().getWindow();
            myStage.close();
        }
    }
    public void HandleSaveClicked(javafx.event.ActionEvent event)
    {
        Window stage = CenterPane.getScene().getWindow();
        fileChooser.setTitle("save Dialog");
        fileChooser.setInitialFileName("myMaze");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("textfile" , "*.txt"));
        try
        {
            File f = fileChooser.showSaveDialog(stage);
            if(f!=null) {
                myViewModel.SaveMaze(f);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
@FXML
private void handleLoadClicked(javafx.event.ActionEvent actionEvent) {
    Window stage = CenterPane.getScene().getWindow();
    fileChooser.setTitle("Load File");
    fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Documents", "*.docx", "*.txt", "*.pdf"));
    File file = fileChooser.showOpenDialog(stage);
    if(file!=null)//Case of cancel clicked
    {
        myViewModel.loadMaze(file);
    }
}
    public void OnMenuClick() {
        Parent root;
        try {
            FXMLLoader fxmlLoader =new FXMLLoader(getClass().getClassLoader().getResource("View/FxmlOptions.fxml"));
            Scene myScene= MainBP.getScene();
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("My New Stage Title");
            stage.setScene(new Scene(root, 300, 300));
            stage.show();
            Options otherController= fxmlLoader.getController();
            otherController.setMyViewController(this);
            otherController.setOptionStage(stage);
            otherController.setChoiceBox();
            otherController.setChoiceBox2();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void OnHowToPlayClick()
    {
        Parent root;
        try {
            FXMLLoader fxmlLoader =new FXMLLoader(getClass().getClassLoader().getResource("View/FxmlHowToPlay.fxml"));
            Scene myScene= MainBP.getScene();
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setScene(new Scene(root, 1000,700 ));
            stage.show();
            Options otherController= fxmlLoader.getController();
//            otherController.setMyViewController(this);
//            otherController.setOptionStage(stage);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void OnAboutClick()
    {
        Parent root;
        try {
            FXMLLoader fxmlLoader =new FXMLLoader(getClass().getClassLoader().getResource("View/FxmlAbout.fxml"));
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setScene(new Scene(root, 1000,700 ));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void choiceBoxButtonPushed() {
        String Character = PlayerChoiceBox.getValue().toString();
        mazeDisplayer.changeCharacter(Character);
    }

    public void zoomIn(boolean In){
        mazeDisplayer.onZoom(In);
    }
    public void newRun(){
        mazeDisplayer.zoom=false;
        mazeDisplayer.FirstDraw=true;
    }

    public void MUTE(){
        myViewModel.doMute();
        if(myViewModel.mute){
            MainMediaPlayer.stop();
        }
        else{
            MainMediaPlayer.play();
        }
        mazeDisplayer.requestFocus();
    }


    public void CheckValue(String Rows,String Cols){
        String regex = "\\d+";
        if (Rows.matches(regex)){
            if(Cols.matches(regex)) {
                btn_Generate.setDisable(false);
            }
            else if(Cols.equals("")){
                btn_Generate.setDisable(true);
                bt_Generate1.setDisable(true);
            }
            else{
                btn_Generate.setDisable(true);
                bt_Generate1.setDisable(true);
                BadKeyPressed("You Inserted a non digit "+Cols+" into COLS Text Field");
                lbl_player_col.requestFocus();
                textField_mazeCols.requestFocus();
            }
        }
        else if(Rows.equals("")){
            btn_Generate.setDisable(true);
            bt_Generate1.setDisable(true);
            if(Cols.matches(regex)||Cols=="");
            else{
                BadKeyPressed("You Inserted a non digit "+Cols+" into COLS Text Field");
                lbl_player_col.requestFocus();
                textField_mazeCols.requestFocus();
            }
        }
        else{
            btn_Generate.setDisable(true);
            bt_Generate1.setDisable(true);
            BadKeyPressed("You Inserted a non digit "+Cols+" into Rows Text Field");
            lbl_player_row.requestFocus();
            textField_mazeRows.requestFocus();
        }
    }
    public void BadKeyPressed(String Message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Try again");
        alert.setHeaderText(Message);
        alert.setResizable(false);
        alert.show();
    }
    public void DoSome(MouseEvent e){
        double x=e.getSceneX()-180.0 ;
        double y=e.getSceneY()-40.0 ;
        int CellRow  =mazeDisplayer.getRowChar(y);
        int CellCol =mazeDisplayer.getColChar(x);
        myViewModel.MoveCharWithMouse(CellRow,CellCol);
    }
    public void NewClick()
    {
        if (maze!=null){
        mazeDisplayer.cleanScreen();
        lbl_player_row.requestFocus();
        textField_mazeRows.requestFocus();}
    }

    public void ExitClick()
    {
        myViewModel.Exit();
        Stage primaryStage= Main.getPrimaryStage();
        primaryStage.close();
    }
}

