package com.capgemini.librarymanagementsystemjdbc.dto;

import java.io.Serializable;



public class BookBean {
	private int bookId;
	private String bookName;
	private String bookPublications;
	private String authorName;
	private String category;
	
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getBookPublications() {
		return bookPublications;
	}
	public void setBookPublications(String bookPublications) {
		this.bookPublications = bookPublications;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}	
	
	


}
