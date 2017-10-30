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

    public void addFile(MetaFile f) {
        files.add(f);
    }

    public void removeFile(int index) {
        files.remove(index);
    }

    public ArrayList<MetaFile> getListOfFiles() { return files; }
}
