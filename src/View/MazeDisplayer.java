package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas {


    public MazeDisplayer() {
        blockZoom = true;
        zoom = false;
        FirstDraw = true;
    }

    public boolean blockZoom;
    boolean zoom;
    Maze maze;
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameEnd = new SimpleStringProperty();
    StringProperty imageFileNameStart = new SimpleStringProperty();
    StringProperty imageSolutionPath = new SimpleStringProperty();
    int rowPrintStart;
    int rowPrintFinish;
    int colPrintStart;
    int colPrintFinish;
    int rowPlayer = -1;
    int colPlayer = -1;
    double cellWidth;
    double cellHeight;
    double canvasWidth;
    double canvasHeight;
    GraphicsContext graphicsContext;
    Image playerImage = null;
    Image StartImage = null;
    Image FinishImage = null;
    Image wallImage = null;
    Image SolutionPathImage = null;
    ImageView iv;
    boolean FirstDraw;


    public String getImageFileNameEnd() {
        return imageFileNameEnd.get();
    }

    public StringProperty imageFileNameEndProperty() {
        return imageFileNameEnd;
    }

    public void setImageFileNameEnd(String imageFileNameEnd) {
        this.imageFileNameEnd.set(imageFileNameEnd);
    }

    public String getImageSolutionPath() {
        return imageSolutionPath.get();
    }

    public StringProperty imageSolutionPathProperty() {
        return imageSolutionPath;
    }

    public void setImageSolutionPath(String imageSolutionPath) {
        this.imageSolutionPath.set(imageSolutionPath);
    }

    public String getImageFileNameStart() {
        return imageFileNameStart.get();
    }

    public StringProperty imageFileNameStartProperty() {
        return imageFileNameStart;
    }

    public void setImageFileNameStart(String imageFileNameStart) {
        this.imageFileNameStart.set(imageFileNameStart);
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public StringProperty imageFileNameWallProperty() {
        return imageFileNameWall;
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public StringProperty imageFileNamePlayerProperty() {
        return imageFileNamePlayer;
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {

        this.imageFileNamePlayer.set(imageFileNamePlayer);
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
            iv = new ImageView(playerImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }//Setters And Getters

    public int getRowPlayer() {
        if (rowPlayer == -1) return maze.getStartPosition().getRowIndex();
        return rowPlayer;
    }

    public void setRowPlayer(int rowPlayer) {
        this.rowPlayer = rowPlayer;
    }

    public int getColPlayer() {
        if (colPlayer == -1) return maze.getStartPosition().getColumnIndex();
        return colPlayer;
    }

    public void setColPlayer(int colPlayer) {
        this.colPlayer = colPlayer;
    }

    public void DisplayMaze(Maze maze) {
        this.maze = maze;
        draw();
    }

    public void cleanScreen() {
        graphicsContext.clearRect(0, 0, getWidth(), getHeight());
    }

    public void draw() {
        if (maze != null) {
            canvasWidth = getWidth();
            canvasHeight = getHeight();
            if (!zoom) {
                rowPrintStart = 0;
                rowPrintFinish = maze.getArrayRowSize() - 1;
                colPrintStart = 0;
                colPrintFinish = maze.getArrayColumnSize() - 1;
                int row = maze.getArrayRowSize();
                int col = maze.getArrayColumnSize();
                cellHeight = canvasHeight / (row);
                cellWidth = canvasWidth / (col);
            }
            graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
            try {
                playerImage = new javafx.scene.image.Image(new FileInputStream(getImageFileNamePlayer()));
                StartImage = new javafx.scene.image.Image(new FileInputStream(getImageFileNameStart()));
                FinishImage = new javafx.scene.image.Image(new FileInputStream(getImageFileNameEnd()));
                wallImage = new Image(new FileInputStream(getImageFileNameWall()));
                SolutionPathImage = new Image(new FileInputStream(getImageSolutionPath()));
                iv = new ImageView(playerImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            double x, y;
            graphicsContext.setFill(javafx.scene.paint.Color.RED);
            for (int i = 0; i <= rowPrintFinish - rowPrintStart; i++) {
                for (int j = 0; j <= colPrintFinish - colPrintStart; j++) {
                    if (maze.getMazeStructure()[i + rowPrintStart][j + colPrintStart] == 1) // Wall
                    {
                        x = (j) * cellWidth;
                        y = (i) * cellHeight;
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                    }

                }
            }
            if (FirstDraw) {
                setRowPlayer(maze.getStartPosition().getRowIndex());
                setColPlayer(maze.getStartPosition().getColumnIndex());
                FirstDraw = false;
            }
            if (rowPrintStart <= maze.getStart().getRowIndex() && maze.getStart().getRowIndex() <= rowPrintFinish
                    && colPrintStart <= maze.getStart().getColumnIndex() && maze.getStart().getColumnIndex() <= colPrintFinish) {
                graphicsContext.drawImage(StartImage, ((maze.getStart().getColumnIndex()) - colPrintStart) * cellWidth, (maze.getStart().getRowIndex() - rowPrintStart) * cellHeight, cellWidth, cellHeight);
            }
            if (colPrintStart <= maze.getFinish().getColumnIndex() && maze.getFinish().getColumnIndex() <= colPrintFinish
                    && rowPrintStart <= maze.getFinish().getRowIndex() && maze.getFinish().getRowIndex() <= rowPrintFinish) {
                graphicsContext.drawImage(FinishImage, (maze.getFinish().getColumnIndex() - colPrintStart) * cellWidth, (maze.getFinish().getRowIndex() - rowPrintStart) * cellHeight, cellWidth, cellHeight);
            }
            graphicsContext.drawImage(playerImage, (getColPlayer() - colPrintStart) * cellWidth, (getRowPlayer() - rowPrintStart) * cellHeight, cellWidth - 2, cellHeight - 2);
            drawFrame();
        }
    }

    public void drawPlayer(int angle) {
        MovePlayer(angle);
        checkCharInScreen();
        iv.setRotate(angle);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = iv.snapshot(params, null);
        graphicsContext.drawImage(rotatedImage, (getColPlayer() - colPrintStart) * cellWidth, (getRowPlayer() - rowPrintStart) * cellHeight, cellWidth - 2, cellHeight - 2);
        drawFrame();
    }

    public void drawPlayerByMouse(int row, int Col) {
        setRowPlayer(row);
        setColPlayer(Col);
        checkCharInScreen();
        iv.setRotate(0);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = iv.snapshot(params, null);
        graphicsContext.drawImage(rotatedImage, (getColPlayer() - colPrintStart) * cellWidth, (getRowPlayer() - rowPrintStart) * cellHeight, cellWidth - 2, cellHeight - 2);
        drawFrame();
    }

    public void PreviousView() {
        int row = getRowPlayer();
        int col = getColPlayer();
        int[][] struct = maze.getMazeStructure();
        if (row == maze.getStart().getRowIndex() && col == maze.getStart().getColumnIndex()) {
            graphicsContext.clearRect((col - colPrintStart) * cellWidth, (row - rowPrintStart) * cellHeight, cellWidth, cellHeight);
            graphicsContext.drawImage(StartImage, (col - colPrintStart) * cellWidth, (row - rowPrintStart) * cellHeight, cellWidth, cellHeight);
        } else if (struct[row][col] == 0) {
            graphicsContext.clearRect((col - colPrintStart) * cellWidth, (row - rowPrintStart) * cellHeight, cellWidth, cellHeight);
        }
    }

    private void MovePlayer(int ang) {
        switch (ang) {
            case (0):
                rowPlayer--;
                break;
            case (45):
                rowPlayer--;
                colPlayer++;
                break;
            case (90):
                colPlayer++;
                break;
            case (135):
                rowPlayer++;
                colPlayer++;
                break;
            case (180):
                rowPlayer++;
                break;
            case (225):
                rowPlayer++;
                colPlayer--;
                break;
            case (270):
                colPlayer--;
                break;
            case (315):
                rowPlayer--;
                colPlayer--;
            case (-1):
                break;
        }

    }
    /*
        do black Frame around the canvas,gives proportsional game board to user.
     */
    private void drawFrame() {
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(3);
        graphicsContext.strokeRect(0, 0, getWidth(), getHeight());
    }

    /*
    Display solution on screen as mushrooms
    can change picture by solution path Image
     */
    public void DisplaySolution(Solution mazeSolution) {
        double x, y;
        ArrayList<AState> path = mazeSolution.getSolutionPath();
        SnapshotParameters param = new SnapshotParameters();
        param.setFill(Color.TRANSPARENT);
        for (AState aState : path) {
            MazeState cur_state = (MazeState) aState;
            x = (cur_state.getColIndex() - colPrintStart) * cellWidth;
            y = (cur_state.getRowIndex() - rowPrintStart) * cellHeight;
            graphicsContext.drawImage(SolutionPathImage, x, y, cellWidth, cellHeight);
        }
    }

    public int getColChar(double x) {
        int z;
        if (x < 5) return colPrintStart;
        if (x > getWidth() - 10) return colPrintFinish;
        return (int) ((x) / cellWidth) + colPrintStart;
    }

    public int getRowChar(double y) {
        if (y < 5) return rowPrintStart;
        if (y > getHeight() - 10) return rowPrintStart;
        return (int) ((y) / cellHeight) + rowPrintStart;
    }

    public void changeCharacter(String Character) {
        setImageFileNamePlayer("src/Resources/Images/" + Character + ".jpg");
        if (maze != null) {
            PreviousView();
            drawPlayer(-1);
//            mazeDisplayer.draw();
        }
        requestFocus();
    }

    /*
        Doing zoom Out in case of char is out screen because of zoom.
     */
    public void checkCharInScreen() {
        if (getColPlayer() - colPrintStart < 0 || getColPlayer() - colPrintStart == getWidth() / cellWidth ||
                getRowPlayer() - rowPrintStart < 0 || getRowPlayer() - rowPrintStart == getHeight() / cellHeight) {
            blockZoom = false;
            onZoom(false);
            blockZoom = false;
            onZoom(true);
        }
    }


    /*
    Zoom Caring
     */
    public void onZoom(boolean zoomIn) {//if Zoomin true do zoomin, zoomin=false -> zoom oUt
        if (maze == null || blockZoom) return;
        zoom = true;
        int numOfRows = maze.getArrayRowSize() - 1;
        int numOfCols = maze.getArrayColumnSize() - 1;
        if (zoomIn) {
            if ((colPlayer - colPrintStart) > 2) {
                if (colPlayer - colPrintStart < 5) {
                    colPrintStart++;
                } else colPrintStart = colPrintStart + (colPlayer - colPrintStart) / 5;
            }
            if (rowPlayer - rowPrintStart > 2) {
                if (rowPlayer - rowPrintStart < 5) {
                    rowPrintStart++;
                }
                //rowPrintStart = Math.min(Math.max(2, rowPlayer - 2), rowPlayer + (rowPlayer - rowPrintStart) / 3);
                else rowPrintStart = rowPrintStart + (rowPlayer - rowPrintStart) / 5;
            }
            if (colPrintFinish - colPlayer > 2) {
                if (colPrintFinish - colPlayer < 5) colPrintFinish--;
                    //colPrintFinish = Math.max(colPlayer - (colPrintFinish - colPlayer) / 3, Math.min(colPlayer + 2, numOfCols));
                else colPrintFinish = colPrintFinish - (colPrintFinish - colPlayer) / 5;
            }

            if (rowPrintFinish - rowPlayer > 2) {
                if (rowPrintFinish - rowPlayer < 5) rowPrintFinish--;
                else rowPrintFinish = rowPrintFinish - (rowPrintFinish - rowPlayer) / 5;
            }
            cellHeight = getHeight() / (rowPrintFinish - rowPrintStart + 1);
            cellWidth = getWidth() / (colPrintFinish - colPrintStart + 1);
        } else {
            if (colPrintStart > 0) {
                if (colPrintStart < 5) {
                    colPrintStart--;
                } else colPrintStart = Math.max(colPrintStart - colPrintStart / 5, colPrintStart - 5);
            }
            if (rowPrintStart > 0) {
                if (rowPrintStart < 5) {
                    rowPrintStart--;
                } else rowPrintStart = Math.max(rowPrintStart - rowPrintStart / 5, rowPrintStart - 5);
            }
            if (colPrintFinish < numOfCols) {
                if (numOfCols - colPrintFinish < 5) colPrintFinish++;
                else colPrintFinish = Math.min(colPrintFinish + (numOfCols - colPrintFinish) / 5, colPrintFinish + 5);
            }
            if (rowPrintFinish < numOfRows) {
                if (rowPrintFinish - rowPlayer < 5) rowPrintFinish++;
                else rowPrintFinish = Math.min(rowPrintFinish + (numOfRows - rowPrintFinish) / 5, rowPrintFinish + 5);
            }
            cellHeight = getHeight() / (rowPrintFinish - rowPrintStart + 1);
            cellWidth = getWidth() / (colPrintFinish - colPrintStart + 1);
        }
        draw();
    }
}