package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.category.CateAdCreateRes;
import fpt.aptech.project4_server.dto.category.CateUserRes;
import fpt.aptech.project4_server.entities.book.Category;
import fpt.aptech.project4_server.repository.CateRepo;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.persistence.criteria.Path;
import java.io.File;
import java.io.IOException;
import static java.lang.System.in;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CateService {

    @Autowired
    CateRepo caterepo;

    @Value("${upload.path}")
    private String fileUpload;

    public ResponseEntity<ResultDto<?>> createNewCate(CateAdCreateRes cateres) throws IOException {
        try {
            var listcheck = caterepo.findAll();
            for (Category c : listcheck) {
                if (c.getName().equals(cateres.getName())) {
                    ResultDto<?> response = ResultDto.builder().status(false).message("Category is existed").build();
                    return new ResponseEntity<ResultDto<?>>(response, HttpStatus.CONFLICT);

                }
            }

            // Lưu file ảnh
            MultipartFile multipartFile = cateres.getFileImage();

            String fileName = multipartFile.getOriginalFilename();
            FileCopyUtils.copy(cateres.getFileImage().getBytes(), new File(fileUpload + "/" + fileName));
            System.out.println(fileUpload + "/" + fileName);
//            
//            Category newCate = new Category();
//            newCate.setName(cateres.getName());
//            newCate.setDescription(cateres.getDescription());
//            newCate.setPathImage("src/main/resources/static/image/"+fileName);
            var newCate = Category.builder().name(cateres.getName())
                    .description(cateres.getDescription())
                    .pathImage(fileUpload + "\\" + fileName).build();

            caterepo.save(newCate);
            ResultDto<?> response = ResultDto.builder().status(true).message("Create successfully").build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.OK);

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResultDto<?>> CateUserShow() {
        try {
            var listcate = caterepo.findAll().stream().map(c -> CateUserRes.builder().id(c.getId()).name(c.getName()).build());
            ResultDto<?> response = ResultDto.builder().status(true).message("ok").model(listcate).build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.OK);

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().status(false).message("Fail to show").build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResultDto<?>> UpdateCate(Integer id, String newname, String newdescription, String newpathImage) {
        try {
            Optional<Category> cateUp = caterepo.findById(id);

            if (cateUp.isEmpty()) {
                ResultDto<?> response = ResultDto.builder().status(false).message("Category is not existed").build();
                return new ResponseEntity<ResultDto<?>>(response, HttpStatus.NOT_FOUND);
            }
            var listcheck = caterepo.findAll();
            for (Category c : listcheck) {
                if (c.getName().equals(newname)) {
                    ResultDto<?> response = ResultDto.builder().status(false).message("category name is existed").build();
                    return new ResponseEntity<ResultDto<?>>(response, HttpStatus.CONFLICT);
                }
            }
            Category existingCategory = cateUp.get();
            existingCategory.setName(newname);
            existingCategory.setDescription(newdescription);
            existingCategory.setPathImage(newpathImage);
            caterepo.save(existingCategory);

            ResultDto<?> response = ResultDto.builder().status(true).message("Update successfully").build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().status(false).message("Update fail").build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
