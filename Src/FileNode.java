import java.util.regex.Pattern;

/**
 * Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * FileNode
 * The FileNode class represents a node in a file tree, storing information about a file or folder.
 */
public class FileNode {
    private String displayName;        // The name to display in the file tree
    private String fullPath;           // The full path to the file or folder
    private String userFriendlyPath;   // A user-friendly path with '~' representing the user's home directory

    /**
     * Constructor for the FileNode class.
     * Initializes the FileNode with a display name and full path.
     *
     * @param displayName The name to display in the file tree.
     * @param fullPath    The full path to the file or folder.
     */
    public FileNode(String displayName, String fullPath) {
        this.displayName = displayName;
        this.fullPath = fullPath;
    }

    /**
     * Constructor for the FileNode class.
     * Initializes the FileNode with a display name, full path, and a user-friendly path.
     *
     * @param displayName    The name to display in the file tree.
     * @param fullPath       The full path to the file or folder.
     * @param currentUserDir The current user's home directory.
     */
    public FileNode(String displayName, String fullPath, String currentUserDir) {
        this.displayName = displayName;
        this.fullPath = fullPath;
        this.userFriendlyPath = fullPath.replaceFirst(Pattern.quote(currentUserDir), "~");
    }

    /**
     * Get the display name of the FileNode.
     *
     * @return The display name.
     */
    @Override
    public String toString() {
        return displayName;  // This will be displayed in the JTree
    }

    /**
     * Get the full path to the file or folder represented by the FileNode.
     *
     * @return The full path.
     */
    public String getFullPath() {
        return fullPath;
    }
}
