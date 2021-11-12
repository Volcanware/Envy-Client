package mathax.legacy.client.systems.accounts.types;

import com.mojang.authlib.Agent;
import com.mojang.authlib.Environment;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.mixin.MinecraftClientAccessor;
import mathax.legacy.client.systems.accounts.Account;
import mathax.legacy.client.systems.accounts.AccountType;
import mathax.legacy.client.systems.accounts.AccountUtils;
import net.minecraft.client.util.Session;

import static mathax.legacy.client.MatHaxLegacy.mc;

public class TheAlteningAccount extends Account<TheAlteningAccount> {
    private static final String AUTH = "http://authserver.thealtening.com";
    private static final String ACCOUNT = "https://api.mojang.com";
    private static final String SESSION = "http://sessionserver.thealtening.com";
    private static final String SERVICES = "https://api.minecraftservices.com";

    public TheAlteningAccount(String token) {
        super(AccountType.The_Altening, token);
    }

    @Override
    public boolean fetchInfo() {
        YggdrasilUserAuthentication auth = getAuth();

        try {
            auth.logIn();

            cache.username = auth.getSelectedProfile().getName();
            cache.uuid = auth.getSelectedProfile().getId().toString();

            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }

    @Override
    public boolean login() {
        YggdrasilMinecraftSessionService service = (YggdrasilMinecraftSessionService) mc.getSessionService();
        AccountUtils.setBaseUrl(service, SESSION + "/session/minecraft/");
        AccountUtils.setJoinUrl(service, SESSION + "/session/minecraft/join");
        AccountUtils.setCheckUrl(service, SESSION + "/session/minecraft/hasJoined");

        YggdrasilUserAuthentication auth = getAuth();

        try {
            auth.logIn();
            setSession(new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang"));

            cache.username = auth.getSelectedProfile().getName();
            return true;
        } catch (AuthenticationException e) {
            MatHaxLegacy.LOG.error(MatHaxLegacy.logPrefix + "Failed to login with The Altening.");
            return false;
        }
    }

    private YggdrasilUserAuthentication getAuth() {
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(((MinecraftClientAccessor) mc).getProxy(), "", Environment.create(AUTH, ACCOUNT, SESSION, SERVICES, "The Altening")).createUserAuthentication(Agent.MINECRAFT);

        auth.setUsername(name);
        auth.setPassword("MatHax on top!");

        return auth;
    }
}
