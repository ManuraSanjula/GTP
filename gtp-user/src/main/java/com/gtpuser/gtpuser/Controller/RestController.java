package com.gtpuser.gtpuser.Controller;

import com.gtpuser.gtpuser.Controller.models.UserReq;
import com.gtpuser.gtpuser.Controller.models.UserRes;
import com.gtpuser.gtpuser.error.model.UserError;
import com.gtpuser.gtpuser.service.UserService;
import com.gtpuser.gtpuser.utils.ErrorMessages;
import com.gtpuser.gtpuser.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@org.springframework.web.bind.annotation.RestController("/user")
public class RestController {
    private final Path basePath = Paths.get("C:\\GTP_re");

    @Autowired
    private UserService userService;

    @Autowired
    private Utils utils;

    @PostMapping("/user/createNewUser")
    Mono<ResponseEntity<UserRes>> creatingNewAccount(@RequestBody(required = true) Mono<UserReq> userReq){
        return userReq.flatMap(req-> userService.saveUser(req))
                .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/user/{id}")
    Mono<UserRes> getUser(@PathVariable String id){
        try{
            return userService.getUser( UUID.fromString(id))
                    .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
        }catch (Exception e){
           return Mono.error(new UserError("Bad Request"));
        }
    }
    @GetMapping("/user/profile/pic/{name}")
    ResponseEntity<Flux<DataBuffer>> getImage(@PathVariable String name) throws MalformedURLException {

        Path path = basePath.resolve(name);
        Resource resource = new UrlResource(path.toUri());

        if (resource.exists() || resource.isReadable()) {
            Flux<DataBuffer> bufferFlux = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 20000)
                    .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());

            String[] imageName = name.split("\\.");
            String fileExtension = imageName[imageName.length - 1];
            return switch (fileExtension) {
                case "jpeg", "jpg" -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(bufferFlux);
                case "png" -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(bufferFlux);
                default -> throw new UserError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
            };
        } else {
            throw new UserError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
    }
    @PostMapping("/file/single")
    public Mono<ResponseEntity<String>> upload(@RequestPart("id") String id, @RequestPart("fileToUpload") Mono<FilePart> filePartMono)
    {
        return filePartMono
                .flatMap(fp -> {
                    String[] imageName = fp.filename().split("\\.");
                    String fileExtension = imageName[imageName.length - 1];

                    return fp.transferTo(basePath.resolve(utils.generateName(13) + "." + fileExtension)).then()
                            .flatMap(file -> userService.updateUser(utils.generateName(13) + "." + fileExtension, id))
                            .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
                }).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
    }
}
