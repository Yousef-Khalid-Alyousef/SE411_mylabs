package psu.edu.ch06.crud04.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import psu.edu.ch06.crud04.model.Product;
import psu.edu.ch06.crud04.repository.ProductRepository;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
	
	private final ProductRepository productRepository;

	private ProductController(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	
	@GetMapping
	private ResponseEntity<Iterable<Product>> findAll() {
	   return ResponseEntity.ok(productRepository.findAll());
	}
	
	@GetMapping("/{requestedId}")
	private ResponseEntity<Product> findById(@PathVariable Integer requestedId) {
		Optional<Product> productA = productRepository.findById(requestedId);
		
		if(productA.isPresent()) {
			return ResponseEntity.ok(productA.get()) ;
		}
		else {
            return ResponseEntity.notFound().build();
        }
	}
	
	@PostMapping
	private ResponseEntity<Void> createProduct(@RequestBody Product newProductRequest, UriComponentsBuilder ucb) {
		Product newProduct = productRepository.save(newProductRequest);
		URI locationOfNewProduct = ucb.path("/api/v1/products/{id}")
										.buildAndExpand(newProduct.id())
										.toUri();
		return ResponseEntity.created(locationOfNewProduct).build();
	}
	

	
	@PutMapping("/{requestedId}")
	private ResponseEntity<Void> putProduct(@PathVariable Integer requestedId, @RequestBody Product productUpdate) {
		Optional<Product> product = productRepository.findById(requestedId);
		if (product.isPresent()) {
			Product updatedProduct = new Product(product.get().id(), productUpdate.name(), productUpdate.description(), productUpdate.price());
			productRepository.save(updatedProduct);
	    } else {
	    	return ResponseEntity.notFound().build();
	    }
	    return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{requestedId}")
	private ResponseEntity<Void> deleteProduct(@PathVariable Integer requestedId) {
		Optional<Product> product = productRepository.findById(requestedId);
		if (product.isPresent()) {
			productRepository.delete(product.get());
	    } else {
	    	return ResponseEntity.notFound().build();
	    }
	    return ResponseEntity.noContent().build();
	}
	
}
