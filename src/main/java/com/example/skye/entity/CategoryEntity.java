package com.example.skye.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
@Data
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    private String type;

    @Column(nullable = false)
    private boolean custom;
}
