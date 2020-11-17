module.exports = {
    launch: {
        dumpio: true,
        headless: false,
        args: ['--start-maximized', '--no-sandbox', '--disable-setuid-sandbox'],
    },
    browserContext: 'default',
    server: {
        command: 'yarn run start:react:prod',
        port: 5000,
        launchTimeout: 100000,
        debug: true
    }
};
