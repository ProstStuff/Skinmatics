package dev.proststuff.skinmatics.client;

import dev.proststuff.skinmatics.SkinmaticsMod;
import net.fabricmc.loader.api.SemanticVersion;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

@SuppressWarnings("deprecation")
public final class SkinmaticsUpdateChecker {
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final HttpRequest REQUEST = HttpRequest.newBuilder().build();

    private static final SemanticVersion CURRENT_VERSION = (SemanticVersion) SkinmaticsMod.getVersion();
    private static SemanticVersion FETCHED_VERSION = null;

    public static void fetch() {
        //CLIENT.sendAsync();
        FETCHED_VERSION = (SemanticVersion) SkinmaticsMod.getVersion();
    }

    public static boolean isLatestVersion() {
        return CURRENT_VERSION.compareTo(FETCHED_VERSION) <= 0;
    }
}
