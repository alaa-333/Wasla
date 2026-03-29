package com.example.wasla.job.mapper;

import com.example.wasla.job.dto.BidResponseDto;
import com.example.wasla.job.dto.JobListDto;
import com.example.wasla.job.dto.JobResponseDto;
import com.example.wasla.job.entity.Bid;
import com.example.wasla.job.entity.Job;
import com.example.wasla.job.entity.JobStatus;
import org.mapstruct.*;

/**
 * MapStruct mapper for Job/Bid entities → DTOs.
 * Handles UUID to String conversion and Client/Driver entity mapping.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {java.util.UUID.class})
public abstract class JobMapper {

    // ── Job → JobResponseDto ──

    @Mapping(source = "client.id", target = "clientId", qualifiedByName = "uuidToString")
    @Mapping(source = "client.fullName", target = "clientName")
    @Mapping(target = "clientPhone", ignore = true)     // set conditionally in @AfterMapping
    @Mapping(target = "driverId", ignore = true)         // set conditionally in @AfterMapping
    @Mapping(target = "driverName", ignore = true)       // set conditionally in @AfterMapping
    @Mapping(target = "driverPhone", ignore = true)      // set conditionally in @AfterMapping
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(target = "bidCount", expression = "java(job.getBids() != null ? job.getBids().size() : 0)")
    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    public abstract JobResponseDto toResponseDto(Job job);

    @AfterMapping
    protected void enrichContactInfo(Job job, @MappingTarget JobResponseDto dto) {
        if (isConfirmedOrLater(job.getStatus())) {
            // Expose client phone to driver
            if (job.getClient() != null) {
                dto.setClientPhone(job.getClient().getPhone());
            }
            // Expose driver info to client
            if (job.getDriver() != null) {
                dto.setDriverId(uuidToString(job.getDriver().getId()));
                dto.setDriverName(job.getDriver().getFullName());
                dto.setDriverPhone(job.getDriver().getPhone());
            }
        }
    }

    // ── Job → JobListDto ──

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(target = "bidCount", expression = "java(job.getBids() != null ? job.getBids().size() : 0)")
    public abstract JobListDto toListDto(Job job);

    // ── Bid → BidResponseDto ──

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    @Mapping(source = "job.id", target = "jobId", qualifiedByName = "uuidToString")
    @Mapping(source = "status", target = "status", qualifiedByName = "bidStatusToString")
    @Mapping(target = "driver", ignore = true) // set in @AfterMapping
    public abstract BidResponseDto toBidResponseDto(Bid bid);

    @AfterMapping
    protected void enrichDriverSummary(Bid bid, @MappingTarget BidResponseDto dto) {
        if (bid.getDriver() != null) {
            var driver = bid.getDriver();
            var driverSummary = BidResponseDto.DriverSummary.builder()
                    .id(uuidToString(driver.getId()))
                    .name(driver.getFullName())
                    .vehicleType(driver.getVehicleType() != null ? driver.getVehicleType().name() : null)
                    .ratingAvg(driver.getRatingAvg())
                    .totalJobs(driver.getTotalJobs())
                    .build();

            dto.setDriver(driverSummary);
        }
    }

    // ── Create Requests → Entities ──

    @Mapping(target = "client", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "bids", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "acceptedPrice", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    public abstract Job toJob(com.example.wasla.job.dto.CreateJobRequest request);

    @Mapping(target = "job", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "status", ignore = true)
    public abstract Bid toBid(com.example.wasla.job.dto.SubmitBidRequest request);

    // ── Helpers ──

    @Named("uuidToString")
    protected String uuidToString(java.util.UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    @Named("statusToString")
    protected String statusToString(JobStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("bidStatusToString")
    protected String bidStatusToString(com.example.wasla.job.entity.BidStatus status) {
        return status != null ? status.name() : null;
    }

    protected boolean isConfirmedOrLater(JobStatus status) {
        return status == JobStatus.CONFIRMED
                || status == JobStatus.IN_PROGRESS
                || status == JobStatus.COMPLETED;
    }
}
