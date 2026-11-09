package edu.psu.se411.lab13.controllers;
import edu.psu.se411.lab13.model.Product;
import edu.psu.se411.lab13.services.ProductService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping
    public ResponseEntity<Iterable<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable @Positive Integer id) {
        return productService.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
