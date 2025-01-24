package pattern.command;

import model.Book;
import controller.BookController;

public class AddBookCommand implements Command {
    private BookController controller;
    private Book book;
    
    public AddBookCommand(BookController controller, Book book) {
        this.controller = controller;
        this.book = book;
    }
    
    @Override
    public void execute() throws Exception {
        controller.saveBook(book);
    }
    
    @Override
    public void undo() throws Exception {
        controller.deleteBook(book.getId());
    }
}

public class DeleteBookCommand implements Command {
    private BookController controller;
    private Book book;
    
    public DeleteBookCommand(BookController controller, int bookId) {
        this.controller = controller;
        this.book = controller.findBookById(bookId);
    }
    
    @Override
    public void execute() throws Exception {
        controller.deleteBook(book.getId());
    }
    
    @Override
    public void undo() throws Exception {
        controller.saveBook(book);
    }
}