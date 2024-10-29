// package com.hfsolution.feature.stockmanagement.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.hfsolution.feature.stockmanagement.service.product.ProductService;

// @RestController
// @RequestMapping("/api/v1")
// public class StockManagementController {

//     @Autowired
//     private ProductService productService;

//     @GetMapping("/product/list")
//     private Object listProduct(){
//         return productService.getProduct();
//     }

//     @GetMapping("/product/list/id/{id}")
//     private Object listProduct(@RequestParam @PathVariable long id){
//         return productService.getProductByID(id);
//     }

//     @GetMapping("/product/list/name/{name}")
//     private Object listProduct(@RequestParam @PathVariable String name){
//         return productService.getProductByName(name);
//     }

// }
