package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tbl_products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 4000)
    private String description;

    @Column(name="date_created")
    private LocalDateTime creationTime;

    @Column(nullable = false,precision = 2)
    private double price;

    @Column(nullable = false,precision = 2)
    private double discount;

    @ManyToOne
    @JoinColumn(name="category_id", nullable = false)
    private CategoryEntity category;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
    private List<ProductImageEntity> productImages;
}