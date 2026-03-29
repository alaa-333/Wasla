package com.example.wasla.rating.entity;

import com.example.wasla.common.entity.BaseEntity;
import com.example.wasla.job.entity.Job;
import com.example.wasla.user.client.entity.Client;
import com.example.wasla.user.driver.entity.Driver;
import jakarta.persistence.*;
import lombok.*;

/**
 * Rating entity — client rates driver after job completion.
 * One rating per job.
 */
@Builder
@Entity
@Table(name = "ratings",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_rating_job",
                columnNames = {"job_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rating extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "score", nullable = false)
    private Short score;

    @Column(name = "comment")
    private String comment;
}
