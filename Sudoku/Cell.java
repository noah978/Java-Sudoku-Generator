import greenfoot.*;

/**
 * Write a description of class Cell here.
 * 
 * @Noah Keck
 * @v1.0
 * @1/26/2018
 */
public class Cell extends Actor
{
    private GreenfootImage image;
    private SudokuBoard world;
    private int num;
    
    public Cell()
    {
        world = (SudokuBoard)getWorld();
        setImage(world.getBlankSquare());
    }
    public int getNum()
    {
        return num;
    }
    public void setNum(int number)
    {
        num = number;
        if (num == 0)
            this.image = world.getBlankSquare();
        else
            this.image = world.getImageNumber(num);
        setImage(image);
    }
    public void act() 
    {
        world = (SudokuBoard)getWorld();
    }
}
