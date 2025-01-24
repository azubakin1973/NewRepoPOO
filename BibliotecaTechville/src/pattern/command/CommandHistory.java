package pattern.command;

import java.util.Stack;

public class CommandHistory {
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    
    public CommandHistory() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }
    
    public void executeCommand(Command command) throws Exception {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Limpa o histórico de redo após nova ação
    }
    
    public void undo() throws Exception {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }
    
    public void redo() throws Exception {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
    
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}