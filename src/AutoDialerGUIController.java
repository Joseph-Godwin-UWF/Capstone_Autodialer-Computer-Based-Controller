import com.fazecast.jSerialComm.SerialPort;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;


public class AutoDialerGUIController {
    @FXML private ComboBox<String> comPortComboBox;
    @FXML TextField comboSizeTextField;
    @FXML TextField tickCountTextField;
    @FXML TextField combinationTextBox;
    @FXML Button closePortButton;
    @FXML Button openPortButton;
    @FXML Button startDialingButton;
    @FXML Button stopDialingButton;

    SerialPort[] coms;
    SerialPort arduinoPort = null;
    SerialMessenger messenger;
    Dialer dialer;
    ComboParser comboParser;
    StopThread dialThread;

    @FXML
    public void initialize(){
        updateComboBox();
        numericValuesOnly(comboSizeTextField);
        numericValuesOnly(tickCountTextField);
        closePortButton.setDisable(true);
        stopDialingButton.setDisable(true);
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
            openPortButton.setDisable(true);
            closePortButton.setDisable(false);
        }
        else {
            System.out.println("Please select a port");
            return;
        }

    }
    public void closeComPortButtonClicked(){
        if(this.dialThread != null)
            stopDialingButtonPressed();
        messenger.closePort();
        openPortButton.setDisable(false);
        closePortButton.setDisable(true);
    }

    public void startDialingButtonPressed(){
        if(messenger == null || !messenger.serialPort.isOpen()) {
            System.out.println("Port is not open");
            return;
        }
        if(dialer == null){
            int comboSize = (comboSizeTextField.getText().isBlank()) ? 3 : Integer.parseInt(comboSizeTextField.getText());
            int tickCount = (tickCountTextField.getText().isBlank()) ? 100 : Integer.parseInt(tickCountTextField.getText());
            dialer = new Dialer(comboSize, tickCount);
        }
        //messenger.sendMessage("SetUpStepper:1.8;200;400");
        messenger.sendMessage("TurnDial:0");/*remove?*/
        dialer.setCurrentCombination(new int[]{40, 0, 90}); //FIXME: CHANGE THIS TO COMBO SET IN GUI
        dialThread = new StopThread();
        dialThread.start();
        startDialingButton.setDisable(true);
        stopDialingButton.setDisable(false);
    }

    public void stopDialingButtonPressed(){
        dialer.setCurrentCombination(dialThread.stopThread());
        startDialingButton.setDisable(false);
        stopDialingButton.setDisable(true);
    }

    public void setDialButtonPressed(){
        if(comboSizeTextField.getText().isBlank() || tickCountTextField.getText().isBlank()){
            System.out.println("Dialer Variable entree left blank");
            return;
        }
        int comboSize = Integer.parseInt(comboSizeTextField.getText());
        int dialTicks = Integer.parseInt(tickCountTextField.getText());
        dialer = new Dialer(comboSize, dialTicks);
        comboParser = new ComboParser(dialer);
        messenger.sendMessage("SetUpStepper:1.8;100;100");
    }

    private void updateComboBox(){
        coms = SerialPort.getCommPorts();
        List<String> availableComPorts = new ArrayList<>();

        for(SerialPort port : coms)
            availableComPorts.add(port.getDescriptivePortName());
        comPortComboBox.getItems().addAll(availableComPorts);
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
            String code;
            while (!exit) {
                message = messenger.getNextMessage();
                code = messenger.getMessageCodeNumber(message);
                if (message != null) {
                    switch(code){
                        case "000":
                            //FIXME Successfully initialized stepper motor parameters
                            //ready to begin dialing
                            //what needs to be done on software side?
                            //-allow "start dialing"
                            System.out.println("ARDUINO READY TO ACCEPT TURN COMMANDS");
                        case "001":
                            messenger.sendMessage(comboParser.getNextRotationCommand());
                            combinationTextBox.setText("Dialing: " + comboParser.getComboAsString());
                            break;
                        case "002":
                            //FIXME Previous dial turn complete, open bolt torque threshold reached(safe presumed unlocked)
                            // -combination likely found
                            // -task complete
                            // display the correct combo
                            System.out.println("COMBO FOUND");
                            break;
                        case "600":
                            //FIXME: invalid message
                            // -waiting for SetUpStepper message
                            // -prompt for setup stepper
                            System.out.println("Invalid message, send SetUpStepper message");
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
