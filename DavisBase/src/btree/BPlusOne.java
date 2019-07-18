package btree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BPlusOne {

    public static final String FILE = "";
    public static final int MAX_ELEMENTS_PER_NODE = 3;
    public static final int PAGE_HEADER_SIZE = 9;
    public static final int PAGE_SIZE = 512;

    RandomAccessFile fileP = null;
    int root_index;

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
        try {
            fileP = new RandomAccessFile(FILE, "rw");
            /**
             * Empty Tree
             */
            root_index = -1;
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
        int ret = 0;
        //TODO
        return ret;
    }

    public void insert(int key, /**
             * TODO: Figure out how the record will be provided*
             */
            byte[] rowData) {
        Cell cell = new Cell(-1, rowData, getNextRowId());

        try {
            if (root_index < 0) {
                /**
                 * Empty Tree *
                 */
                Page page = new Page(true, new ArrayList<>(Arrays.asList(cell)),
                        new ArrayList<>(Arrays.asList(cell.getCellSize())), -1, -1);
                byte[] pageBytes = page.marshalPage();
                root_index = 0;
                fileP.write(pageBytes);
            } else {
                ReturnContainer ret = doInsert(cell, root_index);
                if (ret != null) {
                    root_index = ret.getPageNo();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BPlusOne.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ReturnContainer splitPageOnFullAndAddCell(Page page, Cell newCell)
            throws IOException {
        int newPageNo = (int) (fileP.length() / PAGE_SIZE) + 1;
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
        if (!page.isLeaf()) {
            /**
             * After the split, the left sibling page's right child should be
             * the left child of the first cell in the 2nd half.
             */
            page.setRightNodePageNo(
                parts.get(1).mCells.get(0).getLeftChildPageNo()
            );
            parts.get(1).mCells.remove(0);
        } else {
            page.setRightNodePageNo(newPageNo);
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
         * Dump the newly created right page in file *
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
            Cell newCell = new Cell(page.getParentPageNo(), null, ret.keyValue);
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
        fileP.write(page.marshalPage());
        return null;
    }
}
