import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Metadata implements Serializable {
    @SerializedName("files")
    private ArrayList<MetaFile> files;

    public Metadata(ArrayList<MetaFile> f) {
        files = f;
    }
    public Metadata() {
        files = new ArrayList<>();
    }

    public MetaFile getFile(int index) {
        return files.get(index);
    }

    public MetaFile getFile(String fileName) {
        for(MetaFile f : files) {
            if(f.getName().equals(fileName))
                return f;
        }
        return null;
    }

    public void addFile(MetaFile f) {
        files.add(f);
    }

    public void removeFile(int index) {
        files.remove(index);
    }

    public void removeFile(String fileName) {
        for (int ii = 0; ii < files.size(); ii++) {
            if (files.get(ii).getName().equals(fileName)) {
                files.remove(ii);
                return;
            }
        }
    }

    public boolean doesFileExist(String fileName) {
        for (MetaFile file : files) {
            if(file.getName().equals(fileName))
                return true;
        }
        return false;
    }

    public ArrayList<MetaFile> getListOfFiles() { return files; }
}
