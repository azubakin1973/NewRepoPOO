package model;

import java.util.Date;

public class Loan {
    private int id;
    private int bookId;
    private int memberId;
    private Date loanDate;
    private Date expectedReturnDate;
    private Date actualReturnDate;
    
    
    private static final int MAX_LOAN_DAYS = 30;
    private static final double DAILY_LATE_FEE = 1.0; // 1 euro por dia
    
    // Constructor
    public Loan(int bookId, int memberId, Date loanDate, Date expectedReturnDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.expectedReturnDate = expectedReturnDate;
    }
    
    public double calculateLateFees() {
        if (actualReturnDate == null || !isLate()) {
            return 0.0;
        }
        
        long diffInMillies = actualReturnDate.getTime() - expectedReturnDate.getTime();
        long daysLate = diffInMillies / (1000 * 60 * 60 * 24);
        return daysLate * DAILY_LATE_FEE;
    }
    
    public boolean isLate() {
        if (actualReturnDate == null) {
            // Se ainda não foi devolvido, verifica se a data atual é posterior à data prevista
            return new Date().after(expectedReturnDate);
        }
        return actualReturnDate.after(expectedReturnDate);
    }
    
    public boolean isValidLoanPeriod() {
        long diffInMillies = expectedReturnDate.getTime() - loanDate.getTime();
        long days = diffInMillies / (1000 * 60 * 60 * 24);
        return days <= MAX_LOAN_DAYS;
    }
    
    
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getBookId() {
        return bookId;
    }
    
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    
    public int getMemberId() {
        return memberId;
    }
    
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    public Date getLoanDate() {
        return loanDate;
    }
    
    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }
    
    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }
    
    public void setExpectedReturnDate(Date expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }
    
    public Date getActualReturnDate() {
        return actualReturnDate;
    }
    
    public void setActualReturnDate(Date actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }
}
