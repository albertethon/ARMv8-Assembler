import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class Armv8gui {
    softwareProject project = new softwareProject();
    //create log file
    File logFile = new File(project.logFileName);

    String inputFileName = "input.txt";
    String outputFileName = "output.txt";

    File sampleFile = new File(project.sampleFileName);

    @FXML
    private TextArea inputArea;

    @FXML
    private Button compile;

    @FXML
    private Button sample_code;

    @FXML
    private Button view_log;

    @FXML
    private TextArea outputArea;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label label1;

    @FXML
    private Label label2;

    @FXML
    private Button cancelButton;

    @FXML
    private TextArea sample_code_area;

    @FXML
    void compileClicked(ActionEvent event) {
        project.inputFileName = inputFileName;
        project.outFileName = outputFileName;
        File compilefile = new File(inputFileName);
        File machinecodefile = new File(outputFileName);

        try {
            if (!compilefile.exists()) {
                compilefile.createNewFile();
            }
            if(!machinecodefile.exists()){
                machinecodefile.createNewFile();
            }
            // write input area into the file
            FileInputStream infile = new FileInputStream(machinecodefile);
            BufferedInputStream bis = new BufferedInputStream(infile);
            FileOutputStream outputStream = new FileOutputStream(compilefile);
            outputStream.write((inputArea.getText()).getBytes(StandardCharsets.UTF_8));
            outputStream.close();

            //reset defaults for new run
            project.currentLineNumber = 0;
            project.fileNotFound = false;
            project.binaryCode.clear();
            project.errorMessages.clear();
            project.errorCount = 0;
            project.inputCode.clear();
            //Load File
            project.copyFile(compilefile);
            //Translate File
            project.assembleFile(compilefile);

            //check to see if we can write the file and
            //alert the user the conversion is complete
            //only do if there are errors
/*                    int lineCount = 1;
            for(int i = 0; i < project.binaryCode.size(); i++ ){
                //print out the binary code w/ errors
                System.out.println("\t" +" "+ lineCount + "\t" + project.binaryCode.get(i));
                lineCount++;
            }*/
            project.writeFile();
            project.writeLogFile();
            int len;
            byte[] buf = new byte[1024];
            StringBuilder cmpresult = new StringBuilder();
            while ((len = bis.read(buf)) != -1) {
                cmpresult.append(new String(buf, 0, len));
            }
            int tmp=0;
            while(tmp < cmpresult.length()){
                if(cmpresult.charAt(tmp) != 'E'){
                    for(int i=28;i>3;i-=4){
                        cmpresult.insert(tmp + i,' ');//repeat 7 times each line
                    }
                    tmp = tmp + 32 + 7;// 32 machine code,7 blank characters,2 \r\n,1 \n
                }
                int lineend=0;
                lineend = cmpresult.indexOf("\n",tmp);
                if(cmpresult.charAt(lineend - 1) != '\r'){
                    tmp = lineend + 1;
                }else{
                    tmp = lineend + 2;
                }
            }
            tmp = 0;
            outputArea.setText(cmpresult.toString());

            bis.close();
            infile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        label2.setText("machine code");
    }

    @FXML
    void view_logClicked(ActionEvent event) {
        try {
            if(!logFile.exists()) {
                logFile.createNewFile();
            }
            FileInputStream infile = new FileInputStream(logFile);
            BufferedInputStream bis = new BufferedInputStream(infile);
            int len;
            byte[] buf = new byte[1024];
            while ((len = bis.read(buf)) != -1) {
                outputArea.setText(new String(buf));
            }
            label2.setText("compile log");
            bis.close();
            infile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void sample_code_clicked(ActionEvent event) throws IOException {
//        Parent root = FXMLLoader.load(getClass().getResource("Sample_code.fxml"));
        URL location = getClass().getResource("Sample_code.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        Parent samplePane = fxmlLoader.load();
        Stage sampleStage = new Stage();
        sampleStage.setTitle("SCUT ARM-8 assembler sample code");
        sampleStage.setResizable(true);
        sampleStage.setScene(new Scene(samplePane));
        SampleCode sampleCodeController = fxmlLoader.getController();
        sampleCodeController.init();

        sampleStage.show();


    }

}




