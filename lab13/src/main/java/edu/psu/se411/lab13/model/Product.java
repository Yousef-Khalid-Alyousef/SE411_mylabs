package edu.psu.se411.lab13.model;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.*;
public record Product(
        @Id @Positive Integer id,
        @NotBlank @Size(max = 100) String name,
        @Size(max = 100) String description,
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal price
) {}
