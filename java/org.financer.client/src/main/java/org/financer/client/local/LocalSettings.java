package org.financer.client.local;

import org.financer.shared.model.user.Settings;

public interface LocalSettings extends Settings {

    int getMaxNumberOfMonthsDisplayed();

    void setMaxNumberOfMonthsDisplayed(int maxNumberOfMonthsDisplayed);

}

