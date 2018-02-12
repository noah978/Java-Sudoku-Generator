import greenfoot.*;
import java.util.*;
import static greenfoot.Greenfoot.*;

/**
 * Contains a valid Sudoku number and uses an image to display to the world.
 * May contain a zero to denote the fact that the cell is empty.
 * 
 * @Noah Keck
 * @v1.2.1
 * @2/9/2018
 */
public class Cell extends Actor
{
    public enum CellState{EMPTY, CLICKED, RCLICKED, GUESSED, FILLED, HARDSET}
    private GreenfootImage image;
    private SudokuBoard world;
    private int num, hiddenNum, timer, row, col;
    private ArrayList<Integer> miniNums;
    private CellState state = CellState.EMPTY;
    
    public Cell(int row, int column)
    {
        this.row = row;
        col = column;
        num = 0;
        miniNums = new ArrayList<Integer>();
        world = (SudokuBoard)getWorld();
        setImage(world.getBlankSquare());
    }
    public int getNum(){return num;}
    public int getHiddenNum(){return hiddenNum;}
    public void setNum(int number)
    {
        num = number;
        if (num == 0){
            image = world.getBlankSquare();
            state = CellState.EMPTY;
        }
        else{
            image = world.getImageNumber(num);
            hiddenNum = num;
            state = CellState.HARDSET;
        }
        setImage(image);
    }
    public void act()
    {
        timer++;
        if (timer == 360) timer = 0;
        world = (SudokuBoard)getWorld();
        clickCheck();
    }
    public void clickCheck()
    {
        MouseInfo ms = getMouseInfo();
        if (ms!=null){
            int i = image.getWidth()/2;
            if (ms.getX()>=this.getX()-i && ms.getX()<=this.getX()+i && ms.getY()>=this.getY()-i && ms.getY()<=this.getY()+i){
                if (ms.getButton() == 1 && state != CellState.HARDSET){
                    state = CellState.CLICKED;
                    timer = 0;
                    num = 0;
                    miniNums.clear();
                }
                if (ms.getButton() == 3 && state != CellState.HARDSET){ //figure out how to allow guess nums
                    state = CellState.RCLICKED;
                    num = 0;
                }
            }
            else if ((state == CellState.CLICKED || state == CellState.RCLICKED) && (ms.getButton() == 1 || ms.getButton() == 2)){
                state = CellState.EMPTY;
                setImage(world.getBlankSquare());
            }
        }
        if (state == CellState.CLICKED){
            //cursor animation
            if (timer % 40 == 0)
                image = world.getCursorSquare();
            else if (timer % 20 == 0)
                image = world.getBlankSquare();
            setImage(image);
            for (int i = 1; i <= 9; i++){
                if (isKeyDown(Integer.toString(i))){
                    if (world.checkSquare(row, col, i) && world.checkRow(row, i) && world.checkColumn(col, i))
                        setImage(world.getBlueImageNumber(i));
                    else
                        setImage(world.getRedImageNumber(i));
                    num = i;
                    state = CellState.FILLED;
                    break;
                }
            }
        }
        else if (state == CellState.RCLICKED){
            image = world.getMiniNumbers(new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9)));
            for (int i = 1; i <= 9; i++){
                if (isKeyDown(Integer.toString(i))){
                    if (miniNums.indexOf(new Integer(i)) != -1)
                        miniNums.remove(new Integer(i));
                    else
                        miniNums.add(i);
                    Collections.sort(miniNums);
                    image = world.getMiniNumbers(miniNums);
                    state = CellState.GUESSED;
                    break;
                }
            }
            setImage(image);
        }
        else if (state == CellState.FILLED){
            int save = num;
            num = 0;
            for (int i = 1; i <= 9; i++){
                if (isKeyDown(Integer.toString(i)) || (ms != null && ms.getButton() == 1)){
                    if (world.checkSquare(row, col, save) && world.checkRow(row, save) && world.checkColumn(col, save))
                        setImage(world.getBlueImageNumber(save));
                    else
                        setImage(world.getRedImageNumber(save));
                    break;
                }
            }
            num = save;
        }
    }
}
