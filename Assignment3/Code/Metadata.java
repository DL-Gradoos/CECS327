import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Metadata implements Serializable {
    @SerializedName("files")
    private ArrayList<MetaFile> files;

    /**
     * @param f Parameter that contains the list of files in the metadata.
     * 
     * Constructor that initializes the files in the metadata. 
     */
    public Metadata(ArrayList<MetaFile> f) {
        files = f;
    }
    /**
     *  Empyty constructor of Metadata.
     */
    public Metadata() {
        files = new ArrayList<>();
    }

    /**
     * @param index Parameter that contains where the file is located.
     * @return the file in the index.
     * 
     * This function returns the file indicated by the index in the ArrayList.
     */
    public MetaFile getFile(int index) {
        return files.get(index);
    }

    /**
     * @param fileName Parameter that contains the name of the file being obtained
     * @return the file specified by user.
     * 
     * This function returns the file specified by the user.
     */
    public MetaFile getFile(String fileName) {
        for(MetaFile f : files) {
            if(f.getName().equals(fileName))
                return f;
        }
        return null;
    }

    /**
     * @param f Parameter that contains file to be added to the metadata.json
     * 
     * This function adds a file to the metadata.json file.
     */
    public void addFile(MetaFile f) {
        files.add(f);
    }

    /**
     * @param index Parameter that contains the file to be removed at an index
     * 
     * This function removes a file from the metadata. 
     */
    public void removeFile(int index) {
        files.remove(index);
    }

    /**
     * @param fileName Parameter that contains the name of the file being obtained
     * 
     * This function removes a file from the metadata. 
     */
    public void removeFile(String fileName) {
        for (int ii = 0; ii < files.size(); ii++) {
            if (files.get(ii).getName().equals(fileName)) {
                files.remove(ii);
                return;
            }
        }
    }

    /**
     * @param fileName Parameter that contains the name of the file being obtained
     * @return whether the file exists.
     * 
     * This function determines whether the file exists or not. 
     */
    public boolean doesFileExist(String fileName) {
        for (MetaFile file : files) {
            if(file.getName().equals(fileName))
                return true;
        }
        return false;
    }

    /**
     * @return the list of files in the metadata. 
     */
    public ArrayList<MetaFile> getListOfFiles() { return files; }
}
