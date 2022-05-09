import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Random;
import java.io.*;
import java.util.ArrayList;
import java.lang.*;
import java.util.*;
import java.util.StringTokenizer;

/**
 * Write a description of class CheckOutputs here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CheckOutputs
{
    static ArrayList<String> myCode = new ArrayList<String>();
    static ArrayList<String> dansCode = new ArrayList<String>();
    static ArrayList<String> raw = new ArrayList<String>();
    /**
     * This main method calls allows the user to input their instructios for the assembler to translat
     * to binary. It calls copyFile and assembleFile.
     */
    public static void main (String args[]){
        String myFileName = "test_output1.txt";
        String dansFileName = "basic_output1.txt";
        String rawCodeFile = "basic_input1.txt";
        int lineCount = 0;
        int errorCount = 0;

        //import our file
        System.out.println("Importing: " + myFileName);
        File myFile = new File(myFileName);
        importMyFile(myFile);

        //import dans file
        System.out.println("Importing: " + dansFileName);
        File dansFile = new File(dansFileName);
        importDansFile(dansFile);

        //import rawCode file
        System.out.println("Importing: " + rawCodeFile);
        File rawCode = new File(rawCodeFile);
        importRawCodeFile(rawCode);

        //compare line by line if they are the same binary\
        for(int i = 0; i < myCode.size(); i++) 
        {
            String lineOfCode = "";
            lineOfCode = raw.get(i);
            String myLine = myCode.get(i);
            myLine = myLine.trim();
            myLine = myLine.replaceAll("\\s","");
            String dansLine = dansCode.get(i);
            dansLine = dansLine.trim();
            dansLine = dansLine.replaceAll("\\s","");
            if(myLine.equals(dansLine)) {
                System.out.println("line " + (lineCount+1) + " is exactly the same");
            }
            else{
                System.out.println("ERROR: line " + (lineCount+1) + " is not the same");
                System.out.println("\t" + "Line of Code: " + "\t" + lineOfCode); 
                System.out.println("\t" + "My Output: " + "\t" + myLine);
                System.out.println("\t" + "Dan's Output: " + "\t" + dansLine);
                errorCount++;
            }
            lineCount++;
        }
        System.out.println("There were " +  errorCount + " errors");
    }

    /**
     * This method copies the File to the arrayList, inputCode.
     */
    public static void importMyFile (File f) {
        Scanner sc = null;
        try {
            sc = new Scanner(f);
            //alerts the user that their file is being loaded for translation
            System.out.println("\t" + "Loading File...");
            while (sc.hasNext()) {

                String copyLine = sc.nextLine();

                if (copyLine.trim().length() > 0) { 
                    //if line has text...
                    //add it to an ArrayList
                    myCode.add(copyLine);

                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

    }

    /**
     * This method copies the File to the arrayList, inputCode.
     */
    public static void importDansFile (File f) {
        Scanner sc = null;
        try {
            sc = new Scanner(f);
            //alerts the user that their file is being loaded for translation
            System.out.println("\t" + "Loading File...");
            while (sc.hasNext()) {

                String copyLine = sc.nextLine();

                if (copyLine.trim().length() > 0) { 
                    //if line has text...
                    //add it to an ArrayList
                    dansCode.add(copyLine);

                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

    }

    /**
     * This method copies the File to the arrayList, inputCode.
     */
    public static void importRawCodeFile (File f) {
        Scanner sc = null;
        try {
            sc = new Scanner(f);
            //alerts the user that their file is being loaded for translation
            while (sc.hasNext()) {

                String copyLine = sc.nextLine();

                if (copyLine.trim().length() > 0) { 
                    //if line has text...
                    //add it to an ArrayList
                    raw.add(copyLine);

                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

    }
}
