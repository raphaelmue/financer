import TestUtil from '../util/util';

describe('Login Test', () => {
    beforeEach(() => {
        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/statistics/users/1/history',
            query: {
                numberOfMonths: '6'
            }
        }, {body: {records: []}}).as('getBalanceHistory');

        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/statistics/users/1/categories/distribution',
            query: {
                balanceType: 'expenses',
                numberOfMonths: '1'
            }
        }, {body: {records: []}}).as('getCategoriesDistribution');

        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/statistics/users/1/variableTransactions/count',
            query: {
                numberOfMonths: '6'
            }
        }, {body: {records: []}}).as('getVariableTransactionCountHistory');
    });

    it('should login correctly with email and password', () => {
        cy.intercept({
            method: 'GET',
            url: TestUtil.getServerBaseUrl() + '/users',
            query: {
                email: 'test@gmail.com',
                password: 'password'
            },
            headers: {}
        }, {fixture: 'user.json'}).as('loginUser');

        cy.visit('/');

        cy.location('href').should('contain', '/landing');
        cy.get('#login_email').type('test@gmail.com');
        cy.get('#login_password').type('password{enter}');

        cy.wait('@loginUser');

        cy.get('.ant-page-header-heading-title')
            .should('exist')
            .and('contain.text', 'Dashboard');

        cy.location('href').should('contain', '/dashboard');

        cy.wait('@getBalanceHistory')
            .wait('@getCategoriesDistribution')
            .wait('@getVariableTransactionCountHistory');
    });

});

export {};
