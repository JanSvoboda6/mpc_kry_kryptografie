package com.web.file;

import com.web.security.user.User;
import com.web.security.user.UserRepository;
import com.web.security.utility.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class FileController
{
    private final FileService fileService;
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final UserRepository userRepository;

    @Autowired
    public FileController(FileService fileService, JsonWebTokenUtility jsonWebTokenUtility, UserRepository userRepository)
    {
        this.fileService = fileService;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.userRepository = userRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileInformation> getAllFiles(@RequestHeader(name="Authorization") String token)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            return fileService.getAllFiles(user.get().getId());
        }
        return Collections.emptyList();
    }

    @PostMapping(value = "/folder/create")
    public ResponseEntity<?> createFolder(@RequestHeader(name="Authorization") String token, @RequestBody Key folderKey)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            fileService.createFolder(folderKey.getKey(), user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        return ResponseEntity.badRequest().body("User was not found!");
    }

    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
            @RequestHeader(name="Authorization") String token,
            @RequestPart("keys") Keys keys,
            @RequestPart("files") List<MultipartFile> files)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            try
            {
                List<byte[]> list = new ArrayList<>();
                for (MultipartFile file : files)
                {
                    byte[] bytes = file.getBytes();
                    list.add(bytes);
                }
                fileService.uploadFiles(
                        keys.getKeys(),
                        list,
                        user.get().getId());
            } catch (Exception e)
            {
                return ResponseEntity.badRequest().body("Exception occurred when uploading files!");
            }
            return ResponseEntity.ok("OK.");
        }
        return ResponseEntity.badRequest().body("User was not found!");
    }

    @PostMapping(value = "/folder/delete")
    public ResponseEntity<?> batchDeleteFolders(@RequestHeader(name="Authorization") String token, @RequestBody List<String> keys)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            fileService.delete(keys, user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        return ResponseEntity.badRequest().body("User was not found!");
    }

    @PostMapping(value = "/file/delete")
    public ResponseEntity<?> batchDeleteFiles(@RequestHeader(name="Authorization") String token, @RequestBody List<String> keys)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            fileService.delete(keys, user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        return ResponseEntity.badRequest().body("User was not found!");
    }

    @PostMapping(value = "file/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> download(@RequestHeader(name="Authorization") String token, @RequestBody String key)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            File file = fileService.download(key, user.get().getId());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(file.getSize());
            ContentDisposition disposition = ContentDisposition
                    .inline()
                    .build();
            headers.setContentDisposition(disposition);
            return new ResponseEntity<>(file.getFileContent(), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
    }
}
