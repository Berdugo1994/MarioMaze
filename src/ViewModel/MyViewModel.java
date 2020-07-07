package ViewModel;
import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
public class MyViewModel extends Observable implements Observer, Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    private IModel model;
    private Maze maze;
    private int rowChar;
    private int colChar;
    public String TotalRows;
    public String TotalCols;
    private boolean mazeChanged;
    private boolean charChanged;
    private boolean mazeSolved;
    private int LastPress;
    private Solution mazeSolution;
    private boolean AskForSolution=false;
    public boolean doZoomIn;
    public boolean doZoomOut;
    public int scrollConuter;
    public boolean mute;
    public boolean byMouse;

    public MyViewModel(IModel model){
        byMouse=false;
        scrollConuter=0;
        this.model = model;
        this.model.assignObserver(this);
        this.maze = null;
        mazeChanged=false;
        charChanged=false;
        TotalCols="";
        TotalRows="";
        mazeSolution=null;
        mute=false;
    }

    @Override
    public void update(Observable o,Object arg) {
        if (o instanceof IModel) {
            if (model.isMazeChanged()) {//generate Maze
                maze = model.getMaze();
                this.rowChar=model.getRowCharacter();
                this.colChar=model.getColCharacter();
                mazeChanged=true;
            }
            if(model.AskForSolution())
            {
                mazeSolution = model.getGameSolution();
                AskForSolution=true;
            }

            if(this.rowChar!=model.getRowCharacter() || this.colChar!=model.getColCharacter()) {
                this.rowChar = model.getRowCharacter();
                this.colChar = model.getColCharacter();
                charChanged = true;
            }
            if(model.isMazeSolved()){mazeSolved=true;}
            setChanged();
            notifyObservers();//Let View Know that We Finished.
        }
    }

    public void generateMaze(String Srow,String  Scol){
        int row=0,col=0;
        try{
            row=Integer.parseInt(Srow);
            col=Integer.parseInt(Scol);
        }
        catch (Exception e){
            //TODO Insert Alert
        }
        if (row<3 || row>500 || col <3 || col>500){
            return;
        }
        this.model.GenerateGame(row,col);
    }

    public boolean MazeChanged(){
        boolean res=mazeChanged;
        mazeChanged=false;
        return res;
    }
    public boolean isCharChanged(){
        boolean res=charChanged;
        charChanged=false;
        return res;
    }
    public boolean isMazeSolved(){
        boolean res=mazeSolved;
        mazeSolved=false;
        return res;
    }

    public boolean isAskForSolution()
    {
        boolean res = AskForSolution;
        AskForSolution = false;
        return res;
    }

    public Maze getMaze(){
        return maze;
    }

    public Solution getMazeSolution() {
        return mazeSolution;
    }

    public void setMazeSolution(Solution mazeSolution) {
        this.mazeSolution = mazeSolution;
    }

    public int getRowChar() {
        return rowChar;
    }
    public int getColChar() {
        return colChar;
    }


    public void moveCharacter(KeyEvent keyEvent){
        int direction=-1;
        if(keyEvent==null){return;}
        switch (keyEvent.getCode()){
            case NUMPAD8://UP
            case UP:
                direction = 8;
                LastPress=8;
                break;
            case NUMPAD6://RIGHT
            case RIGHT:
                direction = 6;
                LastPress=6;
                break;
            case NUMPAD4://Left
            case LEFT:
                direction = 4;
                LastPress=4;
                break;
            case NUMPAD2://Down
            case DOWN:
                direction = 2;
                LastPress=2;
                break;
            case NUMPAD7://UPLEFT
                direction = 7;
                LastPress=7;
                break;
            case NUMPAD9://UPRIGHT
                direction = 9;
                LastPress=9;
                break;
            case NUMPAD3://RIGHT DOWN
                direction = 3;
                LastPress=3;
                break;
            case NUMPAD1://LEFTDOWN
                direction = 1;
                LastPress=1;
                break;
            default:
                return;
        }
        byMouse=false;
        model.updateCharacterLocation(direction);
    }
    public int getLastPress() {
        if(LastPress==8)return 0;
        if(LastPress==6)return 90;
        if(LastPress==2)return 180;
        if(LastPress==4)return 270;
        if(LastPress==9)return 45;
        if(LastPress==7)return 315;
        if(LastPress==3)return 135;
        if(LastPress==1)return 225;
        return 0;//NeverHappens
    }
    public void play(String Event) {
        if (!mute) {
            Media sound = null;
            MediaPlayer mediaPlayer;
            switch (Event) {
                case ("STEP"):
                    //sound = new Media(new File("./src/Resources/sounds/smb_jump-small.wav").toURI().toString());
                    break;
                case ("APPLAUSE"):
                    sound = new Media(new File("./src/Resources/sounds/smb_powerup.wav").toURI().toString());
                    break;
                case ("BEGIN"):
                    sound = new Media(new File("./src/Resources/sounds/applause2.mp3").toURI().toString());
                    break;
                case ("MOUSE"):
                    sound = new Media(new File("./src/Resources/sounds/MouseClick.mp3").toURI().toString());
                    break;
                case ("LGRTR"):
                    //sound = new Media(new File("./src/Resources/sounds/lets-get-ready-to-rumble.mp3").toURI().toString());
                    break;

            }
            if (sound != null) {
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
            }

        }
    }
    public void RandomSize(){
        TotalRows=((model.randomHeightAndWidth()-20)+"");
        TotalCols=(model.randomHeightAndWidth()+"");
    }

    public void SaveMaze(File f)
    {
        model.saveMaze(f);

    }

    public void loadMaze(File f)
    {
        model.loadMaze(f);
    }

    public void Solve()
    {
        model.SolveGame();
    }

    public void doMute(){
        mute=!mute;
    }
    public void MoveCharWithMouse(int Row, int Col){
        byMouse=true;
        model.MouseMove(Row,Col);
    }
    public boolean isByMouse(){
        boolean res=byMouse;
        byMouse=false;
        return res;
    }
    public void Exit()
    {
        model.Exit();
    }
}
