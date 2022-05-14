import java.lang.*;
import java.io.*;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This program acts as an assembler for ARM V8.
 * 
 * @author Jordan Cartwright
 * @author Jordan McKosky
 * @version 12/12/16
 */

public class softwareProject
{

    //instance variables
    int errorCount = 0;
    //name of the input file
    String inputFileName = null;
    //name of the exported file
    String outFileName = null;
    String sampleFileName = null;
    //name of the log file
    String logFileName = null;
    //ArrayList of instructions that need to be translated to Binary
    protected ArrayList<String> inputCode = new ArrayList<String>();
    //ArrayList holding the binary code to that gets exported to a txt file
    protected ArrayList<String> binaryCode = new ArrayList<String>();
    //ArrayList of Error Messages for the log file
    protected ArrayList<String> errorMessages = new ArrayList<String>();
    //The Current opcode of the instruction that the assembler is translating
    protected String opcode = null;
    //The Current format of hte instruction that the assembler is translating
    protected String format = null;
    //The whole instruction that the assembler is currently trying to translate
    protected String currentInstruction = null;
    //This is a line of binary that represents the instruction that is being added 
    //to the output text file.
    protected String instructionToAddToText;
    //current rd
    protected String rd = null;
    //curent rd
    protected String rn = null;
    //current rt
    protected String rt = null;
    //current rm
    protected String rm = null;
    //current shamt
    protected String shamt = null;
    //current immediate
    protected String immediate = null;
    //current address used in D format instruction
    protected String address = null;
    //current branch address used in B format instructions
    protected String bAddress = null;
    //current cond branch address used in CB format instructions.
    protected String condBRAddr = null;
    //This int value is solely used to determine the branch address
    protected int currentLineNumber = 0;
    //This String will save the current command being translated
    protected String currentCommand = null;
    //This keeps the current instruction's conditional branch type
    //if necessary
    protected String condBranchType = null;
    //will help determine the desired branch address that compares
    //the labels
    protected String jumpToLabel = null;
    //This boolean will help determine if line starts with a comment,
    //meaning the whole line is a comment
    protected boolean fullCommentLine = true;
    //helps with error detection with branching
    protected boolean badBranch = false;
    //testing a loop to translate multiple files
    boolean run = true;
    //boolean variable that helps determine if a file was found/inputted correctly
    boolean fileNotFound = false;

    /**
     * Constructor for objects of class newSoftware
     * This is used to set defaults and manipulate variables from the Driver
     */
    public softwareProject()
    {
        //default File name to ensure the file name was being changed
        inputFileName = "Default";
        outFileName = "Name Was Not Changed";
        logFileName = "Log.txt";
        sampleFileName = "basic_input1.txt";
        //weather or not the Driver should continue to run
        run = true;
        //dictates what the driver will do if the file is not found
        //defaulted to false because we didnt check for a file yet
        fileNotFound = false;
    }

    /**
     * This method copies the File to the arrayList, inputCode.
     */
    public  void copyFile (File f) {
        Scanner sc = null;
        try {
            sc = new Scanner(f);
            //alerts the user that their file is being loaded for translation
            System.out.println("\t" + "Loading File...");
            int lineCount = 1;
            while (sc.hasNext()) {

                String copyLine = sc.nextLine();

                if (copyLine.trim().length() > 0) { 
                    //if line has text...
                    //add it to an ArrayList
                    inputCode.add(copyLine);
                    //output the imported file to the screen to update progress of the import
                    System.out.println("\t" +" "+ lineCount + "\t" + copyLine);

                    lineCount++;
                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            //if the file is not found, trigger the fileNotFound variable
            fileNotFound = true;
        }

    }

    /**
     * This method is used to write the binary translation into a text file.
     */
    public void writeFile(){
        try {
            PrintWriter out = new PrintWriter(outFileName ,"UTF-8");
            int lineCount = 1;
            //looks through ArrayList of all binary codes
            for(int i = 0; i < binaryCode.size(); i++ ){
                //command to write desired text to a file
                //we output the array list of binary values
                out.println(binaryCode.get(i));

                //this just neatly displays out the translated binary code in the program
//                System.out.println("\t" +" "+ lineCount + "\t" + binaryCode.get(i));
                lineCount++;
            }
            out.close();

        }
        catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    /**
     * This method is used to write the log file for error reports.
     */
    public void writeLogFile(){
        try {
            PrintWriter out = new PrintWriter(logFileName,"UTF-8");
            //format of the file
            out.println("Log Report (" + inputFileName +")"); //header
            out.println("\t" + errorCount + " Errors Detected");
            out.println();  //line break
            if(errorCount > 0) {
                //looks through ArrayList of all binary codes
                for(int i = 0; i < errorMessages.size(); i = i+2 ){
                    //command to write desired text to a file
                    out.println(errorMessages.get(i));
                    //we output the array list of error codes
                    out.println("\t" + errorMessages.get(i+1));
                    out.println();  //line brake
                }
            }
            else {
                out.println("Sucessfully Translated " + binaryCode.size() + " lines of code");
            }

            out.close();
        }
        catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    /**
     * This method is the first step in translating the instructions.  It reads in each line of the input file 
     * and then determines if the line is labeled, a comment, contians a comment, purely and instruction, basically disecting up each line from the file, and then
     * determines its command (if one is present in that line), which then calls
     * the checkCommand method to determine the correct opcode and leads to formating the instruction to binary.
     */
    public  void assembleFile (File f) {

        Scanner sc = null;
        //re-reading in the file for the second time and translating each instruction,
        //line by line.
        try {
            sc = new Scanner(f);
            //alerts the user that the file has begun the translation process
            System.out.println("\t" + "Translating File...");

            while (sc.hasNext()) {

                String line = sc.nextLine();
                //System.out.println(line);

                if (line.trim().length() > 0) { 
                    //if line has text...

                    //If the current instruction has a ":"

                    //This if statment disects up the line if a ":" is present anywhere...even if it is part of a comment

                    if(line.contains(":")){
                        String[] parts = line.split(": *");
                        //just a label---no code
                        if(parts.length==1){
                            String[] partsNew = line.split(":\t");
                            if(partsNew.length == 1){
                                //currentLineNumber++;
                                String[] partsNew2 = line.split(";");
                                if(partsNew2.length == 1){

                                    continue;
                                }
                                else{
                                    //If the ":" is part of a comment and the line has an actual instruction to undergo
                                    currentInstruction = partsNew2[0];
                                    String[] partsNew3 = line.split(" ");
                                    String command = partsNew3[0];

                                    currentCommand = command;
                                    fullCommentLine = commentFirstChecker(); //THIS IS ADDED and for loop****
                                    if(fullCommentLine == true){
                                        currentLineNumber++;
                                        continue;
                                    }
                                    checkCommand( command );

                                }
                            }
                            else{
                                String afterBreak = partsNew[1];

                                String[] parts2 = afterBreak.split(" ");
                                String command = parts2[0];

                                //command.substring(1);
                                currentCommand = command;
                                currentInstruction = afterBreak;
                                fullCommentLine = commentFirstChecker();
                                if(fullCommentLine == true){
                                    currentLineNumber++;
                                    continue;
                                }
                                checkCommand( command );
                            }

                        }

                        //Following else statment divides up the line if a label is present and not in a comment
                        else{
                            String afterBreak = parts[1];

                            String[] parts2 = afterBreak.split(" ");
                            String command = parts2[0];

                            //command.substring(1);
                            currentCommand = command;
                            currentInstruction = afterBreak;
                            fullCommentLine = commentFirstChecker(); 
                            if(fullCommentLine == true){
                                currentLineNumber++;
                                continue;
                            }
                            checkCommand( command );
                        }

                    }
                    //The Current instruction does not contain a label or ":"
                    else{
                        String command = null;
                        String[] parts = line.split(" ");
                        command = parts[0];
                        currentInstruction = line;

                        currentCommand = command;

                        //checks to make sure it is not a commented line
                        fullCommentLine = commentFirstChecker();
                        if(fullCommentLine == true){
                            currentLineNumber++;
                            continue;
                        }
                        checkCommand( command );

                    }
                    currentLineNumber++;
                }

            }

            sc.close();

        } catch (FileNotFoundException e) {
            fileNotFound = true;
        }

    }

    /**
     * This method checks the command and determines the correct opcode and format of the
     * instruction.  It then calls the method, createInstruction to translate the instruction
     * from Assembly Language to binary.
     */
    public  void checkCommand(String command){

        //need to get rid of tabs/whitspace, etc.
        //if they are first in currentCommand, this loop sets currentCommand and command to the new one without tabs present
        //at the start of the line.
        StringTokenizer st = new StringTokenizer(currentInstruction, " X,;/\t\t");
        rm = null;
        rn = null;
        rd = null;
        immediate = null;
        while (st.hasMoreTokens()) {
            String currentToken = st.nextToken();
            //System.out.println(currentToken);

            if( currentToken.contains("\t") == false){
                currentCommand = currentToken;
                command = currentCommand;
                break;
            }

        }

        //List of commands this method checks for and gives the corresponding opcode, format, then calls
        //the createInstruction method to fully translate the instruction to binary.

        if (command.equals("ADD")){
            opcode = "10001011000";
            format = "R";
            createInstruction( currentInstruction );
            
        }
        else if( command.equals( "ADDI")){
            opcode = "1001000100";
            format = "I";
            createInstruction( currentInstruction );

        }
        else if( command.equals("ADDIS")){
            opcode = "1011000100";
            format = "I";
            createInstruction( currentInstruction );

        }
        else if( command.equals("ADDS")){
            opcode = "10101011000";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("AND")){
            opcode = "10001010000";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("ANDI")){
            opcode = "1001001000";
            format = "I";
            createInstruction( currentInstruction );

        }
        else if( command.equals("ANDIS")){
            opcode = "1111001000";
            format = "I";
            createInstruction( currentInstruction );

        }
        else if( command.equals("ANDS")){
            opcode = "11101010000";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("B")){
            opcode = "000101";
            format = "B";
            createInstruction( currentInstruction );

        }

        //************ALL 16 conditionals****
        else if( command.contains("B.")==true){
            opcode = "01010100";
            format = "CB";

            createInstruction( currentInstruction );

        }
        else if( command.equals("BL")){
            opcode = "100101";
            format = "B";
            createInstruction( currentInstruction );

        }
        else if( command.equals("BR")){
            opcode = "11010110000";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("BLR")){
            opcode = "11010110001";
            format = "R";
            createInstruction( currentInstruction);
        }
        else if( command.equals("CBNZ")){
            opcode = "10110101";
            format = "CB";
            createInstruction( currentInstruction );

        }
        else if( command.equals("CBZ")){
            opcode = "10110100";
            format = "CB";
            createInstruction( currentInstruction );

        }
        else if( command.equals("EOR")){
            opcode = "11001010000";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("EORI")){
            opcode = "1101001000";
            format = "I";
            createInstruction( currentInstruction );

        }
        else if( command.equals("LDUR")){
            opcode = "11111000010";
            format = "D";
            createInstruction( currentInstruction );

        }
        else if( command.equals("LSL")){
            opcode = "1101001101";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("LSR")){
            opcode = "1101001101";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("ASR")){
            opcode = "1001001101";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("ORR")){
            opcode = "10101010000";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("ORRI")){
            opcode = "1011001000";
            format = "I";
            createInstruction( currentInstruction );

        }
        else if( command.equals("STUR")){
            opcode = "11111000000";
            format = "D";
            createInstruction( currentInstruction );

        }
        else if( command.equals("SUB")){
            opcode = "11001011000";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("SUBI")){
            opcode = "1101000100";
            format = "I";
            createInstruction( currentInstruction );

        }
        else if( command.equals("SUBIS")){
            opcode = "1111000100";
            format = "I";
            createInstruction( currentInstruction );

        }
        else if( command.equals("SUBS")){
            opcode = "11101011000";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("CMP")){
            //this is actually the SUBS operator
            //CMP X1,X2
            //SUBS XZR,X1,X2
            opcode = "11101011000";
            format = "R";
            createInstruction( currentInstruction );
            //checked

        }
        else if( command.equals("CMPI")){
            //this is actually the SUBIS operator
            //CMPI X1, #3
            //SUBI XZR, X1, #3
            opcode = "1111000100";
            format = "I";
            createInstruction( currentInstruction );
            //checked

        }
        else if( command.equals("MOV")){
            //this is actually the ORR
            //MOV Xd, Xm
            //ORR Xd, XZR, Xm
            opcode = "10101010000";
            format = "R";
            createInstruction( currentInstruction );
            //checked

        }
        else if( command.equals("NOP")){
            //do nothing
            // ADD XZR, XZR, XZR work

            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("BRK")){
            opcode = "11010100001";
            format = "IM";
            createInstruction(currentInstruction );
        }
        else if( command.equals("NEG")){
            //NEG X2, X3
            //Really means
            //SUB X2, XZR, X3

            opcode = "11001011000";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("RET")){
            //RET = BR LR 
            opcode = "11010110010";
            format = "R";
            createInstruction( currentInstruction );

        }
        //ADDITIONAL INSTRUCTIONS
        else if( command.equals("MOVK")){
            //MOVK  Xd, #imm
            //Two zeros are added at end of opcode to fill bit space 
            //Dan said these last two bits can be anything

            //foramt IM = IW (per Dan)
            opcode = "111100101";
            format = "IM";
            createInstruction( currentInstruction );

        }
        else if( command.equals("MOVZ")){
            //MOVZ Xd, #imm
            //Two zeros are added at end of opcode to fill bit space 
            //Dan said these last two bits can be anything

            //format IM = IW (per Dan)
            opcode = "110100101";
            format = "IM";
            createInstruction( currentInstruction );

        }
        else if( command.equals("MUL")){
            opcode = "10011011000";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("SDIV") || command.equals("UDIV")){
            opcode = "10011010110";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("SMULH")){
            opcode = "10011011010";
            format = "R";
            createInstruction( currentInstruction );

        }
        else if( command.equals("UMULH")){
            opcode = "10011011110";
            format = "R";
            createInstruction( currentInstruction );

        }

        //ERROR checker for an illegal/mispelled/invalid command
        else{
            instructionToAddToText = "ERROR: Invalid Instruction";
            binaryCode.add(instructionToAddToText);
            errorMessages.add("Error: Line " + (currentLineNumber + 1));
            errorMessages.add("'" + currentCommand + "'" + " is an invalid ARMv8 Instruction");
            errorCount++;
        }

    }

    /**
     * This method translates the Assembly Instruction to Binary.  It does so by checking the instructions format, then
     * dividing up the instruction to its correct order, in respects to its binary representation, and translates it to binary.
     */
    public void createInstruction(String currentInstruction){

        //R Format
        if( format.equals("R")){

            //Error checker
            if(currentInstruction.contains("#") && !currentInstruction.contains("LSL") && !currentInstruction.contains("LSR")
            && !currentInstruction.contains("ASR")){
                //if the line of code does contain a #
                //this is not an R format instruction
                instructionToAddToText = "ERROR: Invalid 'R' Format - Immediate Detected";
                errorMessages.add("Error: Line " + (currentLineNumber + 1));
                errorMessages.add("'" +currentCommand+ "'" + " is a R Format instruction that only takes register inputs, Immediate values are invalid");
                errorCount++;

            }
            else if(currentInstruction.contains(",") && !currentInstruction.contains("#")){
                StringTokenizer st = new StringTokenizer(currentInstruction, " X,;/\t");
                int i = 0;
                while (st.hasMoreTokens()) {
                    String currentToken = st.nextToken();
                    //System.out.println(currentToken);
                    if( i == 1) rd = currentToken;
                    if( i == 2) rn = currentToken;
                    if( i == 3){
                        rm = currentToken;
                        break;
                    }
                    i++;
                }

                //Checks for some special R format commands
                if(currentCommand.equals("CMP")){
                    rm = rn;
                    rn = rd;
                    rd = "ZR";

                }
                else if( currentCommand.equals("MOV")){
                    rm = rn;
                    rn = "XZR";}
                else if( currentCommand.equals("NEG")){
                    rm = rn;
                    rn = "ZR";
                }
                //fill up the shamt area
                switch(currentCommand){
                    case "MUL":shamt = "011111";break;
                    case "UDIV":shamt = "000010";break;
                    case "SDIV":shamt = "000011";break;
                    default:shamt = "000000";break;
                }

                //Translates registers SP, LR,ZR and FP to their decimal values
                checkForSpecReg();

                //converting decimal to binary with correct
                //number of leading zeros.
                int tempInt = 0;
                tempInt = Integer.parseInt(rd);
                rd = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rd);
                rd = String.format("%05d", tempInt);

                tempInt = Integer.parseInt(rn);
                rn = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rn);
                rn = String.format("%05d", tempInt);

                tempInt = Integer.parseInt(rm);
                rm = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rm);
                rm = String.format("%05d", tempInt);

                //instruction ready to add to txt file
             
                instructionToAddToText = opcode + rm + shamt + rn + rd;

            }
            //LSL,LSR and ASR commands or commands with shift amount
            else if(currentInstruction.contains("#")){
                StringTokenizer st = new StringTokenizer(currentInstruction, " X,;/\t#");
                int i = 0;
                while (st.hasMoreTokens()) {
                    String currentToken = st.nextToken();
                    //System.out.println(currentToken);
                    switch (i){
                        case 1: rd = currentToken;break;
                        case 2: rn = currentToken;break;
                        case 3: immediate = currentToken;break;
                        case 5: rm = immediate; immediate = currentToken;break;
                    }
                    i++;
                }

                checkForSpecReg();
                //converting decimal to binary with correct
                //number of leading zeros.
                int tempInt = 0;
                int immr=0;
                int imms=0;
                tempInt = Integer.parseInt(rd);
                rd = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rd);
                rd = String.format("%05d", tempInt);

                tempInt = Integer.parseInt(rn);
                rn = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rn);
                rn = String.format("%05d", tempInt);

                //Error testing for negative immediate
                tempInt = Integer.parseInt(immediate);
                if(tempInt < 64 && tempInt >=0){
                    if(currentCommand.equals("LSL")){
                        immr = (-tempInt) % 64;
                        imms = 63 - tempInt;
                        rm = Integer.toBinaryString(immr);
                        rm = rm.substring(rm.length() - 6);// get 0b-6b of binary code
                        shamt = Integer.toBinaryString(imms);
                        shamt = String.format("%06d",Integer.parseInt(shamt));
                    }
                    else if(currentCommand.equals("LSR") || currentCommand.equals("ASR")){
                        immr = tempInt;
                        imms = 63;      //64b version
                        rm = Integer.toBinaryString(immr);
                        rm = String.format("%06d",Integer.parseInt(rm));
                        shamt = Integer.toBinaryString(imms);
                        shamt = String.format("%06d",Integer.parseInt(shamt));
                    }
                    else {
                        if(currentInstruction.contains("LSR")){
                            opcode = opcode.substring(0,opcode.length()-3) + "010";
                        }
                        else if(currentInstruction.contains("ASR")){
                            opcode = opcode.substring(0,opcode.length()-3) + "100";
                        }

                        shamt = Integer.toBinaryString(tempInt);
                        shamt = String.format("%06d",Integer.parseInt(shamt));
                        tempInt = Integer.parseInt(rm);
                        rm = Integer.toBinaryString(tempInt);
                        tempInt = Integer.parseInt(rm);
                        rm = String.format("%05d", tempInt);
                    }
                    //instruction ready to add to txt file
                    instructionToAddToText = opcode + rm + shamt + rn + rd;
                }
                else{
                    instructionToAddToText = "ERROR: immediate is out of range";
                    errorMessages.add("Error: Line " + (currentLineNumber + 1));
                    errorMessages.add("immediate value should be in range '0-63' for " +"'"+ currentCommand +"'"+ " instructions");
                    errorCount++;
                }
            }
            //Checks for specific R Formats
            if( currentCommand.equals("NOP")){
/*
                rm = "ZR";
                rd = "ZR";
                rn = "ZR";
                checkForSpecReg();

                int tempInt = 0;
                tempInt = Integer.parseInt(rd);
                rd = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rd);
                rd = String.format("%05d", tempInt);

                tempInt = Integer.parseInt(rn);
                rn = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rn);
                rn = String.format("%05d", tempInt);

                tempInt = Integer.parseInt(rm);
                rm = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rm);
                rm = String.format("%05d", tempInt);

                shamt = "000000";
*/
                //instruction ready to add to txt file
//                instructionToAddToText = opcode + rm + shamt + rn + rd;
                instructionToAddToText = "11010101000000110010000000011111";
            }

            //If command is RET...translate to BR LR
            else if((currentCommand.equals("RET"))|| currentCommand.equals("BR") || currentCommand.equals("BLR")){

                //Rt from instruction is Rn accoring to the format on the
                //green reference sheet.
                StringTokenizer st = new StringTokenizer(currentInstruction, " X,;/\t");
                int i = 0;
                rn = "30";
                while (st.hasMoreTokens()) {
                    String currentToken = st.nextToken();
                    //System.out.println(currentToken);
                    if( i == 1) {
                        rn = currentToken;
                        break;
                    }
                    i++;
                }
                rm = "0";
                rd = "31";
                checkForSpecReg();

                int tempInt = 0;
                tempInt = Integer.parseInt(rd);
                rd = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rd);
                rd = String.format("%05d", tempInt);

                tempInt = Integer.parseInt(rn);
                rn = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rn);
                rn = String.format("%05d", tempInt);

                tempInt = Integer.parseInt(rm);
                rm = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rm);
                rm = String.format("%05d", tempInt);

                shamt = "000000";

                //instruction ready to add to txt file
                instructionToAddToText = opcode + rd + shamt + rn + rm;

            }

            if(instructionToAddToText == null){
                instructionToAddToText="ERROR: Undefined Instruction";
                errorMessages.add("Error: Line " + (currentLineNumber + 1));
                errorMessages.add("Undefined Instruction");
                errorCount++;
            }
            //adds Binary to output txt file
            binaryCode.add(instructionToAddToText);
        }
        //I Format
        else if( format.equals("I")){
            if(currentInstruction.contains("#") == false){
                //if the line of code does not contain a #
                //this is not an I format instruction
                instructionToAddToText = "ERROR: Invalid 'I' Format - not an immediate value";
                errorMessages.add("Error: Line " + (currentLineNumber + 1));
                errorMessages.add("'" +currentCommand+ "'" + " is an I Format instruction that requires an immediate with '#' to compile, registers are not valid");
                errorCount++;
            }
            else {
                //Breaks up and disects the line
                StringTokenizer st = new StringTokenizer(currentInstruction, " X,;/\t#");
                long i = 0;
                while (st.hasMoreTokens()) {
                    String currentToken = st.nextToken();
                    //System.out.println(currentToken);
                    if( i == 1) rd = currentToken;
                    if( i == 2) rn = currentToken;
                    if( i == 3) {
                        immediate = currentToken;
                        break;
                    }
                    i++;
                }

                //if command is CMPI
                if( currentCommand.equals("CMPI")){
                    immediate = rn;
                    rn = rd;
                    rd = "ZR";
                }

                checkForSpecReg();

                //converting decimal to binary with correct
                //number of leading zeros.
                int tempInt = 0;
                tempInt = Integer.parseInt(rd);
                rd = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rd);
                rd = String.format("%05d", tempInt);

                tempInt = Integer.parseInt(rn);
                rn = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rn);
                rn = String.format("%05d", tempInt);

                //Errors out if the value is to large to fit in 12 bits signed
                //aslo checks if it is negative
                try{
                    //only do this if the immediate is not negative
                    if(immediate.contains("-") == false) {
                        tempInt = Integer.parseInt(immediate);
                        if(tempInt>4095){
                            instructionToAddToText = "ERROR: Immediate value is to large";
                            errorMessages.add("Error: Line " + (currentLineNumber + 1));
                            errorMessages.add("Immediate " + "'" + tempInt + "'" +" exceeds 12-bit signed MAX_VALUE");
                            errorCount++;
                        }else{
                            immediate = Integer.toBinaryString(tempInt);
                            immediate = String.format("%012d", Long.parseLong(immediate));
                            instructionToAddToText = opcode + immediate + rn + rd;
                        }

                    }
                    else {
                        instructionToAddToText = "ERROR: Negative immediate value detected";
                        errorMessages.add("Error: Line " + (currentLineNumber + 1));
                        errorMessages.add("Negative Immediate value not allowed with " + currentCommand + " command");
                        errorCount++;
                    }
                }
                catch(NumberFormatException e){
                    instructionToAddToText = "ERROR: Immediate value is to large";
                    errorMessages.add("Error: Line " + (currentLineNumber + 1));
                    errorMessages.add("Immediate " + "'" + tempInt + "'" +" exceeds 12-bit signed MAX_VALUE");
                    errorCount++;
                }
            }
            //adds Binary to output txt file
            binaryCode.add(instructionToAddToText);
        }

        //D Format
        else if (format.equals("D")){
            if(currentInstruction.contains("[") == false || currentInstruction.contains("]") == false){
                instructionToAddToText = "ERROR: Invalid 'D' Format - Missing or incorrect Brackets";
                errorMessages.add("Error: Line " + (currentLineNumber + 1));
                errorMessages.add("Brackets are required for D format Instructions");
                errorCount++;
            }
            else if(currentInstruction.contains("#") == false){
                instructionToAddToText = "ERROR: Invalid 'D' Format - no immediate value detected";
                errorMessages.add("Error: Line " + (currentLineNumber + 1));
                errorMessages.add("D Format instructions require an immediate value");
                errorCount++;
            }
            else {
                //Disects instruction line and finds necessary parts
                StringTokenizer st = new StringTokenizer(currentInstruction, " X,;/\t#[]");
                int i = 0;
                while (st.hasMoreTokens()) {
                    String currentToken = st.nextToken();
                    //System.out.println(currentToken);
                    if( i == 1) rt = currentToken;
                    if( i == 2) rn = currentToken;
                    if( i == 3){
                        address = currentToken;
                        break;
                    }
                    i++;
                }
                checkForSpecReg();

                //converting decimal to binary with correct
                //number of leading zeros.
                int tempInt = 0;
                tempInt = Integer.parseInt(rt);
                rt = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rt);
                rt = String.format("%05d", tempInt);

                tempInt = Integer.parseInt(rn);
                rn = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rn);
                rn = String.format("%05d", tempInt);

                tempInt = Integer.parseInt(address);
                address = Integer.toBinaryString(tempInt);

                if(tempInt < 0){
                    address = address.substring(address.length()-9);

                }
                else{
                    tempInt = Integer.parseInt(address);

                    address = String.format("%09d", tempInt);
                }

                String tinyOpcode = "00";

                //instruction ready to add to txt file
                instructionToAddToText = opcode + address + tinyOpcode + rn + rt;
            }

            //adds Binary to output
            binaryCode.add(instructionToAddToText);

        }
        //B Format
        else if (format.equals("B")){
            StringTokenizer st = new StringTokenizer(currentInstruction, " X,;/\t#[]");
            int i = 0;
            while (st.hasMoreTokens()) {
                String currentToken = st.nextToken();
                //System.out.println(currentToken);
                if( i == 1){
                    jumpToLabel = currentToken;
                    break;

                }

                i++;
            }

            //The following line calls the method that finds the correct Branch Address
            badBranch = false;
            condBRAddr = retBranchAddr();
            if(badBranch == false){

                //instruction ready to add to txt file
                instructionToAddToText = opcode + condBRAddr;

                //adds Binary to output
                binaryCode.add(instructionToAddToText);
            }

        }

        //B Format
        else if (format.equals("CB")){

            //determines the type of conditional branching
            if(currentCommand.equals("CBZ") || currentCommand.equals("CBNZ")){
                //rt is the register it's comparing to

                StringTokenizer st = new StringTokenizer(currentInstruction, " X,;/\t#[]");
                int i = 0;
                while (st.hasMoreTokens()) {
                    String currentToken = st.nextToken();
                    //System.out.println(currentToken);
                    if( i == 1){
                        rt = currentToken;

                    }
                    if( i == 2){
                        jumpToLabel = currentToken;
                        break;
                    }

                    i++;
                }
                checkForSpecReg();

                //call to retBranchAddr
                badBranch = false;
                condBRAddr = retBranchAddr();
                if(badBranch == false){
                    int tempInt = 0;
                    tempInt = Integer.parseInt(rt);
                    rt = Integer.toBinaryString(tempInt);
                    tempInt = Integer.parseInt(rt);
                    rt = String.format("%05d", tempInt);

                    //instruction ready to add to txt file
                    instructionToAddToText = opcode + condBRAddr + rt;

                    //adds Binary to output
                    binaryCode.add(instructionToAddToText);
                }

            }
            //else if(currentCommand.equals("B.")){
            else{
                StringTokenizer st = new StringTokenizer(currentInstruction, " X,;/\t#[]");
                int i = 0;
                while (st.hasMoreTokens()) {
                    String currentToken = st.nextToken();
                    //System.out.println(currentToken);
                    if( i == 1){
                        jumpToLabel = currentToken;
                        break;
                    }

                    i++;
                }
                checkForSpecReg();

                if(currentCommand.equals("B.EQ")){
                    rt = "00000";
                }
                else if(currentCommand.equals("B.NE")){
                    rt = "00001";
                }
                else if(currentCommand.equals("B.HS") || currentCommand.equals("B.CS")){
                    rt = "00010";
                }
                else if(currentCommand.equals("B.LO") || currentCommand.equals("B.CC")){
                    rt = "00011";
                }
                else if(currentCommand.equals("B.MI")){
                    rt = "00100";
                }
                else if(currentCommand.equals("B.PL")){
                    rt = "00101";
                }
                else if(currentCommand.equals("B.VS")){
                    rt = "00110";
                }
                else if(currentCommand.equals("B.VC")){
                    rt = "00111";
                }
                else if(currentCommand.equals("B.HI")){
                    rt = "01000";
                }
                else if(currentCommand.equals("B.LS")){
                    rt = "01001";
                }
                else if(currentCommand.equals("B.GE")){
                    rt = "01010";
                }
                else if(currentCommand.equals("B.LT")){
                    rt = "01011";
                }
                else if(currentCommand.equals("B.GT")){
                    rt = "01100";
                }
                else if(currentCommand.equals("B.LE")){
                    rt = "01101";
                }
                else if(currentCommand.equals("B.AL")){
                    rt = "01110";
                }
                else if(currentCommand.equals("B.NV")){
                    rt = "01111";
                }
                //call to retBranchAddr

                badBranch = false;
                condBRAddr = retBranchAddr();

                if(badBranch == false){
                    //instruction ready to add to txt file
                    instructionToAddToText = opcode + condBRAddr + rt;
                    //adds Binary to output
                    binaryCode.add(instructionToAddToText);
                }
            }

        }
        //IM / IW format
        else if( format.equals("IM")) {
            String lslCommand = null;
            String hw=(currentCommand.equals("MOVZ") || currentCommand.equals("MOVK"))?"00":"";//movz without imm
            StringTokenizer st = new StringTokenizer(currentInstruction, " X,;/\t#");
            int i = 0;
            boolean err=false;
            while (st.hasMoreTokens()) {
                String currentToken = st.nextToken();

                if( i == 1) rd = currentToken;
                else if( i == 2) {
                    immediate = currentToken;
                }else if (i == 3){
                    if(currentToken.equals("LSL") && (currentCommand.equals("MOVZ") || currentCommand.equals("MOVK"))){
                        i++;
                        continue;
                    } else {
                        instructionToAddToText = "ERROR: Undefined Instruction";
                        binaryCode.add(instructionToAddToText);
                        errorMessages.add("Error: Line " + (currentLineNumber + 1));
                        errorMessages.add("Undefined Instruction");
                        errorCount++;
                        err=true;
                        break;
                    }
                }else if (i == 4){
                        switch (currentToken) {
                            case "16":hw = "01";break;
                            case "32":hw = "10";break;
                            case "48":hw = "11";break;
                            default:hw = "00";break;
                        };
                }
                i++;
            }

            if(err){
                err = false;
            }else {
                checkForSpecReg();

                if(currentCommand.equals("BRK")){
                    immediate = (rd==null)?"0":rd;
                    rd = "0";
                }

                int tempInt = 0;
                tempInt = Integer.parseInt(rd);
                //int negImmChecker = tempInt;
                rd = Integer.toBinaryString(tempInt);
                tempInt = Integer.parseInt(rd);
                rd = String.format("%05d", tempInt);

                //Error testing for negative immediate
                try {
                    tempInt = Integer.parseInt(immediate);
                    if(tempInt>65535){
                        instructionToAddToText = "ERROR: Immediate value is to large";
                        errorMessages.add("Error: Line " + (currentLineNumber + 1));
                        errorMessages.add("Immediate " + "'" + tempInt + "'" +" exceeds 16-bit signed MAX_VALUE");
                        errorCount++;
                    }else {
                        immediate = Integer.toBinaryString(tempInt);
                        immediate = String.format("%016d", Long.parseLong(immediate));
                        //adds Binary to output
                        instructionToAddToText = opcode + hw + immediate + rd;
                    }
                } catch (NumberFormatException e) {
                    instructionToAddToText = "ERROR: Negative immediate value detected";
                    errorMessages.add("Error: Line " + (currentLineNumber + 1));
                    errorMessages.add("Negative Immediate value not allowed with " + currentCommand + " command");
                    errorCount++;
                }finally {
                    binaryCode.add(instructionToAddToText);
                }
            }
        }
    }

    /**
     * This method translates the registers SP, FP, LR, and ZP to their binary value.
     */
    public void checkForSpecReg( ){

        //rd
        if(rd != null){
            if (rd .equals( "SP")){
                rd = "28";
            }
            else if( rd.equals( "FP")){
                rd = "29";
            }
            else if( rd.equals( "LR")){
                rd = "30";
            }
            else if( rd.equals( "ZR") || rd.equals("XZR")){
                rd = "31";
            }
        }

        //rn
        if( rn != null){
            if (rn.equals( "SP")){
                rn = "28";
            }
            else if( rn.equals( "FP")){
                rn = "29";
            }
            else if( rn.equals( "LR")){
                rn = "30";
            }
            else if( rn.equals("ZR") || rn.equals("XZR")){
                rn = "31";
            }
        }

        //rm
        if( rm != null){
            if (rm.equals( "SP")){
                rm = "28";
            }
            else if( rm.equals( "FP")){
                rm = "29";
            }
            else if( rm.equals( "LR")){
                rm = "30";
            }
            else if( rm.equals( "ZR") || rm.equals("XZR")){
                rm = "31";
            }
        }

        //rt
        if(rt != null){
            if (rt.equals( "SP")){
                rt = "28";
            }
            else if( rt.equals( "FP")){
                rt = "29";
            }
            else if( rt.equals( "LR")){
                rt = "30";
            }
            else if( rt.equals( "ZR") || rt.equals("XZR")){
                rt = "31";
            }
        }
    }

    /**
     * This method is called when a branch instruction is the current instruction and when a
     * branch address needs to be computed.  This method returns the correct branch address for the current instruction so it can
     * be used in forming the binary translation of the current instruction in the method, createInstruction.
     */
    public String retBranchAddr(){
        //This variable represents the line number where the current instruction will branch to
        int branchToInstNum = 0;
        //temp
        int tempBranchAddr = 0;
        //temp
        String stringtempBranchAddr = null;
        //temp, used to find label of branch
        String tempString = null;

        //loop finds branchToInstNum
        for(int j = 0; j <= inputCode.size(); j++){
            if( j != inputCode.size()){
                tempString = inputCode.get(j);
                if( tempString.contains(jumpToLabel + ":") == true){
                    //testing if the label is the only piece on a line
                    if(inputCode.get(j).equals(jumpToLabel + ":")== true){
                        branchToInstNum = j;
                        break;

                    }
                    else{
                        branchToInstNum = j;
                        break;
                    }
                }
            }

            //Error checker to make sure the label is present in the code and exists
            if(( j == inputCode.size()) && (tempString.contains(jumpToLabel + ":") == false)){
                instructionToAddToText = "ERROR: Missing jump to label";
                errorCount++;
                binaryCode.add(instructionToAddToText);
                errorMessages.add("Error: Line " + (currentLineNumber + 1));
                errorMessages.add("ERROR: there is no instruction labeled" + jumpToLabel + " in the code");
                badBranch = true;

            }
        }

        //caculation between current instruction number and branchToInstNum to 
        //determine the correct branch address

        if(currentLineNumber != branchToInstNum){
            tempBranchAddr = (branchToInstNum - currentLineNumber - 1);
        }

        else if( currentLineNumber == branchToInstNum ){
            instructionToAddToText = "ERROR: Infinite Loop detected";
            errorCount++;
            binaryCode.add(instructionToAddToText);
            errorMessages.add("Error: Line " + (currentLineNumber + 1));
            errorMessages.add(currentCommand +" instruction branching to itself: loop will never end");
            badBranch = true;

        }

        //Code below formats the binary string of the branch address, depending on the format 
        //of the branch instruction

        int tempInt = tempBranchAddr;
        stringtempBranchAddr = Integer.toBinaryString(tempInt);

        if (format.equals("B")){
            if(tempInt < 0){
                stringtempBranchAddr = stringtempBranchAddr.substring(stringtempBranchAddr.length()-26);

            }
            else{
                tempInt = Integer.parseInt(stringtempBranchAddr);

                stringtempBranchAddr = String.format("%026d", tempInt);

            }
        }
        else if (format.equals("CB")){
            if(tempInt < 0){
                stringtempBranchAddr = stringtempBranchAddr.substring(stringtempBranchAddr.length()-19);

            }
            else{
                tempInt = Integer.parseInt(stringtempBranchAddr);

                stringtempBranchAddr = String.format("%019d", tempInt);
            }
        }

        //returns branch address in its binary representation
        return stringtempBranchAddr;
    }

    /**
     * This methods aids in determing if a line is solely a comment, or if a certain parts of a line
     * is a comment.  This is called upon in the method assembleFile when the line is split up and disected.
     * This method helps determines certain cases where a label or comment is present in non-tradition, or tricky, spots.
     */
    public boolean commentFirstChecker(){
        //Ignores the line if a comment if the first thing in the line

        //need to trim line of leading whitespace and see if line starts with ; or /
        String testerString = currentInstruction.trim();

        if(testerString.substring(0,1).equals(";") || testerString.substring(0,1).equals("/")){
            return true;
        }
        
        //second checker
        else if((currentCommand.contains("/")) == true || (currentCommand.contains(";") == true)) {
            return true;
        }

        else{
            return false;
        }

    }

}
