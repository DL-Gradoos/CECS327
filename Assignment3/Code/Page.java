import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Page implements Serializable {
    @SerializedName("number")
    private int number;
    @SerializedName("guid")
    private long guid;
    @SerializedName("size")
    private int size;

    public Page(int n, long g, int s) {
        number = n;
        guid = g;
        size = s;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getGuid() {
        return guid;
    }

    public void setGuid(long guid) {
        this.guid = guid;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
