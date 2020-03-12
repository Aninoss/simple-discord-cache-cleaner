public class FileExt {
    private String mimeType, ext;

    public FileExt(String mimeType, String ext) {
        this.mimeType = mimeType;
        this.ext = ext;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExt() {
        return ext;
    }
}
