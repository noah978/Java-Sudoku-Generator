import greenfoot.*;
import java.util.*;
import static java.lang.System.*;

//for image saving
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * Write a description of class SodokuBoard here.
 * 
 * @Noah Keck
 * @v1.1.2
 * @2/4/2018
 */
public class SudokuBoard extends World
{
    private static GreenfootImage backdrop, blankSquare;
    private static GreenfootImage[] numberImages, blueNumberImages, redNumberImages;
    private static final int difficulty = 23;
    private Cell[][] board;
    public Random rand;
    public int size, recur, numberOfSolutions;

    /**
     * Use this constructor to set the default size of board
     */
    public SudokuBoard()
    {    
        this(2);
    }

    /**
     * Creates a new SodokuBoard based on given size used as a multiplier
     * 
     * @param size 1-4, 1 is the smallest, 4 is the largest
     */
    public SudokuBoard(int size)
    {
        super(300 * size, 300 * size, 1);
        this.size = size;
        rand = new Random();
        loadContent();
        setBackground(backdrop);
        //createBoard();
    }

    public void act()
    {
        createBoard();
        saveWorldAsImage();
    }

    private void loadContent()
    {
        backdrop = new GreenfootImage("SodokuBoardSmall.png");
        blankSquare = new GreenfootImage(31*size, 31*size); //transparent blank square
        numberImages = new GreenfootImage[9];
        blueNumberImages = new GreenfootImage[9];
        redNumberImages = new GreenfootImage[9];
        for (int i = 0; i < numberImages.length; i++){
            numberImages[i] = new GreenfootImage("Square" + (i+1) + ".png");
            blueNumberImages[i] = new GreenfootImage("BlueSquare" + (i+1) + ".png");
            redNumberImages[i] = new GreenfootImage("RedSquare" + (i+1) + ".png");
        }

        //scale all images
        backdrop.scale(backdrop.getWidth()/4*size, backdrop.getHeight()/4*size);
        for (int i = 0; i < numberImages.length; i++){
            numberImages[i].scale(31*size, 31*size);
            blueNumberImages[i].scale(31*size, 31*size);
            redNumberImages[i].scale(31*size, 31*size);
        }
    }

    public void createCells()
    {
        board = new Cell[9][9];
        removeObjects(getObjects(Cell.class));
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[0].length; j++){
                board[j][i] = new Cell();
                addObject(board[j][i], (1*size)*(i/3+1) + (33*size)*i + 31*size/2, (1*size)*(j/3+1) + (33*size)*j + 31*size/2);
                //addition of different factors of the location:
                //offset due to grid walls + current position (in array) + undo the image centering (done automatically by greenfoot)
            }
        }
    }

    /**
     * Sudoku creation will be done in the order of these steps:
     * 
     * 1. Assign values to each cell in the grid.
     * 2. Set the images to each cell.
     * 3. Remove images in groups, ensuring that the puzzle only has one solution
     * 4. Puzzle is completed once 20-30 clues / images remain
     */
    public void createBoard()
    {
        do{
            createCells();
            out.println("\f" + setNums(0,0) + " " + recur);
            recur = 0;
        }while(!removeNums());
    }

    public boolean setNums(int r, int c)
    {
        if (r >= 9)
            return true;
        if (c >= 9)
            return setNums(r+1, 0);
        recur++;
        ArrayList<Integer> nums = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
        int num = 0;
        while (true){
            board[r][c].setNum(0);
            if (nums.isEmpty())
                return false;
            int index = rand.nextInt(nums.size());
            num = nums.get(index);
            nums.remove(index);
            if (checkSquare(r, c, num) && checkRow(r, num) && checkColumn(c, num)){
                board[r][c].setNum(num);
                if ( setNums(r, c+1) )
                    break;
            }
        }
        return true;
    }

    /**
     * Converts to a matrix of integers first. Then removes a specific group of numbers based on the clue count.
     * It checks that the removed numbers leave one unique solution. If it does, it also removes this from the board.
     * If it doesn't work, it recreates the integer matrix and trys again.
     */
    public boolean removeNums()
    {
        int clueCount = 81, r = 0, c = 0;
        long startTime = System.nanoTime();
        int[][] b = convertToMatrix();
        while (clueCount > difficulty){
            while (true){
                r = rand.nextInt(9);
                c = rand.nextInt(9);
                if(clueCount > 60){ //groups of 4
                    if (r != 8 && c != 8 && b[r][c] != 0 && b[r][c+1] != 0 && b[r+1][c] != 0 && b[r+1][c+1] != 0){
                        for (int i = 0; i < 2; i++){
                            b[r][c+i] = 0;
                            b[r+1][c+i] = 0;
                        }
                        break;
                    }
                }
                else if (clueCount > 40){ //groups of 2
                    if (c != 8 && b[r][c] != 0 && b[r][c+1] != 0){ //extends to the right
                        for (int i = 0; i < 2; i++)
                            b[r][c+i] = 0;
                    }
                    else if (r != 8 && b[r][c] != 0 && b[r+1][c] != 0){ //extends below
                        for (int i = 0; i < 2; i++)
                            b[r+i][c] = 0;
                    }
                    break;
                }
                else if (b[r][c] != 0){ //1 by 1
                    b[r][c] = 0;
                    break;
                }
            }
            out.println(checkForOneSolution(0,0,b.clone()) + " " + recur + " " + numberOfSolutions);
            recur = 0;
            if (numberOfSolutions <= 1){
                for(int i = 0; i < board.length; i++)
                    for(int j = 0; j < board[i].length; j++)
                        board[i][j].setNum(b[i][j]);
                if (clueCount > 60)
                    clueCount -= 4;
                else if (clueCount > 40)
                    clueCount -= 2;
                else
                    clueCount--;
            }
            else{
                b = convertToMatrix();
            }
            numberOfSolutions = 0;
            if (System.nanoTime() - startTime > Math.pow(10, 9)*4) // 3 seconds
                return false;
        }
        return true;
    }

    /**
     * This method should check every possible combination of numbers to see if its a valid solution.
     * It should finally return true when the first blank number it checked runs out of numbers to check.
     */
    public boolean checkForOneSolution(int r, int c, int[][] boardCopy)
    {
        if (r >= 9){
            numberOfSolutions++;
            return false;
        }
        if (c >= 9)
            return checkForOneSolution(r+1, 0, boardCopy);
        recur++;
        if (boardCopy[r][c] != 0) //already has a valid number
            return checkForOneSolution(r, c+1, boardCopy);
        int num = 0;
        while (true){
            boardCopy[r][c] = 0;
            num++;
            if (num >= 10) //no more numbers
                return true;
            if (checkSquare(r, c, num, boardCopy) && checkRow(r, num, boardCopy) && checkColumn(c, num, boardCopy)){
                boardCopy[r][c] = num;
                checkForOneSolution(r, c+1, boardCopy);
            }
        }
    }

    private int[][] convertToMatrix()
    {
        int[][] boardCopy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                boardCopy[i][j] = board[i][j].getNum();
        return boardCopy;
    }

    private boolean checkSquare(int r, int c, int num)
    {
        return checkSquare(r, c, num, convertToMatrix());
    }

    private boolean checkRow(int r, int num)
    {
        return checkRow(r, num, convertToMatrix());
    }

    private boolean checkColumn(int c, int num)
    {
        return checkColumn(c, num, convertToMatrix());
    }

    /**
     * @param r the row of cell 0-8
     * @param c the column of cell 0-8
     * @param num the number its checking for 1-9
     */
    public boolean checkSquare(int r, int c, int num, int[][] boardCopy)
    {
        if (r < 3)
            r = 0;
        else if (r < 6)
            r = 3;
        else
            r = 6;
        if (c < 3)
            c = 0;
        else if (c < 6)
            c = 3;
        else
            c = 6;
        for (int i = r; i < r+3; i++){
            for (int j = c; j < c+3; j++){
                if (boardCopy[i][j] == num)
                    return false;
            }
        }
        return true;
    }

    public boolean checkRow(int r, int num, int[][] boardCopy)
    {
        for (int value : boardCopy[r])
            if (value == num)
                return false;
        return true;
    }

    public boolean checkColumn(int c, int num, int[][] boardCopy)
    {
        for (int[] row : boardCopy)
            if (row[c] == num)
                return false;
        return true;
    }

    //graphics based methods
    public static GreenfootImage getImageNumber(int number)
    {
        return numberImages[number-1];
    }
    
    public static GreenfootImage getBlankSquare()
    {
        return blankSquare;
    }

    /**
     * Saves an image of the generated Sudoku to the same folder as the greenfoot project.
     */
    public void saveWorldAsImage()
    {
        GreenfootImage main = new GreenfootImage(getBackground());
        for (Actor actor : getObjects(Actor.class)){
            main.drawImage(actor.getImage(), actor.getX()-actor.getImage().getWidth()/2, actor.getY()-actor.getImage().getHeight()/2);
        }
        try{
            BufferedImage image = main.getAwtImage();
            File filepath = new File("SudokuPuzzle.png"); //or "/Users/Noah/Pictures/SudokuPuzzle.png"
            ImageIO.write(image, "png", filepath);
        }
        catch (IOException e){
            System.out.println("File output failed.");
        }
    }
}
