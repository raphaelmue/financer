package de.raphaelmuesseler.financer.client.local;

import de.raphaelmuesseler.financer.shared.model.user.Settings;

public interface LocalSettings extends Settings {

    int getMaxNumberOfMonthsDisplayed();

    void setMaxNumberOfMonthsDisplayed(int maxNumberOfMonthsDisplayed);

}

