public class DotsAndBoxes{
    private static final int MIN_DOTS_PER_ROW=2;
    private static final int MAX_DOTS_PER_ROW=20;
    
    public static void main(String[] args){
	Ui ui= ((args.length >0) && args[0].equals("nogui")) ? new Console() : new Gui();
	do{
	    int[] ai=new int[2];
	    for(int i=0; i != ai.length; ++i){
		ai[i]=ui.askInt("AI level for player "+(i+1)+ " (0: human)?",0,100);
	    }
	    
	    int nbDotsPerRow= ui.askInt("How many dots per row ?", MIN_DOTS_PER_ROW, MAX_DOTS_PER_ROW);
	    int nbDashedLines= ui.askInt("How many dashed lines ?", 0, DotsAndBoxesGame.maxDashedLines(nbDotsPerRow));
	    play(new DotsAndBoxesGame(nbDotsPerRow, nbDashedLines), ai, ui);
	}while(ui.yesNoChoice("Do you want to continue?"));
	System.exit(0);
    }
    // in Java 8, this could be a static method of Ui
    private static void play(MultiPlayersTurnGame g, int[] aiLevels, Ui ui){
	while(g.nbMoves()!=0){
	    ui.display(g);
	    g= (aiLevels[g.getPlayer()]==0) ?
		ui.play(g)
		: MultiPlayersTurnAI.play(g, aiLevels[g.getPlayer()]);
	}
	ui.display(g);
	int[]scores=g.scores();
	if(scores[0] == scores[1]){
	    ui.display("Draw :"+ scores[0]+ " boxes for each player");
	}else{
	    int winner= scores[0]> scores[1] ? 0 : 1;
	    ui.display("Winner is player "+ (winner+1) + "with " +scores[winner]+ "boxes");
	}
	ui.display(g);
    }
}
