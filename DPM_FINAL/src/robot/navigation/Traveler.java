package robot.navigation;/*
 * Created by evanknox on 2015-11-10.
 */

import robot.constants.Constants;
import robot.constants.Move;
import robot.constants.Move.Direction;
import robot.constants.Position;
import robot.sensors.USSensor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import javax.swing.GroupLayout.Alignment;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

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

    public static int currentX = 0;
    public static int currentY = 0;

    //public static int nav.verticalLinesCrossed = 0;
    //public static int nav.horizontalLinesCrossed = 0;

    public static int finalX;
    public static int finalY;
    boolean isMovingInY = true;
    private Queue<Move> xInstructions;
    private Queue<Move> yInstructions;
    private double gridSpace = 30.48;
    private boolean thirdOption = false , fourthOption = false, secondOption = false;

	private Move.Direction lastDirection=Move.Direction.up;	//should be up or right, keep it up for now
	private Move.Direction lastDirection2=Move.Direction.right;
	private Direction [] directionsArrayPriority1 = {Move.Direction.up,Move.Direction.right,Move.Direction.down,Move.Direction.left}; 
	private Direction [] directionsArrayPriority2 = {Move.Direction.down,Move.Direction.left,Move.Direction.up,Move.Direction.right};
	
	private USSensor us;




    public Traveler() {
        nav = Navigation.getInstance();
        odo = Odometer.getInstance();
        grid = new boolean[12][12];
        xInstructions = new LinkedList<Move>();
        yInstructions = new LinkedList<Move>();
        this.us = USSensor.getInstance();
    }


    /**
     * Moves the robot to a specified tile in the grid
     * @param x the x position to move the robot to
     * @param y the y position to move the robot to
     */
    public void goTo(int x, int y){
        //First find the number of X and Y moves to make
        int xMoves = x - currentX;
        int yMoves = y - currentY;
        
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
        
        while(currentX!= finalX || currentY != finalY){
            //While we aren't there yet, still instructions to execute
        	//System.out.println("Curr X:"+nav.verticalLinesCrossed);
            //System.out.println("Curr Y:"+nav.horizontalLinesCrossed);
            System.out.println("FinalY:"+finalY);
            System.out.println("Final X:"+finalX);
            
            //First fetch the next instruction to execute from the correct queue
//            if (yInstructions.size()==0)
//            		movingInY = false;   
            Move mv = fetchInstruction();

            //Then check to see if we can execute the move or not
            //THIS WILL ALSO ROTATE OUR ROBOT
            if (executeScan(mv)){
                //Then we are safe to go where we want
                executeMove(mv);
            }else{
                //OBSTACLE AVOIDANCE ROUTINE...
            	placeMoveBack(mv.getOpposite());
            	System.out.println("A7A");
                //Depending on our robots map position, figure out if our avoidance goes, up, down, left, right
//                Move newMove = getBestDirection(mv, lastDirection); //Should possibly do a scan in both ways... idk...
                
                Move newMove = getBDirection(mv);
               //Place the move we would have made back onto it's propper queue
//               placeMoveBack(mv);

                //Move to this new direction we calculated
               executeMove(newMove);
//             if (newMove.direction == Direction.down || newMove.direction == Direction.left)
//             {
//            	 Move correctionMove = getBestDirection(newMove ,newMove.direction);
//            	 executeMove(correctionMove);
//            	 placeMoveBack(correctionMove);
//             }
               if (secondOption)
            	   changeDirection();
               if (thirdOption)
               {
            	   isMovingInY = false;
            	   Move correctionMove = fetchInstruction();
            	   System.out.println("Correction direction:  " + correctionMove.direction);
            	   executeMove(correctionMove);
            	   thirdOption = false;
               }
               else if (fourthOption) 
               {
            	   isMovingInY = true;
            	   Move correctionMove = fetchInstruction();
            	   System.out.println("Correction direction:  " + correctionMove.direction);
            	   executeMove(correctionMove);
            	   fourthOption= false;
               }

                //Add a 'correction' move onto the propper queue to compensate for this direction
//                if (newMove.direction == Direction.down || newMove.direction == Direction.left)
//                {          	
//                	Move correcredMove = getBestDirection(newMove,lastDirection2);	
//                	placeMoveBack(newMove);
//                }

            }

        }

    }



	/**
	 * Returns the next best move for our robot to make if there's an obstacle
	 * in its way
	 * 
	 * @param mv & lastDirection
	 *            the move that we can't make anymore due to object
	 *            & the last direction that has been moved by the robot
	 * @return the next best move
	 */
	private Move getBestDirection(Move mv, Move.Direction lastDirection) {
		
		Move.Direction currentDirection;
		
		for (int i=0;i<directionsArrayPriority1.length;i++)
		{
			
			//counter should probably be removed, did not get used
			currentDirection=directionsArrayPriority2[i];
			
			if(mv.direction==currentDirection)		//skip this direction since we already checked it before even going into this method
				continue;
			
			// new direction 
			if(executeScan(new Move(currentDirection))){	// if this direction is free return it 
				return new Move(currentDirection);

			}
		}
		
		return new Move(lastDirection);

	}
	
	/**
	 * This will return the next best move for our robot to make if there's an obstacle
	 * @param mv the move that we were going to make
	 * @return a new move to go to
	 */
	private Move getBDirection(Move mv){
		//First, if we can just switch to the other queue of instructions then do that
		Move move2;
		if(isMovingInY){//Then get element from x queue, best case
			if(xInstructions.size()>=1){
				move2 = xInstructions.element();
			}else{
				//Nothing in the xQueue, pick left and right
				move2 = new Move(Move.Direction.right);
			}
		}
		else{//Get element from the y queue
			if(yInstructions.size()>=0){
				move2 = yInstructions.element();
			}
			else{
				move2 = new Move(Move.Direction.up);
			}
		}
		
		//Check to see if we can go in the direction of the other queue
		if(executeScan(move2)){
			//WE can!!
			System.out.println("Good Move, direction: "+move2.direction);
			//This if statement is necessary to change the direction for the future moves
			
			placeMoveBack(move2.getOpposite());
			placeMoveBack(move2);
			changeDirection();
			secondOption = true;
			return fetchInstruction();//Removes the element we just peeked at

		}else{
			//This is the worst case, we tried switching to other queue and we can't go there
			//We've already tried 2 directions in this case
			
			//Get the oposite of the second suggested direction
			Move move3 = move2.getOpposite();
			System.out.println("Move 3 dirrr-> "+move3.direction);
			
			
			if(executeScan(move3)){
				//then go to that move, correct for it
				placeMoveBack(move3);
				thirdOption = true;
				System.out.println("Third Option direction: "+move3.direction);
				return move3;
			}else{
				//Blocked from 3 sides, go back, correct for it
				placeMoveBack(mv.getOpposite());
				fourthOption = true;
				System.out.println("Fourth Option direction: "+mv.getOpposite().direction);
				return mv.getOpposite();
			}
		}
		//No matter what, add move back
	}
	

    private void correctAvoidanceMove(Move direction){
        //TODO implement this method
    	
    	
    }


    /**
     *
     */
	private void placeMoveBack(Move mv) {
		switch (mv.direction) {
		case up:
			yInstructions.add(new Move(Move.Direction.down));
			break;
		case down:
			System.out.println("UPMOVEADDED B$#%");
			yInstructions.add(new Move(Move.Direction.up));
			break;
		case left:
			xInstructions.add(new Move(Move.Direction.right));
			break;
		case right:
			xInstructions.add(new Move(Move.Direction.left));
			break;
		}
	}

    /**
     * Changes the direction that our robot is traveling, called when there is a detection
     */
    private void changeDirection() {
        if(isMovingInY)
            isMovingInY = false;
        else
            isMovingInY = true;
    }

    /**
     * Will return the proper instruction to execute next depending on the current orientation
     * If there are no more of one instruction to get (we are at the correct x position, will return the y move to do
     * @param isMovingInY , whether we are traveling vertically or not
     * @param toRemoveFromQ whether or not we want to remove from Queue
     * @return an instruction to execute next
     */
	private Move fetchInstruction() {
//		if(toRemoveFromQ){
			if(isMovingInY){
				if(yInstructions.size()>0){
					return yInstructions.remove();
				}
				else {
					isMovingInY = false;
					return xInstructions.remove();
				}

			}else{
				if(xInstructions.size()>0){
					return xInstructions.remove();
				}
				else {
					isMovingInY = true;
					return yInstructions.remove();
				}
			}
		}

//		else{
//			if(isMovingInY){
//				if(yInstructions.size()>0)
//					return yInstructions.element();
//				else {
//					movingInY = false;
//					return xInstructions.element();
//				}
//
//			}else{
//				if(xInstructions.size()>0)
//					return xInstructions.element();
//				else {
//					movingInY = true;
//					return yInstructions.element();
//				}
//			}
//		}

//	}

    /**
     * Will scan in the direction of a proposed move, return whether safe or not
     * @param move
     * @return
     */
    private boolean executeScan(Move move){
        boolean scanResult = true;
        boolean scanResult2 = true;
        switch (move.direction){
            case up:
                nav.turnToSearch(90,true);
                break;
            case down:
                nav.turnToSearch(270,true);

                break;
            case left:
                nav.turnToSearch(180,true);
                break;
            case right:
                nav.turnToSearch(0,true);
                break;
        }
        scanResult = scan() || scan2();
        scanResult2 = scan() || scan2();
        return (scanResult || scanResult2) ;		// will return true if field is free 
    
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
                    
//                    System.out.println(yInstructions.peek().direction);
                }else {
                    xInstructions.add(new Move(Move.Direction.right));
//                    System.out.println(xInstructions.peek().direction);
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
    	int distance;
    	if ((distance=getFilteredData())<30){
    	System.out.println(distance);
        return false; 	
    	}
    	else{
    	return true;}
    }
    public boolean scan2(){
        //TODO need to implement a scan routine that detects if tile is free or not
    	int distance;
    	if ((distance=getFilteredData())<35){
    	System.out.println(distance);
        return false; 	
    	}
    	else{
    	return true;}
    }



    /**
     * Moves the robot left 1 tile x-=1
     */
    private void goLeft(){
    	
        Position p = odo.getPosition();
//        double newX = (currentX-1)*gridSpace - gridSpace/2;
        double newX = p.getX() - gridSpace;
        //if (nav.verticalLinesCrossed!=nav.verticalLinesCrossed)
        //	nav.verticalLinesCrossed=nav.verticalLinesCrossed;

        //Make sure we are facing the correct way
        //nav.turnTo(180,true);
        nav.travelToWithCorrection(newX,p.getY(),180);
        currentX-=1;
        System.out.println("currentX : "+currentX);
      
        //nav.verticalLinesCrossed-=1;
    }

    /**
     * Moves the robot right 1 tile x+=1
     */
    private void goRight(){
    	
        Position p = odo.getPosition();
       double newX = p.getX() + gridSpace;
 //       double newX = (currentX+1)*gridSpace + gridSpace/2;
        System.out.println("NewX: "+newX);

        //if (nav.verticalLinesCrossed!=nav.verticalLinesCrossed)
        //	nav.verticalLinesCrossed=nav.verticalLinesCrossed;
        
        //Make sure we are facing the correct way
        //nav.turnTo(0,true);
        nav.travelToWithCorrection(newX,p.getY(),0);
        currentX++;
        System.out.println("currentX : "+currentX);
    }

    /**
     * Moves the robot up 1 tile, y += 1
     */
    private void goUP(){
    	
        Position p = odo.getPosition();
        double newY = p.getY() + gridSpace;
//        double newY = (currentY+1)*gridSpace + gridSpace/2;
        System.out.println("NewY: "+newY);

        //if (nav.horizontalLinesCrossed!=nav.horizontalLinesCrossed)
        //	nav.horizontalLinesCrossed=nav.horizontalLinesCrossed;
        
        //Make sure we are facing the correct way
        //nav.turnTo(90,true);
        nav.travelToWithCorrection(p.getX(),newY,90);
       	currentY+=1;
//      double newY =currentY*gridSpace;
       	System.out.println("currentY : "+currentY);
    }

    
    /**
     * Moves the robot down 1 tile, y-=1
     */
    private void goDown(){

        Position p = odo.getPosition();
        double newY = p.getY() - gridSpace;
//        double newY = (currentY-1)*gridSpace - gridSpace/2;
        //if (nav.horizontalLinesCrossed!=nav.horizontalLinesCrossed)
        //	nav.horizontalLinesCrossed=nav.horizontalLinesCrossed;
        
        //Make sure we are facing the correct way
        //nav.turnTo(270,true);
        nav.travelToWithCorrection(p.getX(),newY,270);
        currentY-=1;
        System.out.println("currentY : "+currentY);

        //nav.horizontalLinesCrossed-=1;
    }

    /**
     * This method updates the grid every time we make a move
     */

    private void updateGrid(){
        grid[currentX][currentY] = true;		//Set the grid we're in currently to be a 'safe' space
    }
    
	private int getFilteredData() {
		int dist;
		dist = us.scan();
														// thus makes anything above the threshold distance irrelevant
		return dist;
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
