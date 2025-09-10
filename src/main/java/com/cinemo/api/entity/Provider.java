package com.cinemo.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "providers")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Provider {

    @Id
    @Column(name = "provider_id")
    private Integer providerId;

    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @Column(name = "display_priority")
    private Integer displayPriority;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "type")
    private String type;
}
