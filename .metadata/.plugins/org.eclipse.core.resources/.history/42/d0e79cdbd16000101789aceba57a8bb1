package myrmi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Vector; // Consider using ArrayList unless explicit thread-safety on shared list is needed

public class ClientUI extends JFrame {

    private FileTransferClient client;

    private JList<String> fileList;
    private DefaultListModel<String> fileListModel;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JButton uploadButton;
    private JButton downloadButton;
    private JButton refreshButton;
    private JTextField downloadPathField;
    private JButton browseDownloadPathButton;

    public ClientUI(String host, int port) {
        super("Distributed File Manager (RMI)");
        try {
            client = new FileTransferClient(host, port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to RMI services: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initComponents();
        setupLayout();
        addListeners();
        loadFiles(); // Load files on startup
    }

    private void initComponents() {
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        fileList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Settings for Grid View
        fileList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        fileList.setVisibleRowCount(-1);
        fileList.setFixedCellWidth(150); // Adjust as needed for your icon/text size
        fileList.setFixedCellHeight(80); // Adjust as needed for your icon/text size

        fileList.setCellRenderer(new FileListCellRenderer()); // Custom Renderer

        uploadButton = new JButton("Upload File");
        downloadButton = new JButton("Download Selected");
        refreshButton = new JButton("Refresh List");
        browseDownloadPathButton = new JButton("Browse");

        customizeButton(uploadButton);
        customizeButton(downloadButton);
        customizeButton(refreshButton);
        customizeButton(browseDownloadPathButton);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        statusLabel = new JLabel("Ready.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        downloadPathField = new JTextField(System.getProperty("user.home") + File.separator + "Downloads");
        downloadPathField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        downloadPathField.setEditable(false);
    }

    private void customizeButton(JButton button) {
        button.setBackground(new Color(60, 140, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 150, 210));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 140, 200));
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topControlsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 0: Action Buttons
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topControlsPanel.add(uploadButton, gbc);

        gbc.gridx = 1;
        topControlsPanel.add(downloadButton, gbc);

        gbc.gridx = 2;
        topControlsPanel.add(refreshButton, gbc);
        
        // Row 1: Download Path
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        topControlsPanel.add(new JLabel("Download To:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topControlsPanel.add(downloadPathField, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        topControlsPanel.add(browseDownloadPathButton, gbc);

        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Available Files"
        ));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(progressBar);
        bottomPanel.add(statusLabel);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(topControlsPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
    }

    private void addListeners() {
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select File to Upload");
                int returnValue = fileChooser.showOpenDialog(ClientUI.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        uploadFile(selectedFile);
                    }
                }
            }
        });

        browseDownloadPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser dirChooser = new JFileChooser();
                dirChooser.setDialogTitle("Select Download Directory");
                dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                dirChooser.setAcceptAllFileFilterUsed(false);
                
                File currentDir = new File(downloadPathField.getText());
                if (currentDir.exists() && currentDir.isDirectory()) {
                    dirChooser.setCurrentDirectory(currentDir);
                } else {
                    dirChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                }

                int returnValue = dirChooser.showSaveDialog(ClientUI.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = dirChooser.getSelectedFile();
                    if (selectedDirectory != null) {
                        downloadPathField.setText(selectedDirectory.getAbsolutePath());
                    }
                }
            }
        });

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFile = fileList.getSelectedValue();
                if (selectedFile == null || selectedFile.equals("No files available.")) {
                    JOptionPane.showMessageDialog(ClientUI.this, "Please select a valid file to download.", "No File Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String downloadDir = downloadPathField.getText();
                File dir = new File(downloadDir);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        JOptionPane.showMessageDialog(ClientUI.this, "Cannot create download directory: " + downloadDir, "Directory Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else if (!dir.isDirectory()) {
                    JOptionPane.showMessageDialog(ClientUI.this, "Download path is a file, not a directory: " + downloadDir, "Directory Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                downloadFile(selectedFile, downloadDir);
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFiles();
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (client != null) {
                    client.shutdown();
                }
                System.exit(0);
            }
        });
    }

    private void loadFiles() {
        statusLabel.setText("Loading files...");
        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                List<String> files = client.listAvailableFiles();
                Collections.sort(files, String.CASE_INSENSITIVE_ORDER);
                return files;
            }

            @Override
            protected void done() {
                try {
                    List<String> files = get();
                    fileListModel.clear();
                    if (files.isEmpty()) {
                        fileListModel.addElement("No files available.");
                    } else {
                        for (String file : files) {
                            fileListModel.addElement(file);
                        }
                    }
                    statusLabel.setText("Files loaded.");
                } catch (Exception e) {
                    statusLabel.setText("Error loading files.");
                    JOptionPane.showMessageDialog(ClientUI.this, "Error loading files: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void uploadFile(File file) {
        statusLabel.setText("Uploading '" + file.getName() + "'...");
        progressBar.setValue(0);
        progressBar.setVisible(true);
        setButtonsEnabled(false);

        client.uploadFile(file,
            progress -> SwingUtilities.invokeLater(() -> progressBar.setValue(progress)),
            () -> SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Upload of '" + file.getName() + "' complete!");
                progressBar.setVisible(false);
                setButtonsEnabled(true);
                loadFiles();
            }),
            error -> SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Upload failed for '" + file.getName() + "'.");
                progressBar.setVisible(false);
                setButtonsEnabled(true);
                JOptionPane.showMessageDialog(ClientUI.this, "Upload failed: " + error.getMessage(), "Upload Error", JOptionPane.ERROR_MESSAGE);
            })
        );
    }

    private void downloadFile(String fileName, String outputDir) {
        statusLabel.setText("Downloading '" + fileName + "' to " + outputDir + "...");
        progressBar.setValue(0);
        progressBar.setVisible(true);
        setButtonsEnabled(false);

        client.downloadFile(fileName, outputDir,
            progress -> SwingUtilities.invokeLater(() -> progressBar.setValue(progress)),
            () -> SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Download of '" + fileName + "' complete!");
                progressBar.setVisible(false);
                setButtonsEnabled(true);
            }),
            error -> SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Download failed for '" + fileName + "'.");
                progressBar.setVisible(false);
                setButtonsEnabled(true);
                JOptionPane.showMessageDialog(ClientUI.this, "Download failed: " + error.getMessage(), "Download Error", JOptionPane.ERROR_MESSAGE);
            })
        );
    }

    private void setButtonsEnabled(boolean enabled) {
        uploadButton.setEnabled(enabled);
        downloadButton.setEnabled(enabled);
        refreshButton.setEnabled(enabled);
        browseDownloadPathButton.setEnabled(enabled);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientUI frame = new ClientUI("localhost", 1099);
            frame.setVisible(true);
        });
    }

    class FileListCellRenderer extends DefaultListCellRenderer {
        private Icon defaultIcon;
        private Icon documentIcon;
        private Icon imageIcon;
        private Icon audioIcon;
        private Icon videoIcon;

        public FileListCellRenderer() {
            try {
                // Adjust paths if your icons are in a different location (e.g., /icons/file_default.png)
                defaultIcon = new ImageIcon(getClass().getResource("/icons/file_default.png"));
                documentIcon = new ImageIcon(getClass().getResource("/icons/file_document.png"));
                imageIcon = new ImageIcon(getClass().getResource("/icons/file_image.png"));
                audioIcon = new ImageIcon(getClass().getResource("/icons/file_audio.png"));
                videoIcon = new ImageIcon(getClass().getResource("/icons/file_video.png"));
            } catch (Exception e) {
                System.err.println("Error loading custom icons: " + e.getMessage() + ". Falling back to generic icons.");
                // Fallback to generic system icons if custom ones are not found
                defaultIcon = UIManager.getIcon("FileView.fileIcon");
                documentIcon = UIManager.getIcon("FileView.textIcon");
                imageIcon = UIManager.getIcon("FileView.hardDriveIcon");
                audioIcon = UIManager.getIcon("FileView.floppyDriveIcon");
                videoIcon = UIManager.getIcon("FileView.computerIcon");
            }
            // Ensure icons are not null, if UIManager fallback also fails
            if (defaultIcon == null) defaultIcon = new ImageIcon(new byte[0]); // Empty icon
            if (documentIcon == null) documentIcon = defaultIcon;
            if (imageIcon == null) imageIcon = defaultIcon;
            if (audioIcon == null) audioIcon = defaultIcon;
            if (videoIcon == null) videoIcon = defaultIcon;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            String fileName = (String) value;
            if (fileName == null || fileName.isEmpty() || fileName.equals("No files available.")) {
                label.setIcon(null);
                label.setText(fileName);
                label.setHorizontalTextPosition(SwingConstants.RIGHT);
                label.setVerticalTextPosition(SwingConstants.CENTER);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setPreferredSize(null);
                label.setBorder(null);
            } else {
                Icon icon = getIconForFileExtension(fileName);
                label.setIcon(icon);
                label.setText(fileName);
                
                // --- MODIFIED for better Grid View Layout ---
                label.setHorizontalTextPosition(SwingConstants.CENTER);
                label.setVerticalTextPosition(SwingConstants.BOTTOM);
                
                label.setPreferredSize(new Dimension(fileList.getFixedCellWidth(), fileList.getFixedCellHeight()));
                
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER); // Centered vertically within the fixed height cell
                
                label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
                // --- END MODIFIED ---
            }
            
            // Apply selection colors last
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }

            return label;
        }

        private Icon getIconForFileExtension(String fileName) {
            String lowerCaseFileName = fileName.toLowerCase();
            if (lowerCaseFileName.endsWith(".txt") || lowerCaseFileName.endsWith(".doc") ||
                lowerCaseFileName.endsWith(".docx") || lowerCaseFileName.endsWith(".pdf") ||
                lowerCaseFileName.endsWith(".rtf")) {
                return documentIcon;
            } else if (lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg") ||
                       lowerCaseFileName.endsWith(".png") || lowerCaseFileName.endsWith(".gif") ||
                       lowerCaseFileName.endsWith(".bmp") || lowerCaseFileName.endsWith(".tiff")) {
                return imageIcon;
            } else if (lowerCaseFileName.endsWith(".mp3") || lowerCaseFileName.endsWith(".wav") ||
                       lowerCaseFileName.endsWith(".aac") || lowerCaseFileName.endsWith(".flac")) {
                return audioIcon;
            } else if (lowerCaseFileName.endsWith(".mp4") || lowerCaseFileName.endsWith(".avi") ||
                       lowerCaseFileName.endsWith(".mov") || lowerCaseFileName.endsWith(".wmv") ||
                       lowerCaseFileName.endsWith(".mkv")) {
                return videoIcon;
            }
            return defaultIcon;
        }
    }
}