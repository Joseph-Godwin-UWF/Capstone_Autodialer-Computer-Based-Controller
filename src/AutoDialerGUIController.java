import com.fazecast.jSerialComm.SerialPort;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AutoDialerGUIController {

    @FXML private ComboBox<String> comPortComboBox;
    @FXML private ComboBox<String> stepSizeComboBox;
    @FXML TextField combinationTextBox;
    @FXML Button closePortButton;
    @FXML Button openPortButton;
    @FXML Button startDialingButton;
    @FXML Button stopDialingButton;
    @FXML Button setStepResolution;
    @FXML Button setDialingSpeedButton;
    @FXML TextField combo1;
    @FXML TextField combo2;
    @FXML TextField combo3;
    @FXML TextField backlashTextBox;
    @FXML TextField dialingSpeedTextBox;

    SerialPort[] coms;
    SerialPort microControllerSerialPort = null;
    SerialMessenger messenger;

    Dialer dialer;
    int microStepMultiplier = 1;
    int[] stepSizeSelectionBits = new int[]{0, 0, 0};
    ComboParser comboParser;
    StopThread dialThread;

    boolean TorqueDataWroteToFile = false;
    ArrayList<Integer> torqueReadings = new ArrayList<Integer>();

    @FXML
    public void initialize(){
        updatePortSelectionComboBox();
        populateStepSizeSelectionComboBox();
        numericValuesLessThanOneHundredOnly(combo1);
        numericValuesLessThanOneHundredOnly(combo2);
        numericValuesLessThanOneHundredOnly(combo3);
        numericValuesLessThanOneThousandOnly(dialingSpeedTextBox);
        numericValuesLessThanOneHundredOnly(backlashTextBox);
        closePortButton.setDisable(true);
        stopDialingButton.setDisable(true);
    }

    public void setStepResolutionButtonPressed(){
        //FIXME
    }

    public void setDialingSpeedButtonPressed(){
        //FIXME
    }

    /** Refreshes the Serial Port dropdown box */
    public void refreshButtonClicked(){
        comPortComboBox.getItems().clear();
        updatePortSelectionComboBox();

    }

    /** Opens the selected SerialPort
     *  If no SerialPort is selected
     *  prints error message and returns*/
    public void openComPortButtonClicked(){
        int portIndex = comPortComboBox.getSelectionModel().getSelectedIndex();
        // If a port is selected, open it
        if(portIndex != -1) {
            microControllerSerialPort = coms[portIndex];
            messenger = new SerialMessenger(microControllerSerialPort);
            openPortButton.setDisable(true);
            closePortButton.setDisable(false);
        }
        else {
            System.out.println("Please select a port");
        }

    }

    /** Closes SerialPort
     * If still dialing, calls stopDialingButtonPressed() */
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

        dialer = new Dialer(getStartingCombination());
        comboParser = new ComboParser(dialer);
        comboParser.setStepSizeMultiplier(microStepMultiplier);

        //FIXME: SHOULD PROBABLY SET IN GUI
        messenger.sendMessage("SetUpStepper:1.8;200;200");//FIXME ..........................................
        messenger.sendMessage("TurnDial:0");/*remove?*/
        dialThread = new StopThread();
        dialThread.start();
        startDialingButton.setDisable(true);
        stopDialingButton.setDisable(false);
    }

    /** Stops the dialer thread and updates current combination */
    public void stopDialingButtonPressed(){
        dialer.setCurrentCombination(dialThread.stopThread());
        startDialingButton.setDisable(false);
        stopDialingButton.setDisable(true);
    }

    //FIXME: THIS BUTTON NO LONGER EXISTS
    /** Sends command for setting microStep bits
     *  based on the selected step resolution */
    public void setDialButtonPressed(){
        setMicroStepMultiplier(); //Sets microStepping bits
        String stepBits = getStepSizeSelectionBits();
        messenger.sendMessage("SetStepBits:" + stepBits);
    }

    private String getStepSizeSelectionBits(){
        StringBuilder bits = new StringBuilder();
        for(int bit : stepSizeSelectionBits){
            bits.append(Integer.toString(bit));
        }
        return bits.toString();
    } //FIXME: VERIFY THIS WORKS

    private void updatePortSelectionComboBox(){
        coms = SerialPort.getCommPorts();
        List<String> availableComPorts = new ArrayList<>();

        for(SerialPort port : coms)
            availableComPorts.add(port.getDescriptivePortName());
        comPortComboBox.getItems().addAll(availableComPorts);
    }

    private void populateStepSizeSelectionComboBox(){
        List<String> stepSizes = new ArrayList<>(Arrays.asList("1","1/2","1/4","1/8","1/16"));
        stepSizeComboBox.getItems().addAll(stepSizes);
    }

    private void setMicroStepMultiplier(){
        //check if stepSizeComboBox is empty
        if(stepSizeComboBox.getSelectionModel().isEmpty()){
            microStepMultiplier = 1; //DEFAULT TO FULL STEP
            setStepSizeSelectionBits();
            return;
        }
        String stepSizeFromComboBox = stepSizeComboBox.getSelectionModel().getSelectedItem();
        String stepSizeDenominator = stepSizeFromComboBox.substring(stepSizeFromComboBox.indexOf('/') + 1);
        microStepMultiplier = Integer.parseInt(stepSizeDenominator);
        setStepSizeSelectionBits();
    }

    private void setStepSizeSelectionBits(){
        //FIXME: 1/2 and 1/4 probably swapped, no testing done yet
        switch (microStepMultiplier){
            case 2:
                stepSizeSelectionBits = new int[]{1,0,0};
                break;
            case 4:
                stepSizeSelectionBits = new int[]{0,1,0};
                break;
            case 8:
                stepSizeSelectionBits = new int[]{1,1,0};
                break;
            case 16:
                stepSizeSelectionBits = new int[]{1,1,1};
                break;
            default:
                stepSizeSelectionBits = new int[]{0,0,0};
                break;
        }
    }


    public static void numericValuesLessThanOneHundredOnly(final TextField field) {

        // NUMERIC VALUES ONLY
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // 2 DIGITS MAX (0,99]
        int MAX_DIGITS = 2;
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.length() > MAX_DIGITS){
                field.setText(field.getText().substring(0, MAX_DIGITS));
            }
        });
    }

    public static void numericValuesLessThanOneThousandOnly(final TextField field) {

        // NUMERIC VALUES ONLY
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // 3 DIGITS MAX (0,999]
        int MAX_DIGITS = 3;
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > MAX_DIGITS) {
                field.setText(field.getText().substring(0, MAX_DIGITS));
            }
        });
    }

    private int[] getStartingCombination(){
        boolean c1Populated = !combo1.getText().isEmpty();
        boolean c2Populated = !combo2.getText().isEmpty();
        boolean c3Populated = !combo3.getText().isEmpty();
        if(c1Populated && c2Populated && c3Populated){
            return new int[]{ Integer.parseInt(combo1.getText()),
                              Integer.parseInt(combo2.getText()),
                              Integer.parseInt(combo3.getText())  };
        }
        return new int[] { 0 ,0 ,0 };
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
                            if(!TorqueDataWroteToFile){
                                TorqueDataWroteToFile = true;
                                System.out.println("COMBO FOUND");
                                try {
                                    printTorqueDataToFile();
                                 } catch (IOException e) {
                                   throw new RuntimeException(e);
                                }
                            }
                            break;
                        case "003":
                            //FIXME: ADD TORQUE DATA TO ARRAYLIST
                            int index = message.indexOf('>');
                            String data = message.substring(index + 1).trim();
                            System.out.println("Adding to torqueData: " + data);
                            torqueReadings.add(Integer.parseInt(data));
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

    public void printTorqueDataToFile() throws IOException {
        try{
            // Create new file
            String path="C:\\TorqueData";
            File file = getUniqueFileName(path, "torqueReadings.txt");
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for(Integer reading : torqueReadings) {
                bw.write(Integer.toString(reading));
                bw.write(";");
            }


            // Close connection
            bw.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    private File getUniqueFileName(String folderName, String searchedFilename) {
        int num = 1;
        String extension = ".txt";
        String filename = searchedFilename.substring(0, searchedFilename.lastIndexOf("."));
        File file = new File(folderName, searchedFilename);
        while (file.exists()) {
            searchedFilename = filename + "("+(num++)+")"+extension;
            file = new File(folderName, searchedFilename);
        }
        return file;
    }

}
