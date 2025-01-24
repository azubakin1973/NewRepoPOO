package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import controller.*;
import pattern.composite.Category;
import pattern.composite.BookItem;
import pattern.command.CommandHistory;

public class MainWindow extends JFrame {
    private JPanel contentPanel;
    private BookController bookController;
    private MemberController memberController;
    private LoanController loanController;
    private CommandHistory commandHistory;
    private CategoryTreePanel categoryTreePanel;
    private JSplitPane splitPane;
    private NotificationPanel notificationPanel;
    
    public MainWindow() {
        initializeControllers();
        initializeUI();
        setupLayout();
    }
    
    private void initializeControllers() {
        bookController = new BookController();
        memberController = new MemberController();
        loanController = new LoanController();
        commandHistory = new CommandHistory();
    }
    
    private void initializeUI() {
        setTitle("BiblioTechville");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        
        contentPanel = new JPanel(new BorderLayout());
        categoryTreePanel = new CategoryTreePanel();
        
        createMenuBar();
    }
    
    private void setupLayout() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(categoryTreePanel);
        
        // Painel inicial (BookPanel por padrão)
        BookPanel bookPanel = new BookPanel(commandHistory); // Passando commandHistory
        splitPane.setRightComponent(bookPanel);
        splitPane.setDividerLocation(250);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(splitPane, BorderLayout.CENTER);

        notificationPanel = new NotificationPanel();
        notificationPanel.setPreferredSize(new Dimension(0, 150));
        rightPanel.add(notificationPanel, BorderLayout.SOUTH);

        add(rightPanel);
    }

    private void switchPanel(String panelType) {
        JPanel newPanel = null;
        
        switch (panelType) {
            case "books":
                newPanel = new BookPanel(commandHistory);
                break;
            case "members":
                newPanel = new MemberPanel();
                break;
            case "loans":
                newPanel = new LoanPanel(commandHistory);
                break;
        }
        
        if (newPanel != null) {
            splitPane.setRightComponent(newPanel);
            splitPane.revalidate();
            splitPane.repaint();
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Gestion
        JMenu menuGestion = new JMenu("Gestion");
        menuGestion.setMnemonic(KeyEvent.VK_G);
        
        addMenuItem(menuGestion, "Livres", KeyEvent.VK_L, e -> switchPanel("books"));
        addMenuItem(menuGestion, "Membres", KeyEvent.VK_M, e -> switchPanel("members"));
        addMenuItem(menuGestion, "Emprunts", KeyEvent.VK_E, e -> switchPanel("loans"));
        
        menuBar.add(menuGestion);
        
        // Menu Edition (para Undo/Redo)
        JMenu menuEdition = new JMenu("Edition");
        menuEdition.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem undoItem = new JMenuItem("Annuler");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        undoItem.addActionListener(e -> {
            commandHistory.undo();
            refreshCurrentPanel();
        });
        
        JMenuItem redoItem = new JMenuItem("Refaire");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        redoItem.addActionListener(e -> {
            commandHistory.redo();
            refreshCurrentPanel();
        });
        
        menuEdition.add(undoItem);
        menuEdition.add(redoItem);
        menuBar.add(menuEdition);
        
        setJMenuBar(menuBar);
    }
    
    private void addMenuItem(JMenu menu, String label, int mnemonic, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.setMnemonic(mnemonic);
        menuItem.addActionListener(listener);
        menu.add(menuItem);
    }
    
    private void switchPanel(String panelType) {
        JPanel newPanel = null;
        
        switch (panelType) {
            case "books":
                newPanel = new BookPanel();
                break;
            case "members":
                newPanel = new MemberPanel();
                break;
            case "loans":
                newPanel = new LoanPanel();
                break;
        }
        
        if (newPanel != null) {
            splitPane.setRightComponent(newPanel);
            splitPane.revalidate();
            splitPane.repaint();
        }
    }
    
    private void refreshCurrentPanel() {
        Component currentPanel = splitPane.getRightComponent();
        if (currentPanel instanceof BookPanel) {
            ((BookPanel) currentPanel).loadBooks();
        } else if (currentPanel instanceof MemberPanel) {
            ((MemberPanel) currentPanel).loadMembers();
        } else if (currentPanel instanceof LoanPanel) {
            ((LoanPanel) currentPanel).loadLoans();
        }
    }
    

    public void showNotification(String message) {
        if (notificationPanel != null) {
            notificationPanel.addNotification(message);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}