import com.fazecast.jSerialComm.*;

public class Test {
    public static void main(String[] args){

        SerialPort[] coms = SerialPort.getCommPorts();

        for(SerialPort port : coms)
            System.out.println(port.getDescriptivePortName());

    }
}
