import java.io.File;
import java.util.Scanner;

/**
 * Write a description of class Run here.
 * 
 * @author Jordan Cartwright
 * @author Jordan McKosky
 * @version 12/12/2016
 */
public class DriverTest_copy{
    /**
     * This main method calls allows the user to input their instructios for the assembler to translat
     * to binary. It calls copyFile and assembleFile.
     */
    public static void main (String args[]){
        //make sure the program has been run at least once to display a log file
        int timesRun = 0;
        String inputFileOld = null;
        softwareProject project = new softwareProject();
        Scanner in = new Scanner(System.in);
        //create log file
        File logFile = new File(project.logFileName);

        //loop determines if the program should still run
        while(project.run) {
            //alert user that they can quit
            System.out.println("Commands:");
            System.out.println("\t" + "exit - Quit Program");
            if(timesRun > 0){
                System.out.println("\t" + "viewlog - Open Log File");
            }

            //get the input filename from the user
            System.out.print("Input file name(.txt):" + "\t");
            String inputFileName = in.next() + ".txt";
//            String inputFileName = "basic.txt";
            System.out.println("\t" + inputFileName);

            //set the name in project
            project.inputFileName = inputFileName;
            File inputFile = new File(inputFileName);
            if(inputFileName.equals("viewlog.txt")== false) {
                //keep tack of the last known inputfile to print log report to screen
                inputFileOld = inputFileName;
            }

            //System.out.println("\t" + inputFile.length() + "\t" + inputFile.getAbsolutePath());
            if(inputFileName.equals("viewlog.txt")) {
                //only if the program has already generated a log file
                if(timesRun > 0){
                    System.out.println();   //line break
                    System.out.println("\t" +"Log Report (" + inputFileOld +")");
                    System.out.println("\t" + project.errorCount + " Errors Detected");

                    //print out the log file
                    for(int i = 0; i < project.errorMessages.size(); i = i+2 ){
                        //print out the fromatted log file
                        System.out.println("\t" + project.errorMessages.get(i));
                        System.out.println("\t" + "\t" + project.errorMessages.get(i+1));
                    }
                    System.out.println();//line brake
                }
                else {System.out.println("\t" + "No Log File has been created yet");}
            }
            else {
                //reset defaults for new run
                project.currentLineNumber = 0;
                project.fileNotFound = false;
                project.binaryCode.clear();
                project.errorMessages.clear();
                project.errorCount = 0;
                project.inputCode.clear();

                //user names the outputfile name
                System.out.print("Output file name(.txt):" + "\t");
                String outFile = in.next();
                //set the name in project
                project.outFileName = outFile;
                File outputFile = new File(outFile);
                System.out.println();   //line break
                //Load File
                project.copyFile(inputFile);
                //Translate File
                project.assembleFile(inputFile);

                //check to see if we can write the file and
                //alert the user the conversion is complete
                if(project.errorCount > 0) {
                    //only do if there are errors
                    int lineCount = 1;
                    for(int i = 0; i < project.binaryCode.size(); i++ ){
                        //print out the binary code w/ errors
                        System.out.println("\t" +" "+ lineCount + "\t" + project.binaryCode.get(i));
                        lineCount++;
                    }
                    project.writeFile();
                    project.writeLogFile();
                    System.out.println();   //line break
                    //alert user that conversion has failed
                    System.out.println("Conversion incomplete!");
                    //indicates how many errors found in the file
                    System.out.println("\t" + "Alert: " + project.errorCount + " Errors Detected");
                    System.out.println("\t" + "Check Log File for more information");
                    System.out.println();   //line break
                    System.out.println("Log file saved to:");
                    System.out.println("\t" + logFile.getAbsolutePath());
                    System.out.println();   //line break
                    timesRun++;
                }
                else if(project.fileNotFound == true) {
                    //if the input file cannot be found then throw an error
                    System.out.println("\t" + "ERROR: File Not Found");
                    System.out.println();   //line break
                }
                else {
                    //write the file containig binary code
                    project.writeFile();
                    project.writeLogFile();
                    System.out.println();   //line break
                    //alert user that conversion is complete
                    System.out.println("Conversion Complete!");
                    //indicate where the translated file has been saved
                    System.out.println("Binary File saved to:");
                    System.out.println("\t" + outputFile.getAbsolutePath());
                    System.out.println();   //line break
                    System.out.println("Log file saved to:");
                    System.out.println("\t" + logFile.getAbsolutePath());
                    //end program
                    project.run = false;
                }
            }
        }
        System.exit(-1);
    }

}
