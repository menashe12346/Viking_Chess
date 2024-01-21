import java.util.Comparator;

public class ComparatorChess implements Comparator<Piece> {

    private final ComparisonType comparisonType;

    private ConcretePlayer winner;

    public enum ComparisonType {
        StepsHistory,
        NumOfKills,
        Squares,
    }

    public ComparatorChess(ComparisonType comparisonType, ConcretePlayer winner) {
        this.comparisonType = comparisonType;
        this.winner=winner;
    }

    @Override
    public int compare(Piece piece1, Piece piece2) {
        switch (comparisonType) {
            case StepsHistory:
                return compareByStepsHistory(piece1, piece2);
            case NumOfKills:
                return compareByNumOfKills(piece1, piece2);
            case Squares:
                return compareByNumOfSquares(piece1, piece2);
            default:
                throw new IllegalArgumentException("Invalid comparison type");
        }
    }

    public int compareByStepsHistory(Piece piece1, Piece piece2) {
        int size1 = piece1 instanceof Pawn? ((Pawn) piece1).piecesStepsHistory.size(): ((King) piece1).KingStepsHistory.size();
        int size2 = piece2 instanceof Pawn? ((Pawn) piece2).piecesStepsHistory.size(): ((King) piece2).KingStepsHistory.size();

        // Compare based on the size of the position ArrayList
        int sizeComparison = Integer.compare(size1, size2);

        // If sizes are equal, compare by number
        if (sizeComparison == 0) {
            int num1 = piece1 instanceof Pawn ? Integer.parseInt(((Pawn) piece1).name.substring(1)) : Integer.parseInt(((King) piece1).name.substring(1)) ;
            int num2 = piece2 instanceof Pawn ? Integer.parseInt(((Pawn) piece2).name.substring(1)) : Integer.parseInt(((King) piece2).name.substring(1));

            return Integer.compare(num1, num2);
        }

        return sizeComparison;
    }

    public int compareByNumOfKills(Piece piece1, Piece piece2) {
        int kills1 = ((Pawn) piece1).numOfKills;
        int kills2 = ((Pawn) piece2).numOfKills;

        // Compare based on the number of kills of the pawn (Descending)
        int killsComparison = Integer.compare(kills2, kills1);

        if(killsComparison == 0){
            int num1 = piece1 instanceof Pawn ? Integer.parseInt(((Pawn) piece1).name.substring(1)) : Integer.parseInt(((King) piece1).name.substring(1)) ;
            int num2 = piece2 instanceof Pawn ? Integer.parseInt(((Pawn) piece2).name.substring(1)) : Integer.parseInt(((King) piece2).name.substring(1));

            int numComparison = Integer.compare(num1, num2);

            if(numComparison == 0){

                if(((Pawn) piece1).name.charAt(0)=='D') {
                    if(winner.isPlayerOne()){
                        return -1;
                    } else{
                        return 1;
                    }
                }else{
                    if(winner.isPlayerOne()){
                        return 1;
                    } else{
                        return -1;
                    }
                }
            }

            return numComparison;

        }

        return  killsComparison;

    }

    public int compareByNumOfSquares(Piece piece1, Piece piece2) {
        int size1 = piece1 instanceof Pawn? ((Pawn) piece1).sumDistance: ((King) piece1).sumDistance;
        int size2 = piece2 instanceof Pawn? ((Pawn) piece2).sumDistance: ((King) piece2).sumDistance;

        // Compare based on the number of the Suares
        int squaresComparison = Integer.compare(size2, size1);

        if(squaresComparison == 0){

            int num1 = piece1 instanceof Pawn ? Integer.parseInt(((Pawn) piece1).name.substring(1)) : Integer.parseInt(((King) piece1).name.substring(1)) ;
            int num2 = piece2 instanceof Pawn ? Integer.parseInt(((Pawn) piece2).name.substring(1)) : Integer.parseInt(((King) piece2).name.substring(1));

            int numComparison = Integer.compare(num1, num2);

            if(numComparison == 0){

                String objectType1 = (piece1 instanceof Pawn) ? "Pawn" : "King";
                String objectType2 = (piece2 instanceof Pawn) ? "Pawn" : "King";

                if ((objectType1.equals("Pawn") && ((Pawn) piece1).name.charAt(0) == 'D') || objectType1.equals("King")) {
                    if (winner.isPlayerOne()) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    if (winner.isPlayerOne()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }

            return  numComparison;
        }
         return squaresComparison;
    }

    }