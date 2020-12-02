import {User} from '../../../.openapi';

export default class AdminUtils {

    public static isUserAdmin(user: User): boolean {
        if (user.roles !== undefined) {
            for (let role of user.roles) {
                if (role.name === 'ADMIN') {
                    return true;
                }
            }
        }
        return false;
    }

}
