package pattern.command;

import model.Loan;
import controller.LoanController;
import java.util.Date;

public class BorrowCommand implements Command {
    private LoanController controller;
    private Loan loan;
    
    public BorrowCommand(LoanController controller, Loan loan) {
        this.controller = controller;
        this.loan = loan;
    }
    
    @Override
    public void execute() throws Exception {
        controller.saveLoan(loan);
    }
    
    @Override
    public void undo() throws Exception {
        if (loan.getId() > 0) {
            controller.returnBook(loan.getId(), new Date());
        }
    }
}

public class ReturnCommand implements Command {
    private LoanController controller;
    private int loanId;
    private Loan originalLoan;
    
    public ReturnCommand(LoanController controller, int loanId) {
        this.controller = controller;
        this.loanId = loanId;
    }
    
    @Override
    public void execute() throws Exception {
        originalLoan = controller.findLoanById(loanId);
        controller.returnBook(loanId, new Date());
    }
    
    @Override
    public void undo() throws Exception {
        if (originalLoan != null) {
            controller.saveLoan(originalLoan);
        }
    }
}