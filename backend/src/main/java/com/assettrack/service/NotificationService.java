package com.assettrack.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assettrack.domain.Alert;
import com.assettrack.domain.AlertType;
import com.assettrack.domain.Asset;
import com.assettrack.domain.Status;
import com.assettrack.repository.AlertRepository;
import com.assettrack.repository.AssetRepository;

/**
 * §2.3 Automated Updates — Scheduled alert generation and email notification.
 *
 * <p>Two independent jobs run daily at 08:00 server time:
 * <ol>
 *     <li>{@link #checkWarrantyExpiry()} — raises a {@link AlertType#WARRANTY_EXPIRING}
 *         alert for each asset whose warranty expires within the configured lead-time window.</li>
 *     <li>{@link #checkLowStock()} — raises a {@link AlertType#LOW_STOCK} alert for each
 *         asset type whose AVAILABLE inventory count drops below the configured threshold.</li>
 * </ol>
 *
 * <p>Both jobs are idempotent: they check for existing unresolved alerts before creating
 * new ones, so re-running on the same day produces no duplicates.
 *
 * <p>Each new alert is also sent as an email via {@link JavaMailSender} to the configured
 * notification address. Email failures are logged as warnings but do not abort the job.
 *
 * <p>{@code @EnableScheduling} must be active — it is declared on the main application class.
 */
@Service
@Transactional
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final AlertRepository alertRepository;
    private final AssetRepository assetRepository;
    private final JavaMailSender mailSender;

    // ── Configurable thresholds ───────────────────────────────────────────────

    /** How many days before warranty expiry to start alerting. */
    @Value("${assettrack.alerts.warranty-lead-days:30}")
    private int warrantyLeadDays;

    /** Minimum AVAILABLE units per type before a LOW_STOCK alert fires. */
    @Value("${assettrack.alerts.low-stock-threshold:5}")
    private int lowStockThreshold;

    /** The recipient email address for all alert notifications. */
    @Value("${assettrack.alerts.notification-email}")
    private String notificationEmail;

    /** The "from" address used on outgoing alert emails. */
    @Value("${spring.mail.username:noreply@assettrack.local}")
    private String fromEmail;

    public NotificationService(AlertRepository alertRepository,
                               AssetRepository assetRepository,
                               JavaMailSender mailSender) {
        this.alertRepository = alertRepository;
        this.assetRepository = assetRepository;
        this.mailSender = mailSender;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // §2.3 + §2.4.3 — Scheduled: Warranty Expiry Check
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Runs daily at 08:00 server time.
     *
     * <p>Finds every asset whose warranty expires within the next
     * {@link #warrantyLeadDays} days and creates an in-app {@link Alert} plus
     * an email notification — but only if an unresolved alert for that asset
     * doesn't already exist (deduplication).
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void checkWarrantyExpiry() {
        log.info("[NotificationService] Running warranty-expiry check...");

        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(warrantyLeadDays);

        List<Asset> expiringAssets =
                assetRepository.findAssetsWithWarrantyExpiringSoon(today, cutoff);

        for (Asset asset : expiringAssets) {
            // Skip if an active alert for this asset was created today already
            boolean alreadyAlerted = alertRepository
                    .existsByTypeAndAssetIdAndResolvedFalseAndCreatedAtAfter(
                            AlertType.WARRANTY_EXPIRING,
                            asset.getId(),
                            LocalDateTime.now().toLocalDate().atStartOfDay()
                    );

            if (alreadyAlerted) {
                log.debug("Warranty alert already exists for asset id={}, skipping.", asset.getId());
                continue;
            }

            long daysUntilExpiry = today.until(asset.getWarrantyExpirationDate()).getDays();
            String message = String.format(
                    "Warranty for %s %s (%s) expires in %d day(s) on %s.",
                    asset.getBrand(), asset.getModel(),
                    asset.getSerialNumber(),
                    daysUntilExpiry,
                    asset.getWarrantyExpirationDate()
            );

            Alert alert = new Alert(AlertType.WARRANTY_EXPIRING, asset.getId(),
                    message, LocalDateTime.now());
            alertRepository.save(alert);
            log.info("Created WARRANTY_EXPIRING alert: {}", message);

            sendEmail("[AssetTrack] Warranty Expiry Alert", message);
        }

        log.info("[NotificationService] Warranty-expiry check complete. {} asset(s) processed.",
                expiringAssets.size());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // §2.3 + §2.4.3 — Scheduled: Low-Stock Check
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Runs daily at 08:00 server time.
     *
     * <p>Iterates over every distinct asset type in the database. If the count
     * of AVAILABLE units for a type falls below {@link #lowStockThreshold}, a
     * {@link AlertType#LOW_STOCK} alert is created (unless one already exists).
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void checkLowStock() {
        log.info("[NotificationService] Running low-stock check...");

        List<String> allTypes = assetRepository.findDistinctTypes();

        for (String type : allTypes) {
            long available = assetRepository.countByTypeIgnoreCaseAndStatus(type, Status.AVAILABLE);

            if (available < lowStockThreshold) {
                // Deduplicate: skip if an active LOW_STOCK alert mentioning this type exists
                boolean alreadyAlerted = alertRepository
                        .existsByTypeAndMessageContainingAndResolvedFalse(
                                AlertType.LOW_STOCK, type);

                if (alreadyAlerted) {
                    log.debug("Low-stock alert already exists for type '{}', skipping.", type);
                    continue;
                }

                String message = String.format(
                        "%s inventory has fallen below the threshold of %d (currently %d available).",
                        type, lowStockThreshold, available
                );

                Alert alert = new Alert(AlertType.LOW_STOCK, null, message, LocalDateTime.now());
                alertRepository.save(alert);
                log.info("Created LOW_STOCK alert: {}", message);

                sendEmail("[AssetTrack] Low Stock Alert", message);
            }
        }

        log.info("[NotificationService] Low-stock check complete.");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Email Helper
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Sends a plain-text email to the configured notification address.
     *
     * <p>Mail failures are logged as warnings and swallowed so that a broken
     * SMTP configuration does not abort the scheduler job or affect other alerts.
     *
     * @param subject the email subject line
     * @param body    the email body text
     */
    private void sendEmail(String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(notificationEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Alert email sent to {}", notificationEmail);
        } catch (MailException e) {
            log.warn("Failed to send alert email: {}", e.getMessage());
        }
    }
}
