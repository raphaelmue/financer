import {BASE_PATH, Configuration, FetchParams, Middleware, RequestContext} from '../../.openapi';
import store                                                               from '../store';
import * as dotenv                                                         from 'dotenv';

dotenv.config();

const getServerURL = (): string => {
    return BASE_PATH.replace('https://api.financer-project.org', process.env.REACT_APP_SERVER_URL || 'https://api.financer-project.org');
};

const middleware: Middleware = {
    pre(context: RequestContext): Promise<FetchParams | void> {
        context.init.mode = 'cors';
        return Promise.resolve(undefined);
    }
};

export const apiConfiguration = (): Configuration => new Configuration({
    basePath: getServerURL(),
    accessToken: store.getState().user.user?.activeToken.token.token,
    middleware: [middleware],
    headers: {'Accept-Language': store.getState().user.user?.settings?.LANGUAGE?.value || 'en'}
});

console.log('Financer Server URL is set to: ' + getServerURL());
