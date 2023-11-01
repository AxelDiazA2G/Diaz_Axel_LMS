import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.ExpandVetoException;

/**
 * Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * TestCaseSelector
 * The TestCaseSelector class provides a user interface for selecting test case files
 * from a directory using a JTree component. It allows users to browse through folders,
 * select test case files, and continue with the selected file for further processing.
 */
public class TestCaseSelector {
    private JPanel panel1;
    private JTree tree1;
    private JTextField pathTextField;
    private JButton continueButton;
    private File selectedFile;

    private File fileHolder;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Constructor for the TestCaseSelector class.
     * Initializes the user interface components and sets up event listeners.
     */
    public TestCaseSelector() {
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

        // Add to the main panel
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

        File fileRoot = new File("./Test Cases");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileNode(fileRoot.getName(), fileRoot.getAbsolutePath()));
        tree1.setModel(new DefaultTreeModel(root));

        continueButton.addActionListener(e -> {
            selectedFile = fileHolder;
        });

        // Mouse listener for expanding and selecting files
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

        // TreeWillExpandListener to populate nodes on expand
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

        // Populate the root node with initial content
        populateNode(root, fileRoot);
    }

    /**
     * CustomTreeNode
     * CustomTreeNode extends DefaultMutableTreeNode to provide custom behavior
     * for determining whether a node is a leaf node or not based on the associated file.
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
     * Populates a tree node with child nodes representing files and folders.
     *
     * @param node The node to populate.
     * @param file The file or folder associated with the node.
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
     * Returns the main panel of the TestCaseSelector.
     *
     * @return The main panel.
     */
    public JPanel getPanel1() {
        return panel1;
    }

    /**
     * Gets the selected test case file.
     *
     * @return The selected test case file.
     */
    public File getSelectedFile() {
        return selectedFile;
    }
}
