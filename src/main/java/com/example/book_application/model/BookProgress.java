package com.example.book_application.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer currentPage;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    // İsteğe bağlı: Toplam okuma süresi
    @Column(name = "total_reading_time")
    private Long totalReadingTimeInSeconds;
} 