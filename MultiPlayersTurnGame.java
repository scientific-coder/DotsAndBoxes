public interface MultiPlayersTurnGame{
    public int getPlayer();
    public int[] scores();
    public int nbMoves();
    public MultiPlayersTurnGame next(String move);
    public MultiPlayersTurnGame next(int possibleMoveId);
}
