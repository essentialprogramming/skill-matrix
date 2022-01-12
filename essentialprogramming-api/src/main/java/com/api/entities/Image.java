package com.api.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image")
public class Image {

    @Id
    @Column(name = "user_email", unique = true, nullable = false)
    private String userEmail;

    @Column(name = "file_name", unique = true, nullable = false)
    private String fileName;
}
