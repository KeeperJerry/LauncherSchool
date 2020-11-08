package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.*;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import launcher.helper.LogHelper;

import java.net.Proxy;

public class YggdrasilAuthenticationService implements AuthenticationService
{
    private final Environment environment;

    public YggdrasilAuthenticationService(final Proxy proxy) {
        this(proxy, determineEnvironment());
    }

    public YggdrasilAuthenticationService(final Proxy proxy, final Environment environment) {
        this(proxy, null, environment);
    }

    public YggdrasilAuthenticationService(final Proxy proxy, final String clientToken) {
        this(proxy, clientToken, determineEnvironment());
    }

    public YggdrasilAuthenticationService(final Proxy proxy, final String clientToken, final Environment environment) {
        this.environment = environment;
        LogHelper.debug("Patched AuthenticationService created: '%s'", new Object[] { clientToken });
    }

    private static Environment determineEnvironment() {
        return EnvironmentParser.getEnvironmentFromProperties().orElse(YggdrasilEnvironment.PROD);
    }

    public UserAuthentication createUserAuthentication(final Agent agent) {
        throw new UnsupportedOperationException("createUserAuthentication is used only by Mojang Launcher");
    }

    public MinecraftSessionService createMinecraftSessionService() {
        return (MinecraftSessionService)new YggdrasilMinecraftSessionService((AuthenticationService)this, this.environment);
    }

    public GameProfileRepository createProfileRepository() {
        return (GameProfileRepository)new YggdrasilGameProfileRepository(this, this.environment);
    }

    public YggdrasilSocialInteractionsService createSocialInteractionsService(final String accessToken) throws AuthenticationException {
        return new YggdrasilSocialInteractionsService(this, accessToken, this.environment);
    }
}
