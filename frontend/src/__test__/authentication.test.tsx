import * as React      from 'react';
import {mockLoginUser} from './mock/user.mock';
import puppeteer       from 'puppeteer/lib/cjs/puppeteer/node-puppeteer-core';
import {Page}          from 'puppeteer/lib/cjs/puppeteer/common/Page';
import {Browser}       from 'puppeteer/lib/cjs/puppeteer/common/Browser';

let browser: Browser;
let page: Page;

beforeAll(async () => {
    mockLoginUser();

    browser = await puppeteer.launch({
        headless: false,
        slowMo: 30
    });
    page = await browser.newPage();
    await page.goto('http://localhost:3000/');
});

describe('<Authentication />', () => {
    it('should display login and register form', async () => {
        
    });
});

afterAll(async () => {
    await browser.close();
});
