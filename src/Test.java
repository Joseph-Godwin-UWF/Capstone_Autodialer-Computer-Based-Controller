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
        ComboParser parser = new ComboParser(dialer, new int[]{270, 180, 358});

        System.out.println(parser.getNextRotationCommand());
        System.out.println(parser.getNextRotationCommand());
        System.out.println(parser.getNextRotationCommand());
        System.out.println(parser.getNextRotationCommand());
        System.out.println("-----------------------------");
        System.out.println(parser.getNextRotationCommand());
        System.out.println(parser.getNextRotationCommand());
        System.out.println(parser.getNextRotationCommand());
        System.out.println(parser.getNextRotationCommand());


    }
}
