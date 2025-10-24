package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT b.* FROM bookings b WHERE b.booker_id = :bookerId ORDER BY b.start_date DESC", nativeQuery = true)
    Collection<Booking> findAllByBookerId(@Param("bookerId") Long bookerId);


    @Query(value = "SELECT b.* FROM bookings b " +
            "WHERE b.booker_id = :bookerId " +
            "AND b.start_date <= :now " +
            "AND b.end_date >= :now " +
            "ORDER BY b.start_date DESC",
            nativeQuery = true)
    Collection<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId, @Param("now") Timestamp now);

    @Query(value = "SELECT b.* FROM bookings b " +
            "WHERE b.booker_id = :bookerId " +
            "AND b.end_date < :now " +
            "ORDER BY b.start_date DESC",
            nativeQuery = true)
    Collection<Booking> findPastByBookerId(@Param("bookerId") Long bookerId, @Param("now") Timestamp now);

    @Query(value = "SELECT b.* FROM bookings b " +
            "WHERE b.booker_id = :bookerId " +
            "AND b.start_date > :now " +
            "ORDER BY b.start_date DESC",
            nativeQuery = true)
    Collection<Booking> findFutureByBookerId(@Param("bookerId") Long bookerId, @Param("now") Timestamp now);

    @Query(value = "SELECT b.* FROM bookings b " +
            "WHERE b.booker_id = :bookerId " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.start_date DESC",
            nativeQuery = true)
    Collection<Booking> findByBookerIdAndWaitingStatus(@Param("bookerId") Long bookerId);

    @Query(value = "SELECT b.* FROM bookings b " +
            "WHERE b.booker_id = :bookerId " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start_date DESC",
            nativeQuery = true)
    Collection<Booking> findByBookerIdAndRejectStatus(@Param("bookerId") Long bookerId);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = :ownerId " +
            "ORDER BY b.start_date DESC",
            nativeQuery = true)
    Collection<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = :ownerId " +
            "AND b.start_date <= :now " +
            "AND b.end_date >= :now " +
            "ORDER BY b.start_date DESC",
            nativeQuery = true)
    Collection<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("now") Timestamp now);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = :ownerId " +
            "AND b.end_date < :now " +
            "ORDER BY b.start_date DESC",
            nativeQuery = true)
    Collection<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId, @Param("now") Timestamp now);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = :ownerId " +
            "AND b.start_date > :now " +
            "ORDER BY b.start_date DESC",
            nativeQuery = true)
    Collection<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId, @Param("now") Timestamp now);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = :ownerId " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.start_date DESC",
            nativeQuery = true)
    Collection<Booking> findWaitingByOwnerId(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = :ownerId " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start_date DESC",
            nativeQuery = true)
    Collection<Booking> findRejectedByOwnerId(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT " +
            "CASE " +
            "WHEN COUNT(*) > 0 " +
            "THEN TRUE ELSE FALSE " +
            "END " +
            "FROM bookings b " +
            "WHERE b.item_id = :itemId " +
            "AND b.booker_id = :userId " +
            "AND b.end_date < :now " +
            "AND b.status = 'APPROVED'",
            nativeQuery = true)
    boolean hasUserCompletedBooking(@Param("itemId") Long itemId, @Param("userId") Long userId, @Param("now") Timestamp now);

    @Query(
            value = """
        SELECT * FROM bookings b
        WHERE b.item_id = :itemId
          AND b.start_date < :now
          AND b.status = 'APPROVED'
        ORDER BY b.end_date DESC
        LIMIT 1
        """,
            nativeQuery = true
    )
    Optional<Booking> findLastBooking(
            @Param("itemId") Long itemId,
            @Param("now") LocalDateTime now
    );

    @Query(
            value = """
        SELECT * FROM bookings b
        WHERE b.item_id = :itemId
          AND b.start_date > :now
          AND b.status = 'APPROVED'
        ORDER BY b.start_date ASC
        LIMIT 1
        """,
            nativeQuery = true
    )
    Optional<Booking> findNextBooking(
            @Param("itemId") Long itemId,
            @Param("now") LocalDateTime now
    );


}
