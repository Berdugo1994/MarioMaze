package Model;
import IO.MyDecompressorInputStream;
import Server.Server;
import algorithms.mazeGenerators.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import Server.*;
import Client.*;
import algorithms.search.Solution;

public class Model extends Observable implements IModel {

    private Server GeneratingServer;
    private Server SolvingServer;
    private Maze maze;
    private Solution mazeSolution;
    private int rowCharacter;
    private int colCharacter;
    private boolean mazeChanged;
    private boolean mazeSolved;
    private byte[] CompressedMaze;
    private boolean AskForSolution =false;


    public Model() {
        IServerStrategy GeneratingServerStrategy = new ServerStrategyGenerateMaze();
        IServerStrategy SolvingServerStrategy = new ServerStrategySolveSearchProblem();
        SolvingServer = new Server(5401,10000,SolvingServerStrategy);
        GeneratingServer = new Server(5400,10000,GeneratingServerStrategy);
        mazeChanged=false;
        mazeSolved=false;
        maze = null;
        mazeSolution = null;
        GeneratingServer.start();
        SolvingServer.start();
    }

    public void GenerateGame(int row, int col) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row,col};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        CompressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(CompressedMaze));
                        byte[] decompressedMaze = new byte[(row*col)+60]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                        //TODO:: Remove , print maze for testing
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        setColCharacter(maze.getStartPosition().getColumnIndex());
        setRowCharacter(maze.getStartPosition().getRowIndex());
        mazeChanged = true;
        mazeSolved=false;
        setChanged();
        notifyObservers();
    }

    /**
     * Creating a Solution to the current maze (in the maze field) , and assign the the solution the field "mazeSolution"
     */
    public void SolveGame()
    {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        AskForSolution = true;
        setChanged();
        notifyObservers();

    }
    public void saveMaze(File f)
    {
        try {
            FileOutputStream fileOutStream = new FileOutputStream(f);
            ObjectOutputStream toFile = new ObjectOutputStream(fileOutStream);


            Position curposition = new Position(getRowCharacter(),getColCharacter());
            maze.setStart(curposition);

            toFile.writeObject(maze);
            toFile.flush();

            toFile.close();
            fileOutStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMaze(File f)
    {
        try {

            FileInputStream fileinStream = new FileInputStream(f);
            ObjectInputStream fromfile = new ObjectInputStream(fileinStream);
            maze = (Maze) fromfile.readObject();
            fromfile.close();
            fileinStream.close();
            setColCharacter(maze.getStartPosition().getColumnIndex());
            setRowCharacter(maze.getStartPosition().getRowIndex());
            mazeChanged = true;
            mazeSolved=false;
            setChanged();
            notifyObservers();




        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void setRowCharacter(int rowCharacter) {
        this.rowCharacter = rowCharacter;
    }
    public void setColCharacter(int colCharacter) {
        this.colCharacter = colCharacter;
    }
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }
    public int getRowCharacter() {
        return rowCharacter;
    }
    public int getColCharacter() {
        return colCharacter;
    }
    public Maze getMaze() {
        return maze;
    }
    public Solution getGameSolution()
    {
        return mazeSolution;
    }
    public void updateCharacterLocation(int direction) {
        switch (direction) {
            case 8://UP
                if (!validMove(8))
                    break;
                rowCharacter--;
                break;
            case 6://Right
                if (!validMove(6))
                    break;
                colCharacter++;
                break;
            case 2://DOWN
                if (!validMove(2))
                    break;
                rowCharacter++;
                break;
            case 4: //LEFT
                if (!validMove(4))
                    break;
                colCharacter--;
                break;

            case 9://UP+RIGHT
                if (!validMove(9))
                    break;
                rowCharacter--;
                colCharacter++;
                break;
            case 3://DOWN+RIGHT
                if (!validMove(3))
                    break;
                colCharacter++;
                rowCharacter++;
                break;
            case 1://DOWN+LEFT
                if (!validMove(1))
                    break;
                rowCharacter++;
                colCharacter--;
                break;
            case 7: //UP+LEFT
                if (!validMove(7))
                    break;
                rowCharacter--;
                colCharacter--;
                break;
            default:
                System.out.println("Defualt Pressed");
                return;
        }
        setChanged();
        notifyObservers();
    }

    public boolean isMazeChanged() {
        boolean res = mazeChanged;
        mazeChanged = false;
        return res;
    }

    public boolean AskForSolution()
    {
        boolean res = AskForSolution;
        AskForSolution = false;
        return res;
    }


    public boolean validMove(int dir) {
        int row = getRowCharacter();
        int col = getColCharacter();
        switch (dir) {
            case 8: //up
                return vailableMove(row - 1, col);
            case 6: //right
                return vailableMove(row, col + 1);
            case 2: //down
                return vailableMove(row + 1, col);
            case 4: //left
                return vailableMove(row, col - 1);
                //Diagonal
            case 9: //up right
                return vailableDiagonalMove(row - 1, col + 1, row - 1, col,row,col+1);
            case 3: //down right
                return vailableDiagonalMove(row + 1, col + 1, row , col+1,row+1,col);
            case 1: //down left
                return vailableDiagonalMove(row+1, col - 1, row + 1, col,row,col-1);
            case 7: //up left
                return vailableDiagonalMove(row - 1, col - 1, row, col-1,row-1,col);
        }
        return true;
    }


    public boolean vailableMove(int r, int c) {
        return (r >= 0 && r <= maze.getArrayRowSize() - 1 && c >= 0 && c <= maze.getArrayColumnSize() - 1
                && maze.getMazeStructure()[r][c] == 0);
    }

    public boolean vailableDiagonalMove(int r, int c, int prev_above_r, int prev_above_c,int prev_below_r,int prev_below_c) {
        if (!vailableMove(r, c))
            return false;
        else if (prev_above_r >= 0 && prev_above_r <= maze.getArrayRowSize() - 1 && prev_above_c >= 0 &&
                prev_above_c <= maze.getArrayColumnSize() - 1 &&
                maze.getMazeStructure()[prev_above_r][prev_above_c] == 0)
            return true;
        else return prev_below_r >= 0 && prev_below_r <= maze.getArrayRowSize() - 1 && prev_below_c >= 0 &&
                    prev_below_c <= maze.getArrayColumnSize() - 1 &&
                    maze.getMazeStructure()[prev_below_r][prev_below_c] == 0;
    }


    public boolean isMazeSolved(){
        return(rowCharacter==maze.getFinish().getRowIndex() && colCharacter==maze.getFinish().getColumnIndex());
    }
    public int randomHeightAndWidth(){
        Random random = new Random();
        return random.nextInt(70)+37;
    }
    @Override
    public void Exit()
    {
        SolvingServer.stop();
        GeneratingServer.stop();
    }
    public void MouseMove(int Row, int Col){
        if (maze.getMazeStructure()[Row][Col]==0) {
            rowCharacter = Row;
            colCharacter = Col;
            setChanged();
            notifyObservers();
        }
    }
}
