package com.cafe.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;


@NamedQuery(name = "Bill.getAllBills" , query = "SELECT b FROM Bill b")
@NamedQuery(name = "Bill.getOneBill", query = "SELECT b FROM Bill b where email=:email")
@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Table
public class Bill implements Serializable {

    private static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "contactNumber", nullable = false)
    private String contactNumber;

    @Column(name = "paymentMethod", nullable = false)
    private String paymentMethod;

    @Column(name = "total", nullable = false)
    private Integer total;

    @Column(name = "productDetail", nullable = true, columnDefinition = "json")
    private String productDetail;

    @Column(name = "createdBy", nullable = false)
    private String createdBy;

}
