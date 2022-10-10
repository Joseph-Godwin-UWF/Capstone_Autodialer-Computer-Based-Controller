import com.fazecast.jSerialComm.*;

import java.util.concurrent.TimeUnit;

public class Test {
    private int[] prevCombo = {1,2,3,4,2,6,7,9};
    private int[] combo = {1, 2, 3, 4, 5, 6, 7, 8};

    Test() {

    }

    private boolean previousComboOnlyDiffersByLastNumber() {
        if (prevCombo[0] == -998)
            return true;

        for (int i = 0; i < this.combo.length - 1; i++) {
            if (this.combo[i] != this.prevCombo[i])
                return false;
        }
        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        Test t = new Test();
        if (t.previousComboOnlyDiffersByLastNumber())
            System.out.println("hello");
        else
            System.out.println("nope");


    }
}
