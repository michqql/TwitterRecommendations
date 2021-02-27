package me.mpearso.twitter.data;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class DataFile {

    protected final String path, fileName;
    protected File directory, file;
    private final Extension extension;

    public DataFile(String path, String fileName, Extension extension) {
        this.path = path;
        this.fileName = fileName;
        this.extension = extension;

        preInit();
        init();
    }

    /**
     * Called once when the object is first initialized
     * Used to create the files and directories
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void preInit() {
        // directory path specified
        // create directory file
        if(!this.path.isEmpty()) {
            this.directory = new File(path);
            if(!directory.exists())
                directory.mkdirs();

            this.file = new File(directory,
                    fileName + "." + extension.toString());
        } else {
            this.file = new File(fileName + "." + extension.toString());
        }

        if(!this.file.exists()) {
            try {
                file.createNewFile();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Only gets called once when the file is first created
     * Should be overridden to provide a specific copy of a file
     */
    protected void copy() {}

    /**
     * Deletes the directory, and therefore any files inside
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void delete() {
        try {
            if(Objects.requireNonNull(directory.listFiles()).length > 1) {
                this.file.delete();
            } else {
                this.directory.delete();
            }
        } catch(NullPointerException e) {
            this.directory.delete();
        }
    }

    public File getFile() {
        return file;
    }

    public String getFullPath() {
        return file.getAbsolutePath();
    }

    protected abstract void init();
    public abstract void save();

    protected enum Extension {
        TXT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
}
