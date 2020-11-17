import * as React              from 'react';
import {mockUser}              from './mock/user.mock';
import {Browser, launch, Page} from 'puppeteer';
import {mocked}                    from 'ts-jest/utils';
import {LoginUserRequest, UserApi} from '../src/.openapi/apis';
import {User}                      from '../src/.openapi/models';

let browser: Browser;
let page: Page;

jest.mock('../src/.openapi/apis/UserApi');

beforeAll(async () => {
    browser = await launch();
    page = await browser.newPage();

    await page.goto('http://localhost:5000/');
});

afterAll(async () => {
    await browser.close();
});

describe('Authentication Component Test', () => {
    it('should display login and register form', async () => {

        mocked(UserApi.prototype.loginUser).mockImplementation((data: LoginUserRequest): Promise<User> => new Promise<User>((resolve, reject) => {
            if (data.email === 'test@gmail.com' && data.password === 'password') {
                resolve(mockUser);
            } else {
                reject();
            }
        }));

        console.log(await new UserApi().loginUser({ email: 'test@gmail.com', password: 'password'}));

        await page.waitForSelector('.login-form');
        await page.click('.login-form input[name=email]');
        await page.type('.login-form input[name=email]', mockUser.email.emailAddress);

        await page.click('.login-form input[name=password]');
        await page.type('.login-form input[name=password]', mockUser.email.emailAddress);

        await page.click('.login-form-button');

        // await page.waitForSelector('ant-page-header-heading-title[title=Dashboard]');
    });
});
