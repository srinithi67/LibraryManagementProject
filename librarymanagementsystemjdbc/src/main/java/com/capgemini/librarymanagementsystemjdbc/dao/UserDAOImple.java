package com.capgemini.librarymanagementsystemjdbc.dao;


import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.capgemini.librarymanagementsystemjdbc.dto.BookBean;
import com.capgemini.librarymanagementsystemjdbc.dto.BookIssueDetails;
import com.capgemini.librarymanagementsystemjdbc.dto.BorrowedBooks;
import com.capgemini.librarymanagementsystemjdbc.dto.RequestDetails;
import com.capgemini.librarymanagementsystemjdbc.dto.UserBean;
import com.capgemini.librarymanagementsystemjdbc.exception.LMSException;
import com.mysql.jdbc.Statement;

public class UserDAOImple implements UserDAO{
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Statement stmt=null;
	

	@Override
	public boolean registerUser(UserBean user) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("insert into user values(?,?,?,?,?,?,?)")){
				pstmt.setInt(1,user.getuId());
				pstmt.setString(2, user.getFirstName());
				pstmt.setString(3, user.getLastName());
				pstmt.setString(4, user.getEmail());
				pstmt.setString(5, user.getPassword());
				pstmt.setLong(6, user.getMobile());
				pstmt.setString(7, user.getRole());
				int count = pstmt.executeUpdate();
				if(user.getEmail().isEmpty() && count==0) {
					return false;
				} else {
					return true;
				}
			}
		}catch(Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	@Override
	public UserBean authUser(String email, String password) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("select * from user where email=? and password=?");) {
				pstmt.setString(1,email);
				pstmt.setString(2,password);
				rs=pstmt.executeQuery();
				if(rs.next()) {
					UserBean bean = new UserBean();
					bean.setuId(rs.getInt("uId"));
					bean.setFirstName(rs.getString("firstName"));
					bean.setLastName(rs.getString("lastName"));
					bean.setEmail(rs.getString("email"));
					bean.setPassword(rs.getString("password"));
					bean.setMobile(rs.getLong("mobile"));
					bean.setRole(rs.getString("role"));
					return bean;
				} else {
					return null;
				}
			}
		}catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean addBook(BookBean book) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("insert into bookdetails values(?,?,?,?,?)");) {
				pstmt.setInt(1, book.getBookId());
				pstmt.setString(2, book.getBookName());
				pstmt.setString(3, book.getAuthorName());
				pstmt.setString(4, book.getCategory());
				pstmt.setString(5, book.getBookPublications());
				//pstmt.setInt(6, book.getCopies());
				int count = pstmt.executeUpdate();
				if(count!=0) {
					return true;
				} else {
					return false;
				}
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	@Override
	public boolean removeBook(int bId) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("delete from bookdetails where bookId=?");) {
				pstmt.setInt(1,bId);
				int count=pstmt.executeUpdate();
				if(count!=0) {
					return true;
				} else {
					return false;
				}
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	@Override
	public boolean updateBook(BookBean book) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("update bookdetails set bookName=? where bookId=?");) {
				pstmt.setString(1,book.getBookName());
				pstmt.setInt(2,book.getBookId());
				int count=pstmt.executeUpdate();
				if(count!=0) {
					return true;
				} else {
					return false;
				}
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	@Override
	public boolean bookIssue(int bId,int uId) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("select * from request_details where uId=? and bId=? and email=(select email from user where uId=?)")) {
				pstmt.setInt(1, uId);
				pstmt.setInt(2, bId);
				pstmt.setInt(3, uId);
				ResultSet rs = pstmt.executeQuery();
				if(rs.next()) {
					try(PreparedStatement pstmt1 = conn.prepareStatement("insert into book_issue values(?,?,?,?)");){
						pstmt1.setInt(1, bId);
						pstmt1.setInt(2, uId);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
						Calendar cal = Calendar.getInstance();
						String issueDate = sdf.format(cal.getTime());
						pstmt1.setDate(3, java.sql.Date.valueOf(issueDate));
						cal.add(Calendar.DAY_OF_MONTH, 7);
						String returnDate = sdf.format(cal.getTime());
						pstmt1.setDate(4, java.sql.Date.valueOf(returnDate));
						int count=pstmt1.executeUpdate();
						if(count != 0) {	
							try(PreparedStatement pstmt2 = conn.prepareStatement("Insert into borrowed_book values(?,?,(select email from user where uId=?))")){
								pstmt2.setInt(1, uId);
								pstmt2.setInt(2, bId);
								pstmt2.setInt(3, uId);
								int isBorrowed = pstmt2.executeUpdate();
								if(isBorrowed != 0) {
									return true;
								}else {
									return false;
								}
							}
						} else {
							throw new LMSException("Book Not issued");
						}					
					}
				} else {
					throw new LMSException("The respective user have not placed any request");
				}			
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	@Override
	public boolean request(int uId, int bId) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("select count(*) as uId from borrowed_books where uId=? and bId=? and email=(select email from user where uId=?)");) {
				pstmt.setInt(1, uId);
				pstmt.setInt(2, bId);
				pstmt.setInt(3, uId);
				ResultSet rs = pstmt.executeQuery();
				if(rs.next()) {
					int isBookExists = rs.getInt("uId");
					if(isBookExists==0) {
						try(PreparedStatement pstmt1 = conn.prepareStatement("select count(*) as uId from book_issue where id=?");) {
							pstmt1.setInt(1, uId);
							rs=pstmt1.executeQuery();
							if(rs.next()) {
								int noOfBooksBorrowed = rs.getInt("uId");
								if(noOfBooksBorrowed<3) {
									try(PreparedStatement pstmt2 = conn.prepareStatement("insert into request_details values(?,(select concat(firstName,'_',lastName) from user where uId=?)"
											+ ",?,(select bookName from bookdetails where bookId=?),(select email from user where uId=?))");){
										pstmt2.setInt(1,uId);
										pstmt2.setInt(2, uId);
										pstmt2.setInt(3, bId);
										pstmt2.setInt(4, bId);
										pstmt2.setInt(5, uId);
										int count = pstmt2.executeUpdate();
										if(count != 0) {
											return true;
										}else {
											return false;
										}
									}				 
								}else {
									throw new LMSException("no Of books limit has crossed");
								}
							}else {
								throw new LMSException("no of books limit has crossed");
							}		
						}				
					}else{
						throw new LMSException("You have already borrowed the requested book");
					}		
				}else {
					throw new LMSException("You have already borrowed the requested book");
				}			
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	@Override
	public LinkedList<BookBean> searchBookByTitle(String bookName) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("select * from bookdetails where bookName=?");) {
				pstmt.setString(1,bookName);
				rs=pstmt.executeQuery();
				LinkedList<BookBean> beans = new LinkedList<BookBean>();
				while(rs.next()) {
					BookBean bean = new BookBean();
					bean.setBookId(rs.getInt("bookId"));
					bean.setBookName(rs.getString("bookName"));
					bean.setAuthorName(rs.getString("authorName"));
					bean.setCategory(rs.getString("category"));
					bean.setBookPublications(rs.getString("bookPublications"));
					//bean.setCopies(rs.getInt("copies"));
					beans.add(bean);
				}
				return beans;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	@Override
	public LinkedList<BookBean> searchBookByAuthor(String author) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("select * from bookdetails where authorName=?");) {
				pstmt.setString(1,author);
				rs=pstmt.executeQuery();
				LinkedList<BookBean> beans = new LinkedList<BookBean>();
				while(rs.next()) {
					BookBean bean = new BookBean();
					bean.setBookId(rs.getInt("bookId"));
					bean.setBookName(rs.getString("bookName"));
					bean.setAuthorName(rs.getString("authorName"));
					bean.setCategory(rs.getString("category"));
					bean.setBookPublications(rs.getString("bookPublications"));
					//bean.setCopies(rs.getInt("copies"));
					beans.add(bean);
				}
				return beans;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	@Override
	public LinkedList<BookBean> getBooksInfo() {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					Statement stmt = (Statement)conn.createStatement();) {
				rs = stmt.executeQuery("select * from bookdetails");
				LinkedList<BookBean> beans = new LinkedList<BookBean>();
				while(rs.next()) {
					BookBean bean = new BookBean();
					bean.setBookId(rs.getInt("bookId"));
					bean.setBookName(rs.getString("bookName"));
					bean.setAuthorName(rs.getString("authorName"));
					bean.setCategory(rs.getString("category"));
					bean.setBookPublications(rs.getString("bookPublications"));
					//bean.setCopies(rs.getInt("copies"));
					beans.add(bean);
				}
				return beans;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean returnBook(int bId,int uId,String status) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("select * from book_issue where bookId=? and id=?");) {
				pstmt.setInt(1, bId);
				pstmt.setInt(2, uId);
				ResultSet rs = pstmt.executeQuery();
				if(rs.next()) {
					Date issueDate = rs.getDate("issueDate");
					Calendar cal=Calendar.getInstance();
					Date returnDate = rs.getDate("returnDate");
					long difference = issueDate.getTime() - returnDate.getTime();
					float daysBetween = (difference / (1000*60*60*24));
					if(daysBetween>7) {
						float fine = daysBetween*5;
						System.out.println("The user has to pay the fine of the respective book of Rs:"+fine);
						if(status=="yes") {
							try(PreparedStatement pstmt1 = conn.prepareStatement("delete from book_issue where bookId=? and id=?");) {
								pstmt1.setInt(1,bId);
								pstmt1.setInt(2,uId);
								int count =  pstmt1.executeUpdate();
								if(count != 0) {
									try(PreparedStatement pstmt2 = conn.prepareStatement("delete from borrowed_books where bId=? and uId=?");) {
										pstmt2.setInt(1, bId);
										pstmt2.setInt(2, uId);
										int isReturned = pstmt2.executeUpdate();
										if(isReturned != 0 ) {
											try(PreparedStatement pstmt3 = conn.prepareStatement("delete from request_details where bId=? and uId=?");){
												pstmt3.setInt(1, bId);
												pstmt3.setInt(2, uId);
												int isRequestDeleted = pstmt3.executeUpdate();
												if(isRequestDeleted != 0) {
													return true;
												}else {
													return false;
												}
											}
										}else {
											return false;
										}
									}
								} else {
									return false;
								}
							}
						} else {
							throw new LMSException("The User has to pay fine for delaying book return");
						}
					}else {
						try(PreparedStatement pstmt1 = conn.prepareStatement("delete from book_issue where bookId=? and id=?");) {
							pstmt1.setInt(1,bId);
							pstmt1.setInt(2,uId);
							int count =  pstmt1.executeUpdate();
							if(count != 0) {
								try(PreparedStatement pstmt2 = conn.prepareStatement("delete from borrowed_books where bId=? and uId=?");) {
									pstmt2.setInt(1, bId);
									pstmt2.setInt(2, uId);
									int isReturned = pstmt2.executeUpdate();
									if(isReturned != 0 ) {
										try(PreparedStatement pstmt3 = conn.prepareStatement("delete from request_details where bId=? and uId=?");){
											pstmt3.setInt(1, bId);
											pstmt3.setInt(2, uId);
											int isRequestDeleted = pstmt3.executeUpdate();
											if(isRequestDeleted != 0) {
												return true;
											}else {
												return false;
											}
										}
									}else {
										return false;
									}
								}
							} else {
								return false;
							}
						}
					}
				}else {
					throw new LMSException("This respective user hasn't borrowed any book");
				}
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	@Override
	public LinkedList<BookIssueDetails> bookHistoryDetails(int uId) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("select count(*) as uId from book_issue where id=?");) {
				pstmt.setInt(1, uId);
				rs=pstmt.executeQuery();
				LinkedList<BookIssueDetails> beans = new LinkedList<BookIssueDetails>();
				while(rs.next()) {
					BookIssueDetails issueDetails = new BookIssueDetails();
					issueDetails.setUserId(rs.getInt("uId"));
					beans.add(issueDetails);
				} 
				return beans;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	@Override
	public List<BorrowedBooks> borrowedBook(int uId) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("select * from borrowed_books where uId=?");) {
				pstmt.setInt(1, uId);
				rs=pstmt.executeQuery();
				LinkedList<BorrowedBooks> beans = new LinkedList<BorrowedBooks>();
				while(rs.next()) {
					BorrowedBooks listOfbooksBorrowed = new BorrowedBooks();
					listOfbooksBorrowed.setuId(rs.getInt("uId"));
					listOfbooksBorrowed.setbId(rs.getInt("bId"));
					listOfbooksBorrowed.setEmail(rs.getString("email"));
					beans.add(listOfbooksBorrowed);
				} 
				return beans;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	@Override
	public LinkedList<BookBean> searchBookById(int bookId) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pstmt = conn.prepareStatement("select * from bookdetails where bookId=?");) {
				pstmt.setInt(1,bookId);
				rs=pstmt.executeQuery();
				LinkedList<BookBean> beans = new LinkedList<BookBean>();
				while(rs.next()) {
					BookBean bean = new BookBean();
					bean.setBookId(rs.getInt("bookId"));
					bean.setBookName(rs.getString("bookName"));
					bean.setAuthorName(rs.getString("authorName"));
					bean.setCategory(rs.getString("category"));
					bean.setBookPublications(rs.getString("bookPublications"));
					//bean.setCopies(rs.getInt("copies"));
					beans.add(bean);
				}
				return beans;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	@Override
	public LinkedList<RequestDetails> showRequests() {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					Statement stmt = (Statement)conn.createStatement();
					ResultSet rs = stmt.executeQuery("select * from request_details");) {
				LinkedList<RequestDetails> beans = new LinkedList<RequestDetails>();
				while(rs.next()) {
					RequestDetails bean = new RequestDetails();
					bean.setuId(rs.getInt("uId"));
					bean.setFullName(rs.getString("fullName"));
					bean.setbId(rs.getInt("bId"));
					bean.setBookName(rs.getString("bookName"));
					beans.add(bean);
				}
				return beans;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	@Override
	public LinkedList<BookIssueDetails> showIssuedBooks() {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					Statement stmt = (Statement)conn.createStatement();
					ResultSet rs = stmt.executeQuery("select * from book_issue");) {
				LinkedList<BookIssueDetails> beans = new LinkedList<BookIssueDetails>();
				while(rs.next()) {
					BookIssueDetails bean = new BookIssueDetails();
					bean.setBookId(rs.getInt("bookId"));
					bean.setUserId(rs.getInt("userId"));
					bean.setIssueDate(rs.getDate("issueDate"));
					bean.setReturnDate(rs.getDate("returnDate"));
					beans.add(bean);
				}
				return beans;
			}
		} catch(Exception e) {
			//e.printStackTrace();
			System.err.println(e.getMessage());
			return null;
		}
	}


	@Override
	public LinkedList<UserBean> showUsers() {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					Statement stmt = (Statement)conn.createStatement();
					ResultSet rs = stmt.executeQuery("select * from user");) {
				LinkedList<UserBean> beans = new LinkedList<UserBean>();
				while(rs.next()) {
					UserBean bean = new UserBean();
					bean.setuId(rs.getInt("uId"));
					bean.setFirstName(rs.getString("firstName"));
					bean.setLastName(rs.getString("lastName"));
					bean.setEmail(rs.getString("email"));
					bean.setPassword(rs.getString("password"));
					bean.setMobile(rs.getLong("mobile"));
					bean.setRole(rs.getString("role"));
					beans.add(bean);
				}
				return beans;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean updatePassword(String email, String password, String newPassword, String role) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			Class.forName(pro.getProperty("path"));
			try(Connection conn = DriverManager.getConnection(pro.getProperty("dburl"),pro);
					PreparedStatement pst = conn.prepareStatement("select * from user where email=? and role=?")){
				pst.setString(1, email);
				pst.setString(2, role);
				rs=pst.executeQuery();
				if(rs.next()) {
					try(PreparedStatement pstmt = conn.prepareStatement("update user set password=? where email=? and password=?");) {
						pstmt.setString(1, newPassword);
						pstmt.setString(2, email);
						pstmt.setString(3,password);
						int count=pstmt.executeUpdate();
						if(count!=0) {
							return true;
						} else {
							return false;
						}
					}
				}else {
					throw new LMSException("User doesnt exist");
				}
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	}

	
