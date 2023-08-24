package com.ecom.plantygreen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private String suggestedFertilizer;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL)
    private List<Rating> ratings;

}
