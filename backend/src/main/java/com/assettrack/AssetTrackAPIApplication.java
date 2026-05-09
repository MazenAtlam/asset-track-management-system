package com.assettrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the AssetTrack Management API.
 *
 * <p>{@code @EnableScheduling} activates Spring's task scheduling infrastructure,
 * which is required by the {@link com.assettrack.service.NotificationService}
 * cron jobs for §2.3 Automated Updates (warranty-expiry and low-stock checks).
 */
@SpringBootApplication
@EnableScheduling
public class AssetTrackAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetTrackAPIApplication.class, args);
	}

}
