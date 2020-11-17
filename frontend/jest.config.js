module.exports = {
    preset: 'jest-puppeteer',

    // Jest transformations -- this adds support for TypeScript
    // using ts-jest
    transform: {
        "^.+\\.tsx?$": "ts-jest"
    },

    // Test spec file resolution pattern
    // Matches parent folder `__tests__` and filename
    // should contain `__test__` or `spec`.
    testRegex: "(/__tests__/.*|(\\.|/)(test|spec))\\.tsx?$",

    // Module file extensions for importing
    moduleFileExtensions: ["ts", "tsx", "js", "jsx", "json", "node"],

    collectCoverageFrom: ['src/**/*.{ts,tsx}'],
    clearMocks: true,
    testPathIgnorePatterns: ['/node_modules/', '/build/', '/.run/'],
}
