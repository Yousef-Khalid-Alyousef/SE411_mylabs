package psu.edu.ch06.crud04.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import psu.edu.ch06.crud04.model.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer>,
											PagingAndSortingRepository<Product, Integer>{

}
