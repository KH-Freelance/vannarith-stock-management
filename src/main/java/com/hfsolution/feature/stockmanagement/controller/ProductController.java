package com.hfsolution.feature.stockmanagement.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.app.properties.CloudinaryProperties;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductRequest;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductUpdateRequest;
import com.hfsolution.feature.stockmanagement.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CloudinaryProperties cloudinaryProperties;

    @PostMapping("/search")
    @Operation(summary = "remove soon")
    private Object search(@RequestBody SearchRequestDTO request){
        return productService.search(request);
    }

 
    
    @GetMapping("/search")
    @Operation(summary = "List products")
    public Object getUsersInfo( 
            @Parameter(description = "Query string to query resources. Supported query patterns are \"exact match(k=v)\", \"fuzzy match(k=~v)\", \"range(k=[min~max])\", \"list with union releationship(k={v1 v2 v3})\" and \"list with intersetion relationship(k=(v1 v2 v3))\". The value of range and list can be string(enclosed by \" or '), integer or time(in format \"2020-04-09 02:36:00\"). All of these query patterns should be put in the query string \"q=xxx\" and splitted by \",\". e.g. q=k1=v1,k2=~v2,k3=[min~max], Note: q is empty mean query all result.")
            @RequestParam(required = false) String  q,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ASC") Sort.Direction sort,
            @RequestParam(defaultValue = "id") String sortByColum

        ) {
        return productService.search(q,pageNo,pageSize,sort,sortByColum);
    }

    @GetMapping("/export")
    private void exportData(
        @Parameter(description = "Query string to query resources. Supported query patterns are \"exact match(k=v)\", \"fuzzy match(k=~v)\", \"range(k=[min~max])\", \"list with union releationship(k={v1 v2 v3})\" and \"list with intersetion relationship(k=(v1 v2 v3))\". The value of range and list can be string(enclosed by \" or '), integer or time(in format \"2020-04-09 02:36:00\"). All of these query patterns should be put in the query string \"q=xxx\" and splitted by \",\". e.g. q=k1=v1,k2=~v2,k3=[min~max], Note: q is empty mean query all result.")
        @RequestParam(required = false) String q
    ){
        productService.export(q);
    }

    // @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    // private Object uploadImage(@RequestPart("file")MultipartFile file) throws IOException{
    //     // Dotenv dotenv = Dotenv.load()
    //     Cloudinary cloudinary = new Cloudinary(cloudinaryProperties.getUrl());
    //     System.out.println(cloudinary.config.cloudName);
    //     // Upload the image
    //     Map params1 = ObjectUtils.asMap(
    //         "use_filename", true,
    //         "unique_filename", false,
    //         "overwrite", true,
    //         "public_id", "stock-producdt/"+file.getOriginalFilename()
    //     );

    //     System.out.println(cloudinary.uploader().upload(file.getBytes(), params1));
    //     return "Successufully";
    // }

    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    private Object importData(@RequestPart("file")MultipartFile file){
        return productService.importData(file);
    }

    @PostMapping("/add")
    private Object addProduct(@Valid @RequestBody ProductRequest productRequest){
        return productService.addProduct(productRequest);
    }


    @PostMapping(value = "/v2/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    private Object addProduct2(        
        @ModelAttribute ProductRequest productRequest
        ){
        return productService.addProduct(productRequest,productRequest.getFile());
    }

    @DeleteMapping("/delete/{id}")
    private Object deleteProductById( @PathVariable long id){
        return productService.deleteProductById(id);
    }

    @PutMapping("/update/{id}")
    private Object updateProductById(@PathVariable long id,@Valid @RequestBody ProductUpdateRequest productUpdateRequest){
        return productService.updateProductById(id,productUpdateRequest);
    }

    @PutMapping(value = "/v2/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    private Object updateProductById2(@PathVariable long id,
        @ModelAttribute ProductUpdateRequest productUpdateRequest
    ){
        return productService.updateProductById(id,productUpdateRequest,productUpdateRequest.getFile());
    }

  

    
}
