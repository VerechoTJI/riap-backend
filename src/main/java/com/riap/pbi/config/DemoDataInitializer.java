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

    public DemoDataInitializer(ListingRepository listingRepo, ChatRoomRepository chatRoomRepo, MessageRepository messageRepo, UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.listingRepo = listingRepo;
        this.chatRoomRepo = chatRoomRepo;
        this.messageRepo = messageRepo;
        this.userAccountRepo = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Sync landlordId with RCS fake users (FakeUasClient ID "2" = Bob Wang)
        UUID landlordIdUuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID tenantIdUuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID adminIdUuid = UUID.fromString("00000000-0000-0000-0000-000000000003");

        if (userAccountRepo.count() == 0) {
            // 1. Create Users
            userAccountRepo.save(UserAccountEntity.builder()
                    .id(landlordIdUuid)
                    .loginIdentifier("landlord")
                    .passwordHash(passwordEncoder.encode("password"))
                    .role(UserRole.LANDLORD)
                    .status(UserStatus.ACTIVE)
                    .build());

            userAccountRepo.save(UserAccountEntity.builder()
                    .id(tenantIdUuid)
                    .loginIdentifier("tenant")
                    .passwordHash(passwordEncoder.encode("password"))
                    .role(UserRole.TENANT)
                    .status(UserStatus.ACTIVE)
                    .build());

            userAccountRepo.save(UserAccountEntity.builder()
                    .id(adminIdUuid)
                    .loginIdentifier("admin")
                    .passwordHash(passwordEncoder.encode("password"))
                    .role(UserRole.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build());
        }

        if (listingRepo.count() > 0) {
            System.out.println("Database already contains listings. Skipping demo data initialization.");
            return;
        }



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
                .area(10.0).floor(5).totalFloors(12)
                .hasInternet(true).hasFurniture(true).hasAC(true).petFriendly(false).hasParking(false)
                .status(ListingStatus.PUBLISHED)
                .availableFrom(LocalDate.of(2026, 7, 1))
                .postedAt(LocalDateTime.of(2026, 6, 1, 10, 0))
                .imageUrl("https://picsum.photos/seed/listing1/400/300")
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
                .area(6.0).floor(3).totalFloors(5)
                .hasInternet(true).hasFurniture(false).hasAC(false).petFriendly(false).hasParking(false)
                .status(ListingStatus.PUBLISHED)
                .availableFrom(LocalDate.of(2026, 6, 15))
                .postedAt(LocalDateTime.of(2026, 5, 20, 9, 0))
                .imageUrl("https://picsum.photos/seed/listing2/400/300")
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
                .area(28.0).floor(8).totalFloors(15)
                .hasInternet(true).hasFurniture(true).hasAC(true).petFriendly(true).hasParking(true)
                .status(ListingStatus.PUBLISHED)
                .availableFrom(LocalDate.of(2026, 8, 1))
                .postedAt(LocalDateTime.of(2026, 6, 10, 14, 0))
                .imageUrl("https://picsum.photos/seed/listing3/400/300")
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
                .area(35.0).floor(2).totalFloors(4)
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
                .landlordId(landlordIdUuid).city("高雄市").district("前鎮區").propertyType(PropertyType.STUDIO)
                .area(8.0).floor(4).totalFloors(8)
                .hasInternet(true).hasFurniture(true).hasAC(true)
                .status(ListingStatus.PENDING)
                .build());

        // Create Fake Chat Rooms and Messages
        ChatRoom chatRoom1 = chatRoomRepo.addChatSession(tenantIdUuid, landlordIdUuid, "1");
        messageRepo.addMessageRecord(chatRoom1.getId(), tenantIdUuid, "請問這間套房還有空嗎？");
        messageRepo.addMessageRecord(chatRoom1.getId(), landlordIdUuid, "有的，隨時可以看房喔！");

        System.out.println("===================================================");
        System.out.println(" Demo data loaded: 3 Users (landlord, tenant, admin) created with password 'password'");
        System.out.println(" Demo data loaded: 4 PUBLISHED + 1 PENDING listings");
        System.out.println(" Demo data loaded: 1 ChatRoom with 2 Messages");
        System.out.println(" API: http://localhost:8080/api/listings");
        System.out.println("===================================================");
    }
}
