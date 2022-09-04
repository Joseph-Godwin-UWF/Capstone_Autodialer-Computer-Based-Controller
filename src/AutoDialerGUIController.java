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

    @FXML
    public void initialize(){
        coms = SerialPort.getCommPorts();
        List<String> availableComPorts = new ArrayList<String>();

        for(SerialPort port : coms)
            availableComPorts.add(port.getDescriptivePortName());
        comPortComboBox.getItems().addAll(availableComPorts);
    }

    public void refreshButtonClicked(){
        System.out.println("Pressed refresh button");
    }
    public void openComPortButtonClicked(){
        System.out.println("Pressed openComPort button");
    }
    public void closeComPortButtonClicked(){
        System.out.println("Pressed closeComPort button");
    }
}
