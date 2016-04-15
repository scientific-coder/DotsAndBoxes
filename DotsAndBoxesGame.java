import java.lang.Math;
import java.util.Scanner;

public class DotsAndBoxesGame implements MultiPlayersTurnGame{
    private final int[][] lines;// -1: dashed, 0: empty, 1:connecting dots
    private final int[][] boxes;// -1: not boxed, 0: won by player 0, 1: won by  player 1
    private final int player; // 0 or 1, will play next
    private static final int NB_PLAYERS=2;
    private final DotsAndBoxesGame previous;
    
    public int[] scores(){
	int[] res= new int[NB_PLAYERS];
	for(int r=0; r != boxes.length; ++r){
	    for(int c=0; c != boxes[r].length; ++c){
		if(boxes[r][c]!=-1){
		    ++(res[boxes[r][c]]);
		}
	    }
	}
	return res;
    }
    public int getPlayer(){
	return player;
    }
    public int nbMoves(){
	int res=0;
	for(int r=0; r != lines.length; ++r){
	    for(int c=0; c != lines[r].length; ++c){
		if(lines[r][c]<1){
		    ++res;
		}
	    }
	}
	return res;
    }
    
    private char lineToChar(int line, int r){
	return (line == -1) ? '.'
	    : ((line == 0) ? ' '
	       : ( ((r % 2) == 0) ? '-'
		   : '|'));
    }

    private char boxToChar(int box){
	return ((box==-1) ? ' ' :
		((box == 0) ? '1' : '2')); 
    }
	   
    public String toString(){
	String res="";
	for(int r=0; r != lines.length; ++r){
	    if(r%2 == 0){
		for(int c=0; c != lines[r].length; ++c){
		    res+="o"+ lineToChar(lines[r][c], r);
		}
		res+="o\n";
	    }else{
		res+= lineToChar(lines[r][0], r);
		for(int c=1; c != lines[r].length; ++c){
		    res+= boxToChar(boxes[r/2][c-1]);
		    res+=lineToChar(lines[r][c], r);
		}
		res+= '\n';
	    }
	}
	return res;
    }
    public DotsAndBoxesGame(int nbDotsPerRow, int nbDashedLines){
	// nbDotsPerRow must be > 1
	// nbDotsPerRow=3
	// o-o-o
	// | | |
	// o-o-o
	// | | |
	// o-o-o
	// lines:
	// --
	// |||
	// --
	// |||
	// --
	lines= new int[2*nbDotsPerRow-1][];
	for(int r=0; r != lines.length; ++r){ //o-o-o rows have nbDotsPerLine-1 lines
	    lines[r]= new int[nbDotsPerRow -1 + (r%2) ];// | | | have nbDotsPerLine -1 +1 lines
	}// o-o-o are even rows (0,2,...), | | | are odd rows (1,3,...)
	boxes= new int[nbDotsPerRow-1][nbDotsPerRow-1];
	for(int r=0; r != boxes.length; ++r){
	    for(int c=0; c!= boxes[r].length;++c){ // or Arrays.fill(boxes[r], -1)
		boxes[r][c]=-1;
	    }
	}

	nbDashedLines= Math.min(nbDashedLines, maxDashedLines(nbDotsPerRow));
	for(int i= 0; i != nbDashedLines; ++i){
	    int r, c;
	    do{ //take a random line
	    	r= (int) (Math.random()*lines.length);
		c= (int) (Math.random()*lines[r].length);
	    }while(lines[r][c] != 0); // until it is empty (i.e. not already dashed)
	    lines[r][c]= -1;
	}
	player=0;
	previous= this;
    }
    // public static void main(String[] args){
    // 	DotsAndBoxesGame g= new DotsAndBoxesGame(3,2);
    // 	for(int i=0; i != g.nbMoves(); ++i){
    // 	    System.out.println("move: "+i);
    // 	    System.err.println(g.next(i));
    // 	}
    // }
    public DotsAndBoxesGame next(int move){
	int r=0, c=0;
	for( r=0; r != lines.length; ++r){
	    for(c=0; c!= lines[r].length; ++c){
		if(lines[r][c]<1){--move;}
		if(move == -1){break;}
	    }
	    if(move == -1){break;}
	}
	return next(r,c);
    }
    public DotsAndBoxesGame next(String rSpaceC){
	if (rSpaceC.equals("undo")){ return prev();}
	Scanner sc= new Scanner(rSpaceC);
	int r= sc.nextInt();
	int c= sc.nextInt();
	sc.close();
	return next(r, c);
    }

    public DotsAndBoxesGame next(int r, int c){
	return (lines[r][c] != 1)//already a line at [r][c] could throw
	    ? new DotsAndBoxesGame(this, r, c)
	    : this ; //we keep the game unchanged
    }

    public DotsAndBoxesGame prev(){
	return previous;
    }
    
    // nbDashedLines must be <= nbLines, with nbLines= 2*((nbDotsPerRow-1)^2)
    public static int maxDashedLines(int nbDotsPerRow){
	return 2* nbDotsPerRow*(nbDotsPerRow-1);
    }
    
    private int[][] deepCopy(int[][] array2D){
	int[][] res= new int[array2D.length][];
	for(int r=0; r != res.length; ++r){
	    res[r]= new int[array2D[r].length]; // or Arrays.copyOf(array2D[r], array2D[r].length)
	    for(int c=0; c != res[r].length; ++c){
		res[r][c]= array2D[r][c];
	    }
	}
	return res;
    }

    private static int nextPlayer(int p){
	return (p+1)%NB_PLAYERS;
    }
    
    DotsAndBoxesGame(DotsAndBoxesGame prev, int playR, int playC){
	lines= deepCopy(prev.lines);
	boxes= deepCopy(prev.boxes);
	++(lines[playR][playC]);//play 
	boolean boxed=false; //find if it creates a new box
	// TODO: restrict search around playR and playC
	// Note: cannot early exit on boxed=true because
	// one can close more than one box at once.
	for(int r=0; r != (lines.length-1); r += 2){ 
	    for(int c=0; c != lines[r].length; ++c){
		if( (boxes[r/2][c]==-1) && (lines[r][c]==1) && (lines[r+1][c]==1)
		    && (lines[r+1][c+1]==1) && (lines[r+2][c]==1)){
		    boxed= true;
		    boxes[r/2][c]= prev.player;
		}
	    }
	}
	// if we did not create a new box, change current player
	player = boxed ? prev.player : nextPlayer(prev.player);
	previous= prev;
    }
	
}
