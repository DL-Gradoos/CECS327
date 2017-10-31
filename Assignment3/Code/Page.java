import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Page implements Serializable {
    @SerializedName("number")
    private int number;
    @SerializedName("guid")
    private long guid;
    @SerializedName("size")
    private int size;

    /**
     * @param n Parameter that contains the number of pages.
     * @param g Parameter that contains the guid.
     * @param s Parameter that contains the size of the page
     * 
     * Default constructor. 
     */
    public Page(int n, long g, int s) {
        number = n;
        guid = g;
        size = s;
    }

    /**
     * @return the number of pages.
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param number Parameter that contains the new number of pages.
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * @return the guid of the page. 
     */
    public long getGuid() {
        return guid;
    }

    /**
     * @param guid Parameter that sets the guid of the page
     */
    public void setGuid(long guid) {
        this.guid = guid;
    }

    /**
     * @return the size of the page
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size Parameter that will contain the new size. 
     */
    public void setSize(int size) {
        this.size = size;
    }
}
