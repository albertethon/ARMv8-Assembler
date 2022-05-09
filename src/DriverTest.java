import java.io.*;
import java.lang.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Write a description of class Run here.
 * 
 * @author Jordan Cartwright
 * @author Jordan McKosky
 * @version 12/12/2016
 */
public class DriverTest extends Application{
    /**
     * This main method calls allows the user to input their instructios for the assembler to translat
     * to binary. It calls copyFile and assembleFile.
     */
    public static void main (String args[]){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Armv8gui.fxml"));
/*

        Label label1 = new Label("instruction");
        Label label2 = new Label("machine code");
        TextArea inputField = new TextArea();
        TextArea outputField = new TextArea();

        outputField.setEditable(false);
        Button compile = new Button();
        Button logout = new Button();
        compile.setText("Compile");
        logout.setText("view log");

        HBox hb1 = new HBox();
        HBox hb2 = new HBox();
        HBox hb3 = new HBox();

        hb1.getChildren().addAll(label1, label2);
        hb1.setSpacing(500);
        hb1.setAlignment(Pos.CENTER);
        hb2.getChildren().addAll(inputField, outputField);
        hb2.setSpacing(10);
        hb3.getChildren().addAll(compile, logout);
        hb3.setSpacing(50);
        hb3.setAlignment(Pos.CENTER);


        VBox vBox = new VBox();
        vBox.getChildren().addAll(hb1, hb2, hb3);

        vBox.setAlignment(Pos.CENTER);
*/
        Scene scene = new Scene(root);
        stage.setTitle("SCUT ARM-8 assembler");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }
}
