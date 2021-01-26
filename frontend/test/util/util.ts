import packageInfo from '../../package.json';

export default class TestUtil {

    static getServerBaseUrl(): string {
        return '/api/' + packageInfo.version;
    }

}
