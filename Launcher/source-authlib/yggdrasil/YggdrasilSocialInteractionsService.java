package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.minecraft.*;
import com.mojang.authlib.*;
import com.mojang.authlib.exceptions.*;
import java.util.*;

public class YggdrasilSocialInteractionsService implements SocialInteractionsService
{
    public YggdrasilSocialInteractionsService(final YggdrasilAuthenticationService authenticationService, final String accessToken, final Environment env) throws AuthenticationException {
    }
    
    public boolean serversAllowed() {
        return true;
    }
    
    public boolean realmsAllowed() {
        return false;
    }
    
    public boolean chatAllowed() {
        return true;
    }
    
    public boolean isBlockedPlayer(final UUID uuid) {
        return false;
    }
}
