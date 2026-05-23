package dev.proststuff.skinmatics.api.client;

import dev.proststuff.utilitary.api.config.field.ConfigGroup;

/**
 * Currently have no function whatsoever. Soon enough.
 */
@Deprecated
public interface SkinmaticsClientApi {
    default void addTemplateProfile() {}

    default void addProfileConfigFields(ConfigGroup custom) {}
    default void addProfileRuntimeVariables() {}

    default void registerEquipments() {}
}