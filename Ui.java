
public interface Ui{
    public int menu(String[] titleAndChoices);
    public int askInt(String title, int min, int max);
    public boolean yesNoChoice(String title);
    public void display(String title);
    public void display(MultiPlayersTurnGame g);
    public MultiPlayersTurnGame play(MultiPlayersTurnGame g);
}
