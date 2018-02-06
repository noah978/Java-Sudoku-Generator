import greenfoot.*;
import static greenfoot.Greenfoot.*;

/**
 * Contains a valid Sudoku number and uses an image to display to the world.
 * May contain a zero to denote the fact that the cell is empty.
 * 
 * @Noah Keck
 * @v1.1.3
 * @2/5/2018
 */
public class Cell extends Actor
{
    public enum CellState{EMPTY, CLICKED, FILLED, HARDSET}
    private GreenfootImage image;
    private SudokuBoard world;
    private int num, hiddenNum, timer;
    private CellState state = CellState.EMPTY;
    
    public Cell()
    {
        num = 0;
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
                    state = CellState.CLICKED; timer = 0;
                }
                if (ms.getButton() == 2 && state != CellState.HARDSET){ //figure out how to allow guess nums
                    //some code
                }
            }
            else if (state == CellState.CLICKED && ms.getButton() == 1){
                state = CellState.EMPTY;
                setImage(world.getBlankSquare());
            }
        }
        if (state == CellState.CLICKED){
            //animation
            if (timer % 40 == 0)
                image = world.getCursorSquare();
            else if (timer % 20 == 0)
                image = world.getBlankSquare();
            setImage(image);
            num = 0;
            for (int i = 1; i <= 9; i++){
                if (isKeyDown(Integer.toString(i))){
                    num = i;
                    if (num == hiddenNum)
                        setImage(world.getBlueImageNumber(num));
                    else
                        setImage(world.getRedImageNumber(num));
                    state = CellState.FILLED;
                }
            }
        }
    }
}
