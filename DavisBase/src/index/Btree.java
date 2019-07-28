/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import index.IndexCell.IndexCellPayload;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sourav
 */
public class Btree {

    public static final String PARENT_PATH = "";
    public static final int MAX_ELEMENTS_PER_NODE = 3;
    public static final int PAGE_HEADER_SIZE = 9;
    public static final int PAGE_SIZE = 512;
    private static final String INDEXINFO_FILE = PARENT_PATH + "tableInfo.properties";

    RandomAccessFile fileP = null;
    int root_index;
    HashMap<String, ArrayList<Integer>> tableInfo;

    public Btree(String columnName) {
//        Files.deleteIfExists(Paths.get(FILE));
//        fileP = new RandomAccessFile(FILE, "rw");

        /**
         * Empty Tree
         */
        tableInfo = new HashMap<>();
        root_index = -1;
        if (Files.exists(Paths.get(INDEXINFO_FILE),
                new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            try {
                tableInfo = loadHashMapFromFile();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Btree.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class A {

        ArrayList<IndexCell> mCells;
        ArrayList<Integer> mCellLocations;

        public A(ArrayList<IndexCell> cells, ArrayList<Integer> cellLocations) {
            mCells = cells;
            mCellLocations = cellLocations;
        }
    };

    /**
     * ReturnContainer: Contains the new page information to be propagated to
     * parent when a new node is created
     */
    class ReturnContainer {

        byte[] keyValue;
        /**
         * Set to -1 if no new page created *
         */
        int pageNo;

        /**
         * Constructor Function
         *
         * @param keyValue The new key value to be added due to split
         * @param pageNo The page number of the newly created page
         */
        public ReturnContainer(byte[] keyValue, int pageNo) {
            this.pageNo = pageNo;
            this.keyValue = keyValue;
        }

        public byte[] getKeyValue() {
            return keyValue;
        }

        public int getPageNo() {
            return pageNo;
        }
    }

    public void dumpHashMapToFile(HashMap<String, ArrayList<Integer>> tableInfo)
            throws IOException {

        if (tableInfo == null || tableInfo.isEmpty()) {
            return;
        }

        File file = new File(INDEXINFO_FILE);
        FileOutputStream f = new FileOutputStream(file);
        try (ObjectOutputStream s = new ObjectOutputStream(f)) {
            s.writeObject(tableInfo);
        }
    }

    public HashMap<String, ArrayList<Integer>> loadHashMapFromFile()
            throws IOException, ClassNotFoundException {
        HashMap<String, ArrayList<Integer>> ret;

        File file = new File(INDEXINFO_FILE);
        FileInputStream f = new FileInputStream(file);
        try (ObjectInputStream s = new ObjectInputStream(f)) {
            ret = (HashMap<String, ArrayList<Integer>>) s.readObject();
        }
        return ret;
    }

    private ArrayList<Integer> getNewCellLocs(ArrayList<IndexCell> cells) {
        ArrayList<Integer> ret = new ArrayList<>();
        int offset = PAGE_SIZE/*-1*/;

        for (IndexCell cell : cells) {
            offset -= cell.getCellSize();
            ret.add(offset);
        }
        return ret;
    }

    private ArrayList<A> divideCells(ArrayList<IndexCell> cells) {
        int numCells = cells.size();
        ArrayList<A> ret = new ArrayList<>();
        ArrayList<IndexCell> cl = new ArrayList<>(), cl2 = new ArrayList<>();

        //System.out.println(numCells / 2);
        for (int i = 0; i < (numCells / 2); i++) {
            cl.add(cells.get(i));
        }
        ret.add(new A(cl, getNewCellLocs(cl)));

        for (int i = (numCells / 2); i < numCells; i++) {
            cl2.add(cells.get(i));
        }
        ret.add(new A(cl2, getNewCellLocs(cl2)));

        return ret;
    }

    private void setNewParentOfChildren(int leftChildNo, int rightChildNo,
            int parentPageNo) throws IOException {
        byte[] pageBytes = new byte[PAGE_SIZE], pageBytes2 = new byte[PAGE_SIZE];

        /**
         * Left Child *
         */
        fileP.seek(leftChildNo * PAGE_SIZE);
        fileP.read(pageBytes);

        IndexPage leftChild = new IndexPage(pageBytes);
        leftChild.unmarshalPage();
        leftChild.setParentPageNo(parentPageNo);

        fileP.seek(leftChildNo * PAGE_SIZE);
        fileP.write(leftChild.marshalPage());

        /**
         * Right Child *
         */
        fileP.seek(rightChildNo * PAGE_SIZE);
        fileP.read(pageBytes2);

        IndexPage rightChild = new IndexPage(pageBytes2);
        rightChild.unmarshalPage();
        rightChild.setParentPageNo(parentPageNo);

        fileP.seek(rightChildNo * PAGE_SIZE);
        fileP.write(rightChild.marshalPage());
    }

    private ReturnContainer splitPageOnFullAndAddCell(IndexPage page, IndexCell newCell, int leftChildNo)
            throws IOException {
        int newPageNo = (int) (fileP.length() / PAGE_SIZE);
        byte[] newParentKey;
        int oldRightPageNo = page.getRightNodePageNo();
        ArrayList<A> parts = divideCells(page.getAllCells());

        newParentKey = parts.get(1).mCells.get(0).getCellBody().getKey();

        /**
         * Modify the current page and make it the left child page *
         */
        page.setCellArray(parts.get(0).mCells);
        page.setCellLocationArray(parts.get(0).mCellLocations);

        /**
         * Add the new cell to the Right Array formed after split *
         */
        A right = parts.get(1);
        int lastCellLoc = right.mCellLocations.get(
                right.mCellLocations.size() - 1
        );
        right.mCellLocations.add(lastCellLoc - newCell.getCellSize());
        right.mCells.add(newCell);

        /**
         * In case of an internal node, no need to keep the leftmost cell *
         */
        if (page.isLeaf()) {
            page.setRightNodePageNo(newPageNo);
        } else {
            /**
             * After the split, the left sibling page's right child should be
             * the left child of the first cell in the 2nd half.
             */
            page.setRightNodePageNo(
                    parts.get(1).mCells.get(0).getLeftChildPageNo()
            );
        }
        parts.get(1).mCells.remove(0);
        /**
         * Create the new right page *
         */
        IndexPage rightNode = new IndexPage(
                page.isLeaf(),
                parts.get(1).mCells,
                parts.get(1).mCellLocations,
                page.getParentPageNo(),
                oldRightPageNo
        );

        /**
         * Dump the newly created right page at the end file *
         */
        fileP.seek(fileP.length());
        fileP.write(rightNode.marshalPage());

        fileP.seek(leftChildNo * PAGE_SIZE);
        fileP.write(page.marshalPage());

        return new ReturnContainer(newParentKey, newPageNo);
    }

    private ReturnContainer doInsert(byte[] indexKey, int rowID, int currNode)
            throws IOException {

        IndexPage page;
        byte[] pageBytes = new byte[PAGE_SIZE];

        fileP.seek(currNode * PAGE_SIZE);
        fileP.read(pageBytes);

        page = new IndexPage(pageBytes);
        page.unmarshalPage();
        ArrayList<IndexCell> cells = page.getAllCells();
        
        byte indexType = cells.get(0).getCellBody().getType();

        if (!page.isLeaf()) {
            ReturnContainer ret = null;
            boolean isLargest = true;

            for (IndexCell c : cells) {
                if (IndexCell.compareByteArrays(indexKey, c.getCellBody().getKey(),
                        c.getCellBody().getType()) < 0) {
                    ret = doInsert(indexKey, rowID, c.getLeftChildPageNo());
                    isLargest = false;
                } else if (IndexCell.compareByteArrays(indexKey, c.getCellBody().getKey(),
                        c.getCellBody().getType()) == 0) {
                    c.getCellBody().addRowId(rowID);
                    isLargest = false;
                    break;
                }
            }

            if (isLargest) {
                ret = doInsert(indexKey, rowID, page.getRightNodePageNo());
            }

            if (ret == null) {
                return ret;
            }
            /**
             * The new cell to be added must have the left as the original
             * page's right child and the new right child of this page is the
             * new node that has been created
             */
            int leftChildNodeNo = page.getRightNodePageNo();
            IndexCell newCell = new IndexCell(leftChildNodeNo, null, ret.keyValue);
            page.setRightNodePageNo(ret.pageNo);
            if (!page.isNodeFullDummy()) {
                /**
                 * Just Add newCell to Page and dump page on file *
                 */
                setNewParentOfChildren(leftChildNodeNo, ret.pageNo, currNode);
                page.addNewCell(newCell);
            } else {
                ReturnContainer ret2 = splitPageOnFullAndAddCell(page, newCell, currNode);
                setNewParentOfChildren(leftChildNodeNo, ret.getPageNo(), ret2.getPageNo());
                return ret2;
            }
        } else {
            /**
             * Actual Insertion of record *
             */
            boolean isLargest = true;
            IndexCell newCell = null;
            for (int i = 0; i < cells.size(); i++) {
                if (IndexCell.compareByteArrays(indexKey, cells.get(i).getCellBody().getKey(),
                        cells.get(i).getCellBody().getType()) < 0) {
                    isLargest = false;
                    cells.
                } else if (IndexCell.compareByteArrays(indexKey, c.getCellBody().getKey(),
                        c.getCellBody().getType()) == 0) {
                    cells.get(i).getCellBody().addRowId(rowID);
                    return null;
                }
            }
            
            IndexCell newCell = new IndexCell(-1,
                    new IndexCellPayload(
                            (byte) 1,
                            indexKey,
                            indexType,
                            new ArrayList<>(Arrays.asList(rowID))));

            cells.add(newCell);

            if (page.isNodeFullDummy()) {
                ReturnContainer ret = splitPageOnFullAndAddCell(page, cell, currNode);
                //Return a new Cell Object with rowid as the rowid of 
                //the middle element, and left child page no
                return ret;
            }
        }
        fileP.seek(currNode * PAGE_SIZE);
        fileP.write(page.marshalPage());
        return null;
    }
}
