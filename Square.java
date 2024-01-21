import java.util.ArrayList;

public class Square {

    public ArrayList<Piece> pieces;
    public Position position;

    public Square(int row, int col){
        this.pieces = new ArrayList<>();
        this.position = new Position(row, col);
    }

    public void addPiece(Piece newPiece){
        boolean isExist=false;
        for(Piece piece : this.pieces){
            if(piece == newPiece) {
                isExist = true;
                break;
            }
        }

        if(!isExist){
            this.pieces.add(newPiece);
        }
    }

}
