package com.aiproject.ics.service;

import com.aiproject.ics.entity.Room;
import com.aiproject.ics.repository.jpa.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class RoomServiceImp implements RoomService{
    static String DIRECTORY_PATH = "/u02/uploads/";
    private final RoomRepository repository;

    public RoomServiceImp(RoomRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, String> uploadImageToFileSystem(MultipartFile file, Room room) {
        Map<String,String> response=new HashMap<>();
        Room room1=repository.findByName(room.getName()).orElse(null);
        if(room1==null){
            if (file!=null){
                String filePath=saveFileToStorage(file);
                room1=new Room();
                room1.setFilepath(filePath);
                room1.setFileName(file.getOriginalFilename());
                room1.setFileType(file.getContentType());
                Room roomDetail=repository.save(room1);
                response.put("code","00");
                response.put("message","Room has been saved");
                response.put("RoomId",String.valueOf(roomDetail.getId()));
            }else{
                response.put("code","100");
                response.put("message","file does not exist");
            }
        }else {
            response.put("code","100");
            response.put("message","Room already exist");
        }
        return response;
    }

    public String saveFileToStorage(MultipartFile file){
        String extensionType=file.getContentType();//image/png

        String extension= "";
        if (!extensionType.isEmpty()) {
            String[] parts = extensionType.split("/");
            if (parts.length > 1) {
                extension = "." + parts[1];
            }
        }
        String fileName=UUID.randomUUID().toString().replace("-","") +extension;
        try{
            File directory=new File(DIRECTORY_PATH);
        if(!directory.exists()){
            directory.mkdirs();
        }

            File outputFile=new File(DIRECTORY_PATH+fileName);
            FileOutputStream outputStream=new FileOutputStream(outputFile);
            outputStream.write(file.getBytes());
            outputStream.close();
            System.out.println("File saved successfully to: " + outputFile.getAbsolutePath());
        }catch(IOException e){
            System.out.println("Error saving file: " + e.getMessage());
        }
        return fileName;
    }
    @Override
    public byte[] downloadImageFromFileSystem(String filepath) throws IOException {
        File fullPath = new File(DIRECTORY_PATH + filepath);
        log.info("Attempting to download image from: {}", fullPath.getAbsolutePath());
        return Files.readAllBytes(fullPath.toPath());
    }
}
