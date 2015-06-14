package de.ofCourse.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PaginationDataTest {

    @Test
    public void test() {

	PaginationData pagination = new PaginationData();
	// Check creation
	assertNotNull(pagination);

	PaginationData paginationConstruct = new PaginationData(5, 0, "test",
		true);
	pagination.setNumberOfPages(0);
	// Check creation
	assertNotNull(paginationConstruct);

	// Check constructors
	assertNotSame(pagination, paginationConstruct);
	pagination.setElementsPerPage(5);
	pagination.setCurrentPageNumber(0);
	pagination.setSortColumn("test");
	pagination.setSortAsc(true);
	pagination.setNumberOfPages(0);

	// Check setter
	assertTrue(pagination.equals(paginationConstruct));

	// Check calculation of number of pages
	// 24 ==> 5 pages(beginnt bei 0 zu z�hlen)
	pagination.actualizeNumberOfPages(24);
	assertSame(4, pagination.getNumberOfPages());

	// 5==> 1 pages
	pagination.actualizeNumberOfPages(5);
	assertSame(0, pagination.getNumberOfPages());

	// 100==> 20 pages
	pagination.actualizeNumberOfPages(100);
	assertSame(19, pagination.getNumberOfPages());

	// 50==> 10 pages
	pagination.actualizeNumberOfPages(50);
	assertSame(9, pagination.getNumberOfPages());

	// 50==> 10 pages
	pagination.actualizeNumberOfPages(3);
	assertSame(0, pagination.getNumberOfPages());

	// check page navigation
	pagination.actualizeCurrentPageNumber("forward");
	assertSame(0, pagination.getCurrentPageNumber());

	pagination.actualizeCurrentPageNumber("back");
	assertSame(0, pagination.getCurrentPageNumber());

	pagination.actualizeCurrentPageNumber("5");
	assertSame(5, pagination.getCurrentPageNumber());

    }

}