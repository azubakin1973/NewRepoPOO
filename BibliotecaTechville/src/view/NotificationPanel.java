package view;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationPanel extends JPanel {
    private DefaultListModel<String> notificationModel;
    private JList<String> notificationList;
    
    public NotificationPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Notifications"));
        
        notificationModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationModel);
        
        JScrollPane scrollPane = new JScrollPane(notificationList);
        add(scrollPane, BorderLayout.CENTER);
        
        JButton clearButton = new JButton("Effacer");
        clearButton.addActionListener(e -> notificationModel.clear());
        add(clearButton, BorderLayout.SOUTH);
    }
    
    public void addNotification(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timeStamp = sdf.format(new Date());
        notificationModel.addElement("[" + timeStamp + "] " + message);
        notificationList.ensureIndexIsVisible(notificationModel.getSize() - 1);
    }
}