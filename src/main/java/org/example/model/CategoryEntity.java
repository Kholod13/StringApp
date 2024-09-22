package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_categories")
public class CategoryEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 200)
    private String image;

    @Column(length = 4000)
    private String description;

    @Column(name="date_created")
    private LocalDateTime creationTime;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    private List<ProductEntity> products;
}