import com.fazecast.jSerialComm.SerialPort;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class AutoDialerGUIController {
    @FXML
    private ComboBox<String> comPortComboBox;
    @FXML
    TextField comboSizeTextField;
    @FXML
    TextField tickCountTextField;
    @FXML
    TextField combinationTextBox;

    SerialPort[] coms;
    SerialPort arduinoPort = null;
    SerialMessenger messenger;
    Dialer dialer;
    StopThread dialThread;

    @FXML
    public void initialize(){
        updateComboBox();
        numericValuesOnly(comboSizeTextField);
        numericValuesOnly(tickCountTextField);
    }

    public void refreshButtonClicked(){
        comPortComboBox.getItems().clear();
        updateComboBox();

    }
    public void openComPortButtonClicked(){
        int portIndex = comPortComboBox.getSelectionModel().getSelectedIndex();
        if(portIndex != -1) { // if an item is selected
            arduinoPort = coms[portIndex];
            messenger = new SerialMessenger(arduinoPort);
        }
        else
            System.out.println("Please select a port");
    }
    public void closeComPortButtonClicked(){
        messenger.closePort();
    }

    private void updateComboBox(){
        coms = SerialPort.getCommPorts();
        List<String> availableComPorts = new ArrayList<>();

        for(SerialPort port : coms)
            availableComPorts.add(port.getDescriptivePortName());
        comPortComboBox.getItems().addAll(availableComPorts);
    }

    public void startDialingButtonPressed(){
        //FIXME: add check that port is opened
        messenger.sendMessage("Ready");
        dialThread = new StopThread();
        dialThread.start();
    }

    public void stopDialingButtonPressed(){
        dialer.setCurrentCombination(dialThread.stopThread());
    }

    public void setDialButtonPressed(){
        int comboSize = Integer.parseInt(comboSizeTextField.getText());
        int dialTicks = Integer.parseInt(tickCountTextField.getText());
        dialer = new Dialer(comboSize, dialTicks);
    }

    public static void numericValuesOnly(final TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    class StopThread extends Thread {
        /* Setting the volatile variable
           exit to false */
        private volatile boolean exit = false;

        /* Call this method to set the
           exit value to true and stop
           the thread */
        public int[] stopThread() {
            exit = true;
            return dialer.getCurrentCombination();
        }

        @Override
        public void run() {
            // Keep the task in while loop
            String message;
            while (!exit) {
                message = messenger.getNextMessage();
                if (message != null) {
                    switch(message){
                        case "Next Combo\r\n":
                            dialer.getNextCombination();
                            messenger.sendMessage(dialer.ToString());
                            combinationTextBox.textProperty().set("Dialing: " + dialer.ToString());
                            System.out.println(dialer.ToString());
                            break;
                        case "Safe Cracked\r\n":
                            //FIXME add something here
                            break;
                        default:
                            System.out.print("Invalid: " + message);

                    }
                }
            }
            System.out.println("Thread Stopped.... ");
        }
    }

}
