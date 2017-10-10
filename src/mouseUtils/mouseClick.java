package mouseUtils;

import java.awt.*;
import java.awt.event.MouseEvent;

import utils.utils;

public class mouseClick{

    private Robot robot = new Robot();
    private moveMouse moveMouse = new moveMouse();
    private utils utils = new utils();
    private int cursorX;
    private int cursorY;

    public mouseClick() throws AWTException{}

    public void click(int randomHold){
        robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
        robot.delay(randomHold);
        robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
        System.out.println("  click");
    }

    /**
     * similar to doubleClick, just specify how many clicks
     */
    public void multiClickWithOffset(int[] mouseHoldDownValues, int[] delayValues, int offset){
        cursorX = moveMouse.getMouseX();
        cursorY = moveMouse.getMouseY();

        for(int i = 0; i < mouseHoldDownValues.length; i++){
            boolean offBounds = screenBoundsCheck(offset);
            if(offBounds){
                click(mouseHoldDownValues[i]);
                robot.delay(delayValues[i]);
            }else{
                robot.mouseMove(cursorX + moveMouse.random(-offset, offset), cursorY + moveMouse.random(-offset, offset));
                click(mouseHoldDownValues[i]);
                robot.delay(delayValues[i]);
            }
        }
    }

    public void doubleClick(int[] mouseHoldDownValues, int delay){
        click(mouseHoldDownValues[0]);
        robot.delay(delay);
        click(mouseHoldDownValues[1]);
    }

    public void doubleClickWithOffset(int[] mouseHoldDownValues, int delay, int offset){
        cursorX = moveMouse.getMouseX();
        cursorY = moveMouse.getMouseY();

        // check if offset will go "off screen"
        boolean offBounds = screenBoundsCheck(offset);
        if(offBounds){
            click(mouseHoldDownValues[0]);
            robot.delay(delay);
            click(mouseHoldDownValues[1]);
        }else{
            robot.mouseMove(cursorX + moveMouse.random(-offset, offset), cursorY + moveMouse.random(-offset, offset));
            click(mouseHoldDownValues[0]);
            robot.delay(delay);
            robot.mouseMove(cursorX + moveMouse.random(-offset, offset), cursorY + moveMouse.random(-offset, offset));
            click(mouseHoldDownValues[1]);
        }
    }




    /**
     * Check if we are clicking off the screen
     *
     * @param offset
     * @return
     */
    private boolean screenBoundsCheck(int offset){
        cursorX = moveMouse.getMouseX();
        cursorY = moveMouse.getMouseY();

        if(cursorY + offset >= utils.getScreenHeight() || cursorX + offset >= utils.getScreenWidth()){
            System.out.println("screenBoundsCheck()");
            System.out.println("  offbounds - random offset not applied");
            return true;
        }
        return false;
    }

}
