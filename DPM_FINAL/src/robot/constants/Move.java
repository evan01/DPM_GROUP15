package robot.constants;/*
 * Created by evanknox on 2015-11-10.
 */

public class Move {
    public static enum Direction{left,right,up,down}
    public Direction direction;

    public Move(Direction dir){
        this.direction = dir;
    }
    
    public void setOpposite(){
    	switch (this.direction){
        case up:
        	this.direction = Move.Direction.up;
            break;
        case down:
            this.direction = Move.Direction.up;
            break;
        case left:
        	this.direction = Move.Direction.right;
            break;
        case right:
        	this.direction = Move.Direction.left;
            break;
    	}
    }

	public Move getOpposite() {
		Move newMove = null;
		switch (this.direction) {
		case up:
			newMove = new Move(Direction.down);
			break;
		case down:
			newMove = new Move(Direction.up);
			break;
		case left:
			newMove = new Move(Direction.right);
			break;
		case right:
			newMove = new Move(Direction.left);
			break;
		}

	return newMove;
   }
}
