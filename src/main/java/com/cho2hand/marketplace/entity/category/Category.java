package com.cho2hand.marketplace.entity.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "parent_category_id")
    private Long parentCategoryId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 140)
    private String slug;

    @Column(name = "is_leaf", nullable = false)
    private boolean leaf;

    @Column(name = "sort_order", nullable = false)
    private short sortOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public Long getId() { return id; }
    public Long getParentCategoryId() { return parentCategoryId; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public boolean isLeaf() { return leaf; }
    public short getSortOrder() { return sortOrder; }
    public boolean isActive() { return active; }
}
