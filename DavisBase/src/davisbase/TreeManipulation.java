package davisbase;

public class TreeManipulation {
    private String tablename;
    private int[] data_type;
    private String[] data_value;
    private String primary_key;
    private String command;

    //constructor

    public TreeManipulation(){

    }

    // get functions
    public String getTablename() {return this.tablename;}
    public int[] getData_type() {return this.data_type;}
    public String[] getData_value() {return this.getData_value();}
    public String getPrimary_key() {return this.primary_key;}
    public String getCommand() {return this.command;}

    // set functions
    public void setTablename(String dv) { this.tablename = dv;}
    public void setData_type(int[] dv) { this.data_type = dv.clone();}
    public void setData_value(String[] dv) { this.data_value = dv.clone();}
    public void setPrimary_key(String dv) { this.primary_key = dv;}
    public void setCommand(String dv) { this.command = dv;}

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // The following functions will take care of B+ tree and B tree.
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Insert
    public void Insert(){

    }

    // deletion
    public void Delete(){

    }

    // update
    public void Update(){

    }

    // create index file
    public void CreateIndexFile(){
        
    }

}
