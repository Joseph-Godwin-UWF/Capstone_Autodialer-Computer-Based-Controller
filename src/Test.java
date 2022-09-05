import com.fazecast.jSerialComm.*;

import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws InterruptedException {

        SerialPort[] coms = SerialPort.getCommPorts();
        System.out.println(coms[0].getDescriptivePortName());
        SerialMessenger messenger = new SerialMessenger(coms[0]);

        TimeUnit.SECONDS.sleep(1);
        for(int i =0; i < 6; i++){
            System.out.println(messenger.getNextMessage());
        }

    }
}
