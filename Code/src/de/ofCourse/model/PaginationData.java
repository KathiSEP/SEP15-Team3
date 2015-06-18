/**
 * This package represents all the models which are used
 */
package de.ofCourse.model;

import java.io.Serializable;

/**
 * Contains all information for the pagination.
 * 
 * @author Tobias Fuchs
 *
 */
public class PaginationData implements Serializable {

    /**
     * Serial id
     */
    private static final long serialVersionUID = 4527206697875254825L;

    /**
     * Stores the number of items shown on one page.
     */
    private int elementsPerPage;
    /**
     * Stores the current displayed page.
     */
    private int currentPageNumber;
    /**
     * Stores the displayed column name the page is sorted
     */
    private String sortColumn;
    /**
     * Stores whether the displayed data in ascending order
     */
    private boolean sortAsc;
    
    /**
     * Number of pages to display
     */
    private int numberOfPages;

    /**
     * Constructor of the class <code>PaginationData</code>.
     * 
     * @param elementsPerPage
     *            the number of items shown on one page
     * @param currentPageNumber
     *            the current displayed page
     * @param sortCoulmn
     *            the displayed column name the page is sorted
     * @param sortAsc
     *            whether the displayed data in ascending order
     * @param numberOfPages
     *            the number of pages to display
     */
    public PaginationData(int elementsPerPage, int currentPageNumber,
	    String sortCoulmn, boolean sortAsc) {
	this.elementsPerPage = elementsPerPage;
	this.currentPageNumber = currentPageNumber;
	this.sortColumn = sortCoulmn;
	this.sortAsc = sortAsc;
    }

   

    /**
     * Default constructor
     */
    public PaginationData() {
    }


    public String getSQLSortDirection() {
        if (this.isSortAsc()) {
            return "ASC";
        } else {
            return "DESC";
        }
    }
    


    /**
     * Actualizes the number of pages of the pagination.
     * 
     * @param numberOfAllItems
     *            number of elements that are to display(with pagination)
     */
    public void refreshNumberOfPages(int numberOfAllItems) {
	int calculatedNumberOfPages = 0;

	if (numberOfAllItems % this.getElementsPerPage() == 0
		&& numberOfAllItems != 0) {
	    calculatedNumberOfPages = (numberOfAllItems / this
		    .getElementsPerPage())-1;
	} else {
	    calculatedNumberOfPages = (numberOfAllItems / this
		    .getElementsPerPage());
	}
	setNumberOfPages(calculatedNumberOfPages);

    }

    /**
     * Changes the sort direction from ascending order to descending order or
     * the other way round. That depends on the current sort direction.
     */
    public void changeSortDirection() {
	setSortAsc(!isSortAsc());
    }

    /**
     * Returns the number of pages that are displayed.
     * 
     * @return the number of pages
     */
    public int getNumberOfPages() {
	return numberOfPages;
    }

    /**
     * Sets the number of pages that are displayed.
     * 
     * @param numberOfPages
     *            the number of pages
     */
    public void setNumberOfPages(int numberOfPages) {
	this.numberOfPages = numberOfPages;
    }

    /**
     * Returns the value of the attribute <code>itemsPerPage</code>.
     * 
     * @return the number of items shown on one page
     */
    public int getElementsPerPage() {
	return elementsPerPage;
    }

    /**
     * Sets the value of the attribute <code>elementsPerPage</code>.
     * 
     * @param itemsPerPage
     *            new amount of items which are shown on the page
     */
    public void setElementsPerPage(int elementsPerPage) {
	this.elementsPerPage = elementsPerPage;
    }

    /**
     * Returns the value of the attribute <code>currentPageNumber</code>.
     * 
     * @return the page number currently displayed
     */
    public int getCurrentPageNumber() {
	return currentPageNumber;
    }

    /**
     * Sets the value of the attribute <code>currentPageNumber</code>.
     * 
     * @param shownPageNum
     *            new Page Number which is shown
     */
    public void setCurrentPageNumber(int currentPageNum) {
	this.currentPageNumber = currentPageNum;
    }

    /**
     * Returns the value of the attribute <code>sortColumn</code>.
     * 
     * @return the displayed column name the page is sorted
     */
    public String getSortColumn() {
	return sortColumn;
    }

    /**
     * Sets the value of the attribute <code>sortColumn</code>.
     * 
     * @param sortColumn
     *            new column name the page should sort
     */
    public void setSortColumn(String sortColumn) {
	this.sortColumn = sortColumn;
    }

    /**
     * Returns the value of the attribute <code>sortAsc</code>.
     * 
     * @return whether the displayed data in ascending order or not
     */
    public boolean isSortAsc() {
	return sortAsc;
    }

    /**
     * Sets the value of the attribute <code>sortAsc</code>.
     * 
     * @param sortAsc
     *            whether the data is sorted ascending or not
     */
    public void setSortAsc(boolean sortAsc) {
	this.sortAsc = sortAsc;
    }



    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + currentPageNumber;
	result = prime * result + elementsPerPage;
	result = prime * result + numberOfPages;
	result = prime * result + (sortAsc ? 1231 : 1237);
	result = prime * result
		+ ((sortColumn == null) ? 0 : sortColumn.hashCode());
	return result;
    }



    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	PaginationData other = (PaginationData) obj;
	if (currentPageNumber != other.currentPageNumber)
	    return false;
	if (elementsPerPage != other.elementsPerPage)
	    return false;
	if (numberOfPages != other.numberOfPages)
	    return false;
	if (sortAsc != other.sortAsc)
	    return false;
	if (sortColumn == null) {
	    if (other.sortColumn != null)
		return false;
	} else if (!sortColumn.equals(other.sortColumn))
	    return false;
	return true;
    }

}