package com.cafe.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "Category.getAllCategory", query = "SELECT c FROM Category c")
@NamedQuery(name = "Category.getOneCategory", query = "SELECT c FROM Category c WHERE c.id=:id")
@NamedQuery(name = "Category.updateCategory", query = "UPDATE Category c SET c.name=:name WHERE c.id=:id")
@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Table
public class Category implements Serializable {

    private static final long serialVersionUID=1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

}
