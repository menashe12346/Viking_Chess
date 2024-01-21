import java.util.ArrayList;

public class King extends ConcretePiece{

    public String name;

    public ArrayList<Position> KingStepsHistory = new ArrayList<Position>();

    public int sumDistance;
    public King(Player owner, String name, Position p) {
        super(owner);
        this.name = name;
        this.KingStepsHistory.add(p);
        sumDistance=0;
    }

    @Override
    public String getType() {
        return "♔";
    }

    public void move(Position a, Position b){
        this.KingStepsHistory.add(b);

        //compute the distance the pawn moved until now
        sumDistance+= Math.abs(a.getRow()- b.getRow()) + Math.abs(a.getCol()- b.getCol());
    }

    public void printDistance(){
        if(this.sumDistance>0) {
            System.out.println(this.name + ": " + this.sumDistance + " squares");
        }
    }

    public void printStepsHistory(){
        if(this.KingStepsHistory.size()>1) {
            String print = "";
            print += this.name + ": [";
            for (int i = 0; i < KingStepsHistory.size(); i++) {
                print += "(" + KingStepsHistory.get(i).getRow() + ", " + KingStepsHistory.get(i).getCol() + ")";
                if (i != KingStepsHistory.size() - 1) print += ", ";
            }
            print += "]";
            System.out.println(print);
        }
    }

}
