import com.fazecast.jSerialComm.*;

import java.util.concurrent.TimeUnit;

public class Test {
    private int[] prevCombo = {1,2,3,4,2,6,7,9};
    private int[] combo = {1, 2, 3, 4, 5, 6, 7, 8};

    Test() {

    }


    public static void main(String[] args) throws InterruptedException {
        Test t = new Test();

        Dialer dialer = new Dialer();
        dialer.setCurrentCombination(new int[]{0, 0, 0});
        ComboParser parser = new ComboParser(dialer);

        System.out.println("Starting Combo: " + dialer.ToString());
        for(int i = 0; i < 15; i++){
            System.out.println(parser.getNextRotationCommand());
        }


    }
}
