package domain.dto;

public class FileInfo {
  private String name;
  private long filesize;
  private String type;
  private String filepath;

  public FileInfo(String name, long filesize, String type, String filepath) {
    this.name = name;
    this.filesize = filesize;
    this.type = type;
    this.filepath = filepath;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getFilePath() {
    return filepath;
  }

  public long getFileSize() {
    return filesize;
  }
}
