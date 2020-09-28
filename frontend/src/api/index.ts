import {BASE_PATH, Configuration} from '../.openapi';

require('dotenv').config()

export const apiConfiguration = new Configuration({
    basePath: BASE_PATH.replace('https://api.financer-project.org', process.env.REACT_APP_SERVER_URL || 'https://api.financer-project.org')
})