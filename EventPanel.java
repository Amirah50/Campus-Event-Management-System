//EventPanel.java
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EventPanel extends JPanel implements Observer {
    private EventManager eventManager;
    private RegistrationService registrationService;
    private JTextField nameField, dateField, venueField, capacityField, feeField;
    private JComboBox<EventType> typeCombo;
    private JTable eventTable, participantTable, billTable;
    private DefaultTableModel eventTableModel, participantTableModel, billTableModel;
    private JButton createBtn, updateBtn, deleteBtn, clearBtn, logoutBtn;
    private MainFrame mainFrame;
    private String currentSelectedEvent = null;

    public EventPanel(EventManager eventManager, RegistrationService registrationService, BillCalculator billCalculator) {
        this.eventManager = eventManager;
        this.registrationService = registrationService;
        this.eventManager.attach(this);
        this.registrationService.attach(this);

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(236, 240, 241));

        // === Main Content Split Pane ===
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(350);
        mainSplitPane.setResizeWeight(0.3);
        mainSplitPane.setBorder(null);
        
        // === Left Form Panel ===
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(new Color(149, 165, 166)),  // Fixed border creation
            "Event Details", 
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16), new Color(44, 62, 80)));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(350, 700));
        
        // Form Fields
        JPanel[] formRows = {
            createFormRow("Event Name:", nameField = new JTextField()),
            createFormRow("Date (YYYY-MM-DD):", dateField = new JTextField()),
            createFormRow("Venue:", venueField = new JTextField()),
            createFormRow("Type:", typeCombo = new JComboBox<>(EventType.values())),
            createFormRow("Capacity:", capacityField = new JTextField()),
            createFormRow("Fee (RM):", feeField = new JTextField())
        };
        
        for (JPanel row : formRows) {
            formPanel.add(row);
            formPanel.add(Box.createVerticalStrut(10));
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        createBtn = createStyledButton("Create", new Color(46, 204, 113));
        updateBtn = createStyledButton("Update", new Color(52, 152, 219));
        deleteBtn = createStyledButton("Delete", new Color(231, 76, 60));
        clearBtn = createStyledButton("Clear", new Color(149, 165, 166));
        logoutBtn = createStyledButton("Logout", new Color(155, 89, 182));
        
        buttonPanel.add(createBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(logoutBtn);
        
        formPanel.add(buttonPanel);
        
        // === Right Tables Panel ===
        JPanel tablesPanel = new JPanel(new BorderLayout(10, 10));
        
        // Events Table
        JPanel eventTablePanel = new JPanel(new BorderLayout());
        eventTablePanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(new Color(149, 165, 166)), 
            "Available Events", 
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16), new Color(44, 62, 80)));
        eventTablePanel.setBackground(Color.WHITE);
        
        String[] eventColumnNames = {"Name", "Date", "Venue", "Type", "Capacity", "Fee"};
        eventTableModel = new DefaultTableModel(eventColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        eventTable = new JTable(eventTableModel);
        configureTable(eventTable, new int[]{180, 100, 150, 80, 70, 80});
        
        JScrollPane eventScrollPane = new JScrollPane(eventTable);
        eventTablePanel.add(eventScrollPane, BorderLayout.CENTER);
        
        // Participants and Bills Split Pane
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        bottomSplitPane.setDividerLocation(250);
        bottomSplitPane.setResizeWeight(0.5);
        bottomSplitPane.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Participants Table
        JPanel participantPanel = new JPanel(new BorderLayout());
        participantPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(new Color(149, 165, 166)), 
            "Event Participants", 
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16), new Color(44, 62, 80)));
        participantPanel.setBackground(Color.WHITE);
        
        String[] participantColumnNames = {"User ID", "User Name", "Group Size", "Catering", "Transport"};
        participantTableModel = new DefaultTableModel(participantColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        participantTable = new JTable(participantTableModel);
        configureTable(participantTable, new int[]{100, 150, 80, 80, 80});
        
        participantPanel.add(new JScrollPane(participantTable), BorderLayout.CENTER);
        
        // Bills Table
        JPanel billPanel = new JPanel(new BorderLayout());
        billPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(new Color(149, 165, 166)), 
            "Participant Bills", 
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16), new Color(44, 62, 80)));
        billPanel.setBackground(Color.WHITE);
        
        String[] billColumnNames = {"Bill ID", "Date/Time", "User ID", "User Name", "Event", "Net Payable"};
        billTableModel = new DefaultTableModel(billColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        billTable = new JTable(billTableModel);
        configureTable(billTable, new int[]{120, 150, 80, 150, 200, 100});
        
        billPanel.add(new JScrollPane(billTable), BorderLayout.CENTER);
        
        bottomSplitPane.setTopComponent(participantPanel);
        bottomSplitPane.setBottomComponent(billPanel);
        
        tablesPanel.add(eventTablePanel, BorderLayout.NORTH);
        tablesPanel.add(bottomSplitPane, BorderLayout.CENTER);
        
        mainSplitPane.setLeftComponent(formPanel);
        mainSplitPane.setRightComponent(tablesPanel);
        add(mainSplitPane, BorderLayout.CENTER);

        // Action Listeners
        createBtn.addActionListener(this::createEvent);
        updateBtn.addActionListener(this::updateEvent);
        deleteBtn.addActionListener(this::deleteEvent);
        clearBtn.addActionListener(e -> clearForm());
        
        logoutBtn.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.logout();
            }
        });

        eventTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && eventTable.getSelectedRow() != -1) {
                int selectedRow = eventTable.getSelectedRow();
                currentSelectedEvent = (String) eventTableModel.getValueAt(selectedRow, 0);
                displayEventDetails(selectedRow);
                updateParticipantTable(currentSelectedEvent);
                updateBillTable(currentSelectedEvent);
            }
        });

        loadEvents();
    }
    
    private JPanel createFormRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(10, 5));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setPreferredSize(new Dimension(250, 30));
        } else {
            field.setPreferredSize(new Dimension(250, 30));
        }
        
        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        
        return row;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return button;
    }
    
    private void configureTable(JTable table, int[] colWidths) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Set column widths
        TableColumnModel colModel = table.getColumnModel();
        for (int i = 0; i < colWidths.length; i++) {
            colModel.getColumn(i).setPreferredWidth(colWidths[i]);
        }
    }
    
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    @Override
    public void update() {
        loadEvents();
        if (currentSelectedEvent != null) {
            updateParticipantTable(currentSelectedEvent);
            updateBillTable(currentSelectedEvent);
        }
    }

    private void createEvent(ActionEvent e) {
        try {
            String name = nameField.getText();
            LocalDate date = LocalDate.parse(dateField.getText());
            String venue = venueField.getText();
            EventType type = (EventType) typeCombo.getSelectedItem();
            int capacity = Integer.parseInt(capacityField.getText());
            double fee = Double.parseDouble(feeField.getText());

            if (name.isEmpty() || venue.isEmpty()) {
                showError("Event Name and Venue cannot be empty.");
                return;
            }
            if (eventManager.getEventByName(name) != null) {
                showError("An event with this name already exists.");
                return;
            }

            Event newEvent = new Event(name, date, venue, type, capacity, fee);
            eventManager.createEvent(newEvent);
            
            JOptionPane.showMessageDialog(this, "Event created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Please use YYYY-MM-DD.");
        } catch (NumberFormatException ex) {
            showError("Invalid number format for Capacity or Fee.");
        } catch (Exception ex) {
            showError("Error creating event: " + ex.getMessage());
        }
    }

    private void updateEvent(ActionEvent e) {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an event to update.");
            return;
        }

        try {
            String originalName = (String) eventTableModel.getValueAt(selectedRow, 0);
            String name = nameField.getText();
            LocalDate date = LocalDate.parse(dateField.getText());
            String venue = venueField.getText();
            EventType type = (EventType) typeCombo.getSelectedItem();
            int capacity = Integer.parseInt(capacityField.getText());
            double fee = Double.parseDouble(feeField.getText());

            if (name.isEmpty() || venue.isEmpty()) {
                showError("Event Name and Venue cannot be empty.");
                return;
            }
            
            if (!originalName.equalsIgnoreCase(name) && eventManager.getEventByName(name) != null) {
                showError("An event with the new name already exists.");
                return;
            }

            Event updatedEvent = new Event(name, date, venue, type, capacity, fee);
            if (eventManager.updateEvent(originalName, updatedEvent)) {
                JOptionPane.showMessageDialog(this, "Event updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                showError("Event not found for update.");
            }
        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Please use YYYY-MM-DD.");
        } catch (NumberFormatException ex) {
            showError("Invalid number format for Capacity or Fee.");
        } catch (Exception ex) {
            showError("Error updating event: " + ex.getMessage());
        }
    }

    private void deleteEvent(ActionEvent e) {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an event to delete.");
            return;
        }

        String name = (String) eventTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete event: " + name + "?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (eventManager.deleteEvent(name)) {
                JOptionPane.showMessageDialog(this, "Event deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                currentSelectedEvent = null;
            } else {
                showError("Error deleting event.");
            }
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public void loadEvents() {
        eventTableModel.setRowCount(0);
        for (Event event : eventManager.getAllEvents()) {
            eventTableModel.addRow(new Object[]{
                event.getName(),
                event.getDate().toString(),
                event.getVenue(),
                event.getType(),
                event.getCapacity(),
                String.format("RM%.2f", event.getRegistrationFee())
            });
        }
    }

    private void displayEventDetails(int selectedRow) {
        nameField.setText((String) eventTableModel.getValueAt(selectedRow, 0));
        dateField.setText((String) eventTableModel.getValueAt(selectedRow, 1));
        venueField.setText((String) eventTableModel.getValueAt(selectedRow, 2));
        typeCombo.setSelectedItem(EventType.valueOf(eventTableModel.getValueAt(selectedRow, 3).toString()));
        capacityField.setText(eventTableModel.getValueAt(selectedRow, 4).toString());
        String feeString = (String) eventTableModel.getValueAt(selectedRow, 5);
        feeField.setText(feeString.replace("RM", ""));
    }

    private void clearForm() {
        nameField.setText("");
        dateField.setText("");
        venueField.setText("");
        capacityField.setText("");
        feeField.setText("");
        typeCombo.setSelectedIndex(0);
        eventTable.clearSelection();
        participantTableModel.setRowCount(0);
        billTableModel.setRowCount(0);
        currentSelectedEvent = null;
    }

    private void updateParticipantTable(String eventName) {
        participantTableModel.setRowCount(0);
        for (Registration reg : registrationService.getRegistrationsForEvent(eventName)) {
            participantTableModel.addRow(new Object[]{
                reg.getUser().getUserId(),
                reg.getUser().getName(),
                reg.getGroupSize(),
                reg.isCateringSelected() ? "Yes" : "No",
                reg.isTransportSelected() ? "Yes" : "No"
            });
        }
    }

    private void updateBillTable(String eventName) {
        billTableModel.setRowCount(0);
        for (Registration reg : registrationService.getRegistrationsForEvent(eventName)) {
            Bill bill = reg.getBill();
            if (bill != null) {
                billTableModel.addRow(new Object[]{
                    bill.getBillId().substring(0, 8) + "...",
                    bill.getBillDateTime().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd HH:mm")),
                    reg.getUser().getUserId(),
                    reg.getUser().getName(),
                    reg.getEvent().getName(),
                    String.format("RM%.2f", bill.getNetPayable())
                });
            }
        }
    }
}