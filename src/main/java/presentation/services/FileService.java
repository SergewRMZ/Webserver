package presentation.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import presentation.http.HttpResponse;
import dto.FileInfo;

public class FileService {
  public FileService() {}

  public String getFileExtension(File file) {
    String filename = file.getName();
    String extension = "";
    int dotIndex = filename.lastIndexOf('.');
    if(dotIndex > 0) extension = filename.substring(dotIndex + 1);
    return extension;
  }

  public HttpResponse getFiles(String directoryPath) {
    File directory = new File(directoryPath);
    List<FileInfo> fileInfos = new ArrayList<>();
    if(directory.exists() && directory.isDirectory()) {
      File[] files = directory.listFiles();
      if(files != null && files.length > 0) {
        System.out.println("Extrayendo archivos de " + directoryPath);
        for(File f : files) {
          String fileType = f.isDirectory() ? "directory" : "file";
          String cleanPath = f.getPath().replace("public", "").replace("\\", "/");
          fileInfos.add(new FileInfo(f.getName(), f.length() ,fileType, cleanPath));
        }
      }

      else {
        System.out.println("No hay archivos");
      }
    }

    else{
      System.out.println("El directorio no existe");
    }

    Gson gson = new Gson();
    String jsonResponse = gson.toJson(fileInfos);

    HttpResponse response = new HttpResponse();
    response.addHeader("Content-Type", "application/json");
    response.addHeader("Content-Length", String.valueOf(jsonResponse.toString().getBytes().length));
    response.setBody(jsonResponse.getBytes());
    System.out.println(response.generateResponseHeaders() + jsonResponse);
    return response;
  } // getFiles
}
