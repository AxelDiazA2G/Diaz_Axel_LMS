import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.ExpandVetoException;

/**
 * Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * FileExplorer
 * The FileExplorer class provides a user interface for browsing and selecting files and folders.
 */
public class FileExplorer {
    private JPanel panel1;
    private JTree tree1;
    private JTextField pathTextField;
    private JButton continueButton;
    private File selectedFile;

    private File fileHolder;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Constructor for the FileExplorer class.
     * Initializes the user interface components and sets up event listeners.
     */
    public FileExplorer() {
        // Main panel settings
        panel1 = new JPanel(new BorderLayout(30, 30));
        panel1.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel1.setBackground(new Color(240, 248, 255));

        // Title label
        JLabel titleLabel = new JLabel("Test Case Selector");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 30));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel1.add(titleLabel, BorderLayout.NORTH);

        // JTree for file listing
        tree1 = new JTree();
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBorder(new TitledBorder(new EmptyBorder(15, 15, 15, 15), "File List",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Verdana", Font.BOLD, 24), Color.DARK_GRAY));
        JScrollPane treeScrollPane = new JScrollPane(tree1);
        treeScrollPane.setPreferredSize(new Dimension(600, 400));
        treePanel.add(treeScrollPane, BorderLayout.CENTER);

        // South Panel for text field and button
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(new TitledBorder(new EmptyBorder(15, 15, 15, 15), "Select Test Case File",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Verdana", Font.BOLD, 24), Color.DARK_GRAY));
        pathTextField = new JTextField();
        pathTextField.setFont(new Font("Verdana", Font.PLAIN, 15));
        pathTextField.setBorder(BorderFactory.createCompoundBorder(
                pathTextField.getBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Verdana", Font.BOLD, 24));
        continueButton.setBackground(new Color(100, 149, 237));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        southPanel.add(pathTextField, BorderLayout.CENTER);
        southPanel.add(continueButton, BorderLayout.EAST);

        // Add to main panel
        panel1.add(treePanel, BorderLayout.CENTER);
        panel1.add(southPanel, BorderLayout.SOUTH);

        // Update the text field when a node in the tree is selected
        tree1.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree1.getLastSelectedPathComponent();
            if (node == null) return;
            Object nodeInfo = node.getUserObject();
            if (nodeInfo instanceof FileNode) {
                FileNode fileNode = (FileNode) nodeInfo;
                pathTextField.setText(fileNode.getFullPath());
            }
        });

        // Update the tree when the text in the text field is edited
        pathTextField.addActionListener(e -> {
            String path = pathTextField.getText();
            File file = new File(path);
            if (!file.isDirectory()) {
                JOptionPane.showMessageDialog(panel1, "Invalid folder path!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree1.getModel().getRoot();
            root.removeAllChildren();
            populateNode(root, file);

            // Expand the root node to display the new folder
            tree1.expandRow(0);
        });

        String currentUserDir = System.getProperty("user.home");
        File fileRoot = new File(currentUserDir);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileNode(fileRoot.getName(), fileRoot.getAbsolutePath()));
        tree1.setModel(new DefaultTreeModel(root));

        continueButton.addActionListener(e -> {
            selectedFile = fileHolder;
        });
        tree1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selRow = tree1.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree1.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    if (node.getUserObject() instanceof FileNode) {
                        FileNode fileNode = (FileNode) node.getUserObject();
                        File file = new File(fileNode.getFullPath());
                        if (file.isDirectory()) {
                            if (tree1.isExpanded(selRow)) {
                                tree1.collapseRow(selRow);
                            } else {
                                tree1.expandRow(selRow);
                            }
                        } else {
                            fileHolder = file;
                        }
                    }
                }
            }
        });

        // New TreeWillExpandListener
        tree1.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                TreePath path = event.getPath();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.getUserObject() instanceof FileNode) {
                    FileNode fileNode = (FileNode) node.getUserObject();
                    File file = new File(fileNode.getFullPath());
                    if (file.isDirectory()) {
                        populateNode(node, file);  // Repopulate node on expand
                    }
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) {
                // Do nothing on collapse
            }
        });

        populateNode(root, fileRoot);
    }

    /**
     * CustomTreeNode
     * A custom tree node class that extends DefaultMutableTreeNode.
     * Provides custom behavior for determining if a node is a leaf.
     */
    public static class CustomTreeNode extends DefaultMutableTreeNode {
        public CustomTreeNode(Object userObject) {
            super(userObject);
        }

        @Override
        public boolean isLeaf() {
            Object obj = getUserObject();
            if (obj instanceof FileNode) {
                FileNode fileNode = (FileNode) obj;
                File file = new File(fileNode.getFullPath());
                return !file.isDirectory();
            }
            return super.isLeaf();
        }
    }

    /**
     * Populate the given node with child nodes representing files and folders within the specified directory.
     *
     * @param node The parent node to populate.
     * @param file The directory to scan for files and folders.
     */
    private void populateNode(DefaultMutableTreeNode node, File file) {
        executorService.submit(() -> {
            if (file.isDirectory()) {
                // Add a dummy node so the folder appears expandable
                DefaultMutableTreeNode dummyNode = new DefaultMutableTreeNode("Loading...");
                SwingUtilities.invokeLater(() -> {
                    node.add(dummyNode);
                    ((DefaultTreeModel) tree1.getModel()).nodeStructureChanged(node);
                });

                // Load actual content
                File[] files = file.listFiles();
                if (files != null) {
                    SwingUtilities.invokeLater(() -> node.removeAllChildren()); // Remove dummy node
                    for (File f : files) {
                        CustomTreeNode childNode = new CustomTreeNode(new FileNode(f.getName(), f.getAbsolutePath()));
                        SwingUtilities.invokeLater(() -> {
                            node.add(childNode);
                            ((DefaultTreeModel) tree1.getModel()).nodeStructureChanged(node);
                        });
                    }
                }
            }
        });
    }

    /**
     * Get the main panel of the FileExplorer.
     *
     * @return The main panel.
     */
    public JPanel getPanel1() {
        return panel1;
    }

    /**
     * Get the selected file or folder.
     *
     * @return The selected file or folder.
     */
    public File getSelectedFile() {
        return selectedFile;
    }
}
