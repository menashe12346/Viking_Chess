import java.util.ArrayList;

public class Pawn extends ConcretePiece {

    public ArrayList<Position> piecesStepsHistory = new ArrayList<Position>();
    public int numOfKills;
    public String name;
    public int sumDistance;
    public Pawn(Player owner, String name, Position p) {
        super(owner);
        this.name = name;
        this.piecesStepsHistory.add(p);
        numOfKills=0;
        sumDistance=0;
    }

    @Override
    public String getType() {
        return "♙";
    }

    //adds the new position to the arrayList
    public void move(Position a, Position b){
       this.piecesStepsHistory.add(b);

       //compute the distance the pawn moved until now
        sumDistance+= Math.abs(a.getRow()- b.getRow()) + Math.abs(a.getCol()- b.getCol());
    }

    public void printDistance(){
        if(this.sumDistance>0) {
            System.out.println(this.name + ": " + this.sumDistance + " squares");
        }
    }

    public void printStepsHistory(){
        if(this.piecesStepsHistory.size()>1) {
            String print = "";
            print += this.name + ": [";
            for (int i = 0; i < piecesStepsHistory.size(); i++) {
                print += "(" + piecesStepsHistory.get(i).getRow() + ", " + piecesStepsHistory.get(i).getCol() + ")";
                if (i != piecesStepsHistory.size() - 1) print += ", ";
            }
            print += "]";
            System.out.println(print);
        }
    }

    public void printKills(){
        if(numOfKills>0) {
            System.out.println(this.name + ": " + numOfKills + " kills");
        }
    }

    public void increaseKill(){
        this.numOfKills++;
    }
}
