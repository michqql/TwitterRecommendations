package me.mpearso.twitter.io;

import java.io.File;
import java.io.IOException;

public abstract class FileWrapper {

    protected final String path;
    protected File file;

    /**
     * A wrapper class of a java file object
     * Provides useful methods such as copying a default resource, loading and saving the resource
     * Abstract - specific file type implementation must be used
     *
     * Initialises the java file object and calls the abstract load method
     *
     * @param path The location at which this file is located
     *             Always starts from the folder in which this program is running
     */
    public FileWrapper(final String path) {
        this.path = path;

        init();
        load();
    }

    /**
     * Called once when the object is first instantiated
     * Initialise the java file object, creating it if it did not exist
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void init() {
        // Initialise java file object
        this.file = new File(path);

        // Check if the parent directory exists, if not then create it
        File directory = file.getParentFile();
        if(directory != null && !directory.exists()) {
            // mkdirs() method creates the directory
            directory.mkdirs();
        }

        // If the file doesn't exist, create it
        // Catches and handles any errors that occur as a result of this operation
        if(!file.exists()) {
            try {
                file.createNewFile();

                // If file specific implementation requires a default resource
                // call copy method in attempt to copy
                copy();
            } catch (IOException e) {
                // Print the error to console for debugging purposes
                System.out.println("[Error] Could not initialise file @ " + path);
                e.printStackTrace();
            }
        }
    }

    /**
     * Only gets called once when the file is first created
     * Should be overridden to provide implementation specific code to copy default resource
     */
    protected void copy() {}

    /**
     * Deletes the file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void delete() {
        this.file.delete();
    }

    /**
     * Returns the java file object
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets the path to the location at which this file is located
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /* Abstract methods for file type specific implementation */
    protected abstract void load();
    public abstract void save();
}
