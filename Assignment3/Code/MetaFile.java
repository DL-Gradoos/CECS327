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

    /**
     * @param n Parameter that contains the name of the file
     * @param np Parameter that contains the number of pages.
     * @param ps Parameter that contains the size of the pages.
     * @param s Parameter that contains the size of the file.
     * @param p Parameter that contains the list of pages. 
     */
    public MetaFile(String n, int np, int ps, long s, ArrayList<Page> p) {
        name = n;
        numberOfPages = np;
        pageSize = ps;
        size = s;
        pages = p;
    }

    /**
     * @param n Parameter that contains the name of the file
     * @param np Parameter that contains the number of pages.
     * @param ps Parameter that contains the size of the pages.
     * @param s Parameter that contains the size of the file.
     */
    public MetaFile(String n, int np, int ps, long s) {
        name = n;
        numberOfPages = np;
        pageSize = ps;
        size = s;
        pages = new ArrayList<>();
    }

    /**
     * @return the name of the file.
     * 
     * This function returns the name of the file.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Parameter that contains the name of the file.
     * 
     * This function sets the name of the file. 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the number of pages in the file.
     * 
     * This function returns the number of pages in a file.
     */
    public int getNumberOfPages() {
        return pages.size();
    }

    /**
     * @param numberOfPages Parameter that sets the number of pages in the file.
     * 
     * This function sets the number of pages in a file. 
     */
    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    /**
     * @return the size of the page
     * 
     * This function gets the size of the page in the file.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize parameter that sets the page size of the file.
     * 
     * This function sets the page size of the file. 
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the size of the file 
     * 
     * This function returns the size of the file. 
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size parameter that sets the size of the file. 
     * 
     * This function sets the size of the file. 
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * @return the list of pages in the file 
     * 
     * This function returns the list of pages in the file .
     */
    public ArrayList<Page> getListOfPages() {
        return pages;
    }

    /**
     * @param index Parameter that contains the location of the page.
     * @return the page indicated by the index.
     * 
     * This function returns a page indicated by the index.
     */
    public Page getPage(int index) {
        return pages.get(index);
    }

    /**
     * @param p Parameter that contains a page to be added.
     * 
     * This function adds a page to the file.
     */
    public void addPage(Page p) {
        pages.add(p);
        numberOfPages++;
    }

    /**
     * @param index Parameter that contains a location of a page to be removed.
     * 
     * This function removes a page at a indicated index.
     */
    public void removePage(int index) {
        pages.remove(index);
    }

    /**
     *  This function removes all of the pages in the file. 
     */
    public void removeAllPages() {
        for(int ii = pages.size() - 1; ii >= 0; ii--) {
            pages.remove(ii);
        }
    }

    /**
     * This function updates the size of the file. 
     */
    public void updateSize() {
        for(Page p : pages) {
            size += p.getSize();
        }
    }
}
