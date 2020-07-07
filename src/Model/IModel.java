package Model;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.io.File;
import java.util.Observer;

public interface IModel {
    public void GenerateGame(int row , int col);
    public void updateCharacterLocation(int direction);
    public int getColCharacter();
    public int getRowCharacter();
    public boolean validMove(int dir);
    public void assignObserver(Observer o);
    public Maze getMaze();
    public boolean isMazeChanged();
    public boolean isMazeSolved();
    public int randomHeightAndWidth();
    public void SolveGame();
    public void Exit();
    public void saveMaze(File f);
    public void loadMaze(File f);
    public boolean AskForSolution();
    public Solution getGameSolution();
    public void setRowCharacter(int r);
    public void setColCharacter(int c);
    public void MouseMove(int row,int col);
    //GetSolution
    //
}
