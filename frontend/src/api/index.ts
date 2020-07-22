import {BASE_PATH, Configuration} from '../.openapi';

require('dotenv').config()

export const apiConfiguration = new Configuration({
    basePath: process.env.REACT_APP_SERVER_URL || BASE_PATH
})