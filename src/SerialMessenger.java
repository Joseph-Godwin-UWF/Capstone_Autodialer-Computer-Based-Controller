import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.beans.EventHandler;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SerialMessenger implements SerialPortMessageListener {

    List<String> dataBuffer = new ArrayList<>();
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
        this.openPort();
        if(!this.serialPort.isOpen()){
            System.out.println("Error connecting to Serial Port");
            System.exit(0);
        }

        if(this.serialPort.bytesAvailable() > 0) {
            serialPort.flushIOBuffers();
        }
    }

    public void closePort(){
        this.serialPort.removeDataListener();
        this.serialPort.closePort();
    }

    public void openPort(){
        this.serialPort.openPort();
        this.serialPort.addDataListener(this);
    }

    String getNextMessage(){
        if(dataBuffer.size() == 0)
            return null;
        String message = dataBuffer.remove(0);
        return message;
    }

    String getMessageCodeNumber(String message){
        if(message == null)
            return null;
        message = message.trim(); //removes leading and trailing whitespace
        int index = message.indexOf(' ');

        if (index < -1) //message is only one word
            return message;
        return message.substring(0, index).trim(); //returns the first word without whitespace
    }

    public void sendMessage(String message){
        message += "\r\n";
        this.serialPort.writeBytes(message.getBytes(), message.length());
    }

    public void showMessages(){
        for(String message : dataBuffer){
            System.out.print(message);
        }
    }
}
