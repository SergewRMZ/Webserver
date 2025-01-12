package presentation.services;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import domain.dto.FileInfo;
import domain.error.CustomError;
import presentation.http.HttpResponse;

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

  public void createFile(String path, String filename, byte[] contentFile) {
    try {
      createDirectoryIfNotExist(path);
      File file = new File(path + File.separator + filename);
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(contentFile);
      fos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void createDirectoryIfNotExist(String directory) throws Exception {
    File dir = new File(directory);
    if(!dir.exists()) {
      if(dir.mkdirs()) {
        System.out.println("Directorio creado: " + dir);
      }

      else {
        throw new Exception("No se puede crear el directorio: " + directory);
      }
    }
  }

  public HttpResponse deleteFile(String path) {
    File file = new File(path);
    if(!file.exists()) {
      return CustomError.badRequest("El archivo que intentas eliminar, no existe");
    }

    file.delete();
    HttpResponse response = new HttpResponse();
    return response.createSuccessResponse(201, "success", "Archivo eliminado correctamente");
  }
}
