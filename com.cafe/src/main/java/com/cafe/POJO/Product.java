package com.cafe.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "Product.getOneProduct", query = "SELECT p FROM Product p WHERE p.id=:id")
@NamedQuery(name = "Product.getAllProducts", query = "SELECT p FROM Product p")
@NamedQuery(name = "Product.updateProduct", query = "UPDATE Product p SET p.name=:name, p.status=:status, p.price=:price, p.description=:description, p.category=:category_id WHERE p.id=:id")
@NamedQuery(name = "Product.getProductsByCategory", query = "SELECT p FROM Product p WHERE p.category.id=:id")
@Table
@Data
@Entity
@DynamicInsert
@DynamicUpdate
public class Product implements Serializable {
    public static final long serialVersionUid=123456L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id" , nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id" , nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "status", nullable = false)
    private String status;

}
