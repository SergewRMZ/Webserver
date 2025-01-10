package domain.dto;

import java.util.HashMap;
import java.util.Map;

public class MIME_TYPES {
  private final Map<String, String> MIME_TYPES = new HashMap<>();
  private static MIME_TYPES instance;

  private MIME_TYPES() {
    init();
  }

  public static MIME_TYPES getInstance() {
    if(instance == null) {
      instance = new MIME_TYPES();
    }

    return instance;
  }

  private void init() {
    MIME_TYPES.put("htm", "text/html");
    MIME_TYPES.put("html", "text/html");
    MIME_TYPES.put("css", "text/css");
    MIME_TYPES.put("txt", "text/plain");
    MIME_TYPES.put("ico", "image/x-icon");
    MIME_TYPES.put("jpg", "image/jpeg");
    MIME_TYPES.put("jpeg", "image/jpeg");
    MIME_TYPES.put("png", "image/png");
    MIME_TYPES.put("svg", "image/svg+xml");
    MIME_TYPES.put("js", "application/javascript");
    MIME_TYPES.put("json", "application/json");
    MIME_TYPES.put("pdf", "application/pdf");
  }

  public String getTypeMime(String ext) {
    return MIME_TYPES.get(ext);
  }
}
