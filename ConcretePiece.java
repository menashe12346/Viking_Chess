public abstract class ConcretePiece implements Piece{
    private final Player owner;

    public ConcretePiece(Player owner) {
        this.owner = owner;
    }

    @Override
    public Player getOwner() {
        return owner;
    }
}
