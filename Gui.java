import java.awt.Font;
import java.awt.Color;


public class Gui implements Ui{
    public final static int WIDTH=750;
    public final static int HEIGHT=750;
    public final static double LINE_WIDTH_RATIO=1./20.;
    
    public final static Color[] playersColors={Color.BLUE, Color.RED};
    
    private static int fontSize(int lineLength, int nbLines){
	return Math.min((int)(WIDTH*.8)/lineLength, HEIGHT/(2*nbLines));
    }
    private static void waitMouseReleased(){
	    while(StdDraw.mousePressed()){} // wait till released
    }
    public int menu(String[] titleAndChoices){
	// DOES NOT HANDLE \n in strings
	// to do so, we could have a font size per menu line
	// that would be the default font size divided by the nb of lines
	// (nb \n)+1
	StdDraw.setCanvasSize(WIDTH, HEIGHT);
	StdDraw.setXscale(-1,1);
	StdDraw.setYscale(- titleAndChoices.length, 1);
	int maxNbChars=0;
	for(int i=0; i != titleAndChoices.length; ++i){
	    maxNbChars= Math.max(titleAndChoices[i].length(), maxNbChars);
	}
	Font font = new Font("Arial", Font.BOLD
			     , fontSize(maxNbChars, titleAndChoices.length));
	StdDraw.setFont(font);
	int res=0;
	do{
	    res= - (int)Math.ceil(StdDraw.mouseY()-0.5);
	    for(int i=0; i != titleAndChoices.length; ++i){
		if(i==0){
		    StdDraw.setPenColor(StdDraw.BLACK);
		}else if(res == i){
		    StdDraw.setPenColor(StdDraw.RED);
		}else{
		    StdDraw.setPenColor(StdDraw.BLUE);
		}
		StdDraw.text(0,-i, titleAndChoices[i]);
	    }
	    StdDraw.show(10);
	}while(!(StdDraw.mousePressed()
		 && (res >0) && (res < titleAndChoices.length) ));
	waitMouseReleased();
	return res-1;
    }

    private static String valueForAskInt(int v, int min, int max){
	return ""+min+" <= "+v+" <= "+max;
    }
    public int askInt(String title, int min, int max){
	int delta= max-min;
	int pow10= (int)Math.log10(delta);
	// title,.., +10,+1, nb, -1, -10,...,OK
	String[] m= new String[1+(pow10+1)+1+(pow10+1)+1];
	m[0]= title;
	int i=1;
	for(int j=pow10; j >=0; --j, ++i){ 
	    m[i]="+"+(int)Math.pow(10, j);
	}
	int res= (min+max)/2;
	int idxRes=i;
	++i;
	for(int j=0; j <= pow10; ++j, ++i){ 
	    m[i]="-"+(int)Math.pow(10, j);
	}
	m[i]="OK";
	int c=-1;
	do{
	    m[idxRes]= valueForAskInt(res, min, max);
	    c= menu(m);
	    if((c<idxRes)){
		int d= (int)Math.pow(10, (pow10-c));
		if((res + d)<= max){
		    res+= d;
		}
	    }
	    if((c>=idxRes)&&(c<(m.length-2))){
		int d= (int)Math.pow(10, (c-pow10-2));
		if((res-d)>= min){
		    res-= d;
		}
	    }
	    StdDraw.show(100);
	}while(c != (m.length-2));
	return res;
    }
    // could be factored in an abstract class
    public boolean yesNoChoice(String title){
	String[] m= {title, "Yes", "No"};
	return menu(m)==0;
    }
    public void display(String title){
	String[] m= {"",title};
	menu(m);	
    }
    private static void drawDashedHorizontalLine(int x, int y){
	// lines of length 0.2 centered on x-.4,x, x+.4 
	for(int i=-1; i != 2; ++i){
	    StdDraw.filledRectangle(x+i*.4, y,.1, LINE_WIDTH_RATIO);
	}
    }
    private static void drawDashedVerticalLine(int x, int y){
	// lines of length 0.2 centered on x-.4,x, x+.4 
	for(int i=-1; i != 2; ++i){
	    StdDraw.filledRectangle(x, y+i*.4, LINE_WIDTH_RATIO, .1);
	}	
    }
    public void display(MultiPlayersTurnGame g){
	// Assumes only two players
	String[] toRender= (g.toString()+"\n").split("\\n");
	// a line at top for score
	// a line below for current Player
	// toRender.length lines for the game
	// nb cols is max between text lines and 2*n+1
	StdDraw.setCanvasSize(WIDTH, HEIGHT);
	int scale=toRender.length+3;
	StdDraw.setScale(-1, scale );
	String longestMessage="Player "+(g.getPlayer()+1)+" now playing";
	Font font = new Font("Arial", Font.BOLD, fontSize(longestMessage.length(), 1+1+toRender.length+1));
	StdDraw.setFont(font);

	int[] scores= g.scores();

	StdDraw.setPenColor(playersColors[0]);
	StdDraw.textLeft(0, scale-1,"Player 1 :"+scores[0]);
	StdDraw.setPenColor(playersColors[1]);
	StdDraw.textRight(scale, scale-1,"Player 2 :"+scores[1]);

	StdDraw.setPenColor(playersColors[g.getPlayer()]);
	StdDraw.text((scale+1)/2., scale-2, longestMessage);
	for(int i=0; i != toRender.length; ++i){
	    //	    System.err.println(toRender[i]+" line "+i+" of "+toRender.length);
	    for(int x=0; x != toRender[i].length();++x){
		int y= scale-3-i;
		switch (toRender[i].charAt(x)){
		case 'o':{
		    StdDraw.setPenColor(StdDraw.BLACK);
		    StdDraw.filledCircle(x, y,.5);
		    break;
		}
		case '-':{
		    StdDraw.setPenColor(StdDraw.BLACK);
		    StdDraw.filledRectangle(x, y,.5, LINE_WIDTH_RATIO);
		    break;
		}
		case '|':{
		    StdDraw.setPenColor(StdDraw.BLACK);
		    StdDraw.filledRectangle(x, y,LINE_WIDTH_RATIO, .5);
		    break;
		}
		case '.':{
		    if( (i%2)==0){
			StdDraw.setPenColor(StdDraw.BLACK);
			drawDashedHorizontalLine(x,y);
		    }else{
			StdDraw.setPenColor(StdDraw.BLACK);
			drawDashedVerticalLine(x,y);
		    }
		    break;
		}
		case '1':{
		    StdDraw.setPenColor(playersColors[0]);
		    StdDraw.filledSquare(x, y, .5);
		    break;
		}
		case '2':{
		    StdDraw.setPenColor(playersColors[1]);
		    StdDraw.filledSquare(x, y, .5);
		    break;
		}
		}
	    }
	}
	StdDraw.show();
    }
    public MultiPlayersTurnGame play(MultiPlayersTurnGame g){
	MultiPlayersTurnGame res=null;
	String[] rendered= (g.toString()+"\n").split("\\n");
	//	System.err.println(rendered.length);
	do{
	    while(!StdDraw.mousePressed()){
		StdDraw.show(100);
	    }
	    double x= StdDraw.mouseX();
	    /*
	      even lines 0 around 1, 1 around 3,...
	      odd lines 0 around 0, 1 around 2, 2 around 4,...
	     */
	    double y= rendered.length-StdDraw.mouseY();
	    StdDraw.show(100);
	    waitMouseReleased();
	    //	    System.err.println("x="+x+", y="+y);
	    int roundedY=(int)(y+.5);
	    int roundedX=(int)(x+.5);
	    int r= roundedY;
	    int c=-1;
	    if((roundedY%2) == 0){
		if(((roundedX%2) == 1)
		   && (roundedX > 0)
		   && (roundedX < rendered.length)){
		    c= (roundedX-1)/2;
		}
	    }else if(((roundedX%2) == 0)
		   && (roundedX >= 0)
		   && (roundedX < rendered.length)){
		    c= (roundedX)/2;
		}
	    if(c != -1){
		//		System.err.println("r="+r+" c="+c);
		try{
		    res=g.next(""+r+" "+c);
		}catch(Exception e){
		    res=null;
		}
	    }	
	}while(res == null);
	return res;
    }
}
