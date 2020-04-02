package org.financer.server.domain.model.user;

import org.financer.shared.exceptions.UnauthorizedException;

/**
 * This interface indicates that this is property of a user and therefore only accessible with granted access rights.
 *
 * @author Raphael Müßeler
 */
public interface UserProperty {

    /**
     * {@code userEntity} defaults to {@code userEntity#getId}
     *
     * @see UserProperty#isPropertyOfUser(long)
     */
    default boolean isPropertyOfUser(UserEntity userEntity) {
        return isPropertyOfUser(userEntity.getId());
    }

    /**
     * Checks whether this object belongs to the given user id or not.
     *
     * @param userId user id to check
     * @return true if this object is property of user
     */
    boolean isPropertyOfUser(long userId);

    /**
     * Throws an {@link UnauthorizedException} if the given user id does not belong to this object
     *
     * @param userId user id to check
     */
    default void throwIfNotUsersProperty(long userId) {
        if (!isPropertyOfUser(userId)) {
            throw new UnauthorizedException("User is unauthorized to perform this operation.");
        }
    }

}
