import java.util.regex.Pattern;

public class FileNode {
    private String displayName;
    private String fullPath;
    private String userFriendlyPath;

    public FileNode(String displayName, String fullPath) {
        this.displayName = displayName;
        this.fullPath = fullPath;
    }

    public FileNode(String displayName, String fullPath, String currentUserDir) {
        this.displayName = displayName;
        this.fullPath = fullPath;
        this.userFriendlyPath = fullPath.replaceFirst(Pattern.quote(currentUserDir), "~");
    }

    @Override
    public String toString() {
        return displayName;  // This will be displayed in the JTree
    }

    public String getFullPath() {
        return fullPath;
        
    }
}
