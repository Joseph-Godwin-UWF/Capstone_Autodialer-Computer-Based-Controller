import com.fazecast.jSerialComm.SerialPort;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class AutoDialerGUIController {
    @FXML
    private ComboBox<String> comPortComboBox;

    SerialPort[] coms;
    SerialPort arduinoPort = null;

    @FXML
    public void initialize(){
        updateComboBox();
    }

    public void refreshButtonClicked(){
        System.out.println("Pressed refresh button");
        comPortComboBox.getItems().clear();
        updateComboBox();

    }
    public void openComPortButtonClicked(){
        int portIndex = comPortComboBox.getSelectionModel().getSelectedIndex();
        if(portIndex != -1) { // if an item is selected
            arduinoPort = coms[portIndex];
            arduinoPort.openPort();
        }

        if(arduinoPort.isOpen()){
            System.out.println("Port Is Open");
            arduinoPort.setBaudRate(9600);
            String command = "connecting";
            arduinoPort.writeBytes(command.getBytes(), command.length());
        }

    }
    public void closeComPortButtonClicked(){
        System.out.println("Pressed closeComPort button");
    }

    private void updateComboBox(){
        coms = SerialPort.getCommPorts();
        List<String> availableComPorts = new ArrayList<String>();

        for(SerialPort port : coms)
            availableComPorts.add(port.getDescriptivePortName());
        comPortComboBox.getItems().addAll(availableComPorts);
    }
}
