package edu.psu.se411.lab13.services;
import java.util.Optional;
import edu.psu.se411.lab13.model.Product;
import edu.psu.se411.lab13.repositories.ProductRepository;
import org.springframework.stereotype.Service;
@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }
    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }
}
