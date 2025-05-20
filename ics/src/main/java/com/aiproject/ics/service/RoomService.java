package com.aiproject.ics.service;

import com.aiproject.ics.entity.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface RoomService {
    Map<String, String> uploadImageToFileSystem(MultipartFile file, Room room);

    byte[] downloadImageFromFileSystem(String filepath) throws IOException;
}
