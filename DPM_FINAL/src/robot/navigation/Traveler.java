package robot.navigation;/*
 * Created by evanknox on 2015-11-10.
 */

import robot.constants.Constants;
import robot.constants.Move;
import robot.constants.Position;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This class will represent an object which controls the navigation of our robot in such a way, that we can move our robot to
 * individual squares on the map. This is another layer on top of the navigation that seperates moving in cm to moving in tiles.
 *
 *
 *
 * Just as a reference this is how we perceive the coordinate system
 *
 *           Up
 *           |
 * Left<_____|______>Right
 *           |
 *           |
 *          Down
 *
 *
 */
public class Traveler {

    //This will also be a singleton class?

    Navigation nav;
    Odometer odo;
    boolean grid[][]; //Stores whether the square is empty or not
    int currentX = 0;
    int currentY =0;
    int finalX;
    int finalY;
    boolean movingInY = true;
    Queue<Move> xInstructions;
    Queue<Move> yInstructions;
    private double gridSpace = 30.4;



    public Traveler() {
        nav = Navigation.getInstance();
        odo = Odometer.getInstance();
        grid = new boolean[12][12];
        xInstructions = new PriorityBlockingQueue<Move>();
        yInstructions = new PriorityBlockingQueue<Move>();
    }


    /**
     * Moves the robot to a specified tile in the grid
     * @param x the x position to move the robot to
     * @param y the y position to move the robot to
     */
    public void goTo(int x, int y){
        //First find the number of X and Y moves to make
        int xMoves = currentX - x;
        int yMoves = currentY - y;

        //Create a set of moves from each of these instructions
        addToQueue(yMoves,true);
        addToQueue(xMoves, false);

        //Set our final destination
        finalX = x;
        finalY = y;

        //Execute all of the moves
        startTraveling();
    }

    private void startTraveling(){

        while(currentX != finalX || currentY != finalY){
            //While we aren't there yet, still instructions to execute

            //First fetch the instruction to execute from the correct queue
            Move mv = fetchInstruction(movingInY);

            //Then execute the instruction
            executeMove(mv);

        }

    }

    /**
     * Will return the proper instruction to execute next depending on the current orientation
     * If there are no more of one instruction to get (we are at the correct x position, will return the y move to do
     * @param isMovingInY , whether we are traveling vertically or not
     * @return an instruction to execute next
     */
    private Move fetchInstruction(boolean isMovingInY) {
        if(isMovingInY){
            if(yInstructions.size()>0)
                return yInstructions.remove();
            else {
                movingInY = false;
                return xInstructions.remove();
            }

        }else{
            if(xInstructions.size()>0)
                return xInstructions.remove();
            else {
                movingInY = true;
                return yInstructions.remove();
            }
        }
    }


    /**
     * This method will take in a move and it will execute it
     */
    private void executeMove(Move move){
        switch (move.direction) {
            case up:
                goUP();
                break;
            case down:
                goDown();
                break;
            case left:
                goLeft();
                break;
            case right:
                goRight();
                break;
        }
    }



    /**
     * Takes in distances to go and adds them into our queue
     * @param moves the number of x or y moves to make +'ve or negative
     * @param isYMove whether the move is a y or x move
     */
    private void addToQueue(int moves, boolean isYMove){
        // Store all of these moves in the navigation queue
        //We either create left directions or right directions
        if(moves < 0){
            for (int i=0; i<moves;i--){
                if(isYMove){
                    yInstructions.add(new Move(Move.Direction.down));
                }else {
                    xInstructions.add(new Move(Move.Direction.left));
                }
            }
        }else{
            for (int i=0; i<moves;i++){
                if (isYMove){
                    yInstructions.add(new Move(Move.Direction.up));
                }else {
                    xInstructions.add(new Move(Move.Direction.right));
                }
            }
        }
    }

    /**
     * Robot will scan whatever is immediately to it's left
     *  @return whether the grid is safe to travel to or not
     */
    public boolean scanAhead(){
        return false;
    }

    /**
     * Robot will scan whatever is immediately to it's left
     * @return whether the grid is safe to travel to or not
     */
    public boolean scanLeft(){
        return false;
    }

    /**
     * Robot will scan whatever is immediately to it's left
     * @return whether the grid is safe to travel to or not
     */
    public boolean scanRight(){
        return false;
    }

    /**
     * Will make the robot scan the grid that it is directly faceing
     * @return whether the grid is safe to travel to or not
     */
    public boolean scan(){
        //TODO need to implement a scan routine that detects if space is free or not
        return false;
    }


    /**
     * Moves the robot left 1 tile x-=1
     */
    private void goLeft(){
        Position p = odo.getPosition();
        double newX = p.getY() - gridSpace;

        //Make sure we are facing the correct way
        nav.turnTo(180,true);
        nav.travelToWithCorrection(newX,p.getY(),0);
        currentX-=1;
    }

    /**
     * Moves the robot right 1 tile x+=1
     */
    private void goRight(){
        Position p = odo.getPosition();
        double newX = p.getY() + gridSpace;

        //Make sure we are facing the correct way
        nav.turnTo(0,true);
        nav.travelToWithCorrection(newX,p.getY(),0);
        currentX+=1;
    }

    /**
     * Moves the robot up 1 tile, y += 1
     */
    private void goUP(){
        Position p = odo.getPosition();
        double newY = p.getY() + gridSpace;

        //Make sure we are facing the correct way
        nav.turnTo(90,true);
        nav.travelToWithCorrection(p.getX(),newY,90);
        currentY+=1;
    }

    /**
     * Moves the robot down 1 tile, y-=1
     */
    private void goDown(){
        Position p = odo.getPosition();
        double newY = p.getY() - gridSpace;

        //Make sure we are facing the correct way
        nav.turnTo(270,true);
        nav.travelToWithCorrection(p.getX(),newY,270);
        currentY-=1;
        updateGrid();
    }

    /**
     * This method updates the grid every time we make a move
     */

    private void updateGrid(){
        grid[currentX][currentY] = true;//Set the grid we're in currently to be a 'safe' space
    }



}
