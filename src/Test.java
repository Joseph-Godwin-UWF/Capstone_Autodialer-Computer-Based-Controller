import com.fazecast.jSerialComm.*;

import java.util.concurrent.TimeUnit;

public class Test {
    private int[] prevCombo = {1,2,3,4,2,6,7,9};
    private int[] combo = {1, 2, 3, 4, 5, 6, 7, 8};

    Test() {

    }


    public static void main(String[] args) throws InterruptedException {
        Test t = new Test();

        Dialer dialer = new Dialer(3, 360);
        dialer.setCurrentCombination(new int[]{40, 98, 54});

        for(int i = 0; i < 200; i++){
            dialer.getNextCombination();
            System.out.println(dialer.ToString());
        }


    }
}
