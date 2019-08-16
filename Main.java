package com.company;
/*
    This project is created to simulate what a file system is like in a unix based system.
    It is using inodes that only point to direct data blocks.
    Assumptions: There is an inode array that holds the data for each file created and only
    points to the direct data block. There is no indirect, double indirect, or so on data blocks.
    Syntax:
    FM -> Format, this initializes the structures: disk map, and disk directory.
    NF -> New File. EX: NF <fileName.fileType> <numberOfBlocksAllocated>
    MF -> Add to File. EX: MF <fileName.fileType> <numberOfBlocksAdded>
    DF -> Delete File. EX: DF <fileName.fileType>
    DB -> Delete Blocks. EX: DB <fileName.fileType> <numberOfBlocksDeleted>
 */

public class Main {

    public static void main(String[] args) throws Exception {

    client john = new client();

    }
}
