package com.mobileshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins", schema = "public")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;
}