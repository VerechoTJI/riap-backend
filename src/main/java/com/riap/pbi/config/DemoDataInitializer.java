package com.riap.pbi.config;

import com.riap.pbi.dbms.domain.*;
import com.riap.pbi.dbms.repository.ListingRepository;
import com.riap.pbi.dbms.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Profile("demo")
public class DemoDataInitializer implements CommandLineRunner {

    private final UserAccountRepository userRepo;
    private final ListingRepository listingRepo;

    public DemoDataInitializer(UserAccountRepository userRepo, ListingRepository listingRepo) {
        this.userRepo = userRepo;
        this.listingRepo = listingRepo;
    }

    @Override
    public void run(String... args) {
        UserAccount landlord = userRepo.save(
                UserAccount.create("demo-landlord@example.com", "password",
                        UserAccountRole.LANDLORD, UserAccountStatus.ACTIVE));

        listingRepo.save(Listing.builder()
                .title("台北大安套房，近捷運")
                .description("採光佳，近大安森林公園，步行5分鐘至捷運站")
                .rentCents(1800000L).depositCents(3600000L).managementFeeCents(120000L)
                .landlordId(landlord.getId()).city("台北市").district("大安區")
                .address("台北市大安區信義路四段 100 號").propertyType(PropertyType.STUDIO)
                .sizePing(10).floor(5).totalFloors(12)
                .hasInternet(true).hasFurniture(true).hasAC(true).petFriendly(false).hasParking(false)
                .status(ListingStatus.AVAILABLE)
                .availableFrom(LocalDate.of(2026, 7, 1))
                .postedAt(LocalDateTime.of(2026, 6, 1, 10, 0))
                .imageUrl("https://picsum.photos/seed/listing1/400/300")
                .build());

        listingRepo.save(Listing.builder()
                .title("新北板橋雅房，含水電")
                .description("安靜社區，生活機能完善，附近有全聯、市場")
                .rentCents(800000L).depositCents(1600000L).managementFeeCents(0L)
                .landlordId(landlord.getId()).city("新北市").district("板橋區")
                .address("新北市板橋區文化路一段 200 號").propertyType(PropertyType.BEDROOM)
                .sizePing(6).floor(3).totalFloors(5)
                .hasInternet(true).hasFurniture(false).hasAC(false).petFriendly(false).hasParking(false)
                .status(ListingStatus.AVAILABLE)
                .availableFrom(LocalDate.of(2026, 6, 15))
                .postedAt(LocalDateTime.of(2026, 5, 20, 9, 0))
                .imageUrl("https://picsum.photos/seed/listing2/400/300")
                .build());

        listingRepo.save(Listing.builder()
                .title("台北信義整層公寓，可養寵物")
                .description("挑高3米，空間寬敞，鄰近台北101，可養小型寵物")
                .rentCents(3500000L).depositCents(7000000L).managementFeeCents(200000L)
                .landlordId(landlord.getId()).city("台北市").district("信義區")
                .address("台北市信義區忠孝東路五段 88 號").propertyType(PropertyType.APARTMENT)
                .sizePing(28).floor(8).totalFloors(15)
                .hasInternet(true).hasFurniture(true).hasAC(true).petFriendly(true).hasParking(true)
                .status(ListingStatus.AVAILABLE)
                .availableFrom(LocalDate.of(2026, 8, 1))
                .postedAt(LocalDateTime.of(2026, 6, 10, 14, 0))
                .imageUrl("https://picsum.photos/seed/listing3/400/300")
                .build());

        listingRepo.save(Listing.builder()
                .title("台中西區整層住家，含車位")
                .description("近勤美誠品，生活機能佳，地下一樓停車位")
                .rentCents(2200000L).depositCents(4400000L).managementFeeCents(150000L)
                .landlordId(landlord.getId()).city("台中市").district("西區")
                .address("台中市西區公益路 300 號").propertyType(PropertyType.HOUSE)
                .sizePing(35).floor(2).totalFloors(4)
                .hasInternet(false).hasFurniture(false).hasAC(true).petFriendly(false).hasParking(true)
                .status(ListingStatus.AVAILABLE)
                .availableFrom(LocalDate.of(2026, 7, 15))
                .postedAt(LocalDateTime.of(2026, 6, 5, 11, 0))
                .imageUrl("https://picsum.photos/seed/listing4/400/300")
                .build());

        listingRepo.save(Listing.builder()
                .title("高雄前鎮套房（審核中）")
                .description("近高雄展覽館，交通便利")
                .rentCents(1200000L).landlordId(landlord.getId())
                .city("高雄市").district("前鎮區").propertyType(PropertyType.STUDIO)
                .sizePing(8).floor(4).totalFloors(8)
                .hasInternet(true).hasFurniture(true).hasAC(true)
                .status(ListingStatus.PENDING)
                .build());

        System.out.println("===================================================");
        System.out.println(" Demo data loaded: 4 AVAILABLE + 1 PENDING listings");
        System.out.println(" API: http://localhost:8080/api/listings");
        System.out.println("===================================================");
    }
}
