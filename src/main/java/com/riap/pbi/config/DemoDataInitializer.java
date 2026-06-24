package com.riap.pbi.config;

import com.riap.listing.domain.model.FeeDisclosure;
import com.riap.listing.domain.model.ListingEntity;
import com.riap.listing.domain.model.ListingStatus;
import com.riap.listing.domain.model.PropertyType;
import com.riap.listing.domain.repository.ListingRepository;
import com.riap.pbi.rcs.domain.ChatRoom;
import com.riap.pbi.rcs.port.ChatRoomRepository;
import com.riap.pbi.rcs.port.MessageRepository;
import com.riap.user.domain.model.UserAccountEntity;
import com.riap.user.domain.model.UserRole;
import com.riap.user.domain.model.UserStatus;
import com.riap.user.domain.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DemoDataInitializer implements CommandLineRunner {

        private final ListingRepository listingRepo;
        private final ChatRoomRepository chatRoomRepo;
        private final MessageRepository messageRepo;
        private final UserAccountRepository userAccountRepo;
        private final PasswordEncoder passwordEncoder;
        private final JdbcTemplate jdbcTemplate;

        public DemoDataInitializer(ListingRepository listingRepo, ChatRoomRepository chatRoomRepo,
                        MessageRepository messageRepo, UserAccountRepository userAccountRepo,
                        PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
                this.listingRepo = listingRepo;
                this.chatRoomRepo = chatRoomRepo;
                this.messageRepo = messageRepo;
                this.userAccountRepo = userAccountRepo;
                this.passwordEncoder = passwordEncoder;
                this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public void run(String... args) {
                // Sync landlordId with RCS fake users (FakeUasClient ID "2" = Bob Wang)
                UUID landlordIdUuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
                UUID tenantIdUuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
                UUID adminIdUuid = UUID.fromString("00000000-0000-0000-0000-000000000003");

                if (userAccountRepo.count() == 0) {
                        String insertSql = "INSERT INTO user_accounts (id, login_identifier, password_hash, role, status) VALUES (?, ?, ?, ?, ?)";

                        jdbcTemplate.update(insertSql, landlordIdUuid, "bob", passwordEncoder.encode("password"),
                                        "LANDLORD", "ACTIVE");
                        jdbcTemplate.update(insertSql, tenantIdUuid, "alice", passwordEncoder.encode("password"),
                                        "TENANT", "ACTIVE");
                        jdbcTemplate.update(insertSql, adminIdUuid, "admin", passwordEncoder.encode("password"),
                                        "ADMIN", "ACTIVE");
                }

                // Clear listings as requested by user to start fresh
                // listingRepo.deleteAll();

                if (listingRepo.count() > 0) {
                        System.out.println("Database already contains listings. Skipping demo data initialization.");
                        return;
                }
                listingRepo.save(ListingEntity.builder()
                                .title("台北市中正區採光套房")
                                .description("近捷運，採光佳，附傢俱與網路。")
                                .feeDisclosure(FeeDisclosure.builder()
                                                .rent(new BigDecimal("32800.00"))
                                                .deposit(new BigDecimal("25600.00"))
                                                .managementFee(new BigDecimal("800.00"))
                                                .build())
                                .landlordId(landlordIdUuid).city("台北市").district("中正區")
                                .address("南陽街 12 號").propertyType(PropertyType.STUDIO)
                                .area(9.0).floor(5).totalFloors(12).layout("1房1衛")
                                .hasInternet(true).hasFurniture(true).hasAC(true).petFriendly(false).hasParking(false)
                                .status(ListingStatus.PUBLISHED)
                                .availableFrom(LocalDate.now().plusDays(5))
                                .postedAt(LocalDateTime.now().minusDays(1))
                                .imageUrl("https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1400&q=80")
                                .build());

                listingRepo.save(ListingEntity.builder()
                                .title("台北市大安區美式別墅")
                                .description("靠近校園與商圈，公共空間明亮乾淨。")
                                .feeDisclosure(FeeDisclosure.builder()
                                                .rent(new BigDecimal("69800.00"))
                                                .deposit(new BigDecimal("19600.00"))
                                                .managementFee(new BigDecimal("600.00"))
                                                .build())
                                .landlordId(landlordIdUuid).city("台北市").district("大安區")
                                .address("復興南路一段 88 號").propertyType(PropertyType.WHOLE_FLOOR)
                                .area(25.0).floor(3).totalFloors(5).layout("別墅")
                                .hasInternet(true).hasFurniture(true).hasAC(true).petFriendly(true).hasParking(true)
                                .status(ListingStatus.PUBLISHED)
                                .availableFrom(LocalDate.now().plusDays(10))
                                .postedAt(LocalDateTime.now().minusDays(2))
                                .imageUrl("https://images.pexels.com/photos/106399/pexels-photo-106399.jpeg?auto=compress&cs=tinysrgb&w=1400")
                                .build());

                listingRepo.save(ListingEntity.builder()
                                .title("新北板橋河景兩房")
                                .description("室內採光好，適合小家庭或雙人合租。")
                                .feeDisclosure(FeeDisclosure.builder()
                                                .rent(new BigDecimal("16800.00"))
                                                .deposit(new BigDecimal("33600.00"))
                                                .managementFee(new BigDecimal("1200.00"))
                                                .build())
                                .landlordId(landlordIdUuid).city("新北市").district("板橋區")
                                .address("文化路一段 120 號").propertyType(PropertyType.APARTMENT)
                                .area(21.0).floor(12).totalFloors(15).layout("2房1廳1衛")
                                .hasInternet(true).hasFurniture(true).hasAC(true).petFriendly(true).hasParking(true)
                                .status(ListingStatus.PENDING)
                                .availableFrom(LocalDate.now().plusDays(20))
                                .postedAt(LocalDateTime.now().minusDays(3))
                                .imageUrl("https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=1400&q=80")
                                .build());

                listingRepo.save(ListingEntity.builder()
                                .title("台中西區機能公寓")
                                .description("臨近綠園道與市場，生活機能完整。")
                                .feeDisclosure(FeeDisclosure.builder()
                                                .rent(new BigDecimal("11500.00"))
                                                .deposit(new BigDecimal("29000.00"))
                                                .managementFee(new BigDecimal("900.00"))
                                                .build())
                                .landlordId(landlordIdUuid).city("台中市").district("西區")
                                .address("公益路 210 號").propertyType(PropertyType.APARTMENT)
                                .area(18.0).floor(8).totalFloors(10).layout("1房1廳1衛")
                                .hasInternet(false).hasFurniture(true).hasAC(true).petFriendly(false).hasParking(false)
                                .status(ListingStatus.PUBLISHED)
                                .availableFrom(LocalDate.now().plusDays(15))
                                .postedAt(LocalDateTime.now().minusDays(4))
                                .imageUrl("https://images.unsplash.com/photo-1502005229762-cf1b2da7c5d6?auto=format&fit=crop&w=1400&q=80")
                                .build());

                listingRepo.save(ListingEntity.builder()
                                .title("台中西區頂級樓層")
                                .description("視野遼闊，設計感十足")
                                .feeDisclosure(FeeDisclosure.builder()
                                                .rent(new BigDecimal("44000.00"))
                                                .deposit(new BigDecimal("88000.00"))
                                                .managementFee(new BigDecimal("1500.00"))
                                                .build())
                                .landlordId(landlordIdUuid).city("台中市").district("西區")
                                .address("台中市西區公益路 300 號").propertyType(PropertyType.WHOLE_FLOOR)
                                .area(35.0).floor(2).totalFloors(4).layout("3房2廳")
                                .hasInternet(false).hasFurniture(false).hasAC(true).petFriendly(false).hasParking(true)
                                .status(ListingStatus.PUBLISHED)
                                .availableFrom(LocalDate.of(2026, 7, 15))
                                .postedAt(LocalDateTime.of(2026, 6, 5, 11, 0))
                                .imageUrl("https://picsum.photos/seed/listing4/400/300")
                                .build());

                ListingEntity pendingListing = listingRepo.save(ListingEntity.builder()
                                .title("高雄前鎮套房（審核中）")
                                .description("近高雄展覽館，交通便利")
                                .feeDisclosure(FeeDisclosure.builder()
                                                .rent(new BigDecimal("12000.00"))
                                                .deposit(new BigDecimal("24000.00"))
                                                .managementFee(new BigDecimal("0.00"))
                                                .build())
                                .landlordId(landlordIdUuid).city("高雄市").district("前鎮區")
                                .propertyType(PropertyType.STUDIO)
                                .area(8.0).floor(4).totalFloors(8)
                                .hasInternet(true).hasFurniture(true).hasAC(true)
                                .status(ListingStatus.PENDING)
                                .build());

                listingRepo.save(ListingEntity.builder()
                                .title("台北大安套房，近捷運")
                                .description("採光佳，近大安森林公園，步行5分鐘至捷運站")
                                .feeDisclosure(FeeDisclosure.builder()
                                                .rent(new BigDecimal("18000.00"))
                                                .deposit(new BigDecimal("36000.00"))
                                                .managementFee(new BigDecimal("1200.00"))
                                                .build())
                                .landlordId(landlordIdUuid).city("台北市").district("大安區")
                                .address("台北市大安區信義路四段 100 號").propertyType(PropertyType.STUDIO)
                                .area(10.0).floor(5).totalFloors(12).layout("1房1衛")
                                .hasInternet(true).hasFurniture(true).hasAC(true).petFriendly(false).hasParking(false)
                                .status(ListingStatus.PUBLISHED)
                                .availableFrom(LocalDate.of(2026, 7, 1))
                                .postedAt(LocalDateTime.of(2026, 6, 1, 10, 0))
                                .imageUrl("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=1400&q=80")
                                .build());

                listingRepo.save(ListingEntity.builder()
                                .title("新北板橋雅房，含水電")
                                .description("安靜社區，生活機能完善，附近有全聯、市場")
                                .feeDisclosure(FeeDisclosure.builder()
                                                .rent(new BigDecimal("8000.00"))
                                                .deposit(new BigDecimal("16000.00"))
                                                .managementFee(new BigDecimal("0.00"))
                                                .build())
                                .landlordId(landlordIdUuid).city("新北市").district("板橋區")
                                .address("新北市板橋區文化路一段 200 號").propertyType(PropertyType.SUITE)
                                .area(6.0).floor(3).totalFloors(5).layout("1雅房")
                                .hasInternet(true).hasFurniture(false).hasAC(false).petFriendly(false).hasParking(false)
                                .status(ListingStatus.PUBLISHED)
                                .availableFrom(LocalDate.of(2026, 6, 15))
                                .postedAt(LocalDateTime.of(2026, 5, 20, 9, 0))
                                .imageUrl("https://images.unsplash.com/photo-1536376072261-38c75010e6c9?auto=format&fit=crop&w=1400&q=80")
                                .build());

                listingRepo.save(ListingEntity.builder()
                                .title("台北信義整層公寓，可養寵物")
                                .description("挑高3米，空間寬敞，鄰近台北101，可養小型寵物")
                                .feeDisclosure(FeeDisclosure.builder()
                                                .rent(new BigDecimal("35000.00"))
                                                .deposit(new BigDecimal("70000.00"))
                                                .managementFee(new BigDecimal("2000.00"))
                                                .build())
                                .landlordId(landlordIdUuid).city("台北市").district("信義區")
                                .address("台北市信義區忠孝東路五段 88 號").propertyType(PropertyType.APARTMENT)
                                .area(28.0).floor(8).totalFloors(15).layout("3房2廳2衛")
                                .hasInternet(true).hasFurniture(true).hasAC(true).petFriendly(true).hasParking(true)
                                .status(ListingStatus.PUBLISHED)
                                .availableFrom(LocalDate.of(2026, 8, 1))
                                .postedAt(LocalDateTime.of(2026, 6, 10, 14, 0))
                                .imageUrl("https://images.unsplash.com/photo-1502672260266-1c1de24244fe?auto=format&fit=crop&w=1400&q=80")
                                .build());

                listingRepo.save(ListingEntity.builder()
                                .title("台中西區整層住家，含車位")
                                .description("近勤美誠品，生活機能佳，地下一樓停車位")
                                .feeDisclosure(FeeDisclosure.builder()
                                                .rent(new BigDecimal("22000.00"))
                                                .deposit(new BigDecimal("44000.00"))
                                                .managementFee(new BigDecimal("1500.00"))
                                                .build())
                                .landlordId(landlordIdUuid).city("台中市").district("西區")
                                .address("台中市西區公益路 300 號").propertyType(PropertyType.WHOLE_FLOOR)
                                .area(35.0).floor(3).totalFloors(10).layout("3房2廳")
                                .hasInternet(true).hasFurniture(false).hasAC(true).petFriendly(false).hasParking(true)
                                .status(ListingStatus.PUBLISHED)
                                .availableFrom(LocalDate.of(2026, 7, 1))
                                .postedAt(LocalDateTime.of(2026, 6, 15, 16, 30))
                                .imageUrl("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=1400&q=80")
                                .build());

                ListingEntity pendingListing2 = listingRepo.save(ListingEntity.builder()
                                .title("高雄前鎮套房（審核中）")
                                .description("近高雄展覽館，交通便利")
                                .feeDisclosure(FeeDisclosure.builder()
                                                .rent(new BigDecimal("12000.00"))
                                                .deposit(new BigDecimal("24000.00"))
                                                .managementFee(new BigDecimal("0.00"))
                                                .build())
                                .landlordId(landlordIdUuid).city("高雄市").district("前鎮區")
                                .propertyType(PropertyType.STUDIO)
                                .area(8.0).floor(4).totalFloors(8).layout("1房1衛")
                                .hasInternet(true).hasFurniture(true).hasAC(true)
                                .status(ListingStatus.PENDING)
                                .imageUrl("https://images.unsplash.com/photo-1501183638710-841dd1904471?auto=format&fit=crop&w=1400&q=80")
                                .build());

                System.out.println("===================================================");
                System.out.println(
                                " Demo data loaded: 3 Users (landlord, tenant, admin) created with password 'password'");
                System.out.println(" Demo data loaded: 8 PUBLISHED + 3 PENDING listings");
                System.out.println(" API: http://localhost:8080/api/listings");
                System.out.println("===================================================");
        }
}
