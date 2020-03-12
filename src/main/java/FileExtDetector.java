import org.apache.tika.Tika;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileExtDetector {
    private static FileExtDetector ourInstance = new FileExtDetector();
    private ArrayList<FileExt> fileExtArrayList;

    public static FileExtDetector getInstance() {
        return ourInstance;
    }

    private FileExtDetector() {
    }

    public void initilise() throws IOException {
        fileExtArrayList = new ArrayList<>();

        InputStream is = getClass().getResourceAsStream("mime_types.data");
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line;
        while((line = br.readLine()) != null) {
            String[] lineParts = line.split("\\|");
            fileExtArrayList.add(new FileExt(lineParts[1], lineParts[2]));
        }

        br.close();
        is.close();
    }

    public String detectExt(File file) {
        try {
            String mimeType = new Tika().detect(file);
            FileExt fileExt = findFileExt(mimeType);
            if (fileExt != null) return fileExt.getExt();
            else return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private FileExt findFileExt(String mimeType) {
        for(FileExt fileExt: fileExtArrayList) {
            if (fileExt.getMimeType().equalsIgnoreCase(mimeType)) {
                return fileExt;
            }
        }
        return null;
    }
}
