/**
 * Created by TONDEUR H on 16/05/2017.
 */
public class Movement {

    private int i;
    private int j;

    public Movement(int i,  int j) {
        this.i = i;
        this.j = j;
    }

    public boolean isSameMovement(int i, int j) {
        return  (this.i == i && this.j == j);
    }
}
