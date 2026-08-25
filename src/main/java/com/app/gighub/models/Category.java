package com.app.gighub.models;

import jakarta.persistence.*;

@Entity
@Table(name = "category")  // ✅ Specify table name explicitly
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)  // ✅ Ensure name is required and unique
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {  // ✅ Properly override toString()
        return this.name;
    }
}
