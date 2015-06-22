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
    private SortColumn sortColumn;

    /**
     * Stores whether the displayed data in ascending order
     */
    private SortDirection sortDirection;

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
     */
    public PaginationData(int elementsPerPage, int currentPageNumber,
	    SortColumn sortCoulmn, SortDirection sortAsc) {
	this.elementsPerPage = elementsPerPage;
	this.currentPageNumber = currentPageNumber;
	this.sortColumn = sortCoulmn;
	this.sortDirection = sortAsc;
    }

    /**
     * Default constructor
     */
    public PaginationData() {
    }

    /**
     * Refreshes the number of pages of the pagination.
     * 
     * @param numberOfAllItems
     *            number of elements that are to display(with pagination)
     */
    public void refreshNumberOfPages(int numberOfAllItems) {
	int calculatedNumberOfPages = 0;

	if (numberOfAllItems % getElementsPerPage() == 0
		&& numberOfAllItems != 0) {

	    calculatedNumberOfPages = 
		    (numberOfAllItems / getElementsPerPage()) - 1;

	} else {
	    calculatedNumberOfPages = 
		    (numberOfAllItems / getElementsPerPage());
	}
	setNumberOfPages(calculatedNumberOfPages);

    }

    /**
     * Changes the sort direction from ascending order to descending order or
     * the other way round. That depends on the current sort direction.
     */
    public void changeSortDirection() {
	if (sortDirection.equals(SortDirection.ASC)) {
	    sortDirection = SortDirection.DESC;
	} else {
	    sortDirection = SortDirection.ASC;
	}
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
     * @return the column by which is sorted
     */
    public SortColumn getSortColumn() {
	return sortColumn;
    }

    /**
     * Sets the value of the attribute <code>sortColumn</code>.
     * 
     * @param sortColumn
     *            the column by which is sorted
     */
    public void setSortColumn(SortColumn sortColumn) {
	this.sortColumn = sortColumn;
    }

    /**
     * Returns the value of the attribute <code>sortDirection</code>.
     * 
     * @return direction by that is sorted
     */
    public SortDirection getSortDirection() {
	return sortDirection;
    }

    /**
     * Sets the value of the attribute <code>sortDirection</code>.
     * 
     * @param sortDirection
     *            direction by that is sorted
     */
    public void setSortDirection(SortDirection sortDirection) {
	this.sortDirection = sortDirection;
    }

    /**
     * HashCode method of the <code>PaginationData</code> class.
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;

	result = prime * result + currentPageNumber;
	result = prime * result + elementsPerPage;
	result = prime * result + numberOfPages;
	result = prime * result
		+ ((sortColumn == null) ? 0 : sortColumn.hashCode());
	result = prime * result
		+ ((sortDirection == null) ? 0 : sortDirection.hashCode());

	return result;
    }

    /**
     * Equals method of the <code>PaginationData</code> class.
     * 
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

	// Checks the attributes for equality
	PaginationData other = (PaginationData) obj;
	if (currentPageNumber != other.currentPageNumber)
	    return false;
	if (elementsPerPage != other.elementsPerPage)
	    return false;
	if (numberOfPages != other.numberOfPages)
	    return false;
	if (sortColumn != other.sortColumn)
	    return false;
	if (sortDirection != other.sortDirection)
	    return false;

	return true;
    }
}