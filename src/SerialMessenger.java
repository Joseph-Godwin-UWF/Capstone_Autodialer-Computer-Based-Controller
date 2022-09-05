import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SerialMessenger implements SerialPortMessageListener {

    List<String> dataBuffer = new ArrayList<String>();
    SerialPort serialPort;

    @Override
    public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

    @Override
    public byte[] getMessageDelimiter() { return new byte[] { (byte)0x0A }; }

    @Override
    public boolean delimiterIndicatesEndOfMessage() { return true; }

    @Override
    public void serialEvent(SerialPortEvent event) {
        byte[] delimitedMessage = event.getReceivedData();
        String newMessage = new String(delimitedMessage, StandardCharsets.UTF_8);
        dataBuffer.add(newMessage);
    }

    SerialMessenger(SerialPort serialPort){
        this.serialPort = serialPort;
        this.serialPort.openPort();
        this.serialPort.addDataListener(this);
    }

    String getNextMessage(){
        if(dataBuffer.isEmpty())
            return null;
        String message = dataBuffer.get(0);
        dataBuffer.remove(0);
        return message;
    }

    public void showMessages(){
        for(String message : dataBuffer){
            System.out.print(message);
        }
    }

}
