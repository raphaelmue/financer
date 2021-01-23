const plugins = require('./webpack.plugins');

module.exports = {
    /**
     * This is the main entry point for your application, it's the first file
     * that runs in the main process.
     */
    entry: './src/main.ts',
    // Put your normal webpack config below here
    target: 'electron-main',
    module: {
        rules: require('./webpack.rules')
    },
    resolve: {
        extensions: ['.js', '.ts', '.jsx', '.tsx', '.css']
    },
    externals: {
        config:  "config",
    },
    plugins: plugins
};
