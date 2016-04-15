import java.util.Scanner;

public class Console implements Ui{
    Scanner sc;
    public Console(){
	sc= new Scanner(System.in);
    }
	
    public int askInt(String title, int min, int max){
	int res=max+1;
	do{
	    System.out.println(title+"(Must be between "+min+" and "+max +")");
	    try{
		res= sc.nextInt();
	    }catch(Exception e){
		System.err.println(e);
		sc.next();
	    }
	}while((res <min) || (res > max));
	return res;
    }

    public void display(String title){
	System.out.println(title);
    }
    public void display(MultiPlayersTurnGame g){
	System.out.println(g);
    }
    // could be factored in an abstract class
    public boolean yesNoChoice(String title){
	String[] m= {title, "Yes", "No"};
	return menu(m)==0;
    }
    
    public int menu(String[] titleAndChoices){
	int res=-1;
	String title= titleAndChoices[0] + "\n";
	for(int i=1; i != titleAndChoices.length; ++i){
	    title+= (i-1)+".) "+titleAndChoices[i]+"\n";
	}
	return askInt(title, 0, titleAndChoices.length-2);
    }
    public void nextTurn(MultiPlayersTurnGame g){
	int[] scores= g.scores();
	for(int i=0; i != scores.length; ++i){
	    System.out.println("Player "+(i+1)+": "+ scores[i]+" boxes");
	}
    }
    public MultiPlayersTurnGame play(MultiPlayersTurnGame g){
	MultiPlayersTurnGame res=null;
	do{
	    System.out.println("Player "+(g.getPlayer()+1)+" now playing");
	    System.out.println("Which line are you playing (type:row col, indices starting at 0) ?");
	    try{
		String input= sc.nextLine();
		if(input.equals("")){// this is a trailing \n
		    input= sc.nextLine();
		}
		res= g.next(input);
	    }catch(Exception e){
		System.out.println("Not a valid line");
	    }
	}while(res==null);
	return res;
    }
}

	
