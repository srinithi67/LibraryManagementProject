package com.capgemini.librarymanagementsystemjdbc.dao;


import java.util.LinkedList;
import java.util.List;

import com.capgemini.librarymanagementsystemjdbc.dto.BookBean;
import com.capgemini.librarymanagementsystemjdbc.dto.BookIssueDetails;
import com.capgemini.librarymanagementsystemjdbc.dto.BorrowedBooks;
import com.capgemini.librarymanagementsystemjdbc.dto.RequestDetails;
import com.capgemini.librarymanagementsystemjdbc.dto.UserBean;

public interface UserDAO {
	boolean registerUser(UserBean user);
	UserBean authUser(String email, String password);
	boolean addBook(BookBean book);
	boolean removeBook(int bookId);
	boolean updateBook(BookBean book);
	boolean bookIssue(int bookId,int uId);
	boolean request(int uId,int bookId);
	List<BorrowedBooks> borrowedBook(int uId);
	LinkedList<BookBean> searchBookById(int bookId);
	LinkedList<BookBean> searchBookByTitle(String bookName);
	LinkedList<BookBean> searchBookByAuthor(String author);
	LinkedList<BookBean> getBooksInfo();
	boolean returnBook(int bId,int uId,String status);
	LinkedList<BookIssueDetails> bookHistoryDetails(int uId);
	LinkedList<RequestDetails> showRequests();
	LinkedList<BookIssueDetails> showIssuedBooks();
	LinkedList<UserBean> showUsers();
	boolean updatePassword(String email,String password,String newPassword,String role);
	}








