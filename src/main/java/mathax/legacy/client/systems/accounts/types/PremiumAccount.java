package mathax.legacy.client.systems.accounts.types;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.systems.accounts.AccountType;
import mathax.legacy.client.mixin.MinecraftClientAccessor;
import mathax.legacy.client.systems.accounts.Account;
import mathax.legacy.client.utils.misc.NbtException;
import net.minecraft.client.util.Session;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

import static mathax.legacy.client.MatHaxLegacy.mc;

public class PremiumAccount extends Account<PremiumAccount> {
    private String password;

    public PremiumAccount(String name, String password) {
        super(AccountType.Premium, name);
        this.password = password;
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
        super.login();

        YggdrasilUserAuthentication auth = getAuth();

        try {
            auth.logIn();
            setSession(new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), Optional.empty(), Optional.empty(), Session.AccountType.MOJANG));

            cache.username = auth.getSelectedProfile().getName();
            return true;
        } catch (AuthenticationUnavailableException e) {
            MatHaxLegacy.LOG.error(MatHaxLegacy.logPrefix + "Failed to contact the authentication server.");
            return false;
        } catch (AuthenticationException e) {
            if (e.getMessage().contains("Invalid username or password") || e.getMessage().contains("account migrated"))
                MatHaxLegacy.LOG.error(MatHaxLegacy.logPrefix + "Wrong password.");
            else MatHaxLegacy.LOG.error(MatHaxLegacy.logPrefix + "Failed to contact the authentication server.");
            return false;
        }
    }

    public YggdrasilUserAuthentication getAuth() {
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(((MinecraftClientAccessor) mc).getProxy(), "").createUserAuthentication(Agent.MINECRAFT);

        auth.setUsername(name);
        auth.setPassword(password);

        return auth;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = super.toTag();

        tag.putString("password", password);

        return tag;
    }

    @Override
    public PremiumAccount fromTag(NbtCompound tag) {
        super.fromTag(tag);
        if (!tag.contains("password")) throw new NbtException();

        password = tag.getString("password");

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PremiumAccount)) return false;
        return ((PremiumAccount) o).name.equals(this.name);
    }
}
