package com.nhantic.trelloapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "images")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String url;
}
