/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import btree.BPlusOne;
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
    HashMap<String, Integer> tableInfo;

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

        IndexCell parentCell;

        /**
         * Constructor Function
         *
         * @param keyValue The new key value to be added due to split
         * @param pageNo The page number of the newly created page
         */
        public ReturnContainer(byte[] keyValue, int pageNo, IndexCell cell) {
            this.pageNo = pageNo;
            this.keyValue = keyValue;
            parentCell = cell;
        }

        public byte[] getKeyValue() {
            return keyValue;
        }

        public int getPageNo() {
            return pageNo;
        }

        public IndexCell getNewCell() {
            return parentCell;
        }
    }

    public void dumpHashMapToFile(HashMap<String, Integer> tableInfo)
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

    public HashMap<String, Integer> loadHashMapFromFile()
            throws IOException, ClassNotFoundException {
        HashMap<String, Integer> ret;

        File file = new File(INDEXINFO_FILE);
        FileInputStream f = new FileInputStream(file);
        try (ObjectInputStream s = new ObjectInputStream(f)) {
            ret = (HashMap<String, Integer>) s.readObject();
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

    private ReturnContainer splitPageOnFullAndAddCell(IndexPage page, int leftChildNo)
            throws IOException {
        int newPageNo = (int) (fileP.length() / PAGE_SIZE);
        byte[] newParentKey;
        int oldRightPageNo = page.getRightNodePageNo();
        ArrayList<A> parts = divideCells(page.getAllCells());
        IndexCell newParentCell;

        newParentKey = parts.get(1).mCells.get(0).getCellBody().getKey();

        /**
         * Modify the current page and make it the left child page *
         */
        page.setCellArray(parts.get(0).mCells);
        page.setCellLocationArray(parts.get(0).mCellLocations);

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
        newParentCell = parts.get(1).mCells.get(0);
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

        return new ReturnContainer(newParentKey, newPageNo, newParentCell);
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
            int count;

            for (count = 0; count < cells.size(); count++) {
                IndexCell c = cells.get(count);
                if (IndexCell.compareByteArrays(indexKey, c.getCellBody().getKey(),
                        c.getCellBody().getType()) < 0) {
                    ret = doInsert(indexKey, rowID, c.getLeftChildPageNo());
                    break;
                } else if (IndexCell.compareByteArrays(indexKey, c.getCellBody().getKey(),
                        c.getCellBody().getType()) == 0) {
                    c.getCellBody().addRowId(rowID);
                    break;
                }
            }

            if (count == cells.size()) {
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
            IndexCellPayload payload = new IndexCellPayload(
                    (byte) 1,
                    ret.getKeyValue(),
                    indexType,
                    ret.getNewCell().getCellBody().getRowIDs()
            );
            IndexCell newCell = new IndexCell(leftChildNodeNo, payload);

            if (count == cells.size()) {
                /**
                 * New cell to be added to the rightmost entry in the page *
                 */
                page.setRightNodePageNo(ret.pageNo);
                page.addNewCell(newCell);
            } else {
                /**
                 * New cell to be added as middle element of the array *
                 */
                cells.get(count).setLeftChildPageNo(ret.getPageNo());
                cells.add(count, newCell);
                page.setCellLocationArray(getLocationArrayfromCellArray(cells));
            }
            if (!page.isNodeFullDummy()) {
                /**
                 * Just Add newCell to Page and dump page on file *
                 */
                setNewParentOfChildren(leftChildNodeNo, ret.getPageNo(), currNode);
            } else {
                ReturnContainer ret2 = splitPageOnFullAndAddCell(page, currNode);
                setNewParentOfChildren(leftChildNodeNo, ret.getPageNo(), ret2.getPageNo());
                return ret2;
            }
        } else {
            /**
             * Actual Insertion of record *
             */
            IndexCell newCell = new IndexCell(-1,
                    new IndexCellPayload(
                            (byte) 1,
                            indexKey,
                            indexType,
                            new ArrayList<>(Arrays.asList(rowID))));
            int count;
            for (count = 0; count < cells.size(); count++) {
                if (IndexCell.compareByteArrays(indexKey,
                        cells.get(count).getCellBody().getKey(),
                        cells.get(count).getCellBody().getType()) < 0) {
                    cells.add(count, newCell);
                    break;
                } else if (IndexCell.compareByteArrays(indexKey,
                        cells.get(count).getCellBody().getKey(),
                        cells.get(count).getCellBody().getType()) == 0) {
                    cells.get(count).getCellBody().addRowId(rowID);
                    return null;
                }
            }

            if (count == cells.size()) {
                cells.add(newCell);
            }

            page.setCellLocationArray(getLocationArrayfromCellArray(cells));

            if (page.isNodeFullDummy()) {
                ReturnContainer ret = splitPageOnFullAndAddCell(page, currNode);
                //Return a new Cell Object with rowid as the rowid of 
                //the middle element, and left child page no
                return ret;
            }
        }
        fileP.seek(currNode * PAGE_SIZE);
        fileP.write(page.marshalPage());
        return null;
    }

    public ArrayList<Integer> getLocationArrayfromCellArray(
            ArrayList<IndexCell> cells) {
        int offset = BPlusOne.PAGE_SIZE;
        ArrayList<Integer> ret = new ArrayList<>();

        for (IndexCell cell : cells) {
            offset -= cell.getCellSize();
            ret.add(offset);
        }
        return ret;
    }

    /**
     * insert(): Insert a rowID to a particular index
     *
     * @param tablename The name of the index table. incorporates table and
     * column name
     * @param type The data type of data stored in the column
     * @param indexKey The Index Key
     * @param rowID The B-Plus Tree rowID to be inserted at the 'indexKey'
     */
    public void insert(String tablename, byte type, byte[] indexKey, int rowID) {
        root_index = -1;
        if (!tableInfo.containsKey(tablename)) {
            tableInfo.put(tablename, root_index);
        } else {
            root_index = tableInfo.get(tablename);
        }
        try {

            fileP = new RandomAccessFile(PARENT_PATH + tablename, "rw");
            if (root_index < 0) {
                /**
                 * Empty Tree *
                 */
                IndexCellPayload payload = new IndexCellPayload(
                        (byte) 0,
                        indexKey,
                        type,
                        new ArrayList<>(Arrays.asList(rowID)));
                IndexCell cell = new IndexCell(-1, payload);
                IndexPage page = new IndexPage(
                        true,
                        new ArrayList<>(Arrays.asList(cell)),
                        new ArrayList<>(PAGE_SIZE - cell.getCellSize()),
                        -1,
                        -1
                );
                byte[] pageBytes = page.marshalPage();
                root_index = 0;
                fileP.write(pageBytes);
            } else {
                ReturnContainer ret = doInsert(indexKey, rowID, root_index);
                if (ret != null) {
                    IndexCellPayload payload = new IndexCellPayload(
                            (byte) 1,
                            ret.getKeyValue(),
                            type,
                            ret.getNewCell().getCellBody().getRowIDs()
                    );
                    IndexCell cell = new IndexCell(root_index, payload);
                    IndexPage page = new IndexPage(
                            false,
                            new ArrayList<>(Arrays.asList(cell)),
                            new ArrayList<>(Arrays.asList(PAGE_SIZE - cell.getCellSize())),
                            -1,
                            ret.getPageNo()
                    );

                    root_index = (int) (fileP.length() / PAGE_SIZE);

                    setNewParentOfChildren(root_index, ret.getPageNo(), root_index);

                    fileP.seek(fileP.length());
                    fileP.write(page.marshalPage());
                }
            }
            tableInfo.put(tablename, root_index);
            dumpHashMapToFile(tableInfo);
        } catch (IOException ex) {
            Logger.getLogger(Btree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
