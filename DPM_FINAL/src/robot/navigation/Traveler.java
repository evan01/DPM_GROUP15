package robot.navigation;/*
 * Created by evanknox on 2015-11-10.
 */

import robot.constants.Constants;
import robot.constants.Move;
import robot.constants.Move.Direction;
import robot.constants.Position;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This class will represent an object which controls the navigation of our
 * robot in such a way, that we can move our robot to individual squares on the
 * map. This is another layer on top of the navigation that seperates moving in
 * cm to moving in tiles.
 * 
 * 
 * 
 * Just as a reference this is how we perceive the coordinate system
 * 
 * Up | Left<_____|______>Right | | Down
 * 
 * RIGHT NOW SCAN WILL ALWAYS RETURN TRUE. JUST TO TEST WITHOUT COL DETECTION
 */
public class Traveler {

    //This will also be a singleton class?

    Navigation nav;
    Odometer odo;
    boolean grid[][]; //Stores whether the square is empty or not
    public static int currentX = 1;
    public static int currentY = 1;
    public static int finalX;
    public static int finalY;
    boolean movingInY = true;
    Queue<Move> xInstructions;
    Queue<Move> yInstructions;
    private double gridSpace = Constants.SQUARE_WIDTH;



    public Traveler() {
        nav = Navigation.getInstance();
        odo = Odometer.getInstance();
        grid = new boolean[12][12];
        xInstructions = new LinkedList<Move>();
        yInstructions = new LinkedList<Move>();
    }


    /**
     * Moves the robot to a specified tile in the grid
     * @param x the x position to move the robot to
     * @param y the y position to move the robot to
     */
    public void goTo(int x, int y){
        //First find the number of X and Y moves to make
        int xMoves = x - currentX;
        int yMoves = y -  currentY;
        
        
       
        //Create a set of moves from each of these instructions
        addToQueue(yMoves,true);
        addToQueue(xMoves, false);

        //Set our final destination
        finalX = x;
        finalY = y;

        //Execute all of the moves
        startTraveling();
    }

    /**
     * Orders the robot to start moving to the correct positions
     */
    private void startTraveling(){
        

        while(currentX != finalX || currentY != finalY){
            //While we aren't there yet, still instructions to execute
        	System.out.println("Curr X:"+currentX);
            System.out.println("Curr Y:"+currentY);
            System.out.println("FinalY:"+finalY);
            System.out.println("Final X:"+finalX);
            
            //First fetch the next instruction to execute from the correct queue
//            if (yInstructions.size()==0)
//            		movingInY = false;
            
            Move mv = fetchInstruction(movingInY);

            //Then check to see if we can execute the move or not
            //THIS WILL ALSO ROTATE OUR ROBOT
            if (executeScan(mv)){
                //Then we are safe to go where we want
                executeMove(mv);
            }else{
                //OBSTACLE AVOIDANCE ROUTINE...

                //Depending on our robots map position, figure out if our avoidance goes, up, down, left, right
                Move newDirection = getBestDirection(mv); //Should possibly do a scan in both ways... idk...

                //Place the move we would have made back onto it's propper queue
                placeMoveBack(mv);

                //Move to this new direction we calculated
                executeMove(newDirection);

                //Add a 'correction' move onto the propper queue to compensate for this direction
                correctAvoidanceMove(newDirection);

            }

        }

    }


	/**
	 * Returns the next best move for our robot to make if there's an obstacle
	 * in its way
	 * 
	 * @param mv
	 *            the move that we can't make anymore due to object
	 * @return the next best move,
	 * @author morganmattone
	 */
	private Move getBestDirection(Move mv) {
		
		// If our robot is moving either up or down (y direction), turn left or
		// right to avoid the object
		if (mv.direction == Move.Direction.up
				|| mv.direction == Move.Direction.down) {

			if (currentX <= 6) {
				// If our robot is on the left side of the grid, turn right so
				// as not to hit the wall by accident
				return new Move(Move.Direction.right);
			} else {
				// If our robot is on the right side of the grid, turn left
				return new Move(Move.Direction.left);
			}

		} else {
			// If our robot is moving either left or right (x direction), turn
			// up or down to avoid the object

			if (currentY <= 6) {
				// If our robot is at the bottom of the grid, turn up
				return new Move(Move.Direction.up);
			} else {
				// If our robot is at the top of the grid, turn down
				return new Move(Move.Direction.down);
			}
		}

	}

    private void correctAvoidanceMove(Move direction){
        //TODO implement this method
    }


    /**
     *
     */
    private void placeMoveBack(Move mv){
        if (mv.direction == Move.Direction.down ||  mv.direction == Move.Direction.up){
            yInstructions.add(mv);
        }else{
            xInstructions.add(mv);
        }

    }

    /**
     * Changes the direction that our robot is traveling, called when there is a detection
     */
    private void changeDirection() {
        if(movingInY)
            movingInY = false;
        else
            movingInY = true;
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
     * Will scan in the direction of a proposed move, return whether safe or not
     * @param move
     * @return
     */
    private boolean executeScan(Move move){
        boolean scanResult = false;
        switch (move.direction){
            case up:
                nav.turnTo(90,true);
                break;
            case down:
                nav.turnTo(270,true);

                break;
            case left:
                nav.turnTo(180,true);

                break;
            case right:
                nav.turnTo(0,true);
                break;
        }
//        return scan();
        return true;
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
        updateGrid();
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
            for (int i=0; i>moves;i--){
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
        //TODO need to implement a scan routine that detects if tile is free or not
        return true; //Returns true for now
    }


    /**
     * Moves the robot left 1 tile x-=1
     */
    private void goLeft(){
    	
        Position p = odo.getPosition();
        double newX = p.getX() - gridSpace;

        //Make sure we are facing the correct way
        nav.turnTo(180,true);
        nav.travelToWithCorrection(newX,p.getY(),180);
        //currentX-=1;
    }

    /**
     * Moves the robot right 1 tile x+=1
     */
    private void goRight(){
    	
        Position p = odo.getPosition();
        double newX = p.getX() + gridSpace;
        System.out.println("right");
        //Make sure we are facing the correct way
        nav.turnTo(0,true);
        nav.travelToWithCorrection(newX,p.getY(),0);
        nav.stopMoving();
        //currentX+=1;
        
    }

    /**
     * Moves the robot up 1 tile, y += 1
     */
    private void goUP(){
    	
        Position p = odo.getPosition();
        double newY = p.getY() + gridSpace;
        System.out.println("up");
        //Make sure we are facing the correct way
        nav.turnTo(90,true);
        nav.travelToWithCorrection(p.getX(),newY,90);
        nav.stopMoving();
       	//currentY+=1;
        
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
        nav.stopMoving();
        //currentY-=1;
    }

    /**
     * This method updates the grid every time we make a move
     */

    private void updateGrid(){
        grid[currentX][currentY] = true;//Set the grid we're in currently to be a 'safe' space
    }



}

/*
 * 
 * Ideas
 * 
 * What we could do is that at every iteration of the start traveling loop, Just
 * check to see how far we have to go in the x and y direction Then its in this
 * method that we will decide what move to make We won't have to use a queue in
 * this case... hmm food for thought This class is getting pretty intense but we
 * master this and we're golden!
 */
