import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MetaFile implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("numberOfPages")
    private int numberOfPages;
    @SerializedName("pageSize")
    private int pageSize;
    @SerializedName("size")
    private long size;
    @SerializedName("pages")
    private ArrayList<Page> pages;

    public MetaFile(String n, int np, int ps, long s, ArrayList<Page> p) {
        name = n;
        numberOfPages = np;
        pageSize = ps;
        size = s;
        pages = p;
    }

    public MetaFile(String n, int np, int ps, long s) {
        name = n;
        numberOfPages = np;
        pageSize = ps;
        size = s;
        pages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfPages() {
        return pages.size();
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public ArrayList<Page> getListOfPages() {
        return pages;
    }

    public Page getPage(int index) {
        return pages.get(index);
    }

    public void addPage(Page p) {
        pages.add(p);
        numberOfPages++;
    }

    public void removePage(int index) {
        pages.remove(index);
    }

    public void removeAllPages() {
        for(int ii = pages.size() - 1; ii >= 0; ii--) {
            pages.remove(ii);
        }
    }

    public void updateSize() {
        for(Page p : pages) {
            size += p.getSize();
        }
    }
}
