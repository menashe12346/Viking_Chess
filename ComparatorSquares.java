import java.util.Comparator;

public class ComparatorSquares implements Comparator<Square> {
    private ConcretePlayer winner;

    public ComparatorSquares(ConcretePlayer winner) {
        this.winner=winner;
    }

    @Override
    public int compare(Square square1, Square square2) {

        int size1 = square1.pieces.size();
        int size2 = square2.pieces.size();

        int SizeComparison = Integer.compare(size2, size1);

        if(SizeComparison == 0){
            int xComparison = Integer.compare(square1.position.getRow(), square2.position.getRow());

            if(xComparison == 0){
                return Integer.compare(square1.position.getCol(), square2.position.getCol());
            }
            return xComparison;
        }

        return SizeComparison;

    }

}
