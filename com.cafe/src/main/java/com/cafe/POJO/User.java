package com.cafe.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name="User.findByEmail", query = "SELECT u from User u WHERE u.email=:email")
@NamedQuery(name="User.getAllUser", query = "SELECT new com.cafe.wrapper.UserWrapper(u.id, u.name, u.contactNumber, u.email, u.status, u.activeToken) from User u")
@NamedQuery(name = "User.updateStatus", query = "UPDATE User u SET u.status=:status WHERE u.id=:id")
@NamedQuery(name = "User.getAllAdmin", query = "SELECT new com.cafe.wrapper.UserWrapper(u.id, u.name, u.contactNumber, u.email, u.status, u.activeToken) from User u where u.role='admin'")
@NamedQuery(name = "User.updatePassword", query = "UPDATE User u SET u.password=:password WHERE u.id=:id")
@NamedQuery(name = "User.updateActiveToken", query = "UPDATE User u SET u.activeToken=:isActive WHERE u.id=:id")

@Entity
@Table
@Data
@DynamicInsert
@DynamicUpdate
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="contactNumber")
    private String contactNumber;

    @Column(name="activeToken", columnDefinition = "false")
    private String activeToken;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="status", nullable = false, columnDefinition = "string default 'false'")
    private String status;

    @Column(name="role", nullable = false)
    private String role;

}
