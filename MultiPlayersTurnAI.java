public class MultiPlayersTurnAI{
    private static boolean LOG=true;
    // maybe should be implemented by the games to be game specific
    private static final long TIME_LIMIT_RATIO= 5000000;

    private static int computeDepth(int timeLimit, int nbMoves){
	
	for(long time=nbMoves; (nbMoves >=0) && (time < timeLimit*TIME_LIMIT_RATIO); --nbMoves,time*= nbMoves)
	    {}
	int depth=g.nbMoves()-nbMoves;

    }
	public static MultiPlayersTurnGame play(MultiPlayersTurnGame g, int timeLimit){
	int nbMoves= g.nbMoves();
	if(LOG){
	    System.err.println("timeLimit:"+timeLimit+" nbMoves:"+g.nbMoves()+" depth:"+depth);
	}
	return (g.scores().length!=2)
	    ? predict(g, depth, depth).game
	    :predictAlphaBeta(g, depth, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, g.getPlayer()).game;
    }
    private static PredictionMulti predict(MultiPlayersTurnGame g
				      , int maxDepth, int depth){
	int nbMoves= g.nbMoves();
	long startChrono= (LOG && (maxDepth==depth)) ? System.nanoTime() : 0;
	if((depth==0) || (nbMoves==0))
	    { return new PredictionMulti(g); }
	PredictionMulti best=null;
	for(int i=0; i != nbMoves; ++i){
	    PredictionMulti current= predict(g.next(i), maxDepth, depth-1);
	    if((best==null)
	       ||(current.scoreFor(g.getPlayer())>best.scoreFor(g.getPlayer())))
		{ best=current; }
	}
	if(LOG && (depth == maxDepth))
	    {System.err.println("nbMoves:"+nbMoves+":maxDepth:"+maxDepth+":nanoSecs:"+(System.nanoTime()-startChrono));	}
	return (depth == maxDepth)? best : new PredictionMulti(g, best.scores);
    }
    // TODO
    private static PredictionTwo predictAlphaBeta(MultiPlayersTurnGame g
						  , int maxDepth, int depth
						  , int alpha, int beta, int maximizingPlayer){
	int nbMoves= g.nbMoves();
	long startChrono= (LOG && (maxDepth==depth)) ? System.nanoTime() : 0;
	if((depth==0) || (nbMoves == 0))
	    { return new PredictionTwo(g,maximizingPlayer); }
	boolean isMaximizing= (g.getPlayer() == maximizingPlayer);
	PredictionTwo pred=null;
	for(int i=0; (i != nbMoves); ++i){
	    PredictionTwo current= predictAlphaBeta(g.next(i), maxDepth, depth-1, alpha, beta, maximizingPlayer);
	    if(isMaximizing){
		if((pred == null)||(pred.score < current.score)){
		    pred= current;
		    if(alpha < current.score){
			alpha= current.score;
		    }
		}
	    }else{
		if((pred == null)||(pred.score > current.score)){
		    pred= current;
		    if(beta > current.score){
			beta= current.score;
		    }
		}		
	    }
	    if(beta <= alpha){ break;}
	}	    
	if(LOG && (depth == maxDepth))
	    {System.err.println("nbMoves:"+nbMoves+":maxDepth:"+maxDepth+":nanoSecsAlphaBeta:"+(System.nanoTime()-startChrono));	}
	return (depth == maxDepth)? pred : new PredictionTwo(g, new Integer(pred.score));
    }
}
class Prediction {
    public final MultiPlayersTurnGame game;
    protected Prediction(MultiPlayersTurnGame g){
	game=g;
    }
}
class PredictionMulti extends Prediction {
    public final int[] scores;
    PredictionMulti(MultiPlayersTurnGame g, int[] s){
	super(g);
	scores=s;
    }
    PredictionMulti(MultiPlayersTurnGame g){
	super(g);
	scores= g.scores();
    }
    int scoreFor(int player){
	int bestOther= -1;
	for(int i=0; i != scores.length; ++i){
	    if((i !=player)
	       && ((bestOther==-1) || (scores[i]>scores[bestOther]))){
		bestOther= i;
	    }
	}
	return scores[player]-scores[bestOther];
    }
}
class PredictionTwo extends Prediction{
    public final int score;
    PredictionTwo(MultiPlayersTurnGame g, int maximizingPlayer){
	super(g);
	int[] s=g.scores();
	score=s[maximizingPlayer]-s[1-maximizingPlayer];
    }
    PredictionTwo(MultiPlayersTurnGame g, Integer s){
	super(g);
	score= s.intValue();
    }
}
