import {User} from '../../../.openapi';

export default class AdminUtils {

    static isUserAdmin(user: User | undefined): boolean {
        if (user) {
            if (user.roles != undefined) {
                for (const role of user.roles) {
                    if (role.name === 'ADMIN') {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
