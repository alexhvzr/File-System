package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


public class directory {
    static final int MAX_INPUT = 20; // He said at max 20 inputs
    int n;
    inode[] inodeArr = new inode[MAX_INPUT];
    bitmap bitm = new bitmap(); // initialization of bitmap

    class dNode {
        int inodeNumber = 0;
        int blockNumber = 0;
        String command, fileName, fileType;

        // to string for dnodes.
        public String toString() {
            return "inode # = " + inodeNumber + " command = " + command + " file name = "
                    + fileName + " File type = " + fileType + " blocks used = " + blockNumber;
        }
    }

    dNode dir[] = new dNode[MAX_INPUT];

    public void initialize() throws FileNotFoundException {
        File input = new File("C:\\Users\\Public\\Documents\\src\\com\\company\\test1.txt"); // reads files ****** UPDATE WITH CORRECT PATH FOR YOU
        Scanner sc = new Scanner(input);
        int index = 0;

        String CreateFiles = sc.nextLine();
        if (!CreateFiles.equals("FM"))
            throw new FileNotFoundException("You didn't create file system.");

        while (sc.hasNextLine()) {
            dNode d = newNode(sc.nextLine());
            dir[index] = d; // filling up the directory array with dnodes
            index++;
            System.out.println("Currently executing: " + d.command);
            System.out.println("Current inode Array:");
            printInodes();
            System.out.println();
        }
        //printData(); // prints the directory
        System.out.println();
        System.out.println("Final inode Array:");
        printInodes(); // prints the final dnode status
        bitm.printBitmap(); // prints the final bitmap status
    }

    dNode newNode(String input) {
        dNode d = new dNode();
        String[] parse = input.split(" "); // parses input by the space to get each files info

        d.command = parse[0]; // command
        String[] parse1 = parse[1].split("\\.");
        d.fileName = parse1[0]; // file name
        d.fileType = parse1[1]; // file type

        if (!d.command.equals("DF")) // if not deleting a file, get block count
            d.blockNumber = Integer.parseInt(parse[2]); // block count to be added or removed
        d.inodeNumber = n; // index in directory to inode array

        if (d.command.equals("NF")) { // new file
            inodeArr[n] = new inode(d.blockNumber, bitm); // create an inode and update bitmap
            n++; // increase the number of inodes
            bitm.openAddress += 10; // each node gets 10 block no matter what
        } else if (d.command.equals("MF")) { // modify file
            inodeArr[findFile(d.fileName)].addBlocks(d.blockNumber,bitm); // adds blocks to the inode and changes the bitmap
        } else if (d.command.equals("DB")) { // delete blocks
            if(!inodeArr[findFile(d.fileName)].removeBlocks(d.blockNumber,bitm)){ // removes the block and updates the bitmap
                inodeArr[findFile(d.fileName)].deleteInode(bitm); // if there is a false return value, you deleted the whole files contents
                inodeArr[findFile(d.fileName)] = null; // delete the inode
            }
        } else { // command = "DF"
            inodeArr[findFile(d.fileName)].deleteInode(bitm); // delete bitmap allocation
            inodeArr[findFile(d.fileName)] = null; // delete node
        }

        return d; // return the directory node for the directory array
    }

    // Prints the directory info
    void printData() {
        for (int i = 0; i < MAX_INPUT && dir[i] != null; i++) {
            System.out.println(dir[i]);
        }
    }

    // finds the file based off the name and returns the inode number to modify the inode
    int findFile(String fn) {
        for (int i = 0; i < MAX_INPUT && dir[i] != null; i++) {
            if (dir[i].fileName.equals(fn)) {
                return dir[i].inodeNumber;
            }
        }
        return -1;
    }

    // Method to print all of the inodes in the array
    void printInodes(){
        for (int i = 0; i < MAX_INPUT; i++) {
            if(!(inodeArr[i] == null))
                System.out.println("inode[" + i + "]= " + inodeArr[i]);
        }
    }
}

// Inode class
class inode {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");     // Used when want to print formatted time.

    String uid = "CSS430"; // given uid
    String gid = uid; // given gid
    LocalDateTime aTime, cTime, mTime; // Access, Create, and Modified time.
    int fileSize, blockCount, bitMapAddress;

    inode(int bc, bitmap bitm) { // initialization of inode
        blockCount = bc;
        if(blockCount > 10){ // Max 10 blocks per inode
            blockCount = 10;
            System.out.println("MAX FILE SIZE.");
        }
        fileSize = (bc * 512);
        aTime = LocalDateTime.now();
        cTime = aTime;
        mTime = aTime;
        bitMapAddress = bitm.openAddress;
        addBits(blockCount,bitm); // add the bits to the bitmap
    }

    boolean addBlocks(int bc, bitmap bm){ // adds blocks to the inode and updates bitmap
        blockCount += bc;
        if(blockCount > 10){ // Max 10 blocks per inode
            blockCount = 10;
            System.out.println("MAX FILE SIZE.");
        }
        fileSize += (blockCount * 512);
        addBits(blockCount, bm); // here's where it updates the bitmap
        aTime = LocalDateTime.now(); // update access and modified time
        mTime = aTime;
        return true;
    }
    boolean removeBlocks(int bc,bitmap bm){ // removes blocks and updates bitmap
        blockCount -= bc;
        if(blockCount <= 0){ // you can't have an empty file
            blockCount = 0;
            System.out.println("DELETED FILE.");
            return false;
        }
        fileSize -= (bc * 512);
        removeBits(blockCount,bm); // updates bitmap
        aTime = LocalDateTime.now();
        mTime = aTime;
        return true;
    }

    // deletes the inode from the bitmap array.
    void deleteInode(bitmap bm){
        for(int i = bitMapAddress; i < bitMapAddress+10; i++){
            bm.bitMapArray[i] = 0;
        }
    }

    // adds bits to the bitmap array
    void addBits(int blockNum,bitmap bm){
        for(int i = bitMapAddress; i < bitMapAddress+blockNum; i++){
            bm.bitMapArray[i] = 1;
        }

    }

    // removes bits from the bitmap array
    void removeBits(int blockNum,bitmap bm){
        for(int i = bitMapAddress; i < bitMapAddress+10; i++){
            if(i >= bitMapAddress+blockNum)
                bm.bitMapArray[i] = 0;
        }

    }

    // To string for inodes
    public String toString() {
        return "Block count= " + blockCount + " User ID= " + uid + " Group ID = "
                + gid + " bitmap address= " + bitMapAddress + " File size= " + fileSize +
                " Date Accessed= " + dtf.format(aTime) + " Date Modified= " + dtf.format(mTime) + " Date Created= " + dtf.format(cTime);
    }

}



