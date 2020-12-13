import {BASE_PATH, Configuration, FetchParams, Middleware, RequestContext} from '../../.openapi';
import store                                                               from '../store';
import * as dotenv from 'dotenv';

dotenv.config()

const middleware: Middleware = {
    pre(context: RequestContext): Promise<FetchParams | void> {
        context.init.mode = 'cors';
            return Promise.resolve(undefined);
    }
};

export const apiConfiguration = () => new Configuration({
    basePath: BASE_PATH.replace('https://api.financer-project.org', process.env.REACT_APP_SERVER_URL || 'https://api.financer-project.org'),
    accessToken: store.getState().user.user?.activeToken.token.token,
    middleware: [middleware],
    headers: {'Accept-Language': store.getState().user.user?.settings?.LANGUAGE?.value || 'en'}
});
