import com.fazecast.jSerialComm.*;

import java.util.concurrent.TimeUnit;

public class Test {

    Test() {

    }


    public static void main(String[] args) throws InterruptedException {
        Test test = new Test();
        Dialer dialer = new Dialer(new int[]{38, 50, 0});
        ComboParser parser = new ComboParser(dialer);

        for(int i = 0; i < 10; i++){
            System.out.println(parser.getNextRotationCommand());
        }

    }
}
