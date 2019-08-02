package btree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class BPlusOne {

    public static final String PARENT_PATH = "";
    public static final int MAX_ELEMENTS_PER_NODE = 3;
    public static final int PAGE_HEADER_SIZE = 9;
    public static final int PAGE_SIZE = 512;
    private static final String TABLEINFO_FILE = PARENT_PATH + "tableInfo.properties";

    RandomAccessFile fileP = null;
    int root_index;
    HashMap<String, ArrayList<Integer>> tableInfo;
    int dummyRowId;

    class A {

        ArrayList<Cell> mCells;
        ArrayList<Integer> mCellLocations;

        public A(ArrayList<Cell> cells, ArrayList<Integer> cellLocations) {
            mCells = cells;
            mCellLocations = cellLocations;
        }
    };

    /**
     * ReturnContainer: Contains the new page information to be propagated to
     * parent when a new node is created
     */
    class ReturnContainer {

        int keyValue;
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
        public ReturnContainer(int keyValue, int pageNo) {
            this.pageNo = pageNo;
            this.keyValue = keyValue;
        }

        public int getKeyValue() {
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

        File file = new File(TABLEINFO_FILE);
        FileOutputStream f = new FileOutputStream(file);
        try (ObjectOutputStream s = new ObjectOutputStream(f)) {
            s.writeObject(tableInfo);
        }
    }

    public HashMap<String, ArrayList<Integer>> loadHashMapFromFile()
            throws IOException, ClassNotFoundException {
        HashMap<String, ArrayList<Integer>> ret;

        File file = new File(TABLEINFO_FILE);
        FileInputStream f = new FileInputStream(file);
        try (ObjectInputStream s = new ObjectInputStream(f)) {
            ret = (HashMap<String, ArrayList<Integer>>) s.readObject();
        }
        return ret;
    }

    public BPlusOne() {
//        Files.deleteIfExists(Paths.get(FILE));
//        fileP = new RandomAccessFile(FILE, "rw");

        /**
         * Empty Tree
         */
        tableInfo = new HashMap<>();
        dummyRowId = 0;
        root_index = -1;
        if (Files.exists(Paths.get(TABLEINFO_FILE),
                new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            try {
                tableInfo = loadHashMapFromFile();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(BPlusOne.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void closeFile() {
        try {
            fileP.close();
        } catch (IOException ex) {
            Logger.getLogger(BPlusOne.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<Integer> getNewCellLocs(ArrayList<Cell> cells) {
        ArrayList<Integer> ret = new ArrayList<>();
        int offset = PAGE_SIZE/*-1*/;

        for (Cell cell : cells) {
            offset -= cell.getCellSize();
            ret.add(offset);
        }
        return ret;
    }

    private ArrayList<A> divideCells(ArrayList<Cell> cells) {
        int numCells = cells.size();
        ArrayList<A> ret = new ArrayList<>();
        ArrayList<Cell> cl = new ArrayList<>(), cl2 = new ArrayList<>();

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

    private int getNextRowId() {
        //TODO
        return dummyRowId++;
    }

    private void setNewParentOfChildren(int leftChildNo, int rightChildNo,
            int parentPageNo) throws IOException {
        byte[] pageBytes = new byte[PAGE_SIZE], pageBytes2 = new byte[PAGE_SIZE];

        /**
         * Left Child *
         */
        fileP.seek(leftChildNo * PAGE_SIZE);
        fileP.read(pageBytes);

        Page leftChild = new Page(pageBytes);
        leftChild.unmarshalPage();
        leftChild.setParentPageNo(parentPageNo);

        fileP.seek(leftChildNo * PAGE_SIZE);
        fileP.write(leftChild.marshalPage());

        /**
         * Right Child *
         */
        fileP.seek(rightChildNo * PAGE_SIZE);
        fileP.read(pageBytes2);

        Page rightChild = new Page(pageBytes2);
        rightChild.unmarshalPage();
        rightChild.setParentPageNo(parentPageNo);

        fileP.seek(rightChildNo * PAGE_SIZE);
        fileP.write(rightChild.marshalPage());
    }

    public void insert(String tablename, byte[] rowData) {

        dummyRowId = 0;
        root_index = -1;
        if (!tableInfo.containsKey(tablename)) {
            ArrayList<Integer> tableInitInfo = new ArrayList<>();

            tableInitInfo.add(dummyRowId);
            tableInitInfo.add(root_index);
            tableInfo.put(tablename, tableInitInfo);
        } else {
            dummyRowId = tableInfo.get(tablename).get(0);
            root_index = tableInfo.get(tablename).get(1);
        }
        Cell cell = new Cell(-1, rowData, getNextRowId());

        try {
            fileP = new RandomAccessFile(PARENT_PATH + tablename, "rw");

            if (root_index < 0) {
                /**
                 * Empty Tree *
                 */
                int offsetInPage = PAGE_SIZE - cell.getCellSize() /*- 1*/;
                cell.setOffsetInPage(offsetInPage);
                Page page = new Page(true, new ArrayList<>(Arrays.asList(cell)),
                        new ArrayList<>(Arrays.asList(offsetInPage)), -1, -1);
                byte[] pageBytes = page.marshalPage();
                root_index = 0;
                fileP.write(pageBytes);
            } else {
                ReturnContainer ret = doInsert(cell, root_index);
                if (ret != null) {

                    Cell newCell = new Cell(root_index, null, ret.keyValue);
                    int offsetInPage = PAGE_SIZE - Cell.CELL_HEADER_SIZE /*-1*/;
                    newCell.setOffsetInPage(offsetInPage);
                    Page page = new Page(
                            false,
                            new ArrayList<>(Arrays.asList(newCell)),
                            new ArrayList<>(Arrays.asList(offsetInPage)),
                            -1,
                            ret.getPageNo()
                    );
                    setNewParentOfChildren(
                            root_index,
                            ret.getPageNo(),
                            (int) fileP.length() / PAGE_SIZE
                    );

                    page.setRightNodePageNo(ret.getPageNo());
                    root_index = (int) (fileP.length() / PAGE_SIZE);
                    fileP.seek(fileP.length());
                    fileP.write(page.marshalPage());
                }
            }
            ArrayList<Integer> tableInitInfo = new ArrayList<>();

            tableInitInfo.add(dummyRowId);
            tableInitInfo.add(root_index);
            tableInfo.put(tablename, tableInitInfo);
            dumpHashMapToFile(tableInfo);
            fileP.close();

        } catch (IOException /*| ClassNotFoundException*/ ex) {
            Logger.getLogger(BPlusOne.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ReturnContainer splitPageOnFullAndAddCell(Page page, Cell newCell, int leftChildNo)
            throws IOException {
        int newPageNo = (int) (fileP.length() / PAGE_SIZE);
        int newParentKey;
        int oldRightPageNo = page.getRightNodePageNo();
        ArrayList<A> parts = divideCells(page.getAllCells());

        newParentKey = parts.get(1).mCells.get(0).getRowId();

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
            parts.get(1).mCells.remove(0);
        }
        /**
         * Create the new right page *
         */
        Page rightNode = new Page(
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

    private ReturnContainer doInsert(Cell cell, int currNode)
            throws IOException {

        Page page;
        byte[] pageBytes = new byte[PAGE_SIZE];

        fileP.seek(currNode * PAGE_SIZE);
        fileP.read(pageBytes);

        page = new Page(pageBytes);
        page.unmarshalPage();

        if (!page.isLeaf()) {
            ReturnContainer ret = doInsert(cell, page.getRightNodePageNo());

            if (ret == null) {
                return ret;
            }
            /**
             * The new cell to be added must have the left as the original
             * page's right child and the new right child of this page is the
             * new node that has been created
             */
            int leftChildNodeNo = page.getRightNodePageNo();
            Cell newCell = new Cell(leftChildNodeNo, null, ret.keyValue);
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
            if (!page.isNodeFullDummy()) {
                page.addNewCell(cell);
            } else {
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
    
    /**
     * 
     * @param tablename
     * @return Maximum Row ID value currently in the tree
     */
    public int getMaxRowID(String tablename) {
        int ret = 1;
        try {
            if (!Files.exists(Paths.get(PARENT_PATH + tablename),
                    new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                return ret;
            }
            HashMap<String, ArrayList<Integer>> table;
            table = loadHashMapFromFile();
            ret = table.get(tablename).get(0) + 1;
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(BPlusOne.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    public ArrayList<Integer> getLocationArrayfromCellArray(
            ArrayList<Cell> cells) {
        int offset = BPlusOne.PAGE_SIZE;
        ArrayList<Integer> ret = new ArrayList<>();

        for (Cell cell : cells) {
            offset -= cell.getCellSize();
            ret.add(offset);
        }
        return ret;
    }

    private void doDelete(int currNode, int rowID) throws IOException {
        byte[] pageBytes = new byte[PAGE_SIZE];

        fileP.seek(currNode * PAGE_SIZE);
        fileP.read(pageBytes);

        Page page = new Page(pageBytes);
        page.unmarshalPage();

        if (rowID < page.getMinRowidInPage()) {
            doDelete(page.getCell(0).getLeftChildPageNo(), rowID);
        } else if (rowID > page.getMaxRowidInPage()) {
            doDelete(page.getRightNodePageNo(), rowID);
        } else {
            ArrayList<Cell> cells = page.getAllCells();
            int size = cells.size();
            for (int i = 0; i < size; i++) {
                Cell cell = cells.get(i);
                if (rowID <= cell.getRowId()) {
                    if (!page.isLeaf()) {
                        doDelete(cell.getLeftChildPageNo(), rowID);
                    } else {
                        if (rowID != cell.getRowId()) {
                            Logger.getLogger(BPlusOne.class.getName())
                                    .log(Level.SEVERE, "Leaf does not have rowID " + rowID, rowID);
                        } else {
                           Logger.getLogger(BPlusOne.class.getName())
                                    .log(Level.INFO, "Found record, deleting cell " + rowID, rowID); 
                        }
                        cells.remove(i);
                        page.setCellArray(cells);
                        page.setCellLocationArray(getLocationArrayfromCellArray(cells));
                        fileP.seek(currNode * PAGE_SIZE);
                        fileP.write(page.marshalPage());
                        break;
                    }
                }
            }
        }
    }

    /**
     *
     * @param tablename
     * @param rowIDs IDs of Rows to delete
     */
    public void delete(String tablename, ArrayList<Integer> rowIDs) {
        try {
            HashMap<String, ArrayList<Integer>> table = loadHashMapFromFile();

            fileP = new RandomAccessFile(PARENT_PATH + tablename, "rw");
            root_index = table.get(tablename).get(1);

            for (Integer rowID : rowIDs) {
                doDelete(root_index, rowID);
            }
            fileP.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(BPlusOne.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private ArrayList<byte[]> getRowData(String tablename) {
        ArrayList<byte[]> ret = new ArrayList<>();
        int pageNo = 0;
        try {
            fileP = new RandomAccessFile(PARENT_PATH + tablename, "rw");
            while (true) {

                byte[] pageBytes = new byte[PAGE_SIZE];

                fileP.seek(pageNo * PAGE_SIZE);
                fileP.read(pageBytes);

                Page page = new Page(pageBytes);
                page.unmarshalPage();

                ArrayList<Cell> cells = page.getAllCells();
                cells.forEach((cell) -> {
                    ret.add(cell.getPayLoadBytes());
                });
                if (page.getRightNodePageNo() < 0) {
                    break;
                }
                pageNo = page.getRightNodePageNo();

            }
            fileP.close();
        } catch (IOException ex) {
            Logger.getLogger(BPlusOne.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    private byte[] getRowData(int currNode, int rowID) throws IOException {
        byte[] pageBytes = new byte[PAGE_SIZE];

        fileP.seek(currNode * PAGE_SIZE);
        fileP.read(pageBytes);

        Page page = new Page(pageBytes);
        page.unmarshalPage();

        if (rowID < page.getMinRowidInPage()) {
            getRowData(page.getCell(0).getLeftChildPageNo(), rowID);
        } else if (rowID > page.getMaxRowidInPage()) {
            getRowData(page.getRightNodePageNo(), rowID);
        } else {
            ArrayList<Cell> cells = page.getAllCells();
            int size = cells.size();
            for (int i = 0; i < size; i++) {
                Cell cell = cells.get(i);
                if (rowID <= cell.getRowId()) {
                    if (!page.isLeaf()) {
                        getRowData(cell.getLeftChildPageNo(), rowID);
                    } else {
                        if (rowID != cell.getRowId()) {
                            Logger.getLogger(BPlusOne.class.getName())
                                    .log(Level.SEVERE, "Leaf does not have rowID " + rowID, rowID);
                        }
                        return cell.getPayLoadBytes();
                    }
                }
            }
        }
        return null;
    }

    /**
     * getRowData(): Get row data
     * @param tablename : Name of the table whose row Data is needed
     * @param rowIDs : List of rowIDs. If null, getRowData() return all row data
     * @return ArrayList of row data
     */
    public ArrayList<byte[]> getRowData(String tablename, ArrayList<Integer> rowIDs) {
        ArrayList<byte[]> ret = new ArrayList<>();

        if (!Files.exists(Paths.get(PARENT_PATH + tablename),
                new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            return ret;
        }

        if (rowIDs == null) {
            return getRowData(tablename);
        }
        try {
            fileP = new RandomAccessFile(PARENT_PATH + tablename, "rw");
            HashMap<String, ArrayList<Integer>> table = loadHashMapFromFile();
            root_index = table.get(tablename).get(1);
            for (Integer rowID : rowIDs) {
                byte[] r = getRowData(root_index, rowID);

                if (r == null) {
                    Logger.getLogger(BPlusOne.class.getName())
                            .log(Level.SEVERE, "rowID " + rowID + " not in tree", rowID);
                    ret.clear();
                    return ret;
                }
                ret.add(r);
            }
            fileP.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(BPlusOne.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
}
