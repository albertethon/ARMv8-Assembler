import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;

public class SampleCode {
    softwareProject project = new softwareProject();
    File sampleFile ;
    @FXML
    private Button cancelButton;

    @FXML
    private TextArea sample_code_area;

    public void init(){
        try {
            InputStream url = DriverTest.class.getClassLoader().getResourceAsStream(project.sampleFileName);
//            sampleFile = new File(getClass().getResource(project.sampleFileName).getPath());
//            FileInputStream infile = new FileInputStream(sampleFile);
//            BufferedInputStream bis = new BufferedInputStream(infile);
            int len;
            byte[] buf = new byte[1024];
            while ((len = url.read(buf)) != -1) {
                sample_code_area.setText(new String(buf));
            }
//            bis.close();
//            infile.close();
            url.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Cancle_clicked(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
