import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
            ByteArrayOutputStream result = new ByteArrayOutputStream();

            while ((len = url.read(buf)) != -1) {
                result.write(buf,0,len);// 要用到长度，否则会写入上一轮未清空字符
            }
            sample_code_area.setText(result.toString());
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
