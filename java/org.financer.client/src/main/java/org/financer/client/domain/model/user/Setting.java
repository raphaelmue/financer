package org.financer.client.domain.model.user;

import org.financer.shared.domain.model.value.objects.SettingPair;

public class Setting {

    private long id;
    private User user;
    private SettingPair pair;

    public Setting setValue(String value) {
        this.setPair(this.getPair().setValue(value));
        return this;
    }

    /*
     * Getters and Setters
     */

    public long getId() {
        return id;
    }

    public Setting setId(long id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Setting setUser(User user) {
        this.user = user;
        return this;
    }

    public SettingPair getPair() {
        return pair;
    }

    public Setting setPair(SettingPair pair) {
        this.pair = pair;
        return this;
    }
}
