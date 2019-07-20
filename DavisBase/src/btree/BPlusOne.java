package btree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BPlusOne {

    public static final String FILE = "/home/sourav/DavisBaseTable.txt";
    public static final int MAX_ELEMENTS_PER_NODE = 3;
    public static final int PAGE_HEADER_SIZE = 9;
    public static final int PAGE_SIZE = 512;

    RandomAccessFile fileP = null;
    int root_index;

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

    public BPlusOne() {
        dummyRowId = 0;
        try {
            Files.deleteIfExists(Paths.get(FILE));
            
            fileP = new RandomAccessFile(FILE, "rw");
            
            /**
             * Empty Tree
             */
            root_index = -1;
        } catch (IOException ex) {
            Logger.getLogger(BPlusOne.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeFile() {
        try {
            fileP.close();
        } catch (IOException ex) {
            Logger.getLogger(BPlusOne.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<A> divideCells(ArrayList<Cell> cells,
            ArrayList<Integer> cellLocations) {
        int numCells = cells.size();
        ArrayList<A> ret = new ArrayList<>();
        ArrayList<Cell> cl = new ArrayList<>();
        ArrayList<Integer> clLoc = new ArrayList<>();

        for (int i = 0; i < numCells / 2; i++) {
            cl.add(cells.get(i));
            clLoc.add(cellLocations.get(i));
        }
        ret.add(new A(cl, clLoc));

        cl.clear();
        clLoc.clear();
        for (int i = numCells / 2; i < numCells; i++) {
            cl.add(cells.get(i));
            clLoc.add(cellLocations.get(i));
        }
        ret.add(new A(cl, clLoc));
        return ret;
    }

    private int getNextRowId() {
        //TODO
        return dummyRowId++;
    }
    
    private void setNewParentOfChildren(int leftChildNo, int rightChildNo, 
            int parentPageNo) throws IOException{
        byte[] pageBytes = new byte[PAGE_SIZE];
        
        /** Left Child **/
        fileP.seek(leftChildNo * PAGE_SIZE);
        fileP.read(pageBytes);
        
        Page leftChild = new Page(pageBytes);
        leftChild.unmarshalPage();
        leftChild.setParentPageNo(parentPageNo);
        
        fileP.seek(leftChildNo * PAGE_SIZE);
        fileP.write(leftChild.marshalPage());
        
         /** Right Child **/
        fileP.seek(rightChildNo * PAGE_SIZE);
        fileP.read(pageBytes);
        
        Page rightChild = new Page(pageBytes);
        rightChild.unmarshalPage();
        rightChild.setParentPageNo(parentPageNo);
        
        fileP.seek(rightChildNo * PAGE_SIZE);
        fileP.write(rightChild.marshalPage());
    }

    public void insert(/**
             * int key, TODO: Figure out how the record will be provided*
             */
            byte[] rowData) {
        Cell cell = new Cell(-1, rowData, getNextRowId());

        try {
            if (root_index < 0) {
                /**
                 * Empty Tree *
                 */
                int offsetInPage = PAGE_SIZE - cell.getCellSize() - 1;
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
                    int offsetInPage = PAGE_SIZE - Cell.CELL_HEADER_SIZE;
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
                        (int)fileP.length() / PAGE_SIZE
                    );
                    
                    page.setRightNodePageNo(ret.getPageNo());
                    root_index = (int) (fileP.length() / PAGE_SIZE);
                    fileP.seek(fileP.length());
                    fileP.write(page.marshalPage());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BPlusOne.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ReturnContainer splitPageOnFullAndAddCell(Page page, Cell newCell)
            throws IOException {
        int newPageNo = (int) (fileP.length() / PAGE_SIZE);
        int newParentKey;
        int oldRightPageNo = page.getRightNodePageNo();
        ArrayList<A> parts = divideCells(
                page.getAllCells(),
                page.getAllCellLocations()
        );

        newParentKey = parts.get(1).mCells.get(0).getRowId();

        /**
         * Add the new cell to the Right Array formed after split *
         */
        int lastCellLoc = parts.get(1).mCellLocations.get(
                parts.get(1).mCellLocations.size() - 1
        );
        parts.get(1).mCellLocations.add(lastCellLoc - newCell.getCellSize());
        parts.get(1).mCells.add(newCell);

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
         * Modify the current page and make it the left child page *
         */
        page.setCellArray(parts.get(0).mCells);
        page.setCellLocationArray(parts.get(0).mCellLocations);

        /**
         * Dump the newly created right page at the end file *
         */
        fileP.seek(fileP.length());
        fileP.write(rightNode.marshalPage());

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
            Cell newCell = new Cell(page.getRightNodePageNo(), null, ret.keyValue);
            page.setRightNodePageNo(ret.pageNo);
            if (!page.isNodeFullDummy()) {
                /**
                 * Just Add newCell to Page and dump page on file *
                 */
                page.addNewCell(newCell);
            } else {
                ReturnContainer ret2 = splitPageOnFullAndAddCell(page, newCell);
                return ret2;
            }
        } else {
            /**
             * Actual Insertion of record *
             */
            if (!page.isNodeFullDummy()) {
                page.addNewCell(cell);
            } else {
                ReturnContainer ret = splitPageOnFullAndAddCell(page, cell);
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
