package envy.client.systems.accounts.types;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import envy.client.Envy;
import envy.client.mixin.MinecraftClientAccessor;
import envy.client.systems.accounts.Account;
import envy.client.systems.accounts.AccountType;
import envy.client.utils.misc.NbtException;
import net.minecraft.client.util.Session;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public class MojangAccount extends Account<MojangAccount> {
    private String password;

    public MojangAccount(String name, String password) {
        super(AccountType.Mojang, name);
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
        } catch (AuthenticationException exception) {
            return false;
        }
    }

    @Override
    public boolean login() {
        YggdrasilUserAuthentication auth = getAuth();

        try {
            auth.logIn();
            setSession(new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), Optional.empty(), Optional.empty(), Session.AccountType.MOJANG));

            cache.username = auth.getSelectedProfile().getName();
            return true;
        } catch (AuthenticationUnavailableException exception) {
            Envy.LOG.error("Failed to contact the authentication server.");
            return false;
        } catch (AuthenticationException e) {
            if (e.getMessage().contains("Invalid username or password") || e.getMessage().contains("account migrated")) Envy.LOG.error("Wrong password.");
            else Envy.LOG.error("Failed to contact the authentication server.");

            return false;
        }
    }

    public YggdrasilUserAuthentication getAuth() {
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(((MinecraftClientAccessor) Envy.mc).getProxy(), "").createUserAuthentication(Agent.MINECRAFT);

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
    public MojangAccount fromTag(NbtCompound tag) {
        super.fromTag(tag);
        if (!tag.contains("password")) throw new NbtException();

        password = tag.getString("password");

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MojangAccount)) return false;
        return ((MojangAccount) o).name.equals(this.name);
    }
}
